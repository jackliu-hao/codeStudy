package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Marker;

public final class TurboFilterList extends CopyOnWriteArrayList<TurboFilter> {
   private static final long serialVersionUID = 1L;

   public FilterReply getTurboFilterChainDecision(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      int size = this.size();
      if (size == 1) {
         try {
            TurboFilter tf = (TurboFilter)this.get(0);
            return tf.decide(marker, logger, level, format, params, t);
         } catch (IndexOutOfBoundsException var13) {
            return FilterReply.NEUTRAL;
         }
      } else {
         Object[] tfa = this.toArray();
         int len = tfa.length;

         for(int i = 0; i < len; ++i) {
            TurboFilter tf = (TurboFilter)tfa[i];
            FilterReply r = tf.decide(marker, logger, level, format, params, t);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
               return r;
            }
         }

         return FilterReply.NEUTRAL;
      }
   }
}
