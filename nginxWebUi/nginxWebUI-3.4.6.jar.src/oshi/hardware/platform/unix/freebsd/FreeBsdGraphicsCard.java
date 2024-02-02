/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.GraphicsCard;
/*     */ import oshi.hardware.common.AbstractGraphicsCard;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Immutable
/*     */ final class FreeBsdGraphicsCard
/*     */   extends AbstractGraphicsCard
/*     */ {
/*     */   private static final String PCI_CLASS_DISPLAY = "0x03";
/*     */   
/*     */   FreeBsdGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/*  60 */     super(name, deviceId, vendor, versionInfo, vram);
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
/*     */   public static List<GraphicsCard> getGraphicsCards() {
/*  73 */     List<FreeBsdGraphicsCard> cardList = new ArrayList<>();
/*     */     
/*  75 */     List<String> devices = ExecutingCommand.runNative("pciconf -lv");
/*  76 */     if (devices.isEmpty()) {
/*  77 */       return Collections.emptyList();
/*     */     }
/*  79 */     String name = "unknown";
/*  80 */     String vendorId = "unknown";
/*  81 */     String productId = "unknown";
/*  82 */     String classCode = "";
/*  83 */     String versionInfo = "unknown";
/*  84 */     for (String line : devices) {
/*  85 */       if (line.contains("class=0x")) {
/*     */         
/*  87 */         if ("0x03".equals(classCode)) {
/*  88 */           cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? "unknown" : name, 
/*  89 */                 productId.isEmpty() ? "unknown" : productId, 
/*  90 */                 vendorId.isEmpty() ? "unknown" : vendorId, 
/*  91 */                 versionInfo.isEmpty() ? "unknown" : versionInfo, 0L));
/*     */         }
/*     */         
/*  94 */         String[] arrayOfString = ParseUtil.whitespaces.split(line);
/*  95 */         for (String s : arrayOfString) {
/*  96 */           String[] keyVal = s.split("=");
/*  97 */           if (keyVal.length > 1) {
/*  98 */             if (keyVal[0].equals("class") && keyVal[1].length() >= 4) {
/*     */               
/* 100 */               classCode = keyVal[1].substring(0, 4);
/* 101 */             } else if (keyVal[0].equals("chip") && keyVal[1].length() >= 10) {
/*     */               
/* 103 */               productId = keyVal[1].substring(0, 6);
/* 104 */               vendorId = "0x" + keyVal[1].substring(6, 10);
/* 105 */             } else if (keyVal[0].contains("rev")) {
/*     */               
/* 107 */               versionInfo = s;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 112 */         name = "unknown"; continue;
/*     */       } 
/* 114 */       String[] split = line.trim().split("=", 2);
/* 115 */       if (split.length == 2) {
/* 116 */         String key = split[0].trim();
/* 117 */         if (key.equals("vendor")) {
/*     */           
/* 119 */           vendorId = ParseUtil.getSingleQuoteStringValue(line) + (vendorId.equals("unknown") ? "" : (" (" + vendorId + ")")); continue;
/* 120 */         }  if (key.equals("device")) {
/* 121 */           name = ParseUtil.getSingleQuoteStringValue(line);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 127 */     if ("0x03".equals(classCode)) {
/* 128 */       cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? "unknown" : name, 
/* 129 */             productId.isEmpty() ? "unknown" : productId, 
/* 130 */             vendorId.isEmpty() ? "unknown" : vendorId, 
/* 131 */             versionInfo.isEmpty() ? "unknown" : versionInfo, 0L));
/*     */     }
/* 133 */     return (List)Collections.unmodifiableList(cardList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */