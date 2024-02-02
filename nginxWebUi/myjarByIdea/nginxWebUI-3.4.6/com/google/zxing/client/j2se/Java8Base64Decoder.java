package com.google.zxing.client.j2se;

import java.lang.reflect.InvocationTargetException;

final class Java8Base64Decoder extends Base64Decoder {
   byte[] decode(String s) {
      try {
         Object decoder = Class.forName("java.util.Base64").getMethod("getDecoder").invoke((Object)null);
         return (byte[])((byte[])Class.forName("java.util.Base64.Decoder").getMethod("decode", String.class).invoke(decoder, s));
      } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException var3) {
         throw new IllegalStateException(var3);
      }
   }
}
