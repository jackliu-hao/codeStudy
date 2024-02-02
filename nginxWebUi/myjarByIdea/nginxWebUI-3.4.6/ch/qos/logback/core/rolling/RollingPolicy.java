package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.LifeCycle;

public interface RollingPolicy extends LifeCycle {
   void rollover() throws RolloverFailure;

   String getActiveFileName();

   CompressionMode getCompressionMode();

   void setParent(FileAppender<?> var1);
}
