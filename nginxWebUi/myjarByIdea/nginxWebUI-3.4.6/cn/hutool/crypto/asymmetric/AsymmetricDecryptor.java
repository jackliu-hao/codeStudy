package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface AsymmetricDecryptor {
   byte[] decrypt(byte[] var1, KeyType var2);

   default byte[] decrypt(InputStream data, KeyType keyType) throws IORuntimeException {
      return this.decrypt(IoUtil.readBytes(data), keyType);
   }

   default byte[] decrypt(String data, KeyType keyType) {
      return this.decrypt(SecureUtil.decode(data), keyType);
   }

   default String decryptStr(String data, KeyType keyType, Charset charset) {
      return StrUtil.str(this.decrypt(data, keyType), charset);
   }

   default String decryptStr(String data, KeyType keyType) {
      return this.decryptStr(data, keyType, CharsetUtil.CHARSET_UTF_8);
   }

   default byte[] decryptFromBcd(String data, KeyType keyType) {
      return this.decryptFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
   }

   default byte[] decryptFromBcd(String data, KeyType keyType, Charset charset) {
      Assert.notNull(data, "Bcd string must be not null!");
      byte[] dataBytes = BCD.ascToBcd(StrUtil.bytes(data, charset));
      return this.decrypt(dataBytes, keyType);
   }

   default String decryptStrFromBcd(String data, KeyType keyType, Charset charset) {
      return StrUtil.str(this.decryptFromBcd(data, keyType, charset), charset);
   }

   default String decryptStrFromBcd(String data, KeyType keyType) {
      return this.decryptStrFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
   }
}
