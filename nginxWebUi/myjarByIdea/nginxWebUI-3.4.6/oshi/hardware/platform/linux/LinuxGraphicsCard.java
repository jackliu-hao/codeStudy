package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@Immutable
final class LinuxGraphicsCard extends AbstractGraphicsCard {
   LinuxGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
      super(name, deviceId, vendor, versionInfo, vram);
   }

   public static List<GraphicsCard> getGraphicsCards() {
      List<LinuxGraphicsCard> cardList = getGraphicsCardsFromLspci();
      if (cardList.isEmpty()) {
         cardList = getGraphicsCardsFromLshw();
      }

      return Collections.unmodifiableList(cardList);
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLspci() {
      List<LinuxGraphicsCard> cardList = new ArrayList();
      List<String> lspci = ExecutingCommand.runNative("lspci -vnnm");
      String name = "unknown";
      String deviceId = "unknown";
      String vendor = "unknown";
      List<String> versionInfoList = new ArrayList();
      boolean found = false;
      String lookupDevice = null;
      Iterator var8 = lspci.iterator();

      while(var8.hasNext()) {
         String line = (String)var8.next();
         String[] split = line.trim().split(":", 2);
         String prefix = split[0];
         if (prefix.equals("Class") && line.contains("VGA")) {
            found = true;
         } else if (prefix.equals("Device") && !found && split.length > 1) {
            lookupDevice = split[1].trim();
         }

         if (found) {
            if (split.length < 2) {
               cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), queryLspciMemorySize(lookupDevice)));
               versionInfoList.clear();
               found = false;
            } else {
               Pair pair;
               if (prefix.equals("Device")) {
                  pair = ParseUtil.parseLspciMachineReadable(split[1].trim());
                  if (pair != null) {
                     name = (String)pair.getA();
                     deviceId = "0x" + (String)pair.getB();
                  }
               } else if (prefix.equals("Vendor")) {
                  pair = ParseUtil.parseLspciMachineReadable(split[1].trim());
                  if (pair != null) {
                     vendor = (String)pair.getA() + " (0x" + (String)pair.getB() + ")";
                  } else {
                     vendor = split[1].trim();
                  }
               } else if (prefix.equals("Rev:")) {
                  versionInfoList.add(line.trim());
               }
            }
         }
      }

      if (found) {
         cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), queryLspciMemorySize(lookupDevice)));
      }

      return cardList;
   }

   private static long queryLspciMemorySize(String lookupDevice) {
      long vram = 0L;
      List<String> lspciMem = ExecutingCommand.runNative("lspci -v -s " + lookupDevice);
      Iterator var4 = lspciMem.iterator();

      while(var4.hasNext()) {
         String mem = (String)var4.next();
         if (mem.contains(" prefetchable")) {
            vram += ParseUtil.parseLspciMemorySize(mem);
         }
      }

      return vram;
   }

   private static List<LinuxGraphicsCard> getGraphicsCardsFromLshw() {
      List<LinuxGraphicsCard> cardList = new ArrayList();
      List<String> lshw = ExecutingCommand.runNative("lshw -C display");
      String name = "unknown";
      String deviceId = "unknown";
      String vendor = "unknown";
      List<String> versionInfoList = new ArrayList();
      long vram = 0L;
      int cardNum = 0;
      Iterator var9 = lshw.iterator();

      while(var9.hasNext()) {
         String line = (String)var9.next();
         String[] split = line.trim().split(":");
         if (split[0].startsWith("*-display")) {
            if (cardNum++ > 0) {
               cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), vram));
               versionInfoList.clear();
            }
         } else if (split.length == 2) {
            String prefix = split[0];
            if (prefix.equals("product")) {
               name = split[1].trim();
            } else if (prefix.equals("vendor")) {
               vendor = split[1].trim();
            } else if (prefix.equals("version")) {
               versionInfoList.add(line.trim());
            } else if (prefix.startsWith("resources")) {
               vram = ParseUtil.parseLshwResourceString(split[1].trim());
            }
         }
      }

      cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), vram));
      return cardList;
   }
}
