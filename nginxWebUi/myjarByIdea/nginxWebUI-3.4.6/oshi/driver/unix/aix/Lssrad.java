package oshi.driver.unix.aix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Lssrad {
   private Lssrad() {
   }

   public static Map<Integer, Pair<Integer, Integer>> queryNodesPackages() {
      int node = 0;
      int slot = 0;
      Map<Integer, Pair<Integer, Integer>> nodeMap = new HashMap();
      List<String> lssrad = ExecutingCommand.runNative("lssrad -av");
      if (!lssrad.isEmpty()) {
         lssrad.remove(0);
      }

      Iterator var4 = lssrad.iterator();

      while(true) {
         while(true) {
            String s;
            String t;
            do {
               if (!var4.hasNext()) {
                  return nodeMap;
               }

               s = (String)var4.next();
               t = s.trim();
            } while(t.isEmpty());

            if (Character.isDigit(s.charAt(0))) {
               node = ParseUtil.parseIntOrDefault(t, 0);
            } else {
               if (t.contains(".")) {
                  String[] split = ParseUtil.whitespaces.split(t, 3);
                  slot = ParseUtil.parseIntOrDefault(split[0], 0);
                  t = split.length > 2 ? split[2] : "";
               }

               Iterator var9 = ParseUtil.parseHyphenatedIntList(t).iterator();

               while(var9.hasNext()) {
                  Integer proc = (Integer)var9.next();
                  nodeMap.put(proc, new Pair(node, slot));
               }
            }
         }
      }
   }
}
