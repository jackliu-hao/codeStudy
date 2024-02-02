/*     */ package oshi.driver.linux;
/*     */ 
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Sysfs
/*     */ {
/*     */   public static String querySystemVendor() {
/*  47 */     String sysVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/sys_vendor").trim();
/*  48 */     if (!sysVendor.isEmpty()) {
/*  49 */       return sysVendor;
/*     */     }
/*  51 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryProductModel() {
/*  60 */     String productName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_name").trim();
/*     */     
/*  62 */     String productVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_version").trim();
/*  63 */     if (productName.isEmpty()) {
/*  64 */       if (!productVersion.isEmpty()) {
/*  65 */         return productVersion;
/*     */       }
/*     */     } else {
/*  68 */       if (!productVersion.isEmpty() && !"None".equals(productVersion)) {
/*  69 */         return productName + " (version: " + productVersion + ")";
/*     */       }
/*  71 */       return productName;
/*     */     } 
/*     */     
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryProductSerial() {
/*  85 */     String serial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_serial");
/*  86 */     if (!serial.isEmpty() && !"None".equals(serial)) {
/*  87 */       return serial;
/*     */     }
/*  89 */     return queryBoardSerial();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBoardVendor() {
/*  98 */     String boardVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_vendor").trim();
/*  99 */     if (!boardVendor.isEmpty()) {
/* 100 */       return boardVendor;
/*     */     }
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBoardModel() {
/* 111 */     String boardName = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_name").trim();
/* 112 */     if (!boardName.isEmpty()) {
/* 113 */       return boardName;
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBoardVersion() {
/* 124 */     String boardVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_version").trim();
/* 125 */     if (!boardVersion.isEmpty()) {
/* 126 */       return boardVersion;
/*     */     }
/* 128 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBoardSerial() {
/* 137 */     String boardSerial = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial").trim();
/* 138 */     if (!boardSerial.isEmpty()) {
/* 139 */       return boardSerial;
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBiosVendor() {
/* 150 */     String biosVendor = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_vendor").trim();
/* 151 */     if (biosVendor.isEmpty()) {
/* 152 */       return biosVendor;
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBiosDescription() {
/* 163 */     String modalias = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/modalias").trim();
/* 164 */     if (!modalias.isEmpty()) {
/* 165 */       return modalias;
/*     */     }
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBiosVersion(String biosRevision) {
/* 178 */     String biosVersion = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_version").trim();
/* 179 */     if (!biosVersion.isEmpty()) {
/* 180 */       return biosVersion + (Util.isBlank(biosRevision) ? "" : (" (revision " + biosRevision + ")"));
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String queryBiosReleaseDate() {
/* 191 */     String biosDate = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_date").trim();
/* 192 */     if (!biosDate.isEmpty()) {
/* 193 */       return ParseUtil.parseMmDdYyyyToYyyyMmDD(biosDate);
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Sysfs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */