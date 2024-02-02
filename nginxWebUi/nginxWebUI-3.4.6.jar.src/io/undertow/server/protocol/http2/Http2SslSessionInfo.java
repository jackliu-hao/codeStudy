/*    */ package io.undertow.server.protocol.http2;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import io.undertow.protocols.http2.Http2Channel;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.RenegotiationRequiredException;
/*    */ import io.undertow.server.SSLSessionInfo;
/*    */ import java.io.IOException;
/*    */ import java.security.cert.Certificate;
/*    */ import javax.net.ssl.SSLPeerUnverifiedException;
/*    */ import javax.net.ssl.SSLSession;
/*    */ import javax.security.cert.X509Certificate;
/*    */ import org.xnio.Options;
/*    */ import org.xnio.SslClientAuthMode;
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
/*    */ class Http2SslSessionInfo
/*    */   implements SSLSessionInfo
/*    */ {
/*    */   private final Http2Channel channel;
/*    */   
/*    */   Http2SslSessionInfo(Http2Channel channel) {
/* 43 */     this.channel = channel;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getSessionId() {
/* 48 */     return this.channel.getSslSession().getId();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCipherSuite() {
/* 53 */     return this.channel.getSslSession().getCipherSuite();
/*    */   }
/*    */ 
/*    */   
/*    */   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
/*    */     try {
/* 59 */       return this.channel.getSslSession().getPeerCertificates();
/* 60 */     } catch (SSLPeerUnverifiedException e) {
/*    */       try {
/* 62 */         SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
/* 63 */         if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
/* 64 */           throw new RenegotiationRequiredException();
/*    */         }
/* 66 */       } catch (IOException iOException) {}
/*    */ 
/*    */       
/* 69 */       throw e;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
/*    */     try {
/* 76 */       return this.channel.getSslSession().getPeerCertificateChain();
/* 77 */     } catch (SSLPeerUnverifiedException e) {
/*    */       try {
/* 79 */         SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
/* 80 */         if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
/* 81 */           throw new RenegotiationRequiredException();
/*    */         }
/* 83 */       } catch (IOException iOException) {}
/*    */ 
/*    */       
/* 86 */       throw e;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
/* 91 */     throw UndertowMessages.MESSAGES.renegotiationNotSupported();
/*    */   }
/*    */ 
/*    */   
/*    */   public SSLSession getSSLSession() {
/* 96 */     return this.channel.getSslSession();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http2\Http2SslSessionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */