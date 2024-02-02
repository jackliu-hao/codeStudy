/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.Deque;
/*     */ import java.util.Locale;
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
/*     */ public class PathParameterSessionConfig
/*     */   implements SessionConfig
/*     */ {
/*     */   private final String name;
/*     */   
/*     */   public PathParameterSessionConfig(String name) {
/*  37 */     this.name = name;
/*     */   }
/*     */   
/*     */   public PathParameterSessionConfig() {
/*  41 */     this("JSESSIONID".toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionId(HttpServerExchange exchange, String sessionId) {
/*  46 */     exchange.getPathParameters().remove(this.name);
/*  47 */     exchange.addPathParam(this.name, sessionId);
/*  48 */     UndertowLogger.SESSION_LOGGER.tracef("Setting path parameter session id %s on %s", sessionId, exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearSession(HttpServerExchange exchange, String sessionId) {
/*  53 */     UndertowLogger.SESSION_LOGGER.tracef("Clearing path parameter session id %s on %s", sessionId, exchange);
/*  54 */     exchange.getPathParameters().remove(this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String findSessionId(HttpServerExchange exchange) {
/*  59 */     Deque<String> stringDeque = (Deque<String>)exchange.getPathParameters().get(this.name);
/*  60 */     if (stringDeque == null) {
/*  61 */       return null;
/*     */     }
/*  63 */     UndertowLogger.SESSION_LOGGER.tracef("Found path parameter session id %s on %s", stringDeque.getFirst(), exchange);
/*  64 */     return stringDeque.getFirst();
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/*  69 */     return (findSessionId(exchange) != null) ? SessionConfig.SessionCookieSource.URL : SessionConfig.SessionCookieSource.NONE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String rewriteUrl(String url, String sessionId) {
/*  81 */     if (url == null || sessionId == null) {
/*  82 */       return url;
/*     */     }
/*  84 */     String path = url;
/*  85 */     String query = "";
/*  86 */     String anchor = "";
/*  87 */     int question = url.indexOf('?');
/*  88 */     if (question >= 0) {
/*  89 */       path = url.substring(0, question);
/*  90 */       query = url.substring(question);
/*     */     } 
/*  92 */     int pound = path.indexOf('#');
/*  93 */     if (pound >= 0) {
/*  94 */       anchor = path.substring(pound);
/*  95 */       path = path.substring(0, pound);
/*     */     } 
/*  97 */     StringBuilder sb = new StringBuilder();
/*     */     
/*  99 */     int paramIndex = path.indexOf(";" + this.name);
/*     */     
/* 101 */     if (paramIndex >= 0) {
/* 102 */       sb.append(path.substring(0, paramIndex));
/* 103 */       String remainder = path.substring(paramIndex + this.name.length() + 1);
/* 104 */       int endIndex1 = remainder.indexOf(";");
/* 105 */       int endIndex2 = remainder.indexOf("/");
/* 106 */       if (endIndex1 != -1) {
/* 107 */         if (endIndex2 != -1 && endIndex2 < endIndex1) {
/* 108 */           sb.append(remainder.substring(endIndex2));
/*     */         } else {
/* 110 */           sb.append(remainder.substring(endIndex1));
/*     */         } 
/* 112 */       } else if (endIndex2 != -1) {
/* 113 */         sb.append(remainder.substring(endIndex2));
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 118 */       sb.append(path);
/*     */     } 
/*     */     
/* 121 */     sb.append(';');
/* 122 */     sb.append(this.name);
/* 123 */     sb.append('=');
/* 124 */     sb.append(sessionId);
/*     */     
/* 126 */     sb.append(anchor);
/* 127 */     sb.append(query);
/* 128 */     UndertowLogger.SESSION_LOGGER.tracef("Rewrote URL from %s to %s", url, sb);
/* 129 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\PathParameterSessionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */