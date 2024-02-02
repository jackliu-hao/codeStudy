package oshi.driver.linux;

import java.util.Iterator;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Dmidecode {
   private Dmidecode() {
   }

   public static String querySerialNumber() {
      String marker = "Serial Number:";
      Iterator var1 = ExecutingCommand.runNative("dmidecode -t system").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(marker));

      return checkLine.split(marker)[1].trim();
   }

   public static Pair<String, String> queryBiosNameRev() {
      String biosName = null;
      String revision = null;
      String biosMarker = "SMBIOS";
      String revMarker = "Bios Revision:";
      Iterator var4 = ExecutingCommand.runNative("dmidecode -t bios").iterator();

      while(var4.hasNext()) {
         String checkLine = (String)var4.next();
         if (checkLine.contains("SMBIOS")) {
            String[] biosArr = ParseUtil.whitespaces.split(checkLine);
            if (biosArr.length >= 2) {
               biosName = biosArr[0] + " " + biosArr[1];
            }
         }

         if (checkLine.contains("Bios Revision:")) {
            revision = checkLine.split("Bios Revision:")[1].trim();
            break;
         }
      }

      return new Pair(biosName, revision);
   }
}
