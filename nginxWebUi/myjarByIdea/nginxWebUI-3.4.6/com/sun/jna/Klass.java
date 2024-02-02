package com.sun.jna;

import java.lang.reflect.InvocationTargetException;

abstract class Klass {
   private Klass() {
   }

   public static <T> T newInstance(Class<T> klass) {
      String msg;
      try {
         return klass.getDeclaredConstructor().newInstance();
      } catch (IllegalAccessException var3) {
         msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var3;
         throw new IllegalArgumentException(msg, var3);
      } catch (IllegalArgumentException var4) {
         msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var4;
         throw new IllegalArgumentException(msg, var4);
      } catch (InstantiationException var5) {
         msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var5;
         throw new IllegalArgumentException(msg, var5);
      } catch (NoSuchMethodException var6) {
         msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var6;
         throw new IllegalArgumentException(msg, var6);
      } catch (SecurityException var7) {
         msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var7;
         throw new IllegalArgumentException(msg, var7);
      } catch (InvocationTargetException var8) {
         if (var8.getCause() instanceof RuntimeException) {
            throw (RuntimeException)var8.getCause();
         } else {
            msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + var8;
            throw new IllegalArgumentException(msg, var8);
         }
      }
   }
}
