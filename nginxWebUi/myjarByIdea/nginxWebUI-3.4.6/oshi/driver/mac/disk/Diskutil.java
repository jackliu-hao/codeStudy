package oshi.driver.mac.disk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Diskutil {
   private static final String DISKUTIL_CS_LIST = "diskutil cs list";
   private static final String LOGICAL_VOLUME_FAMILY = "Logical Volume Family";
   private static final String LOGICAL_VOLUME_GROUP = "Logical Volume Group";

   private Diskutil() {
   }

   public static Map<String, String> queryLogicalVolumeMap() {
      Map<String, String> logicalVolumeMap = new HashMap();
      Set<String> physicalVolumes = new HashSet();
      boolean logicalVolume = false;
      Iterator var3 = ExecutingCommand.runNative("diskutil cs list").iterator();

      while(true) {
         while(var3.hasNext()) {
            String line = (String)var3.next();
            if (line.contains("Logical Volume Group")) {
               physicalVolumes.clear();
               logicalVolume = false;
            } else if (line.contains("Logical Volume Family")) {
               logicalVolume = true;
            } else if (line.contains("Disk:")) {
               String volume = ParseUtil.parseLastString(line);
               if (!logicalVolume) {
                  physicalVolumes.add(ParseUtil.parseLastString(line));
               } else {
                  Iterator var6 = physicalVolumes.iterator();

                  while(var6.hasNext()) {
                     String pv = (String)var6.next();
                     logicalVolumeMap.put(pv, volume);
                  }

                  physicalVolumes.clear();
               }
            }
         }

         return logicalVolumeMap;
      }
   }
}
