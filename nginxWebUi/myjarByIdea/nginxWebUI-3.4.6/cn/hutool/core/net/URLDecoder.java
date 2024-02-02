package cn.hutool.core.net;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

public class URLDecoder implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final byte ESCAPE_CHAR = 37;

   public static String decodeForPath(String str, Charset charset) {
      return decode(str, charset, false);
   }

   public static String decode(String str, Charset charset) {
      return decode(str, charset, true);
   }

   public static String decode(String str, Charset charset, boolean isPlusToSpace) {
      return null == charset ? str : StrUtil.str(decode(StrUtil.bytes(str, charset), isPlusToSpace), charset);
   }

   public static byte[] decode(byte[] bytes) {
      return decode(bytes, true);
   }

   public static byte[] decode(byte[] bytes, boolean isPlusToSpace) {
      if (bytes == null) {
         return null;
      } else {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream(bytes.length);

         for(int i = 0; i < bytes.length; ++i) {
            int b = bytes[i];
            if (b == 43) {
               buffer.write(isPlusToSpace ? 32 : b);
            } else if (b == 37) {
               if (i + 1 < bytes.length) {
                  int u = CharUtil.digit16(bytes[i + 1]);
                  if (u >= 0 && i + 2 < bytes.length) {
                     int l = CharUtil.digit16(bytes[i + 2]);
                     if (l >= 0) {
                        buffer.write((char)((u << 4) + l));
                        i += 2;
                        continue;
                     }
                  }
               }

               buffer.write(b);
            } else {
               buffer.write(b);
            }
         }

         return buffer.toByteArray();
      }
   }
}
