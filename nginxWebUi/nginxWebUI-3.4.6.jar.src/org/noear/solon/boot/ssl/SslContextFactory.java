/*    */ package org.noear.solon.boot.ssl;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.KeyStore;
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.KeyManagerFactory;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import org.noear.solon.Utils;
/*    */ 
/*    */ public class SslContextFactory
/*    */ {
/*    */   public static SSLContext create() throws IOException {
/*    */     SSLContext sslContext;
/* 20 */     String keyStoreName = System.getProperty("javax.net.ssl.keyStore");
/* 21 */     String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
/* 22 */     String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
/*    */     
/* 24 */     if (Utils.isEmpty(keyStoreType)) {
/* 25 */       keyStoreType = "jks";
/*    */     }
/*    */     
/* 28 */     KeyStore keyStore = loadKeyStore(keyStoreName, keyStoreType, keyStorePassword);
/*    */     
/* 30 */     KeyManager[] keyManagers = buildKeyManagers(keyStore, keyStorePassword.toCharArray());
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 35 */       sslContext = SSLContext.getInstance("TLS");
/* 36 */       sslContext.init(keyManagers, null, null);
/* 37 */     } catch (NoSuchAlgorithmException|java.security.KeyManagementException exc) {
/* 38 */       throw new IOException("Unable to create and initialise the SSLContext", exc);
/*    */     } 
/*    */     
/* 41 */     return sslContext;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static KeyStore loadKeyStore(String location, String type, String storePassword) throws IOException {
/* 47 */     URL KeyStoreUrl = Utils.getResource(location);
/* 48 */     InputStream KeyStoreStream = null;
/*    */     
/* 50 */     if (KeyStoreUrl == null) {
/* 51 */       KeyStoreStream = new FileInputStream(location);
/*    */     } else {
/* 53 */       KeyStoreStream = KeyStoreUrl.openStream();
/*    */     } 
/*    */     
/* 56 */     try (InputStream stream = KeyStoreStream) {
/* 57 */       KeyStore loadedKeystore = KeyStore.getInstance(type);
/* 58 */       loadedKeystore.load(stream, storePassword.toCharArray());
/* 59 */       return loadedKeystore;
/* 60 */     } catch (KeyStoreException|NoSuchAlgorithmException|java.security.cert.CertificateException exc) {
/* 61 */       throw new IOException(String.format("Unable to load KeyStore %s", new Object[] { location }), exc);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static KeyManager[] buildKeyManagers(KeyStore keyStore, char[] storePassword) throws IOException {
/*    */     KeyManager[] keyManagers;
/*    */     try {
/* 69 */       KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
/* 70 */           KeyManagerFactory.getDefaultAlgorithm());
/* 71 */       keyManagerFactory.init(keyStore, storePassword);
/* 72 */       keyManagers = keyManagerFactory.getKeyManagers();
/* 73 */     } catch (NoSuchAlgorithmException|java.security.UnrecoverableKeyException|KeyStoreException exc) {
/* 74 */       throw new IOException("Unable to initialise KeyManager[]", exc);
/*    */     } 
/* 76 */     return keyManagers;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\ssl\SslContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */