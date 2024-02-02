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
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class UtilCalendarValueFactory extends AbstractDateTimeValueFactory<Calendar> {
   private TimeZone defaultTimeZone;
   private TimeZone connectionTimeZone;

   public UtilCalendarValueFactory(PropertySet pset, TimeZone defaultTimeZone, TimeZone connectionTimeZone) {
      super(pset);
      this.defaultTimeZone = defaultTimeZone;
      this.connectionTimeZone = connectionTimeZone;
   }

   public Calendar localCreateFromDate(InternalDate idate) {
      if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         try {
            Calendar c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
            c.set(idate.getYear(), idate.getMonth() - 1, idate.getDay(), 0, 0, 0);
            c.set(14, 0);
            c.setLenient(false);
            return c;
         } catch (IllegalArgumentException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
         }
      }
   }

   public Calendar localCreateFromTime(InternalTime it) {
      if (it.getHours() >= 0 && it.getHours() < 24) {
         try {
            Calendar c = Calendar.getInstance(this.defaultTimeZone, Locale.US);
            c.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
            c.set(14, it.getNanos() / 1000000);
            c.setLenient(false);
            return c;
         } catch (IllegalArgumentException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
         }
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{it.toString()}));
      }
   }

   public Calendar localCreateFromTimestamp(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         try {
            Calendar c = Calendar.getInstance((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
            c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
            c.set(14, its.getNanos() / 1000000);
            c.setLenient(false);
            return c;
         } catch (IllegalArgumentException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
         }
      }
   }

   public Calendar localCreateFromDatetime(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         try {
            Calendar c = Calendar.getInstance((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue() ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
            c.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
            c.set(14, its.getNanos() / 1000000);
            c.setLenient(false);
            return c;
         } catch (IllegalArgumentException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)var3.getMessage(), (Throwable)var3);
         }
      }
   }

   public String getTargetTypeName() {
      return Calendar.class.getName();
   }
}
