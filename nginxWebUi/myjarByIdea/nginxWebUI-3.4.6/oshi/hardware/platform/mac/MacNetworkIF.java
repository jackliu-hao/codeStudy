package oshi.hardware.platform.mac;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.net.NetStat;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;

@ThreadSafe
public final class MacNetworkIF extends AbstractNetworkIF {
   private int ifType;
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

   public MacNetworkIF(NetworkInterface netint, Map<Integer, NetStat.IFdata> data) {
      super(netint);
      this.updateNetworkStats(data);
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      Map<Integer, NetStat.IFdata> data = NetStat.queryIFdata(-1);
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map((ni) -> {
         return new MacNetworkIF(ni, data);
      }).collect(Collectors.toList()));
   }

   public int getIfType() {
      return this.ifType;
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
      int index = this.queryNetworkInterface().getIndex();
      return this.updateNetworkStats(NetStat.queryIFdata(index));
   }

   private boolean updateNetworkStats(Map<Integer, NetStat.IFdata> data) {
      int index = this.queryNetworkInterface().getIndex();
      if (data.containsKey(index)) {
         NetStat.IFdata ifData = (NetStat.IFdata)data.get(index);
         this.ifType = ifData.getIfType();
         this.bytesSent = ifData.getOBytes();
         this.bytesRecv = ifData.getIBytes();
         this.packetsSent = ifData.getOPackets();
         this.packetsRecv = ifData.getIPackets();
         this.outErrors = ifData.getOErrors();
         this.inErrors = ifData.getIErrors();
         this.collisions = ifData.getCollisions();
         this.inDrops = ifData.getIDrops();
         this.speed = ifData.getSpeed();
         this.timeStamp = ifData.getTimeStamp();
         return true;
      } else {
         return false;
      }
   }
}
