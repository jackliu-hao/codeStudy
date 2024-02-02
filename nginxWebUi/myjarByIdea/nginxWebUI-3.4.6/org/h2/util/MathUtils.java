package org.h2.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
   static SecureRandom secureRandom;
   static volatile boolean seeded;

   private MathUtils() {
   }

   public static int roundUpInt(int var0, int var1) {
      return var0 + var1 - 1 & -var1;
   }

   public static long roundUpLong(long var0, long var2) {
      return var0 + var2 - 1L & -var2;
   }

   private static synchronized SecureRandom getSecureRandom() {
      if (secureRandom != null) {
         return secureRandom;
      } else {
         try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            Runnable var0 = () -> {
               try {
                  SecureRandom var0 = SecureRandom.getInstance("SHA1PRNG");
                  byte[] var1 = var0.generateSeed(20);
                  synchronized(secureRandom) {
                     secureRandom.setSeed(var1);
                     seeded = true;
                  }
               } catch (Exception var5) {
                  warn("SecureRandom", var5);
               }

            };

            try {
               Thread var1 = new Thread(var0, "Generate Seed");
               var1.setDaemon(true);
               var1.start();
               Thread.yield();

               try {
                  var1.join(400L);
               } catch (InterruptedException var6) {
                  warn("InterruptedException", var6);
               }

               if (!seeded) {
                  byte[] var2 = generateAlternativeSeed();
                  synchronized(secureRandom) {
                     secureRandom.setSeed(var2);
                  }
               }
            } catch (SecurityException var7) {
               var0.run();
               generateAlternativeSeed();
            }
         } catch (Exception var8) {
            warn("SecureRandom", var8);
            secureRandom = new SecureRandom();
         }

         return secureRandom;
      }
   }

   public static byte[] generateAlternativeSeed() {
      try {
         ByteArrayOutputStream var0 = new ByteArrayOutputStream();
         DataOutputStream var1 = new DataOutputStream(var0);
         var1.writeLong(System.currentTimeMillis());
         var1.writeLong(System.nanoTime());
         var1.writeInt((new Object()).hashCode());
         Runtime var2 = Runtime.getRuntime();
         var1.writeLong(var2.freeMemory());
         var1.writeLong(var2.maxMemory());
         var1.writeLong(var2.totalMemory());

         try {
            String var3 = System.getProperties().toString();
            var1.writeInt(var3.length());
            var1.write(var3.getBytes(StandardCharsets.UTF_8));
         } catch (Exception var12) {
            warn("generateAlternativeSeed", var12);
         }

         try {
            Class var15 = Class.forName("java.net.InetAddress");
            Object var4 = var15.getMethod("getLocalHost").invoke((Object)null);
            String var5 = var15.getMethod("getHostName").invoke(var4).toString();
            var1.writeUTF(var5);
            Object[] var6 = (Object[])((Object[])var15.getMethod("getAllByName", String.class).invoke((Object)null, var5));
            Method var7 = var15.getMethod("getAddress");
            Object[] var8 = var6;
            int var9 = var6.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Object var11 = var8[var10];
               var1.write((byte[])((byte[])var7.invoke(var11)));
            }
         } catch (Throwable var13) {
         }

         for(int var16 = 0; var16 < 16; ++var16) {
            int var17 = 0;

            for(long var18 = System.currentTimeMillis(); var18 == System.currentTimeMillis(); ++var17) {
            }

            var1.writeInt(var17);
         }

         var1.close();
         return var0.toByteArray();
      } catch (IOException var14) {
         warn("generateAlternativeSeed", var14);
         return new byte[1];
      }
   }

   static void warn(String var0, Throwable var1) {
      System.out.println("Warning: " + var0);
      if (var1 != null) {
         var1.printStackTrace();
      }

   }

   public static int nextPowerOf2(int var0) throws IllegalArgumentException {
      if (var0 + Integer.MIN_VALUE > -1073741824) {
         throw new IllegalArgumentException("Argument out of range [0x0-0x40000000]. Argument was: " + var0);
      } else {
         return var0 <= 1 ? 1 : (-1 >>> Integer.numberOfLeadingZeros(var0 - 1)) + 1;
      }
   }

   public static int convertLongToInt(long var0) {
      if (var0 <= -2147483648L) {
         return Integer.MIN_VALUE;
      } else {
         return var0 >= 2147483647L ? Integer.MAX_VALUE : (int)var0;
      }
   }

   public static short convertIntToShort(int var0) {
      if (var0 <= -32768) {
         return Short.MIN_VALUE;
      } else {
         return var0 >= 32767 ? 32767 : (short)var0;
      }
   }

   public static long secureRandomLong() {
      return getSecureRandom().nextLong();
   }

   public static void randomBytes(byte[] var0) {
      ThreadLocalRandom.current().nextBytes(var0);
   }

   public static byte[] secureRandomBytes(int var0) {
      if (var0 <= 0) {
         var0 = 1;
      }

      byte[] var1 = new byte[var0];
      getSecureRandom().nextBytes(var1);
      return var1;
   }

   public static int randomInt(int var0) {
      return ThreadLocalRandom.current().nextInt(var0);
   }

   public static int secureRandomInt(int var0) {
      return getSecureRandom().nextInt(var0);
   }
}
