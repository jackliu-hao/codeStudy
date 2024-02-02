package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;
import java.util.Set;

public interface Session {
   String getId();

   void requestDone(HttpServerExchange var1);

   long getCreationTime();

   long getLastAccessedTime();

   void setMaxInactiveInterval(int var1);

   int getMaxInactiveInterval();

   Object getAttribute(String var1);

   Set<String> getAttributeNames();

   Object setAttribute(String var1, Object var2);

   Object removeAttribute(String var1);

   void invalidate(HttpServerExchange var1);

   SessionManager getSessionManager();

   String changeSessionId(HttpServerExchange var1, SessionConfig var2);
}
