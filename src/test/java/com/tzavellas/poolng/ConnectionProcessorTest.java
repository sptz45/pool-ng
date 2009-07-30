package com.tzavellas.poolng;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class ConnectionProcessorTest {
	
	private static final ConnectionProcessor proc = new ConnectionProcessor();
	
	@Test
	public void apply_default_properties_to_connection_using_default_pool_config() throws SQLException {
		Connection mc = mock(Connection.class);
		proc.applyDefaults(mc, new PoolConfig());
		verify(mc).setAutoCommit(PoolConfig.DEFAULT_AUTOCOMMIT);
	}
	
	@Test
	public void apply_default_properties_to_connection() throws SQLException {
		Connection mc = mock(Connection.class);
		PoolConfig config = new PoolConfig();
		config.setDefaultAutocommit(false);
		config.setDefaultIsolation(TxIsolation.REPEATABLE_READ);
		config.setDefaultReadOnly(false);
		config.setDefaultCatalog("e-shop");
		proc.applyDefaults(mc, config);
		
		verify(mc).setAutoCommit(false);
		verify(mc).setReadOnly(false);
		verify(mc).setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		verify(mc).setCatalog("e-shop");
	}
	
	@Test(expected=SQLException.class)
	public void applyDefaults_does_not_catch_exceptions() throws SQLException {
		Connection mc = mock(Connection.class);
		doThrow(new SQLException()).when(mc).setAutoCommit(PoolConfig.DEFAULT_AUTOCOMMIT);
		proc.applyDefaults(mc, new PoolConfig());
	}
	
	@Test
	public void reset_always_clears_warnings() throws SQLException {
		Connection mc = mock(Connection.class);
		when(mc.getAutoCommit()).thenReturn(true);
		
		boolean sucess = proc.reset(mc);
		
		assertTrue(sucess);
		verify(mc).clearWarnings();
	}
	
	@Test
	public void reset_fails_when_clear_warnings_fails() throws SQLException {
		Connection mc = mock(Connection.class);
		doThrow(new SQLException("Could not clear warnings!")).when(mc).clearWarnings();
		when(mc.getAutoCommit()).thenReturn(false);
		
		boolean sucess = proc.reset(mc);
		
		assertFalse(sucess);
		verify(mc).clearWarnings();
		verify(mc).rollback();
	}
	
	@Test
	public void reset_fails_when_rollback_fails() throws SQLException {
		Connection mc = mock(Connection.class);
		doThrow(new SQLException("Error on rollback!!")).when(mc).rollback();
		
		boolean sucess = proc.reset(mc);
		
		assertFalse(sucess);
		verify(mc).clearWarnings();
		verify(mc).rollback();
	}

	@Test(expected=IllegalArgumentException.class)
	public void isValid_expects_timeout_to_last_more_thatn_one_second() {
		proc.isValid(null, Duration.millis(400));
	}
	
	@Test
	public void test_valid_connection() throws SQLException {
		Connection mc = mock(Connection.class);
		when(mc.isValid(5)).thenReturn(true);
		assertTrue(proc.isValid(mc, Duration.seconds(5)));
	}
	
	@Test
	public void test_invalid_connection() throws SQLException {
		Connection mc = mock(Connection.class);
		when(mc.isValid(5)).thenReturn(false);
		assertFalse(proc.isValid(mc, Duration.seconds(5)));
	}
	
	@Test 
	public void a_connection_is_invalid_if_throws_exception() throws SQLException {
		Connection mc = mock(Connection.class);
		doThrow(new SQLException("invalid")).when(mc).isValid(5);
		assertFalse(proc.isValid(mc, Duration.seconds(5)));
	}
}
