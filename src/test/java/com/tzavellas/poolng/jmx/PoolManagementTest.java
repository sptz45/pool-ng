package com.tzavellas.poolng.jmx;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.tzavellas.poolng.ConnectionPool;
import com.tzavellas.poolng.PoolConfig;

public class PoolManagementTest {
	
	JmxRegistrar registrar = new JmxRegistrar();
	MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	PoolConfig appConfig = new PoolConfig(5);
	
	ConnectionPool mp = mock(ConnectionPool.class);
	
	PoolManagementMBean poolMBean;
	
	@Before
	public void setUp() throws JMException {
		when(mp.getName()).thenReturn("The pool name");
		when(mp.getConfiguration()).thenReturn(appConfig);
		registrar.registerPool(mp);
		poolMBean = JMX.newMBeanProxy(server, registrar.objectNameForPool(mp), PoolManagementMBean.class);
	}
	
	@After
	public void unregisterMBean() {
		registrar.unregisterPool(mp);
	}

	
	// -----------------------------------------------------------------------
	
	@Test
	public void evictionAndShutdownViaJmx() throws Exception {
		PoolManagementMBean mx = JMX.newMBeanProxy(server, registrar.objectNameForPool(mp), PoolManagementMBean.class);
		mx.runIdleConnectionsEviction();
		mx.shutdown();
		
		verify(mp).scheduleEviction(anyLong(), any(TimeUnit.class));
		verify(mp).shutdown();
	}

	
	@Test
	public void resetToAppConfigViaJmx() throws Exception {
		PoolManagementMBean mx = JMX.newMBeanProxy(server, registrar.objectNameForPool(mp), PoolManagementMBean.class);
		mx.resetToApplicatinConfiguration();
		verify(mp).reconfigure(argThat(new IsEqualTo(appConfig)));
	}
	
	@Test
	public void loadDefaultsViaJmx() throws Exception {
		
		poolMBean.loadDefaultConfiguration();
		verify(mp).reconfigure(argThat(new IsEqualTo(new PoolConfig())));
	}
	
	@Test
	public void reconfigurationViaJmx() throws Exception {
		ConfigurationMBean cx = JMX.newMBeanProxy(server, registrar.objectNameForConfig(mp), ConfigurationMBean.class);
		cx.setPoolSize(25);
		poolMBean.loadConfigurationFromJmx();
		verify(mp).reconfigure(argThat(new IsEqualTo(new PoolConfig(25))));
	}
	
	
	// -----------------------------------------------------------------------
	
	private static class IsEqualTo extends ArgumentMatcher<PoolConfig> {
		
		PoolConfig expected;
		
		public IsEqualTo(PoolConfig expected) { this.expected = expected;}
		
		@Override
		public boolean matches(Object obj) {
			return expected.isEqualTo((PoolConfig)obj);
		}
	}
}
