/*    */ package cn.hutool.extra.mail;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MailException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public MailException(Throwable e) {
/* 14 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public MailException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public MailException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public MailException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public MailException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 30 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public MailException(Throwable throwable, String messageTemplate, Object... params) {
/* 34 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\MailException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */