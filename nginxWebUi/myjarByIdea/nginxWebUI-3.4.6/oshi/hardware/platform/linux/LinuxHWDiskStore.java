package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.common.AbstractHWDiskStore;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class LinuxHWDiskStore extends AbstractHWDiskStore {
   private static final String BLOCK = "block";
   private static final String DISK = "disk";
   private static final String PARTITION = "partition";
   private static final String STAT = "stat";
   private static final String SIZE = "size";
   private static final String MINOR = "MINOR";
   private static final String MAJOR = "MAJOR";
   private static final String ID_FS_TYPE = "ID_FS_TYPE";
   private static final String ID_FS_UUID = "ID_FS_UUID";
   private static final String ID_MODEL = "ID_MODEL";
   private static final String ID_SERIAL_SHORT = "ID_SERIAL_SHORT";
   private static final int SECTORSIZE = 512;
   private static final int[] UDEV_STAT_ORDERS = new int[LinuxHWDiskStore.UdevStat.values().length];
   private static final int UDEV_STAT_LENGTH;
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private long timeStamp = 0L;
   private List<HWPartition> partitionList = new ArrayList();

   private LinuxHWDiskStore(String name, String model, String serial, long size) {
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

   public static List<HWDiskStore> getDisks() {
      return Collections.unmodifiableList(getDisks((LinuxHWDiskStore)null));
   }

   private static List<LinuxHWDiskStore> getDisks(LinuxHWDiskStore storeToUpdate) {
      LinuxHWDiskStore store = null;
      List<LinuxHWDiskStore> result = new ArrayList();
      Map<String, String> mountsMap = readMountsMap();
      Udev.UdevContext udev = Udev.INSTANCE.udev_new();

      try {
         Udev.UdevEnumerate enumerate = udev.enumerateNew();

         try {
            enumerate.addMatchSubsystem("block");
            enumerate.scanDevices();

            for(Udev.UdevListEntry entry = enumerate.getListEntry(); entry != null; entry = entry.getNext()) {
               String syspath = entry.getName();
               Udev.UdevDevice device = udev.deviceNewFromSyspath(syspath);
               if (device != null) {
                  try {
                     String devnode = device.getDevnode();
                     if (devnode != null && !devnode.startsWith("/dev/loop") && !devnode.startsWith("/dev/ram")) {
                        String name;
                        if ("disk".equals(device.getDevtype())) {
                           String devModel = device.getPropertyValue("ID_MODEL");
                           name = device.getPropertyValue("ID_SERIAL_SHORT");
                           long devSize = ParseUtil.parseLongOrDefault(device.getSysattrValue("size"), 0L) * 512L;
                           store = new LinuxHWDiskStore(devnode, devModel == null ? "unknown" : devModel, name == null ? "unknown" : name, devSize);
                           if (storeToUpdate == null) {
                              computeDiskStats(store, device.getSysattrValue("stat"));
                              result.add(store);
                           } else if (store.getName().equals(storeToUpdate.getName()) && store.getModel().equals(storeToUpdate.getModel()) && store.getSerial().equals(storeToUpdate.getSerial()) && store.getSize() == storeToUpdate.getSize()) {
                              computeDiskStats(storeToUpdate, device.getSysattrValue("stat"));
                              result.add(storeToUpdate);
                              break;
                           }
                        } else if (storeToUpdate == null && store != null && "partition".equals(device.getDevtype())) {
                           Udev.UdevDevice parent = device.getParentWithSubsystemDevtype("block", "disk");
                           if (parent != null && store.getName().equals(parent.getDevnode())) {
                              name = device.getDevnode();
                              store.partitionList.add(new HWPartition(name, device.getSysname(), device.getPropertyValue("ID_FS_TYPE") == null ? "partition" : device.getPropertyValue("ID_FS_TYPE"), device.getPropertyValue("ID_FS_UUID") == null ? "" : device.getPropertyValue("ID_FS_UUID"), ParseUtil.parseLongOrDefault(device.getSysattrValue("size"), 0L) * 512L, ParseUtil.parseIntOrDefault(device.getPropertyValue("MAJOR"), 0), ParseUtil.parseIntOrDefault(device.getPropertyValue("MINOR"), 0), (String)mountsMap.getOrDefault(name, "")));
                           }
                        }
                     }
                  } finally {
                     device.unref();
                  }
               }
            }
         } finally {
            enumerate.unref();
         }
      } finally {
         udev.unref();
      }

      LinuxHWDiskStore hwds;
      for(Iterator var29 = result.iterator(); var29.hasNext(); hwds.partitionList = Collections.unmodifiableList((List)hwds.partitionList.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()))) {
         hwds = (LinuxHWDiskStore)var29.next();
      }

      return result;
   }

   public boolean updateAttributes() {
      return !getDisks(this).isEmpty();
   }

   private static Map<String, String> readMountsMap() {
      Map<String, String> mountsMap = new HashMap();
      List<String> mounts = FileUtil.readFile(ProcPath.MOUNTS);
      Iterator var2 = mounts.iterator();

      while(var2.hasNext()) {
         String mount = (String)var2.next();
         String[] split = ParseUtil.whitespaces.split(mount);
         if (split.length >= 2 && split[0].startsWith("/dev/")) {
            mountsMap.put(split[0], split[1]);
         }
      }

      return mountsMap;
   }

   private static void computeDiskStats(LinuxHWDiskStore store, String devstat) {
      long[] devstatArray = ParseUtil.parseStringToLongArray(devstat, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, ' ');
      store.timeStamp = System.currentTimeMillis();
      store.reads = devstatArray[LinuxHWDiskStore.UdevStat.READS.ordinal()];
      store.readBytes = devstatArray[LinuxHWDiskStore.UdevStat.READ_BYTES.ordinal()] * 512L;
      store.writes = devstatArray[LinuxHWDiskStore.UdevStat.WRITES.ordinal()];
      store.writeBytes = devstatArray[LinuxHWDiskStore.UdevStat.WRITE_BYTES.ordinal()] * 512L;
      store.currentQueueLength = devstatArray[LinuxHWDiskStore.UdevStat.QUEUE_LENGTH.ordinal()];
      store.transferTime = devstatArray[LinuxHWDiskStore.UdevStat.ACTIVE_MS.ordinal()];
   }

   static {
      UdevStat[] var0 = LinuxHWDiskStore.UdevStat.values();
      int statLength = var0.length;

      for(int var2 = 0; var2 < statLength; ++var2) {
         UdevStat stat = var0[var2];
         UDEV_STAT_ORDERS[stat.ordinal()] = stat.getOrder();
      }

      String stat = FileUtil.getStringFromFile(ProcPath.DISKSTATS);
      statLength = 11;
      if (!stat.isEmpty()) {
         statLength = ParseUtil.countStringToLongArray(stat, ' ');
      }

      UDEV_STAT_LENGTH = statLength;
   }

   static enum UdevStat {
      READS(0),
      READ_BYTES(2),
      WRITES(4),
      WRITE_BYTES(6),
      QUEUE_LENGTH(8),
      ACTIVE_MS(9);

      private int order;

      public int getOrder() {
         return this.order;
      }

      private UdevStat(int order) {
         this.order = order;
      }
   }
}
