package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.freebsd.disk.GeomDiskList;
import oshi.driver.unix.freebsd.disk.GeomPartList;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class FreeBsdHWDiskStore extends AbstractHWDiskStore {
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList;

   private FreeBsdHWDiskStore(String name, String model, String serial, long size) {
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
      List<String> output = ExecutingCommand.runNative("iostat -Ix " + this.getName());
      long now = System.currentTimeMillis();
      boolean diskFound = false;
      Iterator var5 = output.iterator();

      while(var5.hasNext()) {
         String line = (String)var5.next();
         String[] split = ParseUtil.whitespaces.split(line);
         if (split.length >= 7 && split[0].equals(this.getName())) {
            diskFound = true;
            this.reads = (long)ParseUtil.parseDoubleOrDefault(split[1], 0.0);
            this.writes = (long)ParseUtil.parseDoubleOrDefault(split[2], 0.0);
            this.readBytes = (long)(ParseUtil.parseDoubleOrDefault(split[3], 0.0) * 1024.0);
            this.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(split[4], 0.0) * 1024.0);
            this.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
            this.transferTime = (long)(ParseUtil.parseDoubleOrDefault(split[6], 0.0) * 1000.0);
            this.timeStamp = now;
         }
      }

      return diskFound;
   }

   public static List<HWDiskStore> getDisks() {
      List<HWDiskStore> diskList = new ArrayList();
      Map<String, List<HWPartition>> partitionMap = GeomPartList.queryPartitions();
      Map<String, Triplet<String, String, Long>> diskInfoMap = GeomDiskList.queryDisks();
      List<String> devices = Arrays.asList(ParseUtil.whitespaces.split(BsdSysctlUtil.sysctl("kern.disks", "")));
      List<String> iostat = ExecutingCommand.runNative("iostat -Ix");
      long now = System.currentTimeMillis();
      Iterator var7 = iostat.iterator();

      while(var7.hasNext()) {
         String line = (String)var7.next();
         String[] split = ParseUtil.whitespaces.split(line);
         if (split.length > 6 && devices.contains(split[0])) {
            Triplet<String, String, Long> storeInfo = (Triplet)diskInfoMap.get(split[0]);
            FreeBsdHWDiskStore store = storeInfo == null ? new FreeBsdHWDiskStore(split[0], "unknown", "unknown", 0L) : new FreeBsdHWDiskStore(split[0], (String)storeInfo.getA(), (String)storeInfo.getB(), (Long)storeInfo.getC());
            store.reads = (long)ParseUtil.parseDoubleOrDefault(split[1], 0.0);
            store.writes = (long)ParseUtil.parseDoubleOrDefault(split[2], 0.0);
            store.readBytes = (long)(ParseUtil.parseDoubleOrDefault(split[3], 0.0) * 1024.0);
            store.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(split[4], 0.0) * 1024.0);
            store.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
            store.transferTime = (long)(ParseUtil.parseDoubleOrDefault(split[6], 0.0) * 1000.0);
            store.partitionList = Collections.unmodifiableList((List)((List)partitionMap.getOrDefault(split[0], Collections.emptyList())).stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
            store.timeStamp = now;
            diskList.add(store);
         }
      }

      return Collections.unmodifiableList(diskList);
   }
}
