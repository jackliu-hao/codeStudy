/*     */ package oshi.hardware.platform.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.solaris.disk.Iostat;
/*     */ import oshi.driver.unix.solaris.disk.Lshal;
/*     */ import oshi.driver.unix.solaris.disk.Prtvtoc;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
/*     */ import oshi.util.tuples.Quintet;
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
/*     */ public final class SolarisHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*  54 */   private long reads = 0L;
/*  55 */   private long readBytes = 0L;
/*  56 */   private long writes = 0L;
/*  57 */   private long writeBytes = 0L;
/*  58 */   private long currentQueueLength = 0L;
/*  59 */   private long transferTime = 0L;
/*  60 */   private long timeStamp = 0L;
/*     */   private List<HWPartition> partitionList;
/*     */   
/*     */   private SolarisHWDiskStore(String name, String model, String serial, long size) {
/*  64 */     super(name, model, serial, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/*  69 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/*  74 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/*  79 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/*  84 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/*  89 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/*  94 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/*  99 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 104 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 109 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 110 */     try { LibKstat.Kstat ksp = kc.lookup(null, 0, getName());
/* 111 */       if (ksp != null && kc.read(ksp))
/* 112 */       { LibKstat.KstatIO data = new LibKstat.KstatIO(ksp.ks_data);
/* 113 */         this.reads = data.reads;
/* 114 */         this.writes = data.writes;
/* 115 */         this.readBytes = data.nread;
/* 116 */         this.writeBytes = data.nwritten;
/* 117 */         this.currentQueueLength = data.wcnt + data.rcnt;
/*     */         
/* 119 */         this.transferTime = data.rtime / 1000000L;
/* 120 */         this.timeStamp = ksp.ks_snaptime / 1000000L;
/* 121 */         boolean bool = true;
/*     */         
/* 123 */         if (kc != null) kc.close();  return bool; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 124 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return false;
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
/* 136 */     Map<String, String> deviceMap = Iostat.queryPartitionToMountMap();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     Map<String, Integer> majorMap = Lshal.queryDiskToMajorMap();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 146 */     Map<String, Quintet<String, String, String, String, Long>> deviceStringMap = Iostat.queryDeviceStrings(deviceMap.keySet());
/*     */     
/* 148 */     List<SolarisHWDiskStore> storeList = new ArrayList<>();
/* 149 */     for (Map.Entry<String, Quintet<String, String, String, String, Long>> entry : deviceStringMap.entrySet()) {
/* 150 */       String storeName = entry.getKey();
/* 151 */       Quintet<String, String, String, String, Long> val = entry.getValue();
/* 152 */       storeList.add(createStore(storeName, (String)val.getA(), (String)val.getB(), (String)val.getC(), (String)val.getD(), ((Long)val.getE()).longValue(), deviceMap
/* 153 */             .getOrDefault(storeName, ""), ((Integer)majorMap.getOrDefault(storeName, Integer.valueOf(0))).intValue()));
/*     */     } 
/*     */     
/* 156 */     return (List)Collections.unmodifiableList(storeList);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static SolarisHWDiskStore createStore(String diskName, String model, String vendor, String product, String serial, long size, String mount, int major) {
/* 162 */     SolarisHWDiskStore store = new SolarisHWDiskStore(diskName, model.isEmpty() ? (vendor + " " + product).trim() : model, serial, size);
/* 163 */     store.partitionList = Collections.unmodifiableList((List<? extends HWPartition>)Prtvtoc.queryPartitions(mount, major).stream()
/* 164 */         .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
/* 165 */     store.updateAttributes();
/* 166 */     return store;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */