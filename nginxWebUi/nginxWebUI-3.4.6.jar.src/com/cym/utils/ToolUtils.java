/*    */ package com.cym.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ToolUtils
/*    */ {
/*    */   public static String handleConf(String path) {
/* 12 */     return path.replace("};", "  }");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String handlePath(String path) {
/* 21 */     return path.replace("\\", "/").replace("//", "/");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String endDir(String path) {
/* 30 */     if (!path.endsWith("/")) {
/* 31 */       path = path + "/";
/*    */     }
/*    */     
/* 34 */     return path;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\ToolUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */