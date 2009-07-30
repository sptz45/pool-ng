package com.tzavellas.poolng.jmx;

import com.tzavellas.poolng.PoolConfig;

/**
 * MBean used to manage the pool'c configuration
 * 
 * @author spiros
 * 
 * @see PoolConfig
 * @see PoolManagementMBean
 */
public interface ConfigurationMBean {

	int getPoolSize();

	void setPoolSize(int poolSize);

	int getAcquisitionRetries();

	void setAcquisitionRetries(int acquisitionRetries);

	String getConnectionTimeout();

	void setConnectionTimeout(String timeout);

	String getShutdownTimeout();

	void setShutdownTimeout(String timeout);

	int getMinIdle();

	void setMinIdle(int minIdle);

	int getMaxIdle();

	void setMaxIdle(int maxIdle);

	String getIdleTimeout();

	void setIdleTimeout(String timeout);

	String getEvictionInterval();

	void setEvictionInterval(String interval);

	boolean isValidateOnBorrow();

	void setValidateOnBorrow(boolean validateOnBorrow);

	boolean isValidateOnReturn();

	void setValidateOnReturn(boolean validateOnReturn);

	String getValidationTimeout();

	void setValidationTimeout(String timeout);

	boolean isDefaultAutocommit();

	void setDefaultAutocommit(boolean defaultAutocommit);

	String getDefaultIsolation();

	@JmxDescription("Possible valiues: UNDEFINED, NONE, READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE")
	void setDefaultIsolation(String defaultIsolation);

	Boolean getDefaultReadOnly();

	void setDefaultReadOnly(Boolean defaultReadOnly);

	String getDefaultCatalog();

	void setDefaultCatalog(String defaultCatalog);
}