/*    */ package oshi.driver.unix.freebsd.disk;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
/*    */ import oshi.util.tuples.Triplet;
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
/*    */ @ThreadSafe
/*    */ public final class GeomDiskList
/*    */ {
/*    */   private static final String GEOM_DISK_LIST = "geom disk list";
/*    */   
/*    */   public static Map<String, Triplet<String, String, Long>> queryDisks() {
/* 55 */     Map<String, Triplet<String, String, Long>> diskMap = new HashMap<>();
/*    */     
/* 57 */     String diskName = null;
/* 58 */     String descr = "unknown";
/* 59 */     String ident = "unknown";
/* 60 */     long mediaSize = 0L;
/*    */     
/* 62 */     List<String> geom = ExecutingCommand.runNative("geom disk list");
/* 63 */     for (String line : geom) {
/* 64 */       line = line.trim();
/*    */       
/* 66 */       if (line.startsWith("Geom name:")) {
/*    */         
/* 68 */         if (diskName != null) {
/* 69 */           diskMap.put(diskName, new Triplet(descr, ident, Long.valueOf(mediaSize)));
/* 70 */           descr = "unknown";
/* 71 */           ident = "unknown";
/* 72 */           mediaSize = 0L;
/*    */         } 
/*    */         
/* 75 */         diskName = line.substring(line.lastIndexOf(' ') + 1);
/*    */       } 
/*    */       
/* 78 */       if (diskName != null) {
/* 79 */         line = line.trim();
/* 80 */         if (line.startsWith("Mediasize:")) {
/* 81 */           String[] split = ParseUtil.whitespaces.split(line);
/* 82 */           if (split.length > 1) {
/* 83 */             mediaSize = ParseUtil.parseLongOrDefault(split[1], 0L);
/*    */           }
/*    */         } 
/* 86 */         if (line.startsWith("descr:")) {
/* 87 */           descr = line.replace("descr:", "").trim();
/*    */         }
/* 89 */         if (line.startsWith("ident:")) {
/* 90 */           ident = line.replace("ident:", "").replace("(null)", "").trim();
/*    */         }
/*    */       } 
/*    */     } 
/* 94 */     if (diskName != null) {
/* 95 */       diskMap.put(diskName, new Triplet(descr, ident, Long.valueOf(mediaSize)));
/*    */     }
/* 97 */     return diskMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\freebsd\disk\GeomDiskList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */