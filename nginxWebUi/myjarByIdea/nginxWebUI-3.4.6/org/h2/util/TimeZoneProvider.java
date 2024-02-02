package org.h2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.Locale;

public abstract class TimeZoneProvider {
   public static final TimeZoneProvider UTC = new Simple(0);
   public static TimeZoneProvider[] CACHE;
   private static final int CACHE_SIZE = 32;

   public static TimeZoneProvider ofOffset(int var0) {
      if (var0 == 0) {
         return UTC;
      } else if (var0 >= -64800 && var0 <= 64800) {
         return new Simple(var0);
      } else {
         throw new IllegalArgumentException("Time zone offset " + var0 + " seconds is out of range");
      }
   }

   public static TimeZoneProvider ofId(String var0) throws RuntimeException {
      int var1 = var0.length();
      if (var1 == 1 && var0.charAt(0) == 'Z') {
         return UTC;
      } else {
         int var2 = 0;
         if (var0.startsWith("GMT") || var0.startsWith("UTC")) {
            if (var1 == 3) {
               return UTC;
            }

            var2 = 3;
         }

         if (var1 > var2) {
            boolean var3 = false;
            char var4 = var0.charAt(var2);
            if (var1 > var2 + 1) {
               if (var4 == '+') {
                  ++var2;
                  var4 = var0.charAt(var2);
               } else if (var4 == '-') {
                  var3 = true;
                  ++var2;
                  var4 = var0.charAt(var2);
               }
            }

            if (var2 != 3 && var4 >= '0' && var4 <= '9') {
               int var5 = var4 - 48;
               ++var2;
               if (var2 < var1) {
                  var4 = var0.charAt(var2);
                  if (var4 >= '0' && var4 <= '9') {
                     var5 = var5 * 10 + var4 - 48;
                     ++var2;
                  }
               }

               int var6;
               if (var2 == var1) {
                  var6 = var5 * 3600;
                  return ofOffset(var3 ? -var6 : var6);
               }

               if (var0.charAt(var2) == ':') {
                  ++var2;
                  if (var2 < var1) {
                     var4 = var0.charAt(var2);
                     if (var4 >= '0' && var4 <= '9') {
                        var6 = var4 - 48;
                        ++var2;
                        if (var2 < var1) {
                           var4 = var0.charAt(var2);
                           if (var4 >= '0' && var4 <= '9') {
                              var6 = var6 * 10 + var4 - 48;
                              ++var2;
                           }
                        }

                        int var7;
                        if (var2 == var1) {
                           var7 = (var5 * 60 + var6) * 60;
                           return ofOffset(var3 ? -var7 : var7);
                        }

                        if (var0.charAt(var2) == ':') {
                           ++var2;
                           if (var2 < var1) {
                              var4 = var0.charAt(var2);
                              if (var4 >= '0' && var4 <= '9') {
                                 var7 = var4 - 48;
                                 ++var2;
                                 if (var2 < var1) {
                                    var4 = var0.charAt(var2);
                                    if (var4 >= '0' && var4 <= '9') {
                                       var7 = var7 * 10 + var4 - 48;
                                       ++var2;
                                    }
                                 }

                                 if (var2 == var1) {
                                    int var8 = (var5 * 60 + var6) * 60 + var7;
                                    return ofOffset(var3 ? -var8 : var8);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (var2 > 0) {
               throw new IllegalArgumentException(var0);
            }
         }

         int var9 = var0.hashCode() & 31;
         TimeZoneProvider[] var12 = CACHE;
         if (var12 != null) {
            TimeZoneProvider var10 = var12[var9];
            if (var10 != null && var10.getId().equals(var0)) {
               return var10;
            }
         }

         WithTimeZone var11 = new WithTimeZone(ZoneId.of(var0, ZoneId.SHORT_IDS));
         if (var12 == null) {
            CACHE = var12 = new TimeZoneProvider[32];
         }

         var12[var9] = var11;
         return var11;
      }
   }

   public static TimeZoneProvider getDefault() {
      ZoneId var0 = ZoneId.systemDefault();
      ZoneOffset var1;
      if (var0 instanceof ZoneOffset) {
         var1 = (ZoneOffset)var0;
      } else {
         ZoneRules var2 = var0.getRules();
         if (!var2.isFixedOffset()) {
            return new WithTimeZone(var0);
         }

         var1 = var2.getOffset(Instant.EPOCH);
      }

      return ofOffset(var1.getTotalSeconds());
   }

   public abstract int getTimeZoneOffsetUTC(long var1);

   public abstract int getTimeZoneOffsetLocal(long var1, long var3);

   public abstract long getEpochSecondsFromLocal(long var1, long var3);

   public abstract String getId();

   public abstract String getShortId(long var1);

   public boolean hasFixedOffset() {
      return false;
   }

   static final class WithTimeZone extends TimeZoneProvider {
      static final long SECONDS_PER_PERIOD = 12622780800L;
      static final long SECONDS_PER_YEAR = 31556952L;
      private static volatile DateTimeFormatter TIME_ZONE_FORMATTER;
      private final ZoneId zoneId;

      WithTimeZone(ZoneId var1) {
         this.zoneId = var1;
      }

      public int hashCode() {
         return this.zoneId.hashCode() + 951689;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 != null && var1.getClass() == WithTimeZone.class ? this.zoneId.equals(((WithTimeZone)var1).zoneId) : false;
         }
      }

      public int getTimeZoneOffsetUTC(long var1) {
         if (var1 > 31556889832715999L) {
            var1 -= 12622780800L;
         } else if (var1 < -31557014135532000L) {
            var1 += 12622780800L;
         }

         return this.zoneId.getRules().getOffset(Instant.ofEpochSecond(var1)).getTotalSeconds();
      }

      public int getTimeZoneOffsetLocal(long var1, long var3) {
         int var5 = (int)(var3 / 1000000000L);
         int var6 = var5 / 60;
         var5 -= var6 * 60;
         int var7 = var6 / 60;
         var6 -= var7 * 60;
         return ZonedDateTime.of(LocalDateTime.of(yearForCalendar(DateTimeUtils.yearFromDateValue(var1)), DateTimeUtils.monthFromDateValue(var1), DateTimeUtils.dayFromDateValue(var1), var7, var6, var5), this.zoneId).getOffset().getTotalSeconds();
      }

      public long getEpochSecondsFromLocal(long var1, long var3) {
         int var5 = (int)(var3 / 1000000000L);
         int var6 = var5 / 60;
         var5 -= var6 * 60;
         int var7 = var6 / 60;
         var6 -= var7 * 60;
         int var8 = DateTimeUtils.yearFromDateValue(var1);
         int var9 = yearForCalendar(var8);
         long var10 = ZonedDateTime.of(LocalDateTime.of(var9, DateTimeUtils.monthFromDateValue(var1), DateTimeUtils.dayFromDateValue(var1), var7, var6, var5), this.zoneId).toOffsetDateTime().toEpochSecond();
         return var10 + (long)(var8 - var9) * 31556952L;
      }

      public String getId() {
         return this.zoneId.getId();
      }

      public String getShortId(long var1) {
         DateTimeFormatter var3 = TIME_ZONE_FORMATTER;
         if (var3 == null) {
            TIME_ZONE_FORMATTER = var3 = DateTimeFormatter.ofPattern("z", Locale.ENGLISH);
         }

         return ZonedDateTime.ofInstant(Instant.ofEpochSecond(var1), this.zoneId).format(var3);
      }

      private static int yearForCalendar(int var0) {
         if (var0 > 999999999) {
            var0 -= 400;
         } else if (var0 < -999999999) {
            var0 += 400;
         }

         return var0;
      }

      public String toString() {
         return "TimeZoneProvider " + this.zoneId.getId();
      }
   }

   private static final class Simple extends TimeZoneProvider {
      private final int offset;
      private volatile String id;

      Simple(int var1) {
         this.offset = var1;
      }

      public int hashCode() {
         return this.offset + 129607;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && var1.getClass() == Simple.class) {
            return this.offset == ((Simple)var1).offset;
         } else {
            return false;
         }
      }

      public int getTimeZoneOffsetUTC(long var1) {
         return this.offset;
      }

      public int getTimeZoneOffsetLocal(long var1, long var3) {
         return this.offset;
      }

      public long getEpochSecondsFromLocal(long var1, long var3) {
         return DateTimeUtils.getEpochSeconds(var1, var3, this.offset);
      }

      public String getId() {
         String var1 = this.id;
         if (var1 == null) {
            this.id = var1 = DateTimeUtils.timeZoneNameFromOffsetSeconds(this.offset);
         }

         return var1;
      }

      public String getShortId(long var1) {
         return this.getId();
      }

      public boolean hasFixedOffset() {
         return true;
      }

      public String toString() {
         return "TimeZoneProvider " + this.getId();
      }
   }
}
