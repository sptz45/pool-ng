package com.tzavellas.poolng;

import java.sql.Connection;

/**
 * An enumeration to represent the various transaction isolation levels.
 * 
 * @author spiros
 */
public enum TxIsolation {
	
	UNDEFINED         (-3000),
	NONE              (Connection.TRANSACTION_NONE),
	READ_UNCOMMITTED  (Connection.TRANSACTION_READ_UNCOMMITTED),
	READ_COMMITTED    (Connection.TRANSACTION_READ_COMMITTED),
	REPEATABLE_READ   (Connection.TRANSACTION_REPEATABLE_READ),
	SERIALIZABLE      (Connection.TRANSACTION_SERIALIZABLE);
	
	
	private int jdbcValue;
	
	private TxIsolation(int value) {
		jdbcValue = value;
	}
	
	/**
	 * 
	 * Get the JDBC value (using the {@code java.sql.Connection.TRANSACTION_*} constants)
	 * of this isolation.
	 * 
	 * @return the corresponding integer from the JDBC API.
	 */
	public int jdbcValue() { 
		return jdbcValue;
	}
}