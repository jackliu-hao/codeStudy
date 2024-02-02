/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoResourceException
/*    */   extends IORuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -623254467603299129L;
/*    */   
/*    */   public NoResourceException(Throwable e) {
/* 17 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public NoResourceException(String message) {
/* 21 */     super(message);
/*    */   }
/*    */   
/*    */   public NoResourceException(String messageTemplate, Object... params) {
/* 25 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public NoResourceException(String message, Throwable throwable) {
/* 29 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public NoResourceException(Throwable throwable, String messageTemplate, Object... params) {
/* 33 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
/* 44 */     Throwable cause = getCause();
/* 45 */     return clazz.isInstance(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\NoResourceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */