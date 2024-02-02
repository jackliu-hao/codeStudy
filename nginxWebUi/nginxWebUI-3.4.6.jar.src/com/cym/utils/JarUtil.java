/*    */ package com.cym.utils;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import java.security.CodeSource;
/*    */ import java.security.ProtectionDomain;
/*    */ 
/*    */ public class JarUtil {
/*    */   public static String getCurrentFilePath() {
/* 11 */     ProtectionDomain protectionDomain = JarUtil.class.getProtectionDomain();
/* 12 */     CodeSource codeSource = protectionDomain.getCodeSource();
/* 13 */     URI location = null;
/*    */     try {
/* 15 */       location = (codeSource == null) ? null : codeSource.getLocation().toURI();
/* 16 */     } catch (URISyntaxException e) {
/* 17 */       e.printStackTrace();
/*    */     } 
/* 19 */     String path = (location == null) ? null : location.getSchemeSpecificPart();
/* 20 */     if (path == null) {
/* 21 */       throw new IllegalStateException("Unable to determine code source archive");
/*    */     }
/* 23 */     File root = new File(path);
/* 24 */     if (!root.exists()) {
/* 25 */       throw new IllegalStateException("Unable to determine code source archive from " + root);
/*    */     }
/* 27 */     return root.getAbsolutePath();
/*    */   }
/*    */   
/*    */   public static File getCurrentFile() {
/* 31 */     return new File(getCurrentFilePath());
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 35 */     System.out.println(getCurrentFilePath());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\JarUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */