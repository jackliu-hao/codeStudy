package org.apache.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface TrustStrategy {
  boolean isTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ssl\TrustStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */