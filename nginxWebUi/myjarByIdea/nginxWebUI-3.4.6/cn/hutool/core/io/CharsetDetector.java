package cn.hutool.core.io;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetDetector {
   private static final Charset[] DEFAULT_CHARSETS;

   public static Charset detect(File file, Charset... charsets) {
      return detect((InputStream)FileUtil.getInputStream(file), charsets);
   }

   public static Charset detect(InputStream in, Charset... charsets) {
      return detect(8192, in, charsets);
   }

   public static Charset detect(int bufferSize, InputStream in, Charset... charsets) {
      if (ArrayUtil.isEmpty((Object[])charsets)) {
         charsets = DEFAULT_CHARSETS;
      }

      byte[] buffer = new byte[bufferSize];

      try {
         while(in.read(buffer) > -1) {
            Charset[] var4 = charsets;
            int var5 = charsets.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Charset charset = var4[var6];
               CharsetDecoder decoder = charset.newDecoder();
               if (identify(buffer, decoder)) {
                  Charset var9 = charset;
                  return var9;
               }
            }
         }
      } catch (IOException var13) {
         throw new IORuntimeException(var13);
      } finally {
         IoUtil.close(in);
      }

      return null;
   }

   private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
      try {
         decoder.decode(ByteBuffer.wrap(bytes));
         return true;
      } catch (CharacterCodingException var3) {
         return false;
      }
   }

   static {
      String[] names = new String[]{"UTF-8", "GBK", "GB2312", "GB18030", "UTF-16BE", "UTF-16LE", "UTF-16", "BIG5", "UNICODE", "US-ASCII"};
      DEFAULT_CHARSETS = (Charset[])Convert.convert((Class)Charset[].class, names);
   }
}
