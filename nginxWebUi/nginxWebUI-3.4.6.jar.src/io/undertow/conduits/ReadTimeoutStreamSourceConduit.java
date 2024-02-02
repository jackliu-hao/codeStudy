/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.SuspendableReadChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.ReadReadyHandler;
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
/*     */ public final class ReadTimeoutStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private XnioExecutor.Key handle;
/*     */   private final StreamConnection connection;
/*  54 */   private volatile long expireTime = -1L;
/*     */   
/*     */   private final OpenListener openListener;
/*     */   private static final int FUZZ_FACTOR = 50;
/*     */   private volatile boolean expired;
/*     */   
/*  60 */   private final Runnable timeoutCommand = new Runnable()
/*     */     {
/*     */       public void run() {
/*  63 */         ReadTimeoutStreamSourceConduit.this.handle = null;
/*  64 */         if (ReadTimeoutStreamSourceConduit.this.expireTime == -1L) {
/*     */           return;
/*     */         }
/*  67 */         long current = System.currentTimeMillis();
/*  68 */         if (current < ReadTimeoutStreamSourceConduit.this.expireTime) {
/*     */           
/*  70 */           ReadTimeoutStreamSourceConduit.this.handle = WorkerUtils.executeAfter(ReadTimeoutStreamSourceConduit.this.connection.getIoThread(), ReadTimeoutStreamSourceConduit.this.timeoutCommand, ReadTimeoutStreamSourceConduit.this.expireTime - current + 50L, TimeUnit.MILLISECONDS);
/*     */           return;
/*     */         } 
/*  73 */         UndertowLogger.REQUEST_LOGGER.tracef("Timing out channel %s due to inactivity", ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel());
/*  74 */         synchronized (ReadTimeoutStreamSourceConduit.this) {
/*  75 */           ReadTimeoutStreamSourceConduit.this.expired = true;
/*     */         } 
/*  77 */         boolean readResumed = ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel().isReadResumed();
/*  78 */         ChannelListener<? super ConduitStreamSourceChannel> readListener = ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel().getReadListener();
/*     */         
/*  80 */         if (readResumed) {
/*  81 */           ChannelListeners.invokeChannelListener((Channel)ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel(), readListener);
/*     */         }
/*  83 */         if (ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel().isWriteResumed()) {
/*  84 */           ChannelListeners.invokeChannelListener((Channel)ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel(), ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel().getWriteListener());
/*     */         }
/*     */         
/*  87 */         IoUtils.safeClose((Closeable)ReadTimeoutStreamSourceConduit.this.connection);
/*     */       }
/*     */     };
/*     */   
/*     */   public ReadTimeoutStreamSourceConduit(StreamSourceConduit delegate, StreamConnection connection, OpenListener openListener) {
/*  92 */     super(delegate);
/*  93 */     this.connection = connection;
/*  94 */     this.openListener = openListener;
/*  95 */     final ReadReadyHandler.ChannelListenerHandler handler = new ReadReadyHandler.ChannelListenerHandler((SuspendableReadChannel)connection.getSourceChannel());
/*  96 */     delegate.setReadReadyHandler(new ReadReadyHandler()
/*     */         {
/*     */           public void readReady() {
/*  99 */             handler.readReady();
/*     */           }
/*     */ 
/*     */           
/*     */           public void forceTermination() {
/* 104 */             ReadTimeoutStreamSourceConduit.this.cleanup();
/* 105 */             handler.forceTermination();
/*     */           }
/*     */ 
/*     */           
/*     */           public void terminated() {
/* 110 */             ReadTimeoutStreamSourceConduit.this.cleanup();
/* 111 */             handler.terminated();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void handleReadTimeout(long ret) throws IOException {
/* 117 */     if (!this.connection.isOpen()) {
/* 118 */       cleanup();
/*     */       return;
/*     */     } 
/* 121 */     if (ret == -1L) {
/* 122 */       cleanup();
/*     */       return;
/*     */     } 
/* 125 */     Integer timeout = getTimeout();
/* 126 */     if (timeout == null || timeout.intValue() <= 0) {
/*     */       return;
/*     */     }
/* 129 */     long currentTime = System.currentTimeMillis();
/* 130 */     if (ret == 0L) {
/* 131 */       long expireTimeVar = this.expireTime;
/* 132 */       if (expireTimeVar != -1L && currentTime > expireTimeVar) {
/* 133 */         IoUtils.safeClose((Closeable)this.connection);
/* 134 */         throw UndertowMessages.MESSAGES.readTimedOut(getTimeout().intValue());
/*     */       } 
/*     */     } 
/* 137 */     this.expireTime = currentTime + timeout.intValue();
/* 138 */     if (this.handle == null) {
/* 139 */       this.handle = this.connection.getIoThread().executeAfter(this.timeoutCommand, timeout.intValue(), TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 145 */     checkExpired();
/* 146 */     long ret = super.transferTo(position, count, target);
/* 147 */     handleReadTimeout(ret);
/* 148 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 153 */     checkExpired();
/* 154 */     long ret = super.transferTo(count, throughBuffer, target);
/* 155 */     handleReadTimeout(ret);
/* 156 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 161 */     checkExpired();
/* 162 */     long ret = super.read(dsts, offset, length);
/* 163 */     handleReadTimeout(ret);
/* 164 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 169 */     checkExpired();
/* 170 */     int ret = super.read(dst);
/* 171 */     handleReadTimeout(ret);
/* 172 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 177 */     checkExpired();
/* 178 */     Integer timeout = getTimeout();
/* 179 */     if (timeout != null && timeout.intValue() > 0) {
/* 180 */       super.awaitReadable((timeout.intValue() + 50), TimeUnit.MILLISECONDS);
/*     */     } else {
/* 182 */       super.awaitReadable();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 188 */     checkExpired();
/* 189 */     Integer timeout = getTimeout();
/* 190 */     if (timeout != null && timeout.intValue() > 0) {
/* 191 */       long millis = timeUnit.toMillis(time);
/* 192 */       super.awaitReadable(Math.min(millis, (timeout.intValue() + 50)), TimeUnit.MILLISECONDS);
/*     */     } else {
/* 194 */       super.awaitReadable(time, timeUnit);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Integer getTimeout() {
/* 199 */     Integer timeout = Integer.valueOf(0);
/*     */     try {
/* 201 */       timeout = (Integer)this.connection.getSourceChannel().getOption(Options.READ_TIMEOUT);
/* 202 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 205 */     Integer idleTimeout = (Integer)this.openListener.getUndertowOptions().get(UndertowOptions.IDLE_TIMEOUT);
/* 206 */     if ((timeout == null || timeout.intValue() <= 0) && idleTimeout != null) {
/* 207 */       timeout = idleTimeout;
/* 208 */     } else if (timeout != null && idleTimeout != null && idleTimeout.intValue() > 0) {
/* 209 */       timeout = Integer.valueOf(Math.min(timeout.intValue(), idleTimeout.intValue()));
/*     */     } 
/* 211 */     return timeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 216 */     super.resumeReads();
/* 217 */     if (this.handle == null) {
/*     */       try {
/* 219 */         handleReadTimeout(1L);
/* 220 */       } catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 228 */     checkExpired();
/* 229 */     super.terminateReads();
/* 230 */     cleanup();
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 234 */     if (this.handle != null) {
/* 235 */       this.handle.remove();
/* 236 */       this.handle = null;
/* 237 */       this.expireTime = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 243 */     super.suspendReads();
/* 244 */     cleanup();
/*     */   }
/*     */   
/*     */   private void checkExpired() throws ReadTimeoutException {
/* 248 */     synchronized (this) {
/* 249 */       if (this.expired) {
/* 250 */         throw UndertowMessages.MESSAGES.readTimedOut(System.currentTimeMillis());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 256 */     return super.toString() + " (next: " + this.next + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\ReadTimeoutStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */