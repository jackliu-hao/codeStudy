/*    */ package cn.hutool.core.exceptions;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatefulException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 6057602589533840889L;
/*    */   private int status;
/*    */   
/*    */   public StatefulException() {}
/*    */   
/*    */   public StatefulException(String msg) {
/* 20 */     super(msg);
/*    */   }
/*    */   
/*    */   public StatefulException(String messageTemplate, Object... params) {
/* 24 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public StatefulException(Throwable throwable) {
/* 28 */     super(throwable);
/*    */   }
/*    */   
/*    */   public StatefulException(String msg, Throwable throwable) {
/* 32 */     super(msg, throwable);
/*    */   }
/*    */   
/*    */   public StatefulException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 36 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public StatefulException(int status, String msg) {
/* 40 */     super(msg);
/* 41 */     this.status = status;
/*    */   }
/*    */   
/*    */   public StatefulException(int status, Throwable throwable) {
/* 45 */     super(throwable);
/* 46 */     this.status = status;
/*    */   }
/*    */   
/*    */   public StatefulException(int status, String msg, Throwable throwable) {
/* 50 */     super(msg, throwable);
/* 51 */     this.status = status;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getStatus() {
/* 58 */     return this.status;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\StatefulException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */