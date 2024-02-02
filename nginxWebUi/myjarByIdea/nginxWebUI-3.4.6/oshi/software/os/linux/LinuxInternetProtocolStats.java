package oshi.software.os.linux;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class LinuxInternetProtocolStats implements InternetProtocolStats {
   public InternetProtocolStats.TcpStats getTCPv4Stats() {
      return getTcpStats("netstat -st4");
   }

   public InternetProtocolStats.TcpStats getTCPv6Stats() {
      return new InternetProtocolStats.TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
   }

   private static InternetProtocolStats.TcpStats getTcpStats(String netstatStr) {
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
      List<String> netstat = ExecutingCommand.runNative(netstatStr);
      Iterator var22 = netstat.iterator();

      while(var22.hasNext()) {
         String s = (String)var22.next();
         String[] split = s.trim().split(" ", 2);
         if (split.length == 2) {
            switch (split[1]) {
               case "connections established":
                  connectionsEstablished = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "active connection openings":
                  connectionsActive = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "passive connection openings":
                  connectionsPassive = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "failed connection attempts":
                  connectionFailures = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "connection resets received":
                  connectionsReset = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "segments sent out":
                  segmentsSent = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "segments received":
                  segmentsReceived = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "segments retransmitted":
                  segmentsRetransmitted = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "bad segments received":
                  inErrors = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "resets sent":
                  outResets = ParseUtil.parseLongOrDefault(split[0], 0L);
            }
         }
      }

      return new InternetProtocolStats.TcpStats(connectionsEstablished, connectionsActive, connectionsPassive, connectionFailures, connectionsReset, segmentsSent, segmentsReceived, segmentsRetransmitted, inErrors, outResets);
   }

   public InternetProtocolStats.UdpStats getUDPv4Stats() {
      return getUdpStats("netstat -su4");
   }

   public InternetProtocolStats.UdpStats getUDPv6Stats() {
      return getUdpStats("netstat -su6");
   }

   private static InternetProtocolStats.UdpStats getUdpStats(String netstatStr) {
      long datagramsSent = 0L;
      long datagramsReceived = 0L;
      long datagramsNoPort = 0L;
      long datagramsReceivedErrors = 0L;
      List<String> netstat = ExecutingCommand.runNative(netstatStr);
      Iterator var10 = netstat.iterator();

      while(var10.hasNext()) {
         String s = (String)var10.next();
         String[] split = s.trim().split(" ", 2);
         if (split.length == 2) {
            switch (split[1]) {
               case "packets sent":
                  datagramsSent = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "packets received":
                  datagramsReceived = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "packets to unknown port received":
                  datagramsNoPort = ParseUtil.parseLongOrDefault(split[0], 0L);
                  break;
               case "packet receive errors":
                  datagramsReceivedErrors = ParseUtil.parseLongOrDefault(split[0], 0L);
            }
         }
      }

      return new InternetProtocolStats.UdpStats(datagramsSent, datagramsReceived, datagramsNoPort, datagramsReceivedErrors);
   }
}
