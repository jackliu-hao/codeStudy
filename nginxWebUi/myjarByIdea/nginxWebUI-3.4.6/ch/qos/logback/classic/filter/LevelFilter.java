package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class LevelFilter extends AbstractMatcherFilter<ILoggingEvent> {
   Level level;

   public FilterReply decide(ILoggingEvent event) {
      if (!this.isStarted()) {
         return FilterReply.NEUTRAL;
      } else {
         return event.getLevel().equals(this.level) ? this.onMatch : this.onMismatch;
      }
   }

   public void setLevel(Level level) {
      this.level = level;
   }

   public void start() {
      if (this.level != null) {
         super.start();
      }

   }
}
