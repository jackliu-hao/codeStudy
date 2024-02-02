package oshi.hardware.platform.linux;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.FileUtil;

@ThreadSafe
public final class LinuxNetworkIF extends AbstractNetworkIF {
   private int ifType;
   private boolean connectorPresent;
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

   public LinuxNetworkIF(NetworkInterface netint) {
      super(netint);
      this.updateAttributes();
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map(LinuxNetworkIF::new).collect(Collectors.toList()));
   }

   public int getIfType() {
      return this.ifType;
   }

   public boolean isConnectorPresent() {
      return this.connectorPresent;
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
      try {
         File ifDir = new File(String.format("/sys/class/net/%s/statistics", this.getName()));
         if (!ifDir.isDirectory()) {
            return false;
         }
      } catch (SecurityException var14) {
         return false;
      }

      String ifTypePath = String.format("/sys/class/net/%s/type", this.getName());
      String carrierPath = String.format("/sys/class/net/%s/carrier", this.getName());
      String txBytesPath = String.format("/sys/class/net/%s/statistics/tx_bytes", this.getName());
      String rxBytesPath = String.format("/sys/class/net/%s/statistics/rx_bytes", this.getName());
      String txPacketsPath = String.format("/sys/class/net/%s/statistics/tx_packets", this.getName());
      String rxPacketsPath = String.format("/sys/class/net/%s/statistics/rx_packets", this.getName());
      String txErrorsPath = String.format("/sys/class/net/%s/statistics/tx_errors", this.getName());
      String rxErrorsPath = String.format("/sys/class/net/%s/statistics/rx_errors", this.getName());
      String collisionsPath = String.format("/sys/class/net/%s/statistics/collisions", this.getName());
      String rxDropsPath = String.format("/sys/class/net/%s/statistics/rx_dropped", this.getName());
      String ifSpeed = String.format("/sys/class/net/%s/speed", this.getName());
      this.timeStamp = System.currentTimeMillis();
      this.ifType = FileUtil.getIntFromFile(ifTypePath);
      this.connectorPresent = FileUtil.getIntFromFile(carrierPath) > 0;
      this.bytesSent = FileUtil.getUnsignedLongFromFile(txBytesPath);
      this.bytesRecv = FileUtil.getUnsignedLongFromFile(rxBytesPath);
      this.packetsSent = FileUtil.getUnsignedLongFromFile(txPacketsPath);
      this.packetsRecv = FileUtil.getUnsignedLongFromFile(rxPacketsPath);
      this.outErrors = FileUtil.getUnsignedLongFromFile(txErrorsPath);
      this.inErrors = FileUtil.getUnsignedLongFromFile(rxErrorsPath);
      this.collisions = FileUtil.getUnsignedLongFromFile(collisionsPath);
      this.inDrops = FileUtil.getUnsignedLongFromFile(rxDropsPath);
      long speedMiB = FileUtil.getUnsignedLongFromFile(ifSpeed);
      this.speed = speedMiB < 0L ? 0L : speedMiB << 20;
      return true;
   }
}
