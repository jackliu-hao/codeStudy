/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.time.Duration;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.Conduit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BlockingReadTimeoutHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final ConduitWrapper<StreamSourceConduit> streamSourceConduitWrapper;
/*     */   
/*     */   private BlockingReadTimeoutHandler(HttpHandler next, Duration readTimeout) {
/*  62 */     this.next = next;
/*  63 */     this.streamSourceConduitWrapper = new TimeoutStreamSourceConduitWrapper(readTimeout);
/*     */   }
/*     */   
/*     */   private static final class TimeoutStreamSourceConduitWrapper
/*     */     implements ConduitWrapper<StreamSourceConduit> {
/*     */     private final long timeoutNanoseconds;
/*     */     
/*     */     TimeoutStreamSourceConduitWrapper(Duration readTimeout) {
/*  71 */       this.timeoutNanoseconds = readTimeout.toNanos();
/*     */     }
/*     */ 
/*     */     
/*     */     public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
/*  76 */       return new BlockingReadTimeoutHandler.TimeoutStreamSourceConduit((StreamSourceConduit)factory.create(), exchange.getConnection(), this.timeoutNanoseconds);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  82 */     exchange.addRequestWrapper(this.streamSourceConduitWrapper);
/*  83 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class TimeoutStreamSourceConduit
/*     */     implements StreamSourceConduit
/*     */   {
/*     */     private final StreamSourceConduit delegate;
/*     */     private final ServerConnection serverConnection;
/*     */     private final long timeoutNanos;
/*     */     private long remaining;
/*     */     
/*     */     TimeoutStreamSourceConduit(StreamSourceConduit delegate, ServerConnection serverConnection, long timeoutNanos) {
/*  96 */       this.delegate = delegate;
/*  97 */       this.serverConnection = serverConnection;
/*  98 */       this.timeoutNanos = timeoutNanos;
/*  99 */       this.remaining = timeoutNanos;
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long position, long count, FileChannel fileChannel) throws IOException {
/* 104 */       return resetTimeoutIfReadSucceeded(this.delegate.transferTo(position, count, fileChannel));
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long count, ByteBuffer byteBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
/* 109 */       return resetTimeoutIfReadSucceeded(this.delegate.transferTo(count, byteBuffer, streamSinkChannel));
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(ByteBuffer byteBuffer) throws IOException {
/* 114 */       return resetTimeoutIfReadSucceeded(this.delegate.read(byteBuffer));
/*     */     }
/*     */ 
/*     */     
/*     */     public long read(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
/* 119 */       return resetTimeoutIfReadSucceeded(this.delegate.read(byteBuffers, offset, length));
/*     */     }
/*     */ 
/*     */     
/*     */     public void terminateReads() throws IOException {
/* 124 */       this.delegate.terminateReads();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isReadShutdown() {
/* 129 */       return this.delegate.isReadShutdown();
/*     */     }
/*     */ 
/*     */     
/*     */     public void resumeReads() {
/* 134 */       this.delegate.resumeReads();
/*     */     }
/*     */ 
/*     */     
/*     */     public void suspendReads() {
/* 139 */       this.delegate.suspendReads();
/*     */     }
/*     */ 
/*     */     
/*     */     public void wakeupReads() {
/* 144 */       this.delegate.wakeupReads();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isReadResumed() {
/* 149 */       return this.delegate.isReadResumed();
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitReadable() throws IOException {
/* 154 */       awaitReadable(this.remaining, TimeUnit.NANOSECONDS);
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitReadable(long duration, TimeUnit unit) throws IOException {
/* 159 */       long startTime = System.nanoTime();
/* 160 */       long requestedNanos = unit.toNanos(duration);
/*     */       try {
/* 162 */         this.delegate.awaitReadable(Math.min(requestedNanos, this.remaining), TimeUnit.NANOSECONDS);
/*     */       } finally {
/* 164 */         this.remaining -= System.nanoTime() - startTime;
/*     */       } 
/* 166 */       if (this.remaining < 0L) {
/* 167 */         ReadTimeoutException rte = UndertowMessages.MESSAGES.blockingReadTimedOut(this.timeoutNanos);
/* 168 */         UndertowLogger.REQUEST_IO_LOGGER.blockingReadTimedOut(rte);
/* 169 */         IoUtils.safeClose((Closeable)this.serverConnection);
/* 170 */         throw rte;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioIoThread getReadThread() {
/* 176 */       return this.delegate.getReadThread();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setReadReadyHandler(ReadReadyHandler readReadyHandler) {
/* 181 */       this.delegate.setReadReadyHandler(readReadyHandler);
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioWorker getWorker() {
/* 186 */       return this.delegate.getWorker();
/*     */     }
/*     */     
/*     */     private long resetTimeoutIfReadSucceeded(long value) {
/* 190 */       if (value != 0L)
/*     */       {
/* 192 */         this.remaining = this.timeoutNanos;
/*     */       }
/* 194 */       return value;
/*     */     }
/*     */     
/*     */     private int resetTimeoutIfReadSucceeded(int value) {
/* 198 */       if (value != 0)
/*     */       {
/* 200 */         this.remaining = this.timeoutNanos;
/*     */       }
/* 202 */       return value;
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 207 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private HttpHandler nextHandler;
/*     */     private Duration readTimeout;
/*     */     
/*     */     private Builder() {}
/*     */     
/*     */     public Builder readTimeout(Duration readTimeout) {
/* 218 */       this.readTimeout = Objects.<Duration>requireNonNull(readTimeout, "A read timeout is required");
/* 219 */       return this;
/*     */     }
/*     */     
/*     */     public Builder nextHandler(HttpHandler nextHandler) {
/* 223 */       this.nextHandler = Objects.<HttpHandler>requireNonNull(nextHandler, "HttpHandler is required");
/* 224 */       return this;
/*     */     }
/*     */     
/*     */     public HttpHandler build() {
/* 228 */       HttpHandler next = Objects.<HttpHandler>requireNonNull(this.nextHandler, "HttpHandler is required");
/* 229 */       if (this.readTimeout == null) {
/* 230 */         throw new IllegalArgumentException("A read timeout is required");
/*     */       }
/* 232 */       if (this.readTimeout.isZero() || this.readTimeout.isNegative()) {
/* 233 */         throw new IllegalArgumentException("Read timeout must be positive: " + this.readTimeout);
/*     */       }
/* 235 */       return new BlockingReadTimeoutHandler(next, this.readTimeout);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\BlockingReadTimeoutHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */