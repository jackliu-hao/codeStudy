package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.VersionHelpers;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WindowsNetworkIF extends AbstractNetworkIF {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkIF.class);
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final byte CONNECTOR_PRESENT_BIT = 32;
   private int ifType;
   private int ndisPhysicalMediumType;
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

   public WindowsNetworkIF(NetworkInterface netint) {
      super(netint);
      this.updateAttributes();
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map(WindowsNetworkIF::new).collect(Collectors.toList()));
   }

   public int getIfType() {
      return this.ifType;
   }

   public int getNdisPhysicalMediumType() {
      return this.ndisPhysicalMediumType;
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
      if (IS_VISTA_OR_GREATER) {
         IPHlpAPI.MIB_IF_ROW2 ifRow = new IPHlpAPI.MIB_IF_ROW2();
         ifRow.InterfaceIndex = this.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry2(ifRow)) {
            LOG.error((String)"Failed to retrieve data for interface {}, {}", (Object)this.queryNetworkInterface().getIndex(), (Object)this.getName());
            return false;
         }

         this.ifType = ifRow.Type;
         this.ndisPhysicalMediumType = ifRow.PhysicalMediumType;
         this.connectorPresent = (ifRow.InterfaceAndOperStatusFlags & 32) > 0;
         this.bytesSent = ifRow.OutOctets;
         this.bytesRecv = ifRow.InOctets;
         this.packetsSent = ifRow.OutUcastPkts;
         this.packetsRecv = ifRow.InUcastPkts;
         this.outErrors = ifRow.OutErrors;
         this.inErrors = ifRow.InErrors;
         this.collisions = ifRow.OutDiscards;
         this.inDrops = ifRow.InDiscards;
         this.speed = ifRow.ReceiveLinkSpeed;
      } else {
         IPHlpAPI.MIB_IFROW ifRow = new IPHlpAPI.MIB_IFROW();
         ifRow.dwIndex = this.queryNetworkInterface().getIndex();
         if (0 != IPHlpAPI.INSTANCE.GetIfEntry(ifRow)) {
            LOG.error((String)"Failed to retrieve data for interface {}, {}", (Object)this.queryNetworkInterface().getIndex(), (Object)this.getName());
            return false;
         }

         this.ifType = ifRow.dwType;
         this.bytesSent = ParseUtil.unsignedIntToLong(ifRow.dwOutOctets);
         this.bytesRecv = ParseUtil.unsignedIntToLong(ifRow.dwInOctets);
         this.packetsSent = ParseUtil.unsignedIntToLong(ifRow.dwOutUcastPkts);
         this.packetsRecv = ParseUtil.unsignedIntToLong(ifRow.dwInUcastPkts);
         this.outErrors = ParseUtil.unsignedIntToLong(ifRow.dwOutErrors);
         this.inErrors = ParseUtil.unsignedIntToLong(ifRow.dwInErrors);
         this.collisions = ParseUtil.unsignedIntToLong(ifRow.dwOutDiscards);
         this.inDrops = ParseUtil.unsignedIntToLong(ifRow.dwInDiscards);
         this.speed = ParseUtil.unsignedIntToLong(ifRow.dwSpeed);
      }

      this.timeStamp = System.currentTimeMillis();
      return true;
   }
}
