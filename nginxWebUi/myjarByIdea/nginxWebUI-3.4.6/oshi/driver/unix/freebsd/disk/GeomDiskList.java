package oshi.driver.unix.freebsd.disk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class GeomDiskList {
   private static final String GEOM_DISK_LIST = "geom disk list";

   private GeomDiskList() {
   }

   public static Map<String, Triplet<String, String, Long>> queryDisks() {
      Map<String, Triplet<String, String, Long>> diskMap = new HashMap();
      String diskName = null;
      String descr = "unknown";
      String ident = "unknown";
      long mediaSize = 0L;
      List<String> geom = ExecutingCommand.runNative("geom disk list");
      Iterator var7 = geom.iterator();

      while(var7.hasNext()) {
         String line = (String)var7.next();
         line = line.trim();
         if (line.startsWith("Geom name:")) {
            if (diskName != null) {
               diskMap.put(diskName, new Triplet(descr, ident, mediaSize));
               descr = "unknown";
               ident = "unknown";
               mediaSize = 0L;
            }

            diskName = line.substring(line.lastIndexOf(32) + 1);
         }

         if (diskName != null) {
            line = line.trim();
            if (line.startsWith("Mediasize:")) {
               String[] split = ParseUtil.whitespaces.split(line);
               if (split.length > 1) {
                  mediaSize = ParseUtil.parseLongOrDefault(split[1], 0L);
               }
            }

            if (line.startsWith("descr:")) {
               descr = line.replace("descr:", "").trim();
            }

            if (line.startsWith("ident:")) {
               ident = line.replace("ident:", "").replace("(null)", "").trim();
            }
         }
      }

      if (diskName != null) {
         diskMap.put(diskName, new Triplet(descr, ident, mediaSize));
      }

      return diskMap;
   }
}
