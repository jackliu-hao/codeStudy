/*    */ package org.apache.http.cookie;
/*    */ 
/*    */ import org.apache.http.ProtocolException;
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
/*    */ public class MalformedCookieException
/*    */   extends ProtocolException
/*    */ {
/*    */   private static final long serialVersionUID = -6695462944287282185L;
/*    */   
/*    */   public MalformedCookieException() {}
/*    */   
/*    */   public MalformedCookieException(String message) {
/* 56 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MalformedCookieException(String message, Throwable cause) {
/* 67 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\MalformedCookieException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */