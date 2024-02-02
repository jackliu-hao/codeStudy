package oshi.driver.mac.net;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class NetStat {
   private static final Logger LOG = LoggerFactory.getLogger(NetStat.class);
   private static final int CTL_NET = 4;
   private static final int PF_ROUTE = 17;
   private static final int NET_RT_IFLIST2 = 6;
   private static final int RTM_IFINFO2 = 18;

   private NetStat() {
   }

   public static Map<Integer, IFdata> queryIFdata(int index) {
      Map<Integer, IFdata> data = new HashMap();
      int[] mib = new int[]{4, 17, 0, 0, 6, 0};
      IntByReference len = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctl(mib, 6, (Pointer)null, len, (Pointer)null, 0)) {
         LOG.error("Didn't get buffer length for IFLIST2");
         return data;
      } else {
         Memory buf = new Memory((long)len.getValue());
         if (0 != SystemB.INSTANCE.sysctl(mib, 6, buf, len, (Pointer)null, 0)) {
            LOG.error("Didn't get buffer for IFLIST2");
            return data;
         } else {
            long now = System.currentTimeMillis();
            int lim = (int)(buf.size() - (long)(new SystemB.IFmsgHdr()).size());
            int offset = 0;

            do {
               SystemB.IFmsgHdr2 if2m;
               do {
                  Pointer p;
                  SystemB.IFmsgHdr ifm;
                  do {
                     if (offset >= lim) {
                        return data;
                     }

                     p = buf.share((long)offset);
                     ifm = new SystemB.IFmsgHdr(p);
                     ifm.read();
                     offset += ifm.ifm_msglen;
                  } while(ifm.ifm_type != 18);

                  if2m = new SystemB.IFmsgHdr2(p);
                  if2m.read();
               } while(index >= 0 && index != if2m.ifm_index);

               data.put(Integer.valueOf(if2m.ifm_index), new IFdata(if2m.ifm_data.ifi_type, if2m.ifm_data.ifi_opackets, if2m.ifm_data.ifi_ipackets, if2m.ifm_data.ifi_obytes, if2m.ifm_data.ifi_ibytes, if2m.ifm_data.ifi_oerrors, if2m.ifm_data.ifi_ierrors, if2m.ifm_data.ifi_collisions, if2m.ifm_data.ifi_iqdrops, if2m.ifm_data.ifi_baudrate, now));
            } while(index < 0);

            return data;
         }
      }
   }

   @Immutable
   public static class IFdata {
      private final int ifType;
      private final long oPackets;
      private final long iPackets;
      private final long oBytes;
      private final long iBytes;
      private final long oErrors;
      private final long iErrors;
      private final long collisions;
      private final long iDrops;
      private final long speed;
      private final long timeStamp;

      IFdata(int ifType, long oPackets, long iPackets, long oBytes, long iBytes, long oErrors, long iErrors, long collisions, long iDrops, long speed, long timeStamp) {
         this.ifType = ifType;
         this.oPackets = oPackets & 4294967295L;
         this.iPackets = iPackets & 4294967295L;
         this.oBytes = oBytes & 4294967295L;
         this.iBytes = iBytes & 4294967295L;
         this.oErrors = oErrors & 4294967295L;
         this.iErrors = iErrors & 4294967295L;
         this.collisions = collisions & 4294967295L;
         this.iDrops = iDrops & 4294967295L;
         this.speed = speed & 4294967295L;
         this.timeStamp = timeStamp;
      }

      public int getIfType() {
         return this.ifType;
      }

      public long getOPackets() {
         return this.oPackets;
      }

      public long getIPackets() {
         return this.iPackets;
      }

      public long getOBytes() {
         return this.oBytes;
      }

      public long getIBytes() {
         return this.iBytes;
      }

      public long getOErrors() {
         return this.oErrors;
      }

      public long getIErrors() {
         return this.iErrors;
      }

      public long getCollisions() {
         return this.collisions;
      }

      public long getIDrops() {
         return this.iDrops;
      }

      public long getSpeed() {
         return this.speed;
      }

      public long getTimeStamp() {
         return this.timeStamp;
      }
   }
}
