/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.HexConverter;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.util.Collection;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.security.cert.CertificateException;
/*     */ import javax.security.cert.X509Certificate;
/*     */ import org.xnio.SslClientAuthMode;
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
/*     */ public class BasicSSLSessionInfo
/*     */   implements SSLSessionInfo
/*     */ {
/*     */   private final byte[] sessionId;
/*     */   private final String cypherSuite;
/*     */   private final Certificate[] peerCertificate;
/*     */   private final X509Certificate[] certificate;
/*     */   private final Integer keySize;
/*     */   
/*     */   public BasicSSLSessionInfo(byte[] sessionId, String cypherSuite, String certificate, Integer keySize) throws CertificateException, CertificateException {
/*  57 */     this.sessionId = sessionId;
/*  58 */     this.cypherSuite = cypherSuite;
/*  59 */     this.keySize = keySize;
/*  60 */     if (certificate != null) {
/*  61 */       CertificateFactory cf = CertificateFactory.getInstance("X.509");
/*  62 */       byte[] certificateBytes = certificate.getBytes(StandardCharsets.US_ASCII);
/*  63 */       ByteArrayInputStream stream = new ByteArrayInputStream(certificateBytes);
/*  64 */       Collection<? extends Certificate> certCol = cf.generateCertificates(stream);
/*  65 */       this.peerCertificate = new Certificate[certCol.size()];
/*  66 */       X509Certificate[] legacyCertificate = new X509Certificate[certCol.size()];
/*  67 */       int i = 0;
/*  68 */       for (Certificate cert : certCol) {
/*  69 */         this.peerCertificate[i] = cert;
/*  70 */         if (legacyCertificate != null) {
/*     */           try {
/*  72 */             legacyCertificate[i] = X509Certificate.getInstance(cert.getEncoded());
/*  73 */           } catch (CertificateException ce) {
/*     */ 
/*     */ 
/*     */             
/*  77 */             legacyCertificate = null;
/*     */           } 
/*     */         }
/*  80 */         i++;
/*     */       } 
/*  82 */       this.certificate = legacyCertificate;
/*     */     } else {
/*  84 */       this.peerCertificate = null;
/*  85 */       this.certificate = null;
/*     */     } 
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
/*     */   public BasicSSLSessionInfo(byte[] sessionId, String cypherSuite, String certificate) throws CertificateException, CertificateException {
/*  98 */     this(sessionId, cypherSuite, certificate, (Integer)null);
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
/*     */   public BasicSSLSessionInfo(String sessionId, String cypherSuite, String certificate) throws CertificateException, CertificateException {
/* 110 */     this((sessionId == null) ? null : fromHex(sessionId), cypherSuite, certificate, (Integer)null);
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
/*     */   public BasicSSLSessionInfo(String sessionId, String cypherSuite, String certificate, Integer keySize) throws CertificateException, CertificateException {
/* 123 */     this((sessionId == null) ? null : fromHex(sessionId), cypherSuite, certificate, keySize);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSessionId() {
/* 128 */     if (this.sessionId == null) {
/* 129 */       return null;
/*     */     }
/* 131 */     byte[] copy = new byte[this.sessionId.length];
/* 132 */     System.arraycopy(this.sessionId, 0, copy, 0, copy.length);
/* 133 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCipherSuite() {
/* 138 */     return this.cypherSuite;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getKeySize() {
/* 143 */     if (this.keySize != null) {
/* 144 */       return this.keySize.intValue();
/*     */     }
/* 146 */     return super.getKeySize();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
/* 152 */     if (this.peerCertificate == null) {
/* 153 */       throw UndertowMessages.MESSAGES.peerUnverified();
/*     */     }
/* 155 */     return this.peerCertificate;
/*     */   }
/*     */ 
/*     */   
/*     */   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
/* 160 */     if (this.certificate == null) {
/* 161 */       throw UndertowMessages.MESSAGES.peerUnverified();
/*     */     }
/* 163 */     return this.certificate;
/*     */   }
/*     */ 
/*     */   
/*     */   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
/* 168 */     throw UndertowMessages.MESSAGES.renegotiationNotSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 173 */     return null;
/*     */   }
/*     */   
/*     */   private static byte[] fromHex(String sessionId) {
/*     */     try {
/* 178 */       return HexConverter.convertFromHex(sessionId);
/* 179 */     } catch (Exception e) {
/*     */       
/* 181 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\BasicSSLSessionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */