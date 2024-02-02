package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionBindingListener extends EventListener {
  default void valueBound(HttpSessionBindingEvent event) {}
  
  default void valueUnbound(HttpSessionBindingEvent event) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionBindingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */