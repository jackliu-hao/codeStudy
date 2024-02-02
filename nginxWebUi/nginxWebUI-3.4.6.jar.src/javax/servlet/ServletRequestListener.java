package javax.servlet;

import java.util.EventListener;

public interface ServletRequestListener extends EventListener {
  default void requestDestroyed(ServletRequestEvent sre) {}
  
  default void requestInitialized(ServletRequestEvent sre) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRequestListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */