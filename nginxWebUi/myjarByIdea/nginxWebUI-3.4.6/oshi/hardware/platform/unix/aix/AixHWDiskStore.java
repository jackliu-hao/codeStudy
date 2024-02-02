package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Ls;
import oshi.driver.unix.aix.Lscfg;
import oshi.driver.unix.aix.Lspv;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class AixHWDiskStore extends AbstractHWDiskStore {
   private final Supplier<Perfstat.perfstat_disk_t[]> diskStats;
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList;

   private AixHWDiskStore(String name, String model, String serial, long size, Supplier<Perfstat.perfstat_disk_t[]> diskStats) {
      super(name, model, serial, size);
      this.diskStats = diskStats;
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
      Perfstat.perfstat_disk_t[] var1 = (Perfstat.perfstat_disk_t[])this.diskStats.get();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Perfstat.perfstat_disk_t stat = var1[var3];
         String name = Native.toString(stat.name);
         if (name.equals(this.getName())) {
            long blks = stat.rblks + stat.wblks;
            this.reads = stat.xfers;
            if (blks > 0L) {
               this.writes = stat.xfers * stat.wblks / blks;
               this.reads -= this.writes;
            }

            this.readBytes = stat.rblks * stat.bsize;
            this.writeBytes = stat.wblks * stat.bsize;
            this.currentQueueLength = stat.qdepth;
            this.transferTime = stat.time;
            return true;
         }
      }

      return false;
   }

   public static List<HWDiskStore> getDisks(Supplier<Perfstat.perfstat_disk_t[]> diskStats) {
      Map<String, Pair<Integer, Integer>> majMinMap = Ls.queryDeviceMajorMinor();
      List<AixHWDiskStore> storeList = new ArrayList();
      Perfstat.perfstat_disk_t[] var3 = (Perfstat.perfstat_disk_t[])diskStats.get();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Perfstat.perfstat_disk_t disk = var3[var5];
         String storeName = Native.toString(disk.name);
         Pair<String, String> ms = Lscfg.queryModelSerial(storeName);
         String model = ms.getA() == null ? Native.toString(disk.description) : (String)ms.getA();
         String serial = ms.getB() == null ? "unknown" : (String)ms.getB();
         storeList.add(createStore(storeName, model, serial, disk.size << 20, diskStats, majMinMap));
      }

      return Collections.unmodifiableList((List)storeList.stream().sorted(Comparator.comparingInt((s) -> {
         return s.getPartitions().isEmpty() ? Integer.MAX_VALUE : ((HWPartition)s.getPartitions().get(0)).getMajor();
      })).collect(Collectors.toList()));
   }

   private static AixHWDiskStore createStore(String diskName, String model, String serial, long size, Supplier<Perfstat.perfstat_disk_t[]> diskStats, Map<String, Pair<Integer, Integer>> majMinMap) {
      AixHWDiskStore store = new AixHWDiskStore(diskName, model.isEmpty() ? "unknown" : model, serial, size, diskStats);
      store.partitionList = Collections.unmodifiableList((List)Lspv.queryLogicalVolumes(diskName, majMinMap).stream().sorted(Comparator.comparing(HWPartition::getMinor).thenComparing(HWPartition::getName)).collect(Collectors.toList()));
      store.updateAttributes();
      return store;
   }
}
