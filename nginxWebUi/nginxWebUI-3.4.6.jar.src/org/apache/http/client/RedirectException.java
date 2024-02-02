/*    */ package org.apache.http.client;
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
/*    */ public class RedirectException
/*    */   extends ProtocolException
/*    */ {
/*    */   private static final long serialVersionUID = 4418824536372559326L;
/*    */   
/*    */   public RedirectException() {}
/*    */   
/*    */   public RedirectException(String message) {
/* 54 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RedirectException(String message, Throwable cause) {
/* 65 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\RedirectException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */