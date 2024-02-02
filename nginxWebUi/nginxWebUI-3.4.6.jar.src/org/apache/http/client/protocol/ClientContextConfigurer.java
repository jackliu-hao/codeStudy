/*    */ package org.apache.http.client.protocol;
/*    */ 
/*    */ import org.apache.http.auth.AuthSchemeRegistry;
/*    */ import org.apache.http.client.CookieStore;
/*    */ import org.apache.http.client.CredentialsProvider;
/*    */ import org.apache.http.cookie.CookieSpecRegistry;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class ClientContextConfigurer
/*    */   implements ClientContext
/*    */ {
/*    */   private final HttpContext context;
/*    */   
/*    */   public ClientContextConfigurer(HttpContext context) {
/* 50 */     Args.notNull(context, "HTTP context");
/* 51 */     this.context = context;
/*    */   }
/*    */   
/*    */   public void setCookieSpecRegistry(CookieSpecRegistry registry) {
/* 55 */     this.context.setAttribute("http.cookiespec-registry", registry);
/*    */   }
/*    */   
/*    */   public void setAuthSchemeRegistry(AuthSchemeRegistry registry) {
/* 59 */     this.context.setAttribute("http.authscheme-registry", registry);
/*    */   }
/*    */   
/*    */   public void setCookieStore(CookieStore store) {
/* 63 */     this.context.setAttribute("http.cookie-store", store);
/*    */   }
/*    */   
/*    */   public void setCredentialsProvider(CredentialsProvider provider) {
/* 67 */     this.context.setAttribute("http.auth.credentials-provider", provider);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\ClientContextConfigurer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */