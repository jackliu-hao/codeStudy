package org.h2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
   public static final byte[] EMPTY_BYTES = new byte[0];
   public static final int[] EMPTY_INT_ARRAY = new int[0];
   private static final HashMap<String, byte[]> RESOURCES = new HashMap();

   private Utils() {
   }

   public static int indexOf(byte[] var0, byte[] var1, int var2) {
      if (var1.length == 0) {
         return var2;
      } else if (var2 > var0.length) {
         return -1;
      } else {
         int var3 = var0.length - var1.length + 1;

         label32:
         for(int var4 = var1.length; var2 < var3; ++var2) {
            for(int var5 = 0; var5 < var4; ++var5) {
               if (var0[var2 + var5] != var1[var5]) {
                  continue label32;
               }
            }

            return var2;
         }

         return -1;
      }
   }

   public static int getByteArrayHash(byte[] var0) {
      int var1 = var0.length;
      int var2 = var1;
      int var3;
      if (var1 < 50) {
         for(var3 = 0; var3 < var1; ++var3) {
            var2 = 31 * var2 + var0[var3];
         }
      } else {
         var3 = var1 / 16;

         int var4;
         for(var4 = 0; var4 < 4; ++var4) {
            var2 = 31 * var2 + var0[var4];
            int var10000 = 31 * var2;
            --var1;
            var2 = var10000 + var0[var1];
         }

         for(var4 = 4 + var3; var4 < var1; var4 += var3) {
            var2 = 31 * var2 + var0[var4];
         }
      }

      return var2;
   }

   public static boolean compareSecure(byte[] var0, byte[] var1) {
      if (var0 != null && var1 != null) {
         int var2 = var0.length;
         if (var2 != var1.length) {
            return false;
         } else if (var2 == 0) {
            return true;
         } else {
            int var3 = 0;

            for(int var4 = 0; var4 < var2; ++var4) {
               var3 |= var0[var4] ^ var1[var4];
            }

            return var3 == 0;
         }
      } else {
         return var0 == null && var1 == null;
      }
   }

   public static byte[] copy(byte[] var0, byte[] var1) {
      int var2 = var0.length;
      if (var2 > var1.length) {
         var1 = new byte[var2];
      }

      System.arraycopy(var0, 0, var1, 0, var2);
      return var1;
   }

   public static byte[] newBytes(int var0) {
      if (var0 == 0) {
         return EMPTY_BYTES;
      } else {
         try {
            return new byte[var0];
         } catch (OutOfMemoryError var3) {
            OutOfMemoryError var2 = new OutOfMemoryError("Requested memory: " + var0);
            var2.initCause(var3);
            throw var2;
         }
      }
   }

   public static byte[] copyBytes(byte[] var0, int var1) {
      if (var1 == 0) {
         return EMPTY_BYTES;
      } else {
         try {
            return Arrays.copyOf(var0, var1);
         } catch (OutOfMemoryError var4) {
            OutOfMemoryError var3 = new OutOfMemoryError("Requested memory: " + var1);
            var3.initCause(var4);
            throw var3;
         }
      }
   }

   public static byte[] cloneByteArray(byte[] var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length;
         return var1 == 0 ? EMPTY_BYTES : Arrays.copyOf(var0, var1);
      }
   }

   public static long getMemoryUsed() {
      collectGarbage();
      Runtime var0 = Runtime.getRuntime();
      return var0.totalMemory() - var0.freeMemory() >> 10;
   }

   public static long getMemoryFree() {
      collectGarbage();
      return Runtime.getRuntime().freeMemory() >> 10;
   }

   public static long getMemoryMax() {
      return Runtime.getRuntime().maxMemory() >> 10;
   }

   public static long getGarbageCollectionTime() {
      long var0 = 0L;
      Iterator var2 = ManagementFactory.getGarbageCollectorMXBeans().iterator();

      while(var2.hasNext()) {
         GarbageCollectorMXBean var3 = (GarbageCollectorMXBean)var2.next();
         long var4 = var3.getCollectionTime();
         if (var4 > 0L) {
            var0 += var4;
         }
      }

      return var0;
   }

   public static long getGarbageCollectionCount() {
      long var0 = 0L;
      int var2 = 0;
      Iterator var3 = ManagementFactory.getGarbageCollectorMXBeans().iterator();

      while(var3.hasNext()) {
         GarbageCollectorMXBean var4 = (GarbageCollectorMXBean)var3.next();
         long var5 = var4.getCollectionTime();
         if (var5 > 0L) {
            var0 += var5;
            var2 += var4.getMemoryPoolNames().length;
         }
      }

      var2 = Math.max(var2, 1);
      return (var0 + (long)(var2 >> 1)) / (long)var2;
   }

   public static synchronized void collectGarbage() {
      Runtime var0 = Runtime.getRuntime();
      long var1 = getGarbageCollectionCount();

      while(var1 == getGarbageCollectionCount()) {
         var0.gc();
         Thread.yield();
      }

   }

   public static <T> ArrayList<T> newSmallArrayList() {
      return new ArrayList(4);
   }

   public static <X> void sortTopN(X[] var0, int var1, int var2, Comparator<? super X> var3) {
      int var4 = var0.length - 1;
      if (var4 > 0 && var2 > var1) {
         partialQuickSort(var0, 0, var4, var3, var1, var2 - 1);
         Arrays.sort(var0, var1, var2, var3);
      }

   }

   private static <X> void partialQuickSort(X[] var0, int var1, int var2, Comparator<? super X> var3, int var4, int var5) {
      if (var1 < var4 || var2 > var5) {
         int var6 = var1;
         int var7 = var2;
         int var8 = var1 + MathUtils.randomInt(var2 - var1);
         Object var9 = var0[var8];
         int var10 = var1 + var2 >>> 1;
         Object var11 = var0[var10];
         var0[var10] = var9;
         var0[var8] = var11;

         while(var6 <= var7) {
            while(var3.compare(var0[var6], var9) < 0) {
               ++var6;
            }

            while(var3.compare(var0[var7], var9) > 0) {
               --var7;
            }

            if (var6 <= var7) {
               var11 = var0[var6];
               var0[var6++] = var0[var7];
               var0[var7--] = var11;
            }
         }

         if (var1 < var7 && var4 <= var7) {
            partialQuickSort(var0, var1, var7, var3, var4, var5);
         }

         if (var6 < var2 && var6 <= var5) {
            partialQuickSort(var0, var6, var2, var3, var4, var5);
         }

      }
   }

   public static byte[] getResource(String var0) throws IOException {
      byte[] var1 = (byte[])RESOURCES.get(var0);
      if (var1 == null) {
         var1 = loadResource(var0);
         if (var1 != null) {
            RESOURCES.put(var0, var1);
         }
      }

      return var1;
   }

   private static byte[] loadResource(String var0) throws IOException {
      InputStream var1 = Utils.class.getResourceAsStream("data.zip");
      if (var1 == null) {
         var1 = Utils.class.getResourceAsStream(var0);
         return var1 == null ? null : IOUtils.readBytesAndClose(var1, 0);
      } else {
         try {
            ZipInputStream var2 = new ZipInputStream(var1);
            Throwable var3 = null;

            try {
               while(true) {
                  ZipEntry var4 = var2.getNextEntry();
                  if (var4 == null) {
                     return null;
                  }

                  String var5 = var4.getName();
                  if (!var5.startsWith("/")) {
                     var5 = "/" + var5;
                  }

                  if (var5.equals(var0)) {
                     ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                     IOUtils.copy(var2, var6);
                     var2.closeEntry();
                     byte[] var7 = var6.toByteArray();
                     return var7;
                  }

                  var2.closeEntry();
               }
            } catch (Throwable var18) {
               var3 = var18;
               throw var18;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var17) {
                        var3.addSuppressed(var17);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
         } catch (IOException var20) {
            var20.printStackTrace();
            return null;
         }
      }
   }

   public static Object callStaticMethod(String var0, Object... var1) throws Exception {
      int var2 = var0.lastIndexOf(46);
      String var3 = var0.substring(0, var2);
      String var4 = var0.substring(var2 + 1);
      return callMethod((Object)null, Class.forName(var3), var4, var1);
   }

   public static Object callMethod(Object var0, String var1, Object... var2) throws Exception {
      return callMethod(var0, var0.getClass(), var1, var2);
   }

   private static Object callMethod(Object var0, Class<?> var1, String var2, Object... var3) throws Exception {
      Method var4 = null;
      int var5 = 0;
      boolean var6 = var0 == null;
      Method[] var7 = var1.getMethods();
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Method var10 = var7[var9];
         if (Modifier.isStatic(var10.getModifiers()) == var6 && var10.getName().equals(var2)) {
            int var11 = match(var10.getParameterTypes(), var3);
            if (var11 > var5) {
               var5 = var11;
               var4 = var10;
            }
         }
      }

      if (var4 == null) {
         throw new NoSuchMethodException(var2);
      } else {
         return var4.invoke(var0, var3);
      }
   }

   public static Object newInstance(String var0, Object... var1) throws Exception {
      Constructor var2 = null;
      int var3 = 0;
      Constructor[] var4 = Class.forName(var0).getConstructors();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Constructor var7 = var4[var6];
         int var8 = match(var7.getParameterTypes(), var1);
         if (var8 > var3) {
            var3 = var8;
            var2 = var7;
         }
      }

      if (var2 == null) {
         throw new NoSuchMethodException(var0);
      } else {
         return var2.newInstance(var1);
      }
   }

   private static int match(Class<?>[] var0, Object[] var1) {
      int var2 = var0.length;
      if (var2 == var1.length) {
         int var3 = 1;

         for(int var4 = 0; var4 < var2; ++var4) {
            Class var5 = getNonPrimitiveClass(var0[var4]);
            Object var6 = var1[var4];
            Class var7 = var6 == null ? null : var6.getClass();
            if (var5 == var7) {
               ++var3;
            } else if (var7 != null && !var5.isAssignableFrom(var7)) {
               return 0;
            }
         }

         return var3;
      } else {
         return 0;
      }
   }

   public static Class<?> getNonPrimitiveClass(Class<?> var0) {
      if (!var0.isPrimitive()) {
         return var0;
      } else if (var0 == Boolean.TYPE) {
         return Boolean.class;
      } else if (var0 == Byte.TYPE) {
         return Byte.class;
      } else if (var0 == Character.TYPE) {
         return Character.class;
      } else if (var0 == Double.TYPE) {
         return Double.class;
      } else if (var0 == Float.TYPE) {
         return Float.class;
      } else if (var0 == Integer.TYPE) {
         return Integer.class;
      } else if (var0 == Long.TYPE) {
         return Long.class;
      } else if (var0 == Short.TYPE) {
         return Short.class;
      } else {
         return var0 == Void.TYPE ? Void.class : var0;
      }
   }

   public static boolean parseBoolean(String var0, boolean var1, boolean var2) {
      if (var0 == null) {
         return var1;
      } else {
         switch (var0.length()) {
            case 1:
               if (var0.equals("1") || var0.equalsIgnoreCase("t") || var0.equalsIgnoreCase("y")) {
                  return true;
               }

               if (var0.equals("0") || var0.equalsIgnoreCase("f") || var0.equalsIgnoreCase("n")) {
                  return false;
               }
               break;
            case 2:
               if (var0.equalsIgnoreCase("no")) {
                  return false;
               }
               break;
            case 3:
               if (var0.equalsIgnoreCase("yes")) {
                  return true;
               }
               break;
            case 4:
               if (var0.equalsIgnoreCase("true")) {
                  return true;
               }
               break;
            case 5:
               if (var0.equalsIgnoreCase("false")) {
                  return false;
               }
         }

         if (var2) {
            throw new IllegalArgumentException(var0);
         } else {
            return var1;
         }
      }
   }

   public static String getProperty(String var0, String var1) {
      try {
         return System.getProperty(var0, var1);
      } catch (SecurityException var3) {
         return var1;
      }
   }

   public static int getProperty(String var0, int var1) {
      String var2 = getProperty(var0, (String)null);
      if (var2 != null) {
         try {
            return Integer.decode(var2);
         } catch (NumberFormatException var4) {
         }
      }

      return var1;
   }

   public static boolean getProperty(String var0, boolean var1) {
      return parseBoolean(getProperty(var0, (String)null), var1, false);
   }

   public static int scaleForAvailableMemory(int var0) {
      long var1 = Runtime.getRuntime().maxMemory();
      if (var1 != Long.MAX_VALUE) {
         return (int)((long)var0 * var1 / 1073741824L);
      } else {
         try {
            OperatingSystemMXBean var3 = ManagementFactory.getOperatingSystemMXBean();
            Method var4 = Class.forName("com.sun.management.OperatingSystemMXBean").getMethod("getTotalPhysicalMemorySize");
            long var5 = ((Number)var4.invoke(var3)).longValue();
            return (int)((long)var0 * var5 / 1073741824L);
         } catch (Exception var7) {
         } catch (Error var8) {
         }

         return var0;
      }
   }

   public static long currentNanoTime() {
      long var0 = System.nanoTime();
      if (var0 == 0L) {
         var0 = 1L;
      }

      return var0;
   }

   public static long currentNanoTimePlusMillis(int var0) {
      return nanoTimePlusMillis(System.nanoTime(), var0);
   }

   public static long nanoTimePlusMillis(long var0, int var2) {
      long var3 = var0 + (long)var2 * 1000000L;
      if (var3 == 0L) {
         var3 = 1L;
      }

      return var3;
   }

   public interface ClassFactory {
      boolean match(String var1);

      Class<?> loadClass(String var1) throws ClassNotFoundException;
   }
}
