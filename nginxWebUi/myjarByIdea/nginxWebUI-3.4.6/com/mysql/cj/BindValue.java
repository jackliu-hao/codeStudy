package com.mysql.cj;

import java.io.InputStream;

public interface BindValue {
   BindValue clone();

   void reset();

   boolean isNull();

   void setNull(boolean var1);

   boolean isStream();

   void setIsStream(boolean var1);

   MysqlType getMysqlType();

   void setMysqlType(MysqlType var1);

   byte[] getByteValue();

   void setByteValue(byte[] var1);

   void setOrigByteValue(byte[] var1);

   byte[] getOrigByteValue();

   InputStream getStreamValue();

   void setStreamValue(InputStream var1, long var2);

   long getStreamLength();

   boolean isSet();
}
