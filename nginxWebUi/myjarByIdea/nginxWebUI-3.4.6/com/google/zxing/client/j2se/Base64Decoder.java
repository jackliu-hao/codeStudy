package com.google.zxing.client.j2se;

abstract class Base64Decoder {
   private static final Base64Decoder INSTANCE;

   abstract byte[] decode(String var1);

   static Base64Decoder getInstance() {
      return INSTANCE;
   }

   static {
      Object instance;
      try {
         Class.forName("java.util.Base64");
         instance = new Java8Base64Decoder();
      } catch (ClassNotFoundException var2) {
         instance = new JAXBBase64Decoder();
      }

      INSTANCE = (Base64Decoder)instance;
   }
}
