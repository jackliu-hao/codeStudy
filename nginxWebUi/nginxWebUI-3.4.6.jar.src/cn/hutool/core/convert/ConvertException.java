/*    */ package cn.hutool.core.convert;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConvertException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 4730597402855274362L;
/*    */   
/*    */   public ConvertException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public ConvertException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public ConvertException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public ConvertException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public ConvertException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\ConvertException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */