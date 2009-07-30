package com.tzavellas.poolng;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * A {@code java.sql.Connection} subclass to be used by the {@code ConnectionPool}.
 * 
 * <p>If the underline {@code java.sql.Connection} is thread-safe then also instances
 * of this class are thread-safe.
 * 
 * @author spiros
 */
class PooledConnection implements Connection {
	
	private final Connection target;
	private final ConnectionHolder holder;
	private final ConnectionPool pool;
	private volatile boolean closed = false;
	
	/**
	 * Construct a {@code PooledConnection}.
	 * 
	 * @param c the <i>target</i> {@code Connection}
	 * @param cp the associated Connection pool
	 */
	public PooledConnection(ConnectionHolder ch, ConnectionPool cp) {
		holder = ch;
		target = ch.getConnection();
		pool = cp;
	}
	
	
	// -----------------------------------------------------------------------
	
	/**
	 * Return the connection to the pool.
	 * 
	 * Does not close the connection but instead returns it to the pool.
	 */
	@Override
	public void close() throws SQLException {
		closed = true;
		pool.returnConnection(holder);
	}
	
	/**
	 * Returns true id {@code close()} has been called, else false.
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		if (closed) {
			return false;
		}
		return target.isValid(timeout);
	}
	
	// -----------------------------------------------------------------------
	
	protected void assertIsOpen() throws SQLException {
		if (closed) {
			throw  new SQLException("Could not execute operation on a closed JDBC connection!");
		}
	}
	
	protected void assertClosedClientInfo() throws SQLClientInfoException {
		if (closed) 
			throw new SQLClientInfoException();
	}
	
	
	// -----------------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public void clearWarnings() throws SQLException {
		assertIsOpen();
		target.clearWarnings();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void commit() throws SQLException {
		assertIsOpen();
		target.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		assertIsOpen();
		return target.createArrayOf(typeName, elements);
	}

	/**
	 * {@inheritDoc}
	 */
	public Blob createBlob() throws SQLException {
		assertIsOpen();
		return target.createBlob();
	}

	/**
	 * {@inheritDoc}
	 */
	public Clob createClob() throws SQLException {
		assertIsOpen();
		return target.createClob();
	}

	/**
	 * {@inheritDoc}
	 */
	public NClob createNClob() throws SQLException {
		assertIsOpen();
		return target.createNClob();
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLXML createSQLXML() throws SQLException {
		assertIsOpen();
		return target.createSQLXML();
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement() throws SQLException {
		assertIsOpen();
		return target.createStatement();
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		assertIsOpen();
		return target.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		assertIsOpen();
		return target.createStatement(resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 */
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		assertIsOpen();
		return target.createStruct(typeName, attributes);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getAutoCommit() throws SQLException {
		assertIsOpen();
		return target.getAutoCommit();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCatalog() throws SQLException {
		assertIsOpen();
		return target.getCatalog();
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getClientInfo() throws SQLException {
		assertIsOpen();
		return target.getClientInfo();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClientInfo(String name) throws SQLException {
		assertIsOpen();
		return target.getClientInfo(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getHoldability() throws SQLException {
		assertIsOpen();
		return target.getHoldability();
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		assertIsOpen();
		return target.getMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTransactionIsolation() throws SQLException {
		assertIsOpen();
		return target.getTransactionIsolation();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		assertIsOpen();
		return target.getTypeMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLWarning getWarnings() throws SQLException {
		assertIsOpen();
		return target.getWarnings();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() throws SQLException {
		assertIsOpen();
		return target.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	public String nativeSQL(String sql) throws SQLException {
		assertIsOpen();
		return target.nativeSQL(sql);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		assertIsOpen();
		return target.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		assertIsOpen();
		return target.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		assertIsOpen();
		return target.prepareCall(sql);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql, autoGeneratedKeys);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql, columnIndexes);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql, columnNames);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		assertIsOpen();
		return target.prepareStatement(sql);
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		assertIsOpen();
		target.releaseSavepoint(savepoint);
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() throws SQLException {
		assertIsOpen();
		target.rollback();
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		assertIsOpen();
		target.rollback(savepoint);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		assertIsOpen();
		target.setAutoCommit(autoCommit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCatalog(String catalog) throws SQLException {
		assertIsOpen();
		target.setCatalog(catalog);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHoldability(int holdability) throws SQLException {
		assertIsOpen();
		target.setHoldability(holdability);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		assertIsOpen();
		target.setReadOnly(readOnly);
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint() throws SQLException {
		assertIsOpen();
		return target.setSavepoint();
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		assertIsOpen();
		return target.setSavepoint(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		assertIsOpen();
		target.setTransactionIsolation(level);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		assertIsOpen();
		target.setTypeMap(map);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		assertClosedClientInfo();
		target.setClientInfo(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		assertClosedClientInfo();
		target.setClientInfo(name, value);
	}
	
	// -----------------------------------------------------------------------
	
	/*
	 * Maybe the following methods should always throw UnsupportedOperationException
	 * or SQLException, even in an open connection. This is  because the client with
	 * this implementations can unwrap an object (maybe a native connection) and use
	 * that object even when the PooledConnection is closed (since the target
	 * connection remains open).   
	 */
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		assertIsOpen();
		return target.isWrapperFor(iface);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		assertIsOpen();
		return target.unwrap(iface);
	}
}
