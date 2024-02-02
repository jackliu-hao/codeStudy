package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.util.Set;

public interface SessionManager {
   AttachmentKey<SessionManager> ATTACHMENT_KEY = AttachmentKey.create(SessionManager.class);

   String getDeploymentName();

   void start();

   void stop();

   Session createSession(HttpServerExchange var1, SessionConfig var2);

   Session getSession(HttpServerExchange var1, SessionConfig var2);

   Session getSession(String var1);

   void registerSessionListener(SessionListener var1);

   void removeSessionListener(SessionListener var1);

   void setDefaultSessionTimeout(int var1);

   Set<String> getTransientSessions();

   Set<String> getActiveSessions();

   Set<String> getAllSessions();

   SessionManagerStatistics getStatistics();
}
