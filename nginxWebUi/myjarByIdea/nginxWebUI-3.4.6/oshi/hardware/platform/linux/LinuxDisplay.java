package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class LinuxDisplay extends AbstractDisplay {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxDisplay.class);

   LinuxDisplay(byte[] edid) {
      super(edid);
      LOG.debug("Initialized LinuxDisplay");
   }

   public static List<Display> getDisplays() {
      List<String> xrandr = ExecutingCommand.runNative("xrandr --verbose");
      if (xrandr.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Display> displays = new ArrayList();
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
                  LOG.debug((String)"Parsed EDID: {}", (Object)edidStr);
                  byte[] edid = ParseUtil.hexStringToByteArray(edidStr);
                  if (edid.length >= 128) {
                     displays.add(new LinuxDisplay(edid));
                  }

                  sb = null;
               }
            }
         }

         return Collections.unmodifiableList(displays);
      }
   }
}
