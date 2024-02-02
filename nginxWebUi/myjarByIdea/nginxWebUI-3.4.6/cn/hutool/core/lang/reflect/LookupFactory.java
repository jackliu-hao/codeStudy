package cn.hutool.core.lang.reflect;

import cn.hutool.core.exceptions.UtilException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LookupFactory {
   private static final int ALLOWED_MODES = 15;
   private static Constructor<MethodHandles.Lookup> java8LookupConstructor;
   private static Method privateLookupInMethod;

   public static MethodHandles.Lookup lookup(Class<?> callerClass) {
      if (privateLookupInMethod != null) {
         try {
            return (MethodHandles.Lookup)privateLookupInMethod.invoke(MethodHandles.class, callerClass, MethodHandles.lookup());
         } catch (InvocationTargetException | IllegalAccessException var2) {
            throw new UtilException(var2);
         }
      } else {
         try {
            return (MethodHandles.Lookup)java8LookupConstructor.newInstance(callerClass, 15);
         } catch (Exception var3) {
            throw new IllegalStateException("no 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", var3);
         }
      }
   }

   static {
      try {
         privateLookupInMethod = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
      } catch (NoSuchMethodException var2) {
      }

      if (privateLookupInMethod == null) {
         try {
            java8LookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            java8LookupConstructor.setAccessible(true);
         } catch (NoSuchMethodException var1) {
            throw new IllegalStateException("There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", var1);
         }
      }

   }
}
