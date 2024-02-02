/*    */ package com.cym.utils;
/*    */ 
/*    */ import com.warrenstrange.googleauth.GoogleAuthenticator;
/*    */ import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ @Component
/*    */ public class AuthUtils
/*    */ {
/* 13 */   Logger logger = LoggerFactory.getLogger(getClass());
/*    */   GoogleAuthenticator gAuth;
/*    */   
/*    */   @Init
/*    */   public void init() {
/* 18 */     this.gAuth = new GoogleAuthenticator();
/*    */   }
/*    */   
/*    */   public Boolean testKey(String key, String code) {
/*    */     try {
/* 23 */       Integer value = Integer.valueOf(Integer.parseInt(code));
/*    */ 
/*    */ 
/*    */       
/* 27 */       GoogleAuthenticator gAuth = new GoogleAuthenticator();
/* 28 */       return Boolean.valueOf(gAuth.authorize(key, value.intValue()));
/* 29 */     } catch (Exception e) {
/* 30 */       this.logger.error(e.getMessage(), e);
/* 31 */       return Boolean.valueOf(false);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String makeKey() {
/* 38 */     GoogleAuthenticatorKey key = this.gAuth.createCredentials();
/* 39 */     String key1 = key.getKey();
/* 40 */     System.out.println(key1);
/* 41 */     return key1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCode(String key) {
/* 47 */     GoogleAuthenticator gAuth = new GoogleAuthenticator();
/* 48 */     int code = gAuth.getTotpPassword(key);
/* 49 */     System.out.println(code);
/* 50 */     return code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\AuthUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */