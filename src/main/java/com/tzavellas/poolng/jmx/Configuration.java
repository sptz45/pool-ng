package com.tzavellas.poolng.jmx;

import com.tzavellas.poolng.Duration;
import com.tzavellas.poolng.PoolConfig;
import com.tzavellas.poolng.TxIsolation;

/**
 * The implementation of {@code ConfigurationMBean}.
 * 
 * @author spiros
 */
public class Configuration implements ConfigurationMBean {
	
	private int poolSize;
	private int acquisitionRetries;
	private Duration connectionTimeout;
	private Duration shutdownTimeout;
	
	private int minIdle;
	private int maxIdle;
	private Duration idleTimeout;
	private Duration evictionInterval;
	
	private boolean validateOnBorrow;
	private boolean validateOnReturn;
	private Duration validationTimeout;
	
	private boolean defaultAutocommit;
	private TxIsolation defaultIsolation;
	private Boolean defaultReadOnly;
	private String defaultCatalog;
	
	
	public Configuration(PoolConfig c) {
		loadConfig(c);
	}
	
	final void loadConfig(PoolConfig c) {
		poolSize = c.getPoolSize();
		acquisitionRetries = c.getAcquisitionRetries();
		connectionTimeout = c.getConnectionTimeout();
		shutdownTimeout = c.getShutdownTimeout();
		minIdle = c.getMinIdle();
		maxIdle = c.getMaxIdle();
		idleTimeout = c.getIdleTimeout();
		evictionInterval = c.getEvictionInterval();
		validateOnBorrow = c.isValidateOnBorrow();
		validateOnReturn = c.isValidateOnReturn();
		validationTimeout = c.getValidationTimeout();
		defaultAutocommit = c.isDefaultAutocommit();
		defaultIsolation = c.getDefaultIsolation();
		defaultReadOnly = c.getDefaultReadOnly();
		defaultCatalog = c.getDefaultCatalog();
	}
	
	PoolConfig createPoolConfig() {
		PoolConfig config = new PoolConfig();
		config.setPoolSize(poolSize);
		config.setAcquisitionRetries(acquisitionRetries);
		config.setConnectionTimeout(connectionTimeout);
		config.setShutdownTimeout(shutdownTimeout);
		config.setMinIdle(minIdle);
		config.setMaxIdle(maxIdle);
		config.setIdleTimeout(idleTimeout);
		config.setEvictionInterval(evictionInterval);
		config.setValidateOnBorrow(validateOnBorrow);
		config.setValidateOnReturn(validateOnReturn);
		config.setValidationTimeout(validationTimeout);
		config.setDefaultAutocommit(defaultAutocommit);
		config.setDefaultIsolation(defaultIsolation);
		config.setDefaultReadOnly(defaultReadOnly);
		config.setDefaultCatalog(defaultCatalog);
		return config;
	}
	
	
	public int getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	public int getAcquisitionRetries() {
		return acquisitionRetries;
	}
	public void setAcquisitionRetries(int acquisitionRetries) {
		this.acquisitionRetries = acquisitionRetries;
	}
	public String getConnectionTimeout() {
		return connectionTimeout.toString();
	}
	public void setConnectionTimeout(String timeout) {
		this.connectionTimeout = Duration.valueOf(timeout);
	}
	public String getShutdownTimeout() {
		return shutdownTimeout.toString();
	}
	public void setShutdownTimeout(String timeout) {
		this.shutdownTimeout = Duration.valueOf(timeout);
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public String getIdleTimeout() {
		return idleTimeout.toString();
	}
	public void setIdleTimeout(String timeout) {
		this.idleTimeout = Duration.valueOf(timeout);
	}
	public String getEvictionInterval() {
		return evictionInterval.toString();
	}
	public void setEvictionInterval(String interval) {
		this.evictionInterval = Duration.valueOf(interval);
	}
	public boolean isValidateOnBorrow() {
		return validateOnBorrow;
	}
	public void setValidateOnBorrow(boolean validateOnBorrow) {
		this.validateOnBorrow = validateOnBorrow;
	}
	public boolean isValidateOnReturn() {
		return validateOnReturn;
	}
	public void setValidateOnReturn(boolean validateOnReturn) {
		this.validateOnReturn = validateOnReturn;
	}
	public String getValidationTimeout() {
		return validationTimeout.toString();
	}
	public void setValidationTimeout(String timeout) {
		this.validationTimeout = Duration.valueOf(timeout);
	}
	public boolean isDefaultAutocommit() {
		return defaultAutocommit;
	}
	public void setDefaultAutocommit(boolean defaultAutocommit) {
		this.defaultAutocommit = defaultAutocommit;
	}
	public String getDefaultIsolation() {
		return defaultIsolation.toString();
	}
	public void setDefaultIsolation(String defaultIsolation) {
		this.defaultIsolation = TxIsolation.valueOf(defaultIsolation);
	}
	public Boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}
	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}
	public String getDefaultCatalog() {
		return defaultCatalog;
	}
	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}
}
