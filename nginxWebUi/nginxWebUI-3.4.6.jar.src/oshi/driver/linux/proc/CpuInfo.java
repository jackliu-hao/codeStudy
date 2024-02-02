/*     */ package oshi.driver.linux.proc;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
/*     */ import oshi.util.tuples.Quartet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class CpuInfo
/*     */ {
/*     */   public static String queryCpuManufacturer() {
/*  51 */     List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
/*  52 */     for (String line : cpuInfo) {
/*  53 */       if (line.startsWith("CPU implementer")) {
/*  54 */         int part = ParseUtil.parseLastInt(line, 0);
/*  55 */         switch (part) {
/*     */           case 65:
/*  57 */             return "ARM";
/*     */           case 66:
/*  59 */             return "Broadcom";
/*     */           case 67:
/*  61 */             return "Cavium";
/*     */           case 68:
/*  63 */             return "DEC";
/*     */           case 78:
/*  65 */             return "Nvidia";
/*     */           case 80:
/*  67 */             return "APM";
/*     */           case 81:
/*  69 */             return "Qualcomm";
/*     */           case 83:
/*  71 */             return "Samsung";
/*     */           case 86:
/*  73 */             return "Marvell";
/*     */           case 102:
/*  75 */             return "Faraday";
/*     */           case 105:
/*  77 */             return "Intel";
/*     */         } 
/*  79 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Quartet<String, String, String, String> queryBoardInfo() {
/*  94 */     String pcManufacturer = null;
/*  95 */     String pcModel = null;
/*  96 */     String pcVersion = null;
/*  97 */     String pcSerialNumber = null;
/*     */     
/*  99 */     List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
/* 100 */     for (String line : cpuInfo) {
/* 101 */       String[] splitLine = ParseUtil.whitespacesColonWhitespace.split(line);
/* 102 */       if (splitLine.length < 2) {
/*     */         continue;
/*     */       }
/* 105 */       switch (splitLine[0]) {
/*     */         case "Hardware":
/* 107 */           pcModel = splitLine[1];
/*     */         
/*     */         case "Revision":
/* 110 */           pcVersion = splitLine[1];
/* 111 */           if (pcVersion.length() > 1) {
/* 112 */             pcManufacturer = queryBoardManufacturer(pcVersion.charAt(1));
/*     */           }
/*     */         
/*     */         case "Serial":
/* 116 */           pcSerialNumber = splitLine[1];
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 122 */     return new Quartet(pcManufacturer, pcModel, pcVersion, pcSerialNumber);
/*     */   }
/*     */   
/*     */   private static String queryBoardManufacturer(char digit) {
/* 126 */     switch (digit) {
/*     */       case '0':
/* 128 */         return "Sony UK";
/*     */       case '1':
/* 130 */         return "Egoman";
/*     */       case '2':
/* 132 */         return "Embest";
/*     */       case '3':
/* 134 */         return "Sony Japan";
/*     */       case '4':
/* 136 */         return "Embest";
/*     */       case '5':
/* 138 */         return "Stadium";
/*     */     } 
/* 140 */     return "unknown";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\CpuInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */