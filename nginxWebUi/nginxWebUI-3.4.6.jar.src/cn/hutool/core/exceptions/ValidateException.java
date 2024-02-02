/*    */ package cn.hutool.core.exceptions;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ValidateException
/*    */   extends StatefulException
/*    */ {
/*    */   private static final long serialVersionUID = 6057602589533840889L;
/*    */   
/*    */   public ValidateException() {}
/*    */   
/*    */   public ValidateException(String msg) {
/* 17 */     super(msg);
/*    */   }
/*    */   
/*    */   public ValidateException(String messageTemplate, Object... params) {
/* 21 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public ValidateException(Throwable throwable) {
/* 25 */     super(throwable);
/*    */   }
/*    */   
/*    */   public ValidateException(String msg, Throwable throwable) {
/* 29 */     super(msg, throwable);
/*    */   }
/*    */   
/*    */   public ValidateException(int status, String msg) {
/* 33 */     super(status, msg);
/*    */   }
/*    */   
/*    */   public ValidateException(int status, Throwable throwable) {
/* 37 */     super(status, throwable);
/*    */   }
/*    */   
/*    */   public ValidateException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 41 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public ValidateException(int status, String msg, Throwable throwable) {
/* 45 */     super(status, msg, throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\ValidateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */