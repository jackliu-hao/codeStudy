package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

public class TemporalAccessorConverter extends AbstractConverter<TemporalAccessor> {
   private static final long serialVersionUID = 1L;
   private final Class<?> targetType;
   private String format;

   public TemporalAccessorConverter(Class<?> targetType) {
      this(targetType, (String)null);
   }

   public TemporalAccessorConverter(Class<?> targetType, String format) {
      this.targetType = targetType;
      this.format = format;
   }

   public String getFormat() {
      return this.format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public Class<TemporalAccessor> getTargetType() {
      return this.targetType;
   }

   protected TemporalAccessor convertInternal(Object value) {
      if (value instanceof Long) {
         return this.parseFromLong((Long)value);
      } else if (value instanceof TemporalAccessor) {
         return this.parseFromTemporalAccessor((TemporalAccessor)value);
      } else if (value instanceof Date) {
         DateTime dateTime = DateUtil.date((Date)value);
         return this.parseFromInstant(dateTime.toInstant(), dateTime.getZoneId());
      } else if (value instanceof Calendar) {
         Calendar calendar = (Calendar)value;
         return this.parseFromInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
      } else {
         return this.parseFromCharSequence(this.convertToStr(value));
      }
   }

   private TemporalAccessor parseFromCharSequence(CharSequence value) {
      if (StrUtil.isBlank(value)) {
         return null;
      } else {
         Instant instant;
         ZoneId zoneId;
         if (null != this.format) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.format);
            instant = (Instant)formatter.parse(value, Instant::from);
            zoneId = formatter.getZone();
         } else {
            DateTime dateTime = DateUtil.parse(value);
            instant = ((DateTime)Objects.requireNonNull(dateTime)).toInstant();
            zoneId = dateTime.getZoneId();
         }

         return this.parseFromInstant(instant, zoneId);
      }
   }

   private TemporalAccessor parseFromLong(Long time) {
      return this.parseFromInstant(Instant.ofEpochMilli(time), (ZoneId)null);
   }

   private TemporalAccessor parseFromTemporalAccessor(TemporalAccessor temporalAccessor) {
      TemporalAccessor result = null;
      if (temporalAccessor instanceof LocalDateTime) {
         result = this.parseFromLocalDateTime((LocalDateTime)temporalAccessor);
      } else if (temporalAccessor instanceof ZonedDateTime) {
         result = this.parseFromZonedDateTime((ZonedDateTime)temporalAccessor);
      }

      if (null == result) {
         result = this.parseFromInstant(DateUtil.toInstant(temporalAccessor), (ZoneId)null);
      }

      return result;
   }

   private TemporalAccessor parseFromLocalDateTime(LocalDateTime localDateTime) {
      if (Instant.class.equals(this.targetType)) {
         return DateUtil.toInstant((TemporalAccessor)localDateTime);
      } else if (LocalDate.class.equals(this.targetType)) {
         return localDateTime.toLocalDate();
      } else if (LocalTime.class.equals(this.targetType)) {
         return localDateTime.toLocalTime();
      } else if (ZonedDateTime.class.equals(this.targetType)) {
         return localDateTime.atZone(ZoneId.systemDefault());
      } else if (OffsetDateTime.class.equals(this.targetType)) {
         return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
      } else {
         return OffsetTime.class.equals(this.targetType) ? localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime() : null;
      }
   }

   private TemporalAccessor parseFromZonedDateTime(ZonedDateTime zonedDateTime) {
      if (Instant.class.equals(this.targetType)) {
         return DateUtil.toInstant((TemporalAccessor)zonedDateTime);
      } else if (LocalDateTime.class.equals(this.targetType)) {
         return zonedDateTime.toLocalDateTime();
      } else if (LocalDate.class.equals(this.targetType)) {
         return zonedDateTime.toLocalDate();
      } else if (LocalTime.class.equals(this.targetType)) {
         return zonedDateTime.toLocalTime();
      } else if (OffsetDateTime.class.equals(this.targetType)) {
         return zonedDateTime.toOffsetDateTime();
      } else {
         return OffsetTime.class.equals(this.targetType) ? zonedDateTime.toOffsetDateTime().toOffsetTime() : null;
      }
   }

   private TemporalAccessor parseFromInstant(Instant instant, ZoneId zoneId) {
      if (Instant.class.equals(this.targetType)) {
         return instant;
      } else {
         zoneId = (ZoneId)ObjectUtil.defaultIfNull(zoneId, (Supplier)(ZoneId::systemDefault));
         TemporalAccessor result = null;
         if (LocalDateTime.class.equals(this.targetType)) {
            result = LocalDateTime.ofInstant(instant, zoneId);
         } else if (LocalDate.class.equals(this.targetType)) {
            result = instant.atZone(zoneId).toLocalDate();
         } else if (LocalTime.class.equals(this.targetType)) {
            result = instant.atZone(zoneId).toLocalTime();
         } else if (ZonedDateTime.class.equals(this.targetType)) {
            result = instant.atZone(zoneId);
         } else if (OffsetDateTime.class.equals(this.targetType)) {
            result = OffsetDateTime.ofInstant(instant, zoneId);
         } else if (OffsetTime.class.equals(this.targetType)) {
            result = OffsetTime.ofInstant(instant, zoneId);
         }

         return (TemporalAccessor)result;
      }
   }
}
