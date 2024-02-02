package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

public final class ValueTime extends Value {
   public static final int DEFAULT_PRECISION = 8;
   public static final int MAXIMUM_PRECISION = 18;
   public static final int DEFAULT_SCALE = 0;
   public static final int MAXIMUM_SCALE = 9;
   private final long nanos;

   private ValueTime(long var1) {
      this.nanos = var1;
   }

   public static ValueTime fromNanos(long var0) {
      if (var0 >= 0L && var0 < 86400000000000L) {
         return (ValueTime)Value.cache(new ValueTime(var0));
      } else {
         throw DbException.get(22007, (String[])("TIME", DateTimeUtils.appendTime(new StringBuilder(), var0).toString()));
      }
   }

   public static ValueTime parse(String var0) {
      try {
         return fromNanos(DateTimeUtils.parseTimeNanos(var0, 0, var0.length()));
      } catch (Exception var2) {
         throw DbException.get(22007, var2, "TIME", var0);
      }
   }

   public long getNanos() {
      return this.nanos;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_TIME;
   }

   public int getValueType() {
      return 18;
   }

   public String getString() {
      return DateTimeUtils.appendTime(new StringBuilder(18), this.nanos).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return DateTimeUtils.appendTime(var1.append("TIME '"), this.nanos).append('\'');
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Long.compare(this.nanos, ((ValueTime)var1).nanos);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ValueTime && this.nanos == ((ValueTime)var1).nanos;
   }

   public int hashCode() {
      return (int)(this.nanos ^ this.nanos >>> 32);
   }

   public Value add(Value var1) {
      ValueTime var2 = (ValueTime)var1;
      return fromNanos(this.nanos + var2.getNanos());
   }

   public Value subtract(Value var1) {
      ValueTime var2 = (ValueTime)var1;
      return fromNanos(this.nanos - var2.getNanos());
   }

   public Value multiply(Value var1) {
      return fromNanos((long)((double)this.nanos * var1.getDouble()));
   }

   public Value divide(Value var1, TypeInfo var2) {
      return fromNanos((long)((double)this.nanos / var1.getDouble()));
   }
}
