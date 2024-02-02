package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;

public final class HikariProxyPreparedStatement extends ProxyPreparedStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement {
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).isWrapperFor(paramClass);
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
      return ((PreparedStatement)this.delegate).getMaxFieldSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setMaxFieldSize(int paramInt) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setMaxFieldSize(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxRows() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getMaxRows();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setMaxRows(int paramInt) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setMaxRows(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setEscapeProcessing(boolean paramBoolean) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setEscapeProcessing(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getQueryTimeout() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getQueryTimeout();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setQueryTimeout(int paramInt) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setQueryTimeout(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void cancel() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).cancel();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public SQLWarning getWarnings() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getWarnings();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void clearWarnings() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).clearWarnings();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setCursorName(String paramString) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setCursorName(paramString);
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
      return ((PreparedStatement)this.delegate).getUpdateCount();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getMoreResults() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getMoreResults();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setFetchDirection(int paramInt) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setFetchDirection(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getFetchDirection() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getFetchDirection();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setFetchSize(int paramInt) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setFetchSize(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getFetchSize() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getFetchSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetConcurrency() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getResultSetConcurrency();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetType() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getResultSetType();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void addBatch(String paramString) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).addBatch(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void clearBatch() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).clearBatch();
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
      return ((PreparedStatement)this.delegate).getMoreResults(paramInt);
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
      return ((PreparedStatement)this.delegate).getResultSetHoldability();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isClosed() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).isClosed();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setPoolable(boolean paramBoolean) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setPoolable(paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isPoolable() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).isPoolable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void closeOnCompletion() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).closeOnCompletion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isCloseOnCompletion() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).isCloseOnCompletion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLargeUpdateCount() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getLargeUpdateCount();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setLargeMaxRows(long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setLargeMaxRows(paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLargeMaxRows() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getLargeMaxRows();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long[] executeLargeBatch() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeBatch();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString) throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeUpdate(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, int paramInt) throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeUpdate(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, int[] paramArrayOfint) throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeUpdate(paramString, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate(String paramString, String[] paramArrayOfString) throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeUpdate(paramString, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet executeQuery() throws SQLException {
    try {
      return super.executeQuery();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int executeUpdate() throws SQLException {
    try {
      return super.executeUpdate();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNull(int paramInt1, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNull(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBoolean(int paramInt, boolean paramBoolean) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBoolean(paramInt, paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setByte(int paramInt, byte paramByte) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setByte(paramInt, paramByte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setShort(int paramInt, short paramShort) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setShort(paramInt, paramShort);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setInt(int paramInt1, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setInt(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setLong(int paramInt, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setLong(paramInt, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setFloat(int paramInt, float paramFloat) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setFloat(paramInt, paramFloat);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setDouble(int paramInt, double paramDouble) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setDouble(paramInt, paramDouble);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBigDecimal(paramInt, paramBigDecimal);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setString(int paramInt, String paramString) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setString(paramInt, paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBytes(int paramInt, byte[] paramArrayOfbyte) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBytes(paramInt, paramArrayOfbyte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setDate(int paramInt, Date paramDate) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setDate(paramInt, paramDate);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTime(int paramInt, Time paramTime) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setTime(paramInt, paramTime);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setTimestamp(paramInt, paramTimestamp);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setAsciiStream(paramInt1, paramInputStream, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setUnicodeStream(paramInt1, paramInputStream, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBinaryStream(paramInt1, paramInputStream, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void clearParameters() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).clearParameters();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setObject(paramInt1, paramObject, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setObject(int paramInt, Object paramObject) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setObject(paramInt, paramObject);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean execute() throws SQLException {
    try {
      return super.execute();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void addBatch() throws SQLException {
    try {
      ((PreparedStatement)this.delegate).addBatch();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setCharacterStream(paramInt1, paramReader, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setRef(int paramInt, Ref paramRef) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setRef(paramInt, paramRef);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBlob(int paramInt, Blob paramBlob) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBlob(paramInt, paramBlob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setClob(int paramInt, Clob paramClob) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setClob(paramInt, paramClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setArray(int paramInt, Array paramArray) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setArray(paramInt, paramArray);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getMetaData();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setDate(int paramInt, Date paramDate, Calendar paramCalendar) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setDate(paramInt, paramDate, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setTime(paramInt, paramTime, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setTimestamp(paramInt, paramTimestamp, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNull(int paramInt1, int paramInt2, String paramString) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNull(paramInt1, paramInt2, paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setURL(int paramInt, URL paramURL) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setURL(paramInt, paramURL);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ParameterMetaData getParameterMetaData() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).getParameterMetaData();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setRowId(int paramInt, RowId paramRowId) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setRowId(paramInt, paramRowId);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNString(int paramInt, String paramString) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNString(paramInt, paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNCharacterStream(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNClob(int paramInt, NClob paramNClob) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNClob(paramInt, paramNClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setClob(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBlob(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNClob(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setSQLXML(paramInt, paramSQLXML);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setObject(paramInt1, paramObject, paramInt2, paramInt3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setAsciiStream(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBinaryStream(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setCharacterStream(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setAsciiStream(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBinaryStream(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setCharacterStream(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNCharacterStream(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setClob(int paramInt, Reader paramReader) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setClob(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setBlob(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setNClob(int paramInt, Reader paramReader) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setNClob(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setObject(paramInt1, paramObject, paramSQLType, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void setObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException {
    try {
      ((PreparedStatement)this.delegate).setObject(paramInt, paramObject, paramSQLType);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long executeLargeUpdate() throws SQLException {
    try {
      return ((PreparedStatement)this.delegate).executeLargeUpdate();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  HikariProxyPreparedStatement(ProxyConnection paramProxyConnection, PreparedStatement paramPreparedStatement) {
    super(paramProxyConnection, paramPreparedStatement);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariProxyPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */