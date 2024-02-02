package io.undertow.protocols.ssl;

import io.undertow.protocols.alpn.ALPNEngineManager;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;

public class SNIAlpnEngineManager implements ALPNEngineManager {
   public int getPriority() {
      return 100;
   }

   public boolean registerEngine(SSLEngine engine, Function<SSLEngine, SSLEngine> selectedFunction) {
      if (!(engine instanceof SNISSLEngine)) {
         return false;
      } else {
         SNISSLEngine snisslEngine = (SNISSLEngine)engine;
         snisslEngine.setSelectionCallback(selectedFunction);
         return true;
      }
   }
}
