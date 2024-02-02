/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ public class IpUtil {
/*    */   public static String getIp(Context ctx) {
/*  8 */     String ip = ctx.header("X-Real-IP");
/*    */     
/* 10 */     if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
/* 11 */       ip = ctx.header("X-Forwarded-For");
/*    */     }
/*    */     
/* 14 */     if (Utils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
/* 15 */       ip = ctx.ip();
/*    */     }
/*    */     
/* 18 */     return ip;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\IpUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */