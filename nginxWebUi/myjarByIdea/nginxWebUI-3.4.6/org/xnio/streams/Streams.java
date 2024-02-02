package org.xnio.streams;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.xnio.IoUtils;

public final class Streams {
   private Streams() {
   }

   public static void copyStream(InputStream input, OutputStream output, boolean close, int bufferSize) throws IOException {
      byte[] buffer = new byte[bufferSize];

      try {
         while(true) {
            int res = input.read(buffer);
            if (res == -1) {
               if (close) {
                  input.close();
                  output.close();
               }
               break;
            }

            output.write(buffer, 0, res);
         }
      } finally {
         if (close) {
            IoUtils.safeClose((Closeable)input);
            IoUtils.safeClose((Closeable)output);
         }

      }

   }

   public static void copyStream(InputStream input, OutputStream output, boolean close) throws IOException {
      copyStream(input, output, close, 8192);
   }

   public static void copyStream(InputStream input, OutputStream output) throws IOException {
      copyStream(input, output, true, 8192);
   }

   static Charset getCharset(String charsetName) throws UnsupportedEncodingException {
      try {
         return Charset.forName(charsetName);
      } catch (UnsupportedCharsetException var2) {
         throw new UnsupportedEncodingException(var2.getMessage());
      }
   }
}
