/*      */ package cn.hutool.crypto;
/*      */ 
/*      */ import cn.hutool.core.codec.Base64;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IoUtil;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.RandomUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
/*      */ import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.Key;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.KeyPair;
/*      */ import java.security.KeyPairGenerator;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.PublicKey;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.interfaces.RSAPrivateCrtKey;
/*      */ import java.security.spec.AlgorithmParameterSpec;
/*      */ import java.security.spec.ECGenParameterSpec;
/*      */ import java.security.spec.InvalidKeySpecException;
/*      */ import java.security.spec.KeySpec;
/*      */ import java.security.spec.PKCS8EncodedKeySpec;
/*      */ import java.security.spec.RSAPublicKeySpec;
/*      */ import java.security.spec.X509EncodedKeySpec;
/*      */ import javax.crypto.KeyGenerator;
/*      */ import javax.crypto.SecretKey;
/*      */ import javax.crypto.SecretKeyFactory;
/*      */ import javax.crypto.spec.DESKeySpec;
/*      */ import javax.crypto.spec.DESedeKeySpec;
/*      */ import javax.crypto.spec.PBEKeySpec;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class KeyUtil
/*      */ {
/*      */   public static final String KEY_TYPE_JKS = "JKS";
/*      */   public static final String KEY_TYPE_JCEKS = "jceks";
/*      */   public static final String KEY_TYPE_PKCS12 = "pkcs12";
/*      */   public static final String CERT_TYPE_X509 = "X.509";
/*      */   public static final int DEFAULT_KEY_SIZE = 1024;
/*      */   public static final String SM2_DEFAULT_CURVE = "sm2p256v1";
/*      */   
/*      */   public static SecretKey generateKey(String algorithm) {
/*  108 */     return generateKey(algorithm, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateKey(String algorithm, int keySize) {
/*  121 */     return generateKey(algorithm, keySize, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateKey(String algorithm, int keySize, SecureRandom random) {
/*  135 */     algorithm = getMainAlgorithm(algorithm);
/*      */     
/*  137 */     KeyGenerator keyGenerator = getKeyGenerator(algorithm);
/*  138 */     if (keySize <= 0 && SymmetricAlgorithm.AES.getValue().equals(algorithm))
/*      */     {
/*  140 */       keySize = 128;
/*      */     }
/*      */     
/*  143 */     if (keySize > 0) {
/*  144 */       if (null == random) {
/*  145 */         keyGenerator.init(keySize);
/*      */       } else {
/*  147 */         keyGenerator.init(keySize, random);
/*      */       } 
/*      */     }
/*  150 */     return keyGenerator.generateKey();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateKey(String algorithm, byte[] key) {
/*      */     SecretKey secretKey;
/*  161 */     Assert.notBlank(algorithm, "Algorithm is blank!", new Object[0]);
/*      */     
/*  163 */     if (algorithm.startsWith("PBE")) {
/*      */       
/*  165 */       secretKey = generatePBEKey(algorithm, (null == key) ? null : StrUtil.utf8Str(key).toCharArray());
/*  166 */     } else if (algorithm.startsWith("DES")) {
/*      */       
/*  168 */       secretKey = generateDESKey(algorithm, key);
/*      */     } else {
/*      */       
/*  171 */       secretKey = (null == key) ? generateKey(algorithm) : new SecretKeySpec(key, algorithm);
/*      */     } 
/*  173 */     return secretKey;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateDESKey(String algorithm, byte[] key) {
/*      */     SecretKey secretKey;
/*  184 */     if (StrUtil.isBlank(algorithm) || false == algorithm.startsWith("DES")) {
/*  185 */       throw new CryptoException("Algorithm [{}] is not a DES algorithm!", new Object[] { algorithm });
/*      */     }
/*      */ 
/*      */     
/*  189 */     if (null == key) {
/*  190 */       secretKey = generateKey(algorithm);
/*      */     } else {
/*      */       KeySpec keySpec;
/*      */       try {
/*  194 */         if (algorithm.startsWith("DESede")) {
/*      */           
/*  196 */           keySpec = new DESedeKeySpec(key);
/*      */         } else {
/*  198 */           keySpec = new DESKeySpec(key);
/*      */         } 
/*  200 */       } catch (InvalidKeyException e) {
/*  201 */         throw new CryptoException(e);
/*      */       } 
/*  203 */       secretKey = generateKey(algorithm, keySpec);
/*      */     } 
/*  205 */     return secretKey;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generatePBEKey(String algorithm, char[] key) {
/*  216 */     if (StrUtil.isBlank(algorithm) || false == algorithm.startsWith("PBE")) {
/*  217 */       throw new CryptoException("Algorithm [{}] is not a PBE algorithm!", new Object[] { algorithm });
/*      */     }
/*      */     
/*  220 */     if (null == key) {
/*  221 */       key = RandomUtil.randomString(32).toCharArray();
/*      */     }
/*  223 */     PBEKeySpec keySpec = new PBEKeySpec(key);
/*  224 */     return generateKey(algorithm, keySpec);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateKey(String algorithm, KeySpec keySpec) {
/*  235 */     SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm);
/*      */     try {
/*  237 */       return keyFactory.generateSecret(keySpec);
/*  238 */     } catch (InvalidKeySpecException e) {
/*  239 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrivateKey generateRSAPrivateKey(byte[] key) {
/*  253 */     return generatePrivateKey(AsymmetricAlgorithm.RSA.getValue(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrivateKey generatePrivateKey(String algorithm, byte[] key) {
/*  266 */     if (null == key) {
/*  267 */       return null;
/*      */     }
/*  269 */     return generatePrivateKey(algorithm, new PKCS8EncodedKeySpec(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
/*  282 */     if (null == keySpec) {
/*  283 */       return null;
/*      */     }
/*  285 */     algorithm = getAlgorithmAfterWith(algorithm);
/*      */     try {
/*  287 */       return getKeyFactory(algorithm).generatePrivate(keySpec);
/*  288 */     } catch (Exception e) {
/*  289 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrivateKey generatePrivateKey(KeyStore keyStore, String alias, char[] password) {
/*      */     try {
/*  303 */       return (PrivateKey)keyStore.getKey(alias, password);
/*  304 */     } catch (Exception e) {
/*  305 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey generateRSAPublicKey(byte[] key) {
/*  319 */     return generatePublicKey(AsymmetricAlgorithm.RSA.getValue(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey generatePublicKey(String algorithm, byte[] key) {
/*  332 */     if (null == key) {
/*  333 */       return null;
/*      */     }
/*  335 */     return generatePublicKey(algorithm, new X509EncodedKeySpec(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
/*  348 */     if (null == keySpec) {
/*  349 */       return null;
/*      */     }
/*  351 */     algorithm = getAlgorithmAfterWith(algorithm);
/*      */     try {
/*  353 */       return getKeyFactory(algorithm).generatePublic(keySpec);
/*  354 */     } catch (Exception e) {
/*  355 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm) {
/*  367 */     int keySize = 1024;
/*  368 */     if ("ECIES".equalsIgnoreCase(algorithm))
/*      */     {
/*  370 */       keySize = 256;
/*      */     }
/*      */     
/*  373 */     return generateKeyPair(algorithm, keySize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, int keySize) {
/*  385 */     return generateKeyPair(algorithm, keySize, (byte[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
/*  399 */     if ("SM2".equalsIgnoreCase(algorithm)) {
/*  400 */       ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec("sm2p256v1");
/*  401 */       return generateKeyPair(algorithm, keySize, seed, new AlgorithmParameterSpec[] { sm2p256v1 });
/*      */     } 
/*      */     
/*  404 */     return generateKeyPair(algorithm, keySize, seed, (AlgorithmParameterSpec[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
/*  417 */     return generateKeyPair(algorithm, (byte[])null, params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, byte[] seed, AlgorithmParameterSpec param) {
/*  431 */     return generateKeyPair(algorithm, 1024, seed, new AlgorithmParameterSpec[] { param });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
/*  465 */     return generateKeyPair(algorithm, keySize, RandomUtil.createSecureRandom(seed), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
/*  499 */     algorithm = getAlgorithmAfterWith(algorithm);
/*  500 */     KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);
/*      */ 
/*      */     
/*  503 */     if (keySize > 0) {
/*      */       
/*  505 */       if ("EC".equalsIgnoreCase(algorithm) && keySize > 256)
/*      */       {
/*  507 */         keySize = 256;
/*      */       }
/*  509 */       if (null != random) {
/*  510 */         keyPairGen.initialize(keySize, random);
/*      */       } else {
/*  512 */         keyPairGen.initialize(keySize);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  517 */     if (ArrayUtil.isNotEmpty((Object[])params))
/*  518 */       for (AlgorithmParameterSpec param : params) {
/*  519 */         if (null != param) {
/*      */           
/*      */           try {
/*      */             
/*  523 */             if (null != random) {
/*  524 */               keyPairGen.initialize(param, random);
/*      */             } else {
/*  526 */               keyPairGen.initialize(param);
/*      */             } 
/*  528 */           } catch (InvalidAlgorithmParameterException e) {
/*  529 */             throw new CryptoException(e);
/*      */           } 
/*      */         }
/*      */       }  
/*  533 */     return keyPairGen.generateKeyPair();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
/*      */     KeyPairGenerator keyPairGen;
/*  544 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  550 */       keyPairGen = (null == provider) ? KeyPairGenerator.getInstance(getMainAlgorithm(algorithm)) : KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
/*  551 */     } catch (NoSuchAlgorithmException e) {
/*  552 */       throw new CryptoException(e);
/*      */     } 
/*  554 */     return keyPairGen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyFactory getKeyFactory(String algorithm) {
/*      */     KeyFactory keyFactory;
/*  565 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  571 */       keyFactory = (null == provider) ? KeyFactory.getInstance(getMainAlgorithm(algorithm)) : KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
/*  572 */     } catch (NoSuchAlgorithmException e) {
/*  573 */       throw new CryptoException(e);
/*      */     } 
/*  575 */     return keyFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
/*      */     SecretKeyFactory keyFactory;
/*  586 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  592 */       keyFactory = (null == provider) ? SecretKeyFactory.getInstance(getMainAlgorithm(algorithm)) : SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
/*  593 */     } catch (NoSuchAlgorithmException e) {
/*  594 */       throw new CryptoException(e);
/*      */     } 
/*  596 */     return keyFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyGenerator getKeyGenerator(String algorithm) {
/*      */     KeyGenerator generator;
/*  607 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  613 */       generator = (null == provider) ? KeyGenerator.getInstance(getMainAlgorithm(algorithm)) : KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
/*  614 */     } catch (NoSuchAlgorithmException e) {
/*  615 */       throw new CryptoException(e);
/*      */     } 
/*  617 */     return generator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getMainAlgorithm(String algorithm) {
/*  628 */     Assert.notBlank(algorithm, "Algorithm must be not blank!", new Object[0]);
/*  629 */     int slashIndex = algorithm.indexOf('/');
/*  630 */     if (slashIndex > 0) {
/*  631 */       return algorithm.substring(0, slashIndex);
/*      */     }
/*  633 */     return algorithm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getAlgorithmAfterWith(String algorithm) {
/*  644 */     Assert.notNull(algorithm, "algorithm must be not null !", new Object[0]);
/*      */     
/*  646 */     if (StrUtil.startWithIgnoreCase(algorithm, "ECIESWith")) {
/*  647 */       return "EC";
/*      */     }
/*      */     
/*  650 */     int indexOfWith = StrUtil.lastIndexOfIgnoreCase(algorithm, "with");
/*  651 */     if (indexOfWith > 0) {
/*  652 */       algorithm = StrUtil.subSuf(algorithm, indexOfWith + "with".length());
/*      */     }
/*  654 */     if ("ECDSA".equalsIgnoreCase(algorithm) || "SM2"
/*  655 */       .equalsIgnoreCase(algorithm) || "ECIES"
/*  656 */       .equalsIgnoreCase(algorithm))
/*      */     {
/*  658 */       algorithm = "EC";
/*      */     }
/*  660 */     return algorithm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
/*  674 */     return readKeyStore("JKS", keyFile, password);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
/*  687 */     return readKeyStore("JKS", in, password);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
/*  700 */     return readKeyStore("pkcs12", keyFile, password);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) {
/*  713 */     return readKeyStore("pkcs12", in, password);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
/*  728 */     InputStream in = null;
/*      */     try {
/*  730 */       in = FileUtil.getInputStream(keyFile);
/*  731 */       return readKeyStore(type, in, password);
/*      */     } finally {
/*  733 */       IoUtil.close(in);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyStore readKeyStore(String type, InputStream in, char[] password) {
/*      */     KeyStore keyStore;
/*      */     try {
/*  750 */       keyStore = KeyStore.getInstance(type);
/*  751 */       keyStore.load(in, password);
/*  752 */     } catch (Exception e) {
/*  753 */       throw new CryptoException(e);
/*      */     } 
/*  755 */     return keyStore;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair getKeyPair(String type, InputStream in, char[] password, String alias) {
/*  769 */     KeyStore keyStore = readKeyStore(type, in, password);
/*  770 */     return getKeyPair(keyStore, password, alias);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static KeyPair getKeyPair(KeyStore keyStore, char[] password, String alias) {
/*      */     PublicKey publicKey;
/*      */     PrivateKey privateKey;
/*      */     try {
/*  786 */       publicKey = keyStore.getCertificate(alias).getPublicKey();
/*  787 */       privateKey = (PrivateKey)keyStore.getKey(alias, password);
/*  788 */     } catch (Exception e) {
/*  789 */       throw new CryptoException(e);
/*      */     } 
/*  791 */     return new KeyPair(publicKey, privateKey);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Certificate readX509Certificate(InputStream in, char[] password, String alias) {
/*  806 */     return readCertificate("X.509", in, password, alias);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey readPublicKeyFromCert(InputStream in) {
/*  819 */     Certificate certificate = readX509Certificate(in);
/*  820 */     if (null != certificate) {
/*  821 */       return certificate.getPublicKey();
/*      */     }
/*  823 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Certificate readX509Certificate(InputStream in) {
/*  836 */     return readCertificate("X.509", in);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Certificate readCertificate(String type, InputStream in, char[] password, String alias) {
/*  852 */     KeyStore keyStore = readKeyStore(type, in, password);
/*      */     try {
/*  854 */       return keyStore.getCertificate(alias);
/*  855 */     } catch (KeyStoreException e) {
/*  856 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Certificate readCertificate(String type, InputStream in) {
/*      */     try {
/*  871 */       return getCertificateFactory(type).generateCertificate(in);
/*  872 */     } catch (CertificateException e) {
/*  873 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Certificate getCertificate(KeyStore keyStore, String alias) {
/*      */     try {
/*  886 */       return keyStore.getCertificate(alias);
/*  887 */     } catch (Exception e) {
/*  888 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CertificateFactory getCertificateFactory(String type) {
/*      */     CertificateFactory factory;
/*  900 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */     
/*      */     try {
/*  904 */       factory = (null == provider) ? CertificateFactory.getInstance(type) : CertificateFactory.getInstance(type, provider);
/*  905 */     } catch (CertificateException e) {
/*  906 */       throw new CryptoException(e);
/*      */     } 
/*  908 */     return factory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] encodeECPublicKey(PublicKey publicKey) {
/*  920 */     return BCUtil.encodeECPublicKey(publicKey);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey decodeECPoint(String encode, String curveName) {
/*  933 */     return BCUtil.decodeECPoint(encode, curveName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey decodeECPoint(byte[] encodeByte, String curveName) {
/*  946 */     return BCUtil.decodeECPoint(encodeByte, curveName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey getRSAPublicKey(PrivateKey privateKey) {
/*  957 */     if (privateKey instanceof RSAPrivateCrtKey) {
/*  958 */       RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey;
/*  959 */       return getRSAPublicKey(privk.getModulus(), privk.getPublicExponent());
/*      */     } 
/*  961 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey getRSAPublicKey(String modulus, String publicExponent) {
/*  973 */     return getRSAPublicKey(new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PublicKey getRSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
/*  986 */     RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
/*      */     try {
/*  988 */       return getKeyFactory("RSA").generatePublic(publicKeySpec);
/*  989 */     } catch (InvalidKeySpecException e) {
/*  990 */       throw new CryptoException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toBase64(Key key) {
/* 1001 */     return Base64.encode(key.getEncoded());
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\KeyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */