package com.tzavellas.poolng;

import static com.tzavellas.poolng.Preconditions.*;
import java.util.Properties;

/**
 * Information on how to obtain a {@code java.sql.Connection}. 
 * 
 * @author spiros
 */
public class ConnectionInfo {
	
	private static final String PASSWORD_PROPERTY = "password";
	private static final String USERNAME_PROPERTY = "user";
	
	private final String url, driverClassName;
	private final Properties props = new Properties();
	
	/**
	 * Create a {@code ConnectionInfo}.
	 * 
	 * @param driverClassName the class name of the JDBC driver.
	 * @param url the URL for the connection
	 * @param username the username to use
	 * @param password the password
	 * @param properties the connection's properties
	 */
	public ConnectionInfo(String driverClassName, String url, String username, String password, Properties properties) {
		assertHasText("driverClassName", driverClassName);
		assertHasText("url", url);
		assertHasText("username", username);
		
		this.driverClassName = driverClassName;
		this.url = url;
		
		if (properties != null) {
			props.putAll(properties);
		}
		props.setProperty(USERNAME_PROPERTY, username);
		
		//TODO is this the correct way to handle null passwords?
		if (password != null) { 
			props.setProperty(PASSWORD_PROPERTY, password);
		}
	}
	
	/**
	 * Get the name of the driver's class.
	 */
	public String getDriverClassName() {
		return driverClassName;
	}
	/**
	 * Get the connection URL.
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Get the connection's properties.
	 */
	public Properties getProperties() {
		return new Properties(props);
	}
}
