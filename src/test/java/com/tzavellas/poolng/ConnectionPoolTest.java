package com.tzavellas.poolng;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class ConnectionPoolTest {
	
	private Connection mc;
	private ConnectionFactory mcf;
	
	@Before
	public void setUpMocks() throws SQLException {
		mc = mock(Connection.class);
		mcf = mock(ConnectionFactory.class);
		when(mcf.create()).thenReturn(mc);
		when(mc.isValid(anyInt())).thenReturn(true);
	}

	@Test
	public void an_empty_pool_should_create_a_new_connection() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		assertNotNull(c);
		verify(mcf).create();
	}
	
	@Test
	public void a_connection_returns_to_the_pool() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		assertTrue("The pool should be empty!", pool.isEmpty());
		c.close();
		assertFalse("The pool must have a connection!", pool.isEmpty());
		verify(mcf).create();
	}
	
	@Test
	public void the_pool_returns_the_same_connection_when_available() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c1 = pool.borrowConnection();
		c1.close();
		Connection c2 = pool.borrowConnection();
		c2.close();
		
		assertNotSame(c1, c2);
		verify(mcf).create();
		verify(mc, times(2)).isValid(anyInt()); // asserts same connection (with default configuration)
	}
	
	
	@Test(expected=SQLException.class)
	public void fail_after_timeout_when_all_connections_are_borrowed() throws SQLException {
		PoolConfig config = new PoolConfig(1);
		ConnectionPool pool = new ConnectionPool(mcf, config);
		pool.borrowConnection();
		pool.borrowConnection(5, TimeUnit.MILLISECONDS); //blocks and then fails
	}
	
	@Test(expected=SQLException.class)
	public void all_connections_are_invalid() throws SQLException {
		when(mc.isValid(anyInt())).thenReturn(false);
		ConnectionPool pool = new ConnectionPool(mcf);
		pool.borrowConnection();
	}
	
	@Test
	public void a_fresh_connection_has_the_defaults_applied() throws SQLException {
		PoolConfig config = new PoolConfig();
		config.setDefaultAutocommit(false);
		config.setDefaultIsolation(TxIsolation.READ_COMMITTED);
		config.setDefaultReadOnly(false);
		ConnectionPool pool = new ConnectionPool(mcf, config);
		pool.borrowConnection();
		verify(mc).setAutoCommit(false);
		verify(mc).setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
	}
	
	@Test
	public void retry_retrieving_new_connection_after_errors_when_applying_defaults() throws SQLException {
		doThrow(new SQLException("error setting autocommit"))
			.doNothing()
			.when(mc).setAutoCommit(PoolConfig.DEFAULT_AUTOCOMMIT);
		
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		assertTrue(c.isValid(10));
		verify(mcf, times(2)).create();
	}
	
	@Test
	public void a_connection_gets_reset_when_returned() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		c.close();
		verify(mc).clearWarnings();
		verify(mc).rollback();
	}
	
	@Test
	public void error_on_reset_gets_the_connection_discarded() throws SQLException {
		doThrow(new SQLException()).when(mc).clearWarnings();
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		c.close();
		verify(mc).clearWarnings();
		verify(mc).rollback();
		verify(mc).close();
		assertTrue(pool.isEmpty()); // got discarded
	}
	
	@Test
	public void test_if_connection_is_valid_before_after() throws SQLException {
		PoolConfig pc = new PoolConfig();
		pc.setValidateOnBorrow(true);
		pc.setValidateOnReturn(true);
		pc.setValidationTimeout(Duration.seconds(5));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		Connection c = pool.borrowConnection();
		c.close();
		verify(mc, times(2)).isValid(anyInt());
	}
	
	@Test
	public void disable_validation() throws SQLException {
		PoolConfig pc = new PoolConfig();
		pc.setValidateOnBorrow(false);
		pc.setValidateOnReturn(false);
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		Connection c = pool.borrowConnection();
		c.close();
		verify(mc, never()).isValid(anyInt());
	}
	
	@Test
	public void invalid_connectons_get_discarded() throws SQLException {
		when(mc.isValid(anyInt())).thenReturn(true, false); //invalid on return
		doThrow(new SQLException("error closing invalid connection")).when(mc).close();
		
		PoolConfig pc = new PoolConfig();
		pc.setValidateOnBorrow(true);
		pc.setValidateOnReturn(true);
		pc.setValidationTimeout(Duration.seconds(5));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		Connection c = pool.borrowConnection();
		c.close();
		
		verify(mc, times(2)).isValid(anyInt());
		assertTrue(pool.isEmpty());
		verify(mc).close();
	}
	
	@Test
	public void no_connection_available_for_the_idle_collector() throws SQLException, InterruptedException {
		ConnectionPool pool = new ConnectionPool(mcf);
		pool.borrowConnection();
		pool.scheduleEviction(1, TimeUnit.MICROSECONDS);
		TimeUnit.MILLISECONDS.sleep(10);
		verify(mc, never()).close();
	}
	
	@Test
	public void idle_connection_eviction_leaves_minIdle_connections_in_the_pool() throws SQLException, InterruptedException {
		Connection mc1 = mock(Connection.class, "mc1");
		Connection mc2 = mock(Connection.class, "mc2");
		Connection mc3 = mock(Connection.class, "mc3");
		ConnectionFactory mcf = mock(ConnectionFactory.class);
		when(mcf.create()).thenReturn(mc1, mc2, mc3);
		when(mc1.isValid(anyInt())).thenReturn(true);
		when(mc2.isValid(anyInt())).thenReturn(true);
		when(mc3.isValid(anyInt())).thenReturn(true);
		
		PoolConfig pc = new PoolConfig(3);
		pc.setMinIdle(1);
		pc.setMaxIdle(2);
		pc.setIdleTimeout(Duration.millis(1));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		Connection c1 = pool.borrowConnection();
		Connection c2 = pool.borrowConnection();
		Connection c3 = pool.borrowConnection();
		c1.close();
		c2.close();
		c3.close();
		TimeUnit.MILLISECONDS.sleep(5);
		pool.scheduleEviction(1, TimeUnit.MILLISECONDS);
		TimeUnit.MILLISECONDS.sleep(50);
		
		verify(mc1).close();
		verify(mc2).close();
	}
	
	@Test
	public void remove_all_idle_connections() throws SQLException, InterruptedException {
		Connection mc1 = mock(Connection.class, "mc1");
		Connection mc2 = mock(Connection.class, "mc2");
		
		ConnectionFactory mcf = mock(ConnectionFactory.class);
		when(mcf.create()).thenReturn(mc1, mc2);
		when(mc1.isValid(anyInt())).thenReturn(true);
		when(mc2.isValid(anyInt())).thenReturn(true);
		
		
		PoolConfig pc = new PoolConfig(2);
		pc.setMinIdle(0);
		pc.setMaxIdle(2);
		pc.setIdleTimeout(Duration.nanos(50));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		Connection c1 = pool.borrowConnection();
		Connection c2 = pool.borrowConnection();
		c1.close();
		c2.close();
		
		TimeUnit.MILLISECONDS.sleep(5);
		pool.scheduleEviction(1, TimeUnit.MILLISECONDS);
		TimeUnit.MILLISECONDS.sleep(50);
		
		verify(mc1).close();
		verify(mc2).close();
		assertTrue(pool.isEmpty());
	}
	
	@Test
	public void shutdown_an_empty_pool() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		pool.shutdown();
	}
	
	@Test
	public void shutdown_closes_all_available_connections() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		Connection c = pool.borrowConnection();
		c.close();
		pool.shutdown();
		verify(mc).close();
	}
	
	@Test
	public void shutdown_waits_and_closes_borrowed_connections() throws SQLException {
		ConnectionPool pool = new ConnectionPool(mcf);
		final Connection c = pool.borrowConnection();
		Thread exec = new Thread(new Runnable() {
			public void run() { 
				try { c.close(); } catch (SQLException ignore) { } }
		});
		exec.setPriority(Thread.MIN_PRIORITY);
		exec.start();
		pool.shutdown();
		c.close();
		verify(mc).close();
	}
	
	@Test
	public void when_the_shutdown_reaches_timeout_some_connections_are_not_closed() throws SQLException {
		PoolConfig pc = new PoolConfig(10);
		pc.setShutdownTimeout(Duration.millis(5));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		pool.borrowConnection();
		pool.shutdown();
		verify(mc, never()).close();
	}
	
	@Test
	public void shutdown_with_timeout_while_no_connection_permit_available() throws SQLException {
		PoolConfig pc = new PoolConfig(1);
		pc.setShutdownTimeout(Duration.millis(5));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		pool.borrowConnection();
		pool.shutdown();
		verify(mc, never()).close();
	}
	
	@Test
	public void grow_the_size_of_the_pool() throws SQLException {
		PoolConfig pc = new PoolConfig(1);
		pc.setConnectionTimeout(Duration.millis(1));
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		assertEquals(1, pool.getConfiguration().getPoolSize());
		
		pool.borrowConnection();
		try {
			pool.borrowConnection();
			fail("Borrowed connection when no connection available!");
		} catch(SQLException expected) { }
		
		pc.setPoolSize(2);
		pool.reconfigure(pc);
		assertEquals(2, pool.getConfiguration().getPoolSize());
		assertTrue(pool.borrowConnection().isValid(10));
	}
	
	@Test
	public void pool_size_cannot_be_less_than_the_number_of_borrowed_connections() throws SQLException {
		PoolConfig pc = new PoolConfig(3);
		ConnectionPool pool = new ConnectionPool(mcf, pc);
		assertEquals(3, pool.getConfiguration().getPoolSize());
		
		pool.borrowConnection();
		pool.borrowConnection();
		pc.setPoolSize(1);
		pool.reconfigure(pc);
		assertEquals(2, pool.getConfiguration().getPoolSize());
	}
}