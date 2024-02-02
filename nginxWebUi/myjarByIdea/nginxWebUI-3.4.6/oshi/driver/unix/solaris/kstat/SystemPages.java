package oshi.driver.unix.solaris.kstat;

import com.sun.jna.platform.unix.solaris.LibKstat;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class SystemPages {
   private SystemPages() {
   }

   public static Pair<Long, Long> queryAvailableTotal() {
      long memAvailable = 0L;
      long memTotal = 0L;
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         LibKstat.Kstat ksp = kc.lookup((String)null, -1, "system_pages");
         if (ksp != null && kc.read(ksp)) {
            memAvailable = KstatUtil.dataLookupLong(ksp, "availrmem");
            memTotal = KstatUtil.dataLookupLong(ksp, "physmem");
         }
      } catch (Throwable var8) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (kc != null) {
         kc.close();
      }

      return new Pair(memAvailable, memTotal);
   }
}
