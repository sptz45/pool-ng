package com.tzavellas.poolng;

interface ConnectionDefaults {

	/**
	 * Get the default auto-commit.
	 */
	boolean isDefaultAutocommit();

	/**
	 * Get the default transaction isolation level.
	 */
	TxIsolation getDefaultIsolation();

	/**
	 * Get whether the connection will be set to read-only by default.
	 */
	Boolean getDefaultReadOnly();

	/**
	 * Get the default catalog. 
	 */
	String getDefaultCatalog();
}
