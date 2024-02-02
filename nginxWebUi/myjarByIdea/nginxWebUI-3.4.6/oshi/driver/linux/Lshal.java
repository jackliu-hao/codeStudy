package oshi.driver.linux;

import java.util.Iterator;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Lshal {
   private Lshal() {
   }

   public static String querySerialNumber() {
      String marker = "system.hardware.serial =";
      Iterator var1 = ExecutingCommand.runNative("lshal").iterator();

      String checkLine;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         checkLine = (String)var1.next();
      } while(!checkLine.contains(marker));

      return ParseUtil.getSingleQuoteStringValue(checkLine);
   }
}
