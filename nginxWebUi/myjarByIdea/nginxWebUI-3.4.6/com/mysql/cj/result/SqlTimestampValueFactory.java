package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SqlTimestampValueFactory extends AbstractDateTimeValueFactory<Timestamp> {
   private Calendar cal;
   private TimeZone defaultTimeZone;
   private TimeZone connectionTimeZone;

   public SqlTimestampValueFactory(PropertySet pset, Calendar calendar, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
      super(pset);
      this.defaultTimeZone = defaultTimeZone;
      this.connectionTimeZone = connectionTimeZone;
      this.cal = calendar != null ? (Calendar)calendar.clone() : null;
   }

   public Timestamp localCreateFromDate(InternalDate idate) {
      if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         synchronized(this.defaultTimeZone) {
            Calendar c;
            if (this.cal != null) {
               c = this.cal;
            } else {
               c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
               c.setLenient(false);
            }

            Timestamp var10000;
            try {
               c.clear();
               c.set(idate.getYear(), idate.getMonth() - 1, idate.getDay(), 0, 0, 0);
               var10000 = new Timestamp(c.getTimeInMillis());
            } catch (IllegalArgumentException var6) {
               throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var6.getMessage(), (Throwable)var6);
            }

            return var10000;
         }
      }
   }

   public Timestamp localCreateFromTime(InternalTime it) {
      if (it.getHours() >= 0 && it.getHours() < 24) {
         synchronized(this.defaultTimeZone) {
            Calendar c;
            if (this.cal != null) {
               c = this.cal;
            } else {
               c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
               c.setLenient(false);
            }

            Timestamp var10000;
            try {
               c.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
               Timestamp ts = new Timestamp(c.getTimeInMillis());
               ts.setNanos(it.getNanos());
               var10000 = ts;
            } catch (IllegalArgumentException var6) {
               throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var6.getMessage(), (Throwable)var6);
            }

            return var10000;
         }
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{it.toString()}));
      }
   }

   public Timestamp localCreateFromTimestamp(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         synchronized(this.defaultTimeZone) {
            Calendar c;
            if (this.cal != null) {
               c = this.cal;
            } else {
               c = Calendar.getInstance((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
               c.setLenient(false);
            }

            Timestamp var10000;
            try {
               c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
               Timestamp ts = new Timestamp(c.getTimeInMillis());
               ts.setNanos(its.getNanos());
               var10000 = ts;
            } catch (IllegalArgumentException var6) {
               throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var6.getMessage(), (Throwable)var6);
            }

            return var10000;
         }
      }
   }

   public Timestamp localCreateFromDatetime(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         synchronized(this.defaultTimeZone) {
            Calendar c;
            if (this.cal != null) {
               c = this.cal;
            } else {
               c = Calendar.getInstance((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
               c.setLenient(false);
            }

            Timestamp var10000;
            try {
               c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
               Timestamp ts = new Timestamp(c.getTimeInMillis());
               ts.setNanos(its.getNanos());
               var10000 = ts;
            } catch (IllegalArgumentException var6) {
               throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var6.getMessage(), (Throwable)var6);
            }

            return var10000;
         }
      }
   }

   public String getTargetTypeName() {
      return Timestamp.class.getName();
   }
}
