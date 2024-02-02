/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*     */ import com.mysql.cj.exceptions.RSAException;
/*     */ import com.mysql.cj.exceptions.SSLParamsException;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.util.Base64Decoder;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Signature;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathValidator;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorResult;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.PKIXCertPathValidatorResult;
/*     */ import java.security.cert.PKIXParameters;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Base64;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.naming.InvalidNameException;
/*     */ import javax.naming.ldap.LdapName;
/*     */ import javax.naming.ldap.Rdn;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
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
/*     */ public class ExportControlled
/*     */ {
/*     */   private static final String TLSv1 = "TLSv1";
/*     */   private static final String TLSv1_1 = "TLSv1.1";
/*     */   private static final String TLSv1_2 = "TLSv1.2";
/*     */   private static final String TLSv1_3 = "TLSv1.3";
/* 114 */   private static final String[] KNOWN_TLS_PROTOCOLS = new String[] { "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1" };
/* 115 */   private static final String[] VALID_TLS_PROTOCOLS = new String[] { "TLSv1.3", "TLSv1.2" };
/*     */   
/*     */   private static final String TLS_SETTINGS_RESOURCE = "/com/mysql/cj/TlsSettings.properties";
/* 118 */   private static final List<String> ALLOWED_CIPHERS = new ArrayList<>();
/* 119 */   private static final List<String> RESTRICTED_CIPHER_SUBSTR = new ArrayList<>();
/*     */   
/*     */   static {
/*     */     try {
/* 123 */       Properties tlsSettings = new Properties();
/* 124 */       tlsSettings.load(ExportControlled.class.getResourceAsStream("/com/mysql/cj/TlsSettings.properties"));
/*     */       
/* 126 */       Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Mandatory").split("\\s*,\\s*")).forEach(s -> {
/*     */             ALLOWED_CIPHERS.add("TLS_" + s.trim());
/*     */             ALLOWED_CIPHERS.add("SSL_" + s.trim());
/*     */           });
/* 130 */       Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Approved").split("\\s*,\\s*")).forEach(s -> {
/*     */             ALLOWED_CIPHERS.add("TLS_" + s.trim());
/*     */             ALLOWED_CIPHERS.add("SSL_" + s.trim());
/*     */           });
/* 134 */       Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Deprecated").split("\\s*,\\s*")).forEach(s -> {
/*     */             ALLOWED_CIPHERS.add("TLS_" + s.trim());
/*     */             ALLOWED_CIPHERS.add("SSL_" + s.trim());
/*     */           });
/* 138 */       Arrays.<String>stream(tlsSettings.getProperty("TLSCiphers.Unacceptable.Mask").split("\\s*,\\s*")).forEach(s -> RESTRICTED_CIPHER_SUBSTR.add(s.trim()));
/* 139 */     } catch (IOException e) {
/* 140 */       throw ExceptionFactory.createException("Unable to load TlsSettings.properties");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean enabled() {
/* 149 */     return true;
/*     */   }
/*     */   
/*     */   private static String[] getAllowedCiphers(PropertySet pset, List<String> socketCipherSuites) {
/* 153 */     String enabledSSLCipherSuites = (String)pset.getStringProperty(PropertyKey.tlsCiphersuites).getValue();
/*     */     
/* 155 */     Stream<String> filterStream = StringUtils.isNullOrEmpty(enabledSSLCipherSuites) ? socketCipherSuites.stream() : Arrays.<String>stream(enabledSSLCipherSuites.split("\\s*,\\s*")).filter(socketCipherSuites::contains);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 163 */     List<String> allowedCiphers = (List<String>)filterStream.filter(ALLOWED_CIPHERS::contains).filter(c -> !RESTRICTED_CIPHER_SUBSTR.stream().filter(()).findFirst().isPresent()).collect(Collectors.toList());
/*     */     
/* 165 */     return allowedCiphers.<String>toArray(new String[0]);
/*     */   }
/*     */   
/*     */   private static String[] getAllowedProtocols(PropertySet pset, ServerVersion serverVersion, String[] socketProtocols) {
/* 169 */     List<String> tryProtocols = null;
/*     */     
/* 171 */     RuntimeProperty<String> tlsVersions = pset.getStringProperty(PropertyKey.tlsVersions);
/* 172 */     if (tlsVersions != null && tlsVersions.isExplicitlySet()) {
/*     */       
/* 174 */       if (tlsVersions.getValue() == null) {
/* 175 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions is empty. Accepted values are TLSv1.2 and TLSv1.3.");
/*     */       }
/*     */       
/* 178 */       tryProtocols = getValidProtocols(((String)tlsVersions.getValue()).split("\\s*,\\s*"));
/*     */     } else {
/* 180 */       tryProtocols = new ArrayList<>(Arrays.asList(VALID_TLS_PROTOCOLS));
/*     */     } 
/*     */     
/* 183 */     List<String> jvmSupportedProtocols = Arrays.asList(socketProtocols);
/* 184 */     List<String> allowedProtocols = new ArrayList<>();
/* 185 */     for (String protocol : tryProtocols) {
/* 186 */       if (jvmSupportedProtocols.contains(protocol)) {
/* 187 */         allowedProtocols.add(protocol);
/*     */       }
/*     */     } 
/* 190 */     return allowedProtocols.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<String> getValidProtocols(String[] protocols) {
/* 195 */     List<String> requestedProtocols = (List<String>)Arrays.<String>stream(protocols).filter(p -> !StringUtils.isNullOrEmpty(p.trim())).collect(Collectors.toList());
/* 196 */     if (requestedProtocols.size() == 0) {
/* 197 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions is empty. Accepted values are TLSv1.2 and TLSv1.3.");
/*     */     }
/*     */ 
/*     */     
/* 201 */     List<String> sanitizedProtocols = new ArrayList<>();
/* 202 */     for (String protocol : KNOWN_TLS_PROTOCOLS) {
/* 203 */       if (requestedProtocols.contains(protocol)) {
/* 204 */         sanitizedProtocols.add(protocol);
/*     */       }
/*     */     } 
/* 207 */     if (sanitizedProtocols.size() == 0) {
/* 208 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Specified list of TLS versions only contains non valid TLS protocols. Accepted values are TLSv1.2 and TLSv1.3.");
/*     */     }
/*     */ 
/*     */     
/* 212 */     List<String> validProtocols = new ArrayList<>();
/* 213 */     for (String protocol : VALID_TLS_PROTOCOLS) {
/* 214 */       if (sanitizedProtocols.contains(protocol)) {
/* 215 */         validProtocols.add(protocol);
/*     */       }
/*     */     } 
/* 218 */     if (validProtocols.size() == 0) {
/* 219 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "TLS protocols TLSv1 and TLSv1.1 are not supported. Accepted values are TLSv1.2 and TLSv1.3.");
/*     */     }
/*     */ 
/*     */     
/* 223 */     return validProtocols;
/*     */   }
/*     */   
/*     */   public static void checkValidProtocols(List<String> protocols) {
/* 227 */     getValidProtocols(protocols.<String>toArray(new String[0]));
/*     */   }
/*     */   
/*     */   private static class KeyStoreConf {
/* 231 */     public String keyStoreUrl = null;
/* 232 */     public String keyStorePassword = null;
/* 233 */     public String keyStoreType = "JKS";
/*     */ 
/*     */     
/*     */     public KeyStoreConf() {}
/*     */     
/*     */     public KeyStoreConf(String keyStoreUrl, String keyStorePassword, String keyStoreType) {
/* 239 */       this.keyStoreUrl = keyStoreUrl;
/* 240 */       this.keyStorePassword = keyStorePassword;
/* 241 */       this.keyStoreType = keyStoreType;
/*     */     }
/*     */   }
/*     */   
/*     */   private static KeyStoreConf getTrustStoreConf(PropertySet propertySet, boolean required) {
/* 246 */     String trustStoreUrl = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreUrl).getValue();
/* 247 */     String trustStorePassword = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStorePassword).getValue();
/* 248 */     String trustStoreType = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType).getValue();
/* 249 */     boolean fallbackToSystemTrustStore = ((Boolean)propertySet.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore).getValue()).booleanValue();
/*     */     
/* 251 */     if (fallbackToSystemTrustStore && StringUtils.isNullOrEmpty(trustStoreUrl)) {
/* 252 */       trustStoreUrl = System.getProperty("javax.net.ssl.trustStore");
/* 253 */       trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
/* 254 */       trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
/* 255 */       if (StringUtils.isNullOrEmpty(trustStoreType)) {
/* 256 */         trustStoreType = (String)propertySet.getStringProperty(PropertyKey.trustCertificateKeyStoreType).getInitialValue();
/*     */       }
/*     */       
/* 259 */       if (!StringUtils.isNullOrEmpty(trustStoreUrl)) {
/*     */         try {
/* 261 */           new URL(trustStoreUrl);
/* 262 */         } catch (MalformedURLException e) {
/* 263 */           trustStoreUrl = "file:" + trustStoreUrl;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 268 */     if (required && StringUtils.isNullOrEmpty(trustStoreUrl)) {
/* 269 */       throw new CJCommunicationsException("No truststore provided to verify the Server certificate.");
/*     */     }
/*     */     
/* 272 */     return new KeyStoreConf(trustStoreUrl, trustStorePassword, trustStoreType);
/*     */   }
/*     */   
/*     */   private static KeyStoreConf getKeyStoreConf(PropertySet propertySet) {
/* 276 */     String keyStoreUrl = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreUrl).getValue();
/* 277 */     String keyStorePassword = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStorePassword).getValue();
/* 278 */     String keyStoreType = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType).getValue();
/* 279 */     boolean fallbackToSystemKeyStore = ((Boolean)propertySet.getBooleanProperty(PropertyKey.fallbackToSystemKeyStore).getValue()).booleanValue();
/*     */     
/* 281 */     if (fallbackToSystemKeyStore && StringUtils.isNullOrEmpty(keyStoreUrl)) {
/* 282 */       keyStoreUrl = System.getProperty("javax.net.ssl.keyStore");
/* 283 */       keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
/* 284 */       keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
/* 285 */       if (StringUtils.isNullOrEmpty(keyStoreType)) {
/* 286 */         keyStoreType = (String)propertySet.getStringProperty(PropertyKey.clientCertificateKeyStoreType).getInitialValue();
/*     */       }
/*     */       
/* 289 */       if (!StringUtils.isNullOrEmpty(keyStoreUrl)) {
/*     */         try {
/* 291 */           new URL(keyStoreUrl);
/* 292 */         } catch (MalformedURLException e) {
/* 293 */           keyStoreUrl = "file:" + keyStoreUrl;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 298 */     return new KeyStoreConf(keyStoreUrl, keyStorePassword, keyStoreType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Socket performTlsHandshake(Socket rawSocket, SocketConnection socketConnection, ServerVersion serverVersion, Log log) throws IOException, SSLParamsException, FeatureNotAvailableException {
/* 322 */     PropertySet pset = socketConnection.getPropertySet();
/*     */     
/* 324 */     PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)pset.getEnumProperty(PropertyKey.sslMode).getValue();
/* 325 */     boolean verifyServerCert = (sslMode == PropertyDefinitions.SslMode.VERIFY_CA || sslMode == PropertyDefinitions.SslMode.VERIFY_IDENTITY);
/* 326 */     boolean fallbackToSystemTrustStore = ((Boolean)pset.getBooleanProperty(PropertyKey.fallbackToSystemTrustStore).getValue()).booleanValue();
/*     */ 
/*     */ 
/*     */     
/* 330 */     KeyStoreConf trustStore = !verifyServerCert ? new KeyStoreConf() : getTrustStoreConf(pset, (serverVersion == null && verifyServerCert && !fallbackToSystemTrustStore));
/* 331 */     KeyStoreConf keyStore = getKeyStoreConf(pset);
/*     */ 
/*     */ 
/*     */     
/* 335 */     SSLSocketFactory socketFactory = getSSLContext(keyStore, trustStore, fallbackToSystemTrustStore, verifyServerCert, (sslMode == PropertyDefinitions.SslMode.VERIFY_IDENTITY) ? socketConnection.getHost() : null, socketConnection.getExceptionInterceptor()).getSocketFactory();
/*     */     
/* 337 */     SSLSocket sslSocket = (SSLSocket)socketFactory.createSocket(rawSocket, socketConnection.getHost(), socketConnection.getPort(), true);
/*     */     
/* 339 */     String[] allowedProtocols = getAllowedProtocols(pset, serverVersion, sslSocket.getSupportedProtocols());
/* 340 */     sslSocket.setEnabledProtocols(allowedProtocols);
/*     */     
/* 342 */     String[] allowedCiphers = getAllowedCiphers(pset, Arrays.asList(sslSocket.getEnabledCipherSuites()));
/* 343 */     if (allowedCiphers != null) {
/* 344 */       sslSocket.setEnabledCipherSuites(allowedCiphers);
/*     */     }
/*     */     
/* 347 */     sslSocket.startHandshake();
/* 348 */     return sslSocket;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class X509TrustManagerWrapper
/*     */     implements X509TrustManager
/*     */   {
/* 355 */     private X509TrustManager origTm = null;
/*     */     private boolean verifyServerCert = false;
/* 357 */     private String hostName = null;
/* 358 */     private CertificateFactory certFactory = null;
/* 359 */     private PKIXParameters validatorParams = null;
/* 360 */     private CertPathValidator validator = null;
/*     */     
/*     */     public X509TrustManagerWrapper(X509TrustManager tm, boolean verifyServerCertificate, String hostName) throws CertificateException {
/* 363 */       this.origTm = tm;
/* 364 */       this.verifyServerCert = verifyServerCertificate;
/* 365 */       this.hostName = hostName;
/*     */       
/* 367 */       if (verifyServerCertificate) {
/*     */         try {
/* 369 */           Set<TrustAnchor> anch = (Set<TrustAnchor>)Arrays.<X509Certificate>stream(tm.getAcceptedIssuers()).map(c -> new TrustAnchor(c, null)).collect(Collectors.toSet());
/* 370 */           this.validatorParams = new PKIXParameters(anch);
/* 371 */           this.validatorParams.setRevocationEnabled(false);
/* 372 */           this.validator = CertPathValidator.getInstance("PKIX");
/* 373 */           this.certFactory = CertificateFactory.getInstance("X.509");
/* 374 */         } catch (Exception e) {
/* 375 */           throw new CertificateException(e);
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public X509TrustManagerWrapper(boolean verifyServerCertificate, String hostName) {
/* 382 */       this.verifyServerCert = verifyServerCertificate;
/* 383 */       this.hostName = hostName;
/*     */     }
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 387 */       return (this.origTm != null) ? this.origTm.getAcceptedIssuers() : new X509Certificate[0];
/*     */     }
/*     */     
/*     */     public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 391 */       for (int i = 0; i < chain.length; i++) {
/* 392 */         chain[i].checkValidity();
/*     */       }
/*     */       
/* 395 */       if (this.validatorParams != null) {
/* 396 */         X509CertSelector certSelect = new X509CertSelector();
/* 397 */         certSelect.setSerialNumber(chain[0].getSerialNumber());
/*     */         
/*     */         try {
/* 400 */           CertPath certPath = this.certFactory.generateCertPath(Arrays.asList((Certificate[])chain));
/*     */           
/* 402 */           CertPathValidatorResult result = this.validator.validate(certPath, this.validatorParams);
/*     */           
/* 404 */           ((PKIXCertPathValidatorResult)result).getTrustAnchor().getTrustedCert().checkValidity();
/* 405 */         } catch (InvalidAlgorithmParameterException e) {
/* 406 */           throw new CertificateException(e);
/* 407 */         } catch (CertPathValidatorException e) {
/* 408 */           throw new CertificateException(e);
/*     */         } 
/*     */       } 
/*     */       
/* 412 */       if (this.verifyServerCert) {
/* 413 */         if (this.origTm != null) {
/* 414 */           this.origTm.checkServerTrusted(chain, authType);
/*     */         } else {
/* 416 */           throw new CertificateException("Can't verify server certificate because no trust manager is found.");
/*     */         } 
/*     */ 
/*     */         
/* 420 */         if (this.hostName != null) {
/* 421 */           boolean hostNameVerified = false;
/*     */ 
/*     */ 
/*     */           
/* 425 */           Collection<List<?>> subjectAltNames = chain[0].getSubjectAlternativeNames();
/* 426 */           if (subjectAltNames != null) {
/* 427 */             boolean sanVerification = false;
/* 428 */             for (List<?> san : subjectAltNames) {
/* 429 */               Integer nameType = (Integer)san.get(0);
/*     */ 
/*     */               
/* 432 */               if (nameType.intValue() == 2) {
/* 433 */                 sanVerification = true;
/* 434 */                 if (verifyHostName((String)san.get(1))) {
/*     */                   
/* 436 */                   hostNameVerified = true; break;
/*     */                 }  continue;
/*     */               } 
/* 439 */               if (nameType.intValue() == 7) {
/* 440 */                 sanVerification = true;
/* 441 */                 if (this.hostName.equalsIgnoreCase((String)san.get(1))) {
/*     */                   
/* 443 */                   hostNameVerified = true;
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             } 
/* 448 */             if (sanVerification && !hostNameVerified) {
/* 449 */               throw new CertificateException("Server identity verification failed. None of the DNS or IP Subject Alternative Name entries matched the server hostname/IP '" + this.hostName + "'.");
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 454 */           if (!hostNameVerified) {
/*     */ 
/*     */             
/* 457 */             String dn = chain[0].getSubjectX500Principal().getName("RFC2253");
/* 458 */             String cn = null;
/*     */             try {
/* 460 */               LdapName ldapDN = new LdapName(dn);
/* 461 */               for (Rdn rdn : ldapDN.getRdns()) {
/* 462 */                 if (rdn.getType().equalsIgnoreCase("CN")) {
/* 463 */                   cn = rdn.getValue().toString();
/*     */                   break;
/*     */                 } 
/*     */               } 
/* 467 */             } catch (InvalidNameException e) {
/* 468 */               throw new CertificateException("Failed to retrieve the Common Name (CN) from the server certificate.");
/*     */             } 
/*     */             
/* 471 */             if (!verifyHostName(cn)) {
/* 472 */               throw new CertificateException("Server identity verification failed. The certificate Common Name '" + cn + "' does not match '" + this.hostName + "'.");
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 483 */       this.origTm.checkClientTrusted(chain, authType);
/*     */     }
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
/*     */     private boolean verifyHostName(String ptn) {
/* 496 */       int indexOfStar = ptn.indexOf('*');
/* 497 */       if (indexOfStar >= 0 && indexOfStar < ptn.indexOf('.')) {
/* 498 */         String head = ptn.substring(0, indexOfStar);
/* 499 */         String tail = ptn.substring(indexOfStar + 1);
/*     */         
/* 501 */         return (StringUtils.startsWithIgnoreCase(this.hostName, head) && StringUtils.endsWithIgnoreCase(this.hostName, tail) && this.hostName
/* 502 */           .substring(head.length(), this.hostName.length() - tail.length()).indexOf('.') == -1);
/*     */       } 
/* 504 */       return this.hostName.equalsIgnoreCase(ptn);
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
/*     */   public static SSLContext getSSLContext(KeyStoreConf clientCertificateKeyStore, KeyStoreConf trustCertificateKeyStore, boolean fallbackToDefaultTrustStore, boolean verifyServerCert, String hostName, ExceptionInterceptor exceptionInterceptor) throws SSLParamsException {
/* 529 */     String clientCertificateKeyStoreUrl = clientCertificateKeyStore.keyStoreUrl;
/* 530 */     String clientCertificateKeyStoreType = clientCertificateKeyStore.keyStoreType;
/* 531 */     String clientCertificateKeyStorePassword = clientCertificateKeyStore.keyStorePassword;
/* 532 */     String trustCertificateKeyStoreUrl = trustCertificateKeyStore.keyStoreUrl;
/* 533 */     String trustCertificateKeyStoreType = trustCertificateKeyStore.keyStoreType;
/* 534 */     String trustCertificateKeyStorePassword = trustCertificateKeyStore.keyStorePassword;
/*     */     
/* 536 */     TrustManagerFactory tmf = null;
/* 537 */     KeyManagerFactory kmf = null;
/*     */     
/* 539 */     KeyManager[] kms = null;
/* 540 */     List<TrustManager> tms = new ArrayList<>();
/*     */     
/*     */     try {
/* 543 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/* 544 */       kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/* 545 */     } catch (NoSuchAlgorithmException nsae) {
/* 546 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", nsae, exceptionInterceptor);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 551 */     if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) {
/* 552 */       InputStream ksIS = null;
/*     */       try {
/* 554 */         if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreType)) {
/* 555 */           KeyStore clientKeyStore = KeyStore.getInstance(clientCertificateKeyStoreType);
/* 556 */           URL ksURL = new URL(clientCertificateKeyStoreUrl);
/* 557 */           char[] password = (clientCertificateKeyStorePassword == null) ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
/* 558 */           ksIS = ksURL.openStream();
/* 559 */           clientKeyStore.load(ksIS, password);
/* 560 */           kmf.init(clientKeyStore, password);
/* 561 */           kms = kmf.getKeyManagers();
/*     */         } 
/* 563 */       } catch (UnrecoverableKeyException uke) {
/* 564 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not recover keys from client keystore.  Check password?", uke, exceptionInterceptor);
/*     */       }
/* 566 */       catch (NoSuchAlgorithmException nsae) {
/* 567 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Unsupported keystore algorithm [" + nsae.getMessage() + "]", nsae, exceptionInterceptor);
/*     */       }
/* 569 */       catch (KeyStoreException kse) {
/* 570 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not create KeyStore instance [" + kse.getMessage() + "]", kse, exceptionInterceptor);
/*     */       }
/* 572 */       catch (CertificateException nsae) {
/* 573 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl, nsae, exceptionInterceptor);
/*     */       }
/* 575 */       catch (MalformedURLException mue) {
/* 576 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, clientCertificateKeyStoreUrl + " does not appear to be a valid URL.", mue, exceptionInterceptor);
/*     */       }
/* 578 */       catch (IOException ioe) {
/* 579 */         throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", ioe, exceptionInterceptor);
/*     */       } finally {
/*     */         
/* 582 */         if (ksIS != null) {
/*     */           try {
/* 584 */             ksIS.close();
/* 585 */           } catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 592 */     InputStream trustStoreIS = null;
/*     */     try {
/* 594 */       String trustStoreType = "";
/* 595 */       char[] trustStorePassword = null;
/* 596 */       KeyStore trustKeyStore = null;
/*     */       
/* 598 */       if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl) && !StringUtils.isNullOrEmpty(trustCertificateKeyStoreType)) {
/* 599 */         trustStoreType = trustCertificateKeyStoreType;
/* 600 */         trustStorePassword = (trustCertificateKeyStorePassword == null) ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
/* 601 */         trustStoreIS = (new URL(trustCertificateKeyStoreUrl)).openStream();
/*     */         
/* 603 */         trustKeyStore = KeyStore.getInstance(trustStoreType);
/* 604 */         trustKeyStore.load(trustStoreIS, trustStorePassword);
/*     */       } 
/*     */       
/* 607 */       if (trustKeyStore != null || (verifyServerCert && fallbackToDefaultTrustStore)) {
/* 608 */         tmf.init(trustKeyStore);
/*     */ 
/*     */         
/* 611 */         TrustManager[] origTms = tmf.getTrustManagers();
/*     */         
/* 613 */         for (TrustManager tm : origTms)
/*     */         {
/* 615 */           tms.add((tm instanceof X509TrustManager) ? new X509TrustManagerWrapper((X509TrustManager)tm, verifyServerCert, hostName) : tm);
/*     */         }
/*     */       } 
/* 618 */     } catch (MalformedURLException e) {
/* 619 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, trustCertificateKeyStoreUrl + " does not appear to be a valid URL.", e, exceptionInterceptor);
/*     */     }
/* 621 */     catch (NoSuchAlgorithmException e) {
/* 622 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Unsupported keystore algorithm [" + e.getMessage() + "]", e, exceptionInterceptor);
/*     */     }
/* 624 */     catch (KeyStoreException e) {
/* 625 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not create KeyStore instance [" + e.getMessage() + "]", e, exceptionInterceptor);
/*     */     }
/* 627 */     catch (CertificateException e) {
/* 628 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl, e, exceptionInterceptor);
/*     */     }
/* 630 */     catch (IOException e) {
/* 631 */       throw (SSLParamsException)ExceptionFactory.createException(SSLParamsException.class, "Cannot open " + trustCertificateKeyStoreUrl + " [" + e.getMessage() + "]", e, exceptionInterceptor);
/*     */     } finally {
/*     */       
/* 634 */       if (trustStoreIS != null) {
/*     */         try {
/* 636 */           trustStoreIS.close();
/* 637 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 644 */     if (tms.size() == 0) {
/* 645 */       tms.add(new X509TrustManagerWrapper(verifyServerCert, hostName));
/*     */     }
/*     */     
/*     */     try {
/* 649 */       SSLContext sslContext = SSLContext.getInstance("TLS");
/* 650 */       sslContext.init(kms, tms.<TrustManager>toArray(new TrustManager[tms.size()]), null);
/*     */       
/* 652 */       return sslContext;
/*     */     }
/* 654 */     catch (NoSuchAlgorithmException nsae) {
/* 655 */       throw new SSLParamsException("TLS is not a valid SSL protocol.", nsae);
/* 656 */     } catch (KeyManagementException kme) {
/* 657 */       throw new SSLParamsException("KeyManagementException: " + kme.getMessage(), kme);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean isSSLEstablished(Socket socket) {
/* 662 */     return (socket == null) ? false : SSLSocket.class.isAssignableFrom(socket.getClass());
/*     */   }
/*     */   
/*     */   public static RSAPublicKey decodeRSAPublicKey(String key) throws RSAException {
/* 666 */     if (key == null) {
/* 667 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, "Key parameter is null");
/*     */     }
/*     */     
/* 670 */     int offset = key.indexOf("\n") + 1;
/* 671 */     int len = key.indexOf("-----END PUBLIC KEY-----") - offset;
/*     */ 
/*     */     
/* 674 */     byte[] certificateData = Base64Decoder.decode(key.getBytes(), offset, len);
/*     */     
/* 676 */     X509EncodedKeySpec spec = new X509EncodedKeySpec(certificateData);
/*     */     try {
/* 678 */       KeyFactory kf = KeyFactory.getInstance("RSA");
/* 679 */       return (RSAPublicKey)kf.generatePublic(spec);
/* 680 */     } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
/* 681 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, "Unable to decode public key", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, String transformation) throws RSAException {
/*     */     try {
/* 687 */       Cipher cipher = Cipher.getInstance(transformation);
/* 688 */       cipher.init(1, key);
/* 689 */       return cipher.doFinal(source);
/* 690 */     } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException|java.security.InvalidKeyException|javax.crypto.IllegalBlockSizeException|javax.crypto.BadPaddingException e) {
/* 691 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key) throws RSAException {
/* 696 */     return encryptWithRSAPublicKey(source, key, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
/*     */   }
/*     */   
/*     */   public static RSAPrivateKey decodeRSAPrivateKey(String key) throws RSAException {
/* 700 */     if (key == null) {
/* 701 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, "Key parameter is null");
/*     */     }
/*     */     
/* 704 */     String keyData = key.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll("\\R", "").replace("-----END PRIVATE KEY-----", "");
/* 705 */     byte[] decodedKeyData = Base64.getDecoder().decode(keyData);
/*     */     
/*     */     try {
/* 708 */       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
/* 709 */       return (RSAPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKeyData));
/* 710 */     } catch (NoSuchAlgorithmException|java.security.spec.InvalidKeySpecException e) {
/* 711 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, "Unable to decode private key", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static byte[] sign(byte[] source, RSAPrivateKey privateKey) throws RSAException {
/*     */     try {
/* 717 */       Signature signature = Signature.getInstance("SHA256withRSA");
/* 718 */       signature.initSign(privateKey);
/* 719 */       signature.update(source);
/* 720 */       return signature.sign();
/* 721 */     } catch (NoSuchAlgorithmException|java.security.InvalidKeyException|java.security.SignatureException e) {
/* 722 */       throw (RSAException)ExceptionFactory.createException(RSAException.class, e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ExportControlled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */