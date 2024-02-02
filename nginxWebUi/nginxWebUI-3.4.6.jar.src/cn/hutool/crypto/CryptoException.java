/*    */ package cn.hutool.crypto;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CryptoException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8068509879445395353L;
/*    */   
/*    */   public CryptoException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public CryptoException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public CryptoException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public CryptoException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public CryptoException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public CryptoException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\CryptoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */