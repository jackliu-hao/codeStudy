/*    */ package io.undertow.util;
/*    */ 
/*    */ import java.security.cert.Certificate;
/*    */ import java.security.cert.CertificateEncodingException;
/*    */ import javax.security.cert.CertificateEncodingException;
/*    */ import javax.security.cert.X509Certificate;
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
/*    */ public class Certificates
/*    */ {
/*    */   public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
/*    */   public static final String END_CERT = "-----END CERTIFICATE-----";
/*    */   
/*    */   public static String toPem(X509Certificate certificate) throws CertificateEncodingException {
/* 33 */     return toPem(certificate.getEncoded());
/*    */   }
/*    */ 
/*    */   
/*    */   public static String toPem(Certificate certificate) throws CertificateEncodingException {
/* 38 */     return toPem(certificate.getEncoded());
/*    */   }
/*    */   
/*    */   private static String toPem(byte[] encodedCertificate) {
/* 42 */     StringBuilder builder = new StringBuilder();
/* 43 */     builder.append("-----BEGIN CERTIFICATE-----");
/* 44 */     builder.append('\n');
/* 45 */     builder.append(FlexBase64.encodeString(encodedCertificate, true));
/* 46 */     builder.append('\n');
/* 47 */     builder.append("-----END CERTIFICATE-----");
/* 48 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Certificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */