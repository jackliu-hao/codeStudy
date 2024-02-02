package oshi.software.os;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface InternetProtocolStats {
   TcpStats getTCPv4Stats();

   TcpStats getTCPv6Stats();

   UdpStats getUDPv4Stats();

   UdpStats getUDPv6Stats();

   public static final class UdpStats {
      private final long datagramsSent;
      private final long datagramsReceived;
      private final long datagramsNoPort;
      private final long datagramsReceivedErrors;

      public UdpStats(long datagramsSent, long datagramsReceived, long datagramsNoPort, long datagramsReceivedErrors) {
         this.datagramsSent = datagramsSent;
         this.datagramsReceived = datagramsReceived;
         this.datagramsNoPort = datagramsNoPort;
         this.datagramsReceivedErrors = datagramsReceivedErrors;
      }

      public long getDatagramsSent() {
         return this.datagramsSent;
      }

      public long getDatagramsReceived() {
         return this.datagramsReceived;
      }

      public long getDatagramsNoPort() {
         return this.datagramsNoPort;
      }

      public long getDatagramsReceivedErrors() {
         return this.datagramsReceivedErrors;
      }

      public String toString() {
         return "UdpStats [datagramsSent=" + this.datagramsSent + ", datagramsReceived=" + this.datagramsReceived + ", datagramsNoPort=" + this.datagramsNoPort + ", datagramsReceivedErrors=" + this.datagramsReceivedErrors + "]";
      }
   }

   public static final class TcpStats {
      private final long connectionsEstablished;
      private final long connectionsActive;
      private final long connectionsPassive;
      private final long connectionFailures;
      private final long connectionsReset;
      private final long segmentsSent;
      private final long segmentsReceived;
      private final long segmentsRetransmitted;
      private final long inErrors;
      private final long outResets;

      public TcpStats(long connectionsEstablished, long connectionsActive, long connectionsPassive, long connectionFailures, long connectionsReset, long segmentsSent, long segmentsReceived, long segmentsRetransmitted, long inErrors, long outResets) {
         this.connectionsEstablished = connectionsEstablished;
         this.connectionsActive = connectionsActive;
         this.connectionsPassive = connectionsPassive;
         this.connectionFailures = connectionFailures;
         this.connectionsReset = connectionsReset;
         this.segmentsSent = segmentsSent;
         this.segmentsReceived = segmentsReceived;
         this.segmentsRetransmitted = segmentsRetransmitted;
         this.inErrors = inErrors;
         this.outResets = outResets;
      }

      public long getConnectionsEstablished() {
         return this.connectionsEstablished;
      }

      public long getConnectionsActive() {
         return this.connectionsActive;
      }

      public long getConnectionsPassive() {
         return this.connectionsPassive;
      }

      public long getConnectionFailures() {
         return this.connectionFailures;
      }

      public long getConnectionsReset() {
         return this.connectionsReset;
      }

      public long getSegmentsSent() {
         return this.segmentsSent;
      }

      public long getSegmentsReceived() {
         return this.segmentsReceived;
      }

      public long getSegmentsRetransmitted() {
         return this.segmentsRetransmitted;
      }

      public long getInErrors() {
         return this.inErrors;
      }

      public long getOutResets() {
         return this.outResets;
      }

      public String toString() {
         return "TcpStats [connectionsEstablished=" + this.connectionsEstablished + ", connectionsActive=" + this.connectionsActive + ", connectionsPassive=" + this.connectionsPassive + ", connectionFailures=" + this.connectionFailures + ", connectionsReset=" + this.connectionsReset + ", segmentsSent=" + this.segmentsSent + ", segmentsReceived=" + this.segmentsReceived + ", segmentsRetransmitted=" + this.segmentsRetransmitted + ", inErrors=" + this.inErrors + ", outResets=" + this.outResets + "]";
      }
   }
}
