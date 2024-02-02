package oshi.hardware.platform.unix.aix;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.Xrandr;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@ThreadSafe
final class AixDisplay extends AbstractDisplay {
   AixDisplay(byte[] edid) {
      super(edid);
   }

   public static List<Display> getDisplays() {
      return Collections.unmodifiableList((List)Xrandr.getEdidArrays().stream().map(AixDisplay::new).collect(Collectors.toList()));
   }
}
