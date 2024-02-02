package cn.hutool.crypto.symmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface SymmetricEncryptor {
   byte[] encrypt(byte[] var1);

   void encrypt(InputStream var1, OutputStream var2, boolean var3);

   default String encryptHex(byte[] data) {
      return HexUtil.encodeHexStr(this.encrypt(data));
   }

   default String encryptBase64(byte[] data) {
      return Base64.encode(this.encrypt(data));
   }

   default byte[] encrypt(String data, String charset) {
      return this.encrypt(StrUtil.bytes(data, charset));
   }

   default byte[] encrypt(String data, Charset charset) {
      return this.encrypt(StrUtil.bytes(data, charset));
   }

   default String encryptHex(String data, String charset) {
      return HexUtil.encodeHexStr(this.encrypt(data, charset));
   }

   default String encryptHex(String data, Charset charset) {
      return HexUtil.encodeHexStr(this.encrypt(data, charset));
   }

   default String encryptBase64(String data, String charset) {
      return Base64.encode(this.encrypt(data, charset));
   }

   default String encryptBase64(String data, Charset charset) {
      return Base64.encode(this.encrypt(data, charset));
   }

   default byte[] encrypt(String data) {
      return this.encrypt(StrUtil.bytes(data, CharsetUtil.CHARSET_UTF_8));
   }

   default String encryptHex(String data) {
      return HexUtil.encodeHexStr(this.encrypt(data));
   }

   default String encryptBase64(String data) {
      return Base64.encode(this.encrypt(data));
   }

   default byte[] encrypt(InputStream data) throws IORuntimeException {
      return this.encrypt(IoUtil.readBytes(data));
   }

   default String encryptHex(InputStream data) {
      return HexUtil.encodeHexStr(this.encrypt(data));
   }

   default String encryptBase64(InputStream data) {
      return Base64.encode(this.encrypt(data));
   }
}
