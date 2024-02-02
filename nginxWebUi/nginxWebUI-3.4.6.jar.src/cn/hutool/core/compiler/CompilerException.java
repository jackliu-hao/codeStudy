/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompilerException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public CompilerException(Throwable e) {
/* 16 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public CompilerException(String message) {
/* 20 */     super(message);
/*    */   }
/*    */   
/*    */   public CompilerException(String messageTemplate, Object... params) {
/* 24 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public CompilerException(String message, Throwable throwable) {
/* 28 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public CompilerException(Throwable throwable, String messageTemplate, Object... params) {
/* 32 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\CompilerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */