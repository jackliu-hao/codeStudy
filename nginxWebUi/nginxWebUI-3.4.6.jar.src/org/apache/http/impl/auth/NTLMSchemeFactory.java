/*    */ package org.apache.http.impl.auth;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.auth.AuthScheme;
/*    */ import org.apache.http.auth.AuthSchemeFactory;
/*    */ import org.apache.http.auth.AuthSchemeProvider;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class NTLMSchemeFactory
/*    */   implements AuthSchemeFactory, AuthSchemeProvider
/*    */ {
/*    */   public AuthScheme newInstance(HttpParams params) {
/* 51 */     return (AuthScheme)new NTLMScheme();
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthScheme create(HttpContext context) {
/* 56 */     return (AuthScheme)new NTLMScheme();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\NTLMSchemeFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */