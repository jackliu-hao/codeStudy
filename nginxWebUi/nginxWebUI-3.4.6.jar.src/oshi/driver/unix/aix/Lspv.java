/*     */ package oshi.driver.unix.aix;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.HWPartition;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public final class Lspv
/*     */ {
/*     */   public static List<HWPartition> queryLogicalVolumes(String device, Map<String, Pair<Integer, Integer>> majMinMap) {
/*  73 */     String stateMarker = "PV STATE:";
/*  74 */     String sizeMarker = "PP SIZE:";
/*  75 */     long ppSize = 0L;
/*  76 */     for (String s : ExecutingCommand.runNative("lspv -L " + device)) {
/*  77 */       if (s.startsWith(stateMarker)) {
/*  78 */         if (!s.contains("active"))
/*  79 */           return Collections.emptyList();  continue;
/*     */       } 
/*  81 */       if (s.contains(sizeMarker)) {
/*  82 */         ppSize = ParseUtil.getFirstIntValue(s);
/*     */       }
/*     */     } 
/*  85 */     if (ppSize == 0L) {
/*  86 */       return Collections.emptyList();
/*     */     }
/*     */     
/*  89 */     ppSize <<= 20L;
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
/* 118 */     Map<String, String> mountMap = new HashMap<>();
/* 119 */     Map<String, String> typeMap = new HashMap<>();
/* 120 */     Map<String, Integer> ppMap = new HashMap<>();
/* 121 */     for (String s : ExecutingCommand.runNative("lspv -p " + device)) {
/* 122 */       String[] split = ParseUtil.whitespaces.split(s.trim());
/* 123 */       if (split.length >= 6 && "used".equals(split[1])) {
/*     */         
/* 125 */         String name = split[split.length - 3];
/* 126 */         mountMap.put(name, split[split.length - 1]);
/* 127 */         typeMap.put(name, split[split.length - 2]);
/* 128 */         int ppCount = 1 + ParseUtil.getNthIntValue(split[0], 2) - ParseUtil.getNthIntValue(split[0], 1);
/* 129 */         ppMap.put(name, Integer.valueOf(ppCount + ((Integer)ppMap.getOrDefault(name, Integer.valueOf(0))).intValue()));
/*     */       } 
/*     */     } 
/* 132 */     List<HWPartition> partitions = new ArrayList<>();
/* 133 */     for (Map.Entry<String, String> entry : mountMap.entrySet()) {
/* 134 */       String mount = "N/A".equals(entry.getValue()) ? "" : entry.getValue();
/*     */       
/* 136 */       String name = entry.getKey();
/* 137 */       String type = typeMap.get(name);
/* 138 */       long size = ppSize * ((Integer)ppMap.get(name)).intValue();
/* 139 */       Pair<Integer, Integer> majMin = majMinMap.get(name);
/* 140 */       int major = (majMin == null) ? ParseUtil.getFirstIntValue(name) : ((Integer)majMin.getA()).intValue();
/* 141 */       int minor = (majMin == null) ? ParseUtil.getFirstIntValue(name) : ((Integer)majMin.getB()).intValue();
/* 142 */       partitions.add(new HWPartition(name, name, type, "", size, major, minor, mount));
/*     */     } 
/* 144 */     return partitions;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\Lspv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */