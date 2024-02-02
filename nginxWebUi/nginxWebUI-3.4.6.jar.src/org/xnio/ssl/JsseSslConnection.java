/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.StreamConnection;
/*     */ 
/*     */ public final class JsseSslConnection
/*     */   extends SslConnection {
/*     */   private final StreamConnection streamConnection;
/*     */   private final JsseStreamConduit conduit;
/*  22 */   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*     */   public JsseSslConnection(StreamConnection streamConnection, SSLEngine engine) {
/*  25 */     this(streamConnection, engine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool);
/*     */   }
/*     */   
/*     */   JsseSslConnection(StreamConnection streamConnection, SSLEngine engine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) {
/*  29 */     super(streamConnection.getIoThread());
/*  30 */     this.streamConnection = streamConnection;
/*  31 */     this.conduit = new JsseStreamConduit(this, engine, streamConnection.getSourceChannel().getConduit(), streamConnection.getSinkChannel().getConduit(), socketBufferPool, applicationBufferPool);
/*  32 */     setSourceConduit(this.conduit);
/*  33 */     setSinkConduit(this.conduit);
/*     */   }
/*     */   
/*     */   public void startHandshake() throws IOException {
/*  37 */     this.conduit.beginHandshake();
/*     */   }
/*     */   
/*     */   public SSLSession getSslSession() {
/*  41 */     return this.conduit.getSslSession();
/*     */   }
/*     */   
/*     */   protected void closeAction() throws IOException {
/*     */     try {
/*  46 */       if (!this.conduit.isWriteShutdown()) {
/*  47 */         this.conduit.terminateWrites();
/*     */       }
/*  49 */       if (!this.conduit.isReadShutdown()) {
/*  50 */         this.conduit.terminateReads();
/*     */       }
/*  52 */       this.conduit.flush();
/*  53 */       this.conduit.markTerminated();
/*  54 */       this.streamConnection.close();
/*  55 */     } catch (Throwable t) {
/*     */       
/*     */       try {
/*  58 */         if (!this.conduit.isReadShutdown()) {
/*  59 */           this.conduit.terminateReads();
/*     */         }
/*  61 */       } catch (Throwable throwable) {}
/*     */       try {
/*  63 */         this.conduit.markTerminated();
/*  64 */         this.streamConnection.close();
/*  65 */       } catch (Throwable throwable) {}
/*  66 */       throw t;
/*     */     } 
/*     */   }
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/*  71 */     return this.streamConnection.getPeerAddress();
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/*  75 */     return this.streamConnection.getLocalAddress();
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
/*  79 */     return (ChannelListener.Setter<? extends SslConnection>)this.handshakeSetter;
/*     */   }
/*     */   
/*     */   void invokeHandshakeListener() {
/*  83 */     ChannelListeners.invokeChannelListener((Channel)this, this.handshakeSetter.get());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*  89 */     if (option == Options.SSL_CLIENT_AUTH_MODE) {
/*  90 */       SSLEngine engine = this.conduit.getEngine();
/*     */       try {
/*  92 */         return (T)option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */       } finally {
/*  94 */         engine.setNeedClientAuth((value == SslClientAuthMode.REQUIRED));
/*  95 */         engine.setWantClientAuth((value == SslClientAuthMode.REQUESTED));
/*     */       } 
/*  97 */     }  if (option == Options.SECURE) {
/*  98 */       throw new IllegalArgumentException();
/*     */     }
/* 100 */     return (T)this.streamConnection.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 107 */     if (option == Options.SSL_CLIENT_AUTH_MODE) {
/* 108 */       SSLEngine engine = this.conduit.getEngine();
/* 109 */       return (T)option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */     } 
/* 111 */     return (option == Options.SECURE) ? (T)option.cast(Boolean.valueOf(this.conduit.isTls())) : (T)this.streamConnection.getOption(option);
/*     */   }
/*     */ 
/*     */   
/* 115 */   private static final Set<Option<?>> SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 120 */     return (SUPPORTED_OPTIONS.contains(option) || this.streamConnection.supportsOption(option));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 125 */     return this.streamConnection.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 130 */     return this.streamConnection.isWriteShutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadShutdown() {
/* 135 */     return this.streamConnection.isReadShutdown();
/*     */   }
/*     */   
/*     */   public SSLEngine getEngine() {
/* 139 */     return this.conduit.getEngine();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */