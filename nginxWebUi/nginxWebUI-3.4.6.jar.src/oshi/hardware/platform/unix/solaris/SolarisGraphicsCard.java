/*     */ package oshi.hardware.platform.unix.solaris;
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
/*     */ final class SolarisGraphicsCard
/*     */   extends AbstractGraphicsCard
/*     */ {
/*     */   private static final String PCI_CLASS_DISPLAY = "0003";
/*     */   
/*     */   SolarisGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
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
/*  73 */     List<GraphicsCard> cardList = new ArrayList<>();
/*     */     
/*  75 */     List<String> devices = ExecutingCommand.runNative("prtconf -pv");
/*  76 */     if (devices.isEmpty()) {
/*  77 */       return cardList;
/*     */     }
/*  79 */     String name = "";
/*  80 */     String vendorId = "";
/*  81 */     String productId = "";
/*  82 */     String classCode = "";
/*  83 */     List<String> versionInfoList = new ArrayList<>();
/*  84 */     for (String line : devices) {
/*     */ 
/*     */       
/*  87 */       if (line.contains("Node 0x")) {
/*  88 */         if ("0003".equals(classCode)) {
/*  89 */           cardList.add(new SolarisGraphicsCard(name.isEmpty() ? "unknown" : name, 
/*  90 */                 productId.isEmpty() ? "unknown" : productId, 
/*  91 */                 vendorId.isEmpty() ? "unknown" : vendorId, 
/*  92 */                 versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), 0L));
/*     */         }
/*     */         
/*  95 */         name = "";
/*  96 */         vendorId = "unknown";
/*  97 */         productId = "unknown";
/*  98 */         classCode = "";
/*  99 */         versionInfoList.clear(); continue;
/*     */       } 
/* 101 */       String[] split = line.trim().split(":", 2);
/* 102 */       if (split.length == 2) {
/* 103 */         if (split[0].equals("model")) {
/*     */           
/* 105 */           name = ParseUtil.getSingleQuoteStringValue(line); continue;
/* 106 */         }  if (split[0].equals("name")) {
/*     */ 
/*     */           
/* 109 */           if (name.isEmpty())
/* 110 */             name = ParseUtil.getSingleQuoteStringValue(line);  continue;
/*     */         } 
/* 112 */         if (split[0].equals("vendor-id")) {
/*     */           
/* 114 */           vendorId = "0x" + line.substring(line.length() - 4); continue;
/* 115 */         }  if (split[0].equals("device-id")) {
/*     */           
/* 117 */           productId = "0x" + line.substring(line.length() - 4); continue;
/* 118 */         }  if (split[0].equals("revision-id")) {
/*     */           
/* 120 */           versionInfoList.add(line.trim()); continue;
/* 121 */         }  if (split[0].equals("class-code"))
/*     */         {
/*     */           
/* 124 */           classCode = line.substring(line.length() - 8, line.length() - 4);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 130 */     if ("0003".equals(classCode)) {
/* 131 */       cardList.add(new SolarisGraphicsCard(name.isEmpty() ? "unknown" : name, 
/* 132 */             productId.isEmpty() ? "unknown" : productId, 
/* 133 */             vendorId.isEmpty() ? "unknown" : vendorId, 
/* 134 */             versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), 0L));
/*     */     }
/* 136 */     return Collections.unmodifiableList(cardList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */