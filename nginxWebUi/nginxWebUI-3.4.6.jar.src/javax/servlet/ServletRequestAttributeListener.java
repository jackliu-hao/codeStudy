package javax.servlet;

import java.util.EventListener;

public interface ServletRequestAttributeListener extends EventListener {
  default void attributeAdded(ServletRequestAttributeEvent srae) {}
  
  default void attributeRemoved(ServletRequestAttributeEvent srae) {}
  
  default void attributeReplaced(ServletRequestAttributeEvent srae) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRequestAttributeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */