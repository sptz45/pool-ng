package com.tzavellas.poolng.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzavellas.poolng.ds.DataSourceFactory;
import com.tzavellas.poolng.ds.DefaultDataSource;

public class SimpleStressTest {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleStressTest.class);
	
	private static final int POOL_SIZE = 15;
	private static final int NUM_OF_THREADS = 20;
	private static final int ITERATIONS_PER_THREAD = 100;
	private static final int NUM_OF_TABLES = 5;
	
	AtomicInteger numberOfErrors = new AtomicInteger();
	
	volatile DefaultDataSource dataSource;
	volatile List<String> statements;
	
	final CountDownLatch ready = new CountDownLatch(NUM_OF_THREADS);
	final CountDownLatch start = new CountDownLatch(1);
	final CountDownLatch finish = new CountDownLatch(NUM_OF_THREADS);
	
	public SimpleStressTest() {
		List<String> list = new ArrayList<String>();
		list.add("INSERT INTO TEST%d VALUES ('test name')");
		list.add("SELECT * FROM TEST%d");
		list.add("SELECT NAME FROM TEST%d");
		list.add("SELECT COUNT(*) FROM TEST%d");
		statements = Collections.unmodifiableList(list);
	}
	
	@Before
	public void setupDatabase() throws SQLException {
		createDataSource();
		createDbSchema();
	}
	
	private void createDataSource() {
		DataSourceFactory factory = new DataSourceFactory();
		factory.setPoolSize(POOL_SIZE);
		// HSQLDB does not support java.sql.Connection.isValid()
		factory.setValidateOnBorrow(false);
		
		factory.setDriverClassName("org.hsqldb.jdbcDriver");
		factory.setUrl("jdbc:hsqldb:mem:testdb");
		factory.setUsername("sa");
		
		dataSource = factory.create();
		dataSource.init();
	}
	
	private void createDbSchema() throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			Statement create = conn.createStatement();
			for (int i = 0; i < NUM_OF_TABLES; i++) {
				String stmt = String.format("CREATE TABLE TEST%d ( ID IDENTITY, NAME VARCHAR(50) )", i);
				create.executeUpdate(stmt);
			}
			create.close();
			conn.commit();
		} finally {
			if (conn != null) {
				conn.close();
			}
 		}
	}
	
	
	@After
	public void shutdownDataSource() {
		dataSource.shutdown();
	}
	
	
	@Test
	@Ignore("Incomplete stress test")
	public void run() {
		Executor exec = Executors.newFixedThreadPool(NUM_OF_THREADS);
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			exec.execute(new TestRunner());
		}
		try {
			ready.await();
			start.countDown();
			finish.await();
			
			int statemensExecuted = NUM_OF_THREADS * ITERATIONS_PER_THREAD; 
			log.debug("Finished execution. Statements: {}, errors: {}", statemensExecuted, numberOfErrors.get());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	
	class TestRunner implements Runnable {
		public void run() {
			ready.countDown();
			try {
				start.await();
				for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
					runTheTest();
				}
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				finish.countDown();
			}
		}
		private void runTheTest() {
			Connection c = null;
			try {
				c = dataSource.getConnection(); 
				Statement stmt = c.createStatement();
				stmt.execute(getRandomStatement());
			} catch (SQLException e) {
				numberOfErrors.incrementAndGet();
				log.error("Error in ", e);
			} finally {
				if (c != null) try { c.close(); } catch (SQLException ignore) { }
			}
		}
		private String getRandomStatement() {
			Random r = new Random();
			return String.format(
					statements.get(r.nextInt(statements.size())),
					r.nextInt(NUM_OF_TABLES)); 
			
		}
	}
}
