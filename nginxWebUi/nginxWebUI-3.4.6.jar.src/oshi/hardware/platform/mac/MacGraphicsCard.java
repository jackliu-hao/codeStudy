/*     */ package oshi.hardware.platform.mac;
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
/*     */ final class MacGraphicsCard
/*     */   extends AbstractGraphicsCard
/*     */ {
/*     */   MacGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/*  58 */     super(name, deviceId, vendor, versionInfo, vram);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<GraphicsCard> getGraphicsCards() {
/*  69 */     List<MacGraphicsCard> cardList = new ArrayList<>();
/*  70 */     List<String> sp = ExecutingCommand.runNative("system_profiler SPDisplaysDataType");
/*  71 */     String name = "unknown";
/*  72 */     String deviceId = "unknown";
/*  73 */     String vendor = "unknown";
/*  74 */     List<String> versionInfoList = new ArrayList<>();
/*  75 */     long vram = 0L;
/*  76 */     int cardNum = 0;
/*  77 */     for (String line : sp) {
/*  78 */       String[] split = line.trim().split(":", 2);
/*  79 */       if (split.length == 2) {
/*  80 */         String prefix = split[0].toLowerCase();
/*  81 */         if (prefix.equals("chipset model")) {
/*     */           
/*  83 */           if (cardNum++ > 0) {
/*  84 */             cardList.add(new MacGraphicsCard(name, deviceId, vendor, 
/*  85 */                   versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), vram));
/*     */             
/*  87 */             versionInfoList.clear();
/*     */           } 
/*  89 */           name = split[1].trim(); continue;
/*  90 */         }  if (prefix.equals("device id")) {
/*  91 */           deviceId = split[1].trim(); continue;
/*  92 */         }  if (prefix.equals("vendor")) {
/*  93 */           vendor = split[1].trim(); continue;
/*  94 */         }  if (prefix.contains("version") || prefix.contains("revision")) {
/*  95 */           versionInfoList.add(line.trim()); continue;
/*  96 */         }  if (prefix.startsWith("vram")) {
/*  97 */           vram = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
/*     */         }
/*     */       } 
/*     */     } 
/* 101 */     cardList.add(new MacGraphicsCard(name, deviceId, vendor, 
/* 102 */           versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), vram));
/* 103 */     return (List)Collections.unmodifiableList(cardList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */