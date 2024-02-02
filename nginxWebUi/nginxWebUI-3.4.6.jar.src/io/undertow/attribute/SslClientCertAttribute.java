/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.SSLSessionInfo;
/*    */ import io.undertow.util.Certificates;
/*    */ import java.security.cert.Certificate;
/*    */ import javax.net.ssl.SSLPeerUnverifiedException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SslClientCertAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/* 35 */   public static final SslClientCertAttribute INSTANCE = new SslClientCertAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 39 */     SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
/* 40 */     if (ssl == null) {
/* 41 */       return null;
/*    */     }
/*    */     
/*    */     try {
/* 45 */       Certificate[] certificates = ssl.getPeerCertificates();
/* 46 */       if (certificates.length > 0) {
/* 47 */         return Certificates.toPem(certificates[0]);
/*    */       }
/* 49 */       return null;
/* 50 */     } catch (SSLPeerUnverifiedException|java.security.cert.CertificateEncodingException|io.undertow.server.RenegotiationRequiredException e) {
/*    */ 
/*    */       
/* 53 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 59 */     throw new ReadOnlyAttributeException("SSL Client Cert", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "%{SSL_CLIENT_CERT}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 71 */       return "SSL Client Cert";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 76 */       if (token.equals("%{SSL_CLIENT_CERT}")) {
/* 77 */         return SslClientCertAttribute.INSTANCE;
/*    */       }
/* 79 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 84 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\SslClientCertAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */