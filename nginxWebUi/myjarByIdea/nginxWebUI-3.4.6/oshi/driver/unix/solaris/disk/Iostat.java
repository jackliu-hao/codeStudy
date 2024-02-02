package oshi.driver.unix.solaris.disk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class Iostat {
   private static final String IOSTAT_ER_DETAIL = "iostat -Er";
   private static final String IOSTAT_ER = "iostat -er";
   private static final String IOSTAT_ERN = "iostat -ern";
   private static final String DEVICE_HEADER = "device";

   private Iostat() {
   }

   public static Map<String, String> queryPartitionToMountMap() {
      Map<String, String> deviceMap = new HashMap();
      List<String> mountNames = ExecutingCommand.runNative("iostat -er");
      List<String> mountPoints = ExecutingCommand.runNative("iostat -ern");

      for(int i = 0; i < mountNames.size() && i < mountPoints.size(); ++i) {
         String disk = (String)mountNames.get(i);
         String[] diskSplit = disk.split(",");
         if (diskSplit.length >= 5 && !"device".equals(diskSplit[0])) {
            String mount = (String)mountPoints.get(i);
            String[] mountSplit = mount.split(",");
            if (mountSplit.length >= 5 && !"device".equals(mountSplit[4])) {
               deviceMap.put(diskSplit[0], mountSplit[4]);
            }
         }
      }

      return deviceMap;
   }

   public static Map<String, Quintet<String, String, String, String, Long>> queryDeviceStrings(Set<String> diskSet) {
      Map<String, Quintet<String, String, String, String, Long>> deviceParamMap = new HashMap();
      List<String> iostat = ExecutingCommand.runNative("iostat -Er");
      String diskName = null;
      String model = "";
      String vendor = "";
      String product = "";
      String serial = "";
      long size = 0L;
      Iterator var10 = iostat.iterator();

      while(var10.hasNext()) {
         String line = (String)var10.next();
         String[] split = line.split(",");
         String[] var13 = split;
         int var14 = split.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            String keyValue = var13[var15];
            keyValue = keyValue.trim();
            if (diskSet.contains(keyValue)) {
               if (diskName != null) {
                  deviceParamMap.put(diskName, new Quintet(model, vendor, product, serial, size));
               }

               diskName = keyValue;
               model = "";
               vendor = "";
               product = "";
               serial = "";
               size = 0L;
            } else if (keyValue.startsWith("Model:")) {
               model = keyValue.replace("Model:", "").trim();
            } else if (keyValue.startsWith("Serial No:")) {
               serial = keyValue.replace("Serial No:", "").trim();
            } else if (keyValue.startsWith("Vendor:")) {
               vendor = keyValue.replace("Vendor:", "").trim();
            } else if (keyValue.startsWith("Product:")) {
               product = keyValue.replace("Product:", "").trim();
            } else if (keyValue.startsWith("Size:")) {
               String[] bytes = keyValue.split("<");
               if (bytes.length > 1) {
                  bytes = ParseUtil.whitespaces.split(bytes[1]);
                  size = ParseUtil.parseLongOrDefault(bytes[0], 0L);
               }
            }
         }

         if (diskName != null) {
            deviceParamMap.put(diskName, new Quintet(model, vendor, product, serial, size));
         }
      }

      return deviceParamMap;
   }
}
