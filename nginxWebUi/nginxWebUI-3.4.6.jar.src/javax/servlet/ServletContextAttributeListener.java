package javax.servlet;

import java.util.EventListener;

public interface ServletContextAttributeListener extends EventListener {
  default void attributeAdded(ServletContextAttributeEvent event) {}
  
  default void attributeRemoved(ServletContextAttributeEvent event) {}
  
  default void attributeReplaced(ServletContextAttributeEvent event) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletContextAttributeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */