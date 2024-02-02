package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.spi.ContextAware;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextFactoryBean {
   private static final String JSSE_KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
   private static final String JSSE_TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";
   private KeyStoreFactoryBean keyStore;
   private KeyStoreFactoryBean trustStore;
   private SecureRandomFactoryBean secureRandom;
   private KeyManagerFactoryFactoryBean keyManagerFactory;
   private TrustManagerFactoryFactoryBean trustManagerFactory;
   private String protocol;
   private String provider;

   public SSLContext createContext(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, CertificateException {
      SSLContext sslContext = this.getProvider() != null ? SSLContext.getInstance(this.getProtocol(), this.getProvider()) : SSLContext.getInstance(this.getProtocol());
      context.addInfo("SSL protocol '" + sslContext.getProtocol() + "' provider '" + sslContext.getProvider() + "'");
      KeyManager[] keyManagers = this.createKeyManagers(context);
      TrustManager[] trustManagers = this.createTrustManagers(context);
      SecureRandom secureRandom = this.createSecureRandom(context);
      sslContext.init(keyManagers, trustManagers, secureRandom);
      return sslContext;
   }

   private KeyManager[] createKeyManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
      if (this.getKeyStore() == null) {
         return null;
      } else {
         KeyStore keyStore = this.getKeyStore().createKeyStore();
         context.addInfo("key store of type '" + keyStore.getType() + "' provider '" + keyStore.getProvider() + "': " + this.getKeyStore().getLocation());
         KeyManagerFactory kmf = this.getKeyManagerFactory().createKeyManagerFactory();
         context.addInfo("key manager algorithm '" + kmf.getAlgorithm() + "' provider '" + kmf.getProvider() + "'");
         char[] passphrase = this.getKeyStore().getPassword().toCharArray();
         kmf.init(keyStore, passphrase);
         return kmf.getKeyManagers();
      }
   }

   private TrustManager[] createTrustManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
      if (this.getTrustStore() == null) {
         return null;
      } else {
         KeyStore trustStore = this.getTrustStore().createKeyStore();
         context.addInfo("trust store of type '" + trustStore.getType() + "' provider '" + trustStore.getProvider() + "': " + this.getTrustStore().getLocation());
         TrustManagerFactory tmf = this.getTrustManagerFactory().createTrustManagerFactory();
         context.addInfo("trust manager algorithm '" + tmf.getAlgorithm() + "' provider '" + tmf.getProvider() + "'");
         tmf.init(trustStore);
         return tmf.getTrustManagers();
      }
   }

   private SecureRandom createSecureRandom(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException {
      SecureRandom secureRandom = this.getSecureRandom().createSecureRandom();
      context.addInfo("secure random algorithm '" + secureRandom.getAlgorithm() + "' provider '" + secureRandom.getProvider() + "'");
      return secureRandom;
   }

   public KeyStoreFactoryBean getKeyStore() {
      if (this.keyStore == null) {
         this.keyStore = this.keyStoreFromSystemProperties("javax.net.ssl.keyStore");
      }

      return this.keyStore;
   }

   public void setKeyStore(KeyStoreFactoryBean keyStore) {
      this.keyStore = keyStore;
   }

   public KeyStoreFactoryBean getTrustStore() {
      if (this.trustStore == null) {
         this.trustStore = this.keyStoreFromSystemProperties("javax.net.ssl.trustStore");
      }

      return this.trustStore;
   }

   public void setTrustStore(KeyStoreFactoryBean trustStore) {
      this.trustStore = trustStore;
   }

   private KeyStoreFactoryBean keyStoreFromSystemProperties(String property) {
      if (System.getProperty(property) == null) {
         return null;
      } else {
         KeyStoreFactoryBean keyStore = new KeyStoreFactoryBean();
         keyStore.setLocation(this.locationFromSystemProperty(property));
         keyStore.setProvider(System.getProperty(property + "Provider"));
         keyStore.setPassword(System.getProperty(property + "Password"));
         keyStore.setType(System.getProperty(property + "Type"));
         return keyStore;
      }
   }

   private String locationFromSystemProperty(String name) {
      String location = System.getProperty(name);
      if (location != null && !location.startsWith("file:")) {
         location = "file:" + location;
      }

      return location;
   }

   public SecureRandomFactoryBean getSecureRandom() {
      return this.secureRandom == null ? new SecureRandomFactoryBean() : this.secureRandom;
   }

   public void setSecureRandom(SecureRandomFactoryBean secureRandom) {
      this.secureRandom = secureRandom;
   }

   public KeyManagerFactoryFactoryBean getKeyManagerFactory() {
      return this.keyManagerFactory == null ? new KeyManagerFactoryFactoryBean() : this.keyManagerFactory;
   }

   public void setKeyManagerFactory(KeyManagerFactoryFactoryBean keyManagerFactory) {
      this.keyManagerFactory = keyManagerFactory;
   }

   public TrustManagerFactoryFactoryBean getTrustManagerFactory() {
      return this.trustManagerFactory == null ? new TrustManagerFactoryFactoryBean() : this.trustManagerFactory;
   }

   public void setTrustManagerFactory(TrustManagerFactoryFactoryBean trustManagerFactory) {
      this.trustManagerFactory = trustManagerFactory;
   }

   public String getProtocol() {
      return this.protocol == null ? "SSL" : this.protocol;
   }

   public void setProtocol(String protocol) {
      this.protocol = protocol;
   }

   public String getProvider() {
      return this.provider;
   }

   public void setProvider(String provider) {
      this.provider = provider;
   }
}
