/*    */ package cn.hutool.http;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public HttpException(Throwable e) {
/* 14 */     super(e.getMessage(), e);
/*    */   }
/*    */   
/*    */   public HttpException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public HttpException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public HttpException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public HttpException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 30 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public HttpException(Throwable throwable, String messageTemplate, Object... params) {
/* 34 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */