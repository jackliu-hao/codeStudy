package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import java.util.Iterator;

public interface AppenderAttachable<E> {
   void addAppender(Appender<E> var1);

   Iterator<Appender<E>> iteratorForAppenders();

   Appender<E> getAppender(String var1);

   boolean isAttached(Appender<E> var1);

   void detachAndStopAllAppenders();

   boolean detachAppender(Appender<E> var1);

   boolean detachAppender(String var1);
}
