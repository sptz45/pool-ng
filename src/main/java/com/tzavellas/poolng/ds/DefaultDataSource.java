package com.tzavellas.poolng.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.tzavellas.poolng.ConnectionPool;

/**
 * A {@link DataSource} implementations that wraps a {@link ConnectionPool}.
 * 
 * @author spiros
 */
public class DefaultDataSource implements DataSource {
	
	private final ConnectionPool pool;
	private volatile int loginTimeout = 0;
	
	
	public DefaultDataSource(ConnectionPool pool) {
		this.pool = pool;
	}
	
	ConnectionPool getPool() {
		return pool;
	}
	
	/**
	 * Get the DataSource's name (used for management).
	 */
	public String getName() {
		return pool.getName();
	}
	
	/**
	 * Performs <i>mandatory</i> initialization.
	 */
	public void init() {
		pool.init();
	}
	
	/**
	 * Graceful shutdown.
	 * 
	 * <p>Waits and closes all connections.
	 */
	public void shutdown() {
		pool.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() throws SQLException {
		if (loginTimeout > 0) {
			return pool.borrowConnection(loginTimeout, TimeUnit.SECONDS);
		} else {
			return pool.borrowConnection();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setLoginTimeout(int seconds) throws SQLException {
		loginTimeout = seconds;
	}

	/**
	 * Not supported, throws UnsupportedOperationException.
	 * 
	 * <p>Use {@link DefaultDataSource#getConnection()} instead.
	 */
	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException("Method getConnection(String username, String password) not supported! Use getConnection() instead.");
	}

	/**
	 * Not supported, throws UnsupportedOperationException
	 */
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("You cannot get the PrintWriter in since it is defined by a SLF4J implementation");
	}
	
	/**
	 * Not supported, throws UnsupportedOperationException
	 */
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException("You cannot set a PrintWriter this way. You have to use a logging framework that implements SLF4J");
	}

	/**
	 * Not supported, always returns false
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
	/**
	 * Not supported, throws SQLException.
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Operation not supported");
	}
}
