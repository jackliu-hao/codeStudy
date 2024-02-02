/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ public class OriginHandler
/*     */   implements HttpHandler
/*     */ {
/*  41 */   private volatile HttpHandler originFailedHandler = ResponseCodeHandler.HANDLE_403;
/*  42 */   private volatile Set<String> allowedOrigins = new HashSet<>();
/*     */   private volatile boolean requireAllOrigins = true;
/*     */   private volatile boolean requireOriginHeader = true;
/*  45 */   private volatile HttpHandler next = ResponseCodeHandler.HANDLE_404;
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  50 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.ORIGIN);
/*  51 */     if (headerValues == null) {
/*  52 */       if (this.requireOriginHeader) {
/*     */         
/*  54 */         if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/*  55 */           UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s due to lack of Origin: header", exchange.getRequestPath());
/*     */         }
/*  57 */         this.originFailedHandler.handleRequest(exchange);
/*     */         return;
/*     */       } 
/*     */     } else {
/*  61 */       boolean found = false;
/*  62 */       boolean requireAllOrigins = this.requireAllOrigins;
/*  63 */       for (String header : headerValues) {
/*  64 */         if (this.allowedOrigins.contains(header)) {
/*  65 */           found = true;
/*  66 */           if (!requireAllOrigins)
/*     */             break;  continue;
/*     */         } 
/*  69 */         if (requireAllOrigins) {
/*  70 */           if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/*  71 */             UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s due to Origin %s not being in the allowed origins list", exchange.getRequestPath(), header);
/*     */           }
/*  73 */           this.originFailedHandler.handleRequest(exchange);
/*     */           return;
/*     */         } 
/*     */       } 
/*  77 */       if (!found) {
/*  78 */         if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/*  79 */           UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s as none of the specified origins %s were in the allowed origins list", exchange.getRequestPath(), headerValues);
/*     */         }
/*  81 */         this.originFailedHandler.handleRequest(exchange);
/*     */         return;
/*     */       } 
/*     */     } 
/*  85 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public synchronized OriginHandler addAllowedOrigin(String origin) {
/*  89 */     Set<String> allowedOrigins = new HashSet<>(this.allowedOrigins);
/*  90 */     allowedOrigins.add(origin);
/*  91 */     this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
/*  92 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized OriginHandler addAllowedOrigins(Collection<String> origins) {
/*  96 */     Set<String> allowedOrigins = new HashSet<>(this.allowedOrigins);
/*  97 */     allowedOrigins.addAll(origins);
/*  98 */     this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
/*  99 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized OriginHandler addAllowedOrigins(String... origins) {
/* 103 */     Set<String> allowedOrigins = new HashSet<>(this.allowedOrigins);
/* 104 */     allowedOrigins.addAll(Arrays.asList(origins));
/* 105 */     this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
/* 106 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized Set<String> getAllowedOrigins() {
/* 110 */     return this.allowedOrigins;
/*     */   }
/*     */   
/*     */   public synchronized OriginHandler clearAllowedOrigins() {
/* 114 */     this.allowedOrigins = Collections.emptySet();
/* 115 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isRequireAllOrigins() {
/* 119 */     return this.requireAllOrigins;
/*     */   }
/*     */   
/*     */   public OriginHandler setRequireAllOrigins(boolean requireAllOrigins) {
/* 123 */     this.requireAllOrigins = requireAllOrigins;
/* 124 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isRequireOriginHeader() {
/* 128 */     return this.requireOriginHeader;
/*     */   }
/*     */   
/*     */   public OriginHandler setRequireOriginHeader(boolean requireOriginHeader) {
/* 132 */     this.requireOriginHeader = requireOriginHeader;
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/* 137 */     return this.next;
/*     */   }
/*     */   
/*     */   public OriginHandler setNext(HttpHandler next) {
/* 141 */     Handlers.handlerNotNull(next);
/* 142 */     this.next = next;
/* 143 */     return this;
/*     */   }
/*     */   
/*     */   public HttpHandler getOriginFailedHandler() {
/* 147 */     return this.originFailedHandler;
/*     */   }
/*     */   
/*     */   public OriginHandler setOriginFailedHandler(HttpHandler originFailedHandler) {
/* 151 */     Handlers.handlerNotNull(originFailedHandler);
/* 152 */     this.originFailedHandler = originFailedHandler;
/* 153 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\OriginHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */