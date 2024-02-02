/*    */ package org.apache.http.client;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ public class ClientProtocolException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = -5596590843227115865L;
/*    */   
/*    */   public ClientProtocolException() {}
/*    */   
/*    */   public ClientProtocolException(String s) {
/* 45 */     super(s);
/*    */   }
/*    */   
/*    */   public ClientProtocolException(Throwable cause) {
/* 49 */     initCause(cause);
/*    */   }
/*    */   
/*    */   public ClientProtocolException(String message, Throwable cause) {
/* 53 */     super(message);
/* 54 */     initCause(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\ClientProtocolException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */