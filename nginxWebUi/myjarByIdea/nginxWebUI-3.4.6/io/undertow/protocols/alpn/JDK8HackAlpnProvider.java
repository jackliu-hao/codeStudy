package io.undertow.protocols.alpn;

import io.undertow.protocols.ssl.ALPNHackSSLEngine;
import java.util.Arrays;
import javax.net.ssl.SSLEngine;

public class JDK8HackAlpnProvider implements ALPNProvider {
   public boolean isEnabled(SSLEngine sslEngine) {
      return ALPNHackSSLEngine.isEnabled(sslEngine);
   }

   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
      ALPNHackSSLEngine newEngine = engine instanceof ALPNHackSSLEngine ? (ALPNHackSSLEngine)engine : new ALPNHackSSLEngine(engine);
      newEngine.setApplicationProtocols(Arrays.asList(protocols));
      return newEngine;
   }

   public String getSelectedProtocol(SSLEngine engine) {
      return ((ALPNHackSSLEngine)engine).getSelectedApplicationProtocol();
   }

   public int getPriority() {
      return 300;
   }

   public String toString() {
      return "JDK8AlpnProvider";
   }
}
