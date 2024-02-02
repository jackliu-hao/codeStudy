package com.mysql.cj;

import java.io.InputStream;

public class ClientPreparedQueryBindValue implements BindValue {
   protected boolean isNull;
   protected boolean isStream = false;
   protected MysqlType parameterType;
   public Object value;
   public Object origValue;
   protected long streamLength;
   protected boolean isSet;

   public ClientPreparedQueryBindValue() {
      this.parameterType = MysqlType.NULL;
      this.isSet = false;
   }

   public ClientPreparedQueryBindValue clone() {
      return new ClientPreparedQueryBindValue(this);
   }

   protected ClientPreparedQueryBindValue(ClientPreparedQueryBindValue copyMe) {
      this.parameterType = MysqlType.NULL;
      this.isSet = false;
      this.isNull = copyMe.isNull;
      this.isStream = copyMe.isStream;
      this.parameterType = copyMe.parameterType;
      if (copyMe.value != null && copyMe.value instanceof byte[]) {
         this.value = new byte[((byte[])((byte[])copyMe.value)).length];
         System.arraycopy(copyMe.value, 0, this.value, 0, ((byte[])((byte[])copyMe.value)).length);
      } else {
         this.value = copyMe.value;
      }

      this.streamLength = copyMe.streamLength;
      this.isSet = copyMe.isSet;
   }

   public void reset() {
      this.isNull = false;
      this.isStream = false;
      this.parameterType = MysqlType.NULL;
      this.value = null;
      this.origValue = null;
      this.streamLength = 0L;
      this.isSet = false;
   }

   public boolean isNull() {
      return this.isNull;
   }

   public void setNull(boolean isNull) {
      this.isNull = isNull;
      if (isNull) {
         this.parameterType = MysqlType.NULL;
      }

      this.isSet = true;
   }

   public boolean isStream() {
      return this.isStream;
   }

   public void setIsStream(boolean isStream) {
      this.isStream = isStream;
   }

   public MysqlType getMysqlType() {
      return this.parameterType;
   }

   public void setMysqlType(MysqlType type) {
      this.parameterType = type;
   }

   public byte[] getByteValue() {
      return this.value instanceof byte[] ? (byte[])((byte[])this.value) : null;
   }

   public void setByteValue(byte[] parameterValue) {
      this.isNull = false;
      this.isStream = false;
      this.value = parameterValue;
      this.streamLength = 0L;
      this.isSet = true;
   }

   public void setOrigByteValue(byte[] origParamValue) {
      this.origValue = origParamValue;
   }

   public byte[] getOrigByteValue() {
      return (byte[])((byte[])this.origValue);
   }

   public InputStream getStreamValue() {
      return this.value instanceof InputStream ? (InputStream)this.value : null;
   }

   public void setStreamValue(InputStream parameterStream, long streamLength) {
      this.value = parameterStream;
      this.streamLength = streamLength;
      this.isSet = true;
   }

   public long getStreamLength() {
      return this.streamLength;
   }

   public boolean isSet() {
      return this.isSet;
   }
}
