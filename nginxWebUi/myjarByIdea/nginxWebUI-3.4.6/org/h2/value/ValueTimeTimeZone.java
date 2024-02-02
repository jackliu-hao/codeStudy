package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

public final class ValueTimeTimeZone extends Value {
   public static final int DEFAULT_PRECISION = 14;
   public static final int MAXIMUM_PRECISION = 24;
   private final long nanos;
   private final int timeZoneOffsetSeconds;

   private ValueTimeTimeZone(long var1, int var3) {
      this.nanos = var1;
      this.timeZoneOffsetSeconds = var3;
   }

   public static ValueTimeTimeZone fromNanos(long var0, int var2) {
      if (var0 >= 0L && var0 < 86400000000000L) {
         if (var2 >= -64800 && var2 <= 64800) {
            return (ValueTimeTimeZone)Value.cache(new ValueTimeTimeZone(var0, var2));
         } else {
            throw new IllegalArgumentException("timeZoneOffsetSeconds " + var2);
         }
      } else {
         throw DbException.get(22007, (String[])("TIME WITH TIME ZONE", DateTimeUtils.appendTime(new StringBuilder(), var0).toString()));
      }
   }

   public static ValueTimeTimeZone parse(String var0) {
      try {
         return DateTimeUtils.parseTimeWithTimeZone(var0, (CastDataProvider)null);
      } catch (Exception var2) {
         throw DbException.get(22007, var2, "TIME WITH TIME ZONE", var0);
      }
   }

   public long getNanos() {
      return this.nanos;
   }

   public int getTimeZoneOffsetSeconds() {
      return this.timeZoneOffsetSeconds;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_TIME_TZ;
   }

   public int getValueType() {
      return 19;
   }

   public int getMemory() {
      return 32;
   }

   public String getString() {
      return this.toString(new StringBuilder(24)).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.toString(var1.append("TIME WITH TIME ZONE '")).append('\'');
   }

   private StringBuilder toString(StringBuilder var1) {
      return DateTimeUtils.appendTimeZone(DateTimeUtils.appendTime(var1, this.nanos), this.timeZoneOffsetSeconds);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueTimeTimeZone var4 = (ValueTimeTimeZone)var1;
      return Long.compare(this.nanos - (long)this.timeZoneOffsetSeconds * 1000000000L, var4.nanos - (long)var4.timeZoneOffsetSeconds * 1000000000L);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ValueTimeTimeZone)) {
         return false;
      } else {
         ValueTimeTimeZone var2 = (ValueTimeTimeZone)var1;
         return this.nanos == var2.nanos && this.timeZoneOffsetSeconds == var2.timeZoneOffsetSeconds;
      }
   }

   public int hashCode() {
      return (int)(this.nanos ^ this.nanos >>> 32 ^ (long)this.timeZoneOffsetSeconds);
   }
}
