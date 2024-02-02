/*    */ package cn.hutool.json;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   public JSONException(Throwable e) {
/* 16 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public JSONException(String message) {
/* 20 */     super(message);
/*    */   }
/*    */   
/*    */   public JSONException(String messageTemplate, Object... params) {
/* 24 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public JSONException(String message, Throwable cause) {
/* 28 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public JSONException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 32 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public JSONException(Throwable throwable, String messageTemplate, Object... params) {
/* 36 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */