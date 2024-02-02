/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.perfmon.PhysicalDisk;
/*     */ import oshi.driver.windows.wmi.Win32DiskDrive;
/*     */ import oshi.driver.windows.wmi.Win32DiskDriveToDiskPartition;
/*     */ import oshi.driver.windows.wmi.Win32DiskPartition;
/*     */ import oshi.driver.windows.wmi.Win32LogicalDiskToPartition;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Pair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class WindowsHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*  65 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsHWDiskStore.class);
/*     */   
/*     */   private static final String PHYSICALDRIVE_PREFIX = "\\\\.\\PHYSICALDRIVE";
/*     */   
/*  69 */   private static final Pattern DEVICE_ID = Pattern.compile(".*\\.DeviceID=\"(.*)\"");
/*     */   
/*  71 */   private long reads = 0L;
/*  72 */   private long readBytes = 0L;
/*  73 */   private long writes = 0L;
/*  74 */   private long writeBytes = 0L;
/*  75 */   private long currentQueueLength = 0L;
/*  76 */   private long transferTime = 0L;
/*  77 */   private long timeStamp = 0L;
/*     */   private List<HWPartition> partitionList;
/*     */   
/*     */   private WindowsHWDiskStore(String name, String model, String serial, long size) {
/*  81 */     super(name, model, serial, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/*  86 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/*  91 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/*  96 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/* 101 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/* 106 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/* 111 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 116 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 121 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 126 */     String index = null;
/* 127 */     List<HWPartition> partitions = getPartitions();
/* 128 */     if (!partitions.isEmpty()) {
/*     */ 
/*     */       
/* 131 */       index = Integer.toString(((HWPartition)partitions.get(0)).getMajor());
/* 132 */     } else if (getName().startsWith("\\\\.\\PHYSICALDRIVE")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       index = getName().substring("\\\\.\\PHYSICALDRIVE".length(), getName().length());
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 144 */       LOG.warn("Couldn't match index for {}", getName());
/* 145 */       return false;
/*     */     } 
/* 147 */     DiskStats stats = queryReadWriteStats(index);
/* 148 */     if (stats.readMap.containsKey(index)) {
/* 149 */       this.reads = ((Long)stats.readMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 150 */       this.readBytes = ((Long)stats.readByteMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 151 */       this.writes = ((Long)stats.writeMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 152 */       this.writeBytes = ((Long)stats.writeByteMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 153 */       this.currentQueueLength = ((Long)stats.queueLengthMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 154 */       this.transferTime = stats.timeStamp - ((Long)stats.idleTimeMap.getOrDefault(index, Long.valueOf(stats.timeStamp))).longValue();
/* 155 */       this.timeStamp = stats.timeStamp;
/* 156 */       return true;
/*     */     } 
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HWDiskStore> getDisks() {
/* 170 */     List<HWDiskStore> result = new ArrayList<>();
/* 171 */     DiskStats stats = queryReadWriteStats(null);
/* 172 */     PartitionMaps maps = queryPartitionMaps();
/*     */     
/* 174 */     WbemcliUtil.WmiResult<Win32DiskDrive.DiskDriveProperty> vals = Win32DiskDrive.queryDiskDrive();
/* 175 */     for (int i = 0; i < vals.getResultCount(); i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 181 */       WindowsHWDiskStore ds = new WindowsHWDiskStore(WmiUtil.getString(vals, (Enum)Win32DiskDrive.DiskDriveProperty.NAME, i), String.format("%s %s", new Object[] { WmiUtil.getString(vals, (Enum)Win32DiskDrive.DiskDriveProperty.MODEL, i), WmiUtil.getString(vals, (Enum)Win32DiskDrive.DiskDriveProperty.MANUFACTURER, i) }).trim(), ParseUtil.hexStringToString(WmiUtil.getString(vals, (Enum)Win32DiskDrive.DiskDriveProperty.SERIALNUMBER, i)), WmiUtil.getUint64(vals, (Enum)Win32DiskDrive.DiskDriveProperty.SIZE, i));
/*     */       
/* 183 */       String index = Integer.toString(WmiUtil.getUint32(vals, (Enum)Win32DiskDrive.DiskDriveProperty.INDEX, i));
/* 184 */       ds.reads = ((Long)stats.readMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 185 */       ds.readBytes = ((Long)stats.readByteMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 186 */       ds.writes = ((Long)stats.writeMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 187 */       ds.writeBytes = ((Long)stats.writeByteMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 188 */       ds.currentQueueLength = ((Long)stats.queueLengthMap.getOrDefault(index, Long.valueOf(0L))).longValue();
/* 189 */       ds.transferTime = stats.timeStamp - ((Long)stats.idleTimeMap.getOrDefault(index, Long.valueOf(stats.timeStamp))).longValue();
/* 190 */       ds.timeStamp = stats.timeStamp;
/*     */       
/* 192 */       List<HWPartition> partitions = new ArrayList<>();
/* 193 */       List<String> partList = (List<String>)maps.driveToPartitionMap.get(ds.getName());
/* 194 */       if (partList != null && !partList.isEmpty()) {
/* 195 */         for (String part : partList) {
/* 196 */           if (maps.partitionMap.containsKey(part)) {
/* 197 */             partitions.addAll((Collection<? extends HWPartition>)maps.partitionMap.get(part));
/*     */           }
/*     */         } 
/*     */       }
/* 201 */       ds.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)partitions.stream()
/* 202 */           .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
/*     */       
/* 204 */       result.add(ds);
/*     */     } 
/* 206 */     return Collections.unmodifiableList(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DiskStats queryReadWriteStats(String index) {
/* 219 */     DiskStats stats = new DiskStats();
/* 220 */     Pair<List<String>, Map<PhysicalDisk.PhysicalDiskProperty, List<Long>>> instanceValuePair = PhysicalDisk.queryDiskCounters();
/* 221 */     List<String> instances = (List<String>)instanceValuePair.getA();
/* 222 */     Map<PhysicalDisk.PhysicalDiskProperty, List<Long>> valueMap = (Map<PhysicalDisk.PhysicalDiskProperty, List<Long>>)instanceValuePair.getB();
/* 223 */     stats.timeStamp = System.currentTimeMillis();
/* 224 */     List<Long> readList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKREADSPERSEC);
/* 225 */     List<Long> readByteList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKREADBYTESPERSEC);
/* 226 */     List<Long> writeList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITESPERSEC);
/* 227 */     List<Long> writeByteList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.DISKWRITEBYTESPERSEC);
/* 228 */     List<Long> queueLengthList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.CURRENTDISKQUEUELENGTH);
/* 229 */     List<Long> idleTimeList = valueMap.get(PhysicalDisk.PhysicalDiskProperty.PERCENTIDLETIME);
/*     */     
/* 231 */     if (instances.isEmpty() || readList == null || readByteList == null || writeList == null || writeByteList == null || queueLengthList == null || idleTimeList == null)
/*     */     {
/* 233 */       return stats;
/*     */     }
/* 235 */     for (int i = 0; i < instances.size(); i++) {
/* 236 */       String name = getIndexFromName(instances.get(i));
/*     */       
/* 238 */       if (index == null || index.equals(name)) {
/*     */ 
/*     */         
/* 241 */         stats.readMap.put(name, readList.get(i));
/* 242 */         stats.readByteMap.put(name, readByteList.get(i));
/* 243 */         stats.writeMap.put(name, writeList.get(i));
/* 244 */         stats.writeByteMap.put(name, writeByteList.get(i));
/* 245 */         stats.queueLengthMap.put(name, queueLengthList.get(i));
/* 246 */         stats.idleTimeMap.put(name, Long.valueOf(((Long)idleTimeList.get(i)).longValue() / 10000L));
/*     */       } 
/* 248 */     }  return stats;
/*     */   }
/*     */ 
/*     */   
/*     */   private static PartitionMaps queryPartitionMaps() {
/* 253 */     PartitionMaps maps = new PartitionMaps();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 260 */     WbemcliUtil.WmiResult<Win32DiskDriveToDiskPartition.DriveToPartitionProperty> drivePartitionMap = Win32DiskDriveToDiskPartition.queryDriveToPartition();
/* 261 */     for (int i = 0; i < drivePartitionMap.getResultCount(); i++) {
/* 262 */       Matcher mAnt = DEVICE_ID.matcher(WmiUtil.getRefString(drivePartitionMap, (Enum)Win32DiskDriveToDiskPartition.DriveToPartitionProperty.ANTECEDENT, i));
/* 263 */       Matcher mDep = DEVICE_ID.matcher(WmiUtil.getRefString(drivePartitionMap, (Enum)Win32DiskDriveToDiskPartition.DriveToPartitionProperty.DEPENDENT, i));
/* 264 */       if (mAnt.matches() && mDep.matches()) {
/* 265 */         ((List<String>)maps.driveToPartitionMap.computeIfAbsent(mAnt.group(1).replace("\\\\", "\\"), x -> new ArrayList()))
/* 266 */           .add(mDep.group(1));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 271 */     WbemcliUtil.WmiResult<Win32LogicalDiskToPartition.DiskToPartitionProperty> diskPartitionMap = Win32LogicalDiskToPartition.queryDiskToPartition();
/* 272 */     for (int j = 0; j < diskPartitionMap.getResultCount(); j++) {
/* 273 */       Matcher mAnt = DEVICE_ID.matcher(WmiUtil.getRefString(diskPartitionMap, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.ANTECEDENT, j));
/* 274 */       Matcher mDep = DEVICE_ID.matcher(WmiUtil.getRefString(diskPartitionMap, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.DEPENDENT, j));
/*     */       
/* 276 */       long size = WmiUtil.getUint64(diskPartitionMap, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.ENDINGADDRESS, j) - WmiUtil.getUint64(diskPartitionMap, (Enum)Win32LogicalDiskToPartition.DiskToPartitionProperty.STARTINGADDRESS, j) + 1L;
/* 277 */       if (mAnt.matches() && mDep.matches()) {
/* 278 */         if (maps.partitionToLogicalDriveMap.containsKey(mAnt.group(1))) {
/* 279 */           ((List<Pair>)maps.partitionToLogicalDriveMap.get(mAnt.group(1))).add(new Pair(mDep.group(1) + "\\", Long.valueOf(size)));
/*     */         } else {
/* 281 */           List<Pair<String, Long>> list = new ArrayList<>();
/* 282 */           list.add(new Pair(mDep.group(1) + "\\", Long.valueOf(size)));
/* 283 */           maps.partitionToLogicalDriveMap.put(mAnt.group(1), list);
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 289 */     WbemcliUtil.WmiResult<Win32DiskPartition.DiskPartitionProperty> hwPartitionQueryMap = Win32DiskPartition.queryPartition();
/* 290 */     for (int k = 0; k < hwPartitionQueryMap.getResultCount(); k++) {
/* 291 */       String deviceID = WmiUtil.getString(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.DEVICEID, k);
/* 292 */       List<Pair<String, Long>> logicalDrives = (List<Pair<String, Long>>)maps.partitionToLogicalDriveMap.get(deviceID);
/* 293 */       if (logicalDrives != null)
/*     */       {
/*     */         
/* 296 */         for (int m = 0; m < logicalDrives.size(); m++) {
/* 297 */           Pair<String, Long> logicalDrive = logicalDrives.get(m);
/* 298 */           if (logicalDrive != null && !((String)logicalDrive.getA()).isEmpty()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 306 */             HWPartition pt = new HWPartition(WmiUtil.getString(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.NAME, k), WmiUtil.getString(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.TYPE, k), WmiUtil.getString(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.DESCRIPTION, k), "", ((Long)logicalDrive.getB()).longValue(), WmiUtil.getUint32(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.DISKINDEX, k), WmiUtil.getUint32(hwPartitionQueryMap, (Enum)Win32DiskPartition.DiskPartitionProperty.INDEX, k), (String)logicalDrive.getA());
/* 307 */             if (maps.partitionMap.containsKey(deviceID)) {
/* 308 */               ((List<HWPartition>)maps.partitionMap.get(deviceID)).add(pt);
/*     */             } else {
/* 310 */               List<HWPartition> ptlist = new ArrayList<>();
/* 311 */               ptlist.add(pt);
/* 312 */               maps.partitionMap.put(deviceID, ptlist);
/*     */             } 
/*     */           } 
/*     */         }  } 
/*     */     } 
/* 317 */     return maps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getIndexFromName(String s) {
/* 328 */     if (s.isEmpty()) {
/* 329 */       return s;
/*     */     }
/* 331 */     return s.split("\\s")[0];
/*     */   }
/*     */   
/*     */   private static final class DiskStats
/*     */   {
/*     */     private DiskStats() {}
/*     */     
/* 338 */     private final Map<String, Long> readMap = new HashMap<>();
/* 339 */     private final Map<String, Long> readByteMap = new HashMap<>();
/* 340 */     private final Map<String, Long> writeMap = new HashMap<>();
/* 341 */     private final Map<String, Long> writeByteMap = new HashMap<>();
/* 342 */     private final Map<String, Long> queueLengthMap = new HashMap<>();
/* 343 */     private final Map<String, Long> idleTimeMap = new HashMap<>();
/*     */     private long timeStamp;
/*     */   }
/*     */   
/*     */   private static final class PartitionMaps {
/*     */     private final Map<String, List<String>> driveToPartitionMap;
/*     */     
/*     */     private PartitionMaps() {
/* 351 */       this.driveToPartitionMap = new HashMap<>();
/* 352 */       this.partitionToLogicalDriveMap = new HashMap<>();
/* 353 */       this.partitionMap = new HashMap<>();
/*     */     }
/*     */     
/*     */     private final Map<String, List<Pair<String, Long>>> partitionToLogicalDriveMap;
/*     */     private final Map<String, List<HWPartition>> partitionMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */