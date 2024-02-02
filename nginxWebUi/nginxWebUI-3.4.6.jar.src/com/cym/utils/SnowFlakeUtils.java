/*   */ package com.cym.utils;
/*   */ 
/*   */ import com.cym.sqlhelper.utils.SnowFlake;
/*   */ 
/*   */ public class SnowFlakeUtils {
/* 6 */   static SnowFlake snowFlake = new SnowFlake(1L, 1L);
/*   */   
/*   */   public static Long getId() {
/* 9 */     return Long.valueOf(Long.parseLong(snowFlake.nextId()));
/*   */   }
/*   */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\SnowFlakeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */