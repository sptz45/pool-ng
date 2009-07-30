package com.tzavellas.poolng;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An JDBC connection factory.
 * 
 * <p>Implementations must be <b>thread-safe</b>.
 * 
 * @author spiros
 */
public interface ConnectionFactory {

	/**
	 * Returns a newly created connection.
	 * 
	 * @return the connection
	 * @throws SQLException if an error occurred during the creation.
	 */
	Connection create() throws SQLException;
}
