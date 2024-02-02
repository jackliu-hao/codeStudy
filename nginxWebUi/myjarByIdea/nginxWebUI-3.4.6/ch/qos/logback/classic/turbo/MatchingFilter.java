package ch.qos.logback.classic.turbo;

import ch.qos.logback.core.spi.FilterReply;

public abstract class MatchingFilter extends TurboFilter {
   protected FilterReply onMatch;
   protected FilterReply onMismatch;

   public MatchingFilter() {
      this.onMatch = FilterReply.NEUTRAL;
      this.onMismatch = FilterReply.NEUTRAL;
   }

   public final void setOnMatch(String action) {
      if ("NEUTRAL".equals(action)) {
         this.onMatch = FilterReply.NEUTRAL;
      } else if ("ACCEPT".equals(action)) {
         this.onMatch = FilterReply.ACCEPT;
      } else if ("DENY".equals(action)) {
         this.onMatch = FilterReply.DENY;
      }

   }

   public final void setOnMismatch(String action) {
      if ("NEUTRAL".equals(action)) {
         this.onMismatch = FilterReply.NEUTRAL;
      } else if ("ACCEPT".equals(action)) {
         this.onMismatch = FilterReply.ACCEPT;
      } else if ("DENY".equals(action)) {
         this.onMismatch = FilterReply.DENY;
      }

   }
}
