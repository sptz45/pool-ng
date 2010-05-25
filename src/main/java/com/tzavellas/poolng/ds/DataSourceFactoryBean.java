package com.tzavellas.poolng.ds;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.tzavellas.poolng.jmx.JmxRegistrar;

/**
 * A {@code FactoryBean} to create a {@link DefaultDataSource}.
 * 
 * <p>This class is <b>not thread-safe<b>.
 * 
 * @author spiros
 */
public class DataSourceFactoryBean extends DataSourceFactory implements FactoryBean<DefaultDataSource>, DisposableBean {
	
	private DefaultDataSource dataSource;
	private JmxRegistrar registrar;
	
	/**
	 * Enable JMX management of the DataSource.
	 * 
	 * <p>Default is false.
	 */
	public void setEnableJmx(boolean enableJmx) {
		if (enableJmx)
			registrar = new JmxRegistrar();
	}
	
	/**
	 * Return the {@link DefaultDataSource}.
	 * 
	 * <p>This FactoryBean is singleton, all invocations of this method
	 * return the same object.
	 */
	public DefaultDataSource getObject() throws Exception {
		if (dataSource == null) {
			dataSource = create();
			dataSource.init();
			if (registrar != null)
				registrar.registerPool(dataSource.getPool());
		}
		return dataSource;
	}
	
	/**
	 * Supports {@link DefaultDataSource}.
	 */
	public Class<DefaultDataSource> getObjectType() {
		return DefaultDataSource.class; 
	}
	
	/**
	 * This FactoryBean is singleton.
	 */
	public boolean isSingleton() {
		return true;
	}
	
	/**
	 * Shutdown the {@link DefaultDataSource} created by this factory.
	 */
	public void destroy() throws Exception {
		if (dataSource != null) {
			if (registrar != null)
				registrar.unregisterPool(dataSource.getPool());
			dataSource.shutdown();
		}
	}
}
