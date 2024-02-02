/*    */ package org.apache.http.impl.auth;
/*    */ 
/*    */ import java.nio.charset.Charset;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class DigestSchemeFactory
/*    */   implements AuthSchemeFactory, AuthSchemeProvider
/*    */ {
/*    */   private final Charset charset;
/*    */   
/*    */   public DigestSchemeFactory(Charset charset) {
/* 57 */     this.charset = charset;
/*    */   }
/*    */   
/*    */   public DigestSchemeFactory() {
/* 61 */     this(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthScheme newInstance(HttpParams params) {
/* 66 */     return (AuthScheme)new DigestScheme();
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthScheme create(HttpContext context) {
/* 71 */     return (AuthScheme)new DigestScheme(this.charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\DigestSchemeFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */