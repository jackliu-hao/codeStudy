/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ComparatorException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 4475602435485521971L;
/*    */   
/*    */   public ComparatorException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public ComparatorException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public ComparatorException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public ComparatorException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public ComparatorException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\ComparatorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */