/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.apache.http.HttpConnection;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.client.UserTokenHandler;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultUserTokenHandler
/*     */   implements UserTokenHandler
/*     */ {
/*  61 */   public static final DefaultUserTokenHandler INSTANCE = new DefaultUserTokenHandler();
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getUserToken(HttpContext context) {
/*  66 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */     
/*  68 */     Principal userPrincipal = null;
/*     */     
/*  70 */     AuthState targetAuthState = clientContext.getTargetAuthState();
/*  71 */     if (targetAuthState != null) {
/*  72 */       userPrincipal = getAuthPrincipal(targetAuthState);
/*  73 */       if (userPrincipal == null) {
/*  74 */         AuthState proxyAuthState = clientContext.getProxyAuthState();
/*  75 */         userPrincipal = getAuthPrincipal(proxyAuthState);
/*     */       } 
/*     */     } 
/*     */     
/*  79 */     if (userPrincipal == null) {
/*  80 */       HttpConnection conn = clientContext.getConnection();
/*  81 */       if (conn.isOpen() && conn instanceof ManagedHttpClientConnection) {
/*  82 */         SSLSession sslsession = ((ManagedHttpClientConnection)conn).getSSLSession();
/*  83 */         if (sslsession != null) {
/*  84 */           userPrincipal = sslsession.getLocalPrincipal();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  89 */     return userPrincipal;
/*     */   }
/*     */   
/*     */   private static Principal getAuthPrincipal(AuthState authState) {
/*  93 */     AuthScheme scheme = authState.getAuthScheme();
/*  94 */     if (scheme != null && scheme.isComplete() && scheme.isConnectionBased()) {
/*  95 */       Credentials creds = authState.getCredentials();
/*  96 */       if (creds != null) {
/*  97 */         return creds.getUserPrincipal();
/*     */       }
/*     */     } 
/* 100 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultUserTokenHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */