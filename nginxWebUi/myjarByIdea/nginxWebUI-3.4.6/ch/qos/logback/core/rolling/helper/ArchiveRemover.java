package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAware;
import java.util.Date;
import java.util.concurrent.Future;

public interface ArchiveRemover extends ContextAware {
   void clean(Date var1);

   void setMaxHistory(int var1);

   void setTotalSizeCap(long var1);

   Future<?> cleanAsynchronously(Date var1);
}
