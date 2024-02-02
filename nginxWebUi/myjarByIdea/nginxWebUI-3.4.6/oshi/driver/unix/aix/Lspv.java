package oshi.driver.unix.aix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Lspv {
   private Lspv() {
   }

   public static List<HWPartition> queryLogicalVolumes(String device, Map<String, Pair<Integer, Integer>> majMinMap) {
      String stateMarker = "PV STATE:";
      String sizeMarker = "PP SIZE:";
      long ppSize = 0L;
      Iterator var6 = ExecutingCommand.runNative("lspv -L " + device).iterator();

      while(var6.hasNext()) {
         String s = (String)var6.next();
         if (s.startsWith(stateMarker)) {
            if (!s.contains("active")) {
               return Collections.emptyList();
            }
         } else if (s.contains(sizeMarker)) {
            ppSize = (long)ParseUtil.getFirstIntValue(s);
         }
      }

      if (ppSize == 0L) {
         return Collections.emptyList();
      } else {
         ppSize <<= 20;
         Map<String, String> mountMap = new HashMap();
         Map<String, String> typeMap = new HashMap();
         Map<String, Integer> ppMap = new HashMap();
         Iterator var9 = ExecutingCommand.runNative("lspv -p " + device).iterator();

         String mount;
         while(var9.hasNext()) {
            String s = (String)var9.next();
            String[] split = ParseUtil.whitespaces.split(s.trim());
            if (split.length >= 6 && "used".equals(split[1])) {
               mount = split[split.length - 3];
               mountMap.put(mount, split[split.length - 1]);
               typeMap.put(mount, split[split.length - 2]);
               int ppCount = 1 + ParseUtil.getNthIntValue(split[0], 2) - ParseUtil.getNthIntValue(split[0], 1);
               ppMap.put(mount, ppCount + (Integer)ppMap.getOrDefault(mount, 0));
            }
         }

         List<HWPartition> partitions = new ArrayList();
         Iterator var23 = mountMap.entrySet().iterator();

         while(var23.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var23.next();
            mount = "N/A".equals(entry.getValue()) ? "" : (String)entry.getValue();
            String name = (String)entry.getKey();
            String type = (String)typeMap.get(name);
            long size = ppSize * (long)(Integer)ppMap.get(name);
            Pair<Integer, Integer> majMin = (Pair)majMinMap.get(name);
            int major = majMin == null ? ParseUtil.getFirstIntValue(name) : (Integer)majMin.getA();
            int minor = majMin == null ? ParseUtil.getFirstIntValue(name) : (Integer)majMin.getB();
            partitions.add(new HWPartition(name, name, type, "", size, major, minor, mount));
         }

         return partitions;
      }
   }
}
