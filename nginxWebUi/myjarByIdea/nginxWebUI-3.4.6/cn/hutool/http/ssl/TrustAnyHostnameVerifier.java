package cn.hutool.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class TrustAnyHostnameVerifier implements HostnameVerifier {
   public boolean verify(String hostname, SSLSession session) {
      return true;
   }
}
