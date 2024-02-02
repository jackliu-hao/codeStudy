package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.disk.Diskutil;
import oshi.driver.mac.disk.Fsstat;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;

@ThreadSafe
public final class MacHWDiskStore extends AbstractHWDiskStore {
   private static final CoreFoundation CF;
   private static final DiskArbitration DA;
   private static final Logger LOG;
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList;

   private MacHWDiskStore(String name, String model, String serial, long size, DiskArbitration.DASessionRef session, Map<String, String> mountPointMap, Map<String, String> logicalVolumeMap, Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap) {
      super(name, model, serial, size);
      this.updateDiskStats(session, mountPointMap, logicalVolumeMap, cfKeyMap);
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
      DiskArbitration.DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (session == null) {
         LOG.error("Unable to open session to DiskArbitration framework.");
         return false;
      } else {
         Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap = mapCFKeys();
         boolean diskFound = this.updateDiskStats(session, Fsstat.queryPartitionToMountMap(), Diskutil.queryLogicalVolumeMap(), cfKeyMap);
         session.release();
         Iterator var4 = cfKeyMap.values().iterator();

         while(var4.hasNext()) {
            CoreFoundation.CFTypeRef value = (CoreFoundation.CFTypeRef)var4.next();
            value.release();
         }

         return diskFound;
      }
   }

   private boolean updateDiskStats(DiskArbitration.DASessionRef session, Map<String, String> mountPointMap, Map<String, String> logicalVolumeMap, Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap) {
      String bsdName = this.getName();
      CoreFoundation.CFMutableDictionaryRef matchingDict = IOKitUtil.getBSDNameMatchingDict(bsdName);
      if (matchingDict != null) {
         IOKit.IOIterator driveListIter = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
         if (driveListIter != null) {
            IOKit.IORegistryEntry drive = driveListIter.next();
            if (drive != null) {
               if (!drive.conformsTo("IOMedia")) {
                  LOG.error((String)"Unable to find IOMedia device or parent for {}", (Object)bsdName);
               } else {
                  IOKit.IORegistryEntry parent = drive.getParentEntry("IOService");
                  CoreFoundation.CFNumberRef bsdUnit;
                  if (parent != null && parent.conformsTo("IOBlockStorageDriver")) {
                     CoreFoundation.CFMutableDictionaryRef properties = parent.createCFProperties();
                     Pointer result = properties.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.STATISTICS));
                     CoreFoundation.CFDictionaryRef statistics = new CoreFoundation.CFDictionaryRef(result);
                     this.timeStamp = System.currentTimeMillis();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.READ_OPS));
                     bsdUnit = new CoreFoundation.CFNumberRef(result);
                     this.reads = bsdUnit.longValue();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.READ_BYTES));
                     bsdUnit.setPointer(result);
                     this.readBytes = bsdUnit.longValue();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.WRITE_OPS));
                     bsdUnit.setPointer(result);
                     this.writes = bsdUnit.longValue();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.WRITE_BYTES));
                     bsdUnit.setPointer(result);
                     this.writeBytes = bsdUnit.longValue();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.READ_TIME));
                     bsdUnit.setPointer(result);
                     long xferTime = bsdUnit.longValue();
                     result = statistics.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.WRITE_TIME));
                     bsdUnit.setPointer(result);
                     xferTime += bsdUnit.longValue();
                     this.transferTime = xferTime / 1000000L;
                     properties.release();
                  } else {
                     LOG.debug((String)"Unable to find block storage driver properties for {}", (Object)bsdName);
                  }

                  List<HWPartition> partitions = new ArrayList();
                  CoreFoundation.CFMutableDictionaryRef properties = drive.createCFProperties();
                  Pointer result = properties.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.BSD_UNIT));
                  bsdUnit = new CoreFoundation.CFNumberRef(result);
                  result = properties.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.LEAF));
                  CoreFoundation.CFBooleanRef cfFalse = new CoreFoundation.CFBooleanRef(result);
                  CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
                  propertyDict.setValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.BSD_UNIT), bsdUnit);
                  propertyDict.setValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.WHOLE), cfFalse);
                  matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
                  matchingDict.setValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.IO_PROPERTY_MATCH), propertyDict);
                  IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
                  properties.release();
                  propertyDict.release();
                  if (serviceIterator != null) {
                     for(IOKit.IORegistryEntry sdService = IOKit.INSTANCE.IOIteratorNext(serviceIterator); sdService != null; sdService = IOKit.INSTANCE.IOIteratorNext(serviceIterator)) {
                        String partBsdName = sdService.getStringProperty("BSD Name");
                        String name = partBsdName;
                        String type = "";
                        DiskArbitration.DADiskRef disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, partBsdName);
                        if (disk != null) {
                           CoreFoundation.CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
                           if (diskInfo != null) {
                              result = diskInfo.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.DA_MEDIA_NAME));
                              CoreFoundation.CFStringRef volumePtr = new CoreFoundation.CFStringRef(result);
                              type = volumePtr.stringValue();
                              if (type == null) {
                                 type = "unknown";
                              }

                              result = diskInfo.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.DA_VOLUME_NAME));
                              if (result == null) {
                                 name = type;
                              } else {
                                 volumePtr.setPointer(result);
                                 name = volumePtr.stringValue();
                              }

                              diskInfo.release();
                           }

                           disk.release();
                        }

                        String mountPoint;
                        if (logicalVolumeMap.containsKey(partBsdName)) {
                           mountPoint = "Logical Volume: " + (String)logicalVolumeMap.get(partBsdName);
                        } else {
                           mountPoint = (String)mountPointMap.getOrDefault(partBsdName, "");
                        }

                        Long size = sdService.getLongProperty("Size");
                        Integer bsdMajor = sdService.getIntegerProperty("BSD Major");
                        Integer bsdMinor = sdService.getIntegerProperty("BSD Minor");
                        String uuid = sdService.getStringProperty("UUID");
                        partitions.add(new HWPartition(partBsdName, name, type, uuid == null ? "unknown" : uuid, size == null ? 0L : size, bsdMajor == null ? 0 : bsdMajor, bsdMinor == null ? 0 : bsdMinor, mountPoint));
                        sdService.release();
                     }

                     serviceIterator.release();
                  }

                  this.partitionList = Collections.unmodifiableList((List)partitions.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
                  if (parent != null) {
                     parent.release();
                  }
               }

               drive.release();
            }

            driveListIter.release();
            return true;
         }
      }

      return false;
   }

   public static List<HWDiskStore> getDisks() {
      Map<String, String> mountPointMap = Fsstat.queryPartitionToMountMap();
      Map<String, String> logicalVolumeMap = Diskutil.queryLogicalVolumeMap();
      Map<CFKey, CoreFoundation.CFStringRef> cfKeyMap = mapCFKeys();
      List<HWDiskStore> diskList = new ArrayList();
      DiskArbitration.DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
      if (session == null) {
         LOG.error("Unable to open session to DiskArbitration framework.");
         return Collections.emptyList();
      } else {
         List<String> bsdNames = new ArrayList();
         IOKit.IOIterator iter = IOKitUtil.getMatchingServices("IOMedia");
         if (iter != null) {
            for(IOKit.IORegistryEntry media = iter.next(); media != null; media = iter.next()) {
               Boolean whole = media.getBooleanProperty("Whole");
               if (whole != null && whole) {
                  DiskArbitration.DADiskRef disk = DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, media);
                  bsdNames.add(DA.DADiskGetBSDName(disk));
                  disk.release();
               }

               media.release();
            }

            iter.release();
         }

         Iterator var24 = bsdNames.iterator();

         while(true) {
            String serial;
            long size;
            DiskArbitration.DADiskRef disk;
            String bsdName;
            String model;
            do {
               if (!var24.hasNext()) {
                  session.release();
                  var24 = cfKeyMap.values().iterator();

                  while(var24.hasNext()) {
                     CoreFoundation.CFTypeRef value = (CoreFoundation.CFTypeRef)var24.next();
                     value.release();
                  }

                  return Collections.unmodifiableList(diskList);
               }

               bsdName = (String)var24.next();
               model = "";
               serial = "";
               size = 0L;
               String path = "/dev/" + bsdName;
               disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, path);
            } while(disk == null);

            CoreFoundation.CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
            if (diskInfo != null) {
               Pointer result = diskInfo.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.DA_DEVICE_MODEL));
               CoreFoundation.CFStringRef modelPtr = new CoreFoundation.CFStringRef(result);
               model = modelPtr.stringValue();
               if (model == null) {
                  model = "unknown";
               }

               result = diskInfo.getValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.DA_MEDIA_SIZE));
               CoreFoundation.CFNumberRef sizePtr = new CoreFoundation.CFNumberRef(result);
               size = sizePtr.longValue();
               diskInfo.release();
               if (!"Disk Image".equals(model)) {
                  CoreFoundation.CFStringRef modelNameRef = CoreFoundation.CFStringRef.createCFString(model);
                  CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
                  propertyDict.setValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.MODEL), modelNameRef);
                  CoreFoundation.CFMutableDictionaryRef matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
                  matchingDict.setValue((PointerType)cfKeyMap.get(MacHWDiskStore.CFKey.IO_PROPERTY_MATCH), propertyDict);
                  IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
                  modelNameRef.release();
                  propertyDict.release();
                  if (serviceIterator != null) {
                     for(IOKit.IORegistryEntry sdService = serviceIterator.next(); sdService != null; sdService = serviceIterator.next()) {
                        serial = sdService.getStringProperty("Serial Number");
                        sdService.release();
                        if (serial != null) {
                           break;
                        }

                        sdService.release();
                     }

                     serviceIterator.release();
                  }

                  if (serial == null) {
                     serial = "";
                  }
               }
            }

            disk.release();
            if (size > 0L) {
               HWDiskStore diskStore = new MacHWDiskStore(bsdName, model.trim(), serial.trim(), size, session, mountPointMap, logicalVolumeMap, cfKeyMap);
               diskList.add(diskStore);
            }
         }
      }
   }

   private static Map<CFKey, CoreFoundation.CFStringRef> mapCFKeys() {
      Map<CFKey, CoreFoundation.CFStringRef> keyMap = new EnumMap(CFKey.class);
      CFKey[] var1 = MacHWDiskStore.CFKey.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CFKey cfKey = var1[var3];
         keyMap.put(cfKey, CoreFoundation.CFStringRef.createCFString(cfKey.getKey()));
      }

      return keyMap;
   }

   static {
      CF = CoreFoundation.INSTANCE;
      DA = DiskArbitration.INSTANCE;
      LOG = LoggerFactory.getLogger(MacHWDiskStore.class);
   }

   private static enum CFKey {
      IO_PROPERTY_MATCH("IOPropertyMatch"),
      STATISTICS("Statistics"),
      READ_OPS("Operations (Read)"),
      READ_BYTES("Bytes (Read)"),
      READ_TIME("Total Time (Read)"),
      WRITE_OPS("Operations (Write)"),
      WRITE_BYTES("Bytes (Write)"),
      WRITE_TIME("Total Time (Write)"),
      BSD_UNIT("BSD Unit"),
      LEAF("Leaf"),
      WHOLE("Whole"),
      DA_MEDIA_NAME("DAMediaName"),
      DA_VOLUME_NAME("DAVolumeName"),
      DA_MEDIA_SIZE("DAMediaSize"),
      DA_DEVICE_MODEL("DADeviceModel"),
      MODEL("Model");

      private final String key;

      private CFKey(String key) {
         this.key = key;
      }

      public String getKey() {
         return this.key;
      }
   }
}
