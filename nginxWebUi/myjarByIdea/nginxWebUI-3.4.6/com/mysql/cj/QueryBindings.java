package com.mysql.cj;

import com.mysql.cj.protocol.ColumnDefinition;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

public interface QueryBindings<T extends BindValue> {
   QueryBindings<T> clone();

   void setColumnDefinition(ColumnDefinition var1);

   boolean isLoadDataQuery();

   void setLoadDataQuery(boolean var1);

   T[] getBindValues();

   void setBindValues(T[] var1);

   boolean clearBindValues();

   void checkParameterSet(int var1);

   void checkAllParametersSet();

   int getNumberOfExecutions();

   void setNumberOfExecutions(int var1);

   void setValue(int var1, byte[] var2, MysqlType var3);

   void setValue(int var1, String var2, MysqlType var3);

   void setAsciiStream(int var1, InputStream var2);

   void setAsciiStream(int var1, InputStream var2, int var3);

   void setAsciiStream(int var1, InputStream var2, long var3);

   void setBigDecimal(int var1, BigDecimal var2);

   void setBigInteger(int var1, BigInteger var2);

   void setBinaryStream(int var1, InputStream var2);

   void setBinaryStream(int var1, InputStream var2, int var3);

   void setBinaryStream(int var1, InputStream var2, long var3);

   void setBlob(int var1, Blob var2);

   void setBlob(int var1, InputStream var2);

   void setBlob(int var1, InputStream var2, long var3);

   void setBoolean(int var1, boolean var2);

   void setByte(int var1, byte var2);

   void setBytes(int var1, byte[] var2);

   void setBytes(int var1, byte[] var2, boolean var3, boolean var4);

   void setBytesNoEscape(int var1, byte[] var2);

   void setBytesNoEscapeNoQuotes(int var1, byte[] var2);

   void setCharacterStream(int var1, Reader var2);

   void setCharacterStream(int var1, Reader var2, int var3);

   void setCharacterStream(int var1, Reader var2, long var3);

   void setClob(int var1, Clob var2);

   void setClob(int var1, Reader var2);

   void setClob(int var1, Reader var2, long var3);

   void setDate(int var1, Date var2);

   void setDate(int var1, Date var2, Calendar var3);

   void setDouble(int var1, double var2);

   void setFloat(int var1, float var2);

   void setInt(int var1, int var2);

   void setLong(int var1, long var2);

   void setNCharacterStream(int var1, Reader var2);

   void setNCharacterStream(int var1, Reader var2, long var3);

   void setNClob(int var1, Reader var2);

   void setNClob(int var1, Reader var2, long var3);

   void setNClob(int var1, NClob var2);

   void setNString(int var1, String var2);

   void setNull(int var1);

   boolean isNull(int var1);

   void setObject(int var1, Object var2);

   void setObject(int var1, Object var2, MysqlType var3);

   void setObject(int var1, Object var2, MysqlType var3, int var4);

   void setShort(int var1, short var2);

   void setString(int var1, String var2);

   void setTime(int var1, Time var2);

   void setTime(int var1, Time var2, Calendar var3);

   void setTimestamp(int var1, Timestamp var2, Calendar var3, MysqlType var4);

   void setTimestamp(int var1, Timestamp var2, MysqlType var3);

   void setTimestamp(int var1, Timestamp var2, Calendar var3, int var4, MysqlType var5);

   void bindTimestamp(int var1, Timestamp var2, Calendar var3, int var4, MysqlType var5);

   byte[] getBytesRepresentation(int var1);

   byte[] getOrigBytes(int var1);

   void setLocalDate(int var1, LocalDate var2, MysqlType var3);

   void setLocalTime(int var1, LocalTime var2, MysqlType var3);

   void setLocalDateTime(int var1, LocalDateTime var2, MysqlType var3);

   void setDuration(int var1, Duration var2, MysqlType var3);
}
