package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.spi.ContextAware;

public interface TimeBasedFileNamingAndTriggeringPolicy<E> extends TriggeringPolicy<E>, ContextAware {
  void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> paramTimeBasedRollingPolicy);
  
  String getElapsedPeriodsFileName();
  
  String getCurrentPeriodsFileNameWithoutCompressionSuffix();
  
  ArchiveRemover getArchiveRemover();
  
  long getCurrentTime();
  
  void setCurrentTime(long paramLong);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\TimeBasedFileNamingAndTriggeringPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */