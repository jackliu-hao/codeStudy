/*    */ package org.apache.http.auth;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.security.Principal;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.ietf.jgss.GSSCredential;
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
/*    */ public class KerberosCredentials
/*    */   implements Credentials, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 487421613855550713L;
/*    */   private final GSSCredential gssCredential;
/*    */   
/*    */   public KerberosCredentials(GSSCredential gssCredential) {
/* 55 */     this.gssCredential = gssCredential;
/*    */   }
/*    */   
/*    */   public GSSCredential getGSSCredential() {
/* 59 */     return this.gssCredential;
/*    */   }
/*    */ 
/*    */   
/*    */   public Principal getUserPrincipal() {
/* 64 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPassword() {
/* 69 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\KerberosCredentials.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */