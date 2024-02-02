package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Map;

public final class HikariProxyResultSet extends ProxyResultSet implements Wrapper, AutoCloseable, ResultSet {
   public boolean isWrapperFor(Class var1) throws SQLException {
      try {
         return super.delegate.isWrapperFor(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void close() throws Exception {
      ((ResultSet)super.delegate).close();
   }

   public boolean next() throws SQLException {
      try {
         return super.delegate.next();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean wasNull() throws SQLException {
      try {
         return super.delegate.wasNull();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getString(int var1) throws SQLException {
      try {
         return super.delegate.getString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean getBoolean(int var1) throws SQLException {
      try {
         return super.delegate.getBoolean(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public byte getByte(int var1) throws SQLException {
      try {
         return super.delegate.getByte(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public short getShort(int var1) throws SQLException {
      try {
         return super.delegate.getShort(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getInt(int var1) throws SQLException {
      try {
         return super.delegate.getInt(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long getLong(int var1) throws SQLException {
      try {
         return super.delegate.getLong(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public float getFloat(int var1) throws SQLException {
      try {
         return super.delegate.getFloat(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public double getDouble(int var1) throws SQLException {
      try {
         return super.delegate.getDouble(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      try {
         return super.delegate.getBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public byte[] getBytes(int var1) throws SQLException {
      try {
         return super.delegate.getBytes(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(int var1) throws SQLException {
      try {
         return super.delegate.getDate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Time getTime(int var1) throws SQLException {
      try {
         return super.delegate.getTime(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      try {
         return super.delegate.getTimestamp(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      try {
         return super.delegate.getAsciiStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getUnicodeStream(int var1) throws SQLException {
      try {
         return super.delegate.getUnicodeStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      try {
         return super.delegate.getBinaryStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getString(String var1) throws SQLException {
      try {
         return super.delegate.getString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean getBoolean(String var1) throws SQLException {
      try {
         return super.delegate.getBoolean(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public byte getByte(String var1) throws SQLException {
      try {
         return super.delegate.getByte(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public short getShort(String var1) throws SQLException {
      try {
         return super.delegate.getShort(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getInt(String var1) throws SQLException {
      try {
         return super.delegate.getInt(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long getLong(String var1) throws SQLException {
      try {
         return super.delegate.getLong(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public float getFloat(String var1) throws SQLException {
      try {
         return super.delegate.getFloat(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public double getDouble(String var1) throws SQLException {
      try {
         return super.delegate.getDouble(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      try {
         return super.delegate.getBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public byte[] getBytes(String var1) throws SQLException {
      try {
         return super.delegate.getBytes(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(String var1) throws SQLException {
      try {
         return super.delegate.getDate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Time getTime(String var1) throws SQLException {
      try {
         return super.delegate.getTime(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      try {
         return super.delegate.getTimestamp(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      try {
         return super.delegate.getAsciiStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getUnicodeStream(String var1) throws SQLException {
      try {
         return super.delegate.getUnicodeStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      try {
         return super.delegate.getBinaryStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         return super.delegate.getWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         super.delegate.clearWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getCursorName() throws SQLException {
      try {
         return super.delegate.getCursorName();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return super.delegate.getMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Object getObject(int var1) throws SQLException {
      try {
         return super.delegate.getObject(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(String var1) throws SQLException {
      try {
         return super.delegate.getObject(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int findColumn(String var1) throws SQLException {
      try {
         return super.delegate.findColumn(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      try {
         return super.delegate.getCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      try {
         return super.delegate.getCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      try {
         return super.delegate.getBigDecimal(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      try {
         return super.delegate.getBigDecimal(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean isBeforeFirst() throws SQLException {
      try {
         return super.delegate.isBeforeFirst();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isAfterLast() throws SQLException {
      try {
         return super.delegate.isAfterLast();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isFirst() throws SQLException {
      try {
         return super.delegate.isFirst();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isLast() throws SQLException {
      try {
         return super.delegate.isLast();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void beforeFirst() throws SQLException {
      try {
         super.delegate.beforeFirst();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void afterLast() throws SQLException {
      try {
         super.delegate.afterLast();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean first() throws SQLException {
      try {
         return super.delegate.first();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean last() throws SQLException {
      try {
         return super.delegate.last();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getRow() throws SQLException {
      try {
         return super.delegate.getRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean absolute(int var1) throws SQLException {
      try {
         return super.delegate.absolute(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean relative(int var1) throws SQLException {
      try {
         return super.delegate.relative(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean previous() throws SQLException {
      try {
         return super.delegate.previous();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      try {
         super.delegate.setFetchDirection(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         return super.delegate.getFetchDirection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         super.delegate.setFetchSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         return super.delegate.getFetchSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getType() throws SQLException {
      try {
         return super.delegate.getType();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getConcurrency() throws SQLException {
      try {
         return super.delegate.getConcurrency();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean rowUpdated() throws SQLException {
      try {
         return super.delegate.rowUpdated();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean rowInserted() throws SQLException {
      try {
         return super.delegate.rowInserted();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean rowDeleted() throws SQLException {
      try {
         return super.delegate.rowDeleted();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void updateNull(int var1) throws SQLException {
      try {
         super.delegate.updateNull(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      try {
         super.delegate.updateBoolean(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      try {
         super.delegate.updateByte(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateShort(int var1, short var2) throws SQLException {
      try {
         super.delegate.updateShort(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateInt(int var1, int var2) throws SQLException {
      try {
         super.delegate.updateInt(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateLong(int var1, long var2) throws SQLException {
      try {
         super.delegate.updateLong(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      try {
         super.delegate.updateFloat(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      try {
         super.delegate.updateDouble(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      try {
         super.delegate.updateBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateString(int var1, String var2) throws SQLException {
      try {
         super.delegate.updateString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      try {
         super.delegate.updateBytes(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      try {
         super.delegate.updateDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      try {
         super.delegate.updateTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      try {
         super.delegate.updateTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNull(String var1) throws SQLException {
      try {
         super.delegate.updateNull(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      try {
         super.delegate.updateBoolean(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      try {
         super.delegate.updateByte(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateShort(String var1, short var2) throws SQLException {
      try {
         super.delegate.updateShort(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateInt(String var1, int var2) throws SQLException {
      try {
         super.delegate.updateInt(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateLong(String var1, long var2) throws SQLException {
      try {
         super.delegate.updateLong(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      try {
         super.delegate.updateFloat(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      try {
         super.delegate.updateDouble(var1, var2);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      try {
         super.delegate.updateBigDecimal(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateString(String var1, String var2) throws SQLException {
      try {
         super.delegate.updateString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      try {
         super.delegate.updateBytes(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      try {
         super.delegate.updateDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      try {
         super.delegate.updateTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      try {
         super.delegate.updateTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void insertRow() throws SQLException {
      try {
         super.insertRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void updateRow() throws SQLException {
      try {
         super.updateRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void deleteRow() throws SQLException {
      try {
         super.deleteRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void refreshRow() throws SQLException {
      try {
         super.delegate.refreshRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void cancelRowUpdates() throws SQLException {
      try {
         super.delegate.cancelRowUpdates();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void moveToInsertRow() throws SQLException {
      try {
         super.delegate.moveToInsertRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void moveToCurrentRow() throws SQLException {
      try {
         super.delegate.moveToCurrentRow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Object getObject(int var1, Map var2) throws SQLException {
      try {
         return super.delegate.getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Ref getRef(int var1) throws SQLException {
      try {
         return super.delegate.getRef(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Blob getBlob(int var1) throws SQLException {
      try {
         return super.delegate.getBlob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Clob getClob(int var1) throws SQLException {
      try {
         return super.delegate.getClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Array getArray(int var1) throws SQLException {
      try {
         return super.delegate.getArray(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Object getObject(String var1, Map var2) throws SQLException {
      try {
         return super.delegate.getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Ref getRef(String var1) throws SQLException {
      try {
         return super.delegate.getRef(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Blob getBlob(String var1) throws SQLException {
      try {
         return super.delegate.getBlob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Clob getClob(String var1) throws SQLException {
      try {
         return super.delegate.getClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Array getArray(String var1) throws SQLException {
      try {
         return super.delegate.getArray(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getDate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getTime(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      try {
         return super.delegate.getTimestamp(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public URL getURL(int var1) throws SQLException {
      try {
         return super.delegate.getURL(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public URL getURL(String var1) throws SQLException {
      try {
         return super.delegate.getURL(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      try {
         super.delegate.updateRef(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      try {
         super.delegate.updateRef(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      try {
         super.delegate.updateArray(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      try {
         super.delegate.updateArray(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public RowId getRowId(int var1) throws SQLException {
      try {
         return super.delegate.getRowId(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public RowId getRowId(String var1) throws SQLException {
      try {
         return super.delegate.getRowId(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateRowId(int var1, RowId var2) throws SQLException {
      try {
         super.delegate.updateRowId(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateRowId(String var1, RowId var2) throws SQLException {
      try {
         super.delegate.updateRowId(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int getHoldability() throws SQLException {
      try {
         return super.delegate.getHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return super.delegate.isClosed();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void updateNString(int var1, String var2) throws SQLException {
      try {
         super.delegate.updateNString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNString(String var1, String var2) throws SQLException {
      try {
         super.delegate.updateNString(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNClob(int var1, NClob var2) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNClob(String var1, NClob var2) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public NClob getNClob(int var1) throws SQLException {
      try {
         return super.delegate.getNClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public NClob getNClob(String var1) throws SQLException {
      try {
         return super.delegate.getNClob(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      try {
         return super.delegate.getSQLXML(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      try {
         return super.delegate.getSQLXML(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateSQLXML(int var1, SQLXML var2) throws SQLException {
      try {
         super.delegate.updateSQLXML(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateSQLXML(String var1, SQLXML var2) throws SQLException {
      try {
         super.delegate.updateSQLXML(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public String getNString(int var1) throws SQLException {
      try {
         return super.delegate.getNString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getNString(String var1) throws SQLException {
      try {
         return super.delegate.getNString(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      try {
         return super.delegate.getNCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      try {
         return super.delegate.getNCharacterStream(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateBlob(int var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateBlob(String var1, InputStream var2, long var3) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateNClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateNClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2, var3);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateNCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateAsciiStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateBinaryStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateCharacterStream(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBlob(int var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateBlob(String var1, InputStream var2) throws SQLException {
      try {
         super.delegate.updateBlob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateClob(int var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateClob(String var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNClob(int var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateNClob(String var1, Reader var2) throws SQLException {
      try {
         super.delegate.updateNClob(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Object getObject(int var1, Class var2) throws SQLException {
      try {
         return super.delegate.getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Object getObject(String var1, Class var2) throws SQLException {
      try {
         return super.delegate.getObject(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void updateObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public void updateObject(int var1, Object var2, SQLType var3) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public void updateObject(String var1, Object var2, SQLType var3) throws SQLException {
      try {
         super.delegate.updateObject(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   protected HikariProxyResultSet(ProxyConnection var1, ProxyStatement var2, ResultSet var3) {
      super(var1, var2, var3);
   }
}
