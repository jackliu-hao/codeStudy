package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionListener extends EventListener {
  default void sessionCreated(HttpSessionEvent se) {}
  
  default void sessionDestroyed(HttpSessionEvent se) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */