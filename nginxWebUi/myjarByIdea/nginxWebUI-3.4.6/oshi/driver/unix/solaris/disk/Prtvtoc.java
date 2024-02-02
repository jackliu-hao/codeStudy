package oshi.driver.unix.solaris.disk;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;

@ThreadSafe
public final class Prtvtoc {
   private static final String PRTVTOC_DEV_DSK = "prtvtoc /dev/dsk/";

   private Prtvtoc() {
   }

   public static List<HWPartition> queryPartitions(String var0, int var1) {
      // $FF: Couldn't be decompiled
   }
}
