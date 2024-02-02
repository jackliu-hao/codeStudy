package io.undertow.protocols.alpn;

import java.util.function.Function;
import javax.net.ssl.SSLEngine;

public class DefaultAlpnEngineManager implements ALPNEngineManager {
   public int getPriority() {
      return 0;
   }

   public boolean registerEngine(SSLEngine engine, Function<SSLEngine, SSLEngine> selectedFunction) {
      selectedFunction.apply(engine);
      return true;
   }
}
