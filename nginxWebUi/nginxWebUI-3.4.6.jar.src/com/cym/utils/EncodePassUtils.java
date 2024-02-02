/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.crypto.SecureUtil;
/*    */ 
/*    */ public class EncodePassUtils
/*    */ {
/*  8 */   public static String defaultPass = "nginxWebUI";
/*    */ 
/*    */   
/*    */   public static String encode(String pass) {
/* 12 */     if (StrUtil.isNotEmpty(pass)) {
/* 13 */       pass = SecureUtil.md5(pass) + SecureUtil.md5(defaultPass);
/*    */     }
/*    */     
/* 16 */     return pass;
/*    */   }
/*    */   
/*    */   public static String encodeDefaultPass() {
/* 20 */     return SecureUtil.md5(defaultPass) + SecureUtil.md5(defaultPass);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\EncodePassUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */