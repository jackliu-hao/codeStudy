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
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
    try {
      return this.delegate.isWrapperFor(paramClass);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void close() throws Exception {
    this.delegate.close();
  }
  
  public boolean next() throws SQLException {
    try {
      return this.delegate.next();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean wasNull() throws SQLException {
    try {
      return this.delegate.wasNull();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getString(int paramInt) throws SQLException {
    try {
      return this.delegate.getString(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getBoolean(int paramInt) throws SQLException {
    try {
      return this.delegate.getBoolean(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public byte getByte(int paramInt) throws SQLException {
    try {
      return this.delegate.getByte(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public short getShort(int paramInt) throws SQLException {
    try {
      return this.delegate.getShort(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getInt(int paramInt) throws SQLException {
    try {
      return this.delegate.getInt(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLong(int paramInt) throws SQLException {
    try {
      return this.delegate.getLong(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public float getFloat(int paramInt) throws SQLException {
    try {
      return this.delegate.getFloat(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public double getDouble(int paramInt) throws SQLException {
    try {
      return this.delegate.getDouble(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
    try {
      return this.delegate.getBigDecimal(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public byte[] getBytes(int paramInt) throws SQLException {
    try {
      return this.delegate.getBytes(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Date getDate(int paramInt) throws SQLException {
    try {
      return this.delegate.getDate(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Time getTime(int paramInt) throws SQLException {
    try {
      return this.delegate.getTime(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Timestamp getTimestamp(int paramInt) throws SQLException {
    try {
      return this.delegate.getTimestamp(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getAsciiStream(int paramInt) throws SQLException {
    try {
      return this.delegate.getAsciiStream(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getUnicodeStream(int paramInt) throws SQLException {
    try {
      return this.delegate.getUnicodeStream(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getBinaryStream(int paramInt) throws SQLException {
    try {
      return this.delegate.getBinaryStream(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getString(String paramString) throws SQLException {
    try {
      return this.delegate.getString(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean getBoolean(String paramString) throws SQLException {
    try {
      return this.delegate.getBoolean(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public byte getByte(String paramString) throws SQLException {
    try {
      return this.delegate.getByte(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public short getShort(String paramString) throws SQLException {
    try {
      return this.delegate.getShort(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getInt(String paramString) throws SQLException {
    try {
      return this.delegate.getInt(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getLong(String paramString) throws SQLException {
    try {
      return this.delegate.getLong(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public float getFloat(String paramString) throws SQLException {
    try {
      return this.delegate.getFloat(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public double getDouble(String paramString) throws SQLException {
    try {
      return this.delegate.getDouble(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException {
    try {
      return this.delegate.getBigDecimal(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public byte[] getBytes(String paramString) throws SQLException {
    try {
      return this.delegate.getBytes(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Date getDate(String paramString) throws SQLException {
    try {
      return this.delegate.getDate(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Time getTime(String paramString) throws SQLException {
    try {
      return this.delegate.getTime(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Timestamp getTimestamp(String paramString) throws SQLException {
    try {
      return this.delegate.getTimestamp(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getAsciiStream(String paramString) throws SQLException {
    try {
      return this.delegate.getAsciiStream(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getUnicodeStream(String paramString) throws SQLException {
    try {
      return this.delegate.getUnicodeStream(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public InputStream getBinaryStream(String paramString) throws SQLException {
    try {
      return this.delegate.getBinaryStream(paramString);
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
  
  public String getCursorName() throws SQLException {
    try {
      return this.delegate.getCursorName();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    try {
      return this.delegate.getMetaData();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(int paramInt) throws SQLException {
    try {
      return this.delegate.getObject(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(String paramString) throws SQLException {
    try {
      return this.delegate.getObject(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int findColumn(String paramString) throws SQLException {
    try {
      return this.delegate.findColumn(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Reader getCharacterStream(int paramInt) throws SQLException {
    try {
      return this.delegate.getCharacterStream(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Reader getCharacterStream(String paramString) throws SQLException {
    try {
      return this.delegate.getCharacterStream(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public BigDecimal getBigDecimal(int paramInt) throws SQLException {
    try {
      return this.delegate.getBigDecimal(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public BigDecimal getBigDecimal(String paramString) throws SQLException {
    try {
      return this.delegate.getBigDecimal(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isBeforeFirst() throws SQLException {
    try {
      return this.delegate.isBeforeFirst();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isAfterLast() throws SQLException {
    try {
      return this.delegate.isAfterLast();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isFirst() throws SQLException {
    try {
      return this.delegate.isFirst();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isLast() throws SQLException {
    try {
      return this.delegate.isLast();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void beforeFirst() throws SQLException {
    try {
      this.delegate.beforeFirst();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void afterLast() throws SQLException {
    try {
      this.delegate.afterLast();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean first() throws SQLException {
    try {
      return this.delegate.first();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean last() throws SQLException {
    try {
      return this.delegate.last();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getRow() throws SQLException {
    try {
      return this.delegate.getRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean absolute(int paramInt) throws SQLException {
    try {
      return this.delegate.absolute(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean relative(int paramInt) throws SQLException {
    try {
      return this.delegate.relative(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean previous() throws SQLException {
    try {
      return this.delegate.previous();
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
  
  public int getType() throws SQLException {
    try {
      return this.delegate.getType();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getConcurrency() throws SQLException {
    try {
      return this.delegate.getConcurrency();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean rowUpdated() throws SQLException {
    try {
      return this.delegate.rowUpdated();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean rowInserted() throws SQLException {
    try {
      return this.delegate.rowInserted();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean rowDeleted() throws SQLException {
    try {
      return this.delegate.rowDeleted();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNull(int paramInt) throws SQLException {
    try {
      this.delegate.updateNull(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
    try {
      this.delegate.updateBoolean(paramInt, paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateByte(int paramInt, byte paramByte) throws SQLException {
    try {
      this.delegate.updateByte(paramInt, paramByte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateShort(int paramInt, short paramShort) throws SQLException {
    try {
      this.delegate.updateShort(paramInt, paramShort);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateInt(int paramInt1, int paramInt2) throws SQLException {
    try {
      this.delegate.updateInt(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateLong(int paramInt, long paramLong) throws SQLException {
    try {
      this.delegate.updateLong(paramInt, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateFloat(int paramInt, float paramFloat) throws SQLException {
    try {
      this.delegate.updateFloat(paramInt, paramFloat);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateDouble(int paramInt, double paramDouble) throws SQLException {
    try {
      this.delegate.updateDouble(paramInt, paramDouble);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
    try {
      this.delegate.updateBigDecimal(paramInt, paramBigDecimal);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateString(int paramInt, String paramString) throws SQLException {
    try {
      this.delegate.updateString(paramInt, paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfbyte) throws SQLException {
    try {
      this.delegate.updateBytes(paramInt, paramArrayOfbyte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateDate(int paramInt, Date paramDate) throws SQLException {
    try {
      this.delegate.updateDate(paramInt, paramDate);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateTime(int paramInt, Time paramTime) throws SQLException {
    try {
      this.delegate.updateTime(paramInt, paramTime);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
    try {
      this.delegate.updateTimestamp(paramInt, paramTimestamp);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramInt1, paramReader, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
    try {
      this.delegate.updateObject(paramInt1, paramObject, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(int paramInt, Object paramObject) throws SQLException {
    try {
      this.delegate.updateObject(paramInt, paramObject);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNull(String paramString) throws SQLException {
    try {
      this.delegate.updateNull(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException {
    try {
      this.delegate.updateBoolean(paramString, paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateByte(String paramString, byte paramByte) throws SQLException {
    try {
      this.delegate.updateByte(paramString, paramByte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateShort(String paramString, short paramShort) throws SQLException {
    try {
      this.delegate.updateShort(paramString, paramShort);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateInt(String paramString, int paramInt) throws SQLException {
    try {
      this.delegate.updateInt(paramString, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateLong(String paramString, long paramLong) throws SQLException {
    try {
      this.delegate.updateLong(paramString, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateFloat(String paramString, float paramFloat) throws SQLException {
    try {
      this.delegate.updateFloat(paramString, paramFloat);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateDouble(String paramString, double paramDouble) throws SQLException {
    try {
      this.delegate.updateDouble(paramString, paramDouble);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException {
    try {
      this.delegate.updateBigDecimal(paramString, paramBigDecimal);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateString(String paramString1, String paramString2) throws SQLException {
    try {
      this.delegate.updateString(paramString1, paramString2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBytes(String paramString, byte[] paramArrayOfbyte) throws SQLException {
    try {
      this.delegate.updateBytes(paramString, paramArrayOfbyte);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateDate(String paramString, Date paramDate) throws SQLException {
    try {
      this.delegate.updateDate(paramString, paramDate);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateTime(String paramString, Time paramTime) throws SQLException {
    try {
      this.delegate.updateTime(paramString, paramTime);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException {
    try {
      this.delegate.updateTimestamp(paramString, paramTimestamp);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramString, paramInputStream, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramString, paramInputStream, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramString, paramReader, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException {
    try {
      this.delegate.updateObject(paramString, paramObject, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(String paramString, Object paramObject) throws SQLException {
    try {
      this.delegate.updateObject(paramString, paramObject);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void insertRow() throws SQLException {
    try {
      super.insertRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateRow() throws SQLException {
    try {
      super.updateRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void deleteRow() throws SQLException {
    try {
      super.deleteRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void refreshRow() throws SQLException {
    try {
      this.delegate.refreshRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void cancelRowUpdates() throws SQLException {
    try {
      this.delegate.cancelRowUpdates();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void moveToInsertRow() throws SQLException {
    try {
      this.delegate.moveToInsertRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void moveToCurrentRow() throws SQLException {
    try {
      this.delegate.moveToCurrentRow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
    try {
      return this.delegate.getObject(paramInt, paramMap);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Ref getRef(int paramInt) throws SQLException {
    try {
      return this.delegate.getRef(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Blob getBlob(int paramInt) throws SQLException {
    try {
      return this.delegate.getBlob(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Clob getClob(int paramInt) throws SQLException {
    try {
      return this.delegate.getClob(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Array getArray(int paramInt) throws SQLException {
    try {
      return this.delegate.getArray(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException {
    try {
      return this.delegate.getObject(paramString, paramMap);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Ref getRef(String paramString) throws SQLException {
    try {
      return this.delegate.getRef(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Blob getBlob(String paramString) throws SQLException {
    try {
      return this.delegate.getBlob(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Clob getClob(String paramString) throws SQLException {
    try {
      return this.delegate.getClob(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Array getArray(String paramString) throws SQLException {
    try {
      return this.delegate.getArray(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getDate(paramInt, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Date getDate(String paramString, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getDate(paramString, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getTime(paramInt, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Time getTime(String paramString, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getTime(paramString, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getTimestamp(paramInt, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException {
    try {
      return this.delegate.getTimestamp(paramString, paramCalendar);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public URL getURL(int paramInt) throws SQLException {
    try {
      return this.delegate.getURL(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public URL getURL(String paramString) throws SQLException {
    try {
      return this.delegate.getURL(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateRef(int paramInt, Ref paramRef) throws SQLException {
    try {
      this.delegate.updateRef(paramInt, paramRef);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateRef(String paramString, Ref paramRef) throws SQLException {
    try {
      this.delegate.updateRef(paramString, paramRef);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
    try {
      this.delegate.updateBlob(paramInt, paramBlob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(String paramString, Blob paramBlob) throws SQLException {
    try {
      this.delegate.updateBlob(paramString, paramBlob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(int paramInt, Clob paramClob) throws SQLException {
    try {
      this.delegate.updateClob(paramInt, paramClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(String paramString, Clob paramClob) throws SQLException {
    try {
      this.delegate.updateClob(paramString, paramClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateArray(int paramInt, Array paramArray) throws SQLException {
    try {
      this.delegate.updateArray(paramInt, paramArray);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateArray(String paramString, Array paramArray) throws SQLException {
    try {
      this.delegate.updateArray(paramString, paramArray);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public RowId getRowId(int paramInt) throws SQLException {
    try {
      return this.delegate.getRowId(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public RowId getRowId(String paramString) throws SQLException {
    try {
      return this.delegate.getRowId(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateRowId(int paramInt, RowId paramRowId) throws SQLException {
    try {
      this.delegate.updateRowId(paramInt, paramRowId);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateRowId(String paramString, RowId paramRowId) throws SQLException {
    try {
      this.delegate.updateRowId(paramString, paramRowId);
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
  
  public boolean isClosed() throws SQLException {
    try {
      return this.delegate.isClosed();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNString(int paramInt, String paramString) throws SQLException {
    try {
      this.delegate.updateNString(paramInt, paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNString(String paramString1, String paramString2) throws SQLException {
    try {
      this.delegate.updateNString(paramString1, paramString2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(int paramInt, NClob paramNClob) throws SQLException {
    try {
      this.delegate.updateNClob(paramInt, paramNClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(String paramString, NClob paramNClob) throws SQLException {
    try {
      this.delegate.updateNClob(paramString, paramNClob);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public NClob getNClob(int paramInt) throws SQLException {
    try {
      return this.delegate.getNClob(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public NClob getNClob(String paramString) throws SQLException {
    try {
      return this.delegate.getNClob(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public SQLXML getSQLXML(int paramInt) throws SQLException {
    try {
      return this.delegate.getSQLXML(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public SQLXML getSQLXML(String paramString) throws SQLException {
    try {
      return this.delegate.getSQLXML(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
    try {
      this.delegate.updateSQLXML(paramInt, paramSQLXML);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException {
    try {
      this.delegate.updateSQLXML(paramString, paramSQLXML);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getNString(int paramInt) throws SQLException {
    try {
      return this.delegate.getNString(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getNString(String paramString) throws SQLException {
    try {
      return this.delegate.getNString(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Reader getNCharacterStream(int paramInt) throws SQLException {
    try {
      return this.delegate.getNCharacterStream(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Reader getNCharacterStream(String paramString) throws SQLException {
    try {
      return this.delegate.getNCharacterStream(paramString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateNCharacterStream(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateNCharacterStream(paramString, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramString, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramString, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramString, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateBlob(paramInt, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
    try {
      this.delegate.updateBlob(paramString, paramInputStream, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateClob(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateClob(paramString, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateNClob(paramInt, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
    try {
      this.delegate.updateNClob(paramString, paramReader, paramLong);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateNCharacterStream(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateNCharacterStream(paramString, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateAsciiStream(paramString, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateBinaryStream(paramString, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateCharacterStream(paramString, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateBlob(paramInt, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException {
    try {
      this.delegate.updateBlob(paramString, paramInputStream);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(int paramInt, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateClob(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateClob(String paramString, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateClob(paramString, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(int paramInt, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateNClob(paramInt, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateNClob(String paramString, Reader paramReader) throws SQLException {
    try {
      this.delegate.updateNClob(paramString, paramReader);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(int paramInt, Class<?> paramClass) throws SQLException {
    try {
      return this.delegate.getObject(paramInt, paramClass);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public Object getObject(String paramString, Class<?> paramClass) throws SQLException {
    try {
      return this.delegate.getObject(paramString, paramClass);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException {
    try {
      this.delegate.updateObject(paramInt1, paramObject, paramSQLType, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(String paramString, Object paramObject, SQLType paramSQLType, int paramInt) throws SQLException {
    try {
      this.delegate.updateObject(paramString, paramObject, paramSQLType, paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException {
    try {
      this.delegate.updateObject(paramInt, paramObject, paramSQLType);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public void updateObject(String paramString, Object paramObject, SQLType paramSQLType) throws SQLException {
    try {
      this.delegate.updateObject(paramString, paramObject, paramSQLType);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  protected HikariProxyResultSet(ProxyConnection paramProxyConnection, ProxyStatement paramProxyStatement, ResultSet paramResultSet) {
    super(paramProxyConnection, paramProxyStatement, paramResultSet);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariProxyResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */