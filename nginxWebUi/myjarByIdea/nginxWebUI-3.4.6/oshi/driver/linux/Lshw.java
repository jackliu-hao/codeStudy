package oshi.driver.linux;

import java.util.Iterator;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Lshw {
   private Lshw() {
   }

   public static String queryModel() {
      String modelMarker = "product:";
      Iterator var1 = ExecutingCommand.runNative("lshw -C system").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(modelMarker));

      return checkLine.split(modelMarker)[1].trim();
   }

   public static String querySerialNumber() {
      String serialMarker = "serial:";
      Iterator var1 = ExecutingCommand.runNative("lshw -C system").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(serialMarker));

      return checkLine.split(serialMarker)[1].trim();
   }

   public static long queryCpuCapacity() {
      String capacityMarker = "capacity:";
      Iterator var1 = ExecutingCommand.runNative("lshw -class processor").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return -1L;
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(capacityMarker));

      return ParseUtil.parseHertz(checkLine.split(capacityMarker)[1].trim());
   }
}
