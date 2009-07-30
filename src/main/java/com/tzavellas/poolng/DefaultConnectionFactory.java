package com.tzavellas.poolng;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

/**
 * A connection factory that creates connections using the JDBC
 * {@link Driver} class.
 * 
 * <p>This class is <b>thread-safe</b>.
 * 
 * @author spiros
 */
public class DefaultConnectionFactory implements ConnectionFactory {
	
	private final ConnectionInfo info;
	private final Driver driver;
	
	/**
	 * Create the factory.

	 * @param info the required information to create new connections.
	 */
	public DefaultConnectionFactory(ConnectionInfo info) {
		this.info = info;
		driver = new DriverLoader().load(info.getDriverClassName());
		assertDriverSupportsUrl();
	}
	
	/** {@inheritDoc} */
	public Connection create() throws SQLException {
		return driver.connect(info.getUrl(), info.getProperties());
	}
	
	private void assertDriverSupportsUrl() {
		try {
			if (! driver.acceptsURL(info.getUrl())) {
				throw new IllegalArgumentException(
						"Error driver [" + info.getDriverClassName() + 
						"] does not accept URL [ " + info.getUrl() + "]");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
