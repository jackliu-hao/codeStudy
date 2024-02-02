/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
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
/*     */ 
/*     */ 
/*     */ public class MailSSLSocketFactory
/*     */   extends SSLSocketFactory
/*     */ {
/*     */   private boolean trustAllHosts;
/*  71 */   private String[] trustedHosts = null;
/*     */ 
/*     */   
/*     */   private SSLContext sslcontext;
/*     */ 
/*     */   
/*     */   private KeyManager[] keyManagers;
/*     */ 
/*     */   
/*     */   private TrustManager[] trustManagers;
/*     */ 
/*     */   
/*     */   private SecureRandom secureRandom;
/*     */ 
/*     */   
/*  86 */   private SSLSocketFactory adapteeFactory = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailSSLSocketFactory() throws GeneralSecurityException {
/*  94 */     this("TLS");
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
/*     */   public MailSSLSocketFactory(String protocol) throws GeneralSecurityException {
/* 108 */     this.trustAllHosts = false;
/*     */ 
/*     */     
/* 111 */     this.sslcontext = SSLContext.getInstance(protocol);
/*     */ 
/*     */     
/* 114 */     this.keyManagers = null;
/* 115 */     this.trustManagers = new TrustManager[] { new MailTrustManager() };
/* 116 */     this.secureRandom = null;
/*     */ 
/*     */     
/* 119 */     newAdapteeFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void newAdapteeFactory() throws KeyManagementException {
/* 130 */     this.sslcontext.init(this.keyManagers, this.trustManagers, this.secureRandom);
/*     */ 
/*     */     
/* 133 */     this.adapteeFactory = this.sslcontext.getSocketFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized KeyManager[] getKeyManagers() {
/* 140 */     return (KeyManager[])this.keyManagers.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setKeyManagers(KeyManager[] keyManagers) throws GeneralSecurityException {
/* 148 */     this.keyManagers = (KeyManager[])keyManagers.clone();
/* 149 */     newAdapteeFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized SecureRandom getSecureRandom() {
/* 156 */     return this.secureRandom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setSecureRandom(SecureRandom secureRandom) throws GeneralSecurityException {
/* 164 */     this.secureRandom = secureRandom;
/* 165 */     newAdapteeFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized TrustManager[] getTrustManagers() {
/* 172 */     return this.trustManagers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setTrustManagers(TrustManager[] trustManagers) throws GeneralSecurityException {
/* 180 */     this.trustManagers = trustManagers;
/* 181 */     newAdapteeFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean isTrustAllHosts() {
/* 188 */     return this.trustAllHosts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setTrustAllHosts(boolean trustAllHosts) {
/* 195 */     this.trustAllHosts = trustAllHosts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String[] getTrustedHosts() {
/* 202 */     return (String[])this.trustedHosts.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setTrustedHosts(String[] trustedHosts) {
/* 209 */     this.trustedHosts = (String[])trustedHosts.clone();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean isServerTrusted(String server, SSLSocket sslSocket) {
/* 227 */     if (this.trustAllHosts) {
/* 228 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 232 */     if (this.trustedHosts != null) {
/* 233 */       return Arrays.<String>asList(this.trustedHosts).contains(server);
/*     */     }
/*     */     
/* 236 */     return true;
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
/*     */   public synchronized Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
/* 249 */     return this.adapteeFactory.createSocket(socket, s, i, flag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String[] getDefaultCipherSuites() {
/* 257 */     return this.adapteeFactory.getDefaultCipherSuites();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String[] getSupportedCipherSuites() {
/* 265 */     return this.adapteeFactory.getSupportedCipherSuites();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Socket createSocket() throws IOException {
/* 273 */     return this.adapteeFactory.createSocket();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Socket createSocket(InetAddress inetaddress, int i, InetAddress inetaddress1, int j) throws IOException {
/* 283 */     return this.adapteeFactory.createSocket(inetaddress, i, inetaddress1, j);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Socket createSocket(InetAddress inetaddress, int i) throws IOException {
/* 292 */     return this.adapteeFactory.createSocket(inetaddress, i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Socket createSocket(String s, int i, InetAddress inetaddress, int j) throws IOException, UnknownHostException {
/* 303 */     return this.adapteeFactory.createSocket(s, i, inetaddress, j);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Socket createSocket(String s, int i) throws IOException, UnknownHostException {
/* 312 */     return this.adapteeFactory.createSocket(s, i);
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
/*     */   private class MailTrustManager
/*     */     implements X509TrustManager
/*     */   {
/* 326 */     private X509TrustManager adapteeTrustManager = null;
/*     */     
/*     */     private final MailSSLSocketFactory this$0;
/*     */ 
/*     */     
/*     */     private MailTrustManager() throws GeneralSecurityException {
/* 332 */       TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
/* 333 */       tmf.init((KeyStore)null);
/* 334 */       this.adapteeTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
/* 343 */       if (!MailSSLSocketFactory.this.isTrustAllHosts() && MailSSLSocketFactory.this.getTrustedHosts() == null) {
/* 344 */         this.adapteeTrustManager.checkClientTrusted(certs, authType);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
/* 354 */       if (!MailSSLSocketFactory.this.isTrustAllHosts() && MailSSLSocketFactory.this.getTrustedHosts() == null) {
/* 355 */         this.adapteeTrustManager.checkServerTrusted(certs, authType);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 362 */       return this.adapteeTrustManager.getAcceptedIssuers();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\MailSSLSocketFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */