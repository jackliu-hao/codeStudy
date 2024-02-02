package cn.hutool.crypto.symmetric;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface SymmetricDecryptor {
   byte[] decrypt(byte[] var1);

   void decrypt(InputStream var1, OutputStream var2, boolean var3);

   default String decryptStr(byte[] bytes, Charset charset) {
      return StrUtil.str(this.decrypt(bytes), charset);
   }

   default String decryptStr(byte[] bytes) {
      return this.decryptStr(bytes, CharsetUtil.CHARSET_UTF_8);
   }

   default byte[] decrypt(String data) {
      return this.decrypt(SecureUtil.decode(data));
   }

   default String decryptStr(String data, Charset charset) {
      return StrUtil.str(this.decrypt(data), charset);
   }

   default String decryptStr(String data) {
      return this.decryptStr(data, CharsetUtil.CHARSET_UTF_8);
   }

   default byte[] decrypt(InputStream data) throws IORuntimeException {
      return this.decrypt(IoUtil.readBytes(data));
   }

   default String decryptStr(InputStream data, Charset charset) {
      return StrUtil.str(this.decrypt(data), charset);
   }

   default String decryptStr(InputStream data) {
      return this.decryptStr(data, CharsetUtil.CHARSET_UTF_8);
   }
}
