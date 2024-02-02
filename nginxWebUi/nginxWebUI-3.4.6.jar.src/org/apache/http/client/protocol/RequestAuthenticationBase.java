/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.auth.AuthOption;
/*     */ import org.apache.http.auth.AuthProtocolState;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.ContextAwareAuthScheme;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ @Deprecated
/*     */ abstract class RequestAuthenticationBase
/*     */   implements HttpRequestInterceptor
/*     */ {
/*  52 */   final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void process(AuthState authState, HttpRequest request, HttpContext context) {
/*     */     Queue<AuthOption> authOptions;
/*  62 */     AuthScheme authScheme = authState.getAuthScheme();
/*  63 */     Credentials creds = authState.getCredentials();
/*  64 */     switch (authState.getState()) {
/*     */       case FAILURE:
/*     */         return;
/*     */       case SUCCESS:
/*  68 */         ensureAuthScheme(authScheme);
/*  69 */         if (authScheme.isConnectionBased()) {
/*     */           return;
/*     */         }
/*     */         break;
/*     */       case CHALLENGED:
/*  74 */         authOptions = authState.getAuthOptions();
/*  75 */         if (authOptions != null) {
/*  76 */           while (!authOptions.isEmpty()) {
/*  77 */             AuthOption authOption = authOptions.remove();
/*  78 */             authScheme = authOption.getAuthScheme();
/*  79 */             creds = authOption.getCredentials();
/*  80 */             authState.update(authScheme, creds);
/*  81 */             if (this.log.isDebugEnabled()) {
/*  82 */               this.log.debug("Generating response to an authentication challenge using " + authScheme.getSchemeName() + " scheme");
/*     */             }
/*     */             
/*     */             try {
/*  86 */               Header header = authenticate(authScheme, creds, request, context);
/*  87 */               request.addHeader(header);
/*     */               break;
/*  89 */             } catch (AuthenticationException ex) {
/*  90 */               if (this.log.isWarnEnabled()) {
/*  91 */                 this.log.warn(authScheme + " authentication error: " + ex.getMessage());
/*     */               }
/*     */             } 
/*     */           } 
/*     */           return;
/*     */         } 
/*  97 */         ensureAuthScheme(authScheme);
/*     */         break;
/*     */     } 
/* 100 */     if (authScheme != null) {
/*     */       try {
/* 102 */         Header header = authenticate(authScheme, creds, request, context);
/* 103 */         request.addHeader(header);
/* 104 */       } catch (AuthenticationException ex) {
/* 105 */         if (this.log.isErrorEnabled()) {
/* 106 */           this.log.error(authScheme + " authentication error: " + ex.getMessage());
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void ensureAuthScheme(AuthScheme authScheme) {
/* 113 */     Asserts.notNull(authScheme, "Auth scheme");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Header authenticate(AuthScheme authScheme, Credentials creds, HttpRequest request, HttpContext context) throws AuthenticationException {
/* 121 */     Asserts.notNull(authScheme, "Auth scheme");
/* 122 */     if (authScheme instanceof ContextAwareAuthScheme) {
/* 123 */       return ((ContextAwareAuthScheme)authScheme).authenticate(creds, request, context);
/*     */     }
/* 125 */     return authScheme.authenticate(creds, request);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestAuthenticationBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */