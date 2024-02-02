package oshi.hardware.platform.unix.freebsd;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.Xrandr;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@ThreadSafe
final class FreeBsdDisplay extends AbstractDisplay {
   FreeBsdDisplay(byte[] edid) {
      super(edid);
   }

   public static List<Display> getDisplays() {
      return Collections.unmodifiableList((List)Xrandr.getEdidArrays().stream().map(FreeBsdDisplay::new).collect(Collectors.toList()));
   }
}
