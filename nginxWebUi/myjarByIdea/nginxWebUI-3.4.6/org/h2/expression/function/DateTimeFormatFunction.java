package org.h2.expression.function;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.JSR310Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;

public final class DateTimeFormatFunction extends FunctionN {
   public static final int FORMATDATETIME = 0;
   public static final int PARSEDATETIME = 1;
   private static final String[] NAMES = new String[]{"FORMATDATETIME", "PARSEDATETIME"};
   private static final LinkedHashMap<CacheKey, CacheValue> CACHE = new LinkedHashMap<CacheKey, CacheValue>() {
      private static final long serialVersionUID = 1L;

      protected boolean removeEldestEntry(Map.Entry<CacheKey, CacheValue> var1) {
         return this.size() > 100;
      }
   };
   private final int function;

   public DateTimeFormatFunction(int var1) {
      super(new Expression[4]);
      this.function = var1;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      String var5 = var3.getString();
      String var6;
      String var7;
      if (var4 != null) {
         var6 = var4.getString();
         var7 = this.args.length > 3 ? this.args[3].getValue(var1).getString() : null;
      } else {
         var6 = null;
         var7 = null;
      }

      Object var8;
      switch (this.function) {
         case 0:
            var8 = ValueVarchar.get(formatDateTime(var1, var2, var5, var6, var7));
            break;
         case 1:
            var8 = parseDateTime(var1, var2.getString(), var5, var6, var7);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var8;
   }

   public static String formatDateTime(SessionLocal var0, Value var1, String var2, String var3, String var4) {
      CacheValue var5 = getDateFormat(var2, var3, var4);
      ZoneId var6 = var5.zoneId;
      ZonedDateTime var7;
      if (var1 instanceof ValueTimestampTimeZone) {
         OffsetDateTime var8 = JSR310Utils.valueToOffsetDateTime(var1, var0);
         ZoneId var9;
         if (var6 != null) {
            var9 = var6;
         } else {
            ZoneOffset var10 = var8.getOffset();
            var9 = ZoneId.ofOffset(var10.getTotalSeconds() == 0 ? "UTC" : "GMT", var10);
         }

         var7 = var8.atZoneSameInstant(var9);
      } else {
         LocalDateTime var11 = JSR310Utils.valueToLocalDateTime(var1, var0);
         var7 = var11.atZone(var6 != null ? var6 : ZoneId.of(var0.currentTimeZone().getId()));
      }

      return var5.formatter.format(var7);
   }

   public static ValueTimestampTimeZone parseDateTime(SessionLocal var0, String var1, String var2, String var3, String var4) {
      CacheValue var5 = getDateFormat(var2, var3, var4);

      try {
         TemporalAccessor var7 = var5.formatter.parse(var1);
         ZoneId var8 = (ZoneId)var7.query(TemporalQueries.zoneId());
         ValueTimestampTimeZone var6;
         if (var7.isSupported(ChronoField.OFFSET_SECONDS)) {
            var6 = JSR310Utils.offsetDateTimeToValue(OffsetDateTime.from(var7));
         } else if (var7.isSupported(ChronoField.INSTANT_SECONDS)) {
            Instant var9 = Instant.from(var7);
            if (var8 == null) {
               var8 = var5.zoneId;
            }

            if (var8 != null) {
               var6 = JSR310Utils.zonedDateTimeToValue(var9.atZone(var8));
            } else {
               var6 = JSR310Utils.offsetDateTimeToValue(var9.atOffset(ZoneOffset.ofTotalSeconds(var0.currentTimeZone().getTimeZoneOffsetUTC(var9.getEpochSecond()))));
            }
         } else {
            LocalDate var13 = (LocalDate)var7.query(TemporalQueries.localDate());
            LocalTime var10 = (LocalTime)var7.query(TemporalQueries.localTime());
            if (var8 == null) {
               var8 = var5.zoneId;
            }

            if (var13 != null) {
               LocalDateTime var11 = var10 != null ? LocalDateTime.of(var13, var10) : var13.atStartOfDay();
               var6 = var8 != null ? JSR310Utils.zonedDateTimeToValue(var11.atZone(var8)) : (ValueTimestampTimeZone)JSR310Utils.localDateTimeToValue(var11).convertTo(21, var0);
            } else {
               var6 = var8 != null ? JSR310Utils.zonedDateTimeToValue(JSR310Utils.valueToInstant(var0.currentTimestamp(), var0).atZone(var8).with(var10)) : (ValueTimestampTimeZone)ValueTime.fromNanos(var10.toNanoOfDay()).convertTo(21, var0);
            }
         }

         return var6;
      } catch (RuntimeException var12) {
         throw DbException.get(90014, var12, var1);
      }
   }

   private static CacheValue getDateFormat(String var0, String var1, String var2) {
      Exception var3 = null;
      if (var0.length() <= 100) {
         try {
            CacheKey var5 = new CacheKey(var0, var1, var2);
            CacheValue var4;
            synchronized(CACHE) {
               var4 = (CacheValue)CACHE.get(var5);
               if (var4 == null) {
                  DateTimeFormatter var7;
                  if (var1 == null) {
                     var7 = DateTimeFormatter.ofPattern(var0);
                  } else {
                     var7 = DateTimeFormatter.ofPattern(var0, new Locale(var1));
                  }

                  ZoneId var8;
                  if (var2 != null) {
                     var8 = getZoneId(var2);
                     var7.withZone(var8);
                  } else {
                     var8 = null;
                  }

                  var4 = new CacheValue(var7, var8);
                  CACHE.put(var5, var4);
               }
            }

            return var4;
         } catch (Exception var11) {
            var3 = var11;
         }
      }

      throw DbException.get(90014, var3, var0 + '/' + var1);
   }

   private static ZoneId getZoneId(String var0) {
      try {
         return ZoneId.of(var0, ZoneId.SHORT_IDS);
      } catch (RuntimeException var2) {
         throw DbException.getInvalidValueException("TIME ZONE", var0);
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      switch (this.function) {
         case 0:
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         case 1:
            this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }

   private static final class CacheValue {
      final DateTimeFormatter formatter;
      final ZoneId zoneId;

      CacheValue(DateTimeFormatter var1, ZoneId var2) {
         this.formatter = var1;
         this.zoneId = var2;
      }
   }

   private static final class CacheKey {
      private final String format;
      private final String locale;
      private final String timeZone;

      CacheKey(String var1, String var2, String var3) {
         this.format = var1;
         this.locale = var2;
         this.timeZone = var3;
      }

      public int hashCode() {
         int var2 = 1;
         var2 = 31 * var2 + this.format.hashCode();
         var2 = 31 * var2 + (this.locale == null ? 0 : this.locale.hashCode());
         var2 = 31 * var2 + (this.timeZone == null ? 0 : this.timeZone.hashCode());
         return var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof CacheKey)) {
            return false;
         } else {
            CacheKey var2 = (CacheKey)var1;
            return this.format.equals(var2.format) && Objects.equals(this.locale, var2.locale) && Objects.equals(this.timeZone, var2.timeZone);
         }
      }
   }
}
