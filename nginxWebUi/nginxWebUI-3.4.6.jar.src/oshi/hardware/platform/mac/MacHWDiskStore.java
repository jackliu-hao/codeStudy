/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.platform.mac.CoreFoundation;
/*     */ import com.sun.jna.platform.mac.DiskArbitration;
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.mac.disk.Diskutil;
/*     */ import oshi.driver.mac.disk.Fsstat;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class MacHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*  68 */   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
/*  69 */   private static final DiskArbitration DA = DiskArbitration.INSTANCE;
/*     */   
/*  71 */   private static final Logger LOG = LoggerFactory.getLogger(MacHWDiskStore.class);
/*     */   
/*  73 */   private long reads = 0L;
/*  74 */   private long readBytes = 0L;
/*  75 */   private long writes = 0L;
/*  76 */   private long writeBytes = 0L;
/*  77 */   private long currentQueueLength = 0L;
/*  78 */   private long transferTime = 0L;
/*  79 */   private long timeStamp = 0L;
/*     */   
/*     */   private List<HWPartition> partitionList;
/*     */   
/*     */   private MacHWDiskStore(String name, String model, String serial, long size, DiskArbitration.DASessionRef session, Map<String, String> mountPointMap, Map<String, String> logicalVolumeMap, Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap) {
/*  84 */     super(name, model, serial, size);
/*  85 */     updateDiskStats(session, mountPointMap, logicalVolumeMap, cfKeyMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/*  90 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/*  95 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/* 100 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/* 105 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/* 110 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/* 115 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 120 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 125 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 131 */     DiskArbitration.DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
/* 132 */     if (session == null) {
/* 133 */       LOG.error("Unable to open session to DiskArbitration framework.");
/* 134 */       return false;
/*     */     } 
/* 136 */     Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap = mapCFKeys();
/*     */     
/* 138 */     boolean diskFound = updateDiskStats(session, Fsstat.queryPartitionToMountMap(), 
/* 139 */         Diskutil.queryLogicalVolumeMap(), cfKeyMap);
/*     */     
/* 141 */     session.release();
/* 142 */     for (CoreFoundation.CFTypeRef value : cfKeyMap.values()) {
/* 143 */       value.release();
/*     */     }
/*     */     
/* 146 */     return diskFound;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean updateDiskStats(DiskArbitration.DASessionRef session, Map<String, String> mountPointMap, Map<String, String> logicalVolumeMap, Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap) {
/* 153 */     String bsdName = getName();
/* 154 */     CoreFoundation.CFMutableDictionaryRef matchingDict = IOKitUtil.getBSDNameMatchingDict(bsdName);
/* 155 */     if (matchingDict != null) {
/*     */       
/* 157 */       IOKit.IOIterator driveListIter = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
/* 158 */       if (driveListIter != null) {
/*     */         
/* 160 */         IOKit.IORegistryEntry drive = driveListIter.next();
/*     */         
/* 162 */         if (drive != null) {
/*     */ 
/*     */ 
/*     */           
/* 166 */           if (drive.conformsTo("IOMedia")) {
/* 167 */             IOKit.IORegistryEntry parent = drive.getParentEntry("IOService");
/* 168 */             if (parent != null && parent.conformsTo("IOBlockStorageDriver")) {
/* 169 */               CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = parent.createCFProperties();
/*     */ 
/*     */               
/* 172 */               Pointer pointer = cFMutableDictionaryRef.getValue((PointerType)cfKeyMap.get(CFKey.STATISTICS));
/* 173 */               CoreFoundation.CFDictionaryRef statistics = new CoreFoundation.CFDictionaryRef(pointer);
/* 174 */               this.timeStamp = System.currentTimeMillis();
/*     */ 
/*     */               
/* 177 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.READ_OPS));
/* 178 */               CoreFoundation.CFNumberRef stat = new CoreFoundation.CFNumberRef(pointer);
/* 179 */               this.reads = stat.longValue();
/* 180 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.READ_BYTES));
/* 181 */               stat.setPointer(pointer);
/* 182 */               this.readBytes = stat.longValue();
/*     */               
/* 184 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.WRITE_OPS));
/* 185 */               stat.setPointer(pointer);
/* 186 */               this.writes = stat.longValue();
/* 187 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.WRITE_BYTES));
/* 188 */               stat.setPointer(pointer);
/* 189 */               this.writeBytes = stat.longValue();
/*     */ 
/*     */ 
/*     */               
/* 193 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.READ_TIME));
/* 194 */               stat.setPointer(pointer);
/* 195 */               long xferTime = stat.longValue();
/* 196 */               pointer = statistics.getValue((PointerType)cfKeyMap.get(CFKey.WRITE_TIME));
/* 197 */               stat.setPointer(pointer);
/* 198 */               xferTime += stat.longValue();
/* 199 */               this.transferTime = xferTime / 1000000L;
/*     */               
/* 201 */               cFMutableDictionaryRef.release();
/*     */             }
/*     */             else {
/*     */               
/* 205 */               LOG.debug("Unable to find block storage driver properties for {}", bsdName);
/*     */             } 
/*     */             
/* 208 */             List<HWPartition> partitions = new ArrayList<>();
/*     */             
/* 210 */             CoreFoundation.CFMutableDictionaryRef properties = drive.createCFProperties();
/*     */             
/* 212 */             Pointer result = properties.getValue((PointerType)cfKeyMap.get(CFKey.BSD_UNIT));
/* 213 */             CoreFoundation.CFNumberRef bsdUnit = new CoreFoundation.CFNumberRef(result);
/*     */ 
/*     */ 
/*     */             
/* 217 */             result = properties.getValue((PointerType)cfKeyMap.get(CFKey.LEAF));
/* 218 */             CoreFoundation.CFBooleanRef cfFalse = new CoreFoundation.CFBooleanRef(result);
/*     */             
/* 220 */             CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */             
/* 222 */             propertyDict.setValue((PointerType)cfKeyMap.get(CFKey.BSD_UNIT), (PointerType)bsdUnit);
/* 223 */             propertyDict.setValue((PointerType)cfKeyMap.get(CFKey.WHOLE), (PointerType)cfFalse);
/* 224 */             matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */             
/* 226 */             matchingDict.setValue((PointerType)cfKeyMap.get(CFKey.IO_PROPERTY_MATCH), (PointerType)propertyDict);
/*     */ 
/*     */ 
/*     */             
/* 230 */             IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
/*     */             
/* 232 */             properties.release();
/* 233 */             propertyDict.release();
/*     */             
/* 235 */             if (serviceIterator != null) {
/*     */               
/* 237 */               IOKit.IORegistryEntry sdService = IOKit.INSTANCE.IOIteratorNext(serviceIterator);
/* 238 */               while (sdService != null) {
/*     */                 
/* 240 */                 String mountPoint, partBsdName = sdService.getStringProperty("BSD Name");
/* 241 */                 String name = partBsdName;
/* 242 */                 String type = "";
/*     */ 
/*     */                 
/* 245 */                 DiskArbitration.DADiskRef disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, partBsdName);
/*     */                 
/* 247 */                 if (disk != null) {
/* 248 */                   CoreFoundation.CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
/* 249 */                   if (diskInfo != null) {
/*     */                     
/* 251 */                     result = diskInfo.getValue((PointerType)cfKeyMap.get(CFKey.DA_MEDIA_NAME));
/* 252 */                     CoreFoundation.CFStringRef volumePtr = new CoreFoundation.CFStringRef(result);
/* 253 */                     type = volumePtr.stringValue();
/* 254 */                     if (type == null) {
/* 255 */                       type = "unknown";
/*     */                     }
/* 257 */                     result = diskInfo.getValue((PointerType)cfKeyMap.get(CFKey.DA_VOLUME_NAME));
/* 258 */                     if (result == null) {
/* 259 */                       name = type;
/*     */                     } else {
/* 261 */                       volumePtr.setPointer(result);
/* 262 */                       name = volumePtr.stringValue();
/*     */                     } 
/* 264 */                     diskInfo.release();
/*     */                   } 
/* 266 */                   disk.release();
/*     */                 } 
/*     */                 
/* 269 */                 if (logicalVolumeMap.containsKey(partBsdName)) {
/* 270 */                   mountPoint = "Logical Volume: " + (String)logicalVolumeMap.get(partBsdName);
/*     */                 } else {
/* 272 */                   mountPoint = mountPointMap.getOrDefault(partBsdName, "");
/*     */                 } 
/* 274 */                 Long size = sdService.getLongProperty("Size");
/* 275 */                 Integer bsdMajor = sdService.getIntegerProperty("BSD Major");
/* 276 */                 Integer bsdMinor = sdService.getIntegerProperty("BSD Minor");
/* 277 */                 String uuid = sdService.getStringProperty("UUID");
/* 278 */                 partitions.add(new HWPartition(partBsdName, name, type, 
/* 279 */                       (uuid == null) ? "unknown" : uuid, (size == null) ? 0L : size.longValue(), 
/* 280 */                       (bsdMajor == null) ? 0 : bsdMajor.intValue(), (bsdMinor == null) ? 0 : bsdMinor.intValue(), mountPoint));
/*     */                 
/* 282 */                 sdService.release();
/* 283 */                 sdService = IOKit.INSTANCE.IOIteratorNext(serviceIterator);
/*     */               } 
/* 285 */               serviceIterator.release();
/*     */             } 
/* 287 */             this.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)partitions.stream()
/* 288 */                 .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
/* 289 */             if (parent != null) {
/* 290 */               parent.release();
/*     */             }
/*     */           } else {
/* 293 */             LOG.error("Unable to find IOMedia device or parent for {}", bsdName);
/*     */           } 
/* 295 */           drive.release();
/*     */         } 
/* 297 */         driveListIter.release();
/* 298 */         return true;
/*     */       } 
/*     */     } 
/* 301 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HWDiskStore> getDisks() {
/* 311 */     Map<String, String> mountPointMap = Fsstat.queryPartitionToMountMap();
/* 312 */     Map<String, String> logicalVolumeMap = Diskutil.queryLogicalVolumeMap();
/* 313 */     Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap = mapCFKeys();
/*     */     
/* 315 */     List<HWDiskStore> diskList = new ArrayList<>();
/*     */ 
/*     */     
/* 318 */     DiskArbitration.DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
/* 319 */     if (session == null) {
/* 320 */       LOG.error("Unable to open session to DiskArbitration framework.");
/* 321 */       return Collections.emptyList();
/*     */     } 
/*     */ 
/*     */     
/* 325 */     List<String> bsdNames = new ArrayList<>();
/* 326 */     IOKit.IOIterator iter = IOKitUtil.getMatchingServices("IOMedia");
/* 327 */     if (iter != null) {
/* 328 */       IOKit.IORegistryEntry media = iter.next();
/* 329 */       while (media != null) {
/* 330 */         Boolean whole = media.getBooleanProperty("Whole");
/* 331 */         if (whole != null && whole.booleanValue()) {
/* 332 */           DiskArbitration.DADiskRef disk = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, (IOKit.IOObject)media);
/* 333 */           bsdNames.add(DA.DADiskGetBSDName(disk));
/* 334 */           disk.release();
/*     */         } 
/* 336 */         media.release();
/* 337 */         media = iter.next();
/*     */       } 
/* 339 */       iter.release();
/*     */     } 
/*     */ 
/*     */     
/* 343 */     for (String bsdName : bsdNames) {
/* 344 */       String model = "";
/* 345 */       String serial = "";
/* 346 */       long size = 0L;
/*     */ 
/*     */       
/* 349 */       String path = "/dev/" + bsdName;
/*     */ 
/*     */ 
/*     */       
/* 353 */       DiskArbitration.DADiskRef disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, path);
/* 354 */       if (disk != null) {
/* 355 */         CoreFoundation.CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
/* 356 */         if (diskInfo != null) {
/*     */           
/* 358 */           Pointer result = diskInfo.getValue((PointerType)cfKeyMap.get(CFKey.DA_DEVICE_MODEL));
/* 359 */           CoreFoundation.CFStringRef modelPtr = new CoreFoundation.CFStringRef(result);
/* 360 */           model = modelPtr.stringValue();
/* 361 */           if (model == null) {
/* 362 */             model = "unknown";
/*     */           }
/* 364 */           result = diskInfo.getValue((PointerType)cfKeyMap.get(CFKey.DA_MEDIA_SIZE));
/* 365 */           CoreFoundation.CFNumberRef sizePtr = new CoreFoundation.CFNumberRef(result);
/* 366 */           size = sizePtr.longValue();
/* 367 */           diskInfo.release();
/*     */ 
/*     */           
/* 370 */           if (!"Disk Image".equals(model)) {
/* 371 */             CoreFoundation.CFStringRef modelNameRef = CoreFoundation.CFStringRef.createCFString(model);
/* 372 */             CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */             
/* 374 */             propertyDict.setValue((PointerType)cfKeyMap.get(CFKey.MODEL), (PointerType)modelNameRef);
/* 375 */             CoreFoundation.CFMutableDictionaryRef matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */             
/* 377 */             matchingDict.setValue((PointerType)cfKeyMap.get(CFKey.IO_PROPERTY_MATCH), (PointerType)propertyDict);
/*     */ 
/*     */             
/* 380 */             IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
/*     */             
/* 382 */             modelNameRef.release();
/* 383 */             propertyDict.release();
/*     */             
/* 385 */             if (serviceIterator != null) {
/* 386 */               IOKit.IORegistryEntry sdService = serviceIterator.next();
/* 387 */               while (sdService != null) {
/*     */                 
/* 389 */                 serial = sdService.getStringProperty("Serial Number");
/* 390 */                 sdService.release();
/* 391 */                 if (serial != null) {
/*     */                   break;
/*     */                 }
/*     */                 
/* 395 */                 sdService.release();
/* 396 */                 sdService = serviceIterator.next();
/*     */               } 
/* 398 */               serviceIterator.release();
/*     */             } 
/* 400 */             if (serial == null) {
/* 401 */               serial = "";
/*     */             }
/*     */           } 
/*     */         } 
/* 405 */         disk.release();
/*     */ 
/*     */         
/* 408 */         if (size <= 0L) {
/*     */           continue;
/*     */         }
/* 411 */         MacHWDiskStore macHWDiskStore = new MacHWDiskStore(bsdName, model.trim(), serial.trim(), size, session, mountPointMap, logicalVolumeMap, cfKeyMap);
/*     */         
/* 413 */         diskList.add(macHWDiskStore);
/*     */       } 
/*     */     } 
/*     */     
/* 417 */     session.release();
/* 418 */     for (CoreFoundation.CFTypeRef value : cfKeyMap.values()) {
/* 419 */       value.release();
/*     */     }
/* 421 */     return Collections.unmodifiableList(diskList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<CFKey, CoreFoundation.CFStringRef> mapCFKeys() {
/* 432 */     Map<CFKey, CoreFoundation.CFStringRef> keyMap = new EnumMap<>(CFKey.class);
/* 433 */     for (CFKey cfKey : CFKey.values()) {
/* 434 */       keyMap.put(cfKey, CoreFoundation.CFStringRef.createCFString(cfKey.getKey()));
/*     */     }
/* 436 */     return keyMap;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private enum CFKey
/*     */   {
/* 443 */     IO_PROPERTY_MATCH("IOPropertyMatch"),
/*     */     
/* 445 */     STATISTICS("Statistics"),
/* 446 */     READ_OPS("Operations (Read)"), READ_BYTES("Bytes (Read)"), READ_TIME("Total Time (Read)"),
/* 447 */     WRITE_OPS("Operations (Write)"), WRITE_BYTES("Bytes (Write)"), WRITE_TIME("Total Time (Write)"),
/*     */     
/* 449 */     BSD_UNIT("BSD Unit"), LEAF("Leaf"), WHOLE("Whole"),
/*     */     
/* 451 */     DA_MEDIA_NAME("DAMediaName"), DA_VOLUME_NAME("DAVolumeName"), DA_MEDIA_SIZE("DAMediaSize"),
/* 452 */     DA_DEVICE_MODEL("DADeviceModel"), MODEL("Model");
/*     */     
/*     */     private final String key;
/*     */     
/*     */     CFKey(String key) {
/* 457 */       this.key = key;
/*     */     }
/*     */     
/*     */     public String getKey() {
/* 461 */       return this.key;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */