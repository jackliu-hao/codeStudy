package oshi.hardware.platform.unix.freebsd;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class FreeBsdNetworkIF extends AbstractNetworkIF {
   private long bytesRecv;
   private long bytesSent;
   private long packetsRecv;
   private long packetsSent;
   private long inErrors;
   private long outErrors;
   private long inDrops;
   private long collisions;
   private long timeStamp;

   public FreeBsdNetworkIF(NetworkInterface netint) {
      super(netint);
      this.updateAttributes();
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map(FreeBsdNetworkIF::new).collect(Collectors.toList()));
   }

   public long getBytesRecv() {
      return this.bytesRecv;
   }

   public long getBytesSent() {
      return this.bytesSent;
   }

   public long getPacketsRecv() {
      return this.packetsRecv;
   }

   public long getPacketsSent() {
      return this.packetsSent;
   }

   public long getInErrors() {
      return this.inErrors;
   }

   public long getOutErrors() {
      return this.outErrors;
   }

   public long getInDrops() {
      return this.inDrops;
   }

   public long getCollisions() {
      return this.collisions;
   }

   public long getSpeed() {
      return 0L;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public boolean updateAttributes() {
      String stats = ExecutingCommand.getAnswerAt("netstat -bI " + this.getName(), 1);
      this.timeStamp = System.currentTimeMillis();
      String[] split = ParseUtil.whitespaces.split(stats);
      if (split.length < 12) {
         return false;
      } else {
         this.bytesSent = ParseUtil.parseUnsignedLongOrDefault(split[10], 0L);
         this.bytesRecv = ParseUtil.parseUnsignedLongOrDefault(split[7], 0L);
         this.packetsSent = ParseUtil.parseUnsignedLongOrDefault(split[8], 0L);
         this.packetsRecv = ParseUtil.parseUnsignedLongOrDefault(split[4], 0L);
         this.outErrors = ParseUtil.parseUnsignedLongOrDefault(split[9], 0L);
         this.inErrors = ParseUtil.parseUnsignedLongOrDefault(split[5], 0L);
         this.collisions = ParseUtil.parseUnsignedLongOrDefault(split[11], 0L);
         this.inDrops = ParseUtil.parseUnsignedLongOrDefault(split[6], 0L);
         return true;
      }
   }
}
