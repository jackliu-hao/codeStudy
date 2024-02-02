/*    */ package cn.hutool.core.bean;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -8096998667745023423L;
/*    */   
/*    */   public BeanException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public BeanException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public BeanException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public BeanException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public BeanException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */