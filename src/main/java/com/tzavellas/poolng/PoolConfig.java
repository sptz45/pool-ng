package com.tzavellas.poolng;

/**
 * General connection pool configuration.
 * 
 * @author spiros
 */
public class PoolConfig implements ConnectionDefaults, Cloneable {
	
	public static final boolean DEFAULT_AUTOCOMMIT = false;
	
	private int poolSize = 20;
	private int acquisitionRetries = 10;
	private Duration connectionTimeout = Duration.minutes(1);
	private Duration shutdownTimeout = Duration.minutes(1);
	
	private int minIdle = 1;
	private int maxIdle = 5;
	private Duration idleTimeout = Duration.minutes(5);
	private Duration evictionInterval = Duration.minutes(10);
	
	private boolean validateOnBorrow = true;
	private boolean validateOnReturn = false;
	private Duration validationTimeout = Duration.seconds(5);
	
	private boolean defaultAutocommit = DEFAULT_AUTOCOMMIT;
	private TxIsolation defaultIsolation = TxIsolation.UNDEFINED;
	private Boolean defaultReadOnly = null;
	private String defaultCatalog = null;
	
	
	public PoolConfig() { }

	public PoolConfig(int size) {
		poolSize = size;
	}
	
	public boolean isEqualTo(PoolConfig pc) {
		return Objects.equalsUsingFields(this, pc);
	}
	
	
	@Override
	public PoolConfig clone() {
		// Clone is safe cause all the fields are immutable.
		try {
			return (PoolConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	

	/**
	 * Get the maximum number of connection a pool can hold.
	 */
	public int getPoolSize() {
		return poolSize;
	}
	/**
	 * Get how many times the pool will try to get a connection if the
	 * connection is invalid.
	 */
	public int getAcquisitionRetries() {
		return acquisitionRetries;
	}
	/**
	 * Get the amount of time the pool will block if there is no connection
	 * available.
	 */
	public Duration getConnectionTimeout() {
		return connectionTimeout;
	}
	/**
	 * Get the amount of time the pool will wait for all connection to return
	 * before it shuts down.
	 */
	public Duration getShutdownTimeout() {
		return shutdownTimeout;
	}
	
	/**
	 * Get the number of idle connection in the pool after the eviction
	 * process has finished.
	 */
	public int getMinIdle() {
		return minIdle;
	}
	/**
	 * Get the number of idle connection in the poll that triggers the
	 * eviction thread to run.
	 */
	public int getMaxIdle() {
		return maxIdle;
	}
	/**
	 * Get the time which a connection has to remain in the pool before
	 * it is considered idle.
	 */
	public Duration getIdleTimeout() {
		return idleTimeout;
	}
	/**
	 * Get how often the idle connection eviction thread will run.
	 */
	public Duration getEvictionInterval() {
		return evictionInterval;
	}
	
	/**
	 * Get whether a connection should be validated each time it gets
	 * borrowed from the pool.
	 */
	public boolean isValidateOnBorrow() {
		return validateOnBorrow;
	}
	/**
	 * Get whether a connection should be validated each time it gets
	 * returned to the pool.
	 */
	public boolean isValidateOnReturn() {
		return validateOnReturn;
	}
	/**
	 * Get the time to wait for the validation to finish before marking
	 * the connection as invalid.
	 */
	public Duration getValidationTimeout() {
		return validationTimeout;
	}
	
	/**
	 * Get the default auto-commit.
	 */
	public boolean isDefaultAutocommit() {
		return defaultAutocommit;
	}
	/**
	 * Get the default transaction isolation level.
	 */
	public TxIsolation getDefaultIsolation() {
		return defaultIsolation;
	}
	/**
	 * Get whether the connection will be set read-only by default.
	 */
	public Boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}
	/**
	 * Get the default catalog. 
	 */
	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	// -----------------------------------------------------------------------
	
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setAcquisitionRetries(int acquisitionRetries) {
		this.acquisitionRetries = acquisitionRetries;
	}

	public void setConnectionTimeout(Duration connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setShutdownTimeout(Duration shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setIdleTimeout(Duration idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public void setEvictionInterval(Duration evictionInterval) {
		this.evictionInterval = evictionInterval;
	}

	public void setValidateOnBorrow(boolean validateOnBorrow) {
		this.validateOnBorrow = validateOnBorrow;
	}

	public void setValidateOnReturn(boolean validateOnReturn) {
		this.validateOnReturn = validateOnReturn;
	}

	public void setValidationTimeout(Duration timeout) {
		this.validationTimeout = timeout;
	}

	public void setDefaultAutocommit(boolean defaultAutocommit) {
		this.defaultAutocommit = defaultAutocommit;
	}

	public void setDefaultIsolation(TxIsolation defaultIsolation) {
		this.defaultIsolation = defaultIsolation;
	}

	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}
}
