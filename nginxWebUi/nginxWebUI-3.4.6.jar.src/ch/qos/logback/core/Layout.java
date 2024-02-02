package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Layout<E> extends ContextAware, LifeCycle {
  String doLayout(E paramE);
  
  String getFileHeader();
  
  String getPresentationHeader();
  
  String getPresentationFooter();
  
  String getFileFooter();
  
  String getContentType();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\Layout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */