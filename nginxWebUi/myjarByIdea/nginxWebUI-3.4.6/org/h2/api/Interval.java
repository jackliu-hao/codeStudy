package org.h2.api;

import org.h2.message.DbException;
import org.h2.util.IntervalUtils;

public final class Interval {
   private final IntervalQualifier qualifier;
   private final boolean negative;
   private final long leading;
   private final long remaining;

   public static Interval ofYears(long var0) {
      return new Interval(IntervalQualifier.YEAR, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofMonths(long var0) {
      return new Interval(IntervalQualifier.MONTH, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofDays(long var0) {
      return new Interval(IntervalQualifier.DAY, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofHours(long var0) {
      return new Interval(IntervalQualifier.HOUR, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofMinutes(long var0) {
      return new Interval(IntervalQualifier.MINUTE, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofSeconds(long var0) {
      return new Interval(IntervalQualifier.SECOND, var0 < 0L, Math.abs(var0), 0L);
   }

   public static Interval ofSeconds(long var0, int var2) {
      boolean var3 = (var0 | (long)var2) < 0L;
      if (var3) {
         if (var0 > 0L || var2 > 0) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
      }

      return new Interval(IntervalQualifier.SECOND, var3, var0, (long)var2);
   }

   public static Interval ofNanos(long var0) {
      boolean var2 = var0 < 0L;
      if (var2) {
         var0 = -var0;
         if (var0 < 0L) {
            return new Interval(IntervalQualifier.SECOND, true, 9223372036L, 854775808L);
         }
      }

      return new Interval(IntervalQualifier.SECOND, var2, var0 / 1000000000L, var0 % 1000000000L);
   }

   public static Interval ofYearsMonths(long var0, int var2) {
      boolean var3 = (var0 | (long)var2) < 0L;
      if (var3) {
         if (var0 > 0L || var2 > 0) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
      }

      return new Interval(IntervalQualifier.YEAR_TO_MONTH, var3, var0, (long)var2);
   }

   public static Interval ofDaysHours(long var0, int var2) {
      boolean var3 = (var0 | (long)var2) < 0L;
      if (var3) {
         if (var0 > 0L || var2 > 0) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
      }

      return new Interval(IntervalQualifier.DAY_TO_HOUR, var3, var0, (long)var2);
   }

   public static Interval ofDaysHoursMinutes(long var0, int var2, int var3) {
      boolean var4 = (var0 | (long)var2 | (long)var3) < 0L;
      if (var4) {
         if (var0 > 0L || var2 > 0 || var3 > 0) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
         var3 = -var3;
         if ((var2 | var3) < 0) {
            throw new IllegalArgumentException();
         }
      }

      if (var3 >= 60) {
         throw new IllegalArgumentException();
      } else {
         return new Interval(IntervalQualifier.DAY_TO_MINUTE, var4, var0, (long)var2 * 60L + (long)var3);
      }
   }

   public static Interval ofDaysHoursMinutesSeconds(long var0, int var2, int var3, int var4) {
      return ofDaysHoursMinutesNanos(var0, var2, var3, (long)var4 * 1000000000L);
   }

   public static Interval ofDaysHoursMinutesNanos(long var0, int var2, int var3, long var4) {
      boolean var6 = (var0 | (long)var2 | (long)var3 | var4) < 0L;
      if (var6) {
         if (var0 > 0L || var2 > 0 || var3 > 0 || var4 > 0L) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
         var3 = -var3;
         var4 = -var4;
         if (((long)(var2 | var3) | var4) < 0L) {
            throw new IllegalArgumentException();
         }
      }

      if (var3 < 60 && var4 < 60000000000L) {
         return new Interval(IntervalQualifier.DAY_TO_SECOND, var6, var0, ((long)var2 * 60L + (long)var3) * 60000000000L + var4);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Interval ofHoursMinutes(long var0, int var2) {
      boolean var3 = (var0 | (long)var2) < 0L;
      if (var3) {
         if (var0 > 0L || var2 > 0) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
      }

      return new Interval(IntervalQualifier.HOUR_TO_MINUTE, var3, var0, (long)var2);
   }

   public static Interval ofHoursMinutesSeconds(long var0, int var2, int var3) {
      return ofHoursMinutesNanos(var0, var2, (long)var3 * 1000000000L);
   }

   public static Interval ofHoursMinutesNanos(long var0, int var2, long var3) {
      boolean var5 = (var0 | (long)var2 | var3) < 0L;
      if (var5) {
         if (var0 > 0L || var2 > 0 || var3 > 0L) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
         var3 = -var3;
         if (((long)var2 | var3) < 0L) {
            throw new IllegalArgumentException();
         }
      }

      if (var3 >= 60000000000L) {
         throw new IllegalArgumentException();
      } else {
         return new Interval(IntervalQualifier.HOUR_TO_SECOND, var5, var0, (long)var2 * 60000000000L + var3);
      }
   }

   public static Interval ofMinutesSeconds(long var0, int var2) {
      return ofMinutesNanos(var0, (long)var2 * 1000000000L);
   }

   public static Interval ofMinutesNanos(long var0, long var2) {
      boolean var4 = (var0 | var2) < 0L;
      if (var4) {
         if (var0 > 0L || var2 > 0L) {
            throw new IllegalArgumentException();
         }

         var0 = -var0;
         var2 = -var2;
      }

      return new Interval(IntervalQualifier.MINUTE_TO_SECOND, var4, var0, var2);
   }

   public Interval(IntervalQualifier var1, boolean var2, long var3, long var5) {
      this.qualifier = var1;

      try {
         this.negative = IntervalUtils.validateInterval(var1, var2, var3, var5);
      } catch (DbException var8) {
         throw new IllegalArgumentException();
      }

      this.leading = var3;
      this.remaining = var5;
   }

   public IntervalQualifier getQualifier() {
      return this.qualifier;
   }

   public boolean isNegative() {
      return this.negative;
   }

   public long getLeading() {
      return this.leading;
   }

   public long getRemaining() {
      return this.remaining;
   }

   public long getYears() {
      return IntervalUtils.yearsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public long getMonths() {
      return IntervalUtils.monthsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public long getDays() {
      return IntervalUtils.daysFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public long getHours() {
      return IntervalUtils.hoursFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public long getMinutes() {
      return IntervalUtils.minutesFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public long getSeconds() {
      if (this.qualifier == IntervalQualifier.SECOND) {
         return this.negative ? -this.leading : this.leading;
      } else {
         return this.getSecondsAndNanos() / 1000000000L;
      }
   }

   public long getNanosOfSecond() {
      if (this.qualifier == IntervalQualifier.SECOND) {
         return this.negative ? -this.remaining : this.remaining;
      } else {
         return this.getSecondsAndNanos() % 1000000000L;
      }
   }

   public long getSecondsAndNanos() {
      return IntervalUtils.nanosFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
   }

   public int hashCode() {
      int var2 = 1;
      var2 = 31 * var2 + this.qualifier.hashCode();
      var2 = 31 * var2 + (this.negative ? 1231 : 1237);
      var2 = 31 * var2 + (int)(this.leading ^ this.leading >>> 32);
      var2 = 31 * var2 + (int)(this.remaining ^ this.remaining >>> 32);
      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Interval)) {
         return false;
      } else {
         Interval var2 = (Interval)var1;
         return this.qualifier == var2.qualifier && this.negative == var2.negative && this.leading == var2.leading && this.remaining == var2.remaining;
      }
   }

   public String toString() {
      return IntervalUtils.appendInterval(new StringBuilder(), this.getQualifier(), this.negative, this.leading, this.remaining).toString();
   }
}
