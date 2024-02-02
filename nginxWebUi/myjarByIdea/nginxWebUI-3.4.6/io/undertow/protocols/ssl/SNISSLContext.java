package io.undertow.protocols.ssl;

import javax.net.ssl.SSLContext;

public class SNISSLContext extends SSLContext {
   public SNISSLContext(SNIContextMatcher matcher) {
      super(new SNISSLContextSpi(matcher), matcher.getDefaultContext().getProvider(), matcher.getDefaultContext().getProtocol());
   }
}
