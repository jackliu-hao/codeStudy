package org.apache.http.client.utils;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class Punycode {
   private static final Idn impl;

   public static String toUnicode(String punycode) {
      return impl.toUnicode(punycode);
   }

   static {
      Object _impl;
      try {
         _impl = new JdkIdn();
      } catch (Exception var2) {
         _impl = new Rfc3492Idn();
      }

      impl = (Idn)_impl;
   }
}
