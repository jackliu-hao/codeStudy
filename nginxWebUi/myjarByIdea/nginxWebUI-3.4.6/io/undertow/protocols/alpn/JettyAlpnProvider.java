package io.undertow.protocols.alpn;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.alpn.ALPN;

public class JettyAlpnProvider implements ALPNProvider {
   private static final String PROTOCOL_KEY = JettyAlpnProvider.class.getName() + ".protocol";
   private static final boolean ENABLED = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         try {
            Class.forName("org.eclipse.jetty.alpn.ALPN", true, JettyAlpnProvider.class.getClassLoader());
            return true;
         } catch (ClassNotFoundException var2) {
            return false;
         }
      }
   });

   public boolean isEnabled(SSLEngine sslEngine) {
      return ENABLED;
   }

   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
      return JettyAlpnProvider.Impl.setProtocols(engine, protocols);
   }

   public String getSelectedProtocol(SSLEngine engine) {
      SSLSession handshake = engine.getHandshakeSession();
      if (handshake != null) {
         return (String)handshake.getValue(PROTOCOL_KEY);
      } else {
         handshake = engine.getSession();
         return handshake != null ? (String)handshake.getValue(PROTOCOL_KEY) : null;
      }
   }

   public int getPriority() {
      return 100;
   }

   public String toString() {
      return "JettyAlpnProvider";
   }

   private static class ALPNClientSelectionProvider implements ALPN.ClientProvider {
      final List<String> protocols;
      private String selected;
      private final SSLEngine sslEngine;

      private ALPNClientSelectionProvider(List<String> protocols, SSLEngine sslEngine) {
         this.protocols = protocols;
         this.sslEngine = sslEngine;
      }

      public boolean supports() {
         return true;
      }

      public List<String> protocols() {
         return this.protocols;
      }

      public void unsupported() {
         ALPN.remove(this.sslEngine);
         this.selected = "";
      }

      public void selected(String s) {
         ALPN.remove(this.sslEngine);
         this.selected = s;
         this.sslEngine.getHandshakeSession().putValue(JettyAlpnProvider.PROTOCOL_KEY, this.selected);
      }

      // $FF: synthetic method
      ALPNClientSelectionProvider(List x0, SSLEngine x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class Impl {
      static SSLEngine setProtocols(final SSLEngine engine, final String[] protocols) {
         if (engine.getUseClientMode()) {
            ALPN.put(engine, new ALPNClientSelectionProvider(Arrays.asList(protocols), engine));
         } else {
            ALPN.put(engine, new ALPN.ServerProvider() {
               public void unsupported() {
                  ALPN.remove(engine);
               }

               public String select(List<String> strings) {
                  ALPN.remove(engine);
                  String[] var2 = protocols;
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     String p = var2[var4];
                     if (strings.contains(p)) {
                        engine.getHandshakeSession().putValue(JettyAlpnProvider.PROTOCOL_KEY, p);
                        return p;
                     }
                  }

                  return null;
               }
            });
         }

         return engine;
      }
   }
}
