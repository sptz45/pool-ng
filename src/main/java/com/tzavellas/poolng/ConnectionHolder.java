package com.tzavellas.poolng;

import java.sql.Connection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds a {@code Connection} in a {@code PooledObject} and also
 * adds statistics and idle handling.
 * 
 * <p>This class is <b>thread-safe</b>.
 * 
 * @author spiros
 */
class ConnectionHolder implements PooledObject {

	/**
	 * Compares {@code ConnectionHolder} objects based on their creation time.
	 */
	static final Comparator<ConnectionHolder> CREATION_TIME_COMPARATOR = new Comparator<ConnectionHolder>() {
		public int compare(ConnectionHolder o1, ConnectionHolder o2) {
			if (o1.creationTime > o2.creationTime) {
				return 1;
			}
			if (o1.creationTime == o2.creationTime) {
				return 0;
			}
			return -1;
		};
	};
	
	// -----------------------------------------------------------------------
	
	private AtomicInteger timesUsed =  new AtomicInteger();
	private volatile long lastReturnTime;
	private final long creationTime = System.nanoTime();
	private final Connection connection;

	/**
	 * Constructs a {@code ConnectionHolder}.
	 * 
	 * @param c the connection to hold
	 */
	ConnectionHolder(Connection c) {
		connection = c;
		lastReturnTime = System.nanoTime();
	}
	
	/**
	 * Get the Connection
	 */
	Connection getConnection() {
		return connection;
	}
	
	/** {@inheritDoc} */
	public void setBorrowed() {
		timesUsed.incrementAndGet();
		lastReturnTime = 0;
	}
	
	/** {@inheritDoc} */
	public void setReturned() {
		lastReturnTime = System.nanoTime();
	}
	
	/**
	 * Checks if the underline {@code Connection} is idle.
	 * 
	 * @return true if it is idle, else false
	 */
	boolean hasIdleConnection(Duration timeout) {
		return lastReturnTime != 0 && 
			System.nanoTime() - lastReturnTime > timeout.toNanos(); 
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Connection life (nsecs) [").append(System.nanoTime() - creationTime);
		sb.append("] times used: [").append(timesUsed);
		sb.append("] connection: [" + connection).append("]");
		return sb.toString();
	}
}
