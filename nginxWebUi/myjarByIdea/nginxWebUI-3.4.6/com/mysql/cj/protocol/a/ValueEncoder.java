package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ValueEncoder {
   private NativePacketPayload packet;
   private String characterEncoding;
   private TimeZone timezone;

   public ValueEncoder(NativePacketPayload packet, String characterEncoding, TimeZone timezone) {
      this.packet = packet;
      this.characterEncoding = characterEncoding;
      this.timezone = timezone;
   }

   public void encodeValue(Object value, int fieldType) {
      if (value != null) {
         switch (fieldType) {
            case 1:
               this.encodeInt1(this.asByte(value));
               return;
            case 2:
               this.encodeInt2(this.asShort(value));
               return;
            case 3:
            case 4:
               this.encodeInt4(this.asInteger(value));
               return;
            case 5:
            case 8:
               this.encodeInt8(this.asLong(value));
               return;
            case 7:
               this.encodeTimeStamp(this.asInternalTimestampTz(value));
               return;
            case 10:
               this.encodeDate(this.asInternalDate(value));
               return;
            case 11:
               this.encodeTime(this.asInternalTime(value));
               return;
            case 12:
               this.encodeDateTime(this.asInternalTimestampNoTz(value));
               return;
            case 254:
               this.encodeString(this.asString(value));
               return;
            default:
         }
      }
   }

   public void encodeInt1(Byte value) {
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, value.longValue());
   }

   public void encodeInt2(Short value) {
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, value.longValue());
   }

   public void encodeInt4(Integer value) {
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, value.longValue());
   }

   public void encodeInt8(Long value) {
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT8, value);
   }

   public void encodeDate(InternalDate date) {
      this.packet.ensureCapacity(5);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, 4L);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, (long)date.getYear());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)date.getMonth());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)date.getDay());
   }

   public void encodeTime(InternalTime time) {
      boolean hasFractionalSeconds = time.getNanos() > 0;
      this.packet.ensureCapacity((hasFractionalSeconds ? 12 : 8) + 1);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, hasFractionalSeconds ? 12L : 8L);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, time.isNegative() ? 1L : 0L);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, (long)(time.getHours() / 24));
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)(time.getHours() % 24));
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)time.getMinutes());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)time.getSeconds());
      if (hasFractionalSeconds) {
         this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros((long)time.getNanos()));
      }

   }

   public void encodeDateTime(InternalTimestamp timestamp) {
      boolean hasFractionalSeconds = timestamp.getNanos() > 0;
      this.packet.ensureCapacity((hasFractionalSeconds ? 11 : 7) + 1);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, hasFractionalSeconds ? 11L : 7L);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, (long)timestamp.getYear());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getMonth());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getDay());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getHours());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getMinutes());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getSeconds());
      if (hasFractionalSeconds) {
         this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros((long)timestamp.getNanos()));
      }

   }

   public void encodeTimeStamp(InternalTimestamp timestamp) {
      this.packet.ensureCapacity(14);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, 13L);
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, (long)timestamp.getYear());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getMonth());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getDay());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getHours());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getMinutes());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)timestamp.getSeconds());
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros((long)timestamp.getNanos()));
      this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, (long)timestamp.getOffset());
   }

   public void encodeString(String value) {
      this.packet.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(value, this.characterEncoding));
   }

   private Byte asByte(Object value) {
      if (Boolean.class.isInstance(value)) {
         return (Boolean)value ? new Byte((byte)1) : new Byte((byte)0);
      } else if (Byte.class.isInstance(value)) {
         return (Byte)value;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongTinyIntValueType", new Object[]{value.getClass()}));
      }
   }

   private Short asShort(Object value) {
      if (Short.class.isInstance(value)) {
         return (Short)value;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongSmallIntValueType", new Object[]{value.getClass()}));
      }
   }

   private Integer asInteger(Object value) {
      if (Integer.class.isInstance(value)) {
         return (Integer)value;
      } else if (Float.class.isInstance(value)) {
         return Float.floatToIntBits((Float)value);
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongIntValueType", new Object[]{value.getClass()}));
      }
   }

   private Long asLong(Object value) {
      if (Long.class.isInstance(value)) {
         return (Long)value;
      } else if (Double.class.isInstance(value)) {
         return Double.doubleToLongBits((Double)value);
      } else if (BigInteger.class.isInstance(value)) {
         return ((BigInteger)value).longValue();
      } else if (BigDecimal.class.isInstance(value)) {
         return Double.doubleToLongBits(((BigDecimal)value).doubleValue());
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongBigIntValueType", new Object[]{value.getClass()}));
      }
   }

   private InternalDate asInternalDate(Object value) {
      InternalDate internalDate;
      if (LocalDate.class.isInstance(value)) {
         LocalDate localDate = (LocalDate)value;
         internalDate = new InternalDate();
         internalDate.setYear(localDate.getYear());
         internalDate.setMonth(localDate.getMonthValue());
         internalDate.setDay(localDate.getDayOfMonth());
         return internalDate;
      } else if (Date.class.isInstance(value)) {
         Calendar calendar = Calendar.getInstance(this.timezone);
         calendar.setTime((Date)value);
         calendar.set(11, 0);
         calendar.set(12, 0);
         calendar.set(13, 0);
         internalDate = new InternalDate();
         internalDate.setYear(calendar.get(1));
         internalDate.setMonth(calendar.get(2) + 1);
         internalDate.setDay(calendar.get(5));
         return internalDate;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongDateValueType", new Object[]{value.getClass()}));
      }
   }

   private InternalTime asInternalTime(Object value) {
      InternalTime internalTime;
      if (LocalTime.class.isInstance(value)) {
         LocalTime localTime = (LocalTime)value;
         internalTime = new InternalTime();
         internalTime.setHours(localTime.getHour());
         internalTime.setMinutes(localTime.getMinute());
         internalTime.setSeconds(localTime.getSecond());
         internalTime.setNanos(localTime.getNano());
         return internalTime;
      } else if (OffsetTime.class.isInstance(value)) {
         OffsetTime offsetTime = (OffsetTime)value;
         internalTime = new InternalTime();
         internalTime.setHours(offsetTime.getHour());
         internalTime.setMinutes(offsetTime.getMinute());
         internalTime.setSeconds(offsetTime.getSecond());
         internalTime.setNanos(offsetTime.getNano());
         return internalTime;
      } else if (Duration.class.isInstance(value)) {
         Duration duration = (Duration)value;
         Duration durationAbs = duration.abs();
         long fullSeconds = durationAbs.getSeconds();
         int seconds = (int)(fullSeconds % 60L);
         long fullMinutes = fullSeconds / 60L;
         int minutes = (int)(fullMinutes % 60L);
         long fullHours = fullMinutes / 60L;
         InternalTime internalTime = new InternalTime();
         internalTime.setNegative(duration.isNegative());
         internalTime.setHours((int)fullHours);
         internalTime.setMinutes(minutes);
         internalTime.setSeconds(seconds);
         internalTime.setNanos(durationAbs.getNano());
         return internalTime;
      } else if (Time.class.isInstance(value)) {
         Time time = (Time)value;
         Calendar calendar = Calendar.getInstance(this.timezone);
         calendar.setTime(time);
         InternalTime internalTime = new InternalTime();
         internalTime.setHours(calendar.get(11));
         internalTime.setMinutes(calendar.get(12));
         internalTime.setSeconds(calendar.get(13));
         internalTime.setNanos((int)TimeUnit.MILLISECONDS.toNanos((long)calendar.get(14)));
         return internalTime;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongTimeValueType", new Object[]{value.getClass()}));
      }
   }

   private InternalTimestamp asInternalTimestampNoTz(Object value) {
      if (LocalDateTime.class.isInstance(value)) {
         LocalDateTime localDateTime = (LocalDateTime)value;
         InternalTimestamp internalTimestamp = new InternalTimestamp();
         internalTimestamp.setYear(localDateTime.getYear());
         internalTimestamp.setMonth(localDateTime.getMonthValue());
         internalTimestamp.setDay(localDateTime.getDayOfMonth());
         internalTimestamp.setHours(localDateTime.getHour());
         internalTimestamp.setMinutes(localDateTime.getMinute());
         internalTimestamp.setSeconds(localDateTime.getSecond());
         internalTimestamp.setNanos(localDateTime.getNano());
         return internalTimestamp;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongDatetimeValueType", new Object[]{value.getClass()}));
      }
   }

   private InternalTimestamp asInternalTimestampTz(Object value) {
      InternalTimestamp internalTimestamp;
      if (Instant.class.isInstance(value)) {
         Instant instant = (Instant)value;
         OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
         internalTimestamp = new InternalTimestamp();
         internalTimestamp.setYear(offsetDateTime.getYear());
         internalTimestamp.setMonth(offsetDateTime.getMonthValue());
         internalTimestamp.setDay(offsetDateTime.getDayOfMonth());
         internalTimestamp.setHours(offsetDateTime.getHour());
         internalTimestamp.setMinutes(offsetDateTime.getMinute());
         internalTimestamp.setSeconds(offsetDateTime.getSecond());
         internalTimestamp.setNanos(offsetDateTime.getNano());
         internalTimestamp.setOffset(0);
         return internalTimestamp;
      } else {
         InternalTimestamp internalTimestamp;
         if (OffsetDateTime.class.isInstance(value)) {
            OffsetDateTime offsetDateTime = (OffsetDateTime)value;
            internalTimestamp = new InternalTimestamp();
            internalTimestamp.setYear(offsetDateTime.getYear());
            internalTimestamp.setMonth(offsetDateTime.getMonthValue());
            internalTimestamp.setDay(offsetDateTime.getDayOfMonth());
            internalTimestamp.setHours(offsetDateTime.getHour());
            internalTimestamp.setMinutes(offsetDateTime.getMinute());
            internalTimestamp.setSeconds(offsetDateTime.getSecond());
            internalTimestamp.setNanos(offsetDateTime.getNano());
            internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes((long)offsetDateTime.getOffset().getTotalSeconds()));
            return internalTimestamp;
         } else if (ZonedDateTime.class.isInstance(value)) {
            ZonedDateTime zonedDateTime = (ZonedDateTime)value;
            internalTimestamp = new InternalTimestamp();
            internalTimestamp.setYear(zonedDateTime.getYear());
            internalTimestamp.setMonth(zonedDateTime.getMonthValue());
            internalTimestamp.setDay(zonedDateTime.getDayOfMonth());
            internalTimestamp.setHours(zonedDateTime.getHour());
            internalTimestamp.setMinutes(zonedDateTime.getMinute());
            internalTimestamp.setSeconds(zonedDateTime.getSecond());
            internalTimestamp.setNanos(zonedDateTime.getNano());
            internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes((long)zonedDateTime.getOffset().getTotalSeconds()));
            return internalTimestamp;
         } else if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            internalTimestamp = new InternalTimestamp();
            internalTimestamp.setYear(calendar.get(1));
            internalTimestamp.setMonth(calendar.get(2) + 1);
            internalTimestamp.setDay(calendar.get(5));
            internalTimestamp.setHours(calendar.get(11));
            internalTimestamp.setMinutes(calendar.get(12));
            internalTimestamp.setSeconds(calendar.get(13));
            internalTimestamp.setNanos((int)TimeUnit.MILLISECONDS.toNanos((long)calendar.get(14)));
            internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes((long)calendar.getTimeZone().getOffset(calendar.getTimeInMillis())));
            return internalTimestamp;
         } else {
            Calendar calendar;
            if (Timestamp.class.isInstance(value)) {
               Timestamp timestamp = (Timestamp)value;
               calendar = Calendar.getInstance(this.timezone);
               calendar.setTime(timestamp);
               internalTimestamp = new InternalTimestamp();
               internalTimestamp.setYear(calendar.get(1));
               internalTimestamp.setMonth(calendar.get(2) + 1);
               internalTimestamp.setDay(calendar.get(5));
               internalTimestamp.setHours(calendar.get(11));
               internalTimestamp.setMinutes(calendar.get(12));
               internalTimestamp.setSeconds(calendar.get(13));
               internalTimestamp.setNanos(timestamp.getNanos());
               internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes((long)calendar.getTimeZone().getOffset(calendar.getTimeInMillis())));
               return internalTimestamp;
            } else if (java.util.Date.class.isInstance(value)) {
               java.util.Date date = (java.util.Date)value;
               calendar = Calendar.getInstance(this.timezone);
               calendar.setTime(date);
               internalTimestamp = new InternalTimestamp();
               internalTimestamp.setYear(calendar.get(1));
               internalTimestamp.setMonth(calendar.get(2) + 1);
               internalTimestamp.setDay(calendar.get(5));
               internalTimestamp.setHours(calendar.get(11));
               internalTimestamp.setMinutes(calendar.get(12));
               internalTimestamp.setSeconds(calendar.get(13));
               internalTimestamp.setNanos((int)TimeUnit.MILLISECONDS.toNanos((long)calendar.get(14)));
               internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes((long)calendar.getTimeZone().getOffset(date.getTime())));
               return internalTimestamp;
            } else {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ValueEncoder.WrongTimestampValueType", new Object[]{value.getClass()}));
            }
         }
      }
   }

   private String asString(Object value) {
      return String.class.isInstance(value) ? (String)value : value.toString();
   }
}
