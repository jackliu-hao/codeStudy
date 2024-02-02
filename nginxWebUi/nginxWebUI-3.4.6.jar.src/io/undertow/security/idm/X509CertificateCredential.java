/*    */ package io.undertow.security.idm;
/*    */ 
/*    */ import java.security.cert.X509Certificate;
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
/*    */ public final class X509CertificateCredential
/*    */   implements Credential
/*    */ {
/*    */   private final X509Certificate certificate;
/*    */   
/*    */   public X509CertificateCredential(X509Certificate certificate) {
/* 32 */     this.certificate = certificate;
/*    */   }
/*    */   
/*    */   public X509Certificate getCertificate() {
/* 36 */     return this.certificate;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\idm\X509CertificateCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */