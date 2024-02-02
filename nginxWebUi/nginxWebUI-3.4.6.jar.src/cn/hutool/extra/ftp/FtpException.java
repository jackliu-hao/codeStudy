/*    */ package cn.hutool.extra.ftp;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FtpException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -8490149159895201756L;
/*    */   
/*    */   public FtpException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public FtpException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public FtpException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public FtpException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public FtpException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public FtpException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ftp\FtpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */