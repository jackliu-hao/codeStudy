package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

public final class ValueDate extends Value {
   public static final int PRECISION = 10;
   private final long dateValue;

   private ValueDate(long var1) {
      if (var1 >= -511999999967L && var1 <= 512000000415L) {
         this.dateValue = var1;
      } else {
         throw new IllegalArgumentException("dateValue out of range " + var1);
      }
   }

   public static ValueDate fromDateValue(long var0) {
      return (ValueDate)Value.cache(new ValueDate(var0));
   }

   public static ValueDate parse(String var0) {
      try {
         return fromDateValue(DateTimeUtils.parseDateValue(var0, 0, var0.length()));
      } catch (Exception var2) {
         throw DbException.get(22007, var2, "DATE", var0);
      }
   }

   public long getDateValue() {
      return this.dateValue;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_DATE;
   }

   public int getValueType() {
      return 17;
   }

   public String getString() {
      return DateTimeUtils.appendDate(new StringBuilder(10), this.dateValue).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return DateTimeUtils.appendDate(var1.append("DATE '"), this.dateValue).append('\'');
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Long.compare(this.dateValue, ((ValueDate)var1).dateValue);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ValueDate && this.dateValue == ((ValueDate)var1).dateValue;
   }

   public int hashCode() {
      return (int)(this.dateValue ^ this.dateValue >>> 32);
   }
}
