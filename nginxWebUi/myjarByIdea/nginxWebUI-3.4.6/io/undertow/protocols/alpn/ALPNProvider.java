package io.undertow.protocols.alpn;

import javax.net.ssl.SSLEngine;

public interface ALPNProvider {
   boolean isEnabled(SSLEngine var1);

   SSLEngine setProtocols(SSLEngine var1, String[] var2);

   String getSelectedProtocol(SSLEngine var1);

   int getPriority();
}
