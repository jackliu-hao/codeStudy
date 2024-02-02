package com.mysql.cj.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public interface ParameterBindings {
   Array getArray(int var1) throws SQLException;

   InputStream getAsciiStream(int var1) throws SQLException;

   BigDecimal getBigDecimal(int var1) throws SQLException;

   InputStream getBinaryStream(int var1) throws SQLException;

   java.sql.Blob getBlob(int var1) throws SQLException;

   boolean getBoolean(int var1) throws SQLException;

   byte getByte(int var1) throws SQLException;

   byte[] getBytes(int var1) throws SQLException;

   Reader getCharacterStream(int var1) throws SQLException;

   java.sql.Clob getClob(int var1) throws SQLException;

   Date getDate(int var1) throws SQLException;

   double getDouble(int var1) throws SQLException;

   float getFloat(int var1) throws SQLException;

   int getInt(int var1) throws SQLException;

   BigInteger getBigInteger(int var1) throws SQLException;

   long getLong(int var1) throws SQLException;

   Reader getNCharacterStream(int var1) throws SQLException;

   Reader getNClob(int var1) throws SQLException;

   Object getObject(int var1) throws SQLException;

   Ref getRef(int var1) throws SQLException;

   short getShort(int var1) throws SQLException;

   String getString(int var1) throws SQLException;

   Time getTime(int var1) throws SQLException;

   Timestamp getTimestamp(int var1) throws SQLException;

   URL getURL(int var1) throws SQLException;

   boolean isNull(int var1) throws SQLException;
}
