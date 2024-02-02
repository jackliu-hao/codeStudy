package oshi.hardware.platform.unix.aix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
final class AixGraphicsCard extends AbstractGraphicsCard {
   AixGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<GraphicsCard> getGraphicsCards(Supplier<List<String>> lscfg) {
      List<GraphicsCard> cardList = new ArrayList();
      boolean display = false;
      String name = null;
      String vendor = null;
      List<String> versionInfo = new ArrayList();
      Iterator var6 = ((List)lscfg.get()).iterator();

      while(true) {
         while(var6.hasNext()) {
            String line = (String)var6.next();
            String s = line.trim();
            if (s.startsWith("Name:") && s.contains("display")) {
               display = true;
            } else if (display && s.toLowerCase().contains("graphics")) {
               name = s;
            } else if (display && name != null) {
               if (s.startsWith("Manufacture ID")) {
                  vendor = ParseUtil.removeLeadingDots(s.substring(14));
               } else if (s.contains("Level")) {
                  versionInfo.add(s.replaceAll("\\.\\.+", "="));
               } else if (s.startsWith("Hardware Location Code")) {
                  cardList.add(new AixGraphicsCard(name, "unknown", Util.isBlank(vendor) ? "unknown" : vendor, versionInfo.isEmpty() ? "unknown" : String.join(",", versionInfo), 0L));
                  display = false;
               }
            }
         }

         return Collections.unmodifiableList(cardList);
      }
   }
}
