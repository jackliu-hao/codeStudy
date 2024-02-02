package io.undertow.server.session;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import java.util.Deque;
import java.util.Locale;

public class PathParameterSessionConfig implements SessionConfig {
   private final String name;

   public PathParameterSessionConfig(String name) {
      this.name = name;
   }

   public PathParameterSessionConfig() {
      this("JSESSIONID".toLowerCase(Locale.ENGLISH));
   }

   public void setSessionId(HttpServerExchange exchange, String sessionId) {
      exchange.getPathParameters().remove(this.name);
      exchange.addPathParam(this.name, sessionId);
      UndertowLogger.SESSION_LOGGER.tracef("Setting path parameter session id %s on %s", sessionId, exchange);
   }

   public void clearSession(HttpServerExchange exchange, String sessionId) {
      UndertowLogger.SESSION_LOGGER.tracef("Clearing path parameter session id %s on %s", sessionId, exchange);
      exchange.getPathParameters().remove(this.name);
   }

   public String findSessionId(HttpServerExchange exchange) {
      Deque<String> stringDeque = (Deque)exchange.getPathParameters().get(this.name);
      if (stringDeque == null) {
         return null;
      } else {
         UndertowLogger.SESSION_LOGGER.tracef("Found path parameter session id %s on %s", stringDeque.getFirst(), exchange);
         return (String)stringDeque.getFirst();
      }
   }

   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
      return this.findSessionId(exchange) != null ? SessionConfig.SessionCookieSource.URL : SessionConfig.SessionCookieSource.NONE;
   }

   public String rewriteUrl(String url, String sessionId) {
      if (url != null && sessionId != null) {
         String path = url;
         String query = "";
         String anchor = "";
         int question = url.indexOf(63);
         if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
         }

         int pound = path.indexOf(35);
         if (pound >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
         }

         StringBuilder sb = new StringBuilder();
         int paramIndex = path.indexOf(";" + this.name);
         if (paramIndex >= 0) {
            sb.append(path.substring(0, paramIndex));
            String remainder = path.substring(paramIndex + this.name.length() + 1);
            int endIndex1 = remainder.indexOf(";");
            int endIndex2 = remainder.indexOf("/");
            if (endIndex1 != -1) {
               if (endIndex2 != -1 && endIndex2 < endIndex1) {
                  sb.append(remainder.substring(endIndex2));
               } else {
                  sb.append(remainder.substring(endIndex1));
               }
            } else if (endIndex2 != -1) {
               sb.append(remainder.substring(endIndex2));
            }
         } else {
            sb.append(path);
         }

         sb.append(';');
         sb.append(this.name);
         sb.append('=');
         sb.append(sessionId);
         sb.append(anchor);
         sb.append(query);
         UndertowLogger.SESSION_LOGGER.tracef("Rewrote URL from %s to %s", url, sb);
         return sb.toString();
      } else {
         return url;
      }
   }
}
