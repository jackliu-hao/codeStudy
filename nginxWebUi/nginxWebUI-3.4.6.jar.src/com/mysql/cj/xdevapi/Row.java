package com.mysql.cj.xdevapi;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public interface Row {
  BigDecimal getBigDecimal(String paramString);
  
  BigDecimal getBigDecimal(int paramInt);
  
  boolean getBoolean(String paramString);
  
  boolean getBoolean(int paramInt);
  
  byte getByte(String paramString);
  
  byte getByte(int paramInt);
  
  Date getDate(String paramString);
  
  Date getDate(int paramInt);
  
  DbDoc getDbDoc(String paramString);
  
  DbDoc getDbDoc(int paramInt);
  
  double getDouble(String paramString);
  
  double getDouble(int paramInt);
  
  int getInt(String paramString);
  
  int getInt(int paramInt);
  
  long getLong(String paramString);
  
  long getLong(int paramInt);
  
  String getString(String paramString);
  
  String getString(int paramInt);
  
  Time getTime(String paramString);
  
  Time getTime(int paramInt);
  
  Timestamp getTimestamp(String paramString);
  
  Timestamp getTimestamp(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Row.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */