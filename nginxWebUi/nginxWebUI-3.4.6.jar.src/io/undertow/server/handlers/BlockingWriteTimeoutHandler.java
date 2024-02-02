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
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.WriteTimeoutException;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ public final class BlockingWriteTimeoutHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final ConduitWrapper<StreamSinkConduit> streamSinkConduitWrapper;
/*     */   
/*     */   private BlockingWriteTimeoutHandler(HttpHandler next, Duration writeTimeout) {
/*  62 */     this.next = next;
/*  63 */     this.streamSinkConduitWrapper = new TimeoutStreamSinkConduitWrapper(writeTimeout);
/*     */   }
/*     */   
/*     */   private static final class TimeoutStreamSinkConduitWrapper
/*     */     implements ConduitWrapper<StreamSinkConduit> {
/*     */     private final long timeoutNanoseconds;
/*     */     
/*     */     TimeoutStreamSinkConduitWrapper(Duration writeTimeout) {
/*  71 */       this.timeoutNanoseconds = writeTimeout.toNanos();
/*     */     }
/*     */ 
/*     */     
/*     */     public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  76 */       return new BlockingWriteTimeoutHandler.TimeoutStreamSinkConduit((StreamSinkConduit)factory.create(), exchange.getConnection(), this.timeoutNanoseconds);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  82 */     exchange.addResponseWrapper(this.streamSinkConduitWrapper);
/*  83 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class TimeoutStreamSinkConduit
/*     */     implements StreamSinkConduit
/*     */   {
/*     */     private final StreamSinkConduit delegate;
/*     */     
/*     */     private final ServerConnection serverConnection;
/*     */     private final long timeoutNanos;
/*     */     private long remaining;
/*     */     
/*     */     TimeoutStreamSinkConduit(StreamSinkConduit delegate, ServerConnection serverConnection, long timeoutNanos) {
/*  97 */       this.delegate = delegate;
/*  98 */       this.serverConnection = serverConnection;
/*  99 */       this.timeoutNanos = timeoutNanos;
/* 100 */       this.remaining = timeoutNanos;
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferFrom(FileChannel fileChannel, long position, long count) throws IOException {
/* 105 */       return resetTimeoutIfWriteSucceeded(this.delegate.transferFrom(fileChannel, position, count));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long transferFrom(StreamSourceChannel streamSourceChannel, long count, ByteBuffer byteBuffer) throws IOException {
/* 113 */       return resetTimeoutIfWriteSucceeded(this.delegate.transferFrom(streamSourceChannel, count, byteBuffer));
/*     */     }
/*     */ 
/*     */     
/*     */     public int write(ByteBuffer byteBuffer) throws IOException {
/* 118 */       return resetTimeoutIfWriteSucceeded(this.delegate.write(byteBuffer));
/*     */     }
/*     */ 
/*     */     
/*     */     public long write(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
/* 123 */       return resetTimeoutIfWriteSucceeded(this.delegate.write(byteBuffers, offset, length));
/*     */     }
/*     */ 
/*     */     
/*     */     public int writeFinal(ByteBuffer byteBuffer) throws IOException {
/* 128 */       return resetTimeoutIfWriteSucceeded(this.delegate.writeFinal(byteBuffer));
/*     */     }
/*     */ 
/*     */     
/*     */     public long writeFinal(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
/* 133 */       return resetTimeoutIfWriteSucceeded(this.delegate.writeFinal(byteBuffers, offset, length));
/*     */     }
/*     */ 
/*     */     
/*     */     public void terminateWrites() throws IOException {
/* 138 */       this.delegate.terminateWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWriteShutdown() {
/* 143 */       return this.delegate.isWriteShutdown();
/*     */     }
/*     */ 
/*     */     
/*     */     public void resumeWrites() {
/* 148 */       this.delegate.resumeWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public void suspendWrites() {
/* 153 */       this.delegate.suspendWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public void wakeupWrites() {
/* 158 */       this.delegate.wakeupWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWriteResumed() {
/* 163 */       return this.delegate.isWriteResumed();
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitWritable() throws IOException {
/* 168 */       awaitWritable(this.remaining, TimeUnit.NANOSECONDS);
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitWritable(long duration, TimeUnit unit) throws IOException {
/* 173 */       long startTime = System.nanoTime();
/* 174 */       long requestedNanos = unit.toNanos(duration);
/*     */       try {
/* 176 */         this.delegate.awaitWritable(Math.min(requestedNanos, this.remaining), TimeUnit.NANOSECONDS);
/*     */       } finally {
/* 178 */         this.remaining -= System.nanoTime() - startTime;
/*     */       } 
/* 180 */       if (this.remaining < 0L) {
/* 181 */         WriteTimeoutException wte = UndertowMessages.MESSAGES.blockingWriteTimedOut(this.timeoutNanos);
/* 182 */         UndertowLogger.REQUEST_IO_LOGGER.blockingWriteTimedOut(wte);
/* 183 */         IoUtils.safeClose((Closeable)this.serverConnection);
/* 184 */         throw wte;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioIoThread getWriteThread() {
/* 190 */       return this.delegate.getWriteThread();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setWriteReadyHandler(WriteReadyHandler writeReadyHandler) {
/* 195 */       this.delegate.setWriteReadyHandler(writeReadyHandler);
/*     */     }
/*     */ 
/*     */     
/*     */     public void truncateWrites() throws IOException {
/* 200 */       this.delegate.truncateWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean flush() throws IOException {
/* 205 */       return resetTimeoutIfWriteSucceeded(this.delegate.flush());
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioWorker getWorker() {
/* 210 */       return this.delegate.getWorker();
/*     */     }
/*     */     
/*     */     private long resetTimeoutIfWriteSucceeded(long value) {
/* 214 */       if (value != 0L)
/*     */       {
/* 216 */         this.remaining = this.timeoutNanos;
/*     */       }
/* 218 */       return value;
/*     */     }
/*     */     
/*     */     private int resetTimeoutIfWriteSucceeded(int value) {
/* 222 */       if (value != 0)
/*     */       {
/* 224 */         this.remaining = this.timeoutNanos;
/*     */       }
/* 226 */       return value;
/*     */     }
/*     */     
/*     */     private boolean resetTimeoutIfWriteSucceeded(boolean value) {
/* 230 */       if (value)
/*     */       {
/* 232 */         this.remaining = this.timeoutNanos;
/*     */       }
/* 234 */       return value;
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 239 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private HttpHandler nextHandler;
/*     */     private Duration writeTimeout;
/*     */     
/*     */     private Builder() {}
/*     */     
/*     */     public Builder writeTimeout(Duration writeTimeout) {
/* 250 */       this.writeTimeout = Objects.<Duration>requireNonNull(writeTimeout, "A write timeout is required");
/* 251 */       return this;
/*     */     }
/*     */     
/*     */     public Builder nextHandler(HttpHandler nextHandler) {
/* 255 */       this.nextHandler = Objects.<HttpHandler>requireNonNull(nextHandler, "HttpHandler is required");
/* 256 */       return this;
/*     */     }
/*     */     
/*     */     public HttpHandler build() {
/* 260 */       HttpHandler next = Objects.<HttpHandler>requireNonNull(this.nextHandler, "HttpHandler is required");
/* 261 */       if (this.writeTimeout == null) {
/* 262 */         throw new IllegalArgumentException("A write timeout is required");
/*     */       }
/* 264 */       if (this.writeTimeout.isZero() || this.writeTimeout.isNegative()) {
/* 265 */         throw new IllegalArgumentException("Write timeout must be positive: " + this.writeTimeout);
/*     */       }
/* 267 */       return new BlockingWriteTimeoutHandler(next, this.writeTimeout);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\BlockingWriteTimeoutHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */