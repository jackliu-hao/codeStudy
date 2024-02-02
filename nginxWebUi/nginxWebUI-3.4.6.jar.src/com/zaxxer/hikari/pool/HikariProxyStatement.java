package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public final class HikariProxyStatement extends ProxyStatement implements Wrapper, AutoCloseable, Statement {
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
    try {
      return this.delegate.isWrapperFor(paramClass);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet executeQuery(String paramString) throws SQLException {
    try {
      return super.executeQuery(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int executeUpdate(String paramString) throws SQLException {
    try {
      return super.executeUpdate(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxFieldSize() throws SQLException {
    try {
      return this.delegate.getMaxFieldSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setMaxFieldSize(int paramInt) throws SQLException {
    try {
      this.delegate.setMaxFieldSize(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxRows() throws SQLException {
    try {
      return this.delegate.getMaxRows();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setMaxRows(int paramInt) throws SQLException {
    try {
      this.delegate.setMaxRows(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setEscapeProcessing(boolean paramBoolean) throws SQLException {
    try {
      this.delegate.setEscapeProcessing(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getQueryTimeout() throws SQLException {
    try {
      return this.delegate.getQueryTimeout();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setQueryTimeout(int paramInt) throws SQLException {
    try {
      this.delegate.setQueryTimeout(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void cancel() throws SQLException {
    try {
      this.delegate.cancel();
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
  
  public void setCursorName(String paramString) throws SQLException {
    try {
      this.delegate.setCursorName(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean execute(String paramString) throws SQLException {
    try {
      return super.execute(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getResultSet() throws SQLException {
    try {
      return super.getResultSet();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getUpdateCount() throws SQLException {
    try {
      return this.delegate.getUpdateCount();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getMoreResults() throws SQLException {
    try {
      return this.delegate.getMoreResults();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setFetchDirection(int paramInt) throws SQLException {
    try {
      this.delegate.setFetchDirection(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getFetchDirection() throws SQLException {
    try {
      return this.delegate.getFetchDirection();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setFetchSize(int paramInt) throws SQLException {
    try {
      this.delegate.setFetchSize(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getFetchSize() throws SQLException {
    try {
      return this.delegate.getFetchSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetConcurrency() throws SQLException {
    try {
      return this.delegate.getResultSetConcurrency();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetType() throws SQLException {
    try {
      return this.delegate.getResultSetType();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void addBatch(String paramString) throws SQLException {
    try {
      this.delegate.addBatch(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void clearBatch() throws SQLException {
    try {
      this.delegate.clearBatch();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int[] executeBatch() throws SQLException {
    try {
      return super.executeBatch();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Connection getConnection() throws SQLException {
    try {
      return super.getConnection();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getMoreResults(int paramInt) throws SQLException {
    try {
      return this.delegate.getMoreResults(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getGeneratedKeys() throws SQLException {
    try {
      return super.getGeneratedKeys();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int executeUpdate(String paramString, int paramInt) throws SQLException {
    try {
      return super.executeUpdate(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int executeUpdate(String paramString, int[] paramArrayOfint) throws SQLException {
    try {
      return super.executeUpdate(paramString, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int executeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
    try {
      return super.executeUpdate(paramString, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean execute(String paramString, int paramInt) throws SQLException {
    try {
      return super.execute(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean execute(String paramString, int[] paramArrayOfint) throws SQLException {
    try {
      return super.execute(paramString, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean execute(String paramString, String[] paramArrayOfString) throws SQLException {
    try {
      return super.execute(paramString, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetHoldability() throws SQLException {
    try {
      return this.delegate.getResultSetHoldability();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isClosed() throws SQLException {
    try {
      return this.delegate.isClosed();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setPoolable(boolean paramBoolean) throws SQLException {
    try {
      this.delegate.setPoolable(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isPoolable() throws SQLException {
    try {
      return this.delegate.isPoolable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void closeOnCompletion() throws SQLException {
    try {
      this.delegate.closeOnCompletion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isCloseOnCompletion() throws SQLException {
    try {
      return this.delegate.isCloseOnCompletion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLargeUpdateCount() throws SQLException {
    try {
      return this.delegate.getLargeUpdateCount();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setLargeMaxRows(long paramLong) throws SQLException {
    try {
      this.delegate.setLargeMaxRows(paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLargeMaxRows() throws SQLException {
    try {
      return this.delegate.getLargeMaxRows();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long[] executeLargeBatch() throws SQLException {
    try {
      return this.delegate.executeLargeBatch();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString) throws SQLException {
    try {
      return this.delegate.executeLargeUpdate(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, int paramInt) throws SQLException {
    try {
      return this.delegate.executeLargeUpdate(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, int[] paramArrayOfint) throws SQLException {
    try {
      return this.delegate.executeLargeUpdate(paramString, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
    try {
      return this.delegate.executeLargeUpdate(paramString, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  HikariProxyStatement(ProxyConnection paramProxyConnection, Statement paramStatement) {
    super(paramProxyConnection, paramStatement);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariProxyStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */