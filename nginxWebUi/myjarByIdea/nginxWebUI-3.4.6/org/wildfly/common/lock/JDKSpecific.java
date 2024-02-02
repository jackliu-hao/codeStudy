package org.wildfly.common.lock;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class JDKSpecific {
   static final Unsafe unsafe = (Unsafe)AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
      public Unsafe run() {
         try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get((Object)null);
         } catch (IllegalAccessException var2) {
            throw new IllegalAccessError(var2.getMessage());
         } catch (NoSuchFieldException var3) {
            throw new NoSuchFieldError(var3.getMessage());
         }
      }
   });

   private JDKSpecific() {
   }

   static void onSpinWait() {
   }
}
