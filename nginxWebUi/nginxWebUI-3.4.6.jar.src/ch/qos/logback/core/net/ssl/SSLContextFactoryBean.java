/*     */ package ch.qos.logback.core.net.ssl;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SSLContextFactoryBean
/*     */ {
/*     */   private static final String JSSE_KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
/*     */   private static final String JSSE_TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
/*     */   private KeyStoreFactoryBean keyStore;
/*     */   private KeyStoreFactoryBean trustStore;
/*     */   private SecureRandomFactoryBean secureRandom;
/*     */   private KeyManagerFactoryFactoryBean keyManagerFactory;
/*     */   private TrustManagerFactoryFactoryBean trustManagerFactory;
/*     */   private String protocol;
/*     */   private String provider;
/*     */   
/*     */   public SSLContext createContext(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, CertificateException {
/*  77 */     SSLContext sslContext = (getProvider() != null) ? SSLContext.getInstance(getProtocol(), getProvider()) : SSLContext.getInstance(getProtocol());
/*     */     
/*  79 */     context.addInfo("SSL protocol '" + sslContext.getProtocol() + "' provider '" + sslContext.getProvider() + "'");
/*     */     
/*  81 */     KeyManager[] keyManagers = createKeyManagers(context);
/*  82 */     TrustManager[] trustManagers = createTrustManagers(context);
/*  83 */     SecureRandom secureRandom = createSecureRandom(context);
/*  84 */     sslContext.init(keyManagers, trustManagers, secureRandom);
/*  85 */     return sslContext;
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
/*     */   private KeyManager[] createKeyManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
/* 103 */     if (getKeyStore() == null) {
/* 104 */       return null;
/*     */     }
/* 106 */     KeyStore keyStore = getKeyStore().createKeyStore();
/* 107 */     context.addInfo("key store of type '" + keyStore.getType() + "' provider '" + keyStore.getProvider() + "': " + getKeyStore().getLocation());
/*     */     
/* 109 */     KeyManagerFactory kmf = getKeyManagerFactory().createKeyManagerFactory();
/* 110 */     context.addInfo("key manager algorithm '" + kmf.getAlgorithm() + "' provider '" + kmf.getProvider() + "'");
/*     */     
/* 112 */     char[] passphrase = getKeyStore().getPassword().toCharArray();
/* 113 */     kmf.init(keyStore, passphrase);
/* 114 */     return kmf.getKeyManagers();
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
/*     */   private TrustManager[] createTrustManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
/* 132 */     if (getTrustStore() == null) {
/* 133 */       return null;
/*     */     }
/* 135 */     KeyStore trustStore = getTrustStore().createKeyStore();
/* 136 */     context.addInfo("trust store of type '" + trustStore.getType() + "' provider '" + trustStore.getProvider() + "': " + getTrustStore().getLocation());
/*     */     
/* 138 */     TrustManagerFactory tmf = getTrustManagerFactory().createTrustManagerFactory();
/* 139 */     context.addInfo("trust manager algorithm '" + tmf.getAlgorithm() + "' provider '" + tmf.getProvider() + "'");
/*     */     
/* 141 */     tmf.init(trustStore);
/* 142 */     return tmf.getTrustManagers();
/*     */   }
/*     */ 
/*     */   
/*     */   private SecureRandom createSecureRandom(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException {
/* 147 */     SecureRandom secureRandom = getSecureRandom().createSecureRandom();
/* 148 */     context.addInfo("secure random algorithm '" + secureRandom.getAlgorithm() + "' provider '" + secureRandom.getProvider() + "'");
/*     */     
/* 150 */     return secureRandom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyStoreFactoryBean getKeyStore() {
/* 159 */     if (this.keyStore == null) {
/* 160 */       this.keyStore = keyStoreFromSystemProperties("javax.net.ssl.keyStore");
/*     */     }
/* 162 */     return this.keyStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeyStore(KeyStoreFactoryBean keyStore) {
/* 170 */     this.keyStore = keyStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyStoreFactoryBean getTrustStore() {
/* 179 */     if (this.trustStore == null) {
/* 180 */       this.trustStore = keyStoreFromSystemProperties("javax.net.ssl.trustStore");
/*     */     }
/* 182 */     return this.trustStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrustStore(KeyStoreFactoryBean trustStore) {
/* 190 */     this.trustStore = trustStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private KeyStoreFactoryBean keyStoreFromSystemProperties(String property) {
/* 200 */     if (System.getProperty(property) == null)
/* 201 */       return null; 
/* 202 */     KeyStoreFactoryBean keyStore = new KeyStoreFactoryBean();
/* 203 */     keyStore.setLocation(locationFromSystemProperty(property));
/* 204 */     keyStore.setProvider(System.getProperty(property + "Provider"));
/* 205 */     keyStore.setPassword(System.getProperty(property + "Password"));
/* 206 */     keyStore.setType(System.getProperty(property + "Type"));
/* 207 */     return keyStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String locationFromSystemProperty(String name) {
/* 217 */     String location = System.getProperty(name);
/* 218 */     if (location != null && !location.startsWith("file:")) {
/* 219 */       location = "file:" + location;
/*     */     }
/* 221 */     return location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SecureRandomFactoryBean getSecureRandom() {
/* 230 */     if (this.secureRandom == null) {
/* 231 */       return new SecureRandomFactoryBean();
/*     */     }
/* 233 */     return this.secureRandom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSecureRandom(SecureRandomFactoryBean secureRandom) {
/* 241 */     this.secureRandom = secureRandom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyManagerFactoryFactoryBean getKeyManagerFactory() {
/* 250 */     if (this.keyManagerFactory == null) {
/* 251 */       return new KeyManagerFactoryFactoryBean();
/*     */     }
/* 253 */     return this.keyManagerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeyManagerFactory(KeyManagerFactoryFactoryBean keyManagerFactory) {
/* 261 */     this.keyManagerFactory = keyManagerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TrustManagerFactoryFactoryBean getTrustManagerFactory() {
/* 270 */     if (this.trustManagerFactory == null) {
/* 271 */       return new TrustManagerFactoryFactoryBean();
/*     */     }
/* 273 */     return this.trustManagerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrustManagerFactory(TrustManagerFactoryFactoryBean trustManagerFactory) {
/* 281 */     this.trustManagerFactory = trustManagerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProtocol() {
/* 291 */     if (this.protocol == null) {
/* 292 */       return "SSL";
/*     */     }
/* 294 */     return this.protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProtocol(String protocol) {
/* 304 */     this.protocol = protocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProvider() {
/* 312 */     return this.provider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProvider(String provider) {
/* 321 */     this.provider = provider;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\SSLContextFactoryBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */