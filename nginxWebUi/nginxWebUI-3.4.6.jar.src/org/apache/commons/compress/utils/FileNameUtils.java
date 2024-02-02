/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.File;
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
/*    */ public class FileNameUtils
/*    */ {
/*    */   public static String getExtension(String filename) {
/* 41 */     if (filename == null) {
/* 42 */       return null;
/*    */     }
/*    */     
/* 45 */     String name = (new File(filename)).getName();
/* 46 */     int extensionPosition = name.lastIndexOf('.');
/* 47 */     if (extensionPosition < 0) {
/* 48 */       return "";
/*    */     }
/* 50 */     return name.substring(extensionPosition + 1);
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
/*    */   public static String getBaseName(String filename) {
/* 65 */     if (filename == null) {
/* 66 */       return null;
/*    */     }
/*    */     
/* 69 */     String name = (new File(filename)).getName();
/*    */     
/* 71 */     int extensionPosition = name.lastIndexOf('.');
/* 72 */     if (extensionPosition < 0) {
/* 73 */       return name;
/*    */     }
/*    */     
/* 76 */     return name.substring(0, extensionPosition);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\FileNameUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */