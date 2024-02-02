package cn.hutool.core.net;

import cn.hutool.core.io.IORuntimeException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SSLUtil {
   public static SSLContext createSSLContext(String protocol) throws IORuntimeException {
      return SSLContextBuilder.create().setProtocol(protocol).build();
   }

   public static SSLContext createSSLContext(String protocol, KeyManager keyManager, TrustManager trustManager) throws IORuntimeException {
      return createSSLContext(protocol, keyManager == null ? null : new KeyManager[]{keyManager}, trustManager == null ? null : new TrustManager[]{trustManager});
   }

   public static SSLContext createSSLContext(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers) throws IORuntimeException {
      return SSLContextBuilder.create().setProtocol(protocol).setKeyManagers(keyManagers).setTrustManagers(trustManagers).build();
   }
}
