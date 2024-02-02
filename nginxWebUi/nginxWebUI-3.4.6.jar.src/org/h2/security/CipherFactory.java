/*     */ package org.h2.security;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyStore;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Security;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Properties;
/*     */ import javax.net.ServerSocketFactory;
/*     */ import javax.net.ssl.SSLServerSocket;
/*     */ import javax.net.ssl.SSLServerSocketFactory;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CipherFactory
/*     */ {
/*     */   public static final String KEYSTORE_PASSWORD = "h2pass";
/*     */   public static final String LEGACY_ALGORITHMS_SECURITY_KEY = "jdk.tls.legacyAlgorithms";
/*  69 */   public static final String DEFAULT_LEGACY_ALGORITHMS = getLegacyAlgorithmsSilently();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEYSTORE = "~/.h2.keystore";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEYSTORE_KEY = "javax.net.ssl.keyStore";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String KEYSTORE_PASSWORD_KEY = "javax.net.ssl.keyStorePassword";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BlockCipher getBlockCipher(String paramString) {
/*  90 */     if ("XTEA".equalsIgnoreCase(paramString))
/*  91 */       return new XTEA(); 
/*  92 */     if ("AES".equalsIgnoreCase(paramString))
/*  93 */       return new AES(); 
/*  94 */     if ("FOG".equalsIgnoreCase(paramString)) {
/*  95 */       return new Fog();
/*     */     }
/*  97 */     throw DbException.get(90055, paramString);
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
/*     */   public static Socket createSocket(InetAddress paramInetAddress, int paramInt) throws IOException {
/* 111 */     setKeystore();
/* 112 */     SSLSocketFactory sSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
/* 113 */     SSLSocket sSLSocket = (SSLSocket)sSLSocketFactory.createSocket();
/* 114 */     sSLSocket.connect(new InetSocketAddress(paramInetAddress, paramInt), SysProperties.SOCKET_CONNECT_TIMEOUT);
/*     */     
/* 116 */     sSLSocket.setEnabledProtocols(
/* 117 */         disableSSL(sSLSocket.getEnabledProtocols()));
/* 118 */     if (SysProperties.ENABLE_ANONYMOUS_TLS) {
/* 119 */       String[] arrayOfString = enableAnonymous(sSLSocket
/* 120 */           .getEnabledCipherSuites(), sSLSocket
/* 121 */           .getSupportedCipherSuites());
/* 122 */       sSLSocket.setEnabledCipherSuites(arrayOfString);
/*     */     } 
/* 124 */     return sSLSocket;
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
/*     */   public static ServerSocket createServerSocket(int paramInt, InetAddress paramInetAddress) throws IOException {
/* 144 */     SSLServerSocket sSLServerSocket2, sSLServerSocket1 = null;
/* 145 */     if (SysProperties.ENABLE_ANONYMOUS_TLS) {
/* 146 */       removeAnonFromLegacyAlgorithms();
/*     */     }
/* 148 */     setKeystore();
/* 149 */     ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
/*     */     
/* 151 */     if (paramInetAddress == null) {
/* 152 */       sSLServerSocket2 = (SSLServerSocket)serverSocketFactory.createServerSocket(paramInt);
/*     */     } else {
/* 154 */       sSLServerSocket2 = (SSLServerSocket)serverSocketFactory.createServerSocket(paramInt, 0, paramInetAddress);
/*     */     } 
/* 156 */     sSLServerSocket2.setEnabledProtocols(
/* 157 */         disableSSL(sSLServerSocket2.getEnabledProtocols()));
/* 158 */     if (SysProperties.ENABLE_ANONYMOUS_TLS) {
/* 159 */       String[] arrayOfString = enableAnonymous(sSLServerSocket2
/* 160 */           .getEnabledCipherSuites(), sSLServerSocket2
/* 161 */           .getSupportedCipherSuites());
/* 162 */       sSLServerSocket2.setEnabledCipherSuites(arrayOfString);
/*     */     } 
/*     */     
/* 165 */     sSLServerSocket1 = sSLServerSocket2;
/* 166 */     return sSLServerSocket1;
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
/*     */   public static String removeDhAnonFromCommaSeparatedList(String paramString) {
/* 178 */     if (paramString == null) {
/* 179 */       return paramString;
/*     */     }
/* 181 */     LinkedList linkedList = new LinkedList(Arrays.asList((Object[])paramString.split("\\s*,\\s*")));
/* 182 */     boolean bool1 = linkedList.remove("DH_anon");
/* 183 */     boolean bool2 = linkedList.remove("ECDH_anon");
/* 184 */     if (bool1 || bool2) {
/* 185 */       String str = Arrays.toString(linkedList.toArray((Object[])new String[linkedList.size()]));
/* 186 */       return !linkedList.isEmpty() ? str.substring(1, str.length() - 1) : "";
/*     */     } 
/* 188 */     return paramString;
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
/*     */   public static synchronized void removeAnonFromLegacyAlgorithms() {
/* 207 */     String str1 = getLegacyAlgorithmsSilently();
/* 208 */     if (str1 == null) {
/*     */       return;
/*     */     }
/* 211 */     String str2 = removeDhAnonFromCommaSeparatedList(str1);
/* 212 */     if (!str1.equals(str2)) {
/* 213 */       setLegacyAlgorithmsSilently(str2);
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
/*     */   public static synchronized void resetDefaultLegacyAlgorithms() {
/* 227 */     setLegacyAlgorithmsSilently(DEFAULT_LEGACY_ALGORITHMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getLegacyAlgorithmsSilently() {
/* 238 */     String str = null;
/*     */     try {
/* 240 */       str = Security.getProperty("jdk.tls.legacyAlgorithms");
/* 241 */     } catch (SecurityException securityException) {}
/*     */ 
/*     */     
/* 244 */     return str;
/*     */   }
/*     */   
/*     */   private static void setLegacyAlgorithmsSilently(String paramString) {
/* 248 */     if (paramString == null) {
/*     */       return;
/*     */     }
/*     */     try {
/* 252 */       Security.setProperty("jdk.tls.legacyAlgorithms", paramString);
/* 253 */     } catch (SecurityException securityException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] getKeyStoreBytes(KeyStore paramKeyStore, String paramString) throws IOException {
/* 260 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*     */     try {
/* 262 */       paramKeyStore.store(byteArrayOutputStream, paramString.toCharArray());
/* 263 */     } catch (Exception exception) {
/* 264 */       throw DataUtils.convertToIOException(exception);
/*     */     } 
/* 266 */     return byteArrayOutputStream.toByteArray();
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
/*     */   public static KeyStore getKeyStore(String paramString) throws IOException {
/*     */     try {
/* 286 */       KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
/*     */       
/* 288 */       keyStore.load(null, paramString.toCharArray());
/* 289 */       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
/* 290 */       keyStore.load(null, paramString.toCharArray());
/*     */       
/* 292 */       PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(StringUtils.convertHexToBytes("30820277020100300d06092a864886f70d0101010500048202613082025d02010002818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d02030100010281810099aa4ff4d0a09a5af0bd953cb10c4d08c3d98df565664ac5582e494314d5c3c92dddedd5d316a32a206be4ec084616fe57be15e27cad111aa3c21fa79e32258c6ca8430afc69eddd52d3b751b37da6b6860910b94653192c0db1d02abcfd6ce14c01f238eec7c20bd3bb750940004bacba2880349a9494d10e139ecb2355d101024100ffdc3defd9c05a2d377ef6019fa62b3fbd5b0020a04cc8533bca730e1f6fcf5dfceea1b044fbe17d9eababfbc7d955edad6bc60f9be826ad2c22ba77d19a9f65024100dc28d43fdbbc93852cc3567093157702bc16f156f709fb7db0d9eec028f41fd0edcd17224c866e66be1744141fb724a10fd741c8a96afdd9141b36d67fff6309024077b1cddbde0f69604bdcfe33263fb36ddf24aa3b9922327915b890f8a36648295d0139ecdf68c245652c4489c6257b58744fbdd961834a4cab201801a3b1e52d024100b17142e8991d1b350a0802624759d48ae2b8071a158ff91fabeb6a8f7c328e762143dc726b8529f42b1fab6220d1c676fdc27ba5d44e847c72c52064afd351a902407c6e23fe35bcfcd1a662aa82a2aa725fcece311644d5b6e3894853fd4ce9fe78218c957b1ff03fc9e5ef8ffeb6bd58235f6a215c97d354fdace7e781e4a63e8b"));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 325 */       PrivateKey privateKey = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
/*     */ 
/*     */       
/* 328 */       Certificate[] arrayOfCertificate = { CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(
/*     */               
/* 330 */               StringUtils.convertHexToBytes("3082018b3081f502044295ce6b300d06092a864886f70d0101040500300d310b3009060355040313024832301e170d3035303532363133323630335a170d3337303933303036353734375a300d310b300906035504031302483230819f300d06092a864886f70d010101050003818d0030818902818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d0203010001300d06092a864886f70d01010405000381810083f4401a279453701bef9a7681a5b8b24f153f7d18c7c892133d97bd5f13736be7505290a445a7d5ceb75522403e5097515cd966ded6351ff60d5193de34cd36e5cb04d380398e66286f99923fd92296645fd4ada45844d194dfd815e6cd57f385c117be982809028bba1116c85740b3d27a55b1a0948bf291ddba44bed337b9"))) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 351 */       keyStore.setKeyEntry("h2", privateKey, paramString.toCharArray(), arrayOfCertificate);
/*     */       
/* 353 */       return keyStore;
/* 354 */     } catch (Exception exception) {
/* 355 */       throw DataUtils.convertToIOException(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void setKeystore() throws IOException {
/* 360 */     Properties properties = System.getProperties();
/* 361 */     if (properties.getProperty("javax.net.ssl.keyStore") == null) {
/* 362 */       String str1 = "~/.h2.keystore";
/* 363 */       byte[] arrayOfByte = getKeyStoreBytes(getKeyStore("h2pass"), "h2pass");
/*     */       
/* 365 */       boolean bool = true;
/* 366 */       if (FileUtils.exists(str1) && FileUtils.size(str1) == arrayOfByte.length) {
/*     */         
/* 368 */         InputStream inputStream = FileUtils.newInputStream(str1);
/* 369 */         byte[] arrayOfByte1 = IOUtils.readBytesAndClose(inputStream, 0);
/* 370 */         if (arrayOfByte1 != null && Arrays.equals(arrayOfByte, arrayOfByte1)) {
/* 371 */           bool = false;
/*     */         }
/*     */       } 
/* 374 */       if (bool) {
/*     */         try {
/* 376 */           OutputStream outputStream = FileUtils.newOutputStream(str1, false);
/* 377 */           outputStream.write(arrayOfByte);
/* 378 */           outputStream.close();
/* 379 */         } catch (Exception exception) {
/* 380 */           throw DataUtils.convertToIOException(exception);
/*     */         } 
/*     */       }
/* 383 */       String str2 = FileUtils.toRealPath(str1);
/* 384 */       System.setProperty("javax.net.ssl.keyStore", str2);
/*     */     } 
/* 386 */     if (properties.getProperty("javax.net.ssl.keyStorePassword") == null) {
/* 387 */       System.setProperty("javax.net.ssl.keyStorePassword", "h2pass");
/*     */     }
/*     */   }
/*     */   
/*     */   private static String[] enableAnonymous(String[] paramArrayOfString1, String[] paramArrayOfString2) {
/* 392 */     LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
/* 393 */     for (String str : paramArrayOfString2) {
/* 394 */       if (!str.startsWith("SSL") && str.contains("_anon_") && (str
/* 395 */         .contains("_AES_") || str.contains("_3DES_")) && str.contains("_SHA")) {
/* 396 */         linkedHashSet.add(str);
/*     */       }
/*     */     } 
/* 399 */     Collections.addAll(linkedHashSet, paramArrayOfString1);
/* 400 */     return (String[])linkedHashSet.toArray((Object[])new String[0]);
/*     */   }
/*     */   
/*     */   private static String[] disableSSL(String[] paramArrayOfString) {
/* 404 */     HashSet<String> hashSet = new HashSet();
/* 405 */     for (String str : paramArrayOfString) {
/* 406 */       if (!str.startsWith("SSL")) {
/* 407 */         hashSet.add(str);
/*     */       }
/*     */     } 
/* 410 */     return hashSet.<String>toArray(new String[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\CipherFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */