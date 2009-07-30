package com.tzavellas.poolng.ds;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.Test;

import com.tzavellas.poolng.ConnectionPool;


public class DefaultDataSourceTest {
	
	ConnectionPool mp = mock(ConnectionPool.class);
	
	@Test
	public void retrieve_a_connection_from_the_datasource() throws SQLException {
		DataSource ds = new DefaultDataSource(mp);
		ds.getConnection();
		verify(mp).borrowConnection();
	}

	@Test
	public void configure_login_timeout() throws SQLException {
		DataSource ds = new DefaultDataSource(mp);
		ds.getConnection();
		verify(mp).borrowConnection();
		
		ds.setLoginTimeout(2);
		ds.getConnection();
		verify(mp).borrowConnection(2, TimeUnit.SECONDS);
		assertEquals(2, ds.getLoginTimeout());
	}
	
	@Test
	public void unsupported_methods() throws SQLException {
		DataSource ds = new DefaultDataSource(mp);
		try { ds.getConnection("username", "password"); fail(); } catch (UnsupportedOperationException expected) { }
		try { ds.setLogWriter(null); fail(); } catch (UnsupportedOperationException expected) { }
		try { ds.getLogWriter(); fail(); } catch (UnsupportedOperationException expected) { }
		try { ds.unwrap(null); fail(); } catch (SQLException expected) { }
		assertFalse(ds.isWrapperFor(String.class));
	}
}
