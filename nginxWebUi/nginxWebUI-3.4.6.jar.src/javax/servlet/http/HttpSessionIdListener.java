package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionIdListener extends EventListener {
  void sessionIdChanged(HttpSessionEvent paramHttpSessionEvent, String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionIdListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */