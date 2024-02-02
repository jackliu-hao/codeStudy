/*     */ package org.apache.http.conn.ssl;
/*     */ 
/*     */ import java.net.Socket;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509KeyManager;
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
/*     */ @Deprecated
/*     */ public class SSLContextBuilder
/*     */ {
/*     */   static final String TLS = "TLS";
/*     */   static final String SSL = "SSL";
/*     */   private String protocol;
/*  74 */   private final Set<KeyManager> keymanagers = new LinkedHashSet<KeyManager>();
/*  75 */   private final Set<TrustManager> trustmanagers = new LinkedHashSet<TrustManager>();
/*     */   private SecureRandom secureRandom;
/*     */   
/*     */   public SSLContextBuilder useTLS() {
/*  79 */     this.protocol = "TLS";
/*  80 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder useSSL() {
/*  84 */     this.protocol = "SSL";
/*  85 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder useProtocol(String protocol) {
/*  89 */     this.protocol = protocol;
/*  90 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
/*  94 */     this.secureRandom = secureRandom;
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(KeyStore truststore, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
/* 101 */     TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*     */     
/* 103 */     tmfactory.init(truststore);
/* 104 */     TrustManager[] tms = tmfactory.getTrustManagers();
/* 105 */     if (tms != null) {
/* 106 */       if (trustStrategy != null) {
/* 107 */         for (int i = 0; i < tms.length; i++) {
/* 108 */           TrustManager tm = tms[i];
/* 109 */           if (tm instanceof X509TrustManager) {
/* 110 */             tms[i] = new TrustManagerDelegate((X509TrustManager)tm, trustStrategy);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 115 */       for (TrustManager tm : tms) {
/* 116 */         this.trustmanagers.add(tm);
/*     */       }
/*     */     } 
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(KeyStore truststore) throws NoSuchAlgorithmException, KeyStoreException {
/* 124 */     return loadTrustMaterial(truststore, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
/* 131 */     loadKeyMaterial(keystore, keyPassword, null);
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
/* 140 */     KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/*     */     
/* 142 */     kmfactory.init(keystore, keyPassword);
/* 143 */     KeyManager[] kms = kmfactory.getKeyManagers();
/* 144 */     if (kms != null) {
/* 145 */       if (aliasStrategy != null) {
/* 146 */         for (int i = 0; i < kms.length; i++) {
/* 147 */           KeyManager km = kms[i];
/* 148 */           if (km instanceof X509KeyManager) {
/* 149 */             kms[i] = new KeyManagerDelegate((X509KeyManager)km, aliasStrategy);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 154 */       for (KeyManager km : kms) {
/* 155 */         this.keymanagers.add(km);
/*     */       }
/*     */     } 
/* 158 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
/* 162 */     SSLContext sslcontext = SSLContext.getInstance((this.protocol != null) ? this.protocol : "TLS");
/*     */     
/* 164 */     sslcontext.init(!this.keymanagers.isEmpty() ? this.keymanagers.<KeyManager>toArray(new KeyManager[this.keymanagers.size()]) : null, !this.trustmanagers.isEmpty() ? this.trustmanagers.<TrustManager>toArray(new TrustManager[this.trustmanagers.size()]) : null, this.secureRandom);
/*     */ 
/*     */ 
/*     */     
/* 168 */     return sslcontext;
/*     */   }
/*     */   
/*     */   static class TrustManagerDelegate
/*     */     implements X509TrustManager
/*     */   {
/*     */     private final X509TrustManager trustManager;
/*     */     private final TrustStrategy trustStrategy;
/*     */     
/*     */     TrustManagerDelegate(X509TrustManager trustManager, TrustStrategy trustStrategy) {
/* 178 */       this.trustManager = trustManager;
/* 179 */       this.trustStrategy = trustStrategy;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 185 */       this.trustManager.checkClientTrusted(chain, authType);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 191 */       if (!this.trustStrategy.isTrusted(chain, authType)) {
/* 192 */         this.trustManager.checkServerTrusted(chain, authType);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 198 */       return this.trustManager.getAcceptedIssuers();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class KeyManagerDelegate
/*     */     implements X509KeyManager
/*     */   {
/*     */     private final X509KeyManager keyManager;
/*     */     private final PrivateKeyStrategy aliasStrategy;
/*     */     
/*     */     KeyManagerDelegate(X509KeyManager keyManager, PrivateKeyStrategy aliasStrategy) {
/* 210 */       this.keyManager = keyManager;
/* 211 */       this.aliasStrategy = aliasStrategy;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getClientAliases(String keyType, Principal[] issuers) {
/* 217 */       return this.keyManager.getClientAliases(keyType, issuers);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
/* 223 */       Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
/* 224 */       for (String keyType : keyTypes) {
/* 225 */         String[] aliases = this.keyManager.getClientAliases(keyType, issuers);
/* 226 */         if (aliases != null) {
/* 227 */           for (String alias : aliases) {
/* 228 */             validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
/*     */           }
/*     */         }
/*     */       } 
/*     */       
/* 233 */       return this.aliasStrategy.chooseAlias(validAliases, socket);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getServerAliases(String keyType, Principal[] issuers) {
/* 239 */       return this.keyManager.getServerAliases(keyType, issuers);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
/* 245 */       Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
/* 246 */       String[] aliases = this.keyManager.getServerAliases(keyType, issuers);
/* 247 */       if (aliases != null) {
/* 248 */         for (String alias : aliases) {
/* 249 */           validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
/*     */         }
/*     */       }
/*     */       
/* 253 */       return this.aliasStrategy.chooseAlias(validAliases, socket);
/*     */     }
/*     */ 
/*     */     
/*     */     public X509Certificate[] getCertificateChain(String alias) {
/* 258 */       return this.keyManager.getCertificateChain(alias);
/*     */     }
/*     */ 
/*     */     
/*     */     public PrivateKey getPrivateKey(String alias) {
/* 263 */       return this.keyManager.getPrivateKey(alias);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\SSLContextBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */