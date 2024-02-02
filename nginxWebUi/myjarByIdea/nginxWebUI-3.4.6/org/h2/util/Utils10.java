package org.h2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;

public final class Utils10 {
   public static String byteArrayOutputStreamToString(ByteArrayOutputStream var0, Charset var1) {
      try {
         return var0.toString(var1.name());
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static boolean getTcpQuickack(Socket var0) throws IOException {
      throw new UnsupportedOperationException();
   }

   public static boolean setTcpQuickack(Socket var0, boolean var1) {
      return false;
   }

   private Utils10() {
   }
}
