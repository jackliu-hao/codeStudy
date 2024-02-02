package oshi.software.os.unix.solaris;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class SolarisInternetProtocolStats implements InternetProtocolStats {
   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      return getTcpStats();
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
   }

   public InternetProtocolStats.UdpStats getUDPv4Stats() {
      return getUdpStats();
   }

   public InternetProtocolStats.UdpStats getUDPv6Stats() {
      return new InternetProtocolStats.UdpStats(0L, 0L, 0L, 0L);
   }

   private static InternetProtocolStats.TcpStats getTcpStats() {
      long connectionsEstablished = 0L;
      long connectionsActive = 0L;
      long connectionsPassive = 0L;
      long connectionFailures = 0L;
      long connectionsReset = 0L;
      long segmentsSent = 0L;
      long segmentsReceived = 0L;
      long segmentsRetransmitted = 0L;
      long inErrors = 0L;
      long outResets = 0L;
      List<String> netstat = ExecutingCommand.runNative("netstat -s -P tcp");
      netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
      Iterator var21 = netstat.iterator();

      while(var21.hasNext()) {
         String s = (String)var21.next();
         String[] stats = splitOnPrefix(s, "tcp");
         String[] var24 = stats;
         int var25 = stats.length;

         for(int var26 = 0; var26 < var25; ++var26) {
            String stat = var24[var26];
            if (stat != null) {
               String[] split = stat.split("=");
               if (split.length == 2) {
                  switch (split[0].trim()) {
                     case "tcpCurrEstab":
                        connectionsEstablished = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpActiveOpens":
                        connectionsActive = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpPassiveOpens":
                        connectionsPassive = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpAttemptFails":
                        connectionFailures = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpEstabResets":
                        connectionsReset = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpOutSegs":
                        segmentsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpInSegs":
                        segmentsReceived = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpRetransSegs":
                        segmentsRetransmitted = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "tcpInErr":
                        inErrors = (long)ParseUtil.getFirstIntValue(split[1].trim());
                        break;
                     case "tcpOutRsts":
                        outResets = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                  }
               }
            }
         }
      }

      return new InternetProtocolStats.TcpStats(connectionsEstablished, connectionsActive, connectionsPassive, connectionFailures, connectionsReset, segmentsSent, segmentsReceived, segmentsRetransmitted, inErrors, outResets);
   }

   private static InternetProtocolStats.UdpStats getUdpStats() {
      long datagramsSent = 0L;
      long datagramsReceived = 0L;
      long datagramsNoPort = 0L;
      long datagramsReceivedErrors = 0L;
      List<String> netstat = ExecutingCommand.runNative("netstat -s -P udp");
      netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
      Iterator var9 = netstat.iterator();

      while(var9.hasNext()) {
         String s = (String)var9.next();
         String[] stats = splitOnPrefix(s, "udp");
         String[] var12 = stats;
         int var13 = stats.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            String stat = var12[var14];
            if (stat != null) {
               String[] split = stat.split("=");
               if (split.length == 2) {
                  switch (split[0].trim()) {
                     case "udpOutDatagrams":
                        datagramsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "udpInDatagrams":
                        datagramsReceived = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "udpNoPorts":
                        datagramsNoPort = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                        break;
                     case "udpInErrors":
                        datagramsReceivedErrors = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                  }
               }
            }
         }
      }

      return new InternetProtocolStats.UdpStats(datagramsSent, datagramsReceived, datagramsNoPort, datagramsReceivedErrors);
   }

   private static String[] splitOnPrefix(String s, String prefix) {
      String[] stats = new String[2];
      int first = s.indexOf(prefix);
      if (first >= 0) {
         int second = s.indexOf(prefix, first + 1);
         if (second >= 0) {
            stats[0] = s.substring(first, second).trim();
            stats[1] = s.substring(second).trim();
         } else {
            stats[0] = s.substring(first).trim();
         }
      }

      return stats;
   }
}
