package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.LifeCycle;

public interface RollingPolicy extends LifeCycle {
  void rollover() throws RolloverFailure;
  
  String getActiveFileName();
  
  CompressionMode getCompressionMode();
  
  void setParent(FileAppender<?> paramFileAppender);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\RollingPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */