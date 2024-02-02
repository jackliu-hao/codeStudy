/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.Ls;
/*     */ import oshi.driver.unix.aix.Lscfg;
/*     */ import oshi.driver.unix.aix.Lspv;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
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
/*     */ @ThreadSafe
/*     */ public final class AixHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*     */   private final Supplier<Perfstat.perfstat_disk_t[]> diskStats;
/*  55 */   private long reads = 0L;
/*  56 */   private long readBytes = 0L;
/*  57 */   private long writes = 0L;
/*  58 */   private long writeBytes = 0L;
/*  59 */   private long currentQueueLength = 0L;
/*  60 */   private long transferTime = 0L;
/*  61 */   private long timeStamp = 0L;
/*     */   private List<HWPartition> partitionList;
/*     */   
/*     */   private AixHWDiskStore(String name, String model, String serial, long size, Supplier<Perfstat.perfstat_disk_t[]> diskStats) {
/*  65 */     super(name, model, serial, size);
/*  66 */     this.diskStats = diskStats;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/*  71 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/*  76 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/*  81 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/*  86 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/*  91 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/*  96 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/* 101 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 106 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 111 */     for (Perfstat.perfstat_disk_t stat : (Perfstat.perfstat_disk_t[])this.diskStats.get()) {
/* 112 */       String name = Native.toString(stat.name);
/* 113 */       if (name.equals(getName())) {
/*     */         
/* 115 */         long blks = stat.rblks + stat.wblks;
/* 116 */         this.reads = stat.xfers;
/* 117 */         if (blks > 0L) {
/* 118 */           this.writes = stat.xfers * stat.wblks / blks;
/* 119 */           this.reads -= this.writes;
/*     */         } 
/* 121 */         this.readBytes = stat.rblks * stat.bsize;
/* 122 */         this.writeBytes = stat.wblks * stat.bsize;
/* 123 */         this.currentQueueLength = stat.qdepth;
/* 124 */         this.transferTime = stat.time;
/* 125 */         return true;
/*     */       } 
/*     */     } 
/* 128 */     return false;
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
/*     */   public static List<HWDiskStore> getDisks(Supplier<Perfstat.perfstat_disk_t[]> diskStats) {
/* 141 */     Map<String, Pair<Integer, Integer>> majMinMap = Ls.queryDeviceMajorMinor();
/* 142 */     List<AixHWDiskStore> storeList = new ArrayList<>();
/* 143 */     for (Perfstat.perfstat_disk_t disk : (Perfstat.perfstat_disk_t[])diskStats.get()) {
/* 144 */       String storeName = Native.toString(disk.name);
/* 145 */       Pair<String, String> ms = Lscfg.queryModelSerial(storeName);
/* 146 */       String model = (ms.getA() == null) ? Native.toString(disk.description) : (String)ms.getA();
/* 147 */       String serial = (ms.getB() == null) ? "unknown" : (String)ms.getB();
/* 148 */       storeList.add(createStore(storeName, model, serial, disk.size << 20L, diskStats, majMinMap));
/*     */     } 
/* 150 */     return Collections.unmodifiableList((List<? extends HWDiskStore>)storeList.stream()
/* 151 */         .sorted(Comparator.comparingInt(s -> s.getPartitions().isEmpty() ? Integer.MAX_VALUE : ((HWPartition)s.getPartitions().get(0)).getMajor()))
/*     */         
/* 153 */         .collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static AixHWDiskStore createStore(String diskName, String model, String serial, long size, Supplier<Perfstat.perfstat_disk_t[]> diskStats, Map<String, Pair<Integer, Integer>> majMinMap) {
/* 158 */     AixHWDiskStore store = new AixHWDiskStore(diskName, model.isEmpty() ? "unknown" : model, serial, size, diskStats);
/*     */     
/* 160 */     store.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)Lspv.queryLogicalVolumes(diskName, majMinMap).stream()
/* 161 */         .sorted(Comparator.comparing(HWPartition::getMinor).thenComparing(HWPartition::getName))
/* 162 */         .collect(Collectors.toList()));
/* 163 */     store.updateAttributes();
/* 164 */     return store;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */