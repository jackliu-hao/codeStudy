/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.Sequence;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
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
/*     */ abstract class AbstractAcceptingSslChannel<C extends ConnectedChannel, S extends ConnectedChannel>
/*     */   implements AcceptingChannel<C>
/*     */ {
/*     */   private final SSLContext sslContext;
/*     */   private final AcceptingChannel<? extends S> tcpServer;
/*     */   private volatile SslClientAuthMode clientAuthMode;
/*     */   private volatile int useClientMode;
/*     */   private volatile int enableSessionCreation;
/*     */   private volatile String[] cipherSuites;
/*     */   private volatile String[] protocols;
/*  70 */   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, SslClientAuthMode> clientAuthModeUpdater = AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, SslClientAuthMode.class, "clientAuthMode");
/*     */   
/*  72 */   private static final AtomicIntegerFieldUpdater<AbstractAcceptingSslChannel> useClientModeUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, "useClientMode");
/*     */   
/*  74 */   private static final AtomicIntegerFieldUpdater<AbstractAcceptingSslChannel> enableSessionCreationUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, "enableSessionCreation");
/*     */   
/*  76 */   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, String[]> cipherSuitesUpdater = (AtomicReferenceFieldUpdater)AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, (Class)String[].class, "cipherSuites");
/*     */   
/*  78 */   private static final AtomicReferenceFieldUpdater<AbstractAcceptingSslChannel, String[]> protocolsUpdater = (AtomicReferenceFieldUpdater)AtomicReferenceFieldUpdater.newUpdater(AbstractAcceptingSslChannel.class, (Class)String[].class, "protocols");
/*     */   
/*     */   private final ChannelListener.Setter<AcceptingChannel<C>> closeSetter;
/*     */   
/*     */   private final ChannelListener.Setter<AcceptingChannel<C>> acceptSetter;
/*     */   protected final boolean startTls;
/*     */   protected final Pool<ByteBuffer> socketBufferPool;
/*     */   protected final Pool<ByteBuffer> applicationBufferPool;
/*     */   
/*     */   AbstractAcceptingSslChannel(SSLContext sslContext, AcceptingChannel<? extends S> tcpServer, OptionMap optionMap, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
/*  88 */     this.tcpServer = tcpServer;
/*  89 */     this.sslContext = sslContext;
/*  90 */     this.socketBufferPool = socketBufferPool;
/*  91 */     this.applicationBufferPool = applicationBufferPool;
/*  92 */     this.startTls = startTls;
/*  93 */     this.clientAuthMode = (SslClientAuthMode)optionMap.get(Options.SSL_CLIENT_AUTH_MODE);
/*  94 */     this.useClientMode = optionMap.get(Options.SSL_USE_CLIENT_MODE, false) ? 1 : 0;
/*  95 */     this.enableSessionCreation = optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true) ? 1 : 0;
/*  96 */     Sequence<String> enabledCipherSuites = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
/*  97 */     this.cipherSuites = (enabledCipherSuites != null) ? (String[])enabledCipherSuites.toArray((Object[])new String[enabledCipherSuites.size()]) : null;
/*  98 */     Sequence<String> enabledProtocols = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
/*  99 */     this.protocols = (enabledProtocols != null) ? (String[])enabledProtocols.toArray((Object[])new String[enabledProtocols.size()]) : null;
/*     */     
/* 101 */     this.closeSetter = ChannelListeners.getDelegatingSetter(tcpServer.getCloseSetter(), (Channel)this);
/*     */     
/* 103 */     this.acceptSetter = ChannelListeners.getDelegatingSetter(tcpServer.getAcceptSetter(), (Channel)this);
/*     */   }
/*     */   
/* 106 */   private static final Set<Option<?>> SUPPORTED_OPTIONS = Option.setBuilder()
/* 107 */     .add(Options.SSL_CLIENT_AUTH_MODE)
/* 108 */     .add(Options.SSL_USE_CLIENT_MODE)
/* 109 */     .add(Options.SSL_ENABLE_SESSION_CREATION)
/* 110 */     .add(Options.SSL_ENABLED_CIPHER_SUITES)
/* 111 */     .add(Options.SSL_ENABLED_PROTOCOLS)
/* 112 */     .create();
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 115 */     if (option == Options.SSL_CLIENT_AUTH_MODE)
/* 116 */       return (T)option.cast(clientAuthModeUpdater.getAndSet(this, Options.SSL_CLIENT_AUTH_MODE.cast(value))); 
/* 117 */     if (option == Options.SSL_USE_CLIENT_MODE)
/* 118 */     { Boolean valueObject = (Boolean)Options.SSL_USE_CLIENT_MODE.cast(value);
/* 119 */       if (valueObject != null) return (T)option.cast(Boolean.valueOf((useClientModeUpdater.getAndSet(this, valueObject.booleanValue() ? 1 : 0) != 0)));  }
/* 120 */     else if (option == Options.SSL_ENABLE_SESSION_CREATION)
/* 121 */     { Boolean valueObject = (Boolean)Options.SSL_ENABLE_SESSION_CREATION.cast(value);
/* 122 */       if (valueObject != null) return (T)option.cast(Boolean.valueOf((enableSessionCreationUpdater.getAndSet(this, valueObject.booleanValue() ? 1 : 0) != 0)));  }
/* 123 */     else { if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
/* 124 */         Sequence<String> seq = (Sequence<String>)Options.SSL_ENABLED_CIPHER_SUITES.cast(value);
/* 125 */         return (T)option.cast(cipherSuitesUpdater.getAndSet(this, (seq == null) ? null : (String[])seq.toArray((Object[])new String[seq.size()])));
/* 126 */       }  if (option == Options.SSL_ENABLED_PROTOCOLS) {
/* 127 */         Sequence<String> seq = (Sequence<String>)Options.SSL_ENABLED_PROTOCOLS.cast(value);
/* 128 */         return (T)option.cast(protocolsUpdater.getAndSet(this, (seq == null) ? null : (String[])seq.toArray((Object[])new String[seq.size()])));
/*     */       } 
/* 130 */       return (T)this.tcpServer.setOption(option, value); }
/*     */     
/* 132 */     throw Messages.msg.nullParameter("value");
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 136 */     return this.tcpServer.getWorker();
/*     */   }
/*     */   
/*     */   public C accept() throws IOException {
/* 140 */     ConnectedChannel connectedChannel = this.tcpServer.accept();
/* 141 */     if (connectedChannel == null) {
/* 142 */       return null;
/*     */     }
/* 144 */     InetSocketAddress peerAddress = (InetSocketAddress)connectedChannel.getPeerAddress(InetSocketAddress.class);
/* 145 */     SSLEngine engine = this.sslContext.createSSLEngine(peerAddress.getHostString(), peerAddress.getPort());
/* 146 */     boolean clientMode = (this.useClientMode != 0);
/* 147 */     engine.setUseClientMode(clientMode);
/* 148 */     if (!clientMode) {
/* 149 */       SslClientAuthMode clientAuthMode = this.clientAuthMode;
/* 150 */       if (clientAuthMode != null) switch (clientAuthMode) {
/*     */           case NOT_REQUESTED:
/* 152 */             engine.setNeedClientAuth(false);
/* 153 */             engine.setWantClientAuth(false);
/*     */             break;
/*     */           case REQUESTED:
/* 156 */             engine.setWantClientAuth(true);
/*     */             break;
/*     */           case REQUIRED:
/* 159 */             engine.setNeedClientAuth(true); break;
/*     */           default:
/* 161 */             throw new IllegalStateException();
/*     */         }  
/*     */     } 
/* 164 */     engine.setEnableSessionCreation((this.enableSessionCreation != 0));
/* 165 */     String[] cipherSuites = this.cipherSuites;
/* 166 */     if (cipherSuites != null) {
/* 167 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedCipherSuites()));
/* 168 */       List<String> finalList = new ArrayList<>();
/* 169 */       for (String name : cipherSuites) {
/* 170 */         if (supported.contains(name)) {
/* 171 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 174 */       engine.setEnabledCipherSuites(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 176 */     String[] protocols = this.protocols;
/* 177 */     if (protocols != null) {
/* 178 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedProtocols()));
/* 179 */       List<String> finalList = new ArrayList<>();
/* 180 */       for (String name : protocols) {
/* 181 */         if (supported.contains(name)) {
/* 182 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 185 */       engine.setEnabledProtocols(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 187 */     return accept((S)connectedChannel, engine);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AcceptingChannel<C>> getCloseSetter() {
/* 193 */     return this.closeSetter;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 197 */     return this.tcpServer.isOpen();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 201 */     this.tcpServer.close();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 205 */     return (SUPPORTED_OPTIONS.contains(option) || this.tcpServer.supportsOption(option));
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 209 */     if (option == Options.SSL_CLIENT_AUTH_MODE)
/* 210 */       return (T)option.cast(this.clientAuthMode); 
/* 211 */     if (option == Options.SSL_USE_CLIENT_MODE)
/* 212 */       return (T)option.cast(Boolean.valueOf((this.useClientMode != 0))); 
/* 213 */     if (option == Options.SSL_ENABLE_SESSION_CREATION)
/* 214 */       return (T)option.cast(Boolean.valueOf((this.enableSessionCreation != 0))); 
/* 215 */     if (option == Options.SSL_ENABLED_CIPHER_SUITES) {
/* 216 */       String[] cipherSuites = this.cipherSuites;
/* 217 */       return (cipherSuites == null) ? null : (T)option.cast(Sequence.of((Object[])cipherSuites));
/* 218 */     }  if (option == Options.SSL_ENABLED_PROTOCOLS) {
/* 219 */       String[] protocols = this.protocols;
/* 220 */       return (protocols == null) ? null : (T)option.cast(Sequence.of((Object[])protocols));
/*     */     } 
/* 222 */     return (T)this.tcpServer.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AcceptingChannel<C>> getAcceptSetter() {
/* 227 */     return this.acceptSetter;
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 231 */     return this.tcpServer.getLocalAddress();
/*     */   }
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 235 */     return (A)this.tcpServer.getLocalAddress(type);
/*     */   }
/*     */   
/*     */   public void suspendAccepts() {
/* 239 */     this.tcpServer.suspendAccepts();
/*     */   }
/*     */   
/*     */   public void resumeAccepts() {
/* 243 */     this.tcpServer.resumeAccepts();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAcceptResumed() {
/* 248 */     return this.tcpServer.isAcceptResumed();
/*     */   }
/*     */   
/*     */   public void wakeupAccepts() {
/* 252 */     this.tcpServer.wakeupAccepts();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable() throws IOException {
/* 256 */     this.tcpServer.awaitAcceptable();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/* 260 */     this.tcpServer.awaitAcceptable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getAcceptThread() {
/* 265 */     return this.tcpServer.getAcceptThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 269 */     return this.tcpServer.getIoThread();
/*     */   }
/*     */   
/*     */   protected abstract C accept(S paramS, SSLEngine paramSSLEngine) throws IOException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\AbstractAcceptingSslChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */