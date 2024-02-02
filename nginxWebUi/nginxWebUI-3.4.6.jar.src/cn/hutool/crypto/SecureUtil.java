/*      */ package cn.hutool.crypto;
/*      */ 
/*      */ import cn.hutool.core.codec.Base64;
/*      */ import cn.hutool.core.lang.Validator;
/*      */ import cn.hutool.core.util.HexUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
/*      */ import cn.hutool.crypto.asymmetric.RSA;
/*      */ import cn.hutool.crypto.asymmetric.Sign;
/*      */ import cn.hutool.crypto.asymmetric.SignAlgorithm;
/*      */ import cn.hutool.crypto.digest.DigestAlgorithm;
/*      */ import cn.hutool.crypto.digest.Digester;
/*      */ import cn.hutool.crypto.digest.HMac;
/*      */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*      */ import cn.hutool.crypto.digest.MD5;
/*      */ import cn.hutool.crypto.symmetric.AES;
/*      */ import cn.hutool.crypto.symmetric.DES;
/*      */ import cn.hutool.crypto.symmetric.DESede;
/*      */ import cn.hutool.crypto.symmetric.PBKDF2;
/*      */ import cn.hutool.crypto.symmetric.RC4;
/*      */ import cn.hutool.crypto.symmetric.SymmetricCrypto;
/*      */ import cn.hutool.crypto.symmetric.ZUC;
/*      */ import cn.hutool.crypto.symmetric.fpe.FPE;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.security.KeyPair;
/*      */ import java.security.KeyStore;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Security;
/*      */ import java.security.Signature;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.spec.AlgorithmParameterSpec;
/*      */ import java.security.spec.KeySpec;
/*      */ import java.util.Map;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.Mac;
/*      */ import javax.crypto.SecretKey;
/*      */ import org.bouncycastle.crypto.AlphabetMapper;
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
/*      */ public class SecureUtil
/*      */ {
/*      */   public static final int DEFAULT_KEY_SIZE = 1024;
/*      */   
/*      */   public static SecretKey generateKey(String algorithm) {
/*   75 */     return KeyUtil.generateKey(algorithm);
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
/*      */   public static SecretKey generateKey(String algorithm, int keySize) {
/*   87 */     return KeyUtil.generateKey(algorithm, keySize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateKey(String algorithm, byte[] key) {
/*   98 */     return KeyUtil.generateKey(algorithm, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static SecretKey generateDESKey(String algorithm, byte[] key) {
/*  109 */     return KeyUtil.generateDESKey(algorithm, key);
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
/*  120 */     return KeyUtil.generatePBEKey(algorithm, key);
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
/*  131 */     return KeyUtil.generateKey(algorithm, keySpec);
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
/*      */   public static PrivateKey generatePrivateKey(String algorithm, byte[] key) {
/*  143 */     return KeyUtil.generatePrivateKey(algorithm, key);
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
/*  156 */     return KeyUtil.generatePrivateKey(algorithm, keySpec);
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
/*  168 */     return KeyUtil.generatePrivateKey(keyStore, alias, password);
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
/*      */   public static PublicKey generatePublicKey(String algorithm, byte[] key) {
/*  180 */     return KeyUtil.generatePublicKey(algorithm, key);
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
/*  193 */     return KeyUtil.generatePublicKey(algorithm, keySpec);
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
/*  204 */     return KeyUtil.generateKeyPair(algorithm);
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
/*  216 */     return KeyUtil.generateKeyPair(algorithm, keySize);
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
/*      */   public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
/*  229 */     return KeyUtil.generateKeyPair(algorithm, keySize, seed);
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
/*  242 */     return KeyUtil.generateKeyPair(algorithm, params);
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
/*      */   public static KeyPair generateKeyPair(String algorithm, byte[] seed, AlgorithmParameterSpec params) {
/*  256 */     return KeyUtil.generateKeyPair(algorithm, seed, params);
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
/*  267 */     return KeyUtil.getAlgorithmAfterWith(algorithm);
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
/*      */   public static String generateAlgorithm(AsymmetricAlgorithm asymmetricAlgorithm, DigestAlgorithm digestAlgorithm) {
/*  279 */     String digestPart = (null == digestAlgorithm) ? "NONE" : digestAlgorithm.name();
/*  280 */     return StrUtil.format("{}with{}", new Object[] { digestPart, asymmetricAlgorithm.getValue() });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Signature generateSignature(AsymmetricAlgorithm asymmetricAlgorithm, DigestAlgorithm digestAlgorithm) {
/*      */     try {
/*  292 */       return Signature.getInstance(generateAlgorithm(asymmetricAlgorithm, digestAlgorithm));
/*  293 */     } catch (NoSuchAlgorithmException e) {
/*  294 */       throw new CryptoException(e);
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
/*      */   public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
/*  308 */     return KeyUtil.readJKSKeyStore(in, password);
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
/*  322 */     return KeyUtil.readKeyStore(type, in, password);
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
/*  337 */     return KeyUtil.readX509Certificate(in, password, alias);
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
/*  350 */     return KeyUtil.readX509Certificate(in);
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
/*  366 */     return KeyUtil.readCertificate(type, in, password, alias);
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
/*  379 */     return KeyUtil.readCertificate(type, in);
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
/*  390 */     return KeyUtil.getCertificate(keyStore, alias);
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
/*      */   public static AES aes() {
/*  407 */     return new AES();
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
/*      */   public static AES aes(byte[] key) {
/*  423 */     return new AES(key);
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
/*      */   public static DES des() {
/*  438 */     return new DES();
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
/*      */   public static DES des(byte[] key) {
/*  454 */     return new DES(key);
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
/*      */   public static DESede desede() {
/*  471 */     return new DESede();
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
/*      */   public static DESede desede(byte[] key) {
/*  489 */     return new DESede(key);
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
/*      */   public static MD5 md5() {
/*  506 */     return new MD5();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String md5(String data) {
/*  516 */     return (new MD5()).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String md5(InputStream data) {
/*  526 */     return (new MD5()).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String md5(File dataFile) {
/*  536 */     return (new MD5()).digestHex(dataFile);
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
/*      */   public static Digester sha1() {
/*  548 */     return new Digester(DigestAlgorithm.SHA1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha1(String data) {
/*  558 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha1(InputStream data) {
/*  568 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha1(File dataFile) {
/*  578 */     return (new Digester(DigestAlgorithm.SHA1)).digestHex(dataFile);
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
/*      */   public static Digester sha256() {
/*  591 */     return new Digester(DigestAlgorithm.SHA256);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha256(String data) {
/*  602 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha256(InputStream data) {
/*  613 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sha256(File dataFile) {
/*  624 */     return (new Digester(DigestAlgorithm.SHA256)).digestHex(dataFile);
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
/*      */   public static HMac hmac(HmacAlgorithm algorithm, String key) {
/*  636 */     return new HMac(algorithm, StrUtil.utf8Bytes(key));
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
/*      */   public static HMac hmac(HmacAlgorithm algorithm, byte[] key) {
/*  648 */     return new HMac(algorithm, key);
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
/*      */   public static HMac hmac(HmacAlgorithm algorithm, SecretKey key) {
/*  660 */     return new HMac(algorithm, key);
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
/*      */   public static HMac hmacMd5(String key) {
/*  674 */     return hmacMd5(StrUtil.utf8Bytes(key));
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
/*      */   public static HMac hmacMd5(byte[] key) {
/*  687 */     return new HMac(HmacAlgorithm.HmacMD5, key);
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
/*      */   public static HMac hmacMd5() {
/*  699 */     return new HMac(HmacAlgorithm.HmacMD5);
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
/*      */   public static HMac hmacSha1(String key) {
/*  713 */     return hmacSha1(StrUtil.utf8Bytes(key));
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
/*      */   public static HMac hmacSha1(byte[] key) {
/*  726 */     return new HMac(HmacAlgorithm.HmacSHA1, key);
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
/*      */   public static HMac hmacSha1() {
/*  738 */     return new HMac(HmacAlgorithm.HmacSHA1);
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
/*      */   public static HMac hmacSha256(String key) {
/*  752 */     return hmacSha256(StrUtil.utf8Bytes(key));
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
/*      */   public static HMac hmacSha256(byte[] key) {
/*  766 */     return new HMac(HmacAlgorithm.HmacSHA256, key);
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
/*      */   public static HMac hmacSha256() {
/*  779 */     return new HMac(HmacAlgorithm.HmacSHA256);
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
/*      */   public static RSA rsa() {
/*  792 */     return new RSA();
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
/*      */   public static RSA rsa(String privateKeyBase64, String publicKeyBase64) {
/*  806 */     return new RSA(privateKeyBase64, publicKeyBase64);
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
/*      */   public static RSA rsa(byte[] privateKey, byte[] publicKey) {
/*  820 */     return new RSA(privateKey, publicKey);
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
/*      */   public static Sign sign(SignAlgorithm algorithm) {
/*  832 */     return SignUtil.sign(algorithm);
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
/*      */   public static Sign sign(SignAlgorithm algorithm, String privateKeyBase64, String publicKeyBase64) {
/*  847 */     return SignUtil.sign(algorithm, privateKeyBase64, publicKeyBase64);
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
/*      */   public static Sign sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
/*  862 */     return SignUtil.sign(algorithm, privateKey, publicKey);
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
/*      */   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String... otherParams) {
/*  877 */     return SignUtil.signParams(crypto, params, otherParams);
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
/*      */   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/*  895 */     return SignUtil.signParams(crypto, params, separator, keyValueSeparator, isIgnoreNull, otherParams);
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
/*      */   public static String signParamsMd5(Map<?, ?> params, String... otherParams) {
/*  909 */     return SignUtil.signParamsMd5(params, otherParams);
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
/*      */   public static String signParamsSha1(Map<?, ?> params, String... otherParams) {
/*  923 */     return SignUtil.signParamsSha1(params, otherParams);
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
/*      */   public static String signParamsSha256(Map<?, ?> params, String... otherParams) {
/*  937 */     return SignUtil.signParamsSha256(params, otherParams);
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
/*      */   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String... otherParams) {
/*  952 */     return SignUtil.signParams(digestAlgorithm, params, otherParams);
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
/*      */   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/*  970 */     return SignUtil.signParams(digestAlgorithm, params, separator, keyValueSeparator, isIgnoreNull, otherParams);
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
/*      */   public static void addProvider(Provider provider) {
/*  984 */     Security.insertProviderAt(provider, 0);
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
/*      */   public static byte[] decode(String key) {
/* 1000 */     return Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Cipher createCipher(String algorithm) {
/*      */     Cipher cipher;
/* 1011 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */     
/*      */     try {
/* 1015 */       cipher = (null == provider) ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
/* 1016 */     } catch (Exception e) {
/* 1017 */       throw new CryptoException(e);
/*      */     } 
/*      */     
/* 1020 */     return cipher;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MessageDigest createMessageDigest(String algorithm) {
/*      */     MessageDigest messageDigest;
/* 1031 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */     
/*      */     try {
/* 1035 */       messageDigest = (null == provider) ? MessageDigest.getInstance(algorithm) : MessageDigest.getInstance(algorithm, provider);
/* 1036 */     } catch (NoSuchAlgorithmException e) {
/* 1037 */       throw new CryptoException(e);
/*      */     } 
/*      */     
/* 1040 */     return messageDigest;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac createMac(String algorithm) {
/*      */     Mac mac;
/* 1051 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */     
/*      */     try {
/* 1055 */       mac = (null == provider) ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
/* 1056 */     } catch (NoSuchAlgorithmException e) {
/* 1057 */       throw new CryptoException(e);
/*      */     } 
/*      */     
/* 1060 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Signature createSignature(String algorithm) {
/*      */     Signature signature;
/* 1071 */     Provider provider = GlobalBouncyCastleProvider.INSTANCE.getProvider();
/*      */ 
/*      */     
/*      */     try {
/* 1075 */       signature = (null == provider) ? Signature.getInstance(algorithm) : Signature.getInstance(algorithm, provider);
/* 1076 */     } catch (NoSuchAlgorithmException e) {
/* 1077 */       throw new CryptoException(e);
/*      */     } 
/*      */     
/* 1080 */     return signature;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static RC4 rc4(String key) {
/* 1090 */     return new RC4(key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void disableBouncyCastle() {
/* 1099 */     GlobalBouncyCastleProvider.setUseBouncyCastle(false);
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
/*      */   public static String pbkdf2(char[] password, byte[] salt) {
/* 1111 */     return (new PBKDF2()).encryptHex(password, salt);
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
/*      */   public static FPE fpe(FPE.FPEMode mode, byte[] key, AlphabetMapper mapper, byte[] tweak) {
/* 1125 */     return new FPE(mode, key, mapper, tweak);
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
/*      */   public static ZUC zuc128(byte[] key, byte[] iv) {
/* 1137 */     return new ZUC(ZUC.ZUCAlgorithm.ZUC_128, key, iv);
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
/*      */   public static ZUC zuc256(byte[] key, byte[] iv) {
/* 1149 */     return new ZUC(ZUC.ZUCAlgorithm.ZUC_256, key, iv);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\SecureUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */