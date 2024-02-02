package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

public class DateConverter extends AbstractConverter<Date> {
   private static final long serialVersionUID = 1L;
   private final Class<? extends Date> targetType;
   private String format;

   public DateConverter(Class<? extends Date> targetType) {
      this.targetType = targetType;
   }

   public DateConverter(Class<? extends Date> targetType, String format) {
      this.targetType = targetType;
      this.format = format;
   }

   public String getFormat() {
      return this.format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   protected Date convertInternal(Object value) {
      if (value != null && (!(value instanceof CharSequence) || !StrUtil.isBlank(value.toString()))) {
         if (value instanceof TemporalAccessor) {
            return this.wrap(DateUtil.date((TemporalAccessor)value));
         } else if (value instanceof Calendar) {
            return this.wrap(DateUtil.date((Calendar)value));
         } else if (value instanceof Number) {
            return this.wrap(((Number)value).longValue());
         } else {
            String valueStr = this.convertToStr(value);
            DateTime dateTime = StrUtil.isBlank(this.format) ? DateUtil.parse(valueStr) : DateUtil.parse((CharSequence)valueStr, (String)this.format);
            if (null != dateTime) {
               return this.wrap(dateTime);
            } else {
               throw new ConvertException("Can not convert {}:[{}] to {}", new Object[]{value.getClass().getName(), value, this.targetType.getName()});
            }
         }
      } else {
         return null;
      }
   }

   private Date wrap(DateTime date) {
      if (Date.class == this.targetType) {
         return date.toJdkDate();
      } else if (DateTime.class == this.targetType) {
         return date;
      } else if (java.sql.Date.class == this.targetType) {
         return date.toSqlDate();
      } else if (Time.class == this.targetType) {
         return new Time(date.getTime());
      } else if (Timestamp.class == this.targetType) {
         return date.toTimestamp();
      } else {
         throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", new Object[]{this.targetType.getName()}));
      }
   }

   private Date wrap(long mills) {
      if (Date.class == this.targetType) {
         return new Date(mills);
      } else if (DateTime.class == this.targetType) {
         return DateUtil.date(mills);
      } else if (java.sql.Date.class == this.targetType) {
         return new java.sql.Date(mills);
      } else if (Time.class == this.targetType) {
         return new Time(mills);
      } else if (Timestamp.class == this.targetType) {
         return new Timestamp(mills);
      } else {
         throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", new Object[]{this.targetType.getName()}));
      }
   }

   public Class<Date> getTargetType() {
      return this.targetType;
   }
}
