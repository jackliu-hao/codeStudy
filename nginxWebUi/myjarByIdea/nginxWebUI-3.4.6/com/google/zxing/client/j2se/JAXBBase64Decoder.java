package com.google.zxing.client.j2se;

import java.lang.reflect.InvocationTargetException;

final class JAXBBase64Decoder extends Base64Decoder {
   byte[] decode(String s) {
      try {
         return (byte[])((byte[])Class.forName("javax.xml.bind.DatatypeConverter").getMethod("parseBase64Binary", String.class).invoke((Object)null, s));
      } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException var3) {
         throw new IllegalStateException(var3);
      }
   }
}
