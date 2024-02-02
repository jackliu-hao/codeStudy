/*    */ package org.h2.security.auth;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthenticationException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AuthenticationException() {}
/*    */   
/*    */   public AuthenticationException(String paramString) {
/* 19 */     super(paramString);
/*    */   }
/*    */   
/*    */   public AuthenticationException(Throwable paramThrowable) {
/* 23 */     super(paramThrowable);
/*    */   }
/*    */   
/*    */   public AuthenticationException(String paramString, Throwable paramThrowable) {
/* 27 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\AuthenticationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */