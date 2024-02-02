/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.freebsd.disk.GeomDiskList;
/*     */ import oshi.driver.unix.freebsd.disk.GeomPartList;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.hardware.common.AbstractHWDiskStore;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
/*     */ import oshi.util.tuples.Triplet;
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
/*     */ public final class FreeBsdHWDiskStore
/*     */   extends AbstractHWDiskStore
/*     */ {
/*  52 */   private long reads = 0L;
/*  53 */   private long readBytes = 0L;
/*  54 */   private long writes = 0L;
/*  55 */   private long writeBytes = 0L;
/*  56 */   private long currentQueueLength = 0L;
/*  57 */   private long transferTime = 0L;
/*  58 */   private long timeStamp = 0L;
/*     */   private List<HWPartition> partitionList;
/*     */   
/*     */   private FreeBsdHWDiskStore(String name, String model, String serial, long size) {
/*  62 */     super(name, model, serial, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReads() {
/*  67 */     return this.reads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/*  72 */     return this.readBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWrites() {
/*  77 */     return this.writes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/*  82 */     return this.writeBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getCurrentQueueLength() {
/*  87 */     return this.currentQueueLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTransferTime() {
/*  92 */     return this.transferTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeStamp() {
/*  97 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWPartition> getPartitions() {
/* 102 */     return this.partitionList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 107 */     List<String> output = ExecutingCommand.runNative("iostat -Ix " + getName());
/* 108 */     long now = System.currentTimeMillis();
/* 109 */     boolean diskFound = false;
/* 110 */     for (String line : output) {
/* 111 */       String[] split = ParseUtil.whitespaces.split(line);
/* 112 */       if (split.length < 7 || !split[0].equals(getName())) {
/*     */         continue;
/*     */       }
/* 115 */       diskFound = true;
/* 116 */       this.reads = (long)ParseUtil.parseDoubleOrDefault(split[1], 0.0D);
/* 117 */       this.writes = (long)ParseUtil.parseDoubleOrDefault(split[2], 0.0D);
/*     */       
/* 119 */       this.readBytes = (long)(ParseUtil.parseDoubleOrDefault(split[3], 0.0D) * 1024.0D);
/* 120 */       this.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(split[4], 0.0D) * 1024.0D);
/*     */       
/* 122 */       this.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
/*     */       
/* 124 */       this.transferTime = (long)(ParseUtil.parseDoubleOrDefault(split[6], 0.0D) * 1000.0D);
/* 125 */       this.timeStamp = now;
/*     */     } 
/* 127 */     return diskFound;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HWDiskStore> getDisks() {
/* 138 */     List<HWDiskStore> diskList = new ArrayList<>();
/*     */ 
/*     */     
/* 141 */     Map<String, List<HWPartition>> partitionMap = GeomPartList.queryPartitions();
/*     */ 
/*     */     
/* 144 */     Map<String, Triplet<String, String, Long>> diskInfoMap = GeomDiskList.queryDisks();
/*     */ 
/*     */     
/* 147 */     List<String> devices = Arrays.asList(ParseUtil.whitespaces.split(BsdSysctlUtil.sysctl("kern.disks", "")));
/*     */ 
/*     */     
/* 150 */     List<String> iostat = ExecutingCommand.runNative("iostat -Ix");
/* 151 */     long now = System.currentTimeMillis();
/* 152 */     for (String line : iostat) {
/* 153 */       String[] split = ParseUtil.whitespaces.split(line);
/* 154 */       if (split.length > 6 && devices.contains(split[0])) {
/* 155 */         Triplet<String, String, Long> storeInfo = diskInfoMap.get(split[0]);
/*     */ 
/*     */         
/* 158 */         FreeBsdHWDiskStore store = (storeInfo == null) ? new FreeBsdHWDiskStore(split[0], "unknown", "unknown", 0L) : new FreeBsdHWDiskStore(split[0], (String)storeInfo.getA(), (String)storeInfo.getB(), ((Long)storeInfo.getC()).longValue());
/* 159 */         store.reads = (long)ParseUtil.parseDoubleOrDefault(split[1], 0.0D);
/* 160 */         store.writes = (long)ParseUtil.parseDoubleOrDefault(split[2], 0.0D);
/*     */         
/* 162 */         store.readBytes = (long)(ParseUtil.parseDoubleOrDefault(split[3], 0.0D) * 1024.0D);
/* 163 */         store.writeBytes = (long)(ParseUtil.parseDoubleOrDefault(split[4], 0.0D) * 1024.0D);
/*     */         
/* 165 */         store.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
/*     */         
/* 167 */         store.transferTime = (long)(ParseUtil.parseDoubleOrDefault(split[6], 0.0D) * 1000.0D);
/* 168 */         store
/* 169 */           .partitionList = Collections.unmodifiableList((List<? extends HWPartition>)((List)partitionMap.getOrDefault(split[0], Collections.emptyList())).stream()
/* 170 */             .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
/* 171 */         store.timeStamp = now;
/* 172 */         diskList.add(store);
/*     */       } 
/*     */     } 
/* 175 */     return Collections.unmodifiableList(diskList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdHWDiskStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */