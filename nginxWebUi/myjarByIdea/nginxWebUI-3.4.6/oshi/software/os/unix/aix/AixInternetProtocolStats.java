package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.perfstat.PerfstatProtocol;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.software.os.InternetProtocolStats;
import oshi.util.Memoizer;

@ThreadSafe
public class AixInternetProtocolStats implements InternetProtocolStats {
   private Supplier<Perfstat.perfstat_protocol_t[]> ipstats = Memoizer.memoize(PerfstatProtocol::queryProtocols, Memoizer.defaultExpiration());

   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      Perfstat.perfstat_protocol_t[] var1 = (Perfstat.perfstat_protocol_t[])this.ipstats.get();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Perfstat.perfstat_protocol_t stat = var1[var3];
         if ("tcp".equals(Native.toString(stat.name))) {
            return new InternetProtocolStats.TcpStats(stat.u.tcp.established, stat.u.tcp.initiated, stat.u.tcp.accepted, stat.u.tcp.dropped, stat.u.tcp.dropped, stat.u.tcp.opackets, stat.u.tcp.ipackets, 0L, stat.u.tcp.ierrors, 0L);
         }
      }

      return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
   }

   public InternetProtocolStats.UdpStats getUDPv4Stats() {
      Perfstat.perfstat_protocol_t[] var1 = (Perfstat.perfstat_protocol_t[])this.ipstats.get();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Perfstat.perfstat_protocol_t stat = var1[var3];
         if ("udp".equals(Native.toString(stat.name))) {
            return new InternetProtocolStats.UdpStats(stat.u.udp.opackets, stat.u.udp.ipackets, stat.u.udp.no_socket, stat.u.udp.ierrors);
         }
      }

      return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
   }

   public InternetProtocolStats.UdpStats getUDPv6Stats() {
      return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
   }
}
