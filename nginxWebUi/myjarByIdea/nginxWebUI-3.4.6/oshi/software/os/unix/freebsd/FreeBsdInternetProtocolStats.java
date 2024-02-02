package oshi.software.os.unix.freebsd;

import com.sun.jna.Memory;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.NetStatTcp;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.os.InternetProtocolStats;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class FreeBsdInternetProtocolStats implements InternetProtocolStats {
   private Supplier<Pair<Long, Long>> establishedv4v6 = Memoizer.memoize(NetStatTcp::queryTcpnetstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdTcpstat> tcpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdUdpstat> udpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());

   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      CLibrary.BsdTcpstat tcp = (CLibrary.BsdTcpstat)this.tcpstat.get();
      return new InternetProtocolStats.TcpStats((Long)((Pair)this.establishedv4v6.get()).getA(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), ParseUtil.unsignedIntToLong(tcp.tcps_drops), ParseUtil.unsignedIntToLong(tcp.tcps_sndpack), ParseUtil.unsignedIntToLong(tcp.tcps_rcvpack), ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), ParseUtil.unsignedIntToLong(tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort), 0L);
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      return new InternetProtocolStats.TcpStats((Long)((Pair)this.establishedv4v6.get()).getB(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
   }

   public InternetProtocolStats.UdpStats getUDPv4Stats() {
      CLibrary.BsdUdpstat stat = (CLibrary.BsdUdpstat)this.udpstat.get();
      return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_opackets), ParseUtil.unsignedIntToLong(stat.udps_ipackets), ParseUtil.unsignedIntToLong(stat.udps_noportmcast), ParseUtil.unsignedIntToLong(stat.udps_hdrops + stat.udps_badsum + stat.udps_badlen));
   }

   public InternetProtocolStats.UdpStats getUDPv6Stats() {
      CLibrary.BsdUdpstat stat = (CLibrary.BsdUdpstat)this.udpstat.get();
      return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(stat.udps_snd6_swcsum), ParseUtil.unsignedIntToLong(stat.udps_rcv6_swcsum), 0L, 0L);
   }

   private static CLibrary.BsdTcpstat queryTcpstat() {
      CLibrary.BsdTcpstat ft = new CLibrary.BsdTcpstat();
      Memory m = SysctlUtil.sysctl("net.inet.tcp.stats");
      if (m != null && m.size() >= 128L) {
         ft.tcps_connattempt = m.getInt(0L);
         ft.tcps_accepts = m.getInt(4L);
         ft.tcps_drops = m.getInt(12L);
         ft.tcps_conndrops = m.getInt(16L);
         ft.tcps_sndpack = m.getInt(64L);
         ft.tcps_sndrexmitpack = m.getInt(72L);
         ft.tcps_rcvpack = m.getInt(104L);
         ft.tcps_rcvbadsum = m.getInt(112L);
         ft.tcps_rcvbadoff = m.getInt(116L);
         ft.tcps_rcvmemdrop = m.getInt(120L);
         ft.tcps_rcvshort = m.getInt(124L);
      }

      return ft;
   }

   private static CLibrary.BsdUdpstat queryUdpstat() {
      CLibrary.BsdUdpstat ut = new CLibrary.BsdUdpstat();
      Memory m = SysctlUtil.sysctl("net.inet.udp.stats");
      if (m != null && m.size() >= 1644L) {
         ut.udps_ipackets = m.getInt(0L);
         ut.udps_hdrops = m.getInt(4L);
         ut.udps_badsum = m.getInt(8L);
         ut.udps_badlen = m.getInt(12L);
         ut.udps_opackets = m.getInt(36L);
         ut.udps_noportmcast = m.getInt(48L);
         ut.udps_rcv6_swcsum = m.getInt(64L);
         ut.udps_snd6_swcsum = m.getInt(80L);
      }

      return ut;
   }
}
