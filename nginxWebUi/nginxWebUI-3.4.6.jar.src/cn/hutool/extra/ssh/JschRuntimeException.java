/*    */ package cn.hutool.extra.ssh;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JschRuntimeException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public JschRuntimeException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public JschRuntimeException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public JschRuntimeException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public JschRuntimeException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public JschRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 30 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public JschRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/* 34 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\JschRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */