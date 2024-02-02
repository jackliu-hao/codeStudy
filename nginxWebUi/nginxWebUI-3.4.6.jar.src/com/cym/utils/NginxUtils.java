/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.util.RuntimeUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NginxUtils
/*    */ {
/*    */   public static boolean isRun() {
/* 13 */     boolean isRun = false;
/* 14 */     if (SystemTool.isWindows().booleanValue()) {
/* 15 */       String[] command = { "tasklist" };
/* 16 */       String rs = RuntimeUtil.execForStr(command);
/* 17 */       isRun = rs.toLowerCase().contains("nginx.exe");
/*    */     } else {
/* 19 */       String[] command = { "/bin/sh", "-c", "ps -ef|grep nginx" };
/* 20 */       String rs = RuntimeUtil.execForStr(command);
/* 21 */       isRun = (rs.contains("nginx: master process") || rs.contains("nginx: worker process"));
/*    */     } 
/*    */     
/* 24 */     return isRun;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\NginxUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */