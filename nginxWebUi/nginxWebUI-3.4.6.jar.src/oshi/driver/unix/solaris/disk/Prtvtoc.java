/*     */ package oshi.driver.unix.solaris.disk;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.HWPartition;
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
/*     */ @ThreadSafe
/*     */ public final class Prtvtoc
/*     */ {
/*     */   private static final String PRTVTOC_DEV_DSK = "prtvtoc /dev/dsk/";
/*     */   
/*     */   public static List<HWPartition> queryPartitions(String mount, int major) {
/*  47 */     List<HWPartition> partList = new ArrayList<>();
/*     */ 
/*     */     
/*  50 */     List<String> prtvotc = ExecutingCommand.runNative("prtvtoc /dev/dsk/" + mount);
/*     */     
/*  52 */     if (prtvotc.size() > 1) {
/*  53 */       int bytesPerSector = 0;
/*     */ 
/*     */       
/*  56 */       for (String line : prtvotc) {
/*     */ 
/*     */         
/*  59 */         if (line.startsWith("*")) {
/*  60 */           if (line.endsWith("bytes/sector")) {
/*  61 */             String[] split = ParseUtil.whitespaces.split(line);
/*  62 */             if (split.length > 0)
/*  63 */               bytesPerSector = ParseUtil.parseIntOrDefault(split[1], 0); 
/*     */           }  continue;
/*     */         } 
/*  66 */         if (bytesPerSector > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  72 */           String[] split = ParseUtil.whitespaces.split(line.trim());
/*     */           
/*  74 */           if (split.length >= 6 && !"2".equals(split[0])) {
/*     */             
/*  76 */             String name, type, identification = mount + "s" + split[0];
/*     */             
/*  78 */             int minor = ParseUtil.parseIntOrDefault(split[0], 0);
/*     */ 
/*     */             
/*  81 */             switch (ParseUtil.parseIntOrDefault(split[1], 0)) {
/*     */               case 1:
/*     */               case 24:
/*  84 */                 name = "boot";
/*     */                 break;
/*     */               case 2:
/*  87 */                 name = "root";
/*     */                 break;
/*     */               case 3:
/*  90 */                 name = "swap";
/*     */                 break;
/*     */               case 4:
/*  93 */                 name = "usr";
/*     */                 break;
/*     */               case 5:
/*  96 */                 name = "backup";
/*     */                 break;
/*     */               case 6:
/*  99 */                 name = "stand";
/*     */                 break;
/*     */               case 7:
/* 102 */                 name = "var";
/*     */                 break;
/*     */               case 8:
/* 105 */                 name = "home";
/*     */                 break;
/*     */               case 9:
/* 108 */                 name = "altsctr";
/*     */                 break;
/*     */               case 10:
/* 111 */                 name = "cache";
/*     */                 break;
/*     */               case 11:
/* 114 */                 name = "reserved";
/*     */                 break;
/*     */               case 12:
/* 117 */                 name = "system";
/*     */                 break;
/*     */               case 14:
/* 120 */                 name = "public region";
/*     */                 break;
/*     */               case 15:
/* 123 */                 name = "private region";
/*     */                 break;
/*     */               default:
/* 126 */                 name = "unknown";
/*     */                 break;
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 132 */             switch (split[2]) {
/*     */               case "00":
/* 134 */                 type = "wm";
/*     */                 break;
/*     */               case "10":
/* 137 */                 type = "rm";
/*     */                 break;
/*     */               case "01":
/* 140 */                 type = "wu";
/*     */                 break;
/*     */               default:
/* 143 */                 type = "ru";
/*     */                 break;
/*     */             } 
/*     */             
/* 147 */             long partSize = bytesPerSector * ParseUtil.parseLongOrDefault(split[4], 0L);
/*     */             
/* 149 */             String mountPoint = "";
/* 150 */             if (split.length > 6) {
/* 151 */               mountPoint = split[6];
/*     */             }
/* 153 */             partList.add(new HWPartition(identification, name, type, "", partSize, major, minor, mountPoint));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 159 */     return partList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\solaris\disk\Prtvtoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */