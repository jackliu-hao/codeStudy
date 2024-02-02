package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.disk.Iostat;
import oshi.driver.unix.solaris.disk.Lshal;
import oshi.driver.unix.solaris.disk.Prtvtoc;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class SolarisHWDiskStore extends AbstractHWDiskStore {
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList;

   private SolarisHWDiskStore(String name, String model, String serial, long size) {
      super(name, model, serial, size);
   }

   public long getReads() {
      return this.reads;
   }

   public long getReadBytes() {
      return this.readBytes;
   }

   public long getWrites() {
      return this.writes;
   }

   public long getWriteBytes() {
      return this.writeBytes;
   }

   public long getCurrentQueueLength() {
      return this.currentQueueLength;
   }

   public long getTransferTime() {
      return this.transferTime;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public List<HWPartition> getPartitions() {
      return this.partitionList;
   }

   public boolean updateAttributes() {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      boolean var4;
      label42: {
         try {
            LibKstat.Kstat ksp = kc.lookup((String)null, 0, this.getName());
            if (ksp != null && kc.read(ksp)) {
               LibKstat.KstatIO data = new LibKstat.KstatIO(ksp.ks_data);
               this.reads = (long)data.reads;
               this.writes = (long)data.writes;
               this.readBytes = data.nread;
               this.writeBytes = data.nwritten;
               this.currentQueueLength = (long)data.wcnt + (long)data.rcnt;
               this.transferTime = data.rtime / 1000000L;
               this.timeStamp = ksp.ks_snaptime / 1000000L;
               var4 = true;
               break label42;
            }
         } catch (Throwable var6) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (kc != null) {
            kc.close();
         }

         return false;
      }

      if (kc != null) {
         kc.close();
      }

      return var4;
   }

   public static List<HWDiskStore> getDisks() {
      Map<String, String> deviceMap = Iostat.queryPartitionToMountMap();
      Map<String, Integer> majorMap = Lshal.queryDiskToMajorMap();
      Map<String, Quintet<String, String, String, String, Long>> deviceStringMap = Iostat.queryDeviceStrings(deviceMap.keySet());
      List<SolarisHWDiskStore> storeList = new ArrayList();
      Iterator var4 = deviceStringMap.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<String, Quintet<String, String, String, String, Long>> entry = (Map.Entry)var4.next();
         String storeName = (String)entry.getKey();
         Quintet<String, String, String, String, Long> val = (Quintet)entry.getValue();
         storeList.add(createStore(storeName, (String)val.getA(), (String)val.getB(), (String)val.getC(), (String)val.getD(), (Long)val.getE(), (String)deviceMap.getOrDefault(storeName, ""), (Integer)majorMap.getOrDefault(storeName, 0)));
      }

      return Collections.unmodifiableList(storeList);
   }

   private static SolarisHWDiskStore createStore(String diskName, String model, String vendor, String product, String serial, long size, String mount, int major) {
      SolarisHWDiskStore store = new SolarisHWDiskStore(diskName, model.isEmpty() ? (vendor + " " + product).trim() : model, serial, size);
      store.partitionList = Collections.unmodifiableList((List)Prtvtoc.queryPartitions(mount, major).stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
      store.updateAttributes();
      return store;
   }
}
