package org.apache.http.client.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CloneUtils {
   public static <T> T cloneObject(T obj) throws CloneNotSupportedException {
      if (obj == null) {
         return null;
      } else if (obj instanceof Cloneable) {
         Class<?> clazz = obj.getClass();

         Method m;
         try {
            m = clazz.getMethod("clone", (Class[])null);
         } catch (NoSuchMethodException var5) {
            throw new NoSuchMethodError(var5.getMessage());
         }

         try {
            T result = m.invoke(obj, (Object[])null);
            return result;
         } catch (InvocationTargetException var6) {
            Throwable cause = var6.getCause();
            if (cause instanceof CloneNotSupportedException) {
               throw (CloneNotSupportedException)cause;
            } else {
               throw new Error("Unexpected exception", cause);
            }
         } catch (IllegalAccessException var7) {
            throw new IllegalAccessError(var7.getMessage());
         }
      } else {
         throw new CloneNotSupportedException();
      }
   }

   public static Object clone(Object obj) throws CloneNotSupportedException {
      return cloneObject(obj);
   }

   private CloneUtils() {
   }
}
