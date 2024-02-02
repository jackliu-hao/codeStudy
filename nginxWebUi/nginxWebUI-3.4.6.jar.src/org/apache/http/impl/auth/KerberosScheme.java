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
/*     */ public class KerberosScheme
/*     */   extends GGSSchemeBase
/*     */ {
/*     */   private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
/*     */   
/*     */   public KerberosScheme(boolean stripPort, boolean useCanonicalHostname) {
/*  51 */     super(stripPort, useCanonicalHostname);
/*     */   }
/*     */   
/*     */   public KerberosScheme(boolean stripPort) {
/*  55 */     super(stripPort);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public KerberosScheme() {}
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/*  64 */     return "Kerberos";
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
/*  84 */     return super.authenticate(credentials, request, context);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
/*  89 */     return super.generateToken(input, authServer);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
/*  94 */     return generateGSSToken(input, new Oid("1.2.840.113554.1.2.2"), authServer, credentials);
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
/* 105 */     Args.notNull(name, "Parameter name");
/* 106 */     return null;
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
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 127 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\KerberosScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */