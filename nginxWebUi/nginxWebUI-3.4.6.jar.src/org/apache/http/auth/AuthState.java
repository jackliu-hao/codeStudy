/*     */ package org.apache.http.auth;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class AuthState
/*     */ {
/*  57 */   private AuthProtocolState state = AuthProtocolState.UNCHALLENGED;
/*     */   
/*     */   private AuthScheme authScheme;
/*     */   
/*     */   private AuthScope authScope;
/*     */   private Credentials credentials;
/*     */   private Queue<AuthOption> authOptions;
/*     */   
/*     */   public void reset() {
/*  66 */     this.state = AuthProtocolState.UNCHALLENGED;
/*  67 */     this.authOptions = null;
/*  68 */     this.authScheme = null;
/*  69 */     this.authScope = null;
/*  70 */     this.credentials = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthProtocolState getState() {
/*  77 */     return this.state;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setState(AuthProtocolState state) {
/*  84 */     this.state = (state != null) ? state : AuthProtocolState.UNCHALLENGED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScheme getAuthScheme() {
/*  91 */     return this.authScheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Credentials getCredentials() {
/*  98 */     return this.credentials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(AuthScheme authScheme, Credentials credentials) {
/* 110 */     Args.notNull(authScheme, "Auth scheme");
/* 111 */     Args.notNull(credentials, "Credentials");
/* 112 */     this.authScheme = authScheme;
/* 113 */     this.credentials = credentials;
/* 114 */     this.authOptions = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Queue<AuthOption> getAuthOptions() {
/* 123 */     return this.authOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasAuthOptions() {
/* 133 */     return (this.authOptions != null && !this.authOptions.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 142 */     return (this.authScheme != null && this.authScheme.isConnectionBased());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(Queue<AuthOption> authOptions) {
/* 153 */     Args.notEmpty(authOptions, "Queue of auth options");
/* 154 */     this.authOptions = authOptions;
/* 155 */     this.authScheme = null;
/* 156 */     this.credentials = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void invalidate() {
/* 166 */     reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isValid() {
/* 174 */     return (this.authScheme != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setAuthScheme(AuthScheme authScheme) {
/* 186 */     if (authScheme == null) {
/* 187 */       reset();
/*     */       return;
/*     */     } 
/* 190 */     this.authScheme = authScheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setCredentials(Credentials credentials) {
/* 202 */     this.credentials = credentials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public AuthScope getAuthScope() {
/* 214 */     return this.authScope;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setAuthScope(AuthScope authScope) {
/* 226 */     this.authScope = authScope;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 231 */     StringBuilder buffer = new StringBuilder();
/* 232 */     buffer.append("state:").append(this.state).append(";");
/* 233 */     if (this.authScheme != null) {
/* 234 */       buffer.append("auth scheme:").append(this.authScheme.getSchemeName()).append(";");
/*     */     }
/* 236 */     if (this.credentials != null) {
/* 237 */       buffer.append("credentials present");
/*     */     }
/* 239 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\AuthState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */