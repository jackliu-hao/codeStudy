/*     */ package oshi.driver.linux;
/*     */ 
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Dmidecode
/*     */ {
/*     */   public static String querySerialNumber() {
/*  84 */     String marker = "Serial Number:";
/*  85 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t system")) {
/*  86 */       if (checkLine.contains(marker)) {
/*  87 */         return checkLine.split(marker)[1].trim();
/*     */       }
/*     */     } 
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pair<String, String> queryBiosNameRev() {
/* 101 */     String biosName = null;
/* 102 */     String revision = null;
/*     */     
/* 104 */     String biosMarker = "SMBIOS";
/* 105 */     String revMarker = "Bios Revision:";
/*     */ 
/*     */     
/* 108 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t bios")) {
/* 109 */       if (checkLine.contains("SMBIOS")) {
/* 110 */         String[] biosArr = ParseUtil.whitespaces.split(checkLine);
/* 111 */         if (biosArr.length >= 2) {
/* 112 */           biosName = biosArr[0] + " " + biosArr[1];
/*     */         }
/*     */       } 
/* 115 */       if (checkLine.contains("Bios Revision:")) {
/* 116 */         revision = checkLine.split("Bios Revision:")[1].trim();
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 121 */     return new Pair(biosName, revision);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Dmidecode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */