/*     */ package oshi.driver.linux.proc;
/*     */ 
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ public final class DiskStats
/*     */ {
/*     */   public enum IoStat
/*     */   {
/*  49 */     MAJOR,
/*     */ 
/*     */ 
/*     */     
/*  53 */     MINOR,
/*     */ 
/*     */ 
/*     */     
/*  57 */     NAME,
/*     */ 
/*     */ 
/*     */     
/*  61 */     READS,
/*     */ 
/*     */ 
/*     */     
/*  65 */     READS_MERGED,
/*     */ 
/*     */ 
/*     */     
/*  69 */     READS_SECTOR,
/*     */ 
/*     */ 
/*     */     
/*  73 */     READS_MS,
/*     */ 
/*     */ 
/*     */     
/*  77 */     WRITES,
/*     */ 
/*     */ 
/*     */     
/*  81 */     WRITES_MERGED,
/*     */ 
/*     */ 
/*     */     
/*  85 */     WRITES_SECTOR,
/*     */ 
/*     */ 
/*     */     
/*  89 */     WRITES_MS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     IO_QUEUE_LENGTH,
/*     */ 
/*     */ 
/*     */     
/*  98 */     IO_MS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     IO_MS_WEIGHTED,
/*     */ 
/*     */ 
/*     */     
/* 108 */     DISCARDS,
/*     */ 
/*     */ 
/*     */     
/* 112 */     DISCARDS_MERGED,
/*     */ 
/*     */ 
/*     */     
/* 116 */     DISCARDS_SECTOR,
/*     */ 
/*     */ 
/*     */     
/* 120 */     DISCARDS_MS,
/*     */ 
/*     */ 
/*     */     
/* 124 */     FLUSHES,
/*     */ 
/*     */ 
/*     */     
/* 128 */     FLUSHES_MS;
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
/*     */   
/*     */   public static Map<String, Map<IoStat, Long>> getDiskStats() {
/* 142 */     Map<String, Map<IoStat, Long>> diskStatMap = new HashMap<>();
/* 143 */     IoStat[] enumArray = IoStat.class.getEnumConstants();
/* 144 */     List<String> diskStats = FileUtil.readFile(ProcPath.DISKSTATS);
/* 145 */     for (String stat : diskStats) {
/* 146 */       String[] split = ParseUtil.whitespaces.split(stat.trim());
/* 147 */       Map<IoStat, Long> statMap = new EnumMap<>(IoStat.class);
/* 148 */       String name = null;
/* 149 */       for (int i = 0; i < enumArray.length && i < split.length; i++) {
/* 150 */         if (enumArray[i] == IoStat.NAME) {
/* 151 */           name = split[i];
/*     */         } else {
/* 153 */           statMap.put(enumArray[i], Long.valueOf(ParseUtil.parseLongOrDefault(split[i], 0L)));
/*     */         } 
/*     */       } 
/* 156 */       if (name != null) {
/* 157 */         diskStatMap.put(name, statMap);
/*     */       }
/*     */     } 
/* 160 */     return diskStatMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\DiskStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */