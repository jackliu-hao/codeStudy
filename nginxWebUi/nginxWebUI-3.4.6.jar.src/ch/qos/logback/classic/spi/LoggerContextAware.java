package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

public interface LoggerContextAware extends ContextAware {
  void setLoggerContext(LoggerContext paramLoggerContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\LoggerContextAware.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */