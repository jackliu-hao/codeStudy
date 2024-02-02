package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ResponseCommitListener;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import io.undertow.util.SameSiteNoneIncompatibleClientChecker;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SameSiteCookieHandler implements HttpHandler {
   private final HttpHandler next;
   private final String mode;
   private final Pattern cookiePattern;
   private final boolean enableClientChecker;
   private final boolean addSecureForNone;

   public SameSiteCookieHandler(HttpHandler next, String mode) {
      this(next, mode, (String)null, true, true, true);
   }

   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern) {
      this(next, mode, cookiePattern, true, true, true);
   }

   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern, boolean caseSensitive) {
      this(next, mode, cookiePattern, caseSensitive, true, true);
   }

   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern, boolean caseSensitive, boolean enableClientChecker, boolean addSecureForNone) {
      this.next = next;
      this.mode = mode;
      if (cookiePattern != null && !cookiePattern.isEmpty()) {
         this.cookiePattern = Pattern.compile(cookiePattern, caseSensitive ? 0 : 2);
      } else {
         this.cookiePattern = null;
      }

      boolean modeIsNone = CookieSameSiteMode.NONE.toString().equalsIgnoreCase(mode);
      this.enableClientChecker = enableClientChecker && modeIsNone;
      this.addSecureForNone = addSecureForNone && modeIsNone;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (this.mode != null) {
         exchange.addResponseCommitListener(new ResponseCommitListener() {
            public void beforeCommit(HttpServerExchange exchange) {
               String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
               if (!SameSiteCookieHandler.this.enableClientChecker || userAgent == null || SameSiteNoneIncompatibleClientChecker.shouldSendSameSiteNone(userAgent)) {
                  Iterator var3 = exchange.responseCookies().iterator();

                  while(true) {
                     Cookie cookie;
                     do {
                        if (!var3.hasNext()) {
                           return;
                        }

                        cookie = (Cookie)var3.next();
                     } while(SameSiteCookieHandler.this.cookiePattern != null && !SameSiteCookieHandler.this.cookiePattern.matcher(cookie.getName()).matches());

                     cookie.setSameSiteMode(SameSiteCookieHandler.this.mode);
                     if (SameSiteCookieHandler.this.addSecureForNone) {
                        cookie.setSecure(true);
                     }
                  }
               }
            }
         });
      }

      this.next.handleRequest(exchange);
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "samesite-cookie";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> parameters = new HashMap();
         parameters.put("mode", String.class);
         parameters.put("cookie-pattern", String.class);
         parameters.put("case-sensitive", Boolean.class);
         parameters.put("enable-client-checker", Boolean.class);
         parameters.put("add-secure-for-none", Boolean.class);
         return parameters;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("mode");
      }

      public String defaultParameter() {
         return "mode";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         final String mode = (String)config.get("mode");
         final String pattern = (String)config.get("cookie-pattern");
         final Boolean caseSensitive = (Boolean)config.get("case-sensitive");
         final Boolean enableClientChecker = (Boolean)config.get("enable-client-checker");
         final Boolean addSecureForNone = (Boolean)config.get("add-secure-for-none");
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new SameSiteCookieHandler(handler, mode, pattern, caseSensitive == null ? true : caseSensitive, enableClientChecker == null ? true : enableClientChecker, addSecureForNone == null ? true : addSecureForNone);
            }
         };
      }
   }
}
