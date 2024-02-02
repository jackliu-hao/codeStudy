package oshi.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class LsofUtil {
   private LsofUtil() {
   }

   public static Map<Integer, String> getCwdMap(int pid) {
      List<String> lsof = ExecutingCommand.runNative("lsof -Fn -d cwd" + (pid < 0 ? "" : " -p " + pid));
      Map<Integer, String> cwdMap = new HashMap();
      Integer key = -1;
      Iterator var4 = lsof.iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         if (!line.isEmpty()) {
            switch (line.charAt(0)) {
               case 'f':
               default:
                  break;
               case 'n':
                  cwdMap.put(key, line.substring(1));
                  break;
               case 'p':
                  key = ParseUtil.parseIntOrDefault(line.substring(1), -1);
            }
         }
      }

      return cwdMap;
   }

   public static String getCwd(int pid) {
      List<String> lsof = ExecutingCommand.runNative("lsof -Fn -d cwd -p " + pid);
      Iterator var2 = lsof.iterator();

      String line;
      do {
         if (!var2.hasNext()) {
            return "";
         }

         line = (String)var2.next();
      } while(line.isEmpty() || line.charAt(0) != 'n');

      return line.substring(1).trim();
   }

   public static long getOpenFiles(int pid) {
      return (long)ExecutingCommand.runNative(String.format("lsof -p %d", pid)).size() - 1L;
   }
}
