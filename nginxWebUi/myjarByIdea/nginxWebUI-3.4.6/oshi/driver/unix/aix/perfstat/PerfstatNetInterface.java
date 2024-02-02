package oshi.driver.unix.aix.perfstat;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatNetInterface {
   private static final Perfstat PERF;

   private PerfstatNetInterface() {
   }

   public static Perfstat.perfstat_netinterface_t[] queryNetInterfaces() {
      Perfstat.perfstat_netinterface_t netinterface = new Perfstat.perfstat_netinterface_t();
      int total = PERF.perfstat_netinterface((Perfstat.perfstat_id_t)null, (Perfstat.perfstat_netinterface_t[])null, netinterface.size(), 0);
      if (total > 0) {
         Perfstat.perfstat_netinterface_t[] statp = (Perfstat.perfstat_netinterface_t[])netinterface.toArray(total);
         Perfstat.perfstat_id_t firstnetinterface = new Perfstat.perfstat_id_t();
         int ret = PERF.perfstat_netinterface(firstnetinterface, statp, netinterface.size(), total);
         if (ret > 0) {
            return statp;
         }
      }

      return new Perfstat.perfstat_netinterface_t[0];
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
