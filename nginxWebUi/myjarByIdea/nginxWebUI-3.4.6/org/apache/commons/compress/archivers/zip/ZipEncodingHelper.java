package org.apache.commons.compress.archivers.zip;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;

public abstract class ZipEncodingHelper {
   static final String UTF8 = "UTF8";
   static final ZipEncoding UTF8_ZIP_ENCODING = getZipEncoding("UTF8");

   public static ZipEncoding getZipEncoding(String name) {
      Charset cs = Charset.defaultCharset();
      if (name != null) {
         try {
            cs = Charset.forName(name);
         } catch (UnsupportedCharsetException var3) {
         }
      }

      boolean useReplacement = isUTF8(cs.name());
      return new NioZipEncoding(cs, useReplacement);
   }

   static boolean isUTF8(String charsetName) {
      if (charsetName == null) {
         charsetName = Charset.defaultCharset().name();
      }

      if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
         return true;
      } else {
         Iterator var1 = StandardCharsets.UTF_8.aliases().iterator();

         String alias;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            alias = (String)var1.next();
         } while(!alias.equalsIgnoreCase(charsetName));

         return true;
      }
   }

   static ByteBuffer growBufferBy(ByteBuffer buffer, int increment) {
      buffer.limit(buffer.position());
      buffer.rewind();
      ByteBuffer on = ByteBuffer.allocate(buffer.capacity() + increment);
      on.put(buffer);
      return on;
   }
}
