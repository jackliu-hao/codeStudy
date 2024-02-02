package org.h2.value;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueNull extends Value {
   public static final ValueNull INSTANCE = new ValueNull();
   static final int PRECISION = 1;
   static final int DISPLAY_SIZE = 4;

   private ValueNull() {
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append("NULL");
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_NULL;
   }

   public int getValueType() {
      return 0;
   }

   public int getMemory() {
      return 0;
   }

   public String getString() {
      return null;
   }

   public Reader getReader() {
      return null;
   }

   public Reader getReader(long var1, long var3) {
      return null;
   }

   public byte[] getBytes() {
      return null;
   }

   public InputStream getInputStream() {
      return null;
   }

   public InputStream getInputStream(long var1, long var3) {
      return null;
   }

   public boolean getBoolean() {
      throw DbException.getInternalError();
   }

   public byte getByte() {
      throw DbException.getInternalError();
   }

   public short getShort() {
      throw DbException.getInternalError();
   }

   public int getInt() {
      throw DbException.getInternalError();
   }

   public long getLong() {
      throw DbException.getInternalError();
   }

   public BigDecimal getBigDecimal() {
      return null;
   }

   public float getFloat() {
      throw DbException.getInternalError();
   }

   public double getDouble() {
      throw DbException.getInternalError();
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      throw DbException.getInternalError("compare null");
   }

   public boolean containsNull() {
      return true;
   }

   public int hashCode() {
      return 0;
   }

   public boolean equals(Object var1) {
      return var1 == this;
   }
}
