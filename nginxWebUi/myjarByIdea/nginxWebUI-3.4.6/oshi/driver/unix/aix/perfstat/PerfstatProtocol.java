package oshi.driver.unix.aix.perfstat;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatProtocol {
   private static final Perfstat PERF;

   private PerfstatProtocol() {
   }

   public static Perfstat.perfstat_protocol_t[] queryProtocols() {
      Perfstat.perfstat_protocol_t protocol = new Perfstat.perfstat_protocol_t();
      int total = PERF.perfstat_protocol((Perfstat.perfstat_id_t)null, (Perfstat.perfstat_protocol_t[])null, protocol.size(), 0);
      if (total > 0) {
         Perfstat.perfstat_protocol_t[] statp = (Perfstat.perfstat_protocol_t[])protocol.toArray(total);
         Perfstat.perfstat_id_t firstprotocol = new Perfstat.perfstat_id_t();
         int ret = PERF.perfstat_protocol(firstprotocol, statp, protocol.size(), total);
         if (ret > 0) {
            return statp;
         }
      }

      return new Perfstat.perfstat_protocol_t[0];
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
