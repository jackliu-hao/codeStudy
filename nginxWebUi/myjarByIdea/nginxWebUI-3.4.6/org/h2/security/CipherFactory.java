package org.h2.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Properties;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

public class CipherFactory {
   public static final String KEYSTORE_PASSWORD = "h2pass";
   public static final String LEGACY_ALGORITHMS_SECURITY_KEY = "jdk.tls.legacyAlgorithms";
   public static final String DEFAULT_LEGACY_ALGORITHMS = getLegacyAlgorithmsSilently();
   private static final String KEYSTORE = "~/.h2.keystore";
   private static final String KEYSTORE_KEY = "javax.net.ssl.keyStore";
   private static final String KEYSTORE_PASSWORD_KEY = "javax.net.ssl.keyStorePassword";

   private CipherFactory() {
   }

   public static BlockCipher getBlockCipher(String var0) {
      if ("XTEA".equalsIgnoreCase(var0)) {
         return new XTEA();
      } else if ("AES".equalsIgnoreCase(var0)) {
         return new AES();
      } else if ("FOG".equalsIgnoreCase(var0)) {
         return new Fog();
      } else {
         throw DbException.get(90055, var0);
      }
   }

   public static Socket createSocket(InetAddress var0, int var1) throws IOException {
      setKeystore();
      SSLSocketFactory var2 = (SSLSocketFactory)SSLSocketFactory.getDefault();
      SSLSocket var3 = (SSLSocket)var2.createSocket();
      var3.connect(new InetSocketAddress(var0, var1), SysProperties.SOCKET_CONNECT_TIMEOUT);
      var3.setEnabledProtocols(disableSSL(var3.getEnabledProtocols()));
      if (SysProperties.ENABLE_ANONYMOUS_TLS) {
         String[] var4 = enableAnonymous(var3.getEnabledCipherSuites(), var3.getSupportedCipherSuites());
         var3.setEnabledCipherSuites(var4);
      }

      return var3;
   }

   public static ServerSocket createServerSocket(int var0, InetAddress var1) throws IOException {
      Object var2 = null;
      if (SysProperties.ENABLE_ANONYMOUS_TLS) {
         removeAnonFromLegacyAlgorithms();
      }

      setKeystore();
      ServerSocketFactory var3 = SSLServerSocketFactory.getDefault();
      SSLServerSocket var4;
      if (var1 == null) {
         var4 = (SSLServerSocket)var3.createServerSocket(var0);
      } else {
         var4 = (SSLServerSocket)var3.createServerSocket(var0, 0, var1);
      }

      var4.setEnabledProtocols(disableSSL(var4.getEnabledProtocols()));
      if (SysProperties.ENABLE_ANONYMOUS_TLS) {
         String[] var5 = enableAnonymous(var4.getEnabledCipherSuites(), var4.getSupportedCipherSuites());
         var4.setEnabledCipherSuites(var5);
      }

      return var4;
   }

   public static String removeDhAnonFromCommaSeparatedList(String var0) {
      if (var0 == null) {
         return var0;
      } else {
         LinkedList var1 = new LinkedList(Arrays.asList(var0.split("\\s*,\\s*")));
         boolean var2 = var1.remove("DH_anon");
         boolean var3 = var1.remove("ECDH_anon");
         if (!var2 && !var3) {
            return var0;
         } else {
            String var4 = Arrays.toString(var1.toArray(new String[var1.size()]));
            return !var1.isEmpty() ? var4.substring(1, var4.length() - 1) : "";
         }
      }
   }

   public static synchronized void removeAnonFromLegacyAlgorithms() {
      String var0 = getLegacyAlgorithmsSilently();
      if (var0 != null) {
         String var1 = removeDhAnonFromCommaSeparatedList(var0);
         if (!var0.equals(var1)) {
            setLegacyAlgorithmsSilently(var1);
         }

      }
   }

   public static synchronized void resetDefaultLegacyAlgorithms() {
      setLegacyAlgorithmsSilently(DEFAULT_LEGACY_ALGORITHMS);
   }

   public static String getLegacyAlgorithmsSilently() {
      String var0 = null;

      try {
         var0 = Security.getProperty("jdk.tls.legacyAlgorithms");
      } catch (SecurityException var2) {
      }

      return var0;
   }

   private static void setLegacyAlgorithmsSilently(String var0) {
      if (var0 != null) {
         try {
            Security.setProperty("jdk.tls.legacyAlgorithms", var0);
         } catch (SecurityException var2) {
         }

      }
   }

   private static byte[] getKeyStoreBytes(KeyStore var0, String var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         var0.store(var2, var1.toCharArray());
      } catch (Exception var4) {
         throw DataUtils.convertToIOException(var4);
      }

      return var2.toByteArray();
   }

   public static KeyStore getKeyStore(String var0) throws IOException {
      try {
         KeyStore var1 = KeyStore.getInstance(KeyStore.getDefaultType());
         var1.load((InputStream)null, var0.toCharArray());
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         var1.load((InputStream)null, var0.toCharArray());
         PKCS8EncodedKeySpec var3 = new PKCS8EncodedKeySpec(StringUtils.convertHexToBytes("30820277020100300d06092a864886f70d0101010500048202613082025d02010002818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d02030100010281810099aa4ff4d0a09a5af0bd953cb10c4d08c3d98df565664ac5582e494314d5c3c92dddedd5d316a32a206be4ec084616fe57be15e27cad111aa3c21fa79e32258c6ca8430afc69eddd52d3b751b37da6b6860910b94653192c0db1d02abcfd6ce14c01f238eec7c20bd3bb750940004bacba2880349a9494d10e139ecb2355d101024100ffdc3defd9c05a2d377ef6019fa62b3fbd5b0020a04cc8533bca730e1f6fcf5dfceea1b044fbe17d9eababfbc7d955edad6bc60f9be826ad2c22ba77d19a9f65024100dc28d43fdbbc93852cc3567093157702bc16f156f709fb7db0d9eec028f41fd0edcd17224c866e66be1744141fb724a10fd741c8a96afdd9141b36d67fff6309024077b1cddbde0f69604bdcfe33263fb36ddf24aa3b9922327915b890f8a36648295d0139ecdf68c245652c4489c6257b58744fbdd961834a4cab201801a3b1e52d024100b17142e8991d1b350a0802624759d48ae2b8071a158ff91fabeb6a8f7c328e762143dc726b8529f42b1fab6220d1c676fdc27ba5d44e847c72c52064afd351a902407c6e23fe35bcfcd1a662aa82a2aa725fcece311644d5b6e3894853fd4ce9fe78218c957b1ff03fc9e5ef8ffeb6bd58235f6a215c97d354fdace7e781e4a63e8b"));
         PrivateKey var4 = var2.generatePrivate(var3);
         Certificate[] var5 = new Certificate[]{CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(StringUtils.convertHexToBytes("3082018b3081f502044295ce6b300d06092a864886f70d0101040500300d310b3009060355040313024832301e170d3035303532363133323630335a170d3337303933303036353734375a300d310b300906035504031302483230819f300d06092a864886f70d010101050003818d0030818902818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d0203010001300d06092a864886f70d01010405000381810083f4401a279453701bef9a7681a5b8b24f153f7d18c7c892133d97bd5f13736be7505290a445a7d5ceb75522403e5097515cd966ded6351ff60d5193de34cd36e5cb04d380398e66286f99923fd92296645fd4ada45844d194dfd815e6cd57f385c117be982809028bba1116c85740b3d27a55b1a0948bf291ddba44bed337b9")))};
         var1.setKeyEntry("h2", var4, var0.toCharArray(), var5);
         return var1;
      } catch (Exception var6) {
         throw DataUtils.convertToIOException(var6);
      }
   }

   private static void setKeystore() throws IOException {
      Properties var0 = System.getProperties();
      if (var0.getProperty("javax.net.ssl.keyStore") == null) {
         String var1 = "~/.h2.keystore";
         byte[] var2 = getKeyStoreBytes(getKeyStore("h2pass"), "h2pass");
         boolean var3 = true;
         if (FileUtils.exists(var1) && FileUtils.size(var1) == (long)var2.length) {
            InputStream var4 = FileUtils.newInputStream(var1);
            byte[] var5 = IOUtils.readBytesAndClose(var4, 0);
            if (var5 != null && Arrays.equals(var2, var5)) {
               var3 = false;
            }
         }

         if (var3) {
            try {
               OutputStream var7 = FileUtils.newOutputStream(var1, false);
               var7.write(var2);
               var7.close();
            } catch (Exception var6) {
               throw DataUtils.convertToIOException(var6);
            }
         }

         String var8 = FileUtils.toRealPath(var1);
         System.setProperty("javax.net.ssl.keyStore", var8);
      }

      if (var0.getProperty("javax.net.ssl.keyStorePassword") == null) {
         System.setProperty("javax.net.ssl.keyStorePassword", "h2pass");
      }

   }

   private static String[] enableAnonymous(String[] var0, String[] var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (!var6.startsWith("SSL") && var6.contains("_anon_") && (var6.contains("_AES_") || var6.contains("_3DES_")) && var6.contains("_SHA")) {
            var2.add(var6);
         }
      }

      Collections.addAll(var2, var0);
      return (String[])var2.toArray(new String[0]);
   }

   private static String[] disableSSL(String[] var0) {
      HashSet var1 = new HashSet();
      String[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!var5.startsWith("SSL")) {
            var1.add(var5);
         }
      }

      return (String[])var1.toArray(new String[0]);
   }
}
