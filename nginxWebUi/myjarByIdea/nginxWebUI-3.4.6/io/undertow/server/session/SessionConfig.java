package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

public interface SessionConfig {
   AttachmentKey<SessionConfig> ATTACHMENT_KEY = AttachmentKey.create(SessionConfig.class);

   void setSessionId(HttpServerExchange var1, String var2);

   void clearSession(HttpServerExchange var1, String var2);

   String findSessionId(HttpServerExchange var1);

   SessionCookieSource sessionCookieSource(HttpServerExchange var1);

   String rewriteUrl(String var1, String var2);

   public static enum SessionCookieSource {
      URL,
      COOKIE,
      SSL,
      OTHER,
      NONE;
   }
}
