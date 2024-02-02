/*    */ package cn.hutool.core.exceptions;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InvocationTargetRuntimeException
/*    */   extends UtilException
/*    */ {
/*    */   public InvocationTargetRuntimeException(Throwable e) {
/* 12 */     super(e);
/*    */   }
/*    */   
/*    */   public InvocationTargetRuntimeException(String message) {
/* 16 */     super(message);
/*    */   }
/*    */   
/*    */   public InvocationTargetRuntimeException(String messageTemplate, Object... params) {
/* 20 */     super(messageTemplate, params);
/*    */   }
/*    */   
/*    */   public InvocationTargetRuntimeException(String message, Throwable throwable) {
/* 24 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public InvocationTargetRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 28 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public InvocationTargetRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/* 32 */     super(throwable, messageTemplate, params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\InvocationTargetRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */