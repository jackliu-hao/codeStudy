package io.undertow.server.protocol.http;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.alpn.ALPNManager;
import io.undertow.protocols.alpn.ALPNProvider;
import io.undertow.protocols.ssl.SslConduit;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.AggregateConnectorStatistics;
import io.undertow.server.ConnectorStatistics;
import io.undertow.server.DelegateOpenListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.OpenListener;
import io.undertow.server.XnioByteBufferPool;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.ssl.SslConnection;

public class AlpnOpenListener implements ChannelListener<StreamConnection>, OpenListener {
   public static final String REQUIRED_CIPHER = "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256";
   public static final String IBM_REQUIRED_CIPHER = "SSL_ECDHE_RSA_WITH_AES_128_GCM_SHA256";
   private static final Set<String> REQUIRED_PROTOCOLS = Collections.unmodifiableSet(new HashSet(Arrays.asList("TLSv1.2", "TLSv1.3")));
   private final ALPNManager alpnManager;
   private final ByteBufferPool bufferPool;
   private final Map<String, ListenerEntry> listeners;
   private String[] protocols;
   private final String fallbackProtocol;
   private volatile HttpHandler rootHandler;
   private volatile OptionMap undertowOptions;
   private volatile boolean statisticsEnabled;
   private volatile boolean providerLogged;
   private volatile boolean alpnFailLogged;

   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions, DelegateOpenListener httpListener) {
      this(bufferPool, undertowOptions, "http/1.1", httpListener);
   }

   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions) {
      this((Pool)bufferPool, undertowOptions, (String)null, (DelegateOpenListener)null);
   }

   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions, String fallbackProtocol, DelegateOpenListener fallbackListener) {
      this((ByteBufferPool)(new XnioByteBufferPool(bufferPool)), undertowOptions, fallbackProtocol, fallbackListener);
   }

   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions, DelegateOpenListener httpListener) {
      this(bufferPool, undertowOptions, "http/1.1", httpListener);
   }

   public AlpnOpenListener(ByteBufferPool bufferPool) {
      this((ByteBufferPool)bufferPool, OptionMap.EMPTY, (String)null, (DelegateOpenListener)null);
   }

   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions) {
      this((ByteBufferPool)bufferPool, undertowOptions, (String)null, (DelegateOpenListener)null);
   }

   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions, String fallbackProtocol, DelegateOpenListener fallbackListener) {
      this.alpnManager = ALPNManager.INSTANCE;
      this.listeners = new HashMap();
      this.bufferPool = bufferPool;
      this.undertowOptions = undertowOptions;
      this.fallbackProtocol = fallbackProtocol;
      this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
      if (fallbackProtocol != null && fallbackListener != null) {
         this.addProtocol(fallbackProtocol, fallbackListener, 0);
      }

   }

   public HttpHandler getRootHandler() {
      return this.rootHandler;
   }

   public void setRootHandler(HttpHandler rootHandler) {
      this.rootHandler = rootHandler;
      Iterator var2 = this.listeners.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, ListenerEntry> delegate = (Map.Entry)var2.next();
         ((ListenerEntry)delegate.getValue()).listener.setRootHandler(rootHandler);
      }

   }

   public OptionMap getUndertowOptions() {
      return this.undertowOptions;
   }

   public void setUndertowOptions(OptionMap undertowOptions) {
      if (undertowOptions == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
      } else {
         this.undertowOptions = undertowOptions;
         Iterator var2 = this.listeners.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, ListenerEntry> delegate = (Map.Entry)var2.next();
            ((ListenerEntry)delegate.getValue()).listener.setRootHandler(this.rootHandler);
         }

         this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
      }
   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public ConnectorStatistics getConnectorStatistics() {
      if (this.statisticsEnabled) {
         List<ConnectorStatistics> stats = new ArrayList();
         Iterator var2 = this.listeners.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, ListenerEntry> l = (Map.Entry)var2.next();
            ConnectorStatistics c = ((ListenerEntry)l.getValue()).listener.getConnectorStatistics();
            if (c != null) {
               stats.add(c);
            }
         }

         return new AggregateConnectorStatistics((ConnectorStatistics[])stats.toArray(new ConnectorStatistics[stats.size()]));
      } else {
         return null;
      }
   }

   public void closeConnections() {
      Iterator var1 = this.listeners.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<String, ListenerEntry> i = (Map.Entry)var1.next();
         ((ListenerEntry)i.getValue()).listener.closeConnections();
      }

   }

   public AlpnOpenListener addProtocol(String name, DelegateOpenListener listener, int weight) {
      this.listeners.put(name, new ListenerEntry(listener, weight, name));
      List<ListenerEntry> list = new ArrayList(this.listeners.values());
      Collections.sort(list);
      this.protocols = new String[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         this.protocols[i] = ((ListenerEntry)list.get(i)).protocol;
      }

      return this;
   }

   public void handleEvent(final StreamConnection channel) {
      if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
         UndertowLogger.REQUEST_LOGGER.tracef("Opened connection with %s", channel.getPeerAddress());
      }

      SslConduit sslConduit = UndertowXnioSsl.getSslConduit((SslConnection)channel);
      SSLEngine originalSSlEngine = sslConduit.getSSLEngine();
      final CompletableFuture<SelectedAlpn> selectedALPNEngine = new CompletableFuture();
      this.alpnManager.registerEngineCallback(originalSSlEngine, new SSLConduitUpdater(sslConduit, new Function<SSLEngine, SSLEngine>() {
         public SSLEngine apply(SSLEngine engine) {
            if (!AlpnOpenListener.engineSupportsHTTP2(engine)) {
               if (!AlpnOpenListener.this.alpnFailLogged) {
                  synchronized(this) {
                     if (!AlpnOpenListener.this.alpnFailLogged) {
                        UndertowLogger.REQUEST_LOGGER.debugf("ALPN has been configured however %s is not present or TLS1.2 is not enabled, falling back to default protocol", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
                        AlpnOpenListener.this.alpnFailLogged = true;
                     }
                  }
               }

               if (AlpnOpenListener.this.fallbackProtocol != null) {
                  ListenerEntry listener = (ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol);
                  if (listener != null) {
                     selectedALPNEngine.complete((Object)null);
                     return engine;
                  }
               }
            }

            final ALPNProvider provider = AlpnOpenListener.this.alpnManager.getProvider(engine);
            if (provider == null) {
               if (!AlpnOpenListener.this.providerLogged) {
                  synchronized(this) {
                     if (!AlpnOpenListener.this.providerLogged) {
                        UndertowLogger.REQUEST_LOGGER.debugf("ALPN has been configured however no provider could be found for engine %s for connector at %s", engine, channel.getLocalAddress());
                        AlpnOpenListener.this.providerLogged = true;
                     }
                  }
               }

               if (AlpnOpenListener.this.fallbackProtocol != null) {
                  ListenerEntry listenerx = (ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol);
                  if (listenerx != null) {
                     selectedALPNEngine.complete((Object)null);
                     return engine;
                  }
               }

               UndertowLogger.REQUEST_LOGGER.debugf("No ALPN provider available and no fallback defined", new Object[0]);
               IoUtils.safeClose((Closeable)channel);
               selectedALPNEngine.complete((Object)null);
               return engine;
            } else {
               if (!AlpnOpenListener.this.providerLogged) {
                  synchronized(this) {
                     if (!AlpnOpenListener.this.providerLogged) {
                        UndertowLogger.REQUEST_LOGGER.debugf("Using ALPN provider %s for connector at %s", provider, channel.getLocalAddress());
                        AlpnOpenListener.this.providerLogged = true;
                     }
                  }
               }

               final SSLEngine newEngine = provider.setProtocols(engine, AlpnOpenListener.this.protocols);
               ALPNLimitingSSLEngine alpnLimitingSSLEngine = new ALPNLimitingSSLEngine(newEngine, new Runnable() {
                  public void run() {
                     provider.setProtocols(newEngine, new String[]{AlpnOpenListener.this.fallbackProtocol});
                  }
               });
               selectedALPNEngine.complete(new SelectedAlpn(newEngine, provider));
               return alpnLimitingSSLEngine;
            }
         }
      }));
      AlpnConnectionListener potentialConnection = new AlpnConnectionListener(channel, selectedALPNEngine);
      channel.getSourceChannel().setReadListener(potentialConnection);
      potentialConnection.handleEvent((StreamSourceChannel)channel.getSourceChannel());
   }

   public static boolean engineSupportsHTTP2(SSLEngine engine) {
      String[] protcols = engine.getEnabledProtocols();
      boolean found = false;
      String[] ciphers = protcols;
      int var4 = protcols.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         String proto = ciphers[var5];
         if (REQUIRED_PROTOCOLS.contains(proto)) {
            found = true;
            break;
         }
      }

      if (!found) {
         return false;
      } else {
         ciphers = engine.getEnabledCipherSuites();
         String[] var8 = ciphers;
         var5 = ciphers.length;

         for(int var9 = 0; var9 < var5; ++var9) {
            String i = var8[var9];
            if (i.equals("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256") || i.equals("SSL_ECDHE_RSA_WITH_AES_128_GCM_SHA256")) {
               return true;
            }
         }

         return false;
      }
   }

   static final class SSLConduitUpdater implements Function<SSLEngine, SSLEngine> {
      final SslConduit conduit;
      final Function<SSLEngine, SSLEngine> underlying;

      SSLConduitUpdater(SslConduit conduit, Function<SSLEngine, SSLEngine> underlying) {
         this.conduit = conduit;
         this.underlying = underlying;
      }

      public SSLEngine apply(SSLEngine engine) {
         SSLEngine res = (SSLEngine)this.underlying.apply(engine);
         this.conduit.setSslEngine(res);
         return res;
      }
   }

   static final class SelectedAlpn {
      final SSLEngine engine;
      final ALPNProvider provider;

      SelectedAlpn(SSLEngine engine, ALPNProvider provider) {
         this.engine = engine;
         this.provider = provider;
      }
   }

   private class AlpnConnectionListener implements ChannelListener<StreamSourceChannel> {
      private final StreamConnection channel;
      private final CompletableFuture<SelectedAlpn> selectedAlpn;

      private AlpnConnectionListener(StreamConnection channel, CompletableFuture<SelectedAlpn> selectedAlpn) {
         this.channel = channel;
         this.selectedAlpn = selectedAlpn;
      }

      public void handleEvent(StreamSourceChannel source) {
         PooledByteBuffer buffer = AlpnOpenListener.this.bufferPool.allocate();
         boolean free = true;

         try {
            try {
               int res;
               do {
                  res = this.channel.getSourceChannel().read(buffer.getBuffer());
                  if (res == -1) {
                     IoUtils.safeClose((Closeable)this.channel);
                     return;
                  }

                  buffer.getBuffer().flip();
                  SelectedAlpn selectedAlpn = (SelectedAlpn)this.selectedAlpn.getNow((Object)null);
                  String selected;
                  if (selectedAlpn != null) {
                     selected = selectedAlpn.provider.getSelectedProtocol(selectedAlpn.engine);
                  } else {
                     selected = null;
                  }

                  DelegateOpenListener listener;
                  if (selected != null) {
                     if (selected.isEmpty()) {
                        if (AlpnOpenListener.this.fallbackProtocol == null) {
                           UndertowLogger.REQUEST_IO_LOGGER.noALPNFallback(this.channel.getPeerAddress());
                           IoUtils.safeClose((Closeable)this.channel);
                           return;
                        }

                        listener = ((ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol)).listener;
                     } else {
                        listener = ((ListenerEntry)AlpnOpenListener.this.listeners.get(selected)).listener;
                     }

                     source.getReadSetter().set((ChannelListener)null);
                     listener.handleEvent(this.channel, buffer);
                     free = false;
                     return;
                  }

                  if (res > 0) {
                     if (AlpnOpenListener.this.fallbackProtocol == null) {
                        UndertowLogger.REQUEST_IO_LOGGER.noALPNFallback(this.channel.getPeerAddress());
                        IoUtils.safeClose((Closeable)this.channel);
                        return;
                     }

                     listener = ((ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol)).listener;
                     source.getReadSetter().set((ChannelListener)null);
                     listener.handleEvent(this.channel, buffer);
                     free = false;
                     return;
                  }
               } while(res != 0);

               this.channel.getSourceChannel().resumeReads();
               return;
            } catch (IOException var12) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var12);
               IoUtils.safeClose((Closeable)this.channel);
            } catch (Throwable var13) {
               UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var13);
               IoUtils.safeClose((Closeable)this.channel);
            }

         } finally {
            if (free) {
               buffer.close();
            }

         }
      }

      // $FF: synthetic method
      AlpnConnectionListener(StreamConnection x1, CompletableFuture x2, Object x3) {
         this(x1, x2);
      }
   }

   private static class ListenerEntry implements Comparable<ListenerEntry> {
      final DelegateOpenListener listener;
      final int weight;
      final String protocol;

      ListenerEntry(DelegateOpenListener listener, int weight, String protocol) {
         this.listener = listener;
         this.weight = weight;
         this.protocol = protocol;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof ListenerEntry)) {
            return false;
         } else {
            ListenerEntry that = (ListenerEntry)o;
            if (this.weight != that.weight) {
               return false;
            } else {
               return !this.listener.equals(that.listener) ? false : this.protocol.equals(that.protocol);
            }
         }
      }

      public int hashCode() {
         int result = this.listener.hashCode();
         result = 31 * result + this.weight;
         result = 31 * result + this.protocol.hashCode();
         return result;
      }

      public int compareTo(ListenerEntry o) {
         return -Integer.compare(this.weight, o.weight);
      }
   }
}
