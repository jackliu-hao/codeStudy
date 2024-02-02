package oshi.hardware.platform.unix.solaris;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.Xrandr;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@ThreadSafe
final class SolarisDisplay extends AbstractDisplay {
   SolarisDisplay(byte[] edid) {
      super(edid);
   }

   public static List<Display> getDisplays() {
      return Collections.unmodifiableList((List)Xrandr.getEdidArrays().stream().map(SolarisDisplay::new).collect(Collectors.toList()));
   }
}
