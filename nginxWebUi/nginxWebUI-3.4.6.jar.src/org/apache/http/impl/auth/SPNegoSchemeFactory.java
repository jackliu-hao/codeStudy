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
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class SPNegoSchemeFactory
/*    */   implements AuthSchemeFactory, AuthSchemeProvider
/*    */ {
/*    */   private final boolean stripPort;
/*    */   private final boolean useCanonicalHostname;
/*    */   
/*    */   public SPNegoSchemeFactory(boolean stripPort, boolean useCanonicalHostname) {
/* 55 */     this.stripPort = stripPort;
/* 56 */     this.useCanonicalHostname = useCanonicalHostname;
/*    */   }
/*    */ 
/*    */   
/*    */   public SPNegoSchemeFactory(boolean stripPort) {
/* 61 */     this.stripPort = stripPort;
/* 62 */     this.useCanonicalHostname = true;
/*    */   }
/*    */   
/*    */   public SPNegoSchemeFactory() {
/* 66 */     this(true, true);
/*    */   }
/*    */   
/*    */   public boolean isStripPort() {
/* 70 */     return this.stripPort;
/*    */   }
/*    */   
/*    */   public boolean isUseCanonicalHostname() {
/* 74 */     return this.useCanonicalHostname;
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthScheme newInstance(HttpParams params) {
/* 79 */     return (AuthScheme)new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthScheme create(HttpContext context) {
/* 84 */     return (AuthScheme)new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\SPNegoSchemeFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */