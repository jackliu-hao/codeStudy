package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public interface Resource {
   String getName();

   URL getUrl();

   InputStream getStream();

   default boolean isModified() {
      return false;
   }

   default void writeTo(OutputStream out) throws IORuntimeException {
      try {
         InputStream in = this.getStream();
         Throwable var3 = null;

         try {
            IoUtil.copy(in, out);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (in != null) {
               if (var3 != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  in.close();
               }
            }

         }

      } catch (IOException var15) {
         throw new IORuntimeException(var15);
      }
   }

   default BufferedReader getReader(Charset charset) {
      return IoUtil.getReader(this.getStream(), charset);
   }

   default String readStr(Charset charset) throws IORuntimeException {
      return IoUtil.read((Reader)this.getReader(charset));
   }

   default String readUtf8Str() throws IORuntimeException {
      return this.readStr(CharsetUtil.CHARSET_UTF_8);
   }

   default byte[] readBytes() throws IORuntimeException {
      return IoUtil.readBytes(this.getStream());
   }
}
