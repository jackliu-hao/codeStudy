/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ 
/*     */ public final class JsseSslStreamConnection
/*     */   extends SslConnection
/*     */ {
/*     */   private final StreamConnection connection;
/*     */   private final JsseSslConduitEngine sslConduitEngine;
/*     */   private volatile boolean tls;
/*  66 */   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*     */   private volatile int state;
/*     */   
/*     */   private static final int FLAG_READ_CLOSE_REQUESTED = 1;
/*     */   
/*     */   private static final int FLAG_WRITE_CLOSE_REQUESTED = 2;
/*     */   
/*     */   private static final int FLAG_READ_CLOSED = 4;
/*     */   private static final int FLAG_WRITE_CLOSED = 8;
/*  76 */   private static final AtomicIntegerFieldUpdater<JsseSslStreamConnection> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JsseSslStreamConnection.class, "state");
/*     */   
/*     */   public JsseSslStreamConnection(StreamConnection connection, SSLEngine sslEngine, boolean startTls) {
/*  79 */     this(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool, startTls);
/*     */   }
/*     */   
/*     */   JsseSslStreamConnection(StreamConnection connection, SSLEngine sslEngine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
/*  83 */     super(connection.getIoThread());
/*  84 */     this.connection = connection;
/*  85 */     StreamSinkConduit sinkConduit = connection.getSinkChannel().getConduit();
/*  86 */     StreamSourceConduit sourceConduit = connection.getSourceChannel().getConduit();
/*  87 */     this.sslConduitEngine = new JsseSslConduitEngine(this, sinkConduit, sourceConduit, sslEngine, socketBufferPool, applicationBufferPool);
/*  88 */     this.tls = !startTls;
/*  89 */     setSinkConduit((StreamSinkConduit)new JsseSslStreamSinkConduit(sinkConduit, this.sslConduitEngine, this.tls));
/*  90 */     setSourceConduit((StreamSourceConduit)new JsseSslStreamSourceConduit(sourceConduit, this.sslConduitEngine, this.tls));
/*  91 */     getSourceChannel().setCloseListener(channel -> readClosed());
/*  92 */     getSinkChannel().setCloseListener(channel -> writeClosed());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void startHandshake() throws IOException {
/*  98 */     if (!this.tls) {
/*  99 */       this.tls = true;
/* 100 */       ((JsseSslStreamSourceConduit)getSourceChannel().getConduit()).enableTls();
/* 101 */       ((JsseSslStreamSinkConduit)getSinkChannel().getConduit()).enableTls();
/*     */     } 
/* 103 */     this.sslConduitEngine.beginHandshake();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 109 */     return this.connection.getPeerAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 116 */     return this.connection.getLocalAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void closeAction() throws IOException {
/* 125 */     if (!this.sslConduitEngine.isClosed()) {
/* 126 */       this.sslConduitEngine.close();
/*     */     } else {
/* 128 */       if (this.tls) {
/*     */         try {
/* 130 */           getSinkChannel().getConduit().terminateWrites();
/* 131 */         } catch (IOException e) {
/*     */           try {
/* 133 */             getSourceChannel().getConduit().terminateReads();
/* 134 */           } catch (IOException iOException) {}
/*     */           
/* 136 */           IoUtils.safeClose((Closeable)this.connection);
/* 137 */           throw e;
/*     */         } 
/*     */         try {
/* 140 */           getSourceChannel().getConduit().terminateReads();
/* 141 */         } catch (IOException e) {
/* 142 */           IoUtils.safeClose((Closeable)this.connection);
/* 143 */           throw e;
/*     */         } 
/* 145 */         super.closeAction();
/*     */       } 
/* 147 */       this.connection.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void notifyWriteClosed() {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void notifyReadClosed() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 162 */     if (option == Options.SSL_CLIENT_AUTH_MODE) {
/* 163 */       SSLEngine engine = this.sslConduitEngine.getEngine();
/*     */       try {
/* 165 */         return (T)option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */       } finally {
/* 167 */         engine.setNeedClientAuth((value == SslClientAuthMode.REQUIRED));
/* 168 */         engine.setWantClientAuth((value == SslClientAuthMode.REQUESTED));
/*     */       } 
/* 170 */     }  if (option == Options.SECURE) {
/* 171 */       throw new IllegalArgumentException();
/*     */     }
/* 173 */     return (T)this.connection.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 180 */     if (option == Options.SSL_CLIENT_AUTH_MODE) {
/* 181 */       SSLEngine engine = this.sslConduitEngine.getEngine();
/* 182 */       return (T)option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
/*     */     } 
/* 184 */     return (option == Options.SECURE) ? (T)option.cast(Boolean.valueOf(this.tls)) : (T)this.connection.getOption(option);
/*     */   }
/*     */ 
/*     */   
/* 188 */   private static final Set<Option<?>> SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 193 */     return (SUPPORTED_OPTIONS.contains(option) || this.connection.supportsOption(option));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLSession getSslSession() {
/* 199 */     return this.tls ? this.sslConduitEngine.getSession() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
/* 204 */     return (ChannelListener.Setter<? extends SslConnection>)this.handshakeSetter;
/*     */   }
/*     */   
/*     */   SSLEngine getEngine() {
/* 208 */     return this.sslConduitEngine.getEngine();
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean readClosed() {
/* 213 */     synchronized (this) {
/*     */       int oldVal, newVal;
/*     */       do {
/* 216 */         oldVal = this.state;
/* 217 */         if (Bits.allAreSet(oldVal, 1)) {
/*     */           break;
/*     */         }
/* 220 */         newVal = oldVal | 0x1;
/* 221 */       } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/* 222 */       boolean closeRequestedNow = Bits.allAreSet(oldVal, 1);
/* 223 */       if (this.sslConduitEngine.isClosed() || !this.tls)
/*     */         while (true) {
/* 225 */           oldVal = this.state;
/* 226 */           if (Bits.allAreSet(oldVal, 4)) {
/* 227 */             return false;
/*     */           }
/* 229 */           newVal = oldVal | 0x4;
/* 230 */           if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 231 */             if (Bits.allAreSet(oldVal, 4)) {
/* 232 */               return false;
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 237 */             super.readClosed();
/* 238 */             return closeRequestedNow;
/*     */           } 
/*     */         }  
/*     */       return closeRequestedNow;
/*     */     }  } protected boolean writeClosed() {
/* 243 */     synchronized (this) {
/*     */       int oldVal, newVal;
/*     */       do {
/* 246 */         oldVal = this.state;
/* 247 */         if (Bits.allAreSet(oldVal, 2)) {
/*     */           break;
/*     */         }
/* 250 */         newVal = oldVal | 0x2;
/* 251 */       } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/* 252 */       boolean closeRequestedNow = Bits.allAreSet(oldVal, 2);
/* 253 */       if (this.sslConduitEngine.isClosed() || !this.tls)
/*     */         while (true) {
/* 255 */           oldVal = this.state;
/* 256 */           if (Bits.allAreSet(oldVal, 8)) {
/* 257 */             return false;
/*     */           }
/* 259 */           newVal = oldVal | 0x8;
/* 260 */           if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 261 */             if (Bits.allAreSet(oldVal, 8)) {
/* 262 */               return false;
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 268 */             super.writeClosed();
/* 269 */             return closeRequestedNow;
/*     */           } 
/*     */         }  
/*     */       return closeRequestedNow;
/*     */     } 
/*     */   }
/*     */   protected void handleHandshakeFinished() {
/* 276 */     ChannelListener<? super SslConnection> listener = this.handshakeSetter.get();
/* 277 */     if (listener == null) {
/*     */       return;
/*     */     }
/* 280 */     ChannelListeners.invokeChannelListener((Channel)this, listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadShutdown() {
/* 285 */     return Bits.allAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 294 */     return Bits.allAreSet(this.state, 2);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslStreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */