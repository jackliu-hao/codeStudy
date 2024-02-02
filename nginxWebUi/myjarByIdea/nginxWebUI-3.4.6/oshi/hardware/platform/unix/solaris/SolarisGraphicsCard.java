package oshi.hardware.platform.unix.solaris;

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
final class SolarisGraphicsCard extends AbstractGraphicsCard {
   private static final String PCI_CLASS_DISPLAY = "0003";

   SolarisGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<GraphicsCard> getGraphicsCards() {
      List<GraphicsCard> cardList = new ArrayList();
      List<String> devices = ExecutingCommand.runNative("prtconf -pv");
      if (devices.isEmpty()) {
         return cardList;
      } else {
         String name = "";
         String vendorId = "";
         String productId = "";
         String classCode = "";
         List<String> versionInfoList = new ArrayList();
         Iterator var7 = devices.iterator();

         while(var7.hasNext()) {
            String line = (String)var7.next();
            if (line.contains("Node 0x")) {
               if ("0003".equals(classCode)) {
                  cardList.add(new SolarisGraphicsCard(name.isEmpty() ? "unknown" : name, productId.isEmpty() ? "unknown" : productId, vendorId.isEmpty() ? "unknown" : vendorId, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), 0L));
               }

               name = "";
               vendorId = "unknown";
               productId = "unknown";
               classCode = "";
               versionInfoList.clear();
            } else {
               String[] split = line.trim().split(":", 2);
               if (split.length == 2) {
                  if (split[0].equals("model")) {
                     name = ParseUtil.getSingleQuoteStringValue(line);
                  } else if (split[0].equals("name")) {
                     if (name.isEmpty()) {
                        name = ParseUtil.getSingleQuoteStringValue(line);
                     }
                  } else if (split[0].equals("vendor-id")) {
                     vendorId = "0x" + line.substring(line.length() - 4);
                  } else if (split[0].equals("device-id")) {
                     productId = "0x" + line.substring(line.length() - 4);
                  } else if (split[0].equals("revision-id")) {
                     versionInfoList.add(line.trim());
                  } else if (split[0].equals("class-code")) {
                     classCode = line.substring(line.length() - 8, line.length() - 4);
                  }
               }
            }
         }

         if ("0003".equals(classCode)) {
            cardList.add(new SolarisGraphicsCard(name.isEmpty() ? "unknown" : name, productId.isEmpty() ? "unknown" : productId, vendorId.isEmpty() ? "unknown" : vendorId, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), 0L));
         }

         return Collections.unmodifiableList(cardList);
      }
   }
}
