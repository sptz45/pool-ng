package com.tzavellas.poolng;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class PooledConnectionTest {
	
	@Test
	public void the_wrapped_connection_does_not_get_closed_when_close_gets_invoked_in_PooledConnection() throws SQLException {
		Connection mc = mock(Connection.class);
		ConnectionPool mp = mock(ConnectionPool.class);
		
		ConnectionHolder ch = new ConnectionHolder(mc);
		PooledConnection c = new PooledConnection(ch, mp);
		c.close();
		
		assertTrue("Connenction should appear closed", c.isClosed());
		verify(mc, never()).close();
		verify(mp).returnConnection(ch);
	}
	
	@Test
	public void a_connection_is_invalid_after_being_closed() throws SQLException {
		Connection mc = mock(Connection.class);
		ConnectionPool mp = mock(ConnectionPool.class);
		
		ConnectionHolder ch = new ConnectionHolder(mc);
		PooledConnection c = new PooledConnection(ch, mp);
		c.close();
		
		verify(mc, never()).close();
		assertTrue("The connenction should appear closed", c.isClosed());
		assertFalse("The connection should be invalid after closed", c.isValid(0));
	}
	
	@Test
	public void test_after_close_schemantics() throws SQLException {
		Connection mc = mock(Connection.class);
		ConnectionPool mp = mock(ConnectionPool.class);
		
		ConnectionHolder ch = new ConnectionHolder(mc);
		PooledConnection c = new PooledConnection(ch, mp);
		c.close();
		
		verify(mc, never()).close();
		assertTrue("The connenction should appear closed", c.isClosed());
		
		try { c.clearWarnings(); fail(); } catch (SQLException expected) { }
		try { c.commit(); fail(); } catch (SQLException expected) { }
		try { c.createArrayOf("VARCHAR", new Object[] { "hello"}); fail(); } catch (SQLException expected) { }
		try { c.createBlob(); fail(); } catch (SQLException expected) { }
		try { c.createClob(); fail(); } catch (SQLException expected) { }
		try { c.createNClob(); fail(); } catch (SQLException expected) { }
		try { c.createStatement(); fail(); } catch (SQLException expected) { }
		try { c.createStatement(0, 1); fail(); } catch (SQLException expected) { }
		try { c.createStatement(0, 0, 0); fail(); } catch (SQLException expected) { }
		try { c.createStruct("", new Object[] { }); fail(); } catch (SQLException expected) { }
		try { c.getCatalog(); fail(); } catch (SQLException expected) { }
		try { c.getClientInfo(); fail(); } catch (SQLException expected) { }
		try { c.getClientInfo("name"); fail(); } catch (SQLException expected) { }
		try { c.getHoldability(); fail(); } catch (SQLException expected) { }
		try { c.getMetaData(); fail(); } catch (SQLException expected) { }
		try { c.getTransactionIsolation(); fail(); } catch (SQLException expected) { }
		try { c.getTypeMap(); fail(); } catch (SQLException expected) { }
		try { c.getWarnings(); fail(); } catch (SQLException expected) { }
		try { c.isReadOnly(); fail(); } catch (SQLException expected) { }
		try { c.nativeSQL("sql"); fail(); } catch (SQLException expected) { }
		try { c.prepareCall("sql"); fail(); } catch (SQLException expected) { }
		try { c.prepareCall("sql", 1, 1); fail(); } catch (SQLException expected) { }
		try { c.prepareCall("sql", 1, 1, 1); fail(); } catch (SQLException expected) { }
		try { c.prepareStatement("sql"); fail(); } catch (SQLException expected) { }
		try { c.prepareStatement("sql", 1); fail(); } catch (SQLException expected) { }
		try { c.prepareStatement("aql", new int[] { 1 }); fail(); } catch (SQLException expected) { }
		try { c.prepareStatement("sql", new String[] { "name" }); fail(); } catch (SQLException expected) { }
		try { c.releaseSavepoint(null); fail(); } catch (SQLException expected) { }
		try { c.rollback(); fail(); } catch (SQLException expected) { }
		try { c.rollback(null); fail(); } catch (SQLException expected) { }
		try { c.setAutoCommit(true); fail(); } catch (SQLException expected) { }
		try { c.setCatalog("catalog"); fail(); } catch (SQLException expected) { }
		try { c.setClientInfo(new Properties()); fail(); } catch (SQLException expected) { }
		try { c.setClientInfo("name", "value"); fail(); } catch (SQLException expected) { }
		try { c.setHoldability(1); fail(); } catch (SQLException expected) { }
		try { c.setReadOnly(true); fail(); } catch (SQLException expected) { }
		try { c.setSavepoint(); fail(); } catch (SQLException expected) { }
		try { c.setSavepoint("name"); fail(); } catch (SQLException expected) { }
		try { c.setTransactionIsolation(2); fail(); } catch (SQLException expected) { }
		try { c.setTypeMap(null); fail(); } catch (SQLException expected) { }
		try { c.isWrapperFor(Connection.class); fail(); } catch (SQLException expected) { }
		try { c.unwrap(Connection.class); fail(); } catch (SQLException expected) { }
	}
}
