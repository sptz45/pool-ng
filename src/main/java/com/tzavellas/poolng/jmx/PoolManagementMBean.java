package com.tzavellas.poolng.jmx;

/**
 * MBean to control a Connection Pool instance.
 * 
 * @author spiros
 */
public interface PoolManagementMBean {

	@JmxDescription("Shutdown the pool by closing all the available connections")
	void shutdown();

	@JmxDescription("Run the idle connection collector to evict idle connection from the pool")
	void runIdleConnectionsEviction();
	
	@JmxDescription("Reconfigure the pool using the confguration data from the associated ConfigurationMBean")
	void loadConfigurationFromJmx();
	
	@JmxDescription("Reconfigure the pool using configuration specified by the application code")
	void resetToApplicatinConfiguration();
	
	@JmxDescription("Reconfigure the pool using the default configuration")
	void loadDefaultConfiguration();
}