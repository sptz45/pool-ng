package com.tzavellas.poolng;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The connection pool.
 * 
 * <p>This class is <b>thread-safe</b>.
 * 
 * @author spiros
 */
public class ConnectionPool {
	
	private final PoolImpl poolImpl;
	private final String name;
	
	private volatile ScheduledExecutorService executor; 
	private volatile Thread shuhtdownHook;
	private volatile PoolConfig config;

	public ConnectionPool(ConnectionFactory factory) {
		this(factory, new PoolConfig());
	}
	
	public ConnectionPool(ConnectionFactory factory, PoolConfig config) {
		this(factory, config, "ConnectionPool");
	}

	public ConnectionPool(ConnectionFactory factory, PoolConfig config, String name) {
		this.name = name;
		this.config = config.clone();
		poolImpl = new PoolImpl(factory, config.getPoolSize()); 
	}
	
	public String getName() {
		return name;
	}
	
	public PoolConfig getConfiguration() {
		return config.clone();
	}
	
	/**
	 * Perform after-construction initialization.
	 * 
	 * <p>This method schedules the idle connection eviction thread and registers
	 * the pool's shutdown hook in the JVM. 
	 */
	public synchronized void init() {
		if (shuhtdownHook == null) {
			registerShutdownHook();
			registerEvictionTask();
		}
	}
	
	private void registerShutdownHook() {
		shuhtdownHook = new Thread(new Runnable() {
			public void run() {
				poolImpl.shutdown(config);
			}
		});
		Runtime.getRuntime().addShutdownHook(shuhtdownHook);
	}
	
	private void registerEvictionTask() {
		createEvictionExecutorIfNecessary();
		executor.scheduleAtFixedRate(poolImpl.new IdleConnectionsCollector(this, this), 
				config.getEvictionInterval().toMillis(),
				config.getEvictionInterval().toMillis(),
				TimeUnit.MILLISECONDS);
	}
	
	private synchronized void createEvictionExecutorIfNecessary() {
		if (executor == null || executor.isShutdown()) {
			executor = Executors.newSingleThreadScheduledExecutor(
									new IdleCollectorThreadFactory());
		}
	}
	
	/**
	 * Perform a graceful shutdown.
	 * 
	 * <p>This method will close all connections currently in the pool and will
	 * wait a configurable amount of time to acquire and close the rest of the
	 * connections.
	 * 
	 * @see PoolConfig#getShutdownTimeout()
	 */
	public void shutdown() {
		synchronized (this) {
			poolImpl.shutdown(config);
		}
		try {
			Runtime.getRuntime().removeShutdownHook(shuhtdownHook);
		} catch (RuntimeException ignore) { }
	}
	
	public void reconfigure(PoolConfig newConfig) {
		if (config.isEqualTo(newConfig)) {
			return;
		}
		newConfig = newConfig.clone();
		synchronized (this) {
			if (executor != null) {
				executor.shutdownNow();
				registerEvictionTask();
			}
			if (config.getPoolSize() != newConfig.getPoolSize()) {
				int newSize = poolImpl.resize(newConfig.getPoolSize());
				newConfig.setPoolSize(newSize);
			}
			config = newConfig;	
		}
	}
	
	/**
	 * Schedule a idle connection eviction run.
	 * 
	 * <p>Usually clients do not need to call this method since the
	 * pool schedules eviction runs at configurable time intervals.
	 * 
	 * @see ConnectionPool#init()
	 * @see IdleConnectionConfig#getEvictionInterval()
	 */
	public void scheduleEviction(long delay, TimeUnit unit) {
		createEvictionExecutorIfNecessary();
		executor.schedule(poolImpl.new IdleConnectionsCollector(this, this), delay, unit);
	}
	
	/**
	 * Acquire a {@link Connection} from the pool.
	 * 
	 * <p>If a connection is not available then this method will block a
	 * configurable amount of time waiting for one to become available. 
	 * 
	 * @see PoolConfig#getConnectionTimeout()
	 */
	public Connection borrowConnection() throws SQLException {
		return borrowConnection(config.getConnectionTimeout().toMicros(), TimeUnit.MICROSECONDS);
	}
	
	/**
	 * Acquire a {@link Connection} from the pool.
	 * 
	 * <p>If a connection is not available then this method will block
	 * for the specified amount of time waiting for one to become available.
	 * 
	 * @param timeout the amount of time to wait for a connection
	 * @param unit the time-unit if the amount
	 * 
	 * @see PoolConfig#getConnectionTimeout()
	 */
	public Connection borrowConnection(long timeout, TimeUnit unit) throws SQLException {
		return poolImpl.borrowConnection(config, this, timeout, unit);
	}
	
	/**
	 * Return a connection to the pool.
	 * 
	 * <p>Not to be used by client code. Only {@link PooledConnection} calls this
	 * method.
	 */
	void returnConnection(ConnectionHolder holder) {
		poolImpl.returnConnection(config, holder);
	}	
		
	/**
	 * To be used only for testing.
	 */
	boolean isEmpty() {
		return poolImpl.available.isEmpty();
	}
	
	
	// -----------------------------------------------------------------------
	
	private static class IdleCollectorThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "Idle-Conncetions-Collector");
			t.setDaemon(true);
			return t;
		}
	}
	
	// -----------------------------------------------------------------------
	
	private static class PoolImpl {
		
		private static Logger log = LoggerFactory.getLogger(ConnectionPool.class);
		
		private final ConcurrentLinkedQueue<ConnectionHolder> available = new ConcurrentLinkedQueue<ConnectionHolder>();
		private final ConnectionProcessor proc = new ConnectionProcessor();
		private final ConnectionFactory factory;
		private final ResizeableSemaphore sem;

		PoolImpl(ConnectionFactory factory, int poolSize) {
			this.factory = factory;
			sem = new ResizeableSemaphore(poolSize);
		}
		
		int resize(int newSize) {
			return sem.resize(newSize);
		}
		
		void shutdown(PoolConfig config) {
			log.debug("The thread-pool is shutting down...");
			int acquired = sem.drainPermits();
			if (acquired != 0) {
				for (int i=0; i < acquired; i++) {
					ConnectionHolder ch = available.poll();
					if (ch != null) {
						proc.discard(ch.getConnection());
					}
				}
			}
			while (acquired < config.getPoolSize()) {
				try {
					boolean success = sem.tryAcquire(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
					if (! success) {
						break; // on timeout we exit
					}
					acquired++;
					ConnectionHolder ch = available.poll();
					if (ch != null) {
						proc.discard(ch.getConnection());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
		Connection borrowConnection(PoolConfig config, ConnectionPool pool, long timeout, TimeUnit unit) throws SQLException {
			try {
				boolean acquired = sem.tryAcquire(timeout, unit);
				if (!acquired) {
					throw new SQLException("Timeout expired (" + new Duration(timeout, unit) + ") while waiting to acquire a JDBC connection from pool.");
				}
				for (int retries = 0; retries < config.getAcquisitionRetries(); retries++) {
					ConnectionHolder holder = getConnection();
					if (config.isValidateOnBorrow()) {
						if (!proc.isValid(holder.getConnection(), config.getValidationTimeout())) {
							// we got an invalid connection, try to close and retry...
							proc.discard(holder.getConnection());
							continue;
						}
					}
					try {
						proc.applyDefaults(holder.getConnection(), config);
					} catch (SQLException e) {
						continue; // ... to get another connection
					}
					return new PooledConnection(holder, pool);
				}
				throw new SQLException("Coule not acquire a valid JDBC connection after " +
										config.getAcquisitionRetries() + " retries");
			} catch (InterruptedException e1) {
				throw new SQLException("Interrupted while waiting for connection permit.");
			}
		}
		
		ConnectionHolder getConnection() throws SQLException {
			ConnectionHolder holder = available.poll();
			if (holder == null) {
				holder = new ConnectionHolder(factory.create()); 
			}
			return holder;
		}

		void returnConnection(PoolConfig config, ConnectionHolder holder) {
			try {
				boolean resetOk =  proc.reset(holder.getConnection());
				boolean isValid = true;
				if (config.isValidateOnReturn()) {
					isValid = proc.isValid(holder.getConnection(), config.getValidationTimeout());
				}
				if (resetOk && isValid) {
					available.add(holder);
				} else {
					log.debug("The returned connection was invalid so it will be discarted");
					proc.discard(holder.getConnection());
				}
			} finally {
				sem.release();
			}
			
		}
		
		
		// -----------------------------------------------------------------------
		
		
		class IdleConnectionsCollector implements Runnable {
			
			private final ConnectionPool pool;
			private final Object lock;
			
			IdleConnectionsCollector(ConnectionPool cp, Object lock) {
				pool = cp; 
				this.lock = lock;
			}
			
			public void run() {
				synchronized (lock) {
					StopWatch watch = new StopWatch("Idle connection collection");
					watch.start();
					PoolConfig config = pool.getConfiguration();
					try {
						sem.acquire();
						log.debug("Running idle connection collector...");
						try {
							Collection<ConnectionHolder> idle = mark(config);
							if (idle.size() >= config.getMaxIdle()) {
								sweep(config, idle, idle.size() - config.getMinIdle());
							}
						} finally {
							sem.release();
						}
					} catch (InterruptedException ignore) { }
					watch.stop();
					log.debug(watch.toString());
				}
			}
			
			private Collection<ConnectionHolder> mark(PoolConfig config) {
				List<ConnectionHolder> idle = new ArrayList<ConnectionHolder>(config.getPoolSize());
				int i = 0;
				for (ConnectionHolder ch : available) {
					if (ch.hasIdleConnection(config.getIdleTimeout())) {
						idle.add(ch);
						i++;
					}
				}
				Collections.sort(idle, ConnectionHolder.CREATION_TIME_COMPARATOR);
				return idle;
			}
			
			private void sweep(PoolConfig config, Collection<ConnectionHolder> idleConnections, int toBeRemoved) {
				int connectionsRemoved = 0;
				for (ConnectionHolder ch: idleConnections) {
					if (connectionsRemoved == toBeRemoved)
						break;
					if (available.remove(ch)) {
						if (ch.hasIdleConnection(config.getIdleTimeout())) {
							// it's still idle, discard it 
							proc.discard(ch.getConnection());
							connectionsRemoved++;
							log.debug("Removed idle JDBC connection: [" + ch.getConnection() + "] from pool");
						} else {
							// it's not idle now, add it back
							available.add(ch);
						}
					}
				}
			}
		}
	}
}