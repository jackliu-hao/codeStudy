/*    */ package cn.hutool.extra.expression;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExpressionException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ExpressionException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public ExpressionException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public ExpressionException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public ExpressionException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public ExpressionException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public ExpressionException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\ExpressionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */