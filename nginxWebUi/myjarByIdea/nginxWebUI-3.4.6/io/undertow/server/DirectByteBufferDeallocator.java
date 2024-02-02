package io.undertow.server;

import io.undertow.UndertowLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

public final class DirectByteBufferDeallocator {
   private static final boolean SUPPORTED;
   private static final Method cleaner;
   private static final Method cleanerClean;
   private static final Unsafe UNSAFE;

   private DirectByteBufferDeallocator() {
   }

   public static void free(ByteBuffer buffer) {
      if (SUPPORTED && buffer != null && buffer.isDirect()) {
         try {
            if (UNSAFE != null) {
               cleanerClean.invoke(UNSAFE, buffer);
            } else {
               Object cleaner = DirectByteBufferDeallocator.cleaner.invoke(buffer);
               cleanerClean.invoke(cleaner);
            }
         } catch (Throwable var2) {
            UndertowLogger.ROOT_LOGGER.directBufferDeallocationFailed(var2);
         }
      }

   }

   private static Unsafe getUnsafe() {
      return System.getSecurityManager() != null ? (Unsafe)AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
         public Unsafe run() {
            return DirectByteBufferDeallocator.getUnsafe0();
         }
      }) : getUnsafe0();
   }

   private static Unsafe getUnsafe0() {
      try {
         Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
         theUnsafe.setAccessible(true);
         return (Unsafe)theUnsafe.get((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException("JDK did not allow accessing unsafe", var1);
      }
   }

   private static Method getAccesibleMethod(final String className, final String methodName) {
      return System.getSecurityManager() != null ? (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            return DirectByteBufferDeallocator.getAccesibleMethod0(className, methodName);
         }
      }) : getAccesibleMethod0(className, methodName);
   }

   private static Method getAccesibleMethod0(String className, String methodName) {
      try {
         Method method = Class.forName(className).getMethod(methodName);
         method.setAccessible(true);
         return method;
      } catch (Throwable var3) {
         throw new RuntimeException("JDK did not allow accessing method", var3);
      }
   }

   private static Method getDeclaredMethod(final Unsafe tmpUnsafe, final String methodName, final Class<?>... parameterTypes) {
      return System.getSecurityManager() != null ? (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
         public Method run() {
            return DirectByteBufferDeallocator.getDeclaredMethod0(tmpUnsafe, methodName, parameterTypes);
         }
      }) : getDeclaredMethod0(tmpUnsafe, methodName, parameterTypes);
   }

   private static Method getDeclaredMethod0(Unsafe tmpUnsafe, String methodName, Class<?>... parameterTypes) {
      try {
         Method method = tmpUnsafe.getClass().getDeclaredMethod(methodName, parameterTypes);
         method.setAccessible(true);
         return method;
      } catch (Throwable var4) {
         throw new RuntimeException("JDK did not allow accessing method", var4);
      }
   }

   static {
      String versionString = System.getProperty("java.specification.version");
      if (versionString.startsWith("1.")) {
         versionString = versionString.substring(2);
      }

      int version = Integer.parseInt(versionString);
      Method tmpCleaner = null;
      Method tmpCleanerClean = null;
      Unsafe tmpUnsafe = null;
      boolean supported;
      if (version < 9) {
         try {
            tmpCleaner = getAccesibleMethod("java.nio.DirectByteBuffer", "cleaner");
            tmpCleanerClean = getAccesibleMethod("sun.misc.Cleaner", "clean");
            supported = true;
         } catch (Throwable var8) {
            UndertowLogger.ROOT_LOGGER.directBufferDeallocatorInitializationFailed(var8);
            supported = false;
         }
      } else {
         try {
            tmpUnsafe = getUnsafe();
            tmpCleanerClean = getDeclaredMethod(tmpUnsafe, "invokeCleaner", ByteBuffer.class);
            supported = true;
         } catch (Throwable var7) {
            UndertowLogger.ROOT_LOGGER.directBufferDeallocatorInitializationFailed(var7);
            supported = false;
         }
      }

      SUPPORTED = supported;
      cleaner = tmpCleaner;
      cleanerClean = tmpCleanerClean;
      UNSAFE = tmpUnsafe;
   }
}
