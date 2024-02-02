package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.time.LocalTime;

public class LocalTimeValueFactory extends AbstractDateTimeValueFactory<LocalTime> {
   private WarningListener warningListener;

   public LocalTimeValueFactory(PropertySet pset) {
      super(pset);
   }

   public LocalTimeValueFactory(PropertySet pset, WarningListener warningListener) {
      this(pset);
      this.warningListener = warningListener;
   }

   LocalTime localCreateFromDate(InternalDate idate) {
      return LocalTime.of(0, 0);
   }

   public LocalTime localCreateFromTime(InternalTime it) {
      if (it.getHours() >= 0 && it.getHours() < 24) {
         return LocalTime.of(it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos());
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{it.toString()}));
      }
   }

   public LocalTime localCreateFromTimestamp(InternalTimestamp its) {
      if (this.warningListener != null) {
         this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[]{this.getTargetTypeName()}));
      }

      return (LocalTime)this.createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
   }

   public LocalTime localCreateFromDatetime(InternalTimestamp its) {
      if (this.warningListener != null) {
         this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[]{this.getTargetTypeName()}));
      }

      return (LocalTime)this.createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
   }

   public String getTargetTypeName() {
      return LocalTime.class.getName();
   }
}
