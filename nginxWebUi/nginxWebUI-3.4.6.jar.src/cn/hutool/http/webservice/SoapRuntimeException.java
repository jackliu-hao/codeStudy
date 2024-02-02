/*    */ package cn.hutool.http.webservice;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SoapRuntimeException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8247610319171014183L;
/*    */   
/*    */   public SoapRuntimeException(Throwable e) {
/* 14 */     super(e.getMessage(), e);
/*    */   }
/*    */   
/*    */   public SoapRuntimeException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */   
/*    */   public SoapRuntimeException(String messageTemplate, Object... params) {
/* 22 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public SoapRuntimeException(String message, Throwable throwable) {
/* 26 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public SoapRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
/* 30 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\webservice\SoapRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */