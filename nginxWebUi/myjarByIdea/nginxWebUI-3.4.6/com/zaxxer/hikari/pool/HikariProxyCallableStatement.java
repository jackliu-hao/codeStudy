package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;

public final class HikariProxyCallableStatement extends ProxyCallableStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement, CallableStatement {
   public boolean isWrapperFor(Class var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isWrapperFor(var1);
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
         return ((CallableStatement)super.delegate).getMaxFieldSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxFieldSize(int var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setMaxFieldSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getMaxRows() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxRows(int var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setMaxRows(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setEscapeProcessing(boolean var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setEscapeProcessing(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getQueryTimeout() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getQueryTimeout();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setQueryTimeout(int var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setQueryTimeout(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void cancel() throws SQLException {
      try {
         ((CallableStatement)super.delegate).cancel();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCursorName(String var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCursorName(var1);
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
         return ((CallableStatement)super.delegate).getUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMoreResults();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFetchDirection(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFetchDirection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFetchSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFetchSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getResultSetConcurrency();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getResultSetType();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void addBatch(String var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).addBatch(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void clearBatch() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearBatch();
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
         return ((CallableStatement)super.delegate).getMoreResults(var1);
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
         return ((CallableStatement)super.delegate).getResultSetHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isClosed();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setPoolable(boolean var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setPoolable(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean isPoolable() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isPoolable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void closeOnCompletion() throws SQLException {
      try {
         ((CallableStatement)super.delegate).closeOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isCloseOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long getLargeUpdateCount() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLargeUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setLargeMaxRows(long var1) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLargeMaxRows(var1);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLargeMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long executeLargeUpdate(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long executeLargeUpdate(String var1, int var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, int[] var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, String[] var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(var1, var2);
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
         ((CallableStatement)super.delegate).setNull(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBoolean(int var1, boolean var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBoolean(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setByte(int var1, byte var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setByte(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setShort(int var1, short var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setShort(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setInt(int var1, int var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setInt(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setLong(int var1, long var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLong(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setFloat(int var1, float var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFloat(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDouble(int var1, double var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDouble(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBigDecimal(int var1, BigDecimal var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setString(int var1, String var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBytes(int var1, byte[] var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBytes(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDate(int var1, Date var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTime(int var1, Time var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTimestamp(int var1, Timestamp var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setUnicodeStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void clearParameters() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearParameters();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setObject(int var1, Object var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setObject(int var1, Object var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2);
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
         ((CallableStatement)super.delegate).addBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setRef(int var1, Ref var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRef(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBlob(int var1, Blob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Clob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setArray(int var1, Array var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setArray(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setDate(int var1, Date var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTime(int var1, Time var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setNull(int var1, int var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setURL(int var1, URL var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setURL(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getParameterMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRowId(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNString(int var1, String var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setSQLXML(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setObject(int var1, Object var2, int var3, int var4) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setAsciiStream(int var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBinaryStream(int var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public long executeLargeUpdate() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void registerOutParameter(int var1, int var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void registerOutParameter(int var1, int var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public boolean wasNull() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).wasNull();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getString(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean getBoolean(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBoolean(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public byte getByte(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getByte(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public short getShort(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getShort(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getInt(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getInt(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long getLong(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLong(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public float getFloat(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFloat(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public double getDouble(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDouble(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public byte[] getBytes(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBytes(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Time getTime(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(int var1, Map var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Ref getRef(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRef(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Blob getBlob(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBlob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Clob getClob(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Array getArray(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getArray(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void registerOutParameter(int var1, int var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(String var1, int var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void registerOutParameter(String var1, int var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(String var1, int var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public URL getURL(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getURL(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setURL(String var1, URL var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setURL(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNull(String var1, int var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBoolean(String var1, boolean var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBoolean(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setByte(String var1, byte var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setByte(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setShort(String var1, short var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setShort(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setInt(String var1, int var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setInt(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setLong(String var1, long var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLong(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setFloat(String var1, float var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFloat(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDouble(String var1, double var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDouble(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBigDecimal(String var1, BigDecimal var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setString(String var1, String var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBytes(String var1, byte[] var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBytes(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setDate(String var1, Date var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTime(String var1, Time var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setTimestamp(String var1, Timestamp var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setObject(String var1, Object var2, int var3, int var4) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setObject(String var1, Object var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setObject(String var1, Object var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setDate(String var1, Date var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTime(String var1, Time var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void setNull(String var1, int var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public String getString(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean getBoolean(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBoolean(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public byte getByte(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getByte(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public short getShort(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getShort(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getInt(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getInt(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long getLong(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLong(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public float getFloat(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFloat(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public double getDouble(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDouble(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public byte[] getBytes(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBytes(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Time getTime(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(String var1, Map var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Ref getRef(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRef(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Blob getBlob(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBlob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Clob getClob(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Array getArray(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getArray(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public URL getURL(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getURL(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public RowId getRowId(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRowId(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public RowId getRowId(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRowId(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setRowId(String var1, RowId var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRowId(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNString(String var1, String var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(String var1, NClob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBlob(String var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setNClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public NClob getNClob(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public NClob getNClob(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setSQLXML(String var1, SQLXML var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setSQLXML(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getSQLXML(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getSQLXML(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getNString(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getNString(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setBlob(String var1, Blob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(String var1, Clob var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setAsciiStream(String var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBinaryStream(String var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setClob(String var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setBlob(String var1, InputStream var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setNClob(String var1, Reader var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Object getObject(int var1, Class var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Object getObject(String var1, Class var2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void setObject(String var1, Object var2, SQLType var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(int var1, SQLType var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void registerOutParameter(int var1, SQLType var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(int var1, SQLType var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(String var1, SQLType var2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void registerOutParameter(String var1, SQLType var2, int var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void registerOutParameter(String var1, SQLType var2, String var3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   protected HikariProxyCallableStatement(ProxyConnection var1, CallableStatement var2) {
      super(var1, var2);
   }
}
