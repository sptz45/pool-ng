package com.tzavellas.poolng.ds;

import java.sql.Driver;
import java.util.Properties;

import javax.sql.DataSource;

import com.tzavellas.poolng.ConnectionInfo;
import com.tzavellas.poolng.ConnectionPool;
import com.tzavellas.poolng.DefaultConnectionFactory;
import com.tzavellas.poolng.Duration;
import com.tzavellas.poolng.PoolConfig;
import com.tzavellas.poolng.TxIsolation;

/**
 * A convenient factory class to create a {@link DefaultDataSource}.
 * 
 * <p>All the ConnectionPool's configuration options are exposed through JavaBean
 * setters in this class.
 * 
 * <p>This class is <b>not thread-safe<b>.
 * 
 * @author spiros
 */
public class DataSourceFactory {
	
	private String driverClassName, url, username, password;
	private Properties properties;
	
	private final PoolConfig config = new PoolConfig();
	private String name = "DefaultDataSource-" + System.nanoTime();
	
	/**
	 * Create the {@link DataSource}
	 */
	public DefaultDataSource create() {
		return new DefaultDataSource(createThePool());
	}
	
	private ConnectionPool createThePool() {
		ConnectionInfo ci = new ConnectionInfo(driverClassName, url, username, password, properties);
		DefaultConnectionFactory factory = new DefaultConnectionFactory(ci);
		return new ConnectionPool(factory, config, name);
	}

	// -----------------------------------------------------------------------
	
	/**
	 * Set the name of the class of the JDBC {@link Driver}.
	 */
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	/**
	 * Set the JDBC connection URL.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * Set the username of the connection
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Set the password of the connection
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Set the connections's properties.
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	/**
	 * Set whether a connection should be validated each time it gets
	 * borrowed from the pool.
	 */
	public void setValidateOnBorrow(boolean validateOnBorrow) {
		config.setValidateOnBorrow(validateOnBorrow);
	}
	/**
	 * Set whether a connection should be validated each time it gets
	 * returned to the pool.
	 */
	public void setValidateOnReturn(boolean validateOnReturn) {
		config.setValidateOnReturn(validateOnReturn);
	}
	/**
	 * Set the time to wait for the validation to finish before marking
	 * the connection as invalid.
	 */
	public void setValidationTimeout(Duration validationTimeout) {
		config.setValidationTimeout(validationTimeout);
	}
	/**
	 * Set the default auto-commit mode.
	 */
	public void setDefaultAutocommit(boolean defaultAutocommit) {
		config.setDefaultAutocommit(defaultAutocommit);
	}
	/**
	 * Set the default transaction isolation level
	 */
	public void setDefaultTransactionsolation(TxIsolation defaultTransactionsolation) {
		config.setDefaultIsolation(defaultTransactionsolation);
	}
	/**
	 * Get whether the connection will be set read-only by default
	 */
	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		config.setDefaultReadOnly(defaultReadOnly);
	}
	/**
	 * Get the default catalog. 
	 */
	public void setDefaultCatalog(String defaultCatalog) {
		config.setDefaultCatalog(defaultCatalog);
	}
	/**
	 * Set the number of idle connection in the pool after the eviction
	 * process has finished.
	 */
	public void setMinIdle(int minIdle) {
		config.setMinIdle(minIdle);
	}
	/**
	 * Set the number of idle connection in the poll that triggers the
	 * eviction thread to run.
	 */
	public void setMaxIdle(int maxIdle) {
		config.setMaxIdle(maxIdle);
	}
	/**
	 * Set the time which a connection has to remain in the pool before
	 * it is considered idle.
	 */
	public void setIdleTimeout(Duration idleTimeout) {
		config.setIdleTimeout(idleTimeout);
	}
	/**
	 * Set how often the idle connection eviction thread will run.
	 */
	public void setEvictionInterval(Duration evictionInterval) {
		config.setEvictionInterval(evictionInterval);
	}
	/**
	 * Set the maximum number of connection a pool can hold.
	 */
	public void setPoolSize(int poolSize) {
		config.setPoolSize(poolSize);
	}
	/**
	 * Set how many times the pool will try to get a connection if the
	 * connection is invalid.
	 */
	public void setConnectionAcquisitionRetries(int connectionAcquisitionRetries) {
		config.setAcquisitionRetries(connectionAcquisitionRetries);
	}
	/**
	 * Set the amount of time the pool will block if there is no connection
	 * available.
	 */
	public void setConnectionTimeout(Duration connectionTimeout) {
		config.setConnectionTimeout(connectionTimeout);
	}
	/**
	 * Set the amount of time the pool will wait for all connection to return
	 * before it shuts down.
	 */
	public void setShutdownTimeout(Duration shutdownTimeout) {
		config.setShutdownTimeout(shutdownTimeout);
	}
	/**
	 * Set the DataSource's name (used for management).
	 */
	public void setName(String poolName) {
		this.name = poolName;
	}
}
