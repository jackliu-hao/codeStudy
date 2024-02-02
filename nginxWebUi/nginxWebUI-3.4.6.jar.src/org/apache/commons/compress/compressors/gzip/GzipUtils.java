/*    */ package org.apache.commons.compress.compressors.gzip;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.compress.compressors.FileNameUtil;
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
/*    */ public class GzipUtils
/*    */ {
/*    */   private static final FileNameUtil fileNameUtil;
/*    */   
/*    */   static {
/* 37 */     Map<String, String> uncompressSuffix = new LinkedHashMap<>();
/*    */     
/* 39 */     uncompressSuffix.put(".tgz", ".tar");
/* 40 */     uncompressSuffix.put(".taz", ".tar");
/* 41 */     uncompressSuffix.put(".svgz", ".svg");
/* 42 */     uncompressSuffix.put(".cpgz", ".cpio");
/* 43 */     uncompressSuffix.put(".wmz", ".wmf");
/* 44 */     uncompressSuffix.put(".emz", ".emf");
/* 45 */     uncompressSuffix.put(".gz", "");
/* 46 */     uncompressSuffix.put(".z", "");
/* 47 */     uncompressSuffix.put("-gz", "");
/* 48 */     uncompressSuffix.put("-z", "");
/* 49 */     uncompressSuffix.put("_z", "");
/* 50 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".gz");
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
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isCompressedFilename(String fileName) {
/* 65 */     return fileNameUtil.isCompressedFilename(fileName);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getUncompressedFilename(String fileName) {
/* 82 */     return fileNameUtil.getUncompressedFilename(fileName);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getCompressedFilename(String fileName) {
/* 97 */     return fileNameUtil.getCompressedFilename(fileName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\gzip\GzipUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */