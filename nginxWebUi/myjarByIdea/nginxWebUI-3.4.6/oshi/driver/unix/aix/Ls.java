package oshi.driver.unix.aix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Ls {
   private Ls() {
   }

   public static Map<String, Pair<Integer, Integer>> queryDeviceMajorMinor() {
      Map<String, Pair<Integer, Integer>> majMinMap = new HashMap();
      Iterator var1 = ExecutingCommand.runNative("ls -l /dev").iterator();

      while(var1.hasNext()) {
         String s = (String)var1.next();
         if (!s.isEmpty() && s.charAt(0) == 'b') {
            int idx = s.lastIndexOf(32);
            if (idx > 0 && idx < s.length()) {
               String device = s.substring(idx + 1);
               int major = ParseUtil.getNthIntValue(s, 2);
               int minor = ParseUtil.getNthIntValue(s, 3);
               majMinMap.put(device, new Pair(major, minor));
            }
         }
      }

      return majMinMap;
   }
}
