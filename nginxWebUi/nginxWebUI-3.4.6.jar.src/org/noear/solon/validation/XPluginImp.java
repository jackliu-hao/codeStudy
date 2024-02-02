/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.validation.annotation.LoginedChecker;
/*    */ import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;
/*    */ import org.noear.solon.validation.annotation.NotBlacklistChecker;
/*    */ import org.noear.solon.validation.annotation.WhitelistChecker;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 21 */     context.getWrapAsyn(ValidatorFailureHandler.class, bw -> ValidatorManager.setFailureHandler((ValidatorFailureHandler)bw.raw()));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 26 */     context.getWrapAsyn(NoRepeatSubmitChecker.class, bw -> ValidatorManager.setNoRepeatSubmitChecker((NoRepeatSubmitChecker)bw.raw()));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 31 */     context.getWrapAsyn(LoginedChecker.class, bw -> ValidatorManager.setLoginedChecker((LoginedChecker)bw.raw()));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 36 */     context.getWrapAsyn(WhitelistChecker.class, bw -> ValidatorManager.setWhitelistChecker((WhitelistChecker)bw.raw()));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     context.getWrapAsyn(NotBlacklistChecker.class, bw -> ValidatorManager.setNotBlacklistChecker((NotBlacklistChecker)bw.raw()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */