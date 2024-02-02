/*     */ package oshi.driver.unix.solaris.disk;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Iostat
/*     */ {
/*     */   private static final String IOSTAT_ER_DETAIL = "iostat -Er";
/*     */   private static final String IOSTAT_ER = "iostat -er";
/*     */   private static final String IOSTAT_ERN = "iostat -ern";
/*     */   private static final String DEVICE_HEADER = "device";
/*     */   
/*     */   public static Map<String, String> queryPartitionToMountMap() {
/*  74 */     Map<String, String> deviceMap = new HashMap<>();
/*     */ 
/*     */     
/*  77 */     List<String> mountNames = ExecutingCommand.runNative("iostat -er");
/*     */     
/*  79 */     List<String> mountPoints = ExecutingCommand.runNative("iostat -ern");
/*     */ 
/*     */     
/*  82 */     for (int i = 0; i < mountNames.size() && i < mountPoints.size(); i++) {
/*     */       
/*  84 */       String disk = mountNames.get(i);
/*  85 */       String[] diskSplit = disk.split(",");
/*  86 */       if (diskSplit.length >= 5 && !"device".equals(diskSplit[0])) {
/*  87 */         String mount = mountPoints.get(i);
/*  88 */         String[] mountSplit = mount.split(",");
/*  89 */         if (mountSplit.length >= 5 && !"device".equals(mountSplit[4])) {
/*  90 */           deviceMap.put(diskSplit[0], mountSplit[4]);
/*     */         }
/*     */       } 
/*     */     } 
/*  94 */     return deviceMap;
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
/*     */   public static Map<String, Quintet<String, String, String, String, Long>> queryDeviceStrings(Set<String> diskSet) {
/* 106 */     Map<String, Quintet<String, String, String, String, Long>> deviceParamMap = new HashMap<>();
/*     */     
/* 108 */     List<String> iostat = ExecutingCommand.runNative("iostat -Er");
/*     */     
/* 110 */     String diskName = null;
/* 111 */     String model = "";
/* 112 */     String vendor = "";
/* 113 */     String product = "";
/* 114 */     String serial = "";
/* 115 */     long size = 0L;
/* 116 */     for (String line : iostat) {
/*     */ 
/*     */ 
/*     */       
/* 120 */       String[] split = line.split(",");
/* 121 */       for (String keyValue : split) {
/* 122 */         keyValue = keyValue.trim();
/*     */ 
/*     */         
/* 125 */         if (diskSet.contains(keyValue)) {
/*     */ 
/*     */           
/* 128 */           if (diskName != null) {
/* 129 */             deviceParamMap.put(diskName, new Quintet(model, vendor, product, serial, Long.valueOf(size)));
/*     */           }
/*     */           
/* 132 */           diskName = keyValue;
/* 133 */           model = "";
/* 134 */           vendor = "";
/* 135 */           product = "";
/* 136 */           serial = "";
/* 137 */           size = 0L;
/*     */ 
/*     */         
/*     */         }
/* 141 */         else if (keyValue.startsWith("Model:")) {
/* 142 */           model = keyValue.replace("Model:", "").trim();
/* 143 */         } else if (keyValue.startsWith("Serial No:")) {
/* 144 */           serial = keyValue.replace("Serial No:", "").trim();
/* 145 */         } else if (keyValue.startsWith("Vendor:")) {
/* 146 */           vendor = keyValue.replace("Vendor:", "").trim();
/* 147 */         } else if (keyValue.startsWith("Product:")) {
/* 148 */           product = keyValue.replace("Product:", "").trim();
/* 149 */         } else if (keyValue.startsWith("Size:")) {
/*     */           
/* 151 */           String[] bytes = keyValue.split("<");
/* 152 */           if (bytes.length > 1) {
/* 153 */             bytes = ParseUtil.whitespaces.split(bytes[1]);
/* 154 */             size = ParseUtil.parseLongOrDefault(bytes[0], 0L);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 159 */       if (diskName != null) {
/* 160 */         deviceParamMap.put(diskName, new Quintet(model, vendor, product, serial, Long.valueOf(size)));
/*     */       }
/*     */     } 
/* 163 */     return deviceParamMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\solaris\disk\Iostat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */