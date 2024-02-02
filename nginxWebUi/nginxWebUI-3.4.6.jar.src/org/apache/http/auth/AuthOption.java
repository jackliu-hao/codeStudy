/*    */ package org.apache.http.auth;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public final class AuthOption
/*    */ {
/*    */   private final AuthScheme authScheme;
/*    */   private final Credentials creds;
/*    */   
/*    */   public AuthOption(AuthScheme authScheme, Credentials creds) {
/* 44 */     Args.notNull(authScheme, "Auth scheme");
/* 45 */     Args.notNull(creds, "User credentials");
/* 46 */     this.authScheme = authScheme;
/* 47 */     this.creds = creds;
/*    */   }
/*    */   
/*    */   public AuthScheme getAuthScheme() {
/* 51 */     return this.authScheme;
/*    */   }
/*    */   
/*    */   public Credentials getCredentials() {
/* 55 */     return this.creds;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return this.authScheme.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\AuthOption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */