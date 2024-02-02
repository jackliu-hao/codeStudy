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
/*    */ 
/*    */ public class AuthConfigException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AuthConfigException() {}
/*    */   
/*    */   public AuthConfigException(String paramString) {
/* 20 */     super(paramString);
/*    */   }
/*    */   
/*    */   public AuthConfigException(Throwable paramThrowable) {
/* 24 */     super(paramThrowable);
/*    */   }
/*    */   
/*    */   public AuthConfigException(String paramString, Throwable paramThrowable) {
/* 28 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\AuthConfigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */