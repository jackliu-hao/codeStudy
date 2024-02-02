package io.undertow.client;

import io.undertow.protocols.alpn.ALPNManager;
import io.undertow.protocols.alpn.ALPNProvider;
import io.undertow.protocols.ssl.SslConduit;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.util.ImmediatePooled;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLEngine;
import org.xnio.ChannelListener;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.PushBackStreamSourceConduit;
import org.xnio.ssl.SslConnection;

public class ALPNClientSelector {
   private ALPNClientSelector() {
   }

   public static void runAlpn(final SslConnection sslConnection, final ChannelListener<SslConnection> fallback, final ClientCallback<ClientConnection> failedListener, ALPNProtocol... details) {
      SslConduit conduit = UndertowXnioSsl.getSslConduit(sslConnection);
      final ALPNProvider provider = ALPNManager.INSTANCE.getProvider(conduit.getSSLEngine());
      if (provider == null) {
         fallback.handleEvent(sslConnection);
      } else {
         String[] protocols = new String[details.length];
         final Map<String, ALPNProtocol> protocolMap = new HashMap();

         for(int i = 0; i < protocols.length; ++i) {
            protocols[i] = details[i].getProtocol();
            protocolMap.put(details[i].getProtocol(), details[i]);
         }

         final SSLEngine sslEngine = provider.setProtocols(conduit.getSSLEngine(), protocols);
         conduit.setSslEngine(sslEngine);
         final AtomicReference<Boolean> handshakeDone = new AtomicReference(false);

         try {
            sslConnection.startHandshake();
            sslConnection.getHandshakeSetter().set(new ChannelListener<SslConnection>() {
               public void handleEvent(SslConnection channel) {
                  if (!(Boolean)handshakeDone.get()) {
                     handshakeDone.set(true);
                  }
               }
            });
            sslConnection.getSourceChannel().getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
               public void handleEvent(StreamSourceChannel channel) {
                  String selectedProtocol = provider.getSelectedProtocol(sslEngine);
                  if (selectedProtocol != null) {
                     this.handleSelected(selectedProtocol);
                  } else {
                     ByteBuffer buf = ByteBuffer.allocate(100);

                     try {
                        int read = channel.read(buf);
                        if (read > 0) {
                           buf.flip();
                           PushBackStreamSourceConduit pb = new PushBackStreamSourceConduit(sslConnection.getSourceChannel().getConduit());
                           pb.pushBack(new ImmediatePooled(buf));
                           sslConnection.getSourceChannel().setConduit(pb);
                        } else if (read == -1) {
                           failedListener.failed(new ClosedChannelException());
                        }

                        selectedProtocol = provider.getSelectedProtocol(sslEngine);
                        if (selectedProtocol != null) {
                           this.handleSelected(selectedProtocol);
                        } else if (read > 0 || (Boolean)handshakeDone.get()) {
                           sslConnection.getSourceChannel().suspendReads();
                           fallback.handleEvent(sslConnection);
                           return;
                        }
                     } catch (Throwable var6) {
                        IOException e = var6 instanceof IOException ? (IOException)var6 : new IOException(var6);
                        failedListener.failed(e);
                     }
                  }

               }

               private void handleSelected(String selected) {
                  if (selected.isEmpty()) {
                     sslConnection.getSourceChannel().suspendReads();
                     fallback.handleEvent(sslConnection);
                  } else {
                     ALPNProtocol details = (ALPNProtocol)protocolMap.get(selected);
                     if (details == null) {
                        sslConnection.getSourceChannel().suspendReads();
                        fallback.handleEvent(sslConnection);
                     } else {
                        sslConnection.getSourceChannel().suspendReads();
                        details.getSelected().handleEvent(sslConnection);
                     }
                  }
               }
            });
            sslConnection.getSourceChannel().resumeReads();
         } catch (IOException var11) {
            failedListener.failed(var11);
         } catch (Throwable var12) {
            failedListener.failed(new IOException(var12));
         }

      }
   }

   public static class ALPNProtocol {
      private final ChannelListener<SslConnection> selected;
      private final String protocol;

      public ALPNProtocol(ChannelListener<SslConnection> selected, String protocol) {
         this.selected = selected;
         this.protocol = protocol;
      }

      public ChannelListener<SslConnection> getSelected() {
         return this.selected;
      }

      public String getProtocol() {
         return this.protocol;
      }
   }
}
