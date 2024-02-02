/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.GraphicsCard;
/*    */ import oshi.hardware.common.AbstractGraphicsCard;
/*    */ import oshi.util.ParseUtil;
/*    */ import oshi.util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ final class AixGraphicsCard
/*    */   extends AbstractGraphicsCard
/*    */ {
/*    */   AixGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/* 59 */     super(name, deviceId, vendor, versionInfo, vram);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<GraphicsCard> getGraphicsCards(Supplier<List<String>> lscfg) {
/* 71 */     List<GraphicsCard> cardList = new ArrayList<>();
/* 72 */     boolean display = false;
/* 73 */     String name = null;
/* 74 */     String vendor = null;
/* 75 */     List<String> versionInfo = new ArrayList<>();
/* 76 */     for (String line : lscfg.get()) {
/* 77 */       String s = line.trim();
/* 78 */       if (s.startsWith("Name:") && s.contains("display")) {
/* 79 */         display = true; continue;
/* 80 */       }  if (display && s.toLowerCase().contains("graphics")) {
/* 81 */         name = s; continue;
/* 82 */       }  if (display && name != null) {
/* 83 */         if (s.startsWith("Manufacture ID")) {
/* 84 */           vendor = ParseUtil.removeLeadingDots(s.substring(14)); continue;
/* 85 */         }  if (s.contains("Level")) {
/* 86 */           versionInfo.add(s.replaceAll("\\.\\.+", "=")); continue;
/* 87 */         }  if (s.startsWith("Hardware Location Code")) {
/* 88 */           cardList.add(new AixGraphicsCard(name, "unknown", 
/* 89 */                 Util.isBlank(vendor) ? "unknown" : vendor, 
/* 90 */                 versionInfo.isEmpty() ? "unknown" : String.join(",", (Iterable)versionInfo), 0L));
/* 91 */           display = false;
/*    */         } 
/*    */       } 
/*    */     } 
/* 95 */     return Collections.unmodifiableList(cardList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */