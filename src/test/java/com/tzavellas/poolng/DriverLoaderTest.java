package com.tzavellas.poolng;

import static org.junit.Assert.*;

import java.sql.Driver;
import java.sql.SQLException;

import org.junit.Test;

public class DriverLoaderTest {
	
	@Test
	public void load_hsqldb_driver() throws SQLException {
		Driver d = new DriverLoader().load("org.hsqldb.jdbcDriver");
		assertNotNull(d);
		assertTrue("Should accept a valid HSQLDB URL", d.acceptsURL("jdbc:hsqldb:hsql://localhost/db-name"));
		assertFalse("Should not accept a valid MySQL URL", d.acceptsURL("jdbc:mysql://localhost:3306/db-name"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void wrong_class_name() throws SQLException {
		new DriverLoader().load("org.myjdbc.Driver");
	}
}
