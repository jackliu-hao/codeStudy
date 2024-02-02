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
  
  void setColumnDefinition(ColumnDefinition paramColumnDefinition);
  
  boolean isLoadDataQuery();
  
  void setLoadDataQuery(boolean paramBoolean);
  
  T[] getBindValues();
  
  void setBindValues(T[] paramArrayOfT);
  
  boolean clearBindValues();
  
  void checkParameterSet(int paramInt);
  
  void checkAllParametersSet();
  
  int getNumberOfExecutions();
  
  void setNumberOfExecutions(int paramInt);
  
  void setValue(int paramInt, byte[] paramArrayOfbyte, MysqlType paramMysqlType);
  
  void setValue(int paramInt, String paramString, MysqlType paramMysqlType);
  
  void setAsciiStream(int paramInt, InputStream paramInputStream);
  
  void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2);
  
  void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong);
  
  void setBigDecimal(int paramInt, BigDecimal paramBigDecimal);
  
  void setBigInteger(int paramInt, BigInteger paramBigInteger);
  
  void setBinaryStream(int paramInt, InputStream paramInputStream);
  
  void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2);
  
  void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong);
  
  void setBlob(int paramInt, Blob paramBlob);
  
  void setBlob(int paramInt, InputStream paramInputStream);
  
  void setBlob(int paramInt, InputStream paramInputStream, long paramLong);
  
  void setBoolean(int paramInt, boolean paramBoolean);
  
  void setByte(int paramInt, byte paramByte);
  
  void setBytes(int paramInt, byte[] paramArrayOfbyte);
  
  void setBytes(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean1, boolean paramBoolean2);
  
  void setBytesNoEscape(int paramInt, byte[] paramArrayOfbyte);
  
  void setBytesNoEscapeNoQuotes(int paramInt, byte[] paramArrayOfbyte);
  
  void setCharacterStream(int paramInt, Reader paramReader);
  
  void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2);
  
  void setCharacterStream(int paramInt, Reader paramReader, long paramLong);
  
  void setClob(int paramInt, Clob paramClob);
  
  void setClob(int paramInt, Reader paramReader);
  
  void setClob(int paramInt, Reader paramReader, long paramLong);
  
  void setDate(int paramInt, Date paramDate);
  
  void setDate(int paramInt, Date paramDate, Calendar paramCalendar);
  
  void setDouble(int paramInt, double paramDouble);
  
  void setFloat(int paramInt, float paramFloat);
  
  void setInt(int paramInt1, int paramInt2);
  
  void setLong(int paramInt, long paramLong);
  
  void setNCharacterStream(int paramInt, Reader paramReader);
  
  void setNCharacterStream(int paramInt, Reader paramReader, long paramLong);
  
  void setNClob(int paramInt, Reader paramReader);
  
  void setNClob(int paramInt, Reader paramReader, long paramLong);
  
  void setNClob(int paramInt, NClob paramNClob);
  
  void setNString(int paramInt, String paramString);
  
  void setNull(int paramInt);
  
  boolean isNull(int paramInt);
  
  void setObject(int paramInt, Object paramObject);
  
  void setObject(int paramInt, Object paramObject, MysqlType paramMysqlType);
  
  void setObject(int paramInt1, Object paramObject, MysqlType paramMysqlType, int paramInt2);
  
  void setShort(int paramInt, short paramShort);
  
  void setString(int paramInt, String paramString);
  
  void setTime(int paramInt, Time paramTime);
  
  void setTime(int paramInt, Time paramTime, Calendar paramCalendar);
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar, MysqlType paramMysqlType);
  
  void setTimestamp(int paramInt, Timestamp paramTimestamp, MysqlType paramMysqlType);
  
  void setTimestamp(int paramInt1, Timestamp paramTimestamp, Calendar paramCalendar, int paramInt2, MysqlType paramMysqlType);
  
  void bindTimestamp(int paramInt1, Timestamp paramTimestamp, Calendar paramCalendar, int paramInt2, MysqlType paramMysqlType);
  
  byte[] getBytesRepresentation(int paramInt);
  
  byte[] getOrigBytes(int paramInt);
  
  void setLocalDate(int paramInt, LocalDate paramLocalDate, MysqlType paramMysqlType);
  
  void setLocalTime(int paramInt, LocalTime paramLocalTime, MysqlType paramMysqlType);
  
  void setLocalDateTime(int paramInt, LocalDateTime paramLocalDateTime, MysqlType paramMysqlType);
  
  void setDuration(int paramInt, Duration paramDuration, MysqlType paramMysqlType);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\QueryBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */