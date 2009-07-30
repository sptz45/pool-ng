package com.tzavellas.poolng;

/**
 * To be implemented by objects that will be put in object pools.
 * 
 * @author spiros
 */
interface PooledObject {

	/**
	 * Called when the object gets out of the pool.
	 */
	void setBorrowed();

	/**
	 * Called when the object returns to the pool.
	 */
	void setReturned();

}