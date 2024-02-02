package io.undertow.server.handlers;

import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.handlers.cache.LRUCache;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LearningPushHandler implements HttpHandler {
   private static final String SESSION_ATTRIBUTE = "io.undertow.PUSHED_RESOURCES";
   private static final int DEFAULT_MAX_CACHE_ENTRIES = 1000;
   private static final int DEFAULT_MAX_CACHE_AGE = -1;
   private final LRUCache<String, Map<String, PushedRequest>> cache;
   private final HttpHandler next;

   public LearningPushHandler(HttpHandler next) {
      this(1000, -1, next);
   }

   public LearningPushHandler(int maxEntries, int maxAge, HttpHandler next) {
      this.next = next;
      this.cache = new LRUCache(maxEntries, maxAge);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String fullPath;
      String requestPath;
      if (exchange.getQueryString().isEmpty()) {
         fullPath = exchange.getRequestURL();
         requestPath = exchange.getRequestPath();
      } else {
         fullPath = exchange.getRequestURL() + "?" + exchange.getQueryString();
         requestPath = exchange.getRequestPath() + "?" + exchange.getQueryString();
      }

      this.doPush(exchange, fullPath);
      String referrer = exchange.getRequestHeaders().getFirst(Headers.REFERER);
      if (referrer != null) {
         String accept = exchange.getRequestHeaders().getFirst(Headers.ACCEPT);
         if (accept == null || !accept.contains("text/html")) {
            exchange.addExchangeCompleteListener(new PushCompletionListener(fullPath, requestPath, referrer));
         }
      }

      this.next.handleRequest(exchange);
   }

   private void doPush(HttpServerExchange exchange, String fullPath) {
      if (exchange.getConnection().isPushSupported()) {
         Map<String, PushedRequest> toPush = (Map)this.cache.get(fullPath);
         if (toPush != null) {
            Session session = this.getSession(exchange);
            if (session == null) {
               return;
            }

            Map<String, Object> pushed = (Map)session.getAttribute("io.undertow.PUSHED_RESOURCES");
            if (pushed == null) {
               pushed = Collections.synchronizedMap(new HashMap());
            }

            Iterator var6 = toPush.entrySet().iterator();

            while(var6.hasNext()) {
               Map.Entry<String, PushedRequest> entry = (Map.Entry)var6.next();
               PushedRequest request = (PushedRequest)entry.getValue();
               Object pushedKey = pushed.get(request.getPath());
               boolean doPush = pushedKey == null;
               if (!doPush) {
                  if (pushedKey instanceof String && !pushedKey.equals(request.getEtag())) {
                     doPush = true;
                  } else if (pushedKey instanceof Long && (Long)pushedKey != request.getLastModified()) {
                     doPush = true;
                  }
               }

               if (doPush) {
                  exchange.getConnection().pushResource(request.getPath(), Methods.GET, request.getRequestHeaders());
                  if (request.getEtag() != null) {
                     pushed.put(request.getPath(), request.getEtag());
                  } else {
                     pushed.put(request.getPath(), request.getLastModified());
                  }
               }
            }

            session.setAttribute("io.undertow.PUSHED_RESOURCES", pushed);
         }
      }

   }

   protected Session getSession(HttpServerExchange exchange) {
      SessionConfig sc = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
      SessionManager sm = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
      if (sc != null && sm != null) {
         Session session = sm.getSession(exchange, sc);
         return session == null ? sm.createSession(exchange, sc) : session;
      } else {
         return null;
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "learning-push";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("max-age", Integer.class);
         params.put("max-entries", Integer.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return null;
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         final int maxAge = config.containsKey("max-age") ? (Integer)config.get("max-age") : -1;
         final int maxEntries = config.containsKey("max-entries") ? (Integer)config.get("max-entries") : 1000;
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new LearningPushHandler(maxEntries, maxAge, handler);
            }
         };
      }
   }

   private static class PushedRequest {
      private final HeaderMap requestHeaders;
      private final String path;
      private final String etag;
      private final long lastModified;

      private PushedRequest(HeaderMap requestHeaders, String path, String etag, long lastModified) {
         this.requestHeaders = requestHeaders;
         this.path = path;
         this.etag = etag;
         this.lastModified = lastModified;
      }

      public HeaderMap getRequestHeaders() {
         return this.requestHeaders;
      }

      public String getPath() {
         return this.path;
      }

      public String getEtag() {
         return this.etag;
      }

      public long getLastModified() {
         return this.lastModified;
      }

      // $FF: synthetic method
      PushedRequest(HeaderMap x0, String x1, String x2, long x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   private final class PushCompletionListener implements ExchangeCompletionListener {
      private final String fullPath;
      private final String requestPath;
      private final String referer;

      private PushCompletionListener(String fullPath, String requestPath, String referer) {
         this.fullPath = fullPath;
         this.requestPath = requestPath;
         this.referer = referer;
      }

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         if (exchange.getStatusCode() == 200 && this.referer != null) {
            String lmString = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
            String etag = exchange.getResponseHeaders().getFirst(Headers.ETAG);
            long lastModified = -1L;
            if (lmString != null) {
               Date dt = DateUtils.parseDate(lmString);
               if (dt != null) {
                  lastModified = dt.getTime();
               }
            }

            Map<String, PushedRequest> pushes = (Map)LearningPushHandler.this.cache.get(this.referer);
            if (pushes == null) {
               synchronized(LearningPushHandler.this.cache) {
                  pushes = (Map)LearningPushHandler.this.cache.get(this.referer);
                  if (pushes == null) {
                     LearningPushHandler.this.cache.add(this.referer, pushes = Collections.synchronizedMap(new HashMap()));
                  }
               }
            }

            pushes.put(this.fullPath, new PushedRequest(new HeaderMap(), this.requestPath, etag, lastModified));
         }

         nextListener.proceed();
      }

      // $FF: synthetic method
      PushCompletionListener(String x1, String x2, String x3, Object x4) {
         this(x1, x2, x3);
      }
   }
}
