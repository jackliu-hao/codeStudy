package com.mysql.cj.result;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.time.Duration;

public class DurationValueFactory extends AbstractDateTimeValueFactory<Duration> {
   public DurationValueFactory(PropertySet pset) {
      super(pset);
   }

   Duration localCreateFromDate(InternalDate idate) {
      return (Duration)this.unsupported("DATE");
   }

   public Duration localCreateFromTime(InternalTime it) {
      String ptn = (it.getHours() < 0 ? "-PT" : "PT") + (it.getHours() < 0 ? -it.getHours() : it.getHours()) + "H" + it.getMinutes() + "M" + it.getSeconds() + "." + it.getNanos() + "S";
      return Duration.parse(ptn);
   }

   public Duration localCreateFromTimestamp(InternalTimestamp its) {
      return (Duration)this.unsupported("TIMESTAMP");
   }

   public Duration localCreateFromDatetime(InternalTimestamp its) {
      return (Duration)this.unsupported("DATETIME");
   }

   public String getTargetTypeName() {
      return Duration.class.getName();
   }
}
