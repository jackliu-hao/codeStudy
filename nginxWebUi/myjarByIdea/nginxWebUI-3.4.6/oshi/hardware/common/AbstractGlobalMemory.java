package oshi.hardware.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractGlobalMemory implements GlobalMemory {
   public List<PhysicalMemory> getPhysicalMemory() {
      List<PhysicalMemory> pmList = new ArrayList();
      List<String> dmi = ExecutingCommand.runNative("dmidecode --type 17");
      int bank = 0;
      String bankLabel = "unknown";
      String locator = "";
      long capacity = 0L;
      long speed = 0L;
      String manufacturer = "unknown";
      String memoryType = "unknown";
      Iterator var12 = dmi.iterator();

      while(var12.hasNext()) {
         String line = (String)var12.next();
         if (line.trim().contains("DMI type 17")) {
            if (bank++ > 0) {
               if (capacity > 0L) {
                  pmList.add(new PhysicalMemory(bankLabel + locator, capacity, speed, manufacturer, memoryType));
               }

               bankLabel = "unknown";
               locator = "";
               capacity = 0L;
               speed = 0L;
            }
         } else if (bank > 0) {
            String[] split = line.trim().split(":");
            if (split.length == 2) {
               switch (split[0]) {
                  case "Bank Locator":
                     bankLabel = split[1].trim();
                     break;
                  case "Locator":
                     locator = "/" + split[1].trim();
                     break;
                  case "Size":
                     capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
                     break;
                  case "Type":
                     memoryType = split[1].trim();
                     break;
                  case "Speed":
                     speed = ParseUtil.parseHertz(split[1]);
                     break;
                  case "Manufacturer":
                     manufacturer = split[1].trim();
               }
            }
         }
      }

      if (capacity > 0L) {
         pmList.add(new PhysicalMemory(bankLabel + locator, capacity, speed, manufacturer, memoryType));
      }

      return pmList;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Available: ");
      sb.append(FormatUtil.formatBytes(this.getAvailable()));
      sb.append("/");
      sb.append(FormatUtil.formatBytes(this.getTotal()));
      return sb.toString();
   }
}
