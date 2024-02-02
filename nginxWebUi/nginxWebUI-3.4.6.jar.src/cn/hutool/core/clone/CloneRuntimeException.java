/*    */ package cn.hutool.core.clone;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CloneRuntimeException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 6774837422188798989L;
/*    */   
/*    */   public CloneRuntimeException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public CloneRuntimeException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public CloneRuntimeException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public CloneRuntimeException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public CloneRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\clone\CloneRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */