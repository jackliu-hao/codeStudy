package io.undertow.protocols.alpn;

import java.util.function.Function;
import javax.net.ssl.SSLEngine;

public interface ALPNEngineManager {
   int getPriority();

   boolean registerEngine(SSLEngine var1, Function<SSLEngine, SSLEngine> var2);
}
