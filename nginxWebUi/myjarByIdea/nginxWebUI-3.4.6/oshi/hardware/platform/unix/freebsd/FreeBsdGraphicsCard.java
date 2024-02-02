package oshi.hardware.platform.unix.freebsd;

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
final class FreeBsdGraphicsCard extends AbstractGraphicsCard {
   private static final String PCI_CLASS_DISPLAY = "0x03";

   FreeBsdGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<GraphicsCard> getGraphicsCards() {
      List<FreeBsdGraphicsCard> cardList = new ArrayList();
      List<String> devices = ExecutingCommand.runNative("pciconf -lv");
      if (devices.isEmpty()) {
         return Collections.emptyList();
      } else {
         String name = "unknown";
         String vendorId = "unknown";
         String productId = "unknown";
         String classCode = "";
         String versionInfo = "unknown";
         Iterator var7 = devices.iterator();

         while(true) {
            while(var7.hasNext()) {
               String line = (String)var7.next();
               String[] split;
               if (line.contains("class=0x")) {
                  if ("0x03".equals(classCode)) {
                     cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? "unknown" : name, productId.isEmpty() ? "unknown" : productId, vendorId.isEmpty() ? "unknown" : vendorId, versionInfo.isEmpty() ? "unknown" : versionInfo, 0L));
                  }

                  split = ParseUtil.whitespaces.split(line);
                  String[] var15 = split;
                  int var11 = split.length;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     String s = var15[var12];
                     String[] keyVal = s.split("=");
                     if (keyVal.length > 1) {
                        if (keyVal[0].equals("class") && keyVal[1].length() >= 4) {
                           classCode = keyVal[1].substring(0, 4);
                        } else if (keyVal[0].equals("chip") && keyVal[1].length() >= 10) {
                           productId = keyVal[1].substring(0, 6);
                           vendorId = "0x" + keyVal[1].substring(6, 10);
                        } else if (keyVal[0].contains("rev")) {
                           versionInfo = s;
                        }
                     }
                  }

                  name = "unknown";
               } else {
                  split = line.trim().split("=", 2);
                  if (split.length == 2) {
                     String key = split[0].trim();
                     if (key.equals("vendor")) {
                        vendorId = ParseUtil.getSingleQuoteStringValue(line) + (vendorId.equals("unknown") ? "" : " (" + vendorId + ")");
                     } else if (key.equals("device")) {
                        name = ParseUtil.getSingleQuoteStringValue(line);
                     }
                  }
               }
            }

            if ("0x03".equals(classCode)) {
               cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? "unknown" : name, productId.isEmpty() ? "unknown" : productId, vendorId.isEmpty() ? "unknown" : vendorId, versionInfo.isEmpty() ? "unknown" : versionInfo, 0L));
            }

            return Collections.unmodifiableList(cardList);
         }
      }
   }
}
