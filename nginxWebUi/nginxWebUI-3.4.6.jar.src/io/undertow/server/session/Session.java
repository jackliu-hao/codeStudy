package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;
import java.util.Set;

public interface Session {
  String getId();
  
  void requestDone(HttpServerExchange paramHttpServerExchange);
  
  long getCreationTime();
  
  long getLastAccessedTime();
  
  void setMaxInactiveInterval(int paramInt);
  
  int getMaxInactiveInterval();
  
  Object getAttribute(String paramString);
  
  Set<String> getAttributeNames();
  
  Object setAttribute(String paramString, Object paramObject);
  
  Object removeAttribute(String paramString);
  
  void invalidate(HttpServerExchange paramHttpServerExchange);
  
  SessionManager getSessionManager();
  
  String changeSessionId(HttpServerExchange paramHttpServerExchange, SessionConfig paramSessionConfig);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */