package io.undertow.security.idm;

import java.security.cert.X509Certificate;

public final class X509CertificateCredential implements Credential {
   private final X509Certificate certificate;

   public X509CertificateCredential(X509Certificate certificate) {
      this.certificate = certificate;
   }

   public X509Certificate getCertificate() {
      return this.certificate;
   }
}
