/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
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
/*     */ @Deprecated
/*     */ public class NegotiateScheme
/*     */   extends GGSSchemeBase
/*     */ {
/*  53 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
/*     */ 
/*     */   
/*     */   private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
/*     */   
/*     */   private final SpnegoTokenGenerator spengoGenerator;
/*     */ 
/*     */   
/*     */   public NegotiateScheme(SpnegoTokenGenerator spengoGenerator, boolean stripPort) {
/*  65 */     super(stripPort);
/*  66 */     this.spengoGenerator = spengoGenerator;
/*     */   }
/*     */   
/*     */   public NegotiateScheme(SpnegoTokenGenerator spengoGenerator) {
/*  70 */     this(spengoGenerator, false);
/*     */   }
/*     */   
/*     */   public NegotiateScheme() {
/*  74 */     this(null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/*  84 */     return "Negotiate";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
/*  91 */     return authenticate(credentials, request, null);
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
/*     */   
/*     */   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
/* 112 */     return super.authenticate(credentials, request, context);
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
/* 117 */     return super.generateToken(input, authServer);
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
/*     */   protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
/* 137 */     Oid negotiationOid = new Oid("1.3.6.1.5.5.2");
/*     */     
/* 139 */     byte[] token = input;
/* 140 */     boolean tryKerberos = false;
/*     */     try {
/* 142 */       token = generateGSSToken(token, negotiationOid, authServer, credentials);
/* 143 */     } catch (GSSException ex) {
/*     */ 
/*     */       
/* 146 */       if (ex.getMajor() == 2) {
/* 147 */         this.log.debug("GSSException BAD_MECH, retry with Kerberos MECH");
/* 148 */         tryKerberos = true;
/*     */       } else {
/* 150 */         throw ex;
/*     */       } 
/*     */     } 
/*     */     
/* 154 */     if (tryKerberos) {
/*     */       
/* 156 */       this.log.debug("Using Kerberos MECH 1.2.840.113554.1.2.2");
/* 157 */       negotiationOid = new Oid("1.2.840.113554.1.2.2");
/* 158 */       token = generateGSSToken(token, negotiationOid, authServer, credentials);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 164 */       if (token != null && this.spengoGenerator != null) {
/*     */         try {
/* 166 */           token = this.spengoGenerator.generateSpnegoDERObject(token);
/* 167 */         } catch (IOException ex) {
/* 168 */           this.log.error(ex.getMessage(), ex);
/*     */         } 
/*     */       }
/*     */     } 
/* 172 */     return token;
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
/*     */   public String getParameter(String name) {
/* 187 */     Args.notNull(name, "Parameter name");
/* 188 */     return null;
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
/* 199 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/* 210 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\NegotiateScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */