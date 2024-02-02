package oshi.software.os.mac;

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
public class MacInternetProtocolStats implements InternetProtocolStats {
   private boolean isElevated;
   private Supplier<Pair<Long, Long>> establishedv4v6 = Memoizer.memoize(NetStatTcp::queryTcpnetstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdTcpstat> tcpstat = Memoizer.memoize(MacInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdUdpstat> udpstat = Memoizer.memoize(MacInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdIpstat> ipstat = Memoizer.memoize(MacInternetProtocolStats::queryIpstat, Memoizer.defaultExpiration());
   private Supplier<CLibrary.BsdIp6stat> ip6stat = Memoizer.memoize(MacInternetProtocolStats::queryIp6stat, Memoizer.defaultExpiration());

   public MacInternetProtocolStats(boolean elevated) {
      this.isElevated = elevated;
   }

   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      CLibrary.BsdTcpstat tcp = (CLibrary.BsdTcpstat)this.tcpstat.get();
      if (this.isElevated) {
         return new InternetProtocolStats.TcpStats((Long)((Pair)this.establishedv4v6.get()).getA(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), ParseUtil.unsignedIntToLong(tcp.tcps_drops), ParseUtil.unsignedIntToLong(tcp.tcps_sndpack), ParseUtil.unsignedIntToLong(tcp.tcps_rcvpack), ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), ParseUtil.unsignedIntToLong(tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort), 0L);
      } else {
         CLibrary.BsdIpstat ip = (CLibrary.BsdIpstat)this.ipstat.get();
         CLibrary.BsdUdpstat udp = (CLibrary.BsdUdpstat)this.udpstat.get();
         return new InternetProtocolStats.TcpStats((Long)((Pair)this.establishedv4v6.get()).getA(), ParseUtil.unsignedIntToLong(tcp.tcps_connattempt), ParseUtil.unsignedIntToLong(tcp.tcps_accepts), ParseUtil.unsignedIntToLong(tcp.tcps_conndrops), ParseUtil.unsignedIntToLong(tcp.tcps_drops), Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_delivered - udp.udps_opackets)), Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_total - udp.udps_ipackets)), ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), Math.max(0L, ParseUtil.unsignedIntToLong(ip.ips_badsum + ip.ips_tooshort + ip.ips_toosmall + ip.ips_badhlen + ip.ips_badlen - udp.udps_hdrops + udp.udps_badsum + udp.udps_badlen)), 0L);
      }
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      CLibrary.BsdIp6stat ip6 = (CLibrary.BsdIp6stat)this.ip6stat.get();
      CLibrary.BsdUdpstat udp = (CLibrary.BsdUdpstat)this.udpstat.get();
      return new InternetProtocolStats.TcpStats((Long)((Pair)this.establishedv4v6.get()).getB(), 0L, 0L, 0L, 0L, ip6.ip6s_localout - ParseUtil.unsignedIntToLong(udp.udps_snd6_swcsum), ip6.ip6s_total - ParseUtil.unsignedIntToLong(udp.udps_rcv6_swcsum), 0L, 0L, 0L);
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
      CLibrary.BsdTcpstat mt = new CLibrary.BsdTcpstat();
      Memory m = SysctlUtil.sysctl("net.inet.tcp.stats");
      if (m != null && m.size() >= 128L) {
         mt.tcps_connattempt = m.getInt(0L);
         mt.tcps_accepts = m.getInt(4L);
         mt.tcps_drops = m.getInt(12L);
         mt.tcps_conndrops = m.getInt(16L);
         mt.tcps_sndpack = m.getInt(64L);
         mt.tcps_sndrexmitpack = m.getInt(72L);
         mt.tcps_rcvpack = m.getInt(104L);
         mt.tcps_rcvbadsum = m.getInt(112L);
         mt.tcps_rcvbadoff = m.getInt(116L);
         mt.tcps_rcvmemdrop = m.getInt(120L);
         mt.tcps_rcvshort = m.getInt(124L);
      }

      return mt;
   }

   private static CLibrary.BsdIpstat queryIpstat() {
      CLibrary.BsdIpstat mi = new CLibrary.BsdIpstat();
      Memory m = SysctlUtil.sysctl("net.inet.ip.stats");
      if (m != null && m.size() >= 60L) {
         mi.ips_total = m.getInt(0L);
         mi.ips_badsum = m.getInt(4L);
         mi.ips_tooshort = m.getInt(8L);
         mi.ips_toosmall = m.getInt(12L);
         mi.ips_badhlen = m.getInt(16L);
         mi.ips_badlen = m.getInt(20L);
         mi.ips_delivered = m.getInt(56L);
      }

      return mi;
   }

   private static CLibrary.BsdIp6stat queryIp6stat() {
      CLibrary.BsdIp6stat mi6 = new CLibrary.BsdIp6stat();
      Memory m = SysctlUtil.sysctl("net.inet6.ip6.stats");
      if (m != null && m.size() >= 96L) {
         mi6.ip6s_total = m.getLong(0L);
         mi6.ip6s_localout = m.getLong(88L);
      }

      return mi6;
   }

   public static CLibrary.BsdUdpstat queryUdpstat() {
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
