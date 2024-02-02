package org.wildfly.common.cpu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.util.Locale;

public class ProcessorInfo {
   private static final String CPUS_ALLOWED = "Cpus_allowed:";
   private static final byte[] BITS = new byte[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};
   private static final Charset ASCII = Charset.forName("US-ASCII");

   private ProcessorInfo() {
   }

   public static int availableProcessors() {
      return System.getSecurityManager() != null ? (Integer)AccessController.doPrivileged(() -> {
         return determineProcessors();
      }) : determineProcessors();
   }

   private static int determineProcessors() {
      int javaProcs = Runtime.getRuntime().availableProcessors();
      if (!isLinux()) {
         return javaProcs;
      } else {
         int maskProcs = 0;

         try {
            maskProcs = readCPUMask();
         } catch (Exception var3) {
         }

         return maskProcs > 0 ? Math.min(javaProcs, maskProcs) : javaProcs;
      }
   }

   private static int readCPUMask() throws IOException {
      FileInputStream stream = new FileInputStream("/proc/self/status");
      InputStreamReader inputReader = new InputStreamReader(stream, ASCII);
      BufferedReader reader = new BufferedReader(inputReader);

      int i;
      label59: {
         String line;
         try {
            while((line = reader.readLine()) != null) {
               if (line.startsWith("Cpus_allowed:")) {
                  int count = 0;
                  int start = "Cpus_allowed:".length();

                  for(i = start; i < line.length(); ++i) {
                     char ch = line.charAt(i);
                     if (ch >= '0' && ch <= '9') {
                        count += BITS[ch - 48];
                     } else if (ch >= 'a' && ch <= 'f') {
                        count += BITS[ch - 97 + 10];
                     } else if (ch >= 'A' && ch <= 'F') {
                        count += BITS[ch - 65 + 10];
                     }
                  }

                  i = count;
                  break label59;
               }
            }
         } catch (Throwable var9) {
            try {
               reader.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         reader.close();
         return -1;
      }

      reader.close();
      return i;
   }

   private static boolean isLinux() {
      String osArch = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
      return osArch.contains("linux");
   }
}
