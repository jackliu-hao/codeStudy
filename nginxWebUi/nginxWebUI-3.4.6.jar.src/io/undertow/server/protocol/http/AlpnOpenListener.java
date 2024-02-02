/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.alpn.ALPNManager;
/*     */ import io.undertow.protocols.alpn.ALPNProvider;
/*     */ import io.undertow.protocols.ssl.SslConduit;
/*     */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*     */ import io.undertow.server.AggregateConnectorStatistics;
/*     */ import io.undertow.server.ConnectorStatistics;
/*     */ import io.undertow.server.DelegateOpenListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.server.XnioByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.ssl.SslConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlpnOpenListener
/*     */   implements ChannelListener<StreamConnection>, OpenListener
/*     */ {
/*     */   public static final String REQUIRED_CIPHER = "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256";
/*     */   public static final String IBM_REQUIRED_CIPHER = "SSL_ECDHE_RSA_WITH_AES_128_GCM_SHA256";
/*  79 */   private static final Set<String> REQUIRED_PROTOCOLS = Collections.unmodifiableSet(new HashSet<>(
/*  80 */         Arrays.asList(new String[] { "TLSv1.2", "TLSv1.3" })));
/*     */   
/*  82 */   private final ALPNManager alpnManager = ALPNManager.INSTANCE;
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*  85 */   private final Map<String, ListenerEntry> listeners = new HashMap<>();
/*     */   
/*     */   private String[] protocols;
/*     */   private final String fallbackProtocol;
/*     */   private volatile HttpHandler rootHandler;
/*     */   private volatile OptionMap undertowOptions;
/*     */   private volatile boolean statisticsEnabled;
/*     */   private volatile boolean providerLogged;
/*     */   private volatile boolean alpnFailLogged;
/*     */   
/*     */   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions, DelegateOpenListener httpListener) {
/*  96 */     this(bufferPool, undertowOptions, "http/1.1", httpListener);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions) {
/* 100 */     this(bufferPool, undertowOptions, (String)null, (DelegateOpenListener)null);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(Pool<ByteBuffer> bufferPool, OptionMap undertowOptions, String fallbackProtocol, DelegateOpenListener fallbackListener) {
/* 104 */     this((ByteBufferPool)new XnioByteBufferPool(bufferPool), undertowOptions, fallbackProtocol, fallbackListener);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions, DelegateOpenListener httpListener) {
/* 108 */     this(bufferPool, undertowOptions, "http/1.1", httpListener);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(ByteBufferPool bufferPool) {
/* 112 */     this(bufferPool, OptionMap.EMPTY, (String)null, (DelegateOpenListener)null);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions) {
/* 116 */     this(bufferPool, undertowOptions, (String)null, (DelegateOpenListener)null);
/*     */   }
/*     */   
/*     */   public AlpnOpenListener(ByteBufferPool bufferPool, OptionMap undertowOptions, String fallbackProtocol, DelegateOpenListener fallbackListener) {
/* 120 */     this.bufferPool = bufferPool;
/* 121 */     this.undertowOptions = undertowOptions;
/* 122 */     this.fallbackProtocol = fallbackProtocol;
/* 123 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/* 124 */     if (fallbackProtocol != null && fallbackListener != null) {
/* 125 */       addProtocol(fallbackProtocol, fallbackListener, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getRootHandler() {
/* 131 */     return this.rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRootHandler(HttpHandler rootHandler) {
/* 136 */     this.rootHandler = rootHandler;
/* 137 */     for (Map.Entry<String, ListenerEntry> delegate : this.listeners.entrySet()) {
/* 138 */       ((ListenerEntry)delegate.getValue()).listener.setRootHandler(rootHandler);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 144 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUndertowOptions(OptionMap undertowOptions) {
/* 149 */     if (undertowOptions == null) {
/* 150 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
/*     */     }
/* 152 */     this.undertowOptions = undertowOptions;
/* 153 */     for (Map.Entry<String, ListenerEntry> delegate : this.listeners.entrySet()) {
/* 154 */       ((ListenerEntry)delegate.getValue()).listener.setRootHandler(this.rootHandler);
/*     */     }
/* 156 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 161 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */   
/*     */   public ConnectorStatistics getConnectorStatistics() {
/* 166 */     if (this.statisticsEnabled) {
/* 167 */       List<ConnectorStatistics> stats = new ArrayList<>();
/* 168 */       for (Map.Entry<String, ListenerEntry> l : this.listeners.entrySet()) {
/* 169 */         ConnectorStatistics c = ((ListenerEntry)l.getValue()).listener.getConnectorStatistics();
/* 170 */         if (c != null) {
/* 171 */           stats.add(c);
/*     */         }
/*     */       } 
/* 174 */       return (ConnectorStatistics)new AggregateConnectorStatistics(stats.<ConnectorStatistics>toArray(new ConnectorStatistics[stats.size()]));
/*     */     } 
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnections() {
/* 181 */     for (Map.Entry<String, ListenerEntry> i : this.listeners.entrySet())
/* 182 */       ((ListenerEntry)i.getValue()).listener.closeConnections(); 
/*     */   }
/*     */   
/*     */   private static class ListenerEntry
/*     */     implements Comparable<ListenerEntry>
/*     */   {
/*     */     final DelegateOpenListener listener;
/*     */     final int weight;
/*     */     final String protocol;
/*     */     
/*     */     ListenerEntry(DelegateOpenListener listener, int weight, String protocol) {
/* 193 */       this.listener = listener;
/* 194 */       this.weight = weight;
/* 195 */       this.protocol = protocol;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 200 */       if (this == o) return true; 
/* 201 */       if (!(o instanceof ListenerEntry)) return false;
/*     */       
/* 203 */       ListenerEntry that = (ListenerEntry)o;
/*     */       
/* 205 */       if (this.weight != that.weight) return false; 
/* 206 */       if (!this.listener.equals(that.listener)) return false; 
/* 207 */       return this.protocol.equals(that.protocol);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 212 */       int result = this.listener.hashCode();
/* 213 */       result = 31 * result + this.weight;
/* 214 */       result = 31 * result + this.protocol.hashCode();
/* 215 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(ListenerEntry o) {
/* 220 */       return -Integer.compare(this.weight, o.weight);
/*     */     }
/*     */   }
/*     */   
/*     */   public AlpnOpenListener addProtocol(String name, DelegateOpenListener listener, int weight) {
/* 225 */     this.listeners.put(name, new ListenerEntry(listener, weight, name));
/* 226 */     List<ListenerEntry> list = new ArrayList<>(this.listeners.values());
/* 227 */     Collections.sort(list);
/* 228 */     this.protocols = new String[list.size()];
/* 229 */     for (int i = 0; i < list.size(); i++) {
/* 230 */       this.protocols[i] = ((ListenerEntry)list.get(i)).protocol;
/*     */     }
/* 232 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(final StreamConnection channel) {
/* 237 */     if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
/* 238 */       UndertowLogger.REQUEST_LOGGER.tracef("Opened connection with %s", channel.getPeerAddress());
/*     */     }
/* 240 */     SslConduit sslConduit = UndertowXnioSsl.getSslConduit((SslConnection)channel);
/* 241 */     SSLEngine originalSSlEngine = sslConduit.getSSLEngine();
/*     */ 
/*     */     
/* 244 */     final CompletableFuture<SelectedAlpn> selectedALPNEngine = new CompletableFuture<>();
/* 245 */     this.alpnManager.registerEngineCallback(originalSSlEngine, new SSLConduitUpdater(sslConduit, new Function<SSLEngine, SSLEngine>()
/*     */           {
/*     */             public SSLEngine apply(SSLEngine engine)
/*     */             {
/* 249 */               if (!AlpnOpenListener.engineSupportsHTTP2(engine)) {
/* 250 */                 if (!AlpnOpenListener.this.alpnFailLogged) {
/* 251 */                   synchronized (this) {
/* 252 */                     if (!AlpnOpenListener.this.alpnFailLogged) {
/* 253 */                       UndertowLogger.REQUEST_LOGGER.debugf("ALPN has been configured however %s is not present or TLS1.2 is not enabled, falling back to default protocol", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
/* 254 */                       AlpnOpenListener.this.alpnFailLogged = true;
/*     */                     } 
/*     */                   } 
/*     */                 }
/* 258 */                 if (AlpnOpenListener.this.fallbackProtocol != null) {
/* 259 */                   AlpnOpenListener.ListenerEntry listener = (AlpnOpenListener.ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol);
/* 260 */                   if (listener != null) {
/* 261 */                     selectedALPNEngine.complete(null);
/* 262 */                     return engine;
/*     */                   } 
/*     */                 } 
/*     */               } 
/* 266 */               final ALPNProvider provider = AlpnOpenListener.this.alpnManager.getProvider(engine);
/* 267 */               if (provider == null) {
/* 268 */                 if (!AlpnOpenListener.this.providerLogged) {
/* 269 */                   synchronized (this) {
/* 270 */                     if (!AlpnOpenListener.this.providerLogged) {
/* 271 */                       UndertowLogger.REQUEST_LOGGER.debugf("ALPN has been configured however no provider could be found for engine %s for connector at %s", engine, channel.getLocalAddress());
/* 272 */                       AlpnOpenListener.this.providerLogged = true;
/*     */                     } 
/*     */                   } 
/*     */                 }
/* 276 */                 if (AlpnOpenListener.this.fallbackProtocol != null) {
/* 277 */                   AlpnOpenListener.ListenerEntry listener = (AlpnOpenListener.ListenerEntry)AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol);
/* 278 */                   if (listener != null) {
/* 279 */                     selectedALPNEngine.complete(null);
/* 280 */                     return engine;
/*     */                   } 
/*     */                 } 
/* 283 */                 UndertowLogger.REQUEST_LOGGER.debugf("No ALPN provider available and no fallback defined", new Object[0]);
/* 284 */                 IoUtils.safeClose((Closeable)channel);
/* 285 */                 selectedALPNEngine.complete(null);
/* 286 */                 return engine;
/*     */               } 
/*     */               
/* 289 */               if (!AlpnOpenListener.this.providerLogged) {
/* 290 */                 synchronized (this) {
/* 291 */                   if (!AlpnOpenListener.this.providerLogged) {
/* 292 */                     UndertowLogger.REQUEST_LOGGER.debugf("Using ALPN provider %s for connector at %s", provider, channel.getLocalAddress());
/* 293 */                     AlpnOpenListener.this.providerLogged = true;
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */               
/* 298 */               final SSLEngine newEngine = provider.setProtocols(engine, AlpnOpenListener.this.protocols);
/* 299 */               ALPNLimitingSSLEngine alpnLimitingSSLEngine = new ALPNLimitingSSLEngine(newEngine, new Runnable()
/*     */                   {
/*     */                     public void run() {
/* 302 */                       provider.setProtocols(newEngine, new String[] { AlpnOpenListener.access$100(this.this$1.this$0) });
/*     */                     }
/*     */                   });
/* 305 */               selectedALPNEngine.complete(new AlpnOpenListener.SelectedAlpn(newEngine, provider));
/* 306 */               return alpnLimitingSSLEngine;
/*     */             }
/*     */           }));
/*     */ 
/*     */     
/* 311 */     AlpnConnectionListener potentialConnection = new AlpnConnectionListener(channel, selectedALPNEngine);
/* 312 */     channel.getSourceChannel().setReadListener(potentialConnection);
/* 313 */     potentialConnection.handleEvent((StreamSourceChannel)channel.getSourceChannel());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean engineSupportsHTTP2(SSLEngine engine) {
/* 320 */     String[] protcols = engine.getEnabledProtocols();
/* 321 */     boolean found = false;
/* 322 */     for (String proto : protcols) {
/* 323 */       if (REQUIRED_PROTOCOLS.contains(proto)) {
/* 324 */         found = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 328 */     if (!found) {
/* 329 */       return false;
/*     */     }
/*     */     
/* 332 */     String[] ciphers = engine.getEnabledCipherSuites();
/* 333 */     for (String i : ciphers) {
/* 334 */       if (i.equals("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256") || i.equals("SSL_ECDHE_RSA_WITH_AES_128_GCM_SHA256")) {
/* 335 */         return true;
/*     */       }
/*     */     } 
/* 338 */     return false;
/*     */   }
/*     */   
/*     */   private class AlpnConnectionListener implements ChannelListener<StreamSourceChannel> {
/*     */     private final StreamConnection channel;
/*     */     private final CompletableFuture<AlpnOpenListener.SelectedAlpn> selectedAlpn;
/*     */     
/*     */     private AlpnConnectionListener(StreamConnection channel, CompletableFuture<AlpnOpenListener.SelectedAlpn> selectedAlpn) {
/* 346 */       this.channel = channel;
/* 347 */       this.selectedAlpn = selectedAlpn;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamSourceChannel source) {
/* 352 */       PooledByteBuffer buffer = AlpnOpenListener.this.bufferPool.allocate();
/* 353 */       boolean free = true; try {
/*     */         while (true) {
/*     */           String selected;
/* 356 */           int res = this.channel.getSourceChannel().read(buffer.getBuffer());
/* 357 */           if (res == -1) {
/* 358 */             IoUtils.safeClose((Closeable)this.channel);
/*     */             return;
/*     */           } 
/* 361 */           buffer.getBuffer().flip();
/* 362 */           AlpnOpenListener.SelectedAlpn selectedAlpn = this.selectedAlpn.getNow(null);
/*     */           
/* 364 */           if (selectedAlpn != null) {
/* 365 */             selected = selectedAlpn.provider.getSelectedProtocol(selectedAlpn.engine);
/*     */           } else {
/* 367 */             selected = null;
/*     */           } 
/* 369 */           if (selected != null) {
/*     */             DelegateOpenListener listener;
/* 371 */             if (selected.isEmpty()) {
/*     */               
/* 373 */               if (AlpnOpenListener.this.fallbackProtocol == null) {
/* 374 */                 UndertowLogger.REQUEST_IO_LOGGER.noALPNFallback(this.channel.getPeerAddress());
/* 375 */                 IoUtils.safeClose((Closeable)this.channel);
/*     */                 return;
/*     */               } 
/* 378 */               listener = (AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol)).listener;
/*     */             } else {
/* 380 */               listener = (AlpnOpenListener.this.listeners.get(selected)).listener;
/*     */             } 
/* 382 */             source.getReadSetter().set(null);
/* 383 */             listener.handleEvent(this.channel, buffer);
/* 384 */             free = false; return;
/*     */           } 
/* 386 */           if (res > 0) {
/* 387 */             if (AlpnOpenListener.this.fallbackProtocol == null) {
/* 388 */               UndertowLogger.REQUEST_IO_LOGGER.noALPNFallback(this.channel.getPeerAddress());
/* 389 */               IoUtils.safeClose((Closeable)this.channel);
/*     */               return;
/*     */             } 
/* 392 */             DelegateOpenListener listener = (AlpnOpenListener.this.listeners.get(AlpnOpenListener.this.fallbackProtocol)).listener;
/* 393 */             source.getReadSetter().set(null);
/* 394 */             listener.handleEvent(this.channel, buffer);
/* 395 */             free = false; return;
/*     */           } 
/* 397 */           if (res == 0) {
/* 398 */             this.channel.getSourceChannel().resumeReads();
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/* 403 */       } catch (IOException e) {
/* 404 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 405 */         IoUtils.safeClose((Closeable)this.channel);
/* 406 */       } catch (Throwable t) {
/* 407 */         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 408 */         IoUtils.safeClose((Closeable)this.channel);
/*     */       } finally {
/* 410 */         if (free)
/* 411 */           buffer.close(); 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class SelectedAlpn
/*     */   {
/*     */     final SSLEngine engine;
/*     */     final ALPNProvider provider;
/*     */     
/*     */     SelectedAlpn(SSLEngine engine, ALPNProvider provider) {
/* 422 */       this.engine = engine;
/* 423 */       this.provider = provider;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class SSLConduitUpdater implements Function<SSLEngine, SSLEngine> {
/*     */     final SslConduit conduit;
/*     */     final Function<SSLEngine, SSLEngine> underlying;
/*     */     
/*     */     SSLConduitUpdater(SslConduit conduit, Function<SSLEngine, SSLEngine> underlying) {
/* 432 */       this.conduit = conduit;
/* 433 */       this.underlying = underlying;
/*     */     }
/*     */ 
/*     */     
/*     */     public SSLEngine apply(SSLEngine engine) {
/* 438 */       SSLEngine res = this.underlying.apply(engine);
/* 439 */       this.conduit.setSslEngine(res);
/* 440 */       return res;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\AlpnOpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */