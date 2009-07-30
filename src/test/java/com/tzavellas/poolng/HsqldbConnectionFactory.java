package com.tzavellas.poolng;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * HSQL {@code ConnectionFactory} to support (mock) the JDBC v4.0 {@code isValid()}
 * method.
 * 
 * @author spiros
 */
public class HsqldbConnectionFactory implements ConnectionFactory {

	private final ConnectionFactory factory;
	
	public HsqldbConnectionFactory(ConnectionFactory cf) {
		factory = cf;
	}
	
	@Override
	public Connection create() throws SQLException {
		final Connection target  = factory.create();
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if ("isValid".equals(method.getName())) {
					return ! target.isClosed(); // we assume all open connections are valid
				}
				return method.invoke(target, args);
			}
		};
		
		return (Connection) Proxy.newProxyInstance(ConnectionFactory.class.getClassLoader(),
													new Class[] { Connection.class },
													handler);
	}
}
