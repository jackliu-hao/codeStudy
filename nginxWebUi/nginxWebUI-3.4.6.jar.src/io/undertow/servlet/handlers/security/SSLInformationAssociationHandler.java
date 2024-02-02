/*     */ package io.undertow.servlet.handlers.security;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.HexConverter;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.servlet.ServletRequest;
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
/*     */ public class SSLInformationAssociationHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   
/*     */   public SSLInformationAssociationHandler(HttpHandler next) {
/*  44 */     this.next = next;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getKeyLength(String cipherSuite) {
/*  59 */     return SSLSessionInfo.calculateKeySize(cipherSuite);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private X509Certificate[] getCerts(SSLSessionInfo session) {
/*     */     try {
/*  75 */       Certificate[] javaCerts = session.getPeerCertificates();
/*  76 */       if (javaCerts == null) {
/*  77 */         return null;
/*     */       }
/*  79 */       int x509Certs = 0;
/*  80 */       for (Certificate javaCert : javaCerts) {
/*  81 */         if (javaCert instanceof X509Certificate) {
/*  82 */           x509Certs++;
/*     */         }
/*     */       } 
/*  85 */       if (x509Certs == 0) {
/*  86 */         return null;
/*     */       }
/*  88 */       int resultIndex = 0;
/*  89 */       X509Certificate[] results = new X509Certificate[x509Certs];
/*  90 */       for (Certificate certificate : javaCerts) {
/*  91 */         if (certificate instanceof X509Certificate) {
/*  92 */           results[resultIndex++] = (X509Certificate)certificate;
/*     */         }
/*     */       } 
/*  95 */       return results;
/*  96 */     } catch (Exception e) {
/*  97 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 103 */     ServletRequest request = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletRequest();
/* 104 */     SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
/* 105 */     if (ssl != null) {
/* 106 */       String cipherSuite = ssl.getCipherSuite();
/* 107 */       byte[] sessionId = ssl.getSessionId();
/* 108 */       request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
/* 109 */       request.setAttribute("javax.servlet.request.key_size", Integer.valueOf(ssl.getKeySize()));
/* 110 */       request.setAttribute("javax.servlet.request.ssl_session_id", (sessionId != null) ? HexConverter.convertToHexString(sessionId) : null);
/* 111 */       X509Certificate[] certs = getCerts(ssl);
/* 112 */       if (certs != null) {
/* 113 */         request.setAttribute("javax.servlet.request.X509Certificate", certs);
/*     */       }
/*     */     } 
/*     */     
/* 117 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\SSLInformationAssociationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */