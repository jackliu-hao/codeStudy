/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.Oid;
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
/*     */ public class SPNegoScheme
/*     */   extends GGSSchemeBase
/*     */ {
/*     */   private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
/*     */   
/*     */   public SPNegoScheme(boolean stripPort, boolean useCanonicalHostname) {
/*  52 */     super(stripPort, useCanonicalHostname);
/*     */   }
/*     */   
/*     */   public SPNegoScheme(boolean stripPort) {
/*  56 */     super(stripPort);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SPNegoScheme() {}
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/*  65 */     return "Negotiate";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
/*  85 */     return super.authenticate(credentials, request, context);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
/*  90 */     return super.generateToken(input, authServer);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
/*  95 */     return generateGSSToken(input, new Oid("1.3.6.1.5.5.2"), authServer, credentials);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 106 */     Args.notNull(name, "Parameter name");
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRealm() {
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 128 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\SPNegoScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */