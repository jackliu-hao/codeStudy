package cn.hutool.crypto;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyUtil {
   public static final String KEY_TYPE_JKS = "JKS";
   public static final String KEY_TYPE_JCEKS = "jceks";
   public static final String KEY_TYPE_PKCS12 = "pkcs12";
   public static final String CERT_TYPE_X509 = "X.509";
   public static final int DEFAULT_KEY_SIZE = 1024;
   public static final String SM2_DEFAULT_CURVE = "sm2p256v1";

   public static SecretKey generateKey(String algorithm) {
      return generateKey(algorithm, -1);
   }

   public static SecretKey generateKey(String algorithm, int keySize) {
      return generateKey(algorithm, keySize, (SecureRandom)null);
   }

   public static SecretKey generateKey(String algorithm, int keySize, SecureRandom random) {
      algorithm = getMainAlgorithm(algorithm);
      KeyGenerator keyGenerator = getKeyGenerator(algorithm);
      if (keySize <= 0 && SymmetricAlgorithm.AES.getValue().equals(algorithm)) {
         keySize = 128;
      }

      if (keySize > 0) {
         if (null == random) {
            keyGenerator.init(keySize);
         } else {
            keyGenerator.init(keySize, random);
         }
      }

      return keyGenerator.generateKey();
   }

   public static SecretKey generateKey(String algorithm, byte[] key) {
      Assert.notBlank(algorithm, "Algorithm is blank!");
      Object secretKey;
      if (algorithm.startsWith("PBE")) {
         secretKey = generatePBEKey(algorithm, null == key ? null : StrUtil.utf8Str(key).toCharArray());
      } else if (algorithm.startsWith("DES")) {
         secretKey = generateDESKey(algorithm, key);
      } else {
         secretKey = null == key ? generateKey(algorithm) : new SecretKeySpec(key, algorithm);
      }

      return (SecretKey)secretKey;
   }

   public static SecretKey generateDESKey(String algorithm, byte[] key) {
      if (!StrUtil.isBlank(algorithm) && algorithm.startsWith("DES")) {
         SecretKey secretKey;
         if (null == key) {
            secretKey = generateKey(algorithm);
         } else {
            Object keySpec;
            try {
               if (algorithm.startsWith("DESede")) {
                  keySpec = new DESedeKeySpec(key);
               } else {
                  keySpec = new DESKeySpec(key);
               }
            } catch (InvalidKeyException var5) {
               throw new CryptoException(var5);
            }

            secretKey = generateKey(algorithm, (KeySpec)keySpec);
         }

         return secretKey;
      } else {
         throw new CryptoException("Algorithm [{}] is not a DES algorithm!", new Object[]{algorithm});
      }
   }

   public static SecretKey generatePBEKey(String algorithm, char[] key) {
      if (!StrUtil.isBlank(algorithm) && algorithm.startsWith("PBE")) {
         if (null == key) {
            key = RandomUtil.randomString(32).toCharArray();
         }

         PBEKeySpec keySpec = new PBEKeySpec(key);
         return generateKey(algorithm, (KeySpec)keySpec);
      } else {
         throw new CryptoException("Algorithm [{}] is not a PBE algorithm!", new Object[]{algorithm});
      }
   }

   public static SecretKey generateKey(String algorithm, KeySpec keySpec) {
      SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm);

      try {
         return keyFactory.generateSecret(keySpec);
      } catch (InvalidKeySpecException var4) {
         throw new CryptoException(var4);
      }
   }

   public static PrivateKey generateRSAPrivateKey(byte[] key) {
      return generatePrivateKey(AsymmetricAlgorithm.RSA.getValue(), key);
   }

   public static PrivateKey generatePrivateKey(String algorithm, byte[] key) {
      return null == key ? null : generatePrivateKey(algorithm, (KeySpec)(new PKCS8EncodedKeySpec(key)));
   }

   public static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
      if (null == keySpec) {
         return null;
      } else {
         algorithm = getAlgorithmAfterWith(algorithm);

         try {
            return getKeyFactory(algorithm).generatePrivate(keySpec);
         } catch (Exception var3) {
            throw new CryptoException(var3);
         }
      }
   }

   public static PrivateKey generatePrivateKey(KeyStore keyStore, String alias, char[] password) {
      try {
         return (PrivateKey)keyStore.getKey(alias, password);
      } catch (Exception var4) {
         throw new CryptoException(var4);
      }
   }

   public static PublicKey generateRSAPublicKey(byte[] key) {
      return generatePublicKey(AsymmetricAlgorithm.RSA.getValue(), key);
   }

   public static PublicKey generatePublicKey(String algorithm, byte[] key) {
      return null == key ? null : generatePublicKey(algorithm, (KeySpec)(new X509EncodedKeySpec(key)));
   }

   public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
      if (null == keySpec) {
         return null;
      } else {
         algorithm = getAlgorithmAfterWith(algorithm);

         try {
            return getKeyFactory(algorithm).generatePublic(keySpec);
         } catch (Exception var3) {
            throw new CryptoException(var3);
         }
      }
   }

   public static KeyPair generateKeyPair(String algorithm) {
      int keySize = 1024;
      if ("ECIES".equalsIgnoreCase(algorithm)) {
         keySize = 256;
      }

      return generateKeyPair(algorithm, keySize);
   }

   public static KeyPair generateKeyPair(String algorithm, int keySize) {
      return generateKeyPair(algorithm, keySize, (byte[])null);
   }

   public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
      if ("SM2".equalsIgnoreCase(algorithm)) {
         ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec("sm2p256v1");
         return generateKeyPair(algorithm, keySize, seed, sm2p256v1);
      } else {
         return generateKeyPair(algorithm, keySize, seed, (AlgorithmParameterSpec[])null);
      }
   }

   public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
      return generateKeyPair(algorithm, (byte[])null, params);
   }

   public static KeyPair generateKeyPair(String algorithm, byte[] seed, AlgorithmParameterSpec param) {
      return generateKeyPair(algorithm, 1024, (byte[])seed, param);
   }

   public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
      return generateKeyPair(algorithm, keySize, RandomUtil.createSecureRandom(seed), params);
   }

   public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
      algorithm = getAlgorithmAfterWith(algorithm);
      KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);
      if (keySize > 0) {
         if ("EC".equalsIgnoreCase(algorithm) && keySize > 256) {
            keySize = 256;
         }

         if (null != random) {
            keyPairGen.initialize(keySize, random);
         } else {
            keyPairGen.initialize(keySize);
         }
      }

      if (ArrayUtil.isNotEmpty((Object[])params)) {
         AlgorithmParameterSpec[] var5 = params;
         int var6 = params.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            AlgorithmParameterSpec param = var5[var7];
            if (null != param) {
               try {
                  if (null != random) {
                     keyPairGen.initialize(param, random);
                  } else {
                     keyPairGen.initialize(param);
                  }
               } catch (InvalidAlgorithmParameterException var10) {
                  throw new CryptoException(var10);
               }
            }
         }
      }

      return keyPairGen.generateKeyPair();
   }

   public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
      Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

      try {
         KeyPairGenerator keyPairGen = null == provider ? KeyPairGenerator.getInstance(getMainAlgorithm(algorithm)) : KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
         return keyPairGen;
      } catch (NoSuchAlgorithmException var4) {
         throw new CryptoException(var4);
      }
   }

   public static KeyFactory getKeyFactory(String algorithm) {
      Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

      try {
         KeyFactory keyFactory = null == provider ? KeyFactory.getInstance(getMainAlgorithm(algorithm)) : KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
         return keyFactory;
      } catch (NoSuchAlgorithmException var4) {
         throw new CryptoException(var4);
      }
   }

   public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
      Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

      try {
         SecretKeyFactory keyFactory = null == provider ? SecretKeyFactory.getInstance(getMainAlgorithm(algorithm)) : SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
         return keyFactory;
      } catch (NoSuchAlgorithmException var4) {
         throw new CryptoException(var4);
      }
   }

   public static KeyGenerator getKeyGenerator(String algorithm) {
      Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

      try {
         KeyGenerator generator = null == provider ? KeyGenerator.getInstance(getMainAlgorithm(algorithm)) : KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
         return generator;
      } catch (NoSuchAlgorithmException var4) {
         throw new CryptoException(var4);
      }
   }

   public static String getMainAlgorithm(String algorithm) {
      Assert.notBlank(algorithm, "Algorithm must be not blank!");
      int slashIndex = algorithm.indexOf(47);
      return slashIndex > 0 ? algorithm.substring(0, slashIndex) : algorithm;
   }

   public static String getAlgorithmAfterWith(String algorithm) {
      Assert.notNull(algorithm, "algorithm must be not null !");
      if (StrUtil.startWithIgnoreCase(algorithm, "ECIESWith")) {
         return "EC";
      } else {
         int indexOfWith = StrUtil.lastIndexOfIgnoreCase(algorithm, "with");
         if (indexOfWith > 0) {
            algorithm = StrUtil.subSuf(algorithm, indexOfWith + "with".length());
         }

         if ("ECDSA".equalsIgnoreCase(algorithm) || "SM2".equalsIgnoreCase(algorithm) || "ECIES".equalsIgnoreCase(algorithm)) {
            algorithm = "EC";
         }

         return algorithm;
      }
   }

   public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
      return readKeyStore("JKS", keyFile, password);
   }

   public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
      return readKeyStore("JKS", in, password);
   }

   public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
      return readKeyStore("pkcs12", keyFile, password);
   }

   public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) {
      return readKeyStore("pkcs12", in, password);
   }

   public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
      InputStream in = null;

      KeyStore var4;
      try {
         in = FileUtil.getInputStream(keyFile);
         var4 = readKeyStore(type, (InputStream)in, password);
      } finally {
         IoUtil.close(in);
      }

      return var4;
   }

   public static KeyStore readKeyStore(String type, InputStream in, char[] password) {
      try {
         KeyStore keyStore = KeyStore.getInstance(type);
         keyStore.load(in, password);
         return keyStore;
      } catch (Exception var5) {
         throw new CryptoException(var5);
      }
   }

   public static KeyPair getKeyPair(String type, InputStream in, char[] password, String alias) {
      KeyStore keyStore = readKeyStore(type, in, password);
      return getKeyPair(keyStore, password, alias);
   }

   public static KeyPair getKeyPair(KeyStore keyStore, char[] password, String alias) {
      PublicKey publicKey;
      PrivateKey privateKey;
      try {
         publicKey = keyStore.getCertificate(alias).getPublicKey();
         privateKey = (PrivateKey)keyStore.getKey(alias, password);
      } catch (Exception var6) {
         throw new CryptoException(var6);
      }

      return new KeyPair(publicKey, privateKey);
   }

   public static Certificate readX509Certificate(InputStream in, char[] password, String alias) {
      return readCertificate("X.509", in, password, alias);
   }

   public static PublicKey readPublicKeyFromCert(InputStream in) {
      Certificate certificate = readX509Certificate(in);
      return null != certificate ? certificate.getPublicKey() : null;
   }

   public static Certificate readX509Certificate(InputStream in) {
      return readCertificate("X.509", in);
   }

   public static Certificate readCertificate(String type, InputStream in, char[] password, String alias) {
      KeyStore keyStore = readKeyStore(type, in, password);

      try {
         return keyStore.getCertificate(alias);
      } catch (KeyStoreException var6) {
         throw new CryptoException(var6);
      }
   }

   public static Certificate readCertificate(String type, InputStream in) {
      try {
         return getCertificateFactory(type).generateCertificate(in);
      } catch (CertificateException var3) {
         throw new CryptoException(var3);
      }
   }

   public static Certificate getCertificate(KeyStore keyStore, String alias) {
      try {
         return keyStore.getCertificate(alias);
      } catch (Exception var3) {
         throw new CryptoException(var3);
      }
   }

   public static CertificateFactory getCertificateFactory(String type) {
      Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();

      try {
         CertificateFactory factory = null == provider ? CertificateFactory.getInstance(type) : CertificateFactory.getInstance(type, provider);
         return factory;
      } catch (CertificateException var4) {
         throw new CryptoException(var4);
      }
   }

   public static byte[] encodeECPublicKey(PublicKey publicKey) {
      return BCUtil.encodeECPublicKey(publicKey);
   }

   public static PublicKey decodeECPoint(String encode, String curveName) {
      return BCUtil.decodeECPoint(encode, curveName);
   }

   public static PublicKey decodeECPoint(byte[] encodeByte, String curveName) {
      return BCUtil.decodeECPoint(encodeByte, curveName);
   }

   public static PublicKey getRSAPublicKey(PrivateKey privateKey) {
      if (privateKey instanceof RSAPrivateCrtKey) {
         RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey;
         return getRSAPublicKey(privk.getModulus(), privk.getPublicExponent());
      } else {
         return null;
      }
   }

   public static PublicKey getRSAPublicKey(String modulus, String publicExponent) {
      return getRSAPublicKey(new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
   }

   public static PublicKey getRSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
      RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);

      try {
         return getKeyFactory("RSA").generatePublic(publicKeySpec);
      } catch (InvalidKeySpecException var4) {
         throw new CryptoException(var4);
      }
   }

   public static String toBase64(Key key) {
      return Base64.encode(key.getEncoded());
   }
}
