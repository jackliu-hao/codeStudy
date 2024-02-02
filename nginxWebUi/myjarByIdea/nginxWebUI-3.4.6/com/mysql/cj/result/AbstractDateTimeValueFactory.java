package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
import com.mysql.cj.util.StringUtils;

public abstract class AbstractDateTimeValueFactory<T> extends DefaultValueFactory<T> {
   public AbstractDateTimeValueFactory(PropertySet pset) {
      super(pset);
   }

   abstract T localCreateFromDate(InternalDate var1);

   abstract T localCreateFromTime(InternalTime var1);

   abstract T localCreateFromTimestamp(InternalTimestamp var1);

   abstract T localCreateFromDatetime(InternalTimestamp var1);

   public T createFromDate(InternalDate idate) {
      if (idate.isZero()) {
         switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
            case CONVERT_TO_NULL:
               return null;
            case ROUND:
               return this.localCreateFromDate(new InternalDate(1, 1, 1));
         }
      }

      return this.localCreateFromDate(idate);
   }

   public T createFromTime(InternalTime it) {
      return this.localCreateFromTime(it);
   }

   public T createFromTimestamp(InternalTimestamp its) {
      if (its.isZero()) {
         switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
            case CONVERT_TO_NULL:
               return null;
            case ROUND:
               return this.localCreateFromTimestamp(new InternalTimestamp(1, 1, 1, 0, 0, 0, 0, 0));
         }
      }

      return this.localCreateFromTimestamp(its);
   }

   public T createFromDatetime(InternalTimestamp its) {
      if (its.isZero()) {
         switch ((PropertyDefinitions.ZeroDatetimeBehavior)this.pset.getEnumProperty(PropertyKey.zeroDateTimeBehavior).getValue()) {
            case CONVERT_TO_NULL:
               return null;
            case ROUND:
               return this.localCreateFromDatetime(new InternalTimestamp(1, 1, 1, 0, 0, 0, 0, 0));
         }
      }

      return this.localCreateFromDatetime(its);
   }

   public T createFromYear(long year) {
      if ((Boolean)this.pset.getBooleanProperty(PropertyKey.yearIsDateType).getValue()) {
         if (year < 100L) {
            if (year <= 69L) {
               year += 100L;
            }

            year += 1900L;
         }

         return this.createFromDate(new InternalDate((int)year, 1, 1));
      } else {
         return this.createFromLong(year);
      }
   }

   public T createFromBytes(byte[] bytes, int offset, int length, Field f) {
      if (length == 0 && (Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()) {
         return this.createFromLong(0L);
      } else {
         String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
         byte[] newBytes = s.getBytes();
         if (MysqlTextValueDecoder.isDate(s)) {
            return this.createFromDate(MysqlTextValueDecoder.getDate(newBytes, 0, newBytes.length));
         } else if (MysqlTextValueDecoder.isTime(s)) {
            return this.createFromTime(MysqlTextValueDecoder.getTime(newBytes, 0, newBytes.length, f.getDecimals()));
         } else if (MysqlTextValueDecoder.isTimestamp(s)) {
            return this.createFromTimestamp(MysqlTextValueDecoder.getTimestamp(newBytes, 0, newBytes.length, f.getDecimals()));
         } else {
            throw new DataConversionException(Messages.getString("ResultSet.UnableToConvertString", new Object[]{s, this.getTargetTypeName()}));
         }
      }
   }
}
