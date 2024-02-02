/*     */ package io.undertow.security.api;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface AuthenticationMechanism
/*     */ {
/*     */   AuthenticationMechanismOutcome authenticate(HttpServerExchange paramHttpServerExchange, SecurityContext paramSecurityContext);
/*     */   
/*     */   ChallengeResult sendChallenge(HttpServerExchange paramHttpServerExchange, SecurityContext paramSecurityContext);
/*     */   
/*     */   public enum AuthenticationMechanismOutcome
/*     */   {
/*  94 */     AUTHENTICATED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     NOT_ATTEMPTED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     NOT_AUTHENTICATED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ChallengeResult
/*     */   {
/* 115 */     public static final ChallengeResult NOT_SENT = new ChallengeResult(false);
/*     */     
/*     */     private final boolean challengeSent;
/*     */     private final Integer statusCode;
/*     */     
/*     */     public ChallengeResult(boolean challengeSent, Integer statusCode) {
/* 121 */       this.statusCode = statusCode;
/* 122 */       this.challengeSent = challengeSent;
/*     */     }
/*     */     
/*     */     public ChallengeResult(boolean challengeSent) {
/* 126 */       this(challengeSent, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Integer getDesiredResponseCode() {
/* 138 */       return this.statusCode;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isChallengeSent() {
/* 150 */       return this.challengeSent;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\AuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */