package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import java.util.Iterator;

public interface AppenderAttachable<E> {
  void addAppender(Appender<E> paramAppender);
  
  Iterator<Appender<E>> iteratorForAppenders();
  
  Appender<E> getAppender(String paramString);
  
  boolean isAttached(Appender<E> paramAppender);
  
  void detachAndStopAllAppenders();
  
  boolean detachAppender(Appender<E> paramAppender);
  
  boolean detachAppender(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\AppenderAttachable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */