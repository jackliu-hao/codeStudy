/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import com.sun.jna.platform.linux.Udev;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ public final class LinuxHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*     */   private static final String BLOCK = "block";
/*     */   private static final String DISK = "disk";
/*     */   private static final String PARTITION = "partition";
/*     */   private static final String STAT = "stat";
/*     */   private static final String SIZE = "size";
/*     */   private static final String MINOR = "MINOR";
/*     */   private static final String MAJOR = "MAJOR";
/*     */   private static final String ID_FS_TYPE = "ID_FS_TYPE";
/*     */   private static final String ID_FS_UUID = "ID_FS_UUID";
/*     */   private static final String ID_MODEL = "ID_MODEL";
/*     */   private static final String ID_SERIAL_SHORT = "ID_SERIAL_SHORT";
/*     */   private static final int SECTORSIZE = 512;
/*  72 */   private static final int[] UDEV_STAT_ORDERS = new int[(UdevStat.values()).length]; private static final int UDEV_STAT_LENGTH;
/*     */   static {
/*  74 */     for (UdevStat udevStat : UdevStat.values()) {
/*  75 */       UDEV_STAT_ORDERS[udevStat.ordinal()] = udevStat.getOrder();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     String stat = FileUtil.getStringFromFile(ProcPath.DISKSTATS);
/*  84 */     int statLength = 11;
/*  85 */     if (!stat.isEmpty()) {
/*  86 */       statLength = ParseUtil.countStringToLongArray(stat, ' ');
/*     */     }
/*  88 */     UDEV_STAT_LENGTH = statLength;
/*     */   }
/*     */   
/*  91 */   private long reads = 0L;
/*  92 */   private long readBytes = 0L;
/*  93 */   private long writes = 0L;
/*  94 */   private long writeBytes = 0L;
/*  95 */   private long currentQueueLength = 0L;
/*  96 */   private long transferTime = 0L;
/*  97 */   private long timeStamp = 0L;
/*  98 */   private List<HWPartition> partitionList = new ArrayList<>();
/*     */   
/*     */   private LinuxHWDiskStore(String name, String model, String serial, long size) {
/* 101 */     super(name, model, serial, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/* 106 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/* 111 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/* 116 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/* 121 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/* 126 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/* 131 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 136 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 141 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HWDiskStore> getDisks() {
/* 151 */     return (List)Collections.unmodifiableList(getDisks((LinuxHWDiskStore)null));
/*     */   }
/*     */   
/*     */   private static List<LinuxHWDiskStore> getDisks(LinuxHWDiskStore storeToUpdate) {
/* 155 */     LinuxHWDiskStore store = null;
/* 156 */     List<LinuxHWDiskStore> result = new ArrayList<>();
/*     */     
/* 158 */     Map<String, String> mountsMap = readMountsMap();
/*     */     
/* 160 */     Udev.UdevContext udev = Udev.INSTANCE.udev_new();
/*     */     try {
/* 162 */       Udev.UdevEnumerate enumerate = udev.enumerateNew();
/*     */       try {
/* 164 */         enumerate.addMatchSubsystem("block");
/* 165 */         enumerate.scanDevices();
/* 166 */         for (Udev.UdevListEntry entry = enumerate.getListEntry(); entry != null; entry = entry.getNext()) {
/* 167 */           String syspath = entry.getName();
/* 168 */           Udev.UdevDevice device = udev.deviceNewFromSyspath(syspath);
/* 169 */           if (device != null) {
/*     */ 
/*     */             
/* 172 */             try { String devnode = device.getDevnode();
/*     */               
/* 174 */               if (devnode != null && !devnode.startsWith("/dev/loop") && 
/* 175 */                 !devnode.startsWith("/dev/ram"))
/* 176 */                 if ("disk".equals(device.getDevtype()))
/*     */                 
/* 178 */                 { String devModel = device.getPropertyValue("ID_MODEL");
/* 179 */                   String devSerial = device.getPropertyValue("ID_SERIAL_SHORT");
/* 180 */                   long devSize = ParseUtil.parseLongOrDefault(device.getSysattrValue("size"), 0L) * 512L;
/*     */ 
/*     */ 
/*     */                   
/* 184 */                   store = new LinuxHWDiskStore(devnode, (devModel == null) ? "unknown" : devModel, (devSerial == null) ? "unknown" : devSerial, devSize);
/* 185 */                   if (storeToUpdate == null)
/*     */                   
/* 187 */                   { computeDiskStats(store, device.getSysattrValue("stat"));
/* 188 */                     result.add(store); }
/* 189 */                   else if (store.getName().equals(storeToUpdate.getName()) && store
/* 190 */                     .getModel().equals(storeToUpdate.getModel()) && store
/* 191 */                     .getSerial().equals(storeToUpdate.getSerial()) && store
/* 192 */                     .getSize() == storeToUpdate.getSize())
/*     */                   
/*     */                   { 
/*     */                     
/* 196 */                     computeDiskStats(storeToUpdate, device.getSysattrValue("stat"));
/* 197 */                     result.add(storeToUpdate);
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
/* 223 */                     device.unref(); break; }  } else if (storeToUpdate == null && store != null && "partition".equals(device.getDevtype())) { Udev.UdevDevice parent = device.getParentWithSubsystemDevtype("block", "disk"); if (parent != null && store.getName().equals(parent.getDevnode())) { String name = device.getDevnode(); store.partitionList.add(new HWPartition(name, device.getSysname(), (device.getPropertyValue("ID_FS_TYPE") == null) ? "partition" : device.getPropertyValue("ID_FS_TYPE"), (device.getPropertyValue("ID_FS_UUID") == null) ? "" : device.getPropertyValue("ID_FS_UUID"), ParseUtil.parseLongOrDefault(device.getSysattrValue("size"), 0L) * 512L, ParseUtil.parseIntOrDefault(device.getPropertyValue("MAJOR"), 0), ParseUtil.parseIntOrDefault(device.getPropertyValue("MINOR"), 0), mountsMap.getOrDefault(name, ""))); }  }   device.unref(); } finally { device.unref(); }
/*     */           
/*     */           }
/*     */         } 
/*     */       } finally {
/* 228 */         enumerate.unref();
/*     */       } 
/*     */     } finally {
/* 231 */       udev.unref();
/*     */     } 
/*     */     
/* 234 */     for (LinuxHWDiskStore hwds : result) {
/* 235 */       hwds.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)hwds.partitionList.stream()
/* 236 */           .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
/*     */     }
/* 238 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 245 */     return !getDisks(this).isEmpty();
/*     */   }
/*     */   
/*     */   private static Map<String, String> readMountsMap() {
/* 249 */     Map<String, String> mountsMap = new HashMap<>();
/* 250 */     List<String> mounts = FileUtil.readFile(ProcPath.MOUNTS);
/* 251 */     for (String mount : mounts) {
/* 252 */       String[] split = ParseUtil.whitespaces.split(mount);
/* 253 */       if (split.length < 2 || !split[0].startsWith("/dev/")) {
/*     */         continue;
/*     */       }
/* 256 */       mountsMap.put(split[0], split[1]);
/*     */     } 
/* 258 */     return mountsMap;
/*     */   }
/*     */   
/*     */   private static void computeDiskStats(LinuxHWDiskStore store, String devstat) {
/* 262 */     long[] devstatArray = ParseUtil.parseStringToLongArray(devstat, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, ' ');
/* 263 */     store.timeStamp = System.currentTimeMillis();
/*     */ 
/*     */     
/* 266 */     store.reads = devstatArray[UdevStat.READS.ordinal()];
/* 267 */     store.readBytes = devstatArray[UdevStat.READ_BYTES.ordinal()] * 512L;
/* 268 */     store.writes = devstatArray[UdevStat.WRITES.ordinal()];
/* 269 */     store.writeBytes = devstatArray[UdevStat.WRITE_BYTES.ordinal()] * 512L;
/* 270 */     store.currentQueueLength = devstatArray[UdevStat.QUEUE_LENGTH.ordinal()];
/* 271 */     store.transferTime = devstatArray[UdevStat.ACTIVE_MS.ordinal()];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   enum UdevStat
/*     */   {
/* 278 */     READS(0), READ_BYTES(2), WRITES(4), WRITE_BYTES(6), QUEUE_LENGTH(8), ACTIVE_MS(9);
/*     */     
/*     */     private int order;
/*     */     
/*     */     public int getOrder() {
/* 283 */       return this.order;
/*     */     }
/*     */     
/*     */     UdevStat(int order) {
/* 287 */       this.order = order;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */