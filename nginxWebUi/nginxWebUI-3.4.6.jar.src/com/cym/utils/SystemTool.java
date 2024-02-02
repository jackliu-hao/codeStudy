/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.system.SystemUtil;
/*    */ 
/*    */ 
/*    */ public class SystemTool
/*    */ {
/*    */   public static String getSystem() {
/*  9 */     if (SystemUtil.get("os.name").toLowerCase().contains("windows"))
/* 10 */       return "Windows"; 
/* 11 */     if (SystemUtil.get("os.name").toLowerCase().contains("mac os")) {
/* 12 */       return "Mac OS";
/*    */     }
/* 14 */     return "Linux";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static Boolean isWindows() {
/* 20 */     return Boolean.valueOf(getSystem().equals("Windows"));
/*    */   }
/*    */   
/*    */   public static Boolean isMacOS() {
/* 24 */     return Boolean.valueOf(getSystem().equals("Mac OS"));
/*    */   }
/*    */   
/*    */   public static Boolean isLinux() {
/* 28 */     return Boolean.valueOf(getSystem().equals("Linux"));
/*    */   }
/*    */   
/*    */   public static boolean hasRoot() {
/* 32 */     if (isLinux().booleanValue()) {
/* 33 */       String user = System.getProperties().getProperty("user.name");
/* 34 */       if ("root".equals(user)) {
/* 35 */         return true;
/*    */       }
/* 37 */       return false;
/*    */     } 
/*    */     
/* 40 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\SystemTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */