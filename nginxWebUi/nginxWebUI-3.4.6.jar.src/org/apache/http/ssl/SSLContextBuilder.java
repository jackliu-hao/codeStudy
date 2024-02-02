/*     */ package org.apache.http.ssl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Security;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509ExtendedKeyManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class SSLContextBuilder
/*     */ {
/*     */   static final String TLS = "TLS";
/*     */   private String protocol;
/*     */   private final Set<KeyManager> keyManagers;
/*  85 */   private String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
/*  86 */   private String keyStoreType = KeyStore.getDefaultType();
/*     */   private final Set<TrustManager> trustManagers;
/*  88 */   private String trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
/*     */   private SecureRandom secureRandom;
/*     */   private Provider provider;
/*     */   
/*     */   public static SSLContextBuilder create() {
/*  93 */     return new SSLContextBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLContextBuilder() {
/*  98 */     this.keyManagers = new LinkedHashSet<KeyManager>();
/*  99 */     this.trustManagers = new LinkedHashSet<TrustManager>();
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
/*     */   
/*     */   @Deprecated
/*     */   public SSLContextBuilder useProtocol(String protocol) {
/* 119 */     this.protocol = protocol;
/* 120 */     return this;
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
/*     */   
/*     */   public SSLContextBuilder setProtocol(String protocol) {
/* 139 */     this.protocol = protocol;
/* 140 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
/* 144 */     this.secureRandom = secureRandom;
/* 145 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder setProvider(Provider provider) {
/* 149 */     this.provider = provider;
/* 150 */     return this;
/*     */   }
/*     */   
/*     */   public SSLContextBuilder setProvider(String name) {
/* 154 */     this.provider = Security.getProvider(name);
/* 155 */     return this;
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
/*     */   
/*     */   public SSLContextBuilder setKeyStoreType(String keyStoreType) {
/* 174 */     this.keyStoreType = keyStoreType;
/* 175 */     return this;
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
/*     */   
/*     */   public SSLContextBuilder setKeyManagerFactoryAlgorithm(String keyManagerFactoryAlgorithm) {
/* 194 */     this.keyManagerFactoryAlgorithm = keyManagerFactoryAlgorithm;
/* 195 */     return this;
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
/*     */   
/*     */   public SSLContextBuilder setTrustManagerFactoryAlgorithm(String trustManagerFactoryAlgorithm) {
/* 214 */     this.trustManagerFactoryAlgorithm = trustManagerFactoryAlgorithm;
/* 215 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(KeyStore truststore, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
/* 221 */     TrustManagerFactory tmfactory = TrustManagerFactory.getInstance((this.trustManagerFactoryAlgorithm == null) ? TrustManagerFactory.getDefaultAlgorithm() : this.trustManagerFactoryAlgorithm);
/*     */ 
/*     */     
/* 224 */     tmfactory.init(truststore);
/* 225 */     TrustManager[] tms = tmfactory.getTrustManagers();
/* 226 */     if (tms != null) {
/* 227 */       if (trustStrategy != null) {
/* 228 */         for (int i = 0; i < tms.length; i++) {
/* 229 */           TrustManager tm = tms[i];
/* 230 */           if (tm instanceof X509TrustManager) {
/* 231 */             tms[i] = new TrustManagerDelegate((X509TrustManager)tm, trustStrategy);
/*     */           }
/*     */         } 
/*     */       }
/* 235 */       for (TrustManager tm : tms) {
/* 236 */         this.trustManagers.add(tm);
/*     */       }
/*     */     } 
/* 239 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
/* 244 */     return loadTrustMaterial((KeyStore)null, trustStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(File file, char[] storePassword, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
/* 251 */     Args.notNull(file, "Truststore file");
/* 252 */     KeyStore trustStore = KeyStore.getInstance(this.keyStoreType);
/* 253 */     FileInputStream inStream = new FileInputStream(file);
/*     */     try {
/* 255 */       trustStore.load(inStream, storePassword);
/*     */     } finally {
/* 257 */       inStream.close();
/*     */     } 
/* 259 */     return loadTrustMaterial(trustStore, trustStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(File file, char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
/* 265 */     return loadTrustMaterial(file, storePassword, (TrustStrategy)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(File file) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
/* 270 */     return loadTrustMaterial(file, (char[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(URL url, char[] storePassword, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
/* 277 */     Args.notNull(url, "Truststore URL");
/* 278 */     KeyStore trustStore = KeyStore.getInstance(this.keyStoreType);
/* 279 */     InputStream inStream = url.openStream();
/*     */     try {
/* 281 */       trustStore.load(inStream, storePassword);
/*     */     } finally {
/* 283 */       inStream.close();
/*     */     } 
/* 285 */     return loadTrustMaterial(trustStore, trustStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadTrustMaterial(URL url, char[] storePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
/* 291 */     return loadTrustMaterial(url, storePassword, (TrustStrategy)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
/* 299 */     KeyManagerFactory kmfactory = KeyManagerFactory.getInstance((this.keyManagerFactoryAlgorithm == null) ? KeyManagerFactory.getDefaultAlgorithm() : this.keyManagerFactoryAlgorithm);
/*     */ 
/*     */     
/* 302 */     kmfactory.init(keystore, keyPassword);
/* 303 */     KeyManager[] kms = kmfactory.getKeyManagers();
/* 304 */     if (kms != null) {
/* 305 */       if (aliasStrategy != null) {
/* 306 */         for (int i = 0; i < kms.length; i++) {
/* 307 */           KeyManager km = kms[i];
/* 308 */           if (km instanceof X509ExtendedKeyManager) {
/* 309 */             kms[i] = new KeyManagerDelegate((X509ExtendedKeyManager)km, aliasStrategy);
/*     */           }
/*     */         } 
/*     */       }
/* 313 */       for (KeyManager km : kms) {
/* 314 */         this.keyManagers.add(km);
/*     */       }
/*     */     } 
/* 317 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
/* 323 */     return loadKeyMaterial(keystore, keyPassword, (PrivateKeyStrategy)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(File file, char[] storePassword, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
/* 331 */     Args.notNull(file, "Keystore file");
/* 332 */     KeyStore identityStore = KeyStore.getInstance(this.keyStoreType);
/* 333 */     FileInputStream inStream = new FileInputStream(file);
/*     */     try {
/* 335 */       identityStore.load(inStream, storePassword);
/*     */     } finally {
/* 337 */       inStream.close();
/*     */     } 
/* 339 */     return loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(File file, char[] storePassword, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
/* 346 */     return loadKeyMaterial(file, storePassword, keyPassword, (PrivateKeyStrategy)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(URL url, char[] storePassword, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
/* 354 */     Args.notNull(url, "Keystore URL");
/* 355 */     KeyStore identityStore = KeyStore.getInstance(this.keyStoreType);
/* 356 */     InputStream inStream = url.openStream();
/*     */     try {
/* 358 */       identityStore.load(inStream, storePassword);
/*     */     } finally {
/* 360 */       inStream.close();
/*     */     } 
/* 362 */     return loadKeyMaterial(identityStore, keyPassword, aliasStrategy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder loadKeyMaterial(URL url, char[] storePassword, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
/* 369 */     return loadKeyMaterial(url, storePassword, keyPassword, (PrivateKeyStrategy)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initSSLContext(SSLContext sslContext, Collection<KeyManager> keyManagers, Collection<TrustManager> trustManagers, SecureRandom secureRandom) throws KeyManagementException {
/* 377 */     sslContext.init(!keyManagers.isEmpty() ? keyManagers.<KeyManager>toArray(new KeyManager[keyManagers.size()]) : null, !trustManagers.isEmpty() ? trustManagers.<TrustManager>toArray(new TrustManager[trustManagers.size()]) : null, secureRandom);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
/*     */     SSLContext sslContext;
/* 385 */     String protocolStr = (this.protocol != null) ? this.protocol : "TLS";
/* 386 */     if (this.provider != null) {
/* 387 */       sslContext = SSLContext.getInstance(protocolStr, this.provider);
/*     */     } else {
/* 389 */       sslContext = SSLContext.getInstance(protocolStr);
/*     */     } 
/* 391 */     initSSLContext(sslContext, this.keyManagers, this.trustManagers, this.secureRandom);
/* 392 */     return sslContext;
/*     */   }
/*     */   
/*     */   static class TrustManagerDelegate
/*     */     implements X509TrustManager
/*     */   {
/*     */     private final X509TrustManager trustManager;
/*     */     private final TrustStrategy trustStrategy;
/*     */     
/*     */     TrustManagerDelegate(X509TrustManager trustManager, TrustStrategy trustStrategy) {
/* 402 */       this.trustManager = trustManager;
/* 403 */       this.trustStrategy = trustStrategy;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 409 */       this.trustManager.checkClientTrusted(chain, authType);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 415 */       if (!this.trustStrategy.isTrusted(chain, authType)) {
/* 416 */         this.trustManager.checkServerTrusted(chain, authType);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 422 */       return this.trustManager.getAcceptedIssuers();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class KeyManagerDelegate
/*     */     extends X509ExtendedKeyManager
/*     */   {
/*     */     private final X509ExtendedKeyManager keyManager;
/*     */     private final PrivateKeyStrategy aliasStrategy;
/*     */     
/*     */     KeyManagerDelegate(X509ExtendedKeyManager keyManager, PrivateKeyStrategy aliasStrategy) {
/* 434 */       this.keyManager = keyManager;
/* 435 */       this.aliasStrategy = aliasStrategy;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getClientAliases(String keyType, Principal[] issuers) {
/* 441 */       return this.keyManager.getClientAliases(keyType, issuers);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, PrivateKeyDetails> getClientAliasMap(String[] keyTypes, Principal[] issuers) {
/* 446 */       Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
/* 447 */       for (String keyType : keyTypes) {
/* 448 */         String[] aliases = this.keyManager.getClientAliases(keyType, issuers);
/* 449 */         if (aliases != null) {
/* 450 */           for (String alias : aliases) {
/* 451 */             validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
/*     */           }
/*     */         }
/*     */       } 
/*     */       
/* 456 */       return validAliases;
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, PrivateKeyDetails> getServerAliasMap(String keyType, Principal[] issuers) {
/* 461 */       Map<String, PrivateKeyDetails> validAliases = new HashMap<String, PrivateKeyDetails>();
/* 462 */       String[] aliases = this.keyManager.getServerAliases(keyType, issuers);
/* 463 */       if (aliases != null) {
/* 464 */         for (String alias : aliases) {
/* 465 */           validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
/*     */         }
/*     */       }
/*     */       
/* 469 */       return validAliases;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
/* 475 */       Map<String, PrivateKeyDetails> validAliases = getClientAliasMap(keyTypes, issuers);
/* 476 */       return this.aliasStrategy.chooseAlias(validAliases, socket);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getServerAliases(String keyType, Principal[] issuers) {
/* 482 */       return this.keyManager.getServerAliases(keyType, issuers);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
/* 488 */       Map<String, PrivateKeyDetails> validAliases = getServerAliasMap(keyType, issuers);
/* 489 */       return this.aliasStrategy.chooseAlias(validAliases, socket);
/*     */     }
/*     */ 
/*     */     
/*     */     public X509Certificate[] getCertificateChain(String alias) {
/* 494 */       return this.keyManager.getCertificateChain(alias);
/*     */     }
/*     */ 
/*     */     
/*     */     public PrivateKey getPrivateKey(String alias) {
/* 499 */       return this.keyManager.getPrivateKey(alias);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine sslEngine) {
/* 505 */       Map<String, PrivateKeyDetails> validAliases = getClientAliasMap(keyTypes, issuers);
/* 506 */       return this.aliasStrategy.chooseAlias(validAliases, null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine sslEngine) {
/* 512 */       Map<String, PrivateKeyDetails> validAliases = getServerAliasMap(keyType, issuers);
/* 513 */       return this.aliasStrategy.chooseAlias(validAliases, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 520 */     return "[provider=" + this.provider + ", protocol=" + this.protocol + ", keyStoreType=" + this.keyStoreType + ", keyManagerFactoryAlgorithm=" + this.keyManagerFactoryAlgorithm + ", keyManagers=" + this.keyManagers + ", trustManagerFactoryAlgorithm=" + this.trustManagerFactoryAlgorithm + ", trustManagers=" + this.trustManagers + ", secureRandom=" + this.secureRandom + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ssl\SSLContextBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */