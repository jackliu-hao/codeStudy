/*    */ package com.cym.ext;
/*    */ 
/*    */ import com.cym.model.Cert;
/*    */ import com.cym.model.CertCode;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class CertExt
/*    */ {
/*    */   Cert cert;
/*    */   List<CertCode> certCodes;
/*    */   
/*    */   public Cert getCert() {
/* 14 */     return this.cert;
/*    */   }
/*    */   
/*    */   public void setCert(Cert cert) {
/* 18 */     this.cert = cert;
/*    */   }
/*    */   
/*    */   public List<CertCode> getCertCodes() {
/* 22 */     return this.certCodes;
/*    */   }
/*    */   
/*    */   public void setCertCodes(List<CertCode> certCodes) {
/* 26 */     this.certCodes = certCodes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\CertExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */