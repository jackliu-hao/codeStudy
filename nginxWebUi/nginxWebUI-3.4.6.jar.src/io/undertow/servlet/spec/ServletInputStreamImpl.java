/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import javax.servlet.ReadListener;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.EmptyStreamSourceChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletInputStreamImpl
/*     */   extends ServletInputStream
/*     */ {
/*     */   private final HttpServletRequestImpl request;
/*     */   private final StreamSourceChannel channel;
/*     */   private final ByteBufferPool bufferPool;
/*     */   private volatile ReadListener listener;
/*     */   private volatile ServletInputStreamChannelListener internalListener;
/*     */   private static final int FLAG_READY = 1;
/*     */   private static final int FLAG_CLOSED = 2;
/*     */   private static final int FLAG_FINISHED = 4;
/*     */   private static final int FLAG_ON_DATA_READ_CALLED = 8;
/*     */   private static final int FLAG_CALL_ON_ALL_DATA_READ = 16;
/*     */   private static final int FLAG_BEING_INVOKED_IN_IO_THREAD = 32;
/*     */   private static final int FLAG_IS_READY_CALLED = 64;
/*     */   private volatile int state;
/*     */   private volatile AsyncContextImpl asyncContext;
/*     */   private volatile PooledByteBuffer pooled;
/*     */   private volatile boolean asyncIoStarted;
/*  71 */   private static final AtomicIntegerFieldUpdater<ServletInputStreamImpl> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ServletInputStreamImpl.class, "state");
/*     */   
/*     */   public ServletInputStreamImpl(HttpServletRequestImpl request) {
/*  74 */     this.request = request;
/*  75 */     if (request.getExchange().isRequestChannelAvailable()) {
/*  76 */       this.channel = request.getExchange().getRequestChannel();
/*     */     } else {
/*  78 */       this.channel = (StreamSourceChannel)new EmptyStreamSourceChannel(request.getExchange().getIoThread());
/*     */     } 
/*  80 */     this.bufferPool = request.getExchange().getConnection().getByteBufferPool();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFinished() {
/*  86 */     return Bits.anyAreSet(this.state, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/*  91 */     if (!this.asyncContext.isInitialRequestDone()) {
/*  92 */       return false;
/*     */     }
/*  94 */     boolean finished = Bits.anyAreSet(this.state, 4);
/*  95 */     if (finished && 
/*  96 */       Bits.anyAreClear(this.state, 8)) {
/*  97 */       if (Bits.allAreClear(this.state, 32)) {
/*  98 */         setFlags(8);
/*  99 */         this.request.getServletContext().invokeOnAllDataRead(this.request.getExchange(), this.listener);
/*     */       } else {
/* 101 */         setFlags(16);
/*     */       } 
/*     */     }
/*     */     
/* 105 */     if (!this.asyncIoStarted)
/*     */     {
/* 107 */       return false;
/*     */     }
/* 109 */     boolean ready = (Bits.anyAreSet(this.state, 1) && !finished);
/* 110 */     if (!ready && this.listener != null && !finished) {
/* 111 */       this.channel.resumeReads();
/*     */     }
/* 113 */     if (ready) {
/* 114 */       setFlags(64);
/*     */     }
/* 116 */     return ready;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReadListener(ReadListener readListener) {
/* 121 */     if (readListener == null) {
/* 122 */       throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
/*     */     }
/* 124 */     if (this.listener != null) {
/* 125 */       throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
/*     */     }
/* 127 */     if (!this.request.isAsyncStarted()) {
/* 128 */       throw UndertowServletMessages.MESSAGES.asyncNotStarted();
/*     */     }
/*     */     
/* 131 */     this.asyncContext = this.request.getAsyncContext();
/* 132 */     this.listener = readListener;
/* 133 */     this.channel.getReadSetter().set(this.internalListener = new ServletInputStreamChannelListener());
/*     */ 
/*     */     
/* 136 */     this.asyncContext.addAsyncTask(new Runnable()
/*     */         {
/*     */           public void run() {
/* 139 */             ServletInputStreamImpl.this.channel.getIoThread().execute(new Runnable()
/*     */                 {
/*     */                   public void run() {
/* 142 */                     ServletInputStreamImpl.this.asyncIoStarted = true;
/* 143 */                     ServletInputStreamImpl.this.internalListener.handleEvent(ServletInputStreamImpl.this.channel);
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 152 */     byte[] b = new byte[1];
/* 153 */     int read = read(b);
/* 154 */     if (read == -1) {
/* 155 */       return -1;
/*     */     }
/* 157 */     return b[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 162 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 167 */     if (Bits.anyAreSet(this.state, 2)) {
/* 168 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 170 */     if (this.listener != null) {
/* 171 */       if (Bits.anyAreClear(this.state, 65)) {
/* 172 */         throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */       }
/* 174 */       clearFlags(64);
/*     */     } else {
/* 176 */       readIntoBuffer();
/*     */     } 
/* 178 */     if (Bits.anyAreSet(this.state, 4)) {
/* 179 */       return -1;
/*     */     }
/* 181 */     if (len == 0) {
/* 182 */       return 0;
/*     */     }
/* 184 */     ByteBuffer buffer = this.pooled.getBuffer();
/* 185 */     int copied = Math.min(buffer.remaining(), len);
/* 186 */     buffer.get(b, off, copied);
/* 187 */     if (!buffer.hasRemaining()) {
/* 188 */       this.pooled.close();
/* 189 */       this.pooled = null;
/* 190 */       if (this.listener != null) {
/* 191 */         readIntoBufferNonBlocking();
/*     */       }
/*     */     } 
/* 194 */     return copied;
/*     */   }
/*     */   
/*     */   private void readIntoBuffer() throws IOException {
/* 198 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 4)) {
/* 199 */       this.pooled = this.bufferPool.allocate();
/*     */       
/* 201 */       int res = Channels.readBlocking((ReadableByteChannel)this.channel, this.pooled.getBuffer());
/* 202 */       this.pooled.getBuffer().flip();
/* 203 */       if (res == -1) {
/* 204 */         setFlags(4);
/* 205 */         this.pooled.close();
/* 206 */         this.pooled = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readIntoBufferNonBlocking() throws IOException {
/* 212 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 4)) {
/* 213 */       this.pooled = this.bufferPool.allocate();
/* 214 */       if (this.listener == null) {
/* 215 */         int res = this.channel.read(this.pooled.getBuffer());
/* 216 */         if (res == 0) {
/* 217 */           this.pooled.close();
/* 218 */           this.pooled = null;
/*     */           return;
/*     */         } 
/* 221 */         this.pooled.getBuffer().flip();
/* 222 */         if (res == -1) {
/* 223 */           setFlags(4);
/* 224 */           this.pooled.close();
/* 225 */           this.pooled = null;
/*     */         } 
/*     */       } else {
/* 228 */         int res = this.channel.read(this.pooled.getBuffer());
/* 229 */         this.pooled.getBuffer().flip();
/* 230 */         if (res == -1) {
/* 231 */           setFlags(4);
/* 232 */           this.pooled.close();
/* 233 */           this.pooled = null;
/* 234 */         } else if (res == 0) {
/* 235 */           clearFlags(1);
/* 236 */           this.pooled.close();
/* 237 */           this.pooled = null;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 245 */     if (Bits.anyAreSet(this.state, 2)) {
/* 246 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 248 */     readIntoBufferNonBlocking();
/* 249 */     if (Bits.anyAreSet(this.state, 4)) {
/* 250 */       return 0;
/*     */     }
/* 252 */     if (this.pooled == null) {
/* 253 */       return 0;
/*     */     }
/* 255 */     return this.pooled.getBuffer().remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 260 */     if (Bits.anyAreSet(this.state, 2)) {
/*     */       return;
/*     */     }
/* 263 */     setFlags(2);
/*     */     try {
/* 265 */       while (Bits.allAreClear(this.state, 4)) {
/* 266 */         readIntoBuffer();
/* 267 */         if (this.pooled != null) {
/* 268 */           this.pooled.close();
/* 269 */           this.pooled = null;
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 273 */       setFlags(4);
/* 274 */       if (this.pooled != null) {
/* 275 */         this.pooled.close();
/* 276 */         this.pooled = null;
/*     */       } 
/* 278 */       this.channel.shutdownReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   private class ServletInputStreamChannelListener
/*     */     implements ChannelListener<StreamSourceChannel> {
/*     */     public void handleEvent(StreamSourceChannel channel) {
/*     */       try {
/* 286 */         if (ServletInputStreamImpl.this.asyncContext.isDispatched()) {
/*     */ 
/*     */ 
/*     */           
/* 290 */           channel.suspendReads();
/*     */           return;
/*     */         } 
/* 293 */         if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
/* 294 */           channel.suspendReads();
/*     */           return;
/*     */         } 
/* 297 */         ServletInputStreamImpl.this.readIntoBufferNonBlocking();
/* 298 */         if (ServletInputStreamImpl.this.pooled != null) {
/* 299 */           channel.suspendReads();
/* 300 */           ServletInputStreamImpl.this.setFlags(1);
/* 301 */           if (!Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
/* 302 */             ServletInputStreamImpl.this.setFlags(32);
/*     */             try {
/* 304 */               ServletInputStreamImpl.this.request.getServletContext().invokeOnDataAvailable(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
/*     */             } finally {
/* 306 */               ServletInputStreamImpl.this.clearFlags(32);
/*     */             } 
/* 308 */             if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 16) && Bits.allAreClear(ServletInputStreamImpl.this.state, 8)) {
/* 309 */               ServletInputStreamImpl.this.setFlags(8);
/* 310 */               ServletInputStreamImpl.this.request.getServletContext().invokeOnAllDataRead(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
/*     */             } 
/*     */           } 
/* 313 */         } else if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
/* 314 */           if (Bits.allAreClear(ServletInputStreamImpl.this.state, 8)) {
/* 315 */             ServletInputStreamImpl.this.setFlags(8);
/* 316 */             ServletInputStreamImpl.this.request.getServletContext().invokeOnAllDataRead(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
/*     */           } 
/*     */         } else {
/* 319 */           channel.resumeReads();
/*     */         } 
/* 321 */       } catch (Throwable e) {
/*     */         try {
/* 323 */           ServletInputStreamImpl.this.request.getServletContext().invokeRunnable(ServletInputStreamImpl.this.request.getExchange(), new Runnable()
/*     */               {
/*     */                 public void run() {
/* 326 */                   ServletInputStreamImpl.this.listener.onError(e);
/*     */                 }
/*     */               });
/*     */         } finally {
/* 330 */           if (ServletInputStreamImpl.this.pooled != null) {
/* 331 */             ServletInputStreamImpl.this.pooled.close();
/* 332 */             ServletInputStreamImpl.this.pooled = null;
/*     */           } 
/* 334 */           IoUtils.safeClose((Closeable)channel);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     private ServletInputStreamChannelListener() {} }
/*     */   
/*     */   private void setFlags(int flags) {
/*     */     int old;
/*     */     do {
/* 343 */       old = this.state;
/* 344 */     } while (!stateUpdater.compareAndSet(this, old, old | flags));
/*     */   }
/*     */   
/*     */   private void clearFlags(int flags) {
/*     */     int old;
/*     */     do {
/* 350 */       old = this.state;
/* 351 */     } while (!stateUpdater.compareAndSet(this, old, old & (flags ^ 0xFFFFFFFF)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletInputStreamImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */