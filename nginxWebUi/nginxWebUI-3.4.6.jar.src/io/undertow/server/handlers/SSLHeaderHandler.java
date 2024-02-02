/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.BasicSSLSessionInfo;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Collections;
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
/*     */ public class SSLHeaderHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   public static final String HTTPS = "https";
/*     */   private static final String NULL_VALUE = "(null)";
/*     */   
/*  67 */   private static final ExchangeCompletionListener CLEAR_SSL_LISTENER = new ExchangeCompletionListener()
/*     */     {
/*     */       public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*  70 */         exchange.getConnection().setSslSessionInfo(null);
/*  71 */         nextListener.proceed();
/*     */       }
/*     */     };
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   public SSLHeaderHandler(HttpHandler next) {
/*  78 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  83 */     HeaderMap requestHeaders = exchange.getRequestHeaders();
/*  84 */     String sessionId = requestHeaders.getFirst(Headers.SSL_SESSION_ID);
/*  85 */     String cipher = requestHeaders.getFirst(Headers.SSL_CIPHER);
/*  86 */     String clientCert = requestHeaders.getFirst(Headers.SSL_CLIENT_CERT);
/*  87 */     String keySizeStr = requestHeaders.getFirst(Headers.SSL_CIPHER_USEKEYSIZE);
/*  88 */     Integer keySize = null;
/*  89 */     if (keySizeStr != null) {
/*     */       try {
/*  91 */         keySize = Integer.valueOf(Integer.parseUnsignedInt(keySizeStr));
/*  92 */       } catch (NumberFormatException e) {
/*  93 */         UndertowLogger.REQUEST_LOGGER.debugf("Invalid SSL_CIPHER_USEKEYSIZE header %s", keySizeStr);
/*     */       } 
/*     */     }
/*  96 */     if (clientCert != null || sessionId != null || cipher != null) {
/*  97 */       if (clientCert != null) {
/*  98 */         if (clientCert.isEmpty() || clientCert.equals("(null)")) {
/*     */           
/* 100 */           clientCert = null;
/* 101 */         } else if (clientCert.length() > 54) {
/*     */           
/* 103 */           StringBuilder sb = new StringBuilder(clientCert.length() + 1);
/* 104 */           sb.append("-----BEGIN CERTIFICATE-----");
/* 105 */           sb.append('\n');
/* 106 */           sb.append(clientCert.replace(' ', '\n').substring(28, clientCert.length() - 26));
/* 107 */           sb.append('\n');
/* 108 */           sb.append("-----END CERTIFICATE-----");
/* 109 */           clientCert = sb.toString();
/*     */         } 
/*     */       }
/*     */       try {
/* 113 */         BasicSSLSessionInfo basicSSLSessionInfo = new BasicSSLSessionInfo(sessionId, cipher, clientCert, keySize);
/* 114 */         exchange.setRequestScheme("https");
/* 115 */         exchange.getConnection().setSslSessionInfo((SSLSessionInfo)basicSSLSessionInfo);
/* 116 */         exchange.addExchangeCompleteListener(CLEAR_SSL_LISTENER);
/* 117 */       } catch (CertificateException|javax.security.cert.CertificateException e) {
/* 118 */         UndertowLogger.REQUEST_LOGGER.debugf(e, "Could not create certificate from header %s", clientCert);
/*     */       } 
/*     */     } 
/* 121 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 126 */     return "ssl-headers()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 134 */       return "ssl-headers";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 139 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 144 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 149 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 154 */       return new SSLHeaderHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 162 */       return new SSLHeaderHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SSLHeaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */