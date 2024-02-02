package org.apache.http.conn.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustSelfSignedStrategy implements TrustStrategy {
   public static final TrustSelfSignedStrategy INSTANCE = new TrustSelfSignedStrategy();

   public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      return chain.length == 1;
   }
}
