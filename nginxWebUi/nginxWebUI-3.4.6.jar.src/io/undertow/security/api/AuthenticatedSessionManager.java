/*    */ package io.undertow.security.api;
/*    */ 
/*    */ import io.undertow.security.idm.Account;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.AttachmentKey;
/*    */ import java.io.Serializable;
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
/*    */ public interface AuthenticatedSessionManager
/*    */ {
/* 37 */   public static final AttachmentKey<AuthenticatedSessionManager> ATTACHMENT_KEY = AttachmentKey.create(AuthenticatedSessionManager.class);
/*    */   
/*    */   AuthenticatedSession lookupSession(HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   void clearSession(HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   public static class AuthenticatedSession
/*    */     implements Serializable {
/*    */     private final Account account;
/*    */     private final String mechanism;
/*    */     
/*    */     public AuthenticatedSession(Account account, String mechanism) {
/* 49 */       this.account = account;
/* 50 */       this.mechanism = mechanism;
/*    */     }
/*    */     
/*    */     public Account getAccount() {
/* 54 */       return this.account;
/*    */     }
/*    */     
/*    */     public String getMechanism() {
/* 58 */       return this.mechanism;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\AuthenticatedSessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */