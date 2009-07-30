package com.tzavellas.poolng.jmx;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;
import java.sql.Connection;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tzavellas.poolng.ConnectionInfo;
import com.tzavellas.poolng.ConnectionPool;
import com.tzavellas.poolng.DefaultConnectionFactory;
import com.tzavellas.poolng.PoolConfig;

public class JmxIntegrationTest {
	
	private static final String POOL_NAME = "jmx-pool-test";
	
	JmxRegistrar registrar = new JmxRegistrar();
	MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	
	ConnectionPool pool;
	PoolConfig config = new PoolConfig();
	
	@Before
	public void setupAndRegister() throws Exception {
		ConnectionInfo info = new ConnectionInfo("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:xdb", "sa", null, null);
		config.setValidateOnBorrow(false);
		pool = new ConnectionPool(new DefaultConnectionFactory(info), config, POOL_NAME);
		registrar.registerPool(pool);
	}
	
	@After
	public void unregisterFromJmx() {
		registrar.unregisterPool(pool);
	}
	
	@Test
	public void reconfigure_the_pool_while_active() throws Exception {
		Connection c1 = pool.borrowConnection();
		assertFalse(c1.isReadOnly());
		
		Connection c2 = pool.borrowConnection();
		assertFalse(c2.isReadOnly());
		
		PoolManagementMBean pb = getManagement();
		ConfigurationMBean cb = getConfiguration();
		
		cb.setPoolSize(1);
		pb.loadConfigurationFromJmx();
		
		// 2 active connections, so the request to resize to 1 will resize to 2
		assertEquals(2, getConfiguration().getPoolSize());

	}
	
	private ConfigurationMBean getConfiguration() throws JMException {
		return JMX.newMBeanProxy(server, registrar.objectNameForConfig(pool), ConfigurationMBean.class);
	}
	
	private PoolManagementMBean getManagement() throws JMException {
		return JMX.newMBeanProxy(server, registrar.objectNameForPool(pool), PoolManagementMBean.class);
	}	
}
