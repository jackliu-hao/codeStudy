package com.mysql.cj.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public interface ParameterBindings {
  Array getArray(int paramInt) throws SQLException;
  
  InputStream getAsciiStream(int paramInt) throws SQLException;
  
  BigDecimal getBigDecimal(int paramInt) throws SQLException;
  
  InputStream getBinaryStream(int paramInt) throws SQLException;
  
  Blob getBlob(int paramInt) throws SQLException;
  
  boolean getBoolean(int paramInt) throws SQLException;
  
  byte getByte(int paramInt) throws SQLException;
  
  byte[] getBytes(int paramInt) throws SQLException;
  
  Reader getCharacterStream(int paramInt) throws SQLException;
  
  Clob getClob(int paramInt) throws SQLException;
  
  Date getDate(int paramInt) throws SQLException;
  
  double getDouble(int paramInt) throws SQLException;
  
  float getFloat(int paramInt) throws SQLException;
  
  int getInt(int paramInt) throws SQLException;
  
  BigInteger getBigInteger(int paramInt) throws SQLException;
  
  long getLong(int paramInt) throws SQLException;
  
  Reader getNCharacterStream(int paramInt) throws SQLException;
  
  Reader getNClob(int paramInt) throws SQLException;
  
  Object getObject(int paramInt) throws SQLException;
  
  Ref getRef(int paramInt) throws SQLException;
  
  short getShort(int paramInt) throws SQLException;
  
  String getString(int paramInt) throws SQLException;
  
  Time getTime(int paramInt) throws SQLException;
  
  Timestamp getTimestamp(int paramInt) throws SQLException;
  
  URL getURL(int paramInt) throws SQLException;
  
  boolean isNull(int paramInt) throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ParameterBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */