package ch.qos.logback.core.filter;

import ch.qos.logback.core.spi.FilterReply;

public abstract class AbstractMatcherFilter<E> extends Filter<E> {
   protected FilterReply onMatch;
   protected FilterReply onMismatch;

   public AbstractMatcherFilter() {
      this.onMatch = FilterReply.NEUTRAL;
      this.onMismatch = FilterReply.NEUTRAL;
   }

   public final void setOnMatch(FilterReply reply) {
      this.onMatch = reply;
   }

   public final void setOnMismatch(FilterReply reply) {
      this.onMismatch = reply;
   }

   public final FilterReply getOnMatch() {
      return this.onMatch;
   }

   public final FilterReply getOnMismatch() {
      return this.onMismatch;
   }
}
