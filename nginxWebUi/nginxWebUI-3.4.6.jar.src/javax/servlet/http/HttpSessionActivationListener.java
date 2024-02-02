package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionActivationListener extends EventListener {
  default void sessionWillPassivate(HttpSessionEvent se) {}
  
  default void sessionDidActivate(HttpSessionEvent se) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionActivationListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */