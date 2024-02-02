package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
import com.mysql.cj.util.StringUtils;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

public class OffsetDateTimeValueFactory extends AbstractDateTimeValueFactory<OffsetDateTime> {
   private TimeZone defaultTimeZone;
   private TimeZone connectionTimeZone;

   public OffsetDateTimeValueFactory(PropertySet pset, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
      super(pset);
      this.defaultTimeZone = defaultTimeZone;
      this.connectionTimeZone = connectionTimeZone;
   }

   public OffsetDateTime localCreateFromDate(InternalDate idate) {
      if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         return LocalDateTime.of(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0).atZone(this.defaultTimeZone.toZoneId()).toOffsetDateTime();
      }
   }

   public OffsetDateTime localCreateFromTime(InternalTime it) {
      if (it.getHours() >= 0 && it.getHours() < 24) {
         return LocalDateTime.of(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos()).atZone(this.defaultTimeZone.toZoneId()).toOffsetDateTime();
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{it.toString()}));
      }
   }

   public OffsetDateTime localCreateFromTimestamp(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos()).atZone(((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId()).toOffsetDateTime();
      }
   }

   public OffsetDateTime localCreateFromDatetime(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos()).atZone(((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone).toZoneId()).toOffsetDateTime();
      }
   }

   public OffsetDateTime createFromBytes(byte[] bytes, int offset, int length, Field f) {
      if (length == 0 && (Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()) {
         return (OffsetDateTime)this.createFromLong(0L);
      } else {
         String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
         byte[] newBytes = s.getBytes();
         if (MysqlTextValueDecoder.isDate(s)) {
            return (OffsetDateTime)this.createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
         } else if (MysqlTextValueDecoder.isTime(s)) {
            return (OffsetDateTime)this.createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
         } else if (MysqlTextValueDecoder.isTimestamp(s)) {
            return (OffsetDateTime)this.createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
         } else {
            try {
               return OffsetDateTime.parse(s.replace(" ", "T"));
            } catch (DateTimeParseException var8) {
               throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[]{s, this.getTargetTypeName()}));
            }
         }
      }
   }

   public String getTargetTypeName() {
      return OffsetDateTime.class.getName();
   }
}
