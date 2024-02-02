package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionAttributeListener extends EventListener {
  default void attributeAdded(HttpSessionBindingEvent event) {}
  
  default void attributeRemoved(HttpSessionBindingEvent event) {}
  
  default void attributeReplaced(HttpSessionBindingEvent event) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionAttributeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */