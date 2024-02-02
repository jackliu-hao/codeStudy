package io.undertow.servlet.api;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public interface SessionPersistenceManager {
   void persistSessions(String var1, Map<String, PersistentSession> var2);

   Map<String, PersistentSession> loadSessionAttributes(String var1, ClassLoader var2);

   void clear(String var1);

   public static class PersistentSession {
      private final Date expiration;
      private final Map<String, Object> sessionData;

      public PersistentSession(Date expiration, Map<String, Object> sessionData) {
         this.expiration = expiration;
         this.sessionData = sessionData;
      }

      public Date getExpiration() {
         return this.expiration;
      }

      public Map<String, Object> getSessionData() {
         return Collections.unmodifiableMap(this.sessionData);
      }
   }
}
