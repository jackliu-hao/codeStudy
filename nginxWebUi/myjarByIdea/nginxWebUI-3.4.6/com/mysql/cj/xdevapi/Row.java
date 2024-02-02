package com.mysql.cj.xdevapi;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public interface Row {
   BigDecimal getBigDecimal(String var1);

   BigDecimal getBigDecimal(int var1);

   boolean getBoolean(String var1);

   boolean getBoolean(int var1);

   byte getByte(String var1);

   byte getByte(int var1);

   Date getDate(String var1);

   Date getDate(int var1);

   DbDoc getDbDoc(String var1);

   DbDoc getDbDoc(int var1);

   double getDouble(String var1);

   double getDouble(int var1);

   int getInt(String var1);

   int getInt(int var1);

   long getLong(String var1);

   long getLong(int var1);

   String getString(String var1);

   String getString(int var1);

   Time getTime(String var1);

   Time getTime(int var1);

   Timestamp getTimestamp(String var1);

   Timestamp getTimestamp(int var1);
}
