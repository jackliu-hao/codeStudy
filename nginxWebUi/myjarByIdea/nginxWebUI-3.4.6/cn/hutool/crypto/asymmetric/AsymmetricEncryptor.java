package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface AsymmetricEncryptor {
   byte[] encrypt(byte[] var1, KeyType var2);

   default String encryptHex(byte[] data, KeyType keyType) {
      return HexUtil.encodeHexStr(this.encrypt(data, keyType));
   }

   default String encryptBase64(byte[] data, KeyType keyType) {
      return Base64.encode(this.encrypt(data, keyType));
   }

   default byte[] encrypt(String data, String charset, KeyType keyType) {
      return this.encrypt(StrUtil.bytes(data, charset), keyType);
   }

   default byte[] encrypt(String data, Charset charset, KeyType keyType) {
      return this.encrypt(StrUtil.bytes(data, charset), keyType);
   }

   default byte[] encrypt(String data, KeyType keyType) {
      return this.encrypt(StrUtil.utf8Bytes(data), keyType);
   }

   default String encryptHex(String data, KeyType keyType) {
      return HexUtil.encodeHexStr(this.encrypt(data, keyType));
   }

   default String encryptHex(String data, Charset charset, KeyType keyType) {
      return HexUtil.encodeHexStr(this.encrypt(data, charset, keyType));
   }

   default String encryptBase64(String data, KeyType keyType) {
      return Base64.encode(this.encrypt(data, keyType));
   }

   default String encryptBase64(String data, Charset charset, KeyType keyType) {
      return Base64.encode(this.encrypt(data, charset, keyType));
   }

   default byte[] encrypt(InputStream data, KeyType keyType) throws IORuntimeException {
      return this.encrypt(IoUtil.readBytes(data), keyType);
   }

   default String encryptHex(InputStream data, KeyType keyType) {
      return HexUtil.encodeHexStr(this.encrypt(data, keyType));
   }

   default String encryptBase64(InputStream data, KeyType keyType) {
      return Base64.encode(this.encrypt(data, keyType));
   }

   default String encryptBcd(String data, KeyType keyType) {
      return this.encryptBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
   }

   default String encryptBcd(String data, KeyType keyType, Charset charset) {
      return BCD.bcdToStr(this.encrypt(data, charset, keyType));
   }
}
