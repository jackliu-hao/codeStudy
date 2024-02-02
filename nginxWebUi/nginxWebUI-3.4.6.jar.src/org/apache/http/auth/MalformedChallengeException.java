/*    */ package org.apache.http.auth;
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
/*    */ public class MalformedChallengeException
/*    */   extends ProtocolException
/*    */ {
/*    */   private static final long serialVersionUID = 814586927989932284L;
/*    */   
/*    */   public MalformedChallengeException() {}
/*    */   
/*    */   public MalformedChallengeException(String message) {
/* 55 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MalformedChallengeException(String message, Throwable cause) {
/* 66 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\MalformedChallengeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */