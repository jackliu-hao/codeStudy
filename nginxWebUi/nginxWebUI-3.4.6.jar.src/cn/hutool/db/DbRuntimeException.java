/*    */ package cn.hutool.db;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DbRuntimeException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 3624487785708765623L;
/*    */   
/*    */   public DbRuntimeException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public DbRuntimeException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public DbRuntimeException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public DbRuntimeException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public DbRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 30 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public DbRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/* 34 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\DbRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */