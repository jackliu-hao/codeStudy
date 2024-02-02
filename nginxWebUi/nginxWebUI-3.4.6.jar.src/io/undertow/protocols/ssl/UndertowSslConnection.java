/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.StreamConnection;
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
/*     */ class UndertowSslConnection
/*     */   extends SslConnection
/*     */ {
/*  42 */   private static final Set<Option<?>> SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
/*     */   
/*     */   private final StreamConnection delegate;
/*     */   private final SslConduit sslConduit;
/*  46 */   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter = new ChannelListener.SimpleSetter();
/*     */ 
/*     */ 
/*     */   
/*     */   private final SSLEngine engine;
/*     */ 
/*     */ 
/*     */   
/*     */   UndertowSslConnection(StreamConnection delegate, SSLEngine engine, ByteBufferPool bufferPool, Executor delegatedTaskExecutor) {
/*  55 */     super(delegate.getIoThread());
/*  56 */     this.delegate = delegate;
/*  57 */     this.engine = engine;
/*  58 */     this.sslConduit = new SslConduit(this, delegate, engine, delegatedTaskExecutor, bufferPool, new HandshakeCallback());
/*  59 */     setSourceConduit(this.sslConduit);
/*  60 */     setSinkConduit(this.sslConduit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void startHandshake() throws IOException {
/*  65 */     this.sslConduit.startHandshake();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSslSession() {
/*  70 */     return this.sslConduit.getSslSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
/*  75 */     return (ChannelListener.Setter<? extends SslConnection>)this.handshakeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void notifyWriteClosed() {
/*     */     try {
/*  81 */       this.sslConduit.notifyWriteClosed();
/*     */     } finally {
/*  83 */       super.notifyWriteClosed();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void notifyReadClosed() {
/*     */     try {
/*  90 */       this.sslConduit.notifyReadClosed();
/*     */     } finally {
/*  92 */       super.notifyReadClosed();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/*  98 */     return this.delegate.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 103 */     return this.delegate.getLocalAddress();
/*     */   }
/*     */   
/*     */   public SSLEngine getSSLEngine() {
/* 107 */     return this.sslConduit.getSSLEngine();
/*     */   }
/*     */   
/*     */   SslConduit getSslConduit() {
/* 111 */     return this.sslConduit;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 117 */     if (option == Options.SSL_CLIENT_AUTH_MODE)
/*     */       try {
/* 119 */         return (T)option.cast(this.engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (this.engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */       } finally {
/* 121 */         this.engine.setWantClientAuth(false);
/* 122 */         this.engine.setNeedClientAuth(false);
/* 123 */         if (value == SslClientAuthMode.REQUESTED) {
/* 124 */           this.engine.setWantClientAuth(true);
/* 125 */         } else if (value == SslClientAuthMode.REQUIRED) {
/* 126 */           this.engine.setNeedClientAuth(true);
/*     */         } 
/*     */       }  
/* 129 */     if (option == Options.SECURE) {
/* 130 */       throw new IllegalArgumentException();
/*     */     }
/* 132 */     return (T)this.delegate.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 139 */     if (option == Options.SSL_CLIENT_AUTH_MODE) {
/* 140 */       return (T)option.cast(this.engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (this.engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */     }
/* 142 */     return (option == Options.SECURE) ? (T)Boolean.TRUE : (T)this.delegate.getOption(option);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 149 */     return (SUPPORTED_OPTIONS.contains(option) || this.delegate.supportsOption(option));
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean readClosed() {
/* 154 */     return super.readClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean writeClosed() {
/* 159 */     return super.writeClosed();
/*     */   }
/*     */   
/*     */   protected void closeAction() {
/* 163 */     this.sslConduit.close();
/*     */   }
/*     */   
/*     */   private final class HandshakeCallback implements Runnable {
/*     */     private HandshakeCallback() {}
/*     */     
/*     */     public void run() {
/* 170 */       ChannelListener<? super SslConnection> listener = UndertowSslConnection.this.handshakeSetter.get();
/* 171 */       if (listener == null) {
/*     */         return;
/*     */       }
/* 174 */       ChannelListeners.invokeChannelListener((Channel)UndertowSslConnection.this, listener);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\UndertowSslConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */