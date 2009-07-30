package com.tzavellas.poolng.jmx;

import java.util.concurrent.TimeUnit;

import com.tzavellas.poolng.ConnectionPool;
import com.tzavellas.poolng.PoolConfig;

/**
 * The implementation of {@code PoolManagementMBean}.
 * 
 * @author spiros
 */
public class PoolManagement implements PoolManagementMBean {
	
	private final ConnectionPool pool;
	private final PoolConfig appConfig;
	private final Configuration configuration;
	
	public PoolManagement(ConnectionPool p, Configuration cm) {
		pool = p;
		appConfig = pool.getConfiguration();
		configuration = cm;
	}

	public void shutdown() {
		pool.shutdown();
	}
	
	public void runIdleConnectionsEviction() {
		pool.scheduleEviction(100, TimeUnit.MICROSECONDS);
	}
	
	public void loadConfigurationFromJmx() {
		reconfigure(configuration.createPoolConfig());
	}
	
	public void resetToApplicatinConfiguration() {
		reconfigure(appConfig);
	}
	
	public void loadDefaultConfiguration() {
		reconfigure(new PoolConfig());
	}
	
	private void reconfigure(PoolConfig config) {
		pool.reconfigure(config);
		configuration.loadConfig(pool.getConfiguration());
	}
}
