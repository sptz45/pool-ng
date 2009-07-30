package com.tzavellas.poolng.jmx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;

import org.junit.Test;

import com.tzavellas.poolng.ConnectionPool;
import com.tzavellas.poolng.PoolConfig;

public class JmxRegistrarTest {
	
	JmxRegistrar registrar = new JmxRegistrar();
	MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	@Test
	public void jmx_registration_test() throws Exception {
		ConnectionPool mp = mock(ConnectionPool.class);
		when(mp.getName()).thenReturn("pool-name");
		when(mp.getConfiguration()).thenReturn(new PoolConfig());
		
		registrar.registerPool(mp);
		PoolManagementMBean mx = JMX.newMBeanProxy(server, registrar.objectNameForPool(mp), PoolManagementMBean.class);
		ConfigurationMBean cx = JMX.newMBeanProxy(server, registrar.objectNameForConfig(mp), ConfigurationMBean.class);
		
		assertNotNull(mx);
		assertNotNull(cx);
		
		assertTrue(registrar.unregisterPool(mp));
		assertFalse("Should return false since it is unregistered!", registrar.unregisterPool(mp));
	}
}
