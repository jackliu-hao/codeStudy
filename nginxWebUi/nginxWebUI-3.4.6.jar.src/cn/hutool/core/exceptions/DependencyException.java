/*    */ package cn.hutool.core.exceptions;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DependencyException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public DependencyException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public DependencyException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public DependencyException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public DependencyException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public DependencyException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public DependencyException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\DependencyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */