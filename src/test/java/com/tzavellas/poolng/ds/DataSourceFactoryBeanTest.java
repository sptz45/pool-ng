package com.tzavellas.poolng.ds;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DataSourceFactoryBeanTest {

	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("datasource-context.xml");
	
	@Test
	public void simple_integration_test() throws Exception {
		DefaultDataSource ds = (DefaultDataSource) ctx.getBean("ds");
		assertNotNull(ds);
		assertEquals("test-ds", ds.getName());
		
		Connection c = ds.getConnection();
		assertFalse(c.isClosed());
		assertFalse(c.isReadOnly());
		assertEquals(Connection.TRANSACTION_REPEATABLE_READ, c.getTransactionIsolation());
		
		Statement create = c.createStatement();
		create.executeUpdate("CREATE TABLE TEST ( NAME VARCHAR(50) )");
		create.close();
		
		Statement insert = c.createStatement();
		int rows = insert.executeUpdate("INSERT INTO TEST VALUES ('spiros')");
		insert.close();
		assertEquals(1, rows);
		
		c.commit();
		c.close();
		ctx.close();
	}
	
	@Test
	public void the_factory_bean_must_be_a_singleton() throws Exception {
		DataSourceFactoryBean factory = new DataSourceFactoryBean();
		factory.setDriverClassName("org.hsqldb.jdbcDriver");
		factory.setUrl("jdbc:hsqldb:mem:xdb");
		factory.setUsername("sa");
		
		assertTrue(factory.isSingleton());
		assertEquals(DefaultDataSource.class, factory.getObjectType());
		assertSame(factory.getObject(), factory.getObject());
		factory.destroy();
	}
	
	@Test
	public void destroy_unused_factory() throws Exception {
		DataSourceFactoryBean factory = new DataSourceFactoryBean();
		factory.setEnableJmx(false);
		factory.destroy();
	}
}
