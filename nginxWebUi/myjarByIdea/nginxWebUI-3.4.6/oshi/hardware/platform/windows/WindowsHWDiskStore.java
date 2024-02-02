package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.PhysicalDisk;
import oshi.driver.windows.wmi.Win32DiskDrive;
import oshi.driver.windows.wmi.Win32DiskDriveToDiskPartition;
import oshi.driver.windows.wmi.Win32DiskPartition;
import oshi.driver.windows.wmi.Win32LogicalDiskToPartition;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class WindowsHWDiskStore extends AbstractHWDiskStore {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsHWDiskStore.class);
   private static final String PHYSICALDRIVE_PREFIX = "\\\\.\\PHYSICALDRIVE";
   private static final Pattern DEVICE_ID = Pattern.compile(".*\\.DeviceID=\"(.*)\"");
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList;

   private WindowsHWDiskStore(String name, String model, String serial, long size) {
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
      String index = null;
      List<HWPartition> partitions = this.getPartitions();
      if (!partitions.isEmpty()) {
         index = Integer.toString(((HWPartition)partitions.get(0)).getMajor());
      } else {
         if (!this.getName().startsWith("\\\\.\\PHYSICALDRIVE")) {
            LOG.warn((String)"Couldn't match index for {}", (Object)this.getName());
            return false;
         }

         index = this.getName().substring("\\\\.\\PHYSICALDRIVE".length(), this.getName().length());
      }

      DiskStats stats = queryReadWriteStats(index);
      if (stats.readMap.containsKey(index)) {
         this.reads = (Long)stats.readMap.getOrDefault(index, 0L);
         this.readBytes = (Long)stats.readByteMap.getOrDefault(index, 0L);
         this.writes = (Long)stats.writeMap.getOrDefault(index, 0L);
         this.writeBytes = (Long)stats.writeByteMap.getOrDefault(index, 0L);
         this.currentQueueLength = (Long)stats.queueLengthMap.getOrDefault(index, 0L);
         this.transferTime = stats.timeStamp - (Long)stats.idleTimeMap.getOrDefault(index, stats.timeStamp);
         this.timeStamp = stats.timeStamp;
         return true;
      } else {
         return false;
      }
   }

   public static List<HWDiskStore> getDisks() {
      List<HWDiskStore> result = new ArrayList();
      DiskStats stats = queryReadWriteStats((String)null);
      PartitionMaps maps = queryPartitionMaps();
      WbemcliUtil.WmiResult<Win32DiskDrive.DiskDriveProperty> vals = Win32DiskDrive.queryDiskDrive();

      for(int i = 0; i < vals.getResultCount(); ++i) {
         WindowsHWDiskStore ds = new WindowsHWDiskStore(WmiUtil.getString(vals, Win32DiskDrive.DiskDriveProperty.NAME, i), String.format("%s %s", WmiUtil.getString(vals, Win32DiskDrive.DiskDriveProperty.MODEL, i), WmiUtil.getString(vals, Win32DiskDrive.DiskDriveProperty.MANUFACTURER, i)).trim(), ParseUtil.hexStringToString(WmiUtil.getString(vals, Win32DiskDrive.DiskDriveProperty.SERIALNUMBER, i)), WmiUtil.getUint64(vals, Win32DiskDrive.DiskDriveProperty.SIZE, i));
         String index = Integer.toString(WmiUtil.getUint32(vals, Win32DiskDrive.DiskDriveProperty.INDEX, i));
         ds.reads = (Long)stats.readMap.getOrDefault(index, 0L);
         ds.readBytes = (Long)stats.readByteMap.getOrDefault(index, 0L);
         ds.writes = (Long)stats.writeMap.getOrDefault(index, 0L);
         ds.writeBytes = (Long)stats.writeByteMap.getOrDefault(index, 0L);
         ds.currentQueueLength = (Long)stats.queueLengthMap.getOrDefault(index, 0L);
         ds.transferTime = stats.timeStamp - (Long)stats.idleTimeMap.getOrDefault(index, stats.timeStamp);
         ds.timeStamp = stats.timeStamp;
         List<HWPartition> partitions = new ArrayList();
         List<String> partList = (List)maps.driveToPartitionMap.get(ds.getName());
         if (partList != null && !partList.isEmpty()) {
            Iterator var9 = partList.iterator();

            while(var9.hasNext()) {
               String part = (String)var9.next();
               if (maps.partitionMap.containsKey(part)) {
                  partitions.addAll((Collection)maps.partitionMap.get(part));
               }
            }
         }

         ds.partitionList = Collections.unmodifiableList((List)partitions.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
         result.add(ds);
      }

      return Collections.unmodifiableList(result);
   }

   private static DiskStats queryReadWriteStats(String index) {
      DiskStats stats = new DiskStats();
      Pair<List<String>, Map<PhysicalDisk.PhysicalDiskProperty, List<Long>>> instanceValuePair = PhysicalDisk.queryDiskCounters();
      List<String> instances = (List)instanceValuePair.getA();
      Map<PhysicalDisk.PhysicalDiskProperty, List<Long>> valueMap = (Map)instanceValuePair.getB();
      stats.timeStamp = System.currentTimeMillis();
      List<Long> readList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKREADSPERSEC);
      List<Long> readByteList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKREADBYTESPERSEC);
      List<Long> writeList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITESPERSEC);
      List<Long> writeByteList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITEBYTESPERSEC);
      List<Long> queueLengthList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.CURRENTDISKQUEUELENGTH);
      List<Long> idleTimeList = (List)valueMap.get(PhysicalDisk.PhysicalDiskProperty.PERCENTIDLETIME);
      if (!instances.isEmpty() && readList != null && readByteList != null && writeList != null && writeByteList != null && queueLengthList != null && idleTimeList != null) {
         for(int i = 0; i < instances.size(); ++i) {
            String name = getIndexFromName((String)instances.get(i));
            if (index == null || index.equals(name)) {
               stats.readMap.put(name, (Long)readList.get(i));
               stats.readByteMap.put(name, (Long)readByteList.get(i));
               stats.writeMap.put(name, (Long)writeList.get(i));
               stats.writeByteMap.put(name, (Long)writeByteList.get(i));
               stats.queueLengthMap.put(name, (Long)queueLengthList.get(i));
               stats.idleTimeMap.put(name, (Long)idleTimeList.get(i) / 10000L);
            }
         }

         return stats;
      } else {
         return stats;
      }
   }

   private static PartitionMaps queryPartitionMaps() {
      PartitionMaps maps = new PartitionMaps();
      WbemcliUtil.WmiResult<Win32DiskDriveToDiskPartition.DriveToPartitionProperty> drivePartitionMap = Win32DiskDriveToDiskPartition.queryDriveToPartition();

      Matcher mAnt;
      Matcher mDep;
      for(int i = 0; i < drivePartitionMap.getResultCount(); ++i) {
         mAnt = DEVICE_ID.matcher(WmiUtil.getRefString(drivePartitionMap, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.ANTECEDENT, i));
         mDep = DEVICE_ID.matcher(WmiUtil.getRefString(drivePartitionMap, Win32DiskDriveToDiskPartition.DriveToPartitionProperty.DEPENDENT, i));
         if (mAnt.matches() && mDep.matches()) {
            ((List)maps.driveToPartitionMap.computeIfAbsent(mAnt.group(1).replace("\\\\", "\\"), (x) -> {
               return new ArrayList();
            })).add(mDep.group(1));
         }
      }

      WbemcliUtil.WmiResult<Win32LogicalDiskToPartition.DiskToPartitionProperty> diskPartitionMap = Win32LogicalDiskToPartition.queryDiskToPartition();

      for(int i = 0; i < diskPartitionMap.getResultCount(); ++i) {
         mAnt = DEVICE_ID.matcher(WmiUtil.getRefString(diskPartitionMap, Win32LogicalDiskToPartition.DiskToPartitionProperty.ANTECEDENT, i));
         mDep = DEVICE_ID.matcher(WmiUtil.getRefString(diskPartitionMap, Win32LogicalDiskToPartition.DiskToPartitionProperty.DEPENDENT, i));
         long size = WmiUtil.getUint64(diskPartitionMap, Win32LogicalDiskToPartition.DiskToPartitionProperty.ENDINGADDRESS, i) - WmiUtil.getUint64(diskPartitionMap, Win32LogicalDiskToPartition.DiskToPartitionProperty.STARTINGADDRESS, i) + 1L;
         if (mAnt.matches() && mDep.matches()) {
            if (maps.partitionToLogicalDriveMap.containsKey(mAnt.group(1))) {
               ((List)maps.partitionToLogicalDriveMap.get(mAnt.group(1))).add(new Pair(mDep.group(1) + "\\", size));
            } else {
               List<Pair<String, Long>> list = new ArrayList();
               list.add(new Pair(mDep.group(1) + "\\", size));
               maps.partitionToLogicalDriveMap.put(mAnt.group(1), list);
            }
         }
      }

      WbemcliUtil.WmiResult<Win32DiskPartition.DiskPartitionProperty> hwPartitionQueryMap = Win32DiskPartition.queryPartition();

      for(int i = 0; i < hwPartitionQueryMap.getResultCount(); ++i) {
         String deviceID = WmiUtil.getString(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.DEVICEID, i);
         List<Pair<String, Long>> logicalDrives = (List)maps.partitionToLogicalDriveMap.get(deviceID);
         if (logicalDrives != null) {
            for(int j = 0; j < logicalDrives.size(); ++j) {
               Pair<String, Long> logicalDrive = (Pair)logicalDrives.get(j);
               if (logicalDrive != null && !((String)logicalDrive.getA()).isEmpty()) {
                  HWPartition pt = new HWPartition(WmiUtil.getString(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.NAME, i), WmiUtil.getString(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.TYPE, i), WmiUtil.getString(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.DESCRIPTION, i), "", (Long)logicalDrive.getB(), WmiUtil.getUint32(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.DISKINDEX, i), WmiUtil.getUint32(hwPartitionQueryMap, Win32DiskPartition.DiskPartitionProperty.INDEX, i), (String)logicalDrive.getA());
                  if (maps.partitionMap.containsKey(deviceID)) {
                     ((List)maps.partitionMap.get(deviceID)).add(pt);
                  } else {
                     List<HWPartition> ptlist = new ArrayList();
                     ptlist.add(pt);
                     maps.partitionMap.put(deviceID, ptlist);
                  }
               }
            }
         }
      }

      return maps;
   }

   private static String getIndexFromName(String s) {
      return s.isEmpty() ? s : s.split("\\s")[0];
   }

   private static final class DiskStats {
      private final Map<String, Long> readMap;
      private final Map<String, Long> readByteMap;
      private final Map<String, Long> writeMap;
      private final Map<String, Long> writeByteMap;
      private final Map<String, Long> queueLengthMap;
      private final Map<String, Long> idleTimeMap;
      private long timeStamp;

      private DiskStats() {
         this.readMap = new HashMap();
         this.readByteMap = new HashMap();
         this.writeMap = new HashMap();
         this.writeByteMap = new HashMap();
         this.queueLengthMap = new HashMap();
         this.idleTimeMap = new HashMap();
      }

      // $FF: synthetic method
      DiskStats(Object x0) {
         this();
      }
   }

   private static final class PartitionMaps {
      private final Map<String, List<String>> driveToPartitionMap;
      private final Map<String, List<Pair<String, Long>>> partitionToLogicalDriveMap;
      private final Map<String, List<HWPartition>> partitionMap;

      private PartitionMaps() {
         this.driveToPartitionMap = new HashMap();
         this.partitionToLogicalDriveMap = new HashMap();
         this.partitionMap = new HashMap();
      }

      // $FF: synthetic method
      PartitionMaps(Object x0) {
         this();
      }
   }
}
