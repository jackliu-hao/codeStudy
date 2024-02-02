package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface IEvaluator {
  boolean doEvaluate(ILoggingEvent paramILoggingEvent);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\boolex\IEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */