/*     */ package oshi.driver.unix.freebsd.disk;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public final class GeomPartList
/*     */ {
/*     */   private static final String GEOM_PART_LIST = "geom part list";
/*     */   private static final String STAT_FILESIZE = "stat -f %i /dev/";
/*     */   
/*     */   public static Map<String, List<HWPartition>> queryPartitions() {
/*  59 */     Map<String, String> mountMap = Mount.queryPartitionToMountMap();
/*     */     
/*  61 */     Map<String, List<HWPartition>> partitionMap = new HashMap<>();
/*     */     
/*  63 */     String diskName = null;
/*     */     
/*  65 */     List<HWPartition> partList = new ArrayList<>();
/*     */     
/*  67 */     String partName = null;
/*  68 */     String identification = "unknown";
/*  69 */     String type = "unknown";
/*  70 */     String uuid = "unknown";
/*  71 */     long size = 0L;
/*  72 */     String mountPoint = "";
/*     */     
/*  74 */     List<String> geom = ExecutingCommand.runNative("geom part list");
/*  75 */     for (String line : geom) {
/*  76 */       line = line.trim();
/*     */       
/*  78 */       if (line.startsWith("Geom name:")) {
/*     */         
/*  80 */         if (diskName != null && !partList.isEmpty()) {
/*     */           
/*  82 */           partitionMap.put(diskName, partList);
/*     */           
/*  84 */           partList = new ArrayList<>();
/*     */         } 
/*     */         
/*  87 */         diskName = line.substring(line.lastIndexOf(' ') + 1);
/*     */       } 
/*     */       
/*  90 */       if (diskName != null) {
/*     */         
/*  92 */         if (line.contains("Name:")) {
/*     */           
/*  94 */           if (partName != null) {
/*     */ 
/*     */ 
/*     */             
/*  98 */             int minor = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + partName), 0);
/*  99 */             partList.add(new HWPartition(identification, partName, type, uuid, size, 0, minor, mountPoint));
/* 100 */             partName = null;
/* 101 */             identification = "unknown";
/* 102 */             type = "unknown";
/* 103 */             uuid = "unknown";
/* 104 */             size = 0L;
/*     */           } 
/*     */ 
/*     */           
/* 108 */           String part = line.substring(line.lastIndexOf(' ') + 1);
/* 109 */           if (part.startsWith(diskName)) {
/* 110 */             partName = part;
/* 111 */             identification = part;
/* 112 */             mountPoint = mountMap.getOrDefault(part, "");
/*     */           } 
/*     */         } 
/*     */         
/* 116 */         if (partName != null) {
/* 117 */           String[] split = ParseUtil.whitespaces.split(line);
/* 118 */           if (split.length >= 2) {
/* 119 */             if (line.startsWith("Mediasize:")) {
/* 120 */               size = ParseUtil.parseLongOrDefault(split[1], 0L); continue;
/* 121 */             }  if (line.startsWith("rawuuid:")) {
/* 122 */               uuid = split[1]; continue;
/* 123 */             }  if (line.startsWith("type:")) {
/* 124 */               type = split[1];
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 130 */     if (diskName != null) {
/*     */       
/* 132 */       if (partName != null) {
/* 133 */         int minor = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("stat -f %i /dev/" + partName), 0);
/* 134 */         partList.add(new HWPartition(identification, partName, type, uuid, size, 0, minor, mountPoint));
/*     */       } 
/*     */       
/* 137 */       if (!partList.isEmpty()) {
/*     */         
/* 139 */         partList = (List<HWPartition>)partList.stream().sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList());
/* 140 */         partitionMap.put(diskName, Collections.unmodifiableList(partList));
/*     */       } 
/*     */     } 
/* 143 */     return partitionMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\freebsd\disk\GeomPartList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */