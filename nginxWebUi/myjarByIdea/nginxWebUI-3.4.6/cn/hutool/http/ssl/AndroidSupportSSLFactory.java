package cn.hutool.http.ssl;

import cn.hutool.core.io.IORuntimeException;

public class AndroidSupportSSLFactory extends CustomProtocolsSSLFactory {
   private static final String[] protocols = new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};

   public AndroidSupportSSLFactory() throws IORuntimeException {
      super(protocols);
   }
}
