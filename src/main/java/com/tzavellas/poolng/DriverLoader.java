package com.tzavellas.poolng;

import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;

/**
 * Load a JDBC {@code Driver} using the reflection API.
 * 
 * @author spiros
 */
class DriverLoader {
	
	private final ClassLoader cl;
	
	DriverLoader() {
		cl = DriverLoader.class.getClassLoader();
	}
	
	DriverLoader(ClassLoader loader) {
		cl = loader;
	}
	
	/**
	 * Load the JDBC driver from its class name. 
	 * 
	 * @param driverClassName the driver class name
	 * @return the JDBC Driver
	 */
	Driver load(String driverClassName) {
		//  We do not use Class.forName or java.sql.DriverManager cause they are
		// known to have problems with OSGi.
		Class<? extends Driver> dc = loadClass(driverClassName);
		Driver driver = null;
		try {
			driver = (Driver) dc.getDeclaredConstructor().newInstance();
			return driver;

		} catch (IllegalArgumentException e) {
			throw e;
		} catch (SecurityException e) {
			throw new IllegalArgumentException("No-arg constructor was not accessable, unable to load [" + driverClassName + "]", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not load [" + driverClassName +"] cause it represnets an abstract class", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("No-arg constructor was not accessable, unable to load [" + driverClassName + "]", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("The no-arg constructor of [" + driverClassName + "] threw exception: " + e.getTargetException(), e.getTargetException());
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Could not find the no-arg constructor to load [" + driverClassName + "]", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Driver> loadClass(String clazz) {
		try {
			return (Class<? extends Driver>) cl.loadClass(clazz);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Error while loading JDBC driver class." +
					" Class [" + clazz + "] could not be found.");
		}
	}
}
