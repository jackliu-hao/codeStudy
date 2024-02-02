/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ThreadException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 5253124428623713216L;
/*    */   
/*    */   public ThreadException(Throwable e) {
/* 16 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public ThreadException(String message) {
/* 20 */     super(message);
/*    */   }
/*    */   
/*    */   public ThreadException(String messageTemplate, Object... params) {
/* 24 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public ThreadException(String message, Throwable throwable) {
/* 28 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public ThreadException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 32 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public ThreadException(Throwable throwable, String messageTemplate, Object... params) {
/* 36 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\ThreadException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */