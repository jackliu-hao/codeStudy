package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;

public class EnsureExceptionHandling implements PostCompileProcessor<ILoggingEvent> {
   public void process(Context context, Converter<ILoggingEvent> head) {
      if (head == null) {
         throw new IllegalArgumentException("cannot process empty chain");
      } else {
         if (!this.chainHandlesThrowable(head)) {
            Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
            Converter<ILoggingEvent> exConverter = null;
            LoggerContext loggerContext = (LoggerContext)context;
            if (loggerContext.isPackagingDataEnabled()) {
               exConverter = new ExtendedThrowableProxyConverter();
            } else {
               exConverter = new ThrowableProxyConverter();
            }

            tail.setNext((Converter)exConverter);
         }

      }
   }

   public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
      for(Converter<ILoggingEvent> c = head; c != null; c = c.getNext()) {
         if (c instanceof ThrowableHandlingConverter) {
            return true;
         }
      }

      return false;
   }
}
