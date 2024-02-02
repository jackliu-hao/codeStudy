/*    */ package cn.hutool.jwt;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JWTException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JWTException(Throwable e) {
/* 16 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public JWTException(String message) {
/* 20 */     super(message);
/*    */   }
/*    */   
/*    */   public JWTException(String messageTemplate, Object... params) {
/* 24 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public JWTException(String message, Throwable cause) {
/* 28 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public JWTException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 32 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public JWTException(Throwable throwable, String messageTemplate, Object... params) {
/* 36 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWTException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */