package io.undertow.protocols.alpn;

import javax.net.ssl.SSLEngine;

public interface ALPNProvider {
  boolean isEnabled(SSLEngine paramSSLEngine);
  
  SSLEngine setProtocols(SSLEngine paramSSLEngine, String[] paramArrayOfString);
  
  String getSelectedProtocol(SSLEngine paramSSLEngine);
  
  int getPriority();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\ALPNProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */