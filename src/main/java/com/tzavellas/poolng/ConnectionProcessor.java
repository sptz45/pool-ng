package com.tzavellas.poolng;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that does Connection related processing and configuration tasks
 * for the connection pool.
 * 
 * <p>This class is <b>thread-safe</b>.
 * 
 * @author spiros
 */
class ConnectionProcessor {
	
	private static Logger log = LoggerFactory.getLogger(ConnectionProcessor.class);

	
	/**
	 * Apply the default configuration settings to the specified JDBC connection.
	 * 
	 * <p>If an exception occurs then there is no guarantee that any of the settings
	 * got applied.
	 * 
	 * @throws SQLException if something goes wrong during the configuration.
	 */
	void applyDefaults(Connection c, ConnectionDefaults defaults) throws SQLException {
		c.setAutoCommit(defaults.isDefaultAutocommit());
		if (defaults.getDefaultReadOnly() != null) {
			c.setReadOnly(defaults.getDefaultReadOnly());
		}
		if (defaults.getDefaultIsolation() != TxIsolation.UNDEFINED) {
			c.setTransactionIsolation(defaults.getDefaultIsolation().jdbcValue());
		}
		if (defaults.getDefaultCatalog() != null) {
			c.setCatalog(defaults.getDefaultCatalog());
		}
	}
	
	/**
	 * Validate the specified connection.
	 * 
	 * Ask the JDBC driver to validate the connection in the specified timeout.
	 * 
	 * @param c the connection
	 * @param timeoutInSecs timeout in seconds
	 * @return true if valid. else false
	 */
	boolean isValid(Connection c, Duration timeout) {
		if (! timeout.hasSeconds())
			throw new IllegalArgumentException("timeout must be zero or more seconds");
		try {
			return c.isValid((int)timeout.toSeconds());
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Resets the specified connection.
	 * 
	 * The warnings are cleared and if the {@code Connections} has {@code autoCommit == false }
	 * it also gets roll-backed;
	 * 
	 * @return {@code true} if no errors occurred, else {@code false}.
	 */
	boolean reset(Connection c) {
		boolean successfulReset = true;
		try {
			c.clearWarnings();
		} catch  (SQLException e) {
			successfulReset = false;
			log.warn("Could not clean the warnings from JDBC connection [" + c + "]");
		}
		try {
			if (! c.getAutoCommit()) {
				c.rollback();
			}
		} catch  (SQLException e) {
			successfulReset = false;
			log.warn("JDBC connection had autocommit false but we could not get it to rollback [" + c + "]");
		}
		return successfulReset;
	}

	
	void discard(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			log.debug("Exception while closing connection [" + c + "]", e);
		}
	}
}
