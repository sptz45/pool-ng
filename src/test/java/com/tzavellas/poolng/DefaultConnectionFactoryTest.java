package com.tzavellas.poolng;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class DefaultConnectionFactoryTest {
	
	static final String HSQL_CLASS = "org.hsqldb.jdbcDriver";
	static final String HSQL_URL = "jdbc:hsqldb:hsql://localhost/xdb";
	static final String HSQL_MEM_URL = "jdbc:hsqldb:mem:xdb";
	
	private ConnectionInfo info;
	private ConnectionFactory factory;
	
	@Test
	public void factory_creates_a_connecton() throws SQLException {
		info = new ConnectionInfo(HSQL_CLASS, HSQL_MEM_URL, "sa", "", new Properties());
		factory = new DefaultConnectionFactory(info);
		Connection c = factory.create();
		//assertTrue(c.isValid(1)); not supported in HSQLDB
		c.close();
		assertTrue(c.isClosed());
	}
	
	@Test(expected=SQLException.class)
	public void no_database_server_running() throws SQLException {
		// HSQL_URL is for connecting to HSQL via network 
		info = new ConnectionInfo(HSQL_CLASS, HSQL_URL, "sa", "", new Properties());
		factory = new DefaultConnectionFactory(info);
		factory.create();
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void wrong_jdbc_url_for_the_specified_driver() throws SQLException {
		info = new ConnectionInfo(HSQL_CLASS, "wrong-url", "sa", "", new Properties());
		new DefaultConnectionFactory(info);
	}
	
	
	// -----------------------------------------------------------------------
	
	/*
	 * Tests test-helper class HsqldbConnectionFactory not actual code.
	 */
	@Test 
	public void connections_returned_from_HsqldbConnectionFactory_support_isValid() throws SQLException {
		info = new ConnectionInfo(HSQL_CLASS, HSQL_MEM_URL, "sa", "", new Properties());
		factory = new HsqldbConnectionFactory(new DefaultConnectionFactory(info));
		Connection c = factory.create();
		assertTrue(c.isValid(1)); 
		c.close();
		assertTrue(c.isClosed());
		assertFalse(c.isValid(1)); 
	}
}
