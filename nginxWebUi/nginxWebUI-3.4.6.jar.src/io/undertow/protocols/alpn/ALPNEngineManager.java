package io.undertow.protocols.alpn;

import java.util.function.Function;
import javax.net.ssl.SSLEngine;

public interface ALPNEngineManager {
  int getPriority();
  
  boolean registerEngine(SSLEngine paramSSLEngine, Function<SSLEngine, SSLEngine> paramFunction);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\ALPNEngineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */