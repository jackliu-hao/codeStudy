package io.undertow.util;

import java.security.cert.Certificate;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;

public class Certificates {
   public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
   public static final String END_CERT = "-----END CERTIFICATE-----";

   public static String toPem(X509Certificate certificate) throws CertificateEncodingException {
      return toPem(certificate.getEncoded());
   }

   public static String toPem(Certificate certificate) throws java.security.cert.CertificateEncodingException {
      return toPem(certificate.getEncoded());
   }

   private static String toPem(byte[] encodedCertificate) {
      StringBuilder builder = new StringBuilder();
      builder.append("-----BEGIN CERTIFICATE-----");
      builder.append('\n');
      builder.append(FlexBase64.encodeString(encodedCertificate, true));
      builder.append('\n');
      builder.append("-----END CERTIFICATE-----");
      return builder.toString();
   }

   private Certificates() {
   }
}
