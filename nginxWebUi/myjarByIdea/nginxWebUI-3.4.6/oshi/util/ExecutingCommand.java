package oshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ExecutingCommand {
   private static final Logger LOG = LoggerFactory.getLogger(ExecutingCommand.class);
   private static final String[] DEFAULT_ENV = getDefaultEnv();

   private ExecutingCommand() {
   }

   private static String[] getDefaultEnv() {
      PlatformEnum platform = SystemInfo.getCurrentPlatformEnum();
      if (platform == PlatformEnum.WINDOWS) {
         return new String[]{"LANGUAGE=C"};
      } else {
         return platform != PlatformEnum.UNKNOWN ? new String[]{"LC_ALL=C"} : null;
      }
   }

   public static List<String> runNative(String cmdToRun) {
      String[] cmd = cmdToRun.split(" ");
      return runNative(cmd);
   }

   public static List<String> runNative(String[] cmdToRunWithArgs) {
      Process p = null;

      try {
         p = Runtime.getRuntime().exec(cmdToRunWithArgs, DEFAULT_ENV);
      } catch (IOException | SecurityException var7) {
         LOG.trace((String)"Couldn't run command {}: {}", (Object)Arrays.toString(cmdToRunWithArgs), (Object)var7.getMessage());
         return new ArrayList(0);
      }

      ArrayList<String> sa = new ArrayList();

      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()));

         try {
            String line;
            while((line = reader.readLine()) != null) {
               sa.add(line);
            }

            p.waitFor();
         } catch (Throwable var8) {
            try {
               reader.close();
            } catch (Throwable var6) {
               var8.addSuppressed(var6);
            }

            throw var8;
         }

         reader.close();
      } catch (IOException var9) {
         LOG.trace((String)"Problem reading output from {}: {}", (Object)Arrays.toString(cmdToRunWithArgs), (Object)var9.getMessage());
         return new ArrayList(0);
      } catch (InterruptedException var10) {
         LOG.trace((String)"Interrupted while reading output from {}: {}", (Object)Arrays.toString(cmdToRunWithArgs), (Object)var10.getMessage());
         Thread.currentThread().interrupt();
      }

      return sa;
   }

   public static String getFirstAnswer(String cmd2launch) {
      return getAnswerAt(cmd2launch, 0);
   }

   public static String getAnswerAt(String cmd2launch, int answerIdx) {
      List<String> sa = runNative(cmd2launch);
      return answerIdx >= 0 && answerIdx < sa.size() ? (String)sa.get(answerIdx) : "";
   }
}
