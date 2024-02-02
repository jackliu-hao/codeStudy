/*     */ package oshi.driver.unix.aix;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class Lscfg
/*     */ {
/*     */   public static List<String> queryAllDevices() {
/*  49 */     return ExecutingCommand.runNative("lscfg -vp");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Triplet<String, String, String> queryBackplaneModelSerialVersion(List<String> lscfg) {
/*  60 */     String planeMarker = "WAY BACKPLANE";
/*  61 */     String modelMarker = "Part Number";
/*  62 */     String serialMarker = "Serial Number";
/*  63 */     String versionMarker = "Version";
/*  64 */     String locationMarker = "Physical Location";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     String model = null;
/*  77 */     String serialNumber = null;
/*  78 */     String version = null;
/*  79 */     boolean planeFlag = false;
/*  80 */     for (String checkLine : lscfg) {
/*  81 */       if (!planeFlag && checkLine.contains("WAY BACKPLANE")) {
/*  82 */         planeFlag = true; continue;
/*  83 */       }  if (planeFlag) {
/*  84 */         if (checkLine.contains("Part Number")) {
/*  85 */           model = ParseUtil.removeLeadingDots(checkLine.split("Part Number")[1].trim()); continue;
/*  86 */         }  if (checkLine.contains("Serial Number")) {
/*  87 */           serialNumber = ParseUtil.removeLeadingDots(checkLine.split("Serial Number")[1].trim()); continue;
/*  88 */         }  if (checkLine.contains("Version")) {
/*  89 */           version = ParseUtil.removeLeadingDots(checkLine.split("Version")[1].trim()); continue;
/*  90 */         }  if (checkLine.contains("Physical Location")) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*  95 */     return new Triplet(model, serialNumber, version);
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
/*     */   public static Pair<String, String> queryModelSerial(String device) {
/* 107 */     String modelMarker = "Machine Type and Model";
/* 108 */     String serialMarker = "Serial Number";
/* 109 */     String model = null;
/* 110 */     String serial = null;
/* 111 */     for (String s : ExecutingCommand.runNative("lscfg -vl " + device)) {
/* 112 */       if (s.contains(modelMarker)) {
/* 113 */         model = ParseUtil.removeLeadingDots(s.split(modelMarker)[1].trim()); continue;
/* 114 */       }  if (s.contains(serialMarker)) {
/* 115 */         serial = ParseUtil.removeLeadingDots(s.split(serialMarker)[1].trim());
/*     */       }
/*     */     } 
/* 118 */     return new Pair(model, serial);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\Lscfg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */