package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisNetworkIF extends AbstractNetworkIF {
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

   public SolarisNetworkIF(NetworkInterface netint) {
      super(netint);
      this.updateAttributes();
   }

   public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
      return Collections.unmodifiableList((List)getNetworkInterfaces(includeLocalInterfaces).stream().map(SolarisNetworkIF::new).collect(Collectors.toList()));
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
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      boolean var3;
      label46: {
         try {
            LibKstat.Kstat ksp = kc.lookup("link", -1, this.getName());
            if (ksp == null) {
               ksp = kc.lookup((String)null, -1, this.getName());
            }

            if (ksp != null && kc.read(ksp)) {
               this.bytesSent = KstatUtil.dataLookupLong(ksp, "obytes64");
               this.bytesRecv = KstatUtil.dataLookupLong(ksp, "rbytes64");
               this.packetsSent = KstatUtil.dataLookupLong(ksp, "opackets64");
               this.packetsRecv = KstatUtil.dataLookupLong(ksp, "ipackets64");
               this.outErrors = KstatUtil.dataLookupLong(ksp, "oerrors");
               this.inErrors = KstatUtil.dataLookupLong(ksp, "ierrors");
               this.collisions = KstatUtil.dataLookupLong(ksp, "collisions");
               this.inDrops = KstatUtil.dataLookupLong(ksp, "dl_idrops");
               this.speed = KstatUtil.dataLookupLong(ksp, "ifspeed");
               this.timeStamp = ksp.ks_snaptime / 1000000L;
               var3 = true;
               break label46;
            }
         } catch (Throwable var5) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (kc != null) {
            kc.close();
         }

         return false;
      }

      if (kc != null) {
         kc.close();
      }

      return var3;
   }
}
