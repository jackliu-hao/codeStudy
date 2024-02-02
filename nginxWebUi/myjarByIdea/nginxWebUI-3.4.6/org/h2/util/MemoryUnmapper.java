package org.h2.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.h2.engine.SysProperties;

public final class MemoryUnmapper {
   private static final boolean ENABLED;
   private static final Object UNSAFE;
   private static final Method INVOKE_CLEANER;

   public static boolean unmap(ByteBuffer var0) {
      if (!ENABLED) {
         return false;
      } else {
         try {
            if (INVOKE_CLEANER != null) {
               INVOKE_CLEANER.invoke(UNSAFE, var0);
               return true;
            } else {
               Method var1 = var0.getClass().getMethod("cleaner");
               var1.setAccessible(true);
               Object var2 = var1.invoke(var0);
               if (var2 != null) {
                  Method var3 = var2.getClass().getMethod("clean");
                  var3.invoke(var2);
               }

               return true;
            }
         } catch (Throwable var4) {
            return false;
         }
      }
   }

   private MemoryUnmapper() {
   }

   static {
      boolean var0 = SysProperties.NIO_CLEANER_HACK;
      Object var1 = null;
      Method var2 = null;
      if (var0) {
         try {
            Class var3 = Class.forName("sun.misc.Unsafe");
            Field var4 = var3.getDeclaredField("theUnsafe");
            var4.setAccessible(true);
            var1 = var4.get((Object)null);
            var2 = var3.getMethod("invokeCleaner", ByteBuffer.class);
         } catch (ReflectiveOperationException var5) {
            var1 = null;
         } catch (Throwable var6) {
            var0 = false;
            var1 = null;
         }
      }

      ENABLED = var0;
      UNSAFE = var1;
      INVOKE_CLEANER = var2;
   }
}
