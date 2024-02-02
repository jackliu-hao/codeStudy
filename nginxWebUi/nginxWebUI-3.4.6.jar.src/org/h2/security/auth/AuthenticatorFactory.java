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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthenticatorFactory
/*    */ {
/*    */   public static Authenticator createAuthenticator() {
/* 18 */     return DefaultAuthenticator.getInstance();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\AuthenticatorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */