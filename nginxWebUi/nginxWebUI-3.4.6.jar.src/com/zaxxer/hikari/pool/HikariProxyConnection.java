package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.FastList;
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
import java.sql.Wrapper;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class HikariProxyConnection extends ProxyConnection implements Wrapper, AutoCloseable, Connection {
  public Statement createStatement() throws SQLException {
    try {
      return super.createStatement();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString) throws SQLException {
    try {
      return super.prepareStatement(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public CallableStatement prepareCall(String paramString) throws SQLException {
    try {
      return super.prepareCall(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String nativeSQL(String paramString) throws SQLException {
    try {
      return this.delegate.nativeSQL(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setAutoCommit(boolean paramBoolean) throws SQLException {
    try {
      super.setAutoCommit(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getAutoCommit() throws SQLException {
    try {
      return this.delegate.getAutoCommit();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void commit() throws SQLException {
    try {
      super.commit();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void rollback() throws SQLException {
    try {
      super.rollback();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isClosed() throws SQLException {
    try {
      return super.isClosed();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public DatabaseMetaData getMetaData() throws SQLException {
    try {
      return super.getMetaData();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setReadOnly(boolean paramBoolean) throws SQLException {
    try {
      super.setReadOnly(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return this.delegate.isReadOnly();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setCatalog(String paramString) throws SQLException {
    try {
      super.setCatalog(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getCatalog() throws SQLException {
    try {
      return this.delegate.getCatalog();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTransactionIsolation(int paramInt) throws SQLException {
    try {
      super.setTransactionIsolation(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getTransactionIsolation() throws SQLException {
    try {
      return this.delegate.getTransactionIsolation();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      return this.delegate.getWarnings();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void clearWarnings() throws SQLException {
    try {
      this.delegate.clearWarnings();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Statement createStatement(int paramInt1, int paramInt2) throws SQLException {
    try {
      return super.createStatement(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2) throws SQLException {
    try {
      return super.prepareStatement(paramString, paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2) throws SQLException {
    try {
      return super.prepareCall(paramString, paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Map getTypeMap() throws SQLException {
    try {
      return this.delegate.getTypeMap();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTypeMap(Map<String, Class<?>> paramMap) throws SQLException {
    try {
      this.delegate.setTypeMap(paramMap);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setHoldability(int paramInt) throws SQLException {
    try {
      this.delegate.setHoldability(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getHoldability() throws SQLException {
    try {
      return this.delegate.getHoldability();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Savepoint setSavepoint() throws SQLException {
    try {
      return this.delegate.setSavepoint();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Savepoint setSavepoint(String paramString) throws SQLException {
    try {
      return this.delegate.setSavepoint(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void rollback(Savepoint paramSavepoint) throws SQLException {
    try {
      super.rollback(paramSavepoint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void releaseSavepoint(Savepoint paramSavepoint) throws SQLException {
    try {
      this.delegate.releaseSavepoint(paramSavepoint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Statement createStatement(int paramInt1, int paramInt2, int paramInt3) throws SQLException {
    try {
      return super.createStatement(paramInt1, paramInt2, paramInt3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException {
    try {
      return super.prepareStatement(paramString, paramInt1, paramInt2, paramInt3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException {
    try {
      return super.prepareCall(paramString, paramInt1, paramInt2, paramInt3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString, int paramInt) throws SQLException {
    try {
      return super.prepareStatement(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfint) throws SQLException {
    try {
      return super.prepareStatement(paramString, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString) throws SQLException {
    try {
      return super.prepareStatement(paramString, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Clob createClob() throws SQLException {
    try {
      return this.delegate.createClob();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Blob createBlob() throws SQLException {
    try {
      return this.delegate.createBlob();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public NClob createNClob() throws SQLException {
    try {
      return this.delegate.createNClob();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public SQLXML createSQLXML() throws SQLException {
    try {
      return this.delegate.createSQLXML();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isValid(int paramInt) throws SQLException {
    try {
      return this.delegate.isValid(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setClientInfo(String paramString1, String paramString2) throws SQLClientInfoException {
    this.delegate.setClientInfo(paramString1, paramString2);
  }
  
  public void setClientInfo(Properties paramProperties) throws SQLClientInfoException {
    this.delegate.setClientInfo(paramProperties);
  }
  
  public String getClientInfo(String paramString) throws SQLException {
    try {
      return this.delegate.getClientInfo(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Properties getClientInfo() throws SQLException {
    try {
      return this.delegate.getClientInfo();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Array createArrayOf(String paramString, Object[] paramArrayOfObject) throws SQLException {
    try {
      return this.delegate.createArrayOf(paramString, paramArrayOfObject);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Struct createStruct(String paramString, Object[] paramArrayOfObject) throws SQLException {
    try {
      return this.delegate.createStruct(paramString, paramArrayOfObject);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setSchema(String paramString) throws SQLException {
    try {
      super.setSchema(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getSchema() throws SQLException {
    try {
      return this.delegate.getSchema();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void abort(Executor paramExecutor) throws SQLException {
    try {
      this.delegate.abort(paramExecutor);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNetworkTimeout(Executor paramExecutor, int paramInt) throws SQLException {
    try {
      super.setNetworkTimeout(paramExecutor, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getNetworkTimeout() throws SQLException {
    try {
      return this.delegate.getNetworkTimeout();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  protected HikariProxyConnection(PoolEntry paramPoolEntry, Connection paramConnection, FastList<Statement> paramFastList, ProxyLeakTask paramProxyLeakTask, long paramLong, boolean paramBoolean1, boolean paramBoolean2) {
    super(paramPoolEntry, paramConnection, paramFastList, paramProxyLeakTask, paramLong, paramBoolean1, paramBoolean2);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariProxyConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */