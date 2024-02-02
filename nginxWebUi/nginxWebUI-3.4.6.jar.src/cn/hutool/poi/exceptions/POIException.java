/*    */ package cn.hutool.poi.exceptions;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class POIException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 2711633732613506552L;
/*    */   
/*    */   public POIException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public POIException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public POIException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public POIException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public POIException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public POIException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\exceptions\POIException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */