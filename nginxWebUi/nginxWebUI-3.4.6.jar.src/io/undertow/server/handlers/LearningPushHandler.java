/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.handlers.cache.LRUCache;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionConfig;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LearningPushHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private static final String SESSION_ATTRIBUTE = "io.undertow.PUSHED_RESOURCES";
/*     */   private static final int DEFAULT_MAX_CACHE_ENTRIES = 1000;
/*     */   private static final int DEFAULT_MAX_CACHE_AGE = -1;
/*     */   private final LRUCache<String, Map<String, PushedRequest>> cache;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public LearningPushHandler(HttpHandler next) {
/*  58 */     this(1000, -1, next);
/*     */   }
/*     */   
/*     */   public LearningPushHandler(int maxEntries, int maxAge, HttpHandler next) {
/*  62 */     this.next = next;
/*  63 */     this.cache = new LRUCache(maxEntries, maxAge);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*     */     String fullPath, requestPath;
/*  70 */     if (exchange.getQueryString().isEmpty()) {
/*  71 */       fullPath = exchange.getRequestURL();
/*  72 */       requestPath = exchange.getRequestPath();
/*     */     } else {
/*  74 */       fullPath = exchange.getRequestURL() + "?" + exchange.getQueryString();
/*  75 */       requestPath = exchange.getRequestPath() + "?" + exchange.getQueryString();
/*     */     } 
/*     */     
/*  78 */     doPush(exchange, fullPath);
/*  79 */     String referrer = exchange.getRequestHeaders().getFirst(Headers.REFERER);
/*  80 */     if (referrer != null) {
/*  81 */       String accept = exchange.getRequestHeaders().getFirst(Headers.ACCEPT);
/*  82 */       if (accept == null || !accept.contains("text/html"))
/*     */       {
/*     */ 
/*     */ 
/*     */         
/*  87 */         exchange.addExchangeCompleteListener(new PushCompletionListener(fullPath, requestPath, referrer));
/*     */       }
/*     */     } 
/*  90 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private void doPush(HttpServerExchange exchange, String fullPath) {
/*  94 */     if (exchange.getConnection().isPushSupported()) {
/*  95 */       Map<String, PushedRequest> toPush = (Map<String, PushedRequest>)this.cache.get(fullPath);
/*  96 */       if (toPush != null) {
/*  97 */         Session session = getSession(exchange);
/*  98 */         if (session == null) {
/*     */           return;
/*     */         }
/* 101 */         Map<String, Object> pushed = (Map<String, Object>)session.getAttribute("io.undertow.PUSHED_RESOURCES");
/* 102 */         if (pushed == null) {
/* 103 */           pushed = Collections.synchronizedMap(new HashMap<>());
/*     */         }
/* 105 */         for (Map.Entry<String, PushedRequest> entry : toPush.entrySet()) {
/* 106 */           PushedRequest request = entry.getValue();
/* 107 */           Object pushedKey = pushed.get(request.getPath());
/* 108 */           boolean doPush = (pushedKey == null);
/* 109 */           if (!doPush) {
/* 110 */             if (pushedKey instanceof String && !pushedKey.equals(request.getEtag())) {
/* 111 */               doPush = true;
/* 112 */             } else if (pushedKey instanceof Long && ((Long)pushedKey).longValue() != request.getLastModified()) {
/* 113 */               doPush = true;
/*     */             } 
/*     */           }
/* 116 */           if (doPush) {
/* 117 */             exchange.getConnection().pushResource(request.getPath(), Methods.GET, request.getRequestHeaders());
/* 118 */             if (request.getEtag() != null) {
/* 119 */               pushed.put(request.getPath(), request.getEtag()); continue;
/*     */             } 
/* 121 */             pushed.put(request.getPath(), Long.valueOf(request.getLastModified()));
/*     */           } 
/*     */         } 
/*     */         
/* 125 */         session.setAttribute("io.undertow.PUSHED_RESOURCES", pushed);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Session getSession(HttpServerExchange exchange) {
/* 132 */     SessionConfig sc = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/* 133 */     SessionManager sm = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/* 134 */     if (sc == null || sm == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     Session session = sm.getSession(exchange, sc);
/* 138 */     if (session == null) {
/* 139 */       return sm.createSession(exchange, sc);
/*     */     }
/* 141 */     return session;
/*     */   }
/*     */   
/*     */   private final class PushCompletionListener
/*     */     implements ExchangeCompletionListener {
/*     */     private final String fullPath;
/*     */     private final String requestPath;
/*     */     private final String referer;
/*     */     
/*     */     private PushCompletionListener(String fullPath, String requestPath, String referer) {
/* 151 */       this.fullPath = fullPath;
/* 152 */       this.requestPath = requestPath;
/* 153 */       this.referer = referer;
/*     */     }
/*     */ 
/*     */     
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 158 */       if (exchange.getStatusCode() == 200 && this.referer != null) {
/*     */         
/* 160 */         String lmString = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
/* 161 */         String etag = exchange.getResponseHeaders().getFirst(Headers.ETAG);
/* 162 */         long lastModified = -1L;
/* 163 */         if (lmString != null) {
/* 164 */           Date dt = DateUtils.parseDate(lmString);
/* 165 */           if (dt != null) {
/* 166 */             lastModified = dt.getTime();
/*     */           }
/*     */         } 
/* 169 */         Map<String, LearningPushHandler.PushedRequest> pushes = (Map<String, LearningPushHandler.PushedRequest>)LearningPushHandler.this.cache.get(this.referer);
/* 170 */         if (pushes == null) {
/* 171 */           synchronized (LearningPushHandler.this.cache) {
/* 172 */             pushes = (Map<String, LearningPushHandler.PushedRequest>)LearningPushHandler.this.cache.get(this.referer);
/* 173 */             if (pushes == null) {
/* 174 */               LearningPushHandler.this.cache.add(this.referer, pushes = Collections.synchronizedMap(new HashMap<>()));
/*     */             }
/*     */           } 
/*     */         }
/* 178 */         pushes.put(this.fullPath, new LearningPushHandler.PushedRequest(new HeaderMap(), this.requestPath, etag, lastModified));
/*     */       } 
/*     */       
/* 181 */       nextListener.proceed();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PushedRequest {
/*     */     private final HeaderMap requestHeaders;
/*     */     private final String path;
/*     */     private final String etag;
/*     */     private final long lastModified;
/*     */     
/*     */     private PushedRequest(HeaderMap requestHeaders, String path, String etag, long lastModified) {
/* 192 */       this.requestHeaders = requestHeaders;
/* 193 */       this.path = path;
/* 194 */       this.etag = etag;
/* 195 */       this.lastModified = lastModified;
/*     */     }
/*     */     
/*     */     public HeaderMap getRequestHeaders() {
/* 199 */       return this.requestHeaders;
/*     */     }
/*     */     
/*     */     public String getPath() {
/* 203 */       return this.path;
/*     */     }
/*     */     
/*     */     public String getEtag() {
/* 207 */       return this.etag;
/*     */     }
/*     */     
/*     */     public long getLastModified() {
/* 211 */       return this.lastModified;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 219 */       return "learning-push";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 224 */       Map<String, Class<?>> params = new HashMap<>();
/* 225 */       params.put("max-age", Integer.class);
/* 226 */       params.put("max-entries", Integer.class);
/* 227 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 232 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 237 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 242 */       final int maxAge = config.containsKey("max-age") ? ((Integer)config.get("max-age")).intValue() : -1;
/* 243 */       final int maxEntries = config.containsKey("max-entries") ? ((Integer)config.get("max-entries")).intValue() : 1000;
/*     */       
/* 245 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 248 */             return new LearningPushHandler(maxEntries, maxAge, handler);
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\LearningPushHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */