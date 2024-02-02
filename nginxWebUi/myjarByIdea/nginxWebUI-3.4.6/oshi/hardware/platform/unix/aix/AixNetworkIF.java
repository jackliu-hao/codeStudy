package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.perfstat.PerfstatNetInterface;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.util.Memoizer;

@ThreadSafe
public final class AixNetworkIF extends AbstractNetworkIF {
   private long bytesRecv;
   private long bytesSent;
   private long packetsRecv;
   private long packetsSent;
   private long inErrors;
   private long outErrors;
   private long inDrops;
   private long collisions;
   private long speed;
   private long timeStamp;
   private Supplier<Perfstat.perfstat_netinterface_t[]> netstats;

   public AixNetworkIF(NetworkInterface netint, Supplier<Perfstat.perfstat_netinterface_t[]> netstats) {
      super(netint);
      this.netstats = netstats;
      this.updateAttributes();
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      Supplier<Perfstat.perfstat_netinterface_t[]> netstats = Memoizer.memoize(PerfstatNetInterface::queryNetInterfaces, Memoizer.defaultExpiration());
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map((n) -> {
         return new AixNetworkIF(n, netstats);
      }).collect(Collectors.toList()));
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
      return this.speed;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public boolean updateAttributes() {
      Perfstat.perfstat_netinterface_t[] stats = (Perfstat.perfstat_netinterface_t[])this.netstats.get();
      long now = System.currentTimeMillis();
      Perfstat.perfstat_netinterface_t[] var4 = stats;
      int var5 = stats.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Perfstat.perfstat_netinterface_t stat = var4[var6];
         String name = Native.toString(stat.name);
         if (name.equals(this.getName())) {
            this.bytesSent = stat.obytes;
            this.bytesRecv = stat.ibytes;
            this.packetsSent = stat.opackets;
            this.packetsRecv = stat.ipackets;
            this.outErrors = stat.oerrors;
            this.inErrors = stat.ierrors;
            this.collisions = stat.collisions;
            this.inDrops = stat.if_iqdrops;
            this.speed = stat.bitrate;
            this.timeStamp = now;
            return true;
         }
      }

      return false;
   }
}
