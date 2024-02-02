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
   public boolean isWrapperFor(Class var1) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isWrapperFor(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet executeQuery(String var1) throws SQLException {
      try {
         return super.executeQuery(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int executeUpdate(String var1) throws SQLException {
      try {
         return super.executeUpdate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getMaxFieldSize() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMaxFieldSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxFieldSize(int var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setMaxFieldSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getMaxRows() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxRows(int var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setMaxRows(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setEscapeProcessing(boolean var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setEscapeProcessing(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getQueryTimeout() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getQueryTimeout();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setQueryTimeout(int var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setQueryTimeout(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void cancel() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).cancel();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCursorName(String var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCursorName(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean execute(String var1) throws SQLException {
      try {
         return super.execute(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         return super.getResultSet();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getUpdateCount() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMoreResults();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFetchDirection(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getFetchDirection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFetchSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getFetchSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getResultSetConcurrency();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getResultSetType();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void addBatch(String var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).addBatch(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void clearBatch() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         return super.executeBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Connection getConnection() throws SQLException {
      try {
         return super.getConnection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean getMoreResults(int var1) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMoreResults(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         return super.getGeneratedKeys();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int executeUpdate(String var1, int var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int executeUpdate(String var1, int[] var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int executeUpdate(String var1, String[] var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, int var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, int[] var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, String[] var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getResultSetHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isClosed();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setPoolable(boolean var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setPoolable(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean isPoolable() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isPoolable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void closeOnCompletion() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).closeOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isCloseOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long getLargeUpdateCount() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getLargeUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setLargeMaxRows(long var1) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setLargeMaxRows(var1);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getLargeMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long executeLargeUpdate(String var1) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long executeLargeUpdate(String var1, int var2) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, int[] var2) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, String[] var2) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public ResultSet executeQuery() throws SQLException {
      try {
         return super.executeQuery();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int executeUpdate() throws SQLException {
      try {
         return super.executeUpdate();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setNull(int var1, int var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNull(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBoolean(int var1, boolean var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBoolean(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setByte(int var1, byte var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setByte(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setShort(int var1, short var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setShort(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setInt(int var1, int var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setInt(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setLong(int var1, long var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setLong(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setFloat(int var1, float var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFloat(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDouble(int var1, double var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDouble(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBigDecimal(int var1, BigDecimal var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setString(int var1, String var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBytes(int var1, byte[] var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBytes(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDate(int var1, Date var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTime(int var1, Time var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTimestamp(int var1, Timestamp var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setUnicodeStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void clearParameters() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearParameters();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setObject(int var1, Object var2, int var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setObject(int var1, Object var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute() throws SQLException {
      try {
         return super.execute();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void addBatch() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).addBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setRef(int var1, Ref var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setRef(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBlob(int var1, Blob var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Clob var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setArray(int var1, Array var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setArray(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setDate(int var1, Date var2, Calendar var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDate(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTime(int var1, Time var2, Calendar var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTime(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTimestamp(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setNull(int var1, int var2, String var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNull(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setURL(int var1, URL var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setURL(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getParameterMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setRowId(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNString(int var1, String var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setSQLXML(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setObject(int var1, Object var2, int var3, int var4) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setAsciiStream(int var1, InputStream var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBinaryStream(int var1, InputStream var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public long executeLargeUpdate() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   HikariProxyPreparedStatement(ProxyConnection var1, PreparedStatement var2) {
      super(var1, var2);
   }
}
