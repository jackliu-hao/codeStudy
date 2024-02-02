package cn.hutool.crypto.digest;

import cn.hutool.core.util.CharsetUtil;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.crypto.SecretKey;

public class DigestUtil {
   public static byte[] md5(byte[] data) {
      return (new MD5()).digest(data);
   }

   public static byte[] md5(String data, String charset) {
      return (new MD5()).digest(data, charset);
   }

   public static byte[] md5(String data) {
      return md5(data, "UTF-8");
   }

   public static byte[] md5(InputStream data) {
      return (new MD5()).digest(data);
   }

   public static byte[] md5(File file) {
      return (new MD5()).digest(file);
   }

   public static String md5Hex(byte[] data) {
      return (new MD5()).digestHex(data);
   }

   public static String md5Hex(String data, String charset) {
      return (new MD5()).digestHex(data, charset);
   }

   public static String md5Hex(String data, Charset charset) {
      return (new MD5()).digestHex(data, charset);
   }

   public static String md5Hex(String data) {
      return md5Hex(data, "UTF-8");
   }

   public static String md5Hex(InputStream data) {
      return (new MD5()).digestHex(data);
   }

   public static String md5Hex(File file) {
      return (new MD5()).digestHex(file);
   }

   public static String md5Hex16(byte[] data) {
      return (new MD5()).digestHex16(data);
   }

   public static String md5Hex16(String data, Charset charset) {
      return (new MD5()).digestHex16(data, charset);
   }

   public static String md5Hex16(String data) {
      return md5Hex16(data, CharsetUtil.CHARSET_UTF_8);
   }

   public static String md5Hex16(InputStream data) {
      return (new MD5()).digestHex16(data);
   }

   public static String md5Hex16(File file) {
      return (new MD5()).digestHex16(file);
   }

   public static String md5HexTo16(String md5Hex) {
      return md5Hex.substring(8, 24);
   }

   public static byte[] sha1(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA1)).digest(data);
   }

   public static byte[] sha1(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA1)).digest(data, charset);
   }

   public static byte[] sha1(String data) {
      return sha1(data, "UTF-8");
   }

   public static byte[] sha1(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA1)).digest(data);
   }

   public static byte[] sha1(File file) {
      return (new Digester(DigestAlgorithm.SHA1)).digest(file);
   }

   public static String sha1Hex(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
   }

   public static String sha1Hex(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA1)).digestHex(data, charset);
   }

   public static String sha1Hex(String data) {
      return sha1Hex(data, "UTF-8");
   }

   public static String sha1Hex(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA1)).digestHex(data);
   }

   public static String sha1Hex(File file) {
      return (new Digester(DigestAlgorithm.SHA1)).digestHex(file);
   }

   public static byte[] sha256(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA256)).digest(data);
   }

   public static byte[] sha256(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA256)).digest(data, charset);
   }

   public static byte[] sha256(String data) {
      return sha256(data, "UTF-8");
   }

   public static byte[] sha256(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA256)).digest(data);
   }

   public static byte[] sha256(File file) {
      return (new Digester(DigestAlgorithm.SHA256)).digest(file);
   }

   public static String sha256Hex(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
   }

   public static String sha256Hex(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA256)).digestHex(data, charset);
   }

   public static String sha256Hex(String data) {
      return sha256Hex(data, "UTF-8");
   }

   public static String sha256Hex(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA256)).digestHex(data);
   }

   public static String sha256Hex(File file) {
      return (new Digester(DigestAlgorithm.SHA256)).digestHex(file);
   }

   public static HMac hmac(HmacAlgorithm algorithm, byte[] key) {
      return new HMac(algorithm, key);
   }

   public static HMac hmac(HmacAlgorithm algorithm, SecretKey key) {
      return new HMac(algorithm, key);
   }

   public static Digester digester(DigestAlgorithm algorithm) {
      return new Digester(algorithm);
   }

   public static Digester digester(String algorithm) {
      return new Digester(algorithm);
   }

   public static String bcrypt(String password) {
      return BCrypt.hashpw(password);
   }

   public static boolean bcryptCheck(String password, String hashed) {
      return BCrypt.checkpw(password, hashed);
   }

   public static byte[] sha512(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA512)).digest(data);
   }

   public static byte[] sha512(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA512)).digest(data, charset);
   }

   public static byte[] sha512(String data) {
      return sha512(data, "UTF-8");
   }

   public static byte[] sha512(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA512)).digest(data);
   }

   public static byte[] sha512(File file) {
      return (new Digester(DigestAlgorithm.SHA512)).digest(file);
   }

   public static String sha512Hex(byte[] data) {
      return (new Digester(DigestAlgorithm.SHA512)).digestHex(data);
   }

   public static String sha512Hex(String data, String charset) {
      return (new Digester(DigestAlgorithm.SHA512)).digestHex(data, charset);
   }

   public static String sha512Hex(String data) {
      return sha512Hex(data, "UTF-8");
   }

   public static String sha512Hex(InputStream data) {
      return (new Digester(DigestAlgorithm.SHA512)).digestHex(data);
   }

   public static String sha512Hex(File file) {
      return (new Digester(DigestAlgorithm.SHA512)).digestHex(file);
   }
}
