/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.http.HttpServerConnection;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.security.cert.X509Certificate;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.SslChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public class ConnectionSSLSessionInfo
/*     */   implements SSLSessionInfo
/*     */ {
/*  50 */   private static final SSLPeerUnverifiedException PEER_UNVERIFIED_EXCEPTION = new SSLPeerUnverifiedException("");
/*  51 */   private static final RenegotiationRequiredException RENEGOTIATION_REQUIRED_EXCEPTION = new RenegotiationRequiredException();
/*     */   
/*     */   private static final long MAX_RENEGOTIATION_WAIT = 30000L;
/*     */   
/*     */   private final SslChannel channel;
/*     */   private final HttpServerConnection serverConnection;
/*     */   private SSLPeerUnverifiedException unverified;
/*     */   private RenegotiationRequiredException renegotiationRequiredException;
/*     */   
/*     */   public ConnectionSSLSessionInfo(SslChannel channel, HttpServerConnection serverConnection) {
/*  61 */     this.channel = channel;
/*  62 */     this.serverConnection = serverConnection;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSessionId() {
/*  67 */     return this.channel.getSslSession().getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCipherSuite() {
/*  72 */     return this.channel.getSslSession().getCipherSuite();
/*     */   }
/*     */ 
/*     */   
/*     */   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
/*  77 */     if (this.unverified != null) {
/*  78 */       throw this.unverified;
/*     */     }
/*  80 */     if (this.renegotiationRequiredException != null) {
/*  81 */       throw this.renegotiationRequiredException;
/*     */     }
/*     */     try {
/*  84 */       return this.channel.getSslSession().getPeerCertificates();
/*  85 */     } catch (SSLPeerUnverifiedException e) {
/*     */       try {
/*  87 */         SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
/*  88 */         if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
/*  89 */           this.renegotiationRequiredException = RENEGOTIATION_REQUIRED_EXCEPTION;
/*  90 */           throw this.renegotiationRequiredException;
/*     */         } 
/*  92 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/*  95 */       this.unverified = PEER_UNVERIFIED_EXCEPTION;
/*  96 */       throw this.unverified;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
/* 102 */     if (this.unverified != null) {
/* 103 */       throw this.unverified;
/*     */     }
/* 105 */     if (this.renegotiationRequiredException != null) {
/* 106 */       throw this.renegotiationRequiredException;
/*     */     }
/*     */     try {
/* 109 */       return this.channel.getSslSession().getPeerCertificateChain();
/* 110 */     } catch (SSLPeerUnverifiedException e) {
/*     */       try {
/* 112 */         SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
/* 113 */         if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
/* 114 */           this.renegotiationRequiredException = RENEGOTIATION_REQUIRED_EXCEPTION;
/* 115 */           throw this.renegotiationRequiredException;
/*     */         } 
/* 117 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/* 120 */       this.unverified = PEER_UNVERIFIED_EXCEPTION;
/* 121 */       throw this.unverified;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
/* 128 */     if ("TLSv1.3".equals(this.channel.getSslSession().getProtocol()))
/*     */     {
/*     */       
/* 131 */       throw UndertowMessages.MESSAGES.renegotiationNotSupported();
/*     */     }
/* 133 */     this.unverified = null;
/* 134 */     this.renegotiationRequiredException = null;
/* 135 */     if (exchange.isRequestComplete()) {
/* 136 */       renegotiateNoRequest(exchange, sslClientAuthMode);
/*     */     } else {
/* 138 */       renegotiateBufferRequest(exchange, sslClientAuthMode);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 144 */     return this.channel.getSslSession();
/*     */   }
/*     */   
/*     */   public void renegotiateBufferRequest(HttpServerExchange exchange, SslClientAuthMode newAuthMode) throws IOException {
/* 148 */     int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
/* 149 */     if (maxSize <= 0) {
/* 150 */       throw new SSLPeerUnverifiedException("");
/*     */     }
/*     */ 
/*     */     
/* 154 */     boolean requestResetRequired = false;
/* 155 */     StreamSourceChannel requestChannel = Connectors.getExistingRequestChannel(exchange);
/* 156 */     if (requestChannel == null) {
/* 157 */       requestChannel = exchange.getRequestChannel();
/* 158 */       requestResetRequired = true;
/*     */     } 
/*     */     
/* 161 */     PooledByteBuffer pooled = exchange.getConnection().getByteBufferPool().allocate();
/* 162 */     boolean free = true;
/* 163 */     int usedBuffers = 0;
/* 164 */     PooledByteBuffer[] poolArray = null;
/* 165 */     int bufferSize = pooled.getBuffer().remaining();
/* 166 */     int allowedBuffers = (maxSize + bufferSize - 1) / bufferSize;
/* 167 */     poolArray = new PooledByteBuffer[allowedBuffers];
/* 168 */     poolArray[usedBuffers++] = pooled;
/* 169 */     boolean overflow = false;
/*     */     
/*     */     try {
/*     */       while (true) {
/* 173 */         ByteBuffer buf = pooled.getBuffer();
/* 174 */         int res = Channels.readBlocking((ReadableByteChannel)requestChannel, buf);
/* 175 */         if (!buf.hasRemaining()) {
/* 176 */           buf.flip();
/* 177 */           if (allowedBuffers == usedBuffers) {
/* 178 */             overflow = true;
/*     */             break;
/*     */           } 
/* 181 */           pooled = exchange.getConnection().getByteBufferPool().allocate();
/* 182 */           poolArray[usedBuffers++] = pooled; continue;
/*     */         } 
/* 184 */         if (res == -1) {
/* 185 */           buf.flip();
/*     */           break;
/*     */         } 
/*     */       } 
/* 189 */       free = false;
/* 190 */       Connectors.ungetRequestBytes(exchange, poolArray);
/* 191 */       if (overflow) {
/* 192 */         throw new SSLPeerUnverifiedException("Cannot renegotiate");
/*     */       }
/* 194 */       renegotiateNoRequest(exchange, newAuthMode);
/*     */     } finally {
/* 196 */       if (free) {
/* 197 */         for (PooledByteBuffer buf : poolArray) {
/* 198 */           if (buf != null) {
/* 199 */             buf.close();
/*     */           }
/*     */         } 
/*     */       }
/* 203 */       if (requestResetRequired) {
/* 204 */         exchange.requestChannel = null;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void renegotiateNoRequest(HttpServerExchange exchange, SslClientAuthMode newAuthMode) throws IOException {
/* 210 */     AbstractServerConnection.ConduitState oldState = this.serverConnection.resetChannel();
/*     */     try {
/* 212 */       SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
/* 213 */       if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
/* 214 */         SslHandshakeWaiter waiter = new SslHandshakeWaiter();
/* 215 */         this.channel.getHandshakeSetter().set(waiter);
/*     */         
/* 217 */         this.channel.setOption(Options.SSL_CLIENT_AUTH_MODE, newAuthMode);
/* 218 */         this.channel.getSslSession().invalidate();
/* 219 */         this.channel.startHandshake();
/* 220 */         this.serverConnection.getOriginalSinkConduit().flush();
/* 221 */         ByteBuffer buff = ByteBuffer.wrap(new byte[1]);
/* 222 */         long end = System.currentTimeMillis() + 30000L;
/* 223 */         while (!waiter.isDone() && this.serverConnection.isOpen() && System.currentTimeMillis() < end) {
/* 224 */           int read = this.serverConnection.getSourceChannel().read(buff);
/* 225 */           if (read != 0) {
/* 226 */             throw new SSLPeerUnverifiedException("");
/*     */           }
/* 228 */           if (!waiter.isDone()) {
/* 229 */             this.serverConnection.getSourceChannel().awaitReadable(end - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
/*     */           }
/*     */         } 
/* 232 */         if (!waiter.isDone()) {
/* 233 */           if (this.serverConnection.isOpen()) {
/* 234 */             IoUtils.safeClose((Closeable)this.serverConnection);
/* 235 */             throw UndertowMessages.MESSAGES.rengotiationTimedOut();
/*     */           } 
/* 237 */           IoUtils.safeClose((Closeable)this.serverConnection);
/* 238 */           throw UndertowMessages.MESSAGES.rengotiationFailed();
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       
/* 243 */       if (oldState != null) {
/* 244 */         this.serverConnection.restoreChannel(oldState);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class SslHandshakeWaiter
/*     */     implements ChannelListener<SslChannel>
/*     */   {
/*     */     private volatile boolean done = false;
/*     */     
/*     */     boolean isDone() {
/* 255 */       return this.done;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(SslChannel channel) {
/* 260 */       this.done = true;
/*     */     }
/*     */     
/*     */     private SslHandshakeWaiter() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ConnectionSSLSessionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */