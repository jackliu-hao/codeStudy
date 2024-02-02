package oshi.driver.linux.proc;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Quartet;

@ThreadSafe
public final class CpuInfo {
   private CpuInfo() {
   }

   public static String queryCpuManufacturer() {
      List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
      Iterator var1 = cpuInfo.iterator();

      String line;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         line = (String)var1.next();
      } while(!line.startsWith("CPU implementer"));

      int part = ParseUtil.parseLastInt(line, 0);
      switch (part) {
         case 65:
            return "ARM";
         case 66:
            return "Broadcom";
         case 67:
            return "Cavium";
         case 68:
            return "DEC";
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 84:
         case 85:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 103:
         case 104:
         default:
            return null;
         case 78:
            return "Nvidia";
         case 80:
            return "APM";
         case 81:
            return "Qualcomm";
         case 83:
            return "Samsung";
         case 86:
            return "Marvell";
         case 102:
            return "Faraday";
         case 105:
            return "Intel";
      }
   }

   public static Quartet<String, String, String, String> queryBoardInfo() {
      String pcManufacturer = null;
      String pcModel = null;
      String pcVersion = null;
      String pcSerialNumber = null;
      List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
      Iterator var5 = cpuInfo.iterator();

      while(var5.hasNext()) {
         String line = (String)var5.next();
         String[] splitLine = ParseUtil.whitespacesColonWhitespace.split(line);
         if (splitLine.length >= 2) {
            switch (splitLine[0]) {
               case "Hardware":
                  pcModel = splitLine[1];
                  break;
               case "Revision":
                  pcVersion = splitLine[1];
                  if (pcVersion.length() > 1) {
                     pcManufacturer = queryBoardManufacturer(pcVersion.charAt(1));
                  }
                  break;
               case "Serial":
                  pcSerialNumber = splitLine[1];
            }
         }
      }

      return new Quartet(pcManufacturer, pcModel, pcVersion, pcSerialNumber);
   }

   private static String queryBoardManufacturer(char digit) {
      switch (digit) {
         case '0':
            return "Sony UK";
         case '1':
            return "Egoman";
         case '2':
            return "Embest";
         case '3':
            return "Sony Japan";
         case '4':
            return "Embest";
         case '5':
            return "Stadium";
         default:
            return "unknown";
      }
   }
}
