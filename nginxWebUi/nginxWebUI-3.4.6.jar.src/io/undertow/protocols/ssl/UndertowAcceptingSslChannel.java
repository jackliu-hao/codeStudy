/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Sequence;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
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
/*     */ class UndertowAcceptingSslChannel
/*     */   implements AcceptingChannel<SslConnection>
/*     */ {
/*     */   private final UndertowXnioSsl ssl;
/*     */   private final AcceptingChannel<? extends StreamConnection> tcpServer;
/*     */   private volatile SslClientAuthMode clientAuthMode;
/*     */   private volatile int useClientMode;
/*     */   private volatile int enableSessionCreation;
/*     */   private volatile String[] cipherSuites;
/*     */   private volatile String[] protocols;
/*  70 */   private static final AtomicReferenceFieldUpdater<UndertowAcceptingSslChannel, SslClientAuthMode> clientAuthModeUpdater = AtomicReferenceFieldUpdater.newUpdater(UndertowAcceptingSslChannel.class, SslClientAuthMode.class, "clientAuthMode");
/*     */   
/*  72 */   private static final AtomicIntegerFieldUpdater<UndertowAcceptingSslChannel> useClientModeUpdater = AtomicIntegerFieldUpdater.newUpdater(UndertowAcceptingSslChannel.class, "useClientMode");
/*     */   
/*  74 */   private static final AtomicIntegerFieldUpdater<UndertowAcceptingSslChannel> enableSessionCreationUpdater = AtomicIntegerFieldUpdater.newUpdater(UndertowAcceptingSslChannel.class, "enableSessionCreation");
/*     */   
/*     */   private final ChannelListener.Setter<AcceptingChannel<SslConnection>> closeSetter;
/*     */   private final ChannelListener.Setter<AcceptingChannel<SslConnection>> acceptSetter;
/*     */   protected final boolean startTls;
/*     */   protected final ByteBufferPool applicationBufferPool;
/*     */   private final boolean useCipherSuitesOrder;
/*     */   
/*     */   UndertowAcceptingSslChannel(UndertowXnioSsl ssl, AcceptingChannel<? extends StreamConnection> tcpServer, OptionMap optionMap, ByteBufferPool applicationBufferPool, boolean startTls) {
/*  83 */     this.tcpServer = tcpServer;
/*  84 */     this.ssl = ssl;
/*  85 */     this.applicationBufferPool = applicationBufferPool;
/*  86 */     this.startTls = startTls;
/*  87 */     this.clientAuthMode = (SslClientAuthMode)optionMap.get(Options.SSL_CLIENT_AUTH_MODE);
/*  88 */     this.useClientMode = optionMap.get(Options.SSL_USE_CLIENT_MODE, false) ? 1 : 0;
/*  89 */     this.enableSessionCreation = optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true) ? 1 : 0;
/*  90 */     Sequence<String> enabledCipherSuites = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
/*  91 */     this.cipherSuites = (enabledCipherSuites != null) ? (String[])enabledCipherSuites.toArray((Object[])new String[enabledCipherSuites.size()]) : null;
/*  92 */     Sequence<String> enabledProtocols = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
/*  93 */     this.protocols = (enabledProtocols != null) ? (String[])enabledProtocols.toArray((Object[])new String[enabledProtocols.size()]) : null;
/*     */     
/*  95 */     this.closeSetter = ChannelListeners.getDelegatingSetter(tcpServer.getCloseSetter(), (Channel)this);
/*     */     
/*  97 */     this.acceptSetter = ChannelListeners.getDelegatingSetter(tcpServer.getAcceptSetter(), (Channel)this);
/*  98 */     this.useCipherSuitesOrder = optionMap.get(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER, false);
/*     */   }
/*     */   
/* 101 */   private static final Set<Option<?>> SUPPORTED_OPTIONS = Option.setBuilder()
/* 102 */     .add(Options.SSL_CLIENT_AUTH_MODE)
/* 103 */     .add(Options.SSL_USE_CLIENT_MODE)
/* 104 */     .add(Options.SSL_ENABLE_SESSION_CREATION)
/* 105 */     .add(Options.SSL_ENABLED_CIPHER_SUITES)
/* 106 */     .add(Options.SSL_ENABLED_PROTOCOLS)
/* 107 */     .create();
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 110 */     if (option == Options.SSL_CLIENT_AUTH_MODE)
/* 111 */       return (T)option.cast(clientAuthModeUpdater.getAndSet(this, Options.SSL_CLIENT_AUTH_MODE.cast(value))); 
/* 112 */     if (option == Options.SSL_USE_CLIENT_MODE)
/* 113 */     { Boolean valueObject = (Boolean)Options.SSL_USE_CLIENT_MODE.cast(value);
/* 114 */       if (valueObject != null) return (T)option.cast(Boolean.valueOf((useClientModeUpdater.getAndSet(this, valueObject.booleanValue() ? 1 : 0) != 0)));  }
/* 115 */     else if (option == Options.SSL_ENABLE_SESSION_CREATION)
/* 116 */     { Boolean valueObject = (Boolean)Options.SSL_ENABLE_SESSION_CREATION.cast(value);
/* 117 */       if (valueObject != null) return (T)option.cast(Boolean.valueOf((enableSessionCreationUpdater.getAndSet(this, valueObject.booleanValue() ? 1 : 0) != 0)));  }
/* 118 */     else { if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
/* 119 */         Sequence<String> seq = (Sequence<String>)Options.SSL_ENABLED_CIPHER_SUITES.cast(value);
/* 120 */         String[] old = this.cipherSuites;
/* 121 */         this.cipherSuites = (seq == null) ? null : (String[])seq.toArray((Object[])new String[seq.size()]);
/* 122 */         return (T)option.cast(old);
/* 123 */       }  if (option == Options.SSL_ENABLED_PROTOCOLS) {
/* 124 */         Sequence<String> seq = (Sequence<String>)Options.SSL_ENABLED_PROTOCOLS.cast(value);
/* 125 */         String[] old = this.protocols;
/* 126 */         this.protocols = (seq == null) ? null : (String[])seq.toArray((Object[])new String[seq.size()]);
/* 127 */         return (T)option.cast(old);
/*     */       } 
/* 129 */       return (T)this.tcpServer.setOption(option, value); }
/*     */     
/* 131 */     throw UndertowLogger.ROOT_LOGGER.nullParameter("value");
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 135 */     return this.tcpServer.getWorker();
/*     */   }
/*     */   
/*     */   public UndertowSslConnection accept() throws IOException {
/* 139 */     StreamConnection tcpConnection = (StreamConnection)this.tcpServer.accept();
/* 140 */     if (tcpConnection == null) {
/* 141 */       return null;
/*     */     }
/*     */     try {
/* 144 */       InetSocketAddress peerAddress = (InetSocketAddress)tcpConnection.getPeerAddress(InetSocketAddress.class);
/* 145 */       SSLEngine engine = this.ssl.getSslContext().createSSLEngine(getHostNameNoResolve(peerAddress), peerAddress.getPort());
/*     */       
/* 147 */       if (this.useCipherSuitesOrder) {
/* 148 */         SSLParameters sslParameters = engine.getSSLParameters();
/* 149 */         sslParameters.setUseCipherSuitesOrder(true);
/* 150 */         engine.setSSLParameters(sslParameters);
/*     */       } 
/* 152 */       boolean clientMode = (this.useClientMode != 0);
/* 153 */       engine.setUseClientMode(clientMode);
/* 154 */       if (!clientMode) {
/* 155 */         SslClientAuthMode clientAuthMode = this.clientAuthMode;
/* 156 */         if (clientAuthMode != null) switch (clientAuthMode) {
/*     */             case NOT_REQUESTED:
/* 158 */               engine.setNeedClientAuth(false);
/* 159 */               engine.setWantClientAuth(false);
/*     */               break;
/*     */             case REQUESTED:
/* 162 */               engine.setWantClientAuth(true);
/*     */               break;
/*     */             case REQUIRED:
/* 165 */               engine.setNeedClientAuth(true);
/*     */               break;
/*     */             default:
/* 168 */               throw new IllegalStateException();
/*     */           }  
/*     */       } 
/* 171 */       engine.setEnableSessionCreation((this.enableSessionCreation != 0));
/* 172 */       String[] cipherSuites = this.cipherSuites;
/* 173 */       if (cipherSuites != null) {
/* 174 */         Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedCipherSuites()));
/* 175 */         if (supported.isEmpty()) {
/* 176 */           engine.setEnabledCipherSuites(cipherSuites);
/*     */         } else {
/* 178 */           List<String> finalList = new ArrayList<>();
/* 179 */           for (String name : cipherSuites) {
/* 180 */             if (supported.contains(name)) {
/* 181 */               finalList.add(name);
/*     */             }
/*     */           } 
/* 184 */           engine.setEnabledCipherSuites(finalList.<String>toArray(new String[finalList.size()]));
/*     */         } 
/*     */       } 
/* 187 */       String[] protocols = this.protocols;
/* 188 */       if (protocols != null) {
/* 189 */         Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedProtocols()));
/* 190 */         if (supported.isEmpty()) {
/* 191 */           engine.setEnabledProtocols(protocols);
/*     */         } else {
/* 193 */           List<String> finalList = new ArrayList<>();
/* 194 */           for (String name : protocols) {
/* 195 */             if (supported.contains(name)) {
/* 196 */               finalList.add(name);
/*     */             }
/*     */           } 
/* 199 */           engine.setEnabledProtocols(finalList.<String>toArray(new String[finalList.size()]));
/*     */         } 
/*     */       } 
/* 202 */       return accept(tcpConnection, engine);
/* 203 */     } catch (IOException|RuntimeException e) {
/* 204 */       IoUtils.safeClose((Closeable)tcpConnection);
/* 205 */       UndertowLogger.REQUEST_LOGGER.failedToAcceptSSLRequest(e);
/* 206 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected UndertowSslConnection accept(StreamConnection tcpServer, SSLEngine sslEngine) throws IOException {
/* 211 */     return new UndertowSslConnection(tcpServer, sslEngine, this.applicationBufferPool, this.ssl.getDelegatedTaskExecutor());
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends AcceptingChannel<SslConnection>> getCloseSetter() {
/* 215 */     return this.closeSetter;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 219 */     return this.tcpServer.isOpen();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 223 */     this.tcpServer.close();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 227 */     return (SUPPORTED_OPTIONS.contains(option) || this.tcpServer.supportsOption(option));
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 231 */     if (option == Options.SSL_CLIENT_AUTH_MODE)
/* 232 */       return (T)option.cast(this.clientAuthMode); 
/* 233 */     if (option == Options.SSL_USE_CLIENT_MODE)
/* 234 */       return (T)option.cast(Boolean.valueOf((this.useClientMode != 0))); 
/* 235 */     if (option == Options.SSL_ENABLE_SESSION_CREATION)
/* 236 */       return (T)option.cast(Boolean.valueOf((this.enableSessionCreation != 0))); 
/* 237 */     if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
/* 238 */       String[] cipherSuites = this.cipherSuites;
/* 239 */       return (cipherSuites == null) ? null : (T)option.cast(Sequence.of((Object[])cipherSuites));
/* 240 */     }  if (option == Options.SSL_ENABLED_PROTOCOLS) {
/* 241 */       String[] protocols = this.protocols;
/* 242 */       return (protocols == null) ? null : (T)option.cast(Sequence.of((Object[])protocols));
/*     */     } 
/* 244 */     return (T)this.tcpServer.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AcceptingChannel<SslConnection>> getAcceptSetter() {
/* 249 */     return this.acceptSetter;
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 253 */     return this.tcpServer.getLocalAddress();
/*     */   }
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 257 */     return (A)this.tcpServer.getLocalAddress(type);
/*     */   }
/*     */   
/*     */   public void suspendAccepts() {
/* 261 */     this.tcpServer.suspendAccepts();
/*     */   }
/*     */   
/*     */   public void resumeAccepts() {
/* 265 */     this.tcpServer.resumeAccepts();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAcceptResumed() {
/* 270 */     return this.tcpServer.isAcceptResumed();
/*     */   }
/*     */   
/*     */   public void wakeupAccepts() {
/* 274 */     this.tcpServer.wakeupAccepts();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable() throws IOException {
/* 278 */     this.tcpServer.awaitAcceptable();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/* 282 */     this.tcpServer.awaitAcceptable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getAcceptThread() {
/* 287 */     return this.tcpServer.getAcceptThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 291 */     return this.tcpServer.getIoThread();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getHostNameNoResolve(InetSocketAddress socketAddress) {
/* 297 */     return socketAddress.getHostString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\UndertowAcceptingSslChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */