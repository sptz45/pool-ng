package com.tzavellas.poolng.jmx;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.tzavellas.poolng.ConnectionPool;

/**
 * Helper class to register a {@link ConnectionPool} in JMX.
 * 
 * @author spiros
 */
public class JmxRegistrar {
	
	private final MBeanServer server;
	
	/**
	 * Create the registrar.
	 * 
	 * <p>The {@link MBeanServer} used for registering the pool
	 * is the Platform MBean Server as retrieved by 
	 * {@code ManagementFactory.getPlatformMBeanServer()};
	 */
	public JmxRegistrar() {
		this(ManagementFactory.getPlatformMBeanServer());
	}
	
	/**
	 * Create the registrar.
	 * 
	 * @param server the {@link MBeanServer} to use for the registration.
	 */
	public JmxRegistrar(MBeanServer server) {
		this.server = server;
	}

	/**
	 * Register the specified {@link ConnectionPool} in JMX.
	 * 
	 * <p>The ObjectName of the created MBeans is based on the name of
	 * the connection pool.
	 * 
	 * @see ConnectionPool#getName()
	 */
	public void registerPool(ConnectionPool pool) throws JMException {
		Configuration cmx = new Configuration(pool.getConfiguration());
		PoolManagement mx = new PoolManagement(pool, cmx);
		server.registerMBean(mx, objectNameForPool(pool));
		server.registerMBean(cmx, objectNameForConfig(pool));
	}
	
	/**
	 * Unregister the specified pool from JMX.
	 * 
	 * @return true if the pool was unregistered successfully
	 */
	public boolean unregisterPool(ConnectionPool pool) {
		try {
			server.unregisterMBean(objectNameForConfig(pool));
			server.unregisterMBean(objectNameForPool(pool));
			return true;
		} catch (JMException e) {
			return false;
		}
	}
	
	ObjectName objectNameForPool(ConnectionPool pool) throws MalformedObjectNameException {
		return new ObjectName("com.tzavellas.poolng:type=PoolManagement,name=" + pool.getName());
	}
	
	ObjectName objectNameForConfig(ConnectionPool pool) throws MalformedObjectNameException {
		return new ObjectName("com.tzavellas.poolng:type=Configuration,name=" + pool.getName());
	}
}
