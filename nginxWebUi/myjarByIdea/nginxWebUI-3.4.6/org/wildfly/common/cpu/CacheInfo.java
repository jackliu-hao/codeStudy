package org.wildfly.common.cpu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Locale;

public final class CacheInfo {
   private static final CacheLevelInfo[] cacheLevels = (CacheLevelInfo[])AccessController.doPrivileged(() -> {
      try {
         String osArch = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
         int i;
         if (osArch.contains("linux")) {
            File cpu0 = new File("/sys/devices/system/cpu/cpu0/cache");
            if (cpu0.exists()) {
               File[] files = cpu0.listFiles();
               if (files != null) {
                  ArrayList<File> indexes = new ArrayList();
                  File[] var4 = files;
                  i = files.length;

                  for(int var6 = 0; var6 < i; ++var6) {
                     File filex = var4[var6];
                     if (filex.getName().startsWith("index")) {
                        indexes.add(filex);
                     }
                  }

                  CacheLevelInfo[] levelInfoArray = new CacheLevelInfo[indexes.size()];

                  for(i = 0; i < indexes.size(); ++i) {
                     File file = (File)indexes.get(i);
                     int index = parseIntFile(new File(file, "level"));
                     CacheType type;
                     switch (parseStringFile(new File(file, "type"))) {
                        case "Data":
                           type = CacheType.DATA;
                           break;
                        case "Instruction":
                           type = CacheType.INSTRUCTION;
                           break;
                        case "Unified":
                           type = CacheType.UNIFIED;
                           break;
                        default:
                           type = CacheType.UNKNOWN;
                     }

                     int size = parseIntKBFile(new File(file, "size"));
                     lineSizex = parseIntFile(new File(file, "coherency_line_size"));
                     levelInfoArray[i] = new CacheLevelInfo(index, type, size, lineSizex);
                  }

                  return levelInfoArray;
               }
            }
         } else if (osArch.contains("mac os x")) {
            int lineSize = safeParseInt(parseProcessOutput("/usr/sbin/sysctl", "-n", "hw.cachelinesize"));
            if (lineSize != 0) {
               int l1d = safeParseInt(parseProcessOutput("/usr/sbin/sysctl", "-n", "hw.l1dcachesize"));
               int l1i = safeParseInt(parseProcessOutput("/usr/sbin/sysctl", "-n", "hw.l1icachesize"));
               int l2 = safeParseInt(parseProcessOutput("/usr/sbin/sysctl", "-n", "hw.l2cachesize"));
               i = safeParseInt(parseProcessOutput("/usr/sbin/sysctl", "-n", "hw.l3cachesize"));
               ArrayList<CacheLevelInfo> list = new ArrayList();
               if (l1d != 0) {
                  list.add(new CacheLevelInfo(1, CacheType.DATA, l1d / 1024, lineSize));
               }

               if (l1i != 0) {
                  list.add(new CacheLevelInfo(1, CacheType.INSTRUCTION, l1i / 1024, lineSize));
               }

               if (l2 != 0) {
                  list.add(new CacheLevelInfo(2, CacheType.UNIFIED, l2 / 1024, lineSize));
               }

               if (i != 0) {
                  list.add(new CacheLevelInfo(3, CacheType.UNIFIED, i / 1024, lineSize));
               }

               if (list.size() > 0) {
                  return (CacheLevelInfo[])list.toArray(new CacheLevelInfo[list.size()]);
               }
            }
         } else if (osArch.contains("windows")) {
         }
      } catch (Throwable var11) {
      }

      return new CacheLevelInfo[0];
   });

   public static int getLevelEntryCount() {
      return cacheLevels.length;
   }

   public static CacheLevelInfo getCacheLevelInfo(int index) {
      return cacheLevels[index];
   }

   public static int getSmallestDataCacheLineSize() {
      int minSize = Integer.MAX_VALUE;
      CacheLevelInfo[] var1 = cacheLevels;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CacheLevelInfo cacheLevel = var1[var3];
         if (cacheLevel.getCacheType().isData()) {
            int cacheLineSize = cacheLevel.getCacheLineSize();
            if (cacheLineSize != 0 && cacheLineSize < minSize) {
               minSize = cacheLineSize;
            }
         }
      }

      return minSize == Integer.MAX_VALUE ? 0 : minSize;
   }

   public static int getSmallestInstructionCacheLineSize() {
      int minSize = Integer.MAX_VALUE;
      CacheLevelInfo[] var1 = cacheLevels;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CacheLevelInfo cacheLevel = var1[var3];
         if (cacheLevel.getCacheType().isInstruction()) {
            int cacheLineSize = cacheLevel.getCacheLineSize();
            if (cacheLineSize != 0 && cacheLineSize < minSize) {
               minSize = cacheLineSize;
            }
         }
      }

      return minSize == Integer.MAX_VALUE ? 0 : minSize;
   }

   static int parseIntFile(File file) {
      return safeParseInt(parseStringFile(file));
   }

   static int safeParseInt(String string) {
      try {
         return Integer.parseInt(string);
      } catch (Throwable var2) {
         return 0;
      }
   }

   static int parseIntKBFile(File file) {
      try {
         String s = parseStringFile(file);
         if (s.endsWith("K")) {
            return Integer.parseInt(s.substring(0, s.length() - 1));
         } else if (s.endsWith("M")) {
            return Integer.parseInt(s.substring(0, s.length() - 1)) * 1024;
         } else {
            return s.endsWith("G") ? Integer.parseInt(s.substring(0, s.length() - 1)) * 1024 * 1024 : Integer.parseInt(s);
         }
      } catch (Throwable var2) {
         return 0;
      }
   }

   static String parseStringFile(File file) {
      try {
         FileInputStream is = new FileInputStream(file);

         String var2;
         try {
            var2 = parseStringStream(is);
         } catch (Throwable var5) {
            try {
               is.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         is.close();
         return var2;
      } catch (Throwable var6) {
         return "";
      }
   }

   static String parseStringStream(InputStream is) {
      try {
         Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);

         String var5;
         try {
            StringBuilder b = new StringBuilder();
            char[] cb = new char[64];

            while(true) {
               int res;
               if ((res = r.read(cb)) == -1) {
                  var5 = b.toString().trim();
                  break;
               }

               b.append(cb, 0, res);
            }
         } catch (Throwable var7) {
            try {
               r.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         r.close();
         return var5;
      } catch (Throwable var8) {
         return "";
      }
   }

   static String parseProcessOutput(String... args) {
      ProcessBuilder processBuilder = new ProcessBuilder(args);

      try {
         Process process = processBuilder.start();
         process.getOutputStream().close();
         InputStream errorStream = process.getErrorStream();
         Thread errorThread = new Thread((ThreadGroup)null, new StreamConsumer(errorStream), "Process thread", 32768L);
         errorThread.start();
         InputStream inputStream = process.getInputStream();

         String result;
         try {
            result = parseStringStream(inputStream);
         } catch (Throwable var61) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var58) {
                  var61.addSuppressed(var58);
               }
            }

            throw var61;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         boolean intr = false;

         Object var8;
         try {
            process.waitFor();
            return result;
         } catch (InterruptedException var62) {
            intr = true;
            var8 = null;
         } finally {
            try {
               errorThread.join();
            } catch (InterruptedException var59) {
               intr = true;
            } finally {
               if (intr) {
                  Thread.currentThread().interrupt();
               }

            }

         }

         return (String)var8;
      } catch (IOException var64) {
         return "";
      }
   }

   public static void main(String[] args) {
      System.out.println("Detected cache info:");
      CacheLevelInfo[] var1 = cacheLevels;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CacheLevelInfo levelInfo = var1[var3];
         System.out.printf("Level %d cache: type %s, size %d KiB, cache line is %d bytes%n", levelInfo.getCacheLevel(), levelInfo.getCacheType(), levelInfo.getCacheLevelSizeKB(), levelInfo.getCacheLineSize());
      }

   }

   static class StreamConsumer implements Runnable {
      private final InputStream stream;

      StreamConsumer(InputStream stream) {
         this.stream = stream;
      }

      public void run() {
         byte[] buffer = new byte[128];

         try {
            while(true) {
               if (this.stream.read(buffer) != -1) {
                  continue;
               }
            }
         } catch (IOException var11) {
         } finally {
            try {
               this.stream.close();
            } catch (IOException var10) {
            }

         }

      }
   }
}
