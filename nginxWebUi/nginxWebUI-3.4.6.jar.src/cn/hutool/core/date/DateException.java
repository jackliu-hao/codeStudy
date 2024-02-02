/*    */ package cn.hutool.core.date;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DateException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public DateException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public DateException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public DateException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public DateException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public DateException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */