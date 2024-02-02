package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.time.LocalDateTime;

public class LocalDateTimeValueFactory extends AbstractDateTimeValueFactory<LocalDateTime> {
   public LocalDateTimeValueFactory(PropertySet pset) {
      super(pset);
   }

   public LocalDateTime localCreateFromDate(InternalDate idate) {
      return (LocalDateTime)this.createFromTimestamp(new InternalTimestamp(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0, 0));
   }

   public LocalDateTime localCreateFromTime(InternalTime it) {
      if (it.getHours() >= 0 && it.getHours() < 24) {
         return (LocalDateTime)this.createFromTimestamp(new InternalTimestamp(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos(), it.getScale()));
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{it.toString()}));
      }
   }

   public LocalDateTime localCreateFromTimestamp(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos());
      }
   }

   public LocalDateTime localCreateFromDatetime(InternalTimestamp its) {
      if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
      } else {
         return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos());
      }
   }

   public String getTargetTypeName() {
      return LocalDateTime.class.getName();
   }
}
