package oshi.software.os.windows;

import com.sun.jna.platform.win32.IPHlpAPI;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.InternetProtocolStats;

@ThreadSafe
public class WindowsInternetProtocolStats implements InternetProtocolStats {
   private static final IPHlpAPI IPHLP;

   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
      IPHLP.GetTcpStatisticsEx(stats, 2);
      return new InternetProtocolStats.TcpStats((long)stats.dwCurrEstab, (long)stats.dwActiveOpens, (long)stats.dwPassiveOpens, (long)stats.dwAttemptFails, (long)stats.dwEstabResets, (long)stats.dwOutSegs, (long)stats.dwInSegs, (long)stats.dwRetransSegs, (long)stats.dwInErrs, (long)stats.dwOutRsts);
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
      IPHLP.GetTcpStatisticsEx(stats, 23);
      return new InternetProtocolStats.TcpStats((long)stats.dwCurrEstab, (long)stats.dwActiveOpens, (long)stats.dwPassiveOpens, (long)stats.dwAttemptFails, (long)stats.dwEstabResets, (long)stats.dwOutSegs, (long)stats.dwInSegs, (long)stats.dwRetransSegs, (long)stats.dwInErrs, (long)stats.dwOutRsts);
   }

   public InternetProtocolStats.UdpStats getUDPv4Stats() {
      IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
      IPHLP.GetUdpStatisticsEx(stats, 2);
      return new InternetProtocolStats.UdpStats((long)stats.dwOutDatagrams, (long)stats.dwInDatagrams, (long)stats.dwNoPorts, (long)stats.dwInErrors);
   }

   public InternetProtocolStats.UdpStats getUDPv6Stats() {
      IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
      IPHLP.GetUdpStatisticsEx(stats, 23);
      return new InternetProtocolStats.UdpStats((long)stats.dwOutDatagrams, (long)stats.dwInDatagrams, (long)stats.dwNoPorts, (long)stats.dwInErrors);
   }

   static {
      IPHLP = IPHlpAPI.INSTANCE;
   }
}
