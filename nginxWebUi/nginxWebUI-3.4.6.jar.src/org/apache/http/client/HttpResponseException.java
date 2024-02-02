/*    */ package org.apache.http.client;
/*    */ 
/*    */ import org.apache.http.util.TextUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpResponseException
/*    */   extends ClientProtocolException
/*    */ {
/*    */   private static final long serialVersionUID = -7186627969477257933L;
/*    */   private final int statusCode;
/*    */   private final String reasonPhrase;
/*    */   
/*    */   public HttpResponseException(int statusCode, String reasonPhrase) {
/* 44 */     super(String.format("status code: %d" + (TextUtils.isBlank(reasonPhrase) ? "" : ", reason phrase: %s"), new Object[] { Integer.valueOf(statusCode), reasonPhrase }));
/*    */     
/* 46 */     this.statusCode = statusCode;
/* 47 */     this.reasonPhrase = reasonPhrase;
/*    */   }
/*    */   
/*    */   public int getStatusCode() {
/* 51 */     return this.statusCode;
/*    */   }
/*    */   
/*    */   public String getReasonPhrase() {
/* 55 */     return this.reasonPhrase;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\HttpResponseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */