package oshi.driver.unix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Xrandr {
   private Xrandr() {
   }

   public static List<byte[]> getEdidArrays() {
      List<String> xrandr = ExecutingCommand.runNative("xrandr --verbose");
      if (xrandr.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<byte[]> displays = new ArrayList();
         StringBuilder sb = null;
         Iterator var3 = xrandr.iterator();

         while(var3.hasNext()) {
            String s = (String)var3.next();
            if (s.contains("EDID")) {
               sb = new StringBuilder();
            } else if (sb != null) {
               sb.append(s.trim());
               if (sb.length() >= 256) {
                  String edidStr = sb.toString();
                  byte[] edid = ParseUtil.hexStringToByteArray(edidStr);
                  if (edid.length >= 128) {
                     displays.add(edid);
                  }

                  sb = null;
               }
            }
         }

         return displays;
      }
   }
}
