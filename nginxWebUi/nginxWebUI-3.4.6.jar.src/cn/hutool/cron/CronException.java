/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public CronException(Throwable e) {
/* 14 */     super(e.getMessage(), e);
/*    */   }
/*    */   
/*    */   public CronException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public CronException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public CronException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 26 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public CronException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\CronException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */