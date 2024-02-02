package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class MacGraphicsCard extends AbstractGraphicsCard {
   MacGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<GraphicsCard> getGraphicsCards() {
      List<MacGraphicsCard> cardList = new ArrayList();
      List<String> sp = ExecutingCommand.runNative("system_profiler SPDisplaysDataType");
      String name = "unknown";
      String deviceId = "unknown";
      String vendor = "unknown";
      List<String> versionInfoList = new ArrayList();
      long vram = 0L;
      int cardNum = 0;
      Iterator var9 = sp.iterator();

      while(true) {
         while(true) {
            String line;
            String[] split;
            do {
               if (!var9.hasNext()) {
                  cardList.add(new MacGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), vram));
                  return Collections.unmodifiableList(cardList);
               }

               line = (String)var9.next();
               split = line.trim().split(":", 2);
            } while(split.length != 2);

            String prefix = split[0].toLowerCase();
            if (prefix.equals("chipset model")) {
               if (cardNum++ > 0) {
                  cardList.add(new MacGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), vram));
                  versionInfoList.clear();
               }

               name = split[1].trim();
            } else if (prefix.equals("device id")) {
               deviceId = split[1].trim();
            } else if (prefix.equals("vendor")) {
               vendor = split[1].trim();
            } else if (!prefix.contains("version") && !prefix.contains("revision")) {
               if (prefix.startsWith("vram")) {
                  vram = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
               }
            } else {
               versionInfoList.add(line.trim());
            }
         }
      }
   }
}
