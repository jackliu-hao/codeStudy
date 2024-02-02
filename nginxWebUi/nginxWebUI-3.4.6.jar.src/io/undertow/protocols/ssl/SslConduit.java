/*      */ package io.undertow.protocols.ssl;
/*      */ 
/*      */ import io.undertow.UndertowLogger;
/*      */ import io.undertow.connector.ByteBufferPool;
/*      */ import io.undertow.connector.PooledByteBuffer;
/*      */ import io.undertow.server.DefaultByteBufferPool;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.nio.channels.WritableByteChannel;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLEngineResult;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.xnio.Bits;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.ChannelListeners;
/*      */ import org.xnio.IoUtils;
/*      */ import org.xnio.StreamConnection;
/*      */ import org.xnio.XnioIoThread;
/*      */ import org.xnio.XnioWorker;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.conduits.ConduitReadableByteChannel;
/*      */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*      */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*      */ import org.xnio.conduits.ConduitWritableByteChannel;
/*      */ import org.xnio.conduits.Conduits;
/*      */ import org.xnio.conduits.ReadReadyHandler;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
/*      */ import org.xnio.conduits.WriteReadyHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SslConduit
/*      */   implements StreamSourceConduit, StreamSinkConduit
/*      */ {
/*   72 */   public static final int MAX_READ_LISTENER_INVOCATIONS = Integer.getInteger("io.undertow.ssl.max-read-listener-invocations", 100).intValue();
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_READ_REQUIRES_WRITE = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_WRITE_REQUIRES_READ = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_READS_RESUMED = 4;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_WRITES_RESUMED = 8;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_DATA_TO_UNWRAP = 16;
/*      */ 
/*      */   
/*      */   private static final int FLAG_READ_SHUTDOWN = 32;
/*      */ 
/*      */   
/*      */   private static final int FLAG_WRITE_SHUTDOWN = 64;
/*      */ 
/*      */   
/*      */   private static final int FLAG_ENGINE_INBOUND_SHUTDOWN = 128;
/*      */ 
/*      */   
/*      */   private static final int FLAG_ENGINE_OUTBOUND_SHUTDOWN = 256;
/*      */ 
/*      */   
/*      */   private static final int FLAG_DELEGATE_SINK_SHUTDOWN = 512;
/*      */ 
/*      */   
/*      */   private static final int FLAG_DELEGATE_SOURCE_SHUTDOWN = 1024;
/*      */ 
/*      */   
/*      */   private static final int FLAG_IN_HANDSHAKE = 2048;
/*      */ 
/*      */   
/*      */   private static final int FLAG_CLOSED = 4096;
/*      */ 
/*      */   
/*      */   private static final int FLAG_WRITE_CLOSED = 8192;
/*      */ 
/*      */   
/*      */   private static final int FLAG_READ_CLOSED = 16384;
/*      */ 
/*      */   
/*  125 */   public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
/*      */   
/*      */   private static volatile ByteBufferPool expandedBufferPool;
/*      */   
/*      */   private final UndertowSslConnection connection;
/*      */   
/*      */   private final StreamConnection delegate;
/*      */   
/*      */   private final Executor delegatedTaskExecutor;
/*      */   
/*      */   private SSLEngine engine;
/*      */   
/*      */   private final StreamSinkConduit sink;
/*      */   
/*      */   private final StreamSourceConduit source;
/*      */   
/*      */   private final ByteBufferPool bufferPool;
/*      */   
/*      */   private final Runnable handshakeCallback;
/*  144 */   private volatile int state = 0;
/*      */   
/*  146 */   private volatile int outstandingTasks = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile PooledByteBuffer wrappedData;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile PooledByteBuffer dataToUnwrap;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile PooledByteBuffer unwrappedData;
/*      */ 
/*      */ 
/*      */   
/*      */   private SslWriteReadyHandler writeReadyHandler;
/*      */ 
/*      */ 
/*      */   
/*      */   private SslReadReadyHandler readReadyHandler;
/*      */ 
/*      */ 
/*      */   
/*      */   private int readListenerInvocationCount;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean invokingReadListenerHandshake = false;
/*      */ 
/*      */ 
/*      */   
/*  178 */   private final Runnable runReadListenerCommand = new Runnable()
/*      */     {
/*      */       public void run() {
/*  181 */         int count = SslConduit.this.readListenerInvocationCount;
/*      */         try {
/*  183 */           SslConduit.this.readReadyHandler.readReady();
/*      */         } finally {
/*  185 */           if (count == SslConduit.this.readListenerInvocationCount) {
/*  186 */             SslConduit.this.readListenerInvocationCount = 0;
/*      */           }
/*      */         } 
/*      */       }
/*      */     };
/*      */   
/*  192 */   private final Runnable runReadListenerAndResumeCommand = new Runnable()
/*      */     {
/*      */       public void run() {
/*  195 */         if (Bits.allAreSet(SslConduit.this.state, 4)) {
/*  196 */           SslConduit.this.delegate.getSourceChannel().resumeReads();
/*      */         }
/*  198 */         SslConduit.this.runReadListenerCommand.run();
/*      */       }
/*      */     };
/*      */   
/*      */   SslConduit(UndertowSslConnection connection, StreamConnection delegate, SSLEngine engine, Executor delegatedTaskExecutor, ByteBufferPool bufferPool, Runnable handshakeCallback) {
/*  203 */     this.connection = connection;
/*  204 */     this.delegate = delegate;
/*  205 */     this.handshakeCallback = handshakeCallback;
/*  206 */     this.sink = delegate.getSinkChannel().getConduit();
/*  207 */     this.source = delegate.getSourceChannel().getConduit();
/*  208 */     this.engine = engine;
/*  209 */     this.delegatedTaskExecutor = delegatedTaskExecutor;
/*  210 */     this.bufferPool = bufferPool;
/*  211 */     delegate.getSourceChannel().getConduit().setReadReadyHandler(this.readReadyHandler = new SslReadReadyHandler(null));
/*  212 */     delegate.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler = new SslWriteReadyHandler(null));
/*  213 */     if (engine.getUseClientMode()) {
/*  214 */       this.state = 2049;
/*      */     } else {
/*  216 */       this.state = 2050;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void terminateReads() throws IOException {
/*  222 */     this.state |= 0x20;
/*  223 */     notifyReadClosed();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadShutdown() {
/*  228 */     return Bits.anyAreSet(this.state, 32);
/*      */   }
/*      */ 
/*      */   
/*      */   public void resumeReads() {
/*  233 */     if (Bits.anyAreSet(this.state, 4)) {
/*      */       return;
/*      */     }
/*      */     
/*  237 */     resumeReads(false);
/*      */   }
/*      */   
/*      */   public void suspendReads() {
/*  241 */     this.state &= 0xFFFFFFFB;
/*  242 */     if (!Bits.allAreSet(this.state, 10)) {
/*  243 */       this.delegate.getSourceChannel().suspendReads();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void wakeupReads() {
/*  249 */     resumeReads(true);
/*      */   }
/*      */   
/*      */   private void resumeReads(boolean wakeup) {
/*  253 */     this.state |= 0x4;
/*  254 */     if (Bits.anyAreSet(this.state, 1)) {
/*  255 */       this.delegate.getSinkChannel().resumeWrites();
/*      */     }
/*  257 */     else if (Bits.anyAreSet(this.state, 16) || wakeup || this.unwrappedData != null) {
/*  258 */       runReadListener(true);
/*      */     } else {
/*  260 */       this.delegate.getSourceChannel().resumeReads();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void runReadListener(boolean resumeInListener) {
/*      */     try {
/*  268 */       if (this.readListenerInvocationCount++ == MAX_READ_LISTENER_INVOCATIONS) {
/*  269 */         UndertowLogger.REQUEST_LOGGER.sslReadLoopDetected(this);
/*  270 */         IoUtils.safeClose(new Closeable[] { (Closeable)this.connection, (Closeable)this.delegate });
/*  271 */         close();
/*      */         return;
/*      */       } 
/*  274 */       if (resumeInListener) {
/*  275 */         this.delegate.getIoThread().execute(this.runReadListenerAndResumeCommand);
/*      */       } else {
/*  277 */         this.delegate.getIoThread().execute(this.runReadListenerCommand);
/*      */       } 
/*  279 */     } catch (Throwable e) {
/*      */       
/*  281 */       IoUtils.safeClose(new Closeable[] { (Closeable)this.connection, (Closeable)this.delegate });
/*  282 */       UndertowLogger.REQUEST_IO_LOGGER.debugf(e, "Failed to queue read listener invocation", new Object[0]);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void runWriteListener() {
/*      */     try {
/*  288 */       this.delegate.getIoThread().execute(new Runnable()
/*      */           {
/*      */             public void run() {
/*  291 */               SslConduit.this.writeReadyHandler.writeReady();
/*      */             }
/*      */           });
/*  294 */     } catch (Throwable e) {
/*      */       
/*  296 */       IoUtils.safeClose(new Closeable[] { (Closeable)this.connection, (Closeable)this.delegate });
/*  297 */       UndertowLogger.REQUEST_IO_LOGGER.debugf(e, "Failed to queue read listener invocation", new Object[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadResumed() {
/*  303 */     return Bits.anyAreSet(this.state, 4);
/*      */   }
/*      */ 
/*      */   
/*      */   public void awaitReadable() throws IOException {
/*  308 */     synchronized (this) {
/*  309 */       if (this.outstandingTasks > 0) {
/*      */         try {
/*  311 */           wait();
/*      */           return;
/*  313 */         } catch (InterruptedException e) {
/*  314 */           Thread.currentThread().interrupt();
/*  315 */           throw new InterruptedIOException();
/*      */         } 
/*      */       }
/*      */     } 
/*  319 */     if (this.unwrappedData != null) {
/*      */       return;
/*      */     }
/*  322 */     if (Bits.anyAreSet(this.state, 16)) {
/*      */       return;
/*      */     }
/*  325 */     if (Bits.anyAreSet(this.state, 1)) {
/*  326 */       awaitWritable();
/*      */       return;
/*      */     } 
/*  329 */     this.source.awaitReadable();
/*      */   }
/*      */ 
/*      */   
/*      */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/*  334 */     synchronized (this) {
/*  335 */       if (this.outstandingTasks > 0) {
/*      */         try {
/*  337 */           wait(timeUnit.toMillis(time));
/*      */           return;
/*  339 */         } catch (InterruptedException e) {
/*  340 */           Thread.currentThread().interrupt();
/*  341 */           throw new InterruptedIOException();
/*      */         } 
/*      */       }
/*      */     } 
/*  345 */     if (this.unwrappedData != null) {
/*      */       return;
/*      */     }
/*  348 */     if (Bits.anyAreSet(this.state, 16)) {
/*      */       return;
/*      */     }
/*  351 */     if (Bits.anyAreSet(this.state, 1)) {
/*  352 */       awaitWritable(time, timeUnit);
/*      */       return;
/*      */     } 
/*  355 */     this.source.awaitReadable(time, timeUnit);
/*      */   }
/*      */ 
/*      */   
/*      */   public XnioIoThread getReadThread() {
/*  360 */     return this.delegate.getIoThread();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setReadReadyHandler(ReadReadyHandler handler) {
/*  365 */     this.delegate.getSourceChannel().getConduit().setReadReadyHandler(this.readReadyHandler = new SslReadReadyHandler(handler));
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  370 */     if (Bits.anyAreSet(this.state, 64)) {
/*  371 */       throw new ClosedChannelException();
/*      */     }
/*  373 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel(this));
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  378 */     if (Bits.anyAreSet(this.state, 64)) {
/*  379 */       throw new ClosedChannelException();
/*      */     }
/*  381 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel(this));
/*      */   }
/*      */ 
/*      */   
/*      */   public int write(ByteBuffer src) throws IOException {
/*  386 */     if (Bits.anyAreSet(this.state, 64)) {
/*  387 */       throw new ClosedChannelException();
/*      */     }
/*  389 */     return (int)doWrap(new ByteBuffer[] { src }, 0, 1);
/*      */   }
/*      */ 
/*      */   
/*      */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  394 */     if (Bits.anyAreSet(this.state, 64)) {
/*  395 */       throw new ClosedChannelException();
/*      */     }
/*  397 */     return doWrap(srcs, offs, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeFinal(ByteBuffer src) throws IOException {
/*  402 */     if (Bits.anyAreSet(this.state, 64)) {
/*  403 */       throw new ClosedChannelException();
/*      */     }
/*  405 */     return Conduits.writeFinalBasic(this, src);
/*      */   }
/*      */ 
/*      */   
/*      */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  410 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public void terminateWrites() throws IOException {
/*  415 */     this.state |= 0x40;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWriteShutdown() {
/*  420 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void resumeWrites() {
/*  425 */     this.state |= 0x8;
/*  426 */     if (Bits.anyAreSet(this.state, 2)) {
/*  427 */       this.delegate.getSourceChannel().resumeReads();
/*      */     } else {
/*  429 */       this.delegate.getSinkChannel().resumeWrites();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void suspendWrites() {
/*  435 */     this.state &= 0xFFFFFFF7;
/*  436 */     if (!Bits.allAreSet(this.state, 5)) {
/*  437 */       this.delegate.getSinkChannel().suspendWrites();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void wakeupWrites() {
/*  443 */     this.state |= 0x8;
/*  444 */     getWriteThread().execute(new Runnable()
/*      */         {
/*      */           public void run() {
/*  447 */             SslConduit.this.resumeWrites();
/*  448 */             SslConduit.this.writeReadyHandler.writeReady();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWriteResumed() {
/*  455 */     return Bits.anyAreSet(this.state, 8);
/*      */   }
/*      */ 
/*      */   
/*      */   public void awaitWritable() throws IOException {
/*  460 */     if (Bits.anyAreSet(this.state, 64)) {
/*      */       return;
/*      */     }
/*  463 */     if (this.outstandingTasks > 0) {
/*  464 */       synchronized (this) {
/*  465 */         if (this.outstandingTasks > 0) {
/*      */           try {
/*  467 */             wait();
/*      */             return;
/*  469 */           } catch (InterruptedException e) {
/*  470 */             Thread.currentThread().interrupt();
/*  471 */             throw new InterruptedIOException();
/*      */           } 
/*      */         }
/*      */       } 
/*      */     }
/*  476 */     if (Bits.anyAreSet(this.state, 2)) {
/*  477 */       awaitReadable();
/*      */       return;
/*      */     } 
/*  480 */     this.sink.awaitWritable();
/*      */   }
/*      */ 
/*      */   
/*      */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/*  485 */     if (Bits.anyAreSet(this.state, 64)) {
/*      */       return;
/*      */     }
/*  488 */     if (this.outstandingTasks > 0) {
/*  489 */       synchronized (this) {
/*  490 */         if (this.outstandingTasks > 0) {
/*      */           try {
/*  492 */             wait(timeUnit.toMillis(time));
/*      */             return;
/*  494 */           } catch (InterruptedException e) {
/*  495 */             Thread.currentThread().interrupt();
/*  496 */             throw new InterruptedIOException();
/*      */           } 
/*      */         }
/*      */       } 
/*      */     }
/*  501 */     if (Bits.anyAreSet(this.state, 2)) {
/*  502 */       awaitReadable(time, timeUnit);
/*      */       return;
/*      */     } 
/*  505 */     this.sink.awaitWritable();
/*      */   }
/*      */ 
/*      */   
/*      */   public XnioIoThread getWriteThread() {
/*  510 */     return this.delegate.getIoThread();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/*  515 */     this.delegate.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler = new SslWriteReadyHandler(handler));
/*      */   }
/*      */ 
/*      */   
/*      */   public void truncateWrites() throws IOException {
/*      */     try {
/*  521 */       notifyWriteClosed();
/*      */     } finally {
/*  523 */       this.delegate.getSinkChannel().close();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean flush() throws IOException {
/*  529 */     if (Bits.anyAreSet(this.state, 512)) {
/*  530 */       return this.sink.flush();
/*      */     }
/*  532 */     if (this.wrappedData != null) {
/*  533 */       doWrap(null, 0, 0);
/*  534 */       if (this.wrappedData != null) {
/*  535 */         return false;
/*      */       }
/*      */     } 
/*  538 */     if (Bits.allAreSet(this.state, 64)) {
/*  539 */       if (Bits.allAreClear(this.state, 256)) {
/*  540 */         this.state |= 0x100;
/*  541 */         this.engine.closeOutbound();
/*  542 */         doWrap(null, 0, 0);
/*  543 */         if (this.wrappedData != null) {
/*  544 */           return false;
/*      */         }
/*  546 */       } else if (this.wrappedData != null && Bits.allAreClear(this.state, 512)) {
/*  547 */         doWrap(null, 0, 0);
/*  548 */         if (this.wrappedData != null) {
/*  549 */           return false;
/*      */         }
/*      */       } 
/*  552 */       if (Bits.allAreClear(this.state, 512)) {
/*  553 */         this.sink.terminateWrites();
/*  554 */         this.state |= 0x200;
/*  555 */         notifyWriteClosed();
/*      */       } 
/*  557 */       boolean result = this.sink.flush();
/*  558 */       if (result && Bits.anyAreSet(this.state, 16384)) {
/*  559 */         closed();
/*      */       }
/*  561 */       return result;
/*      */     } 
/*  563 */     return this.sink.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  568 */     if (Bits.anyAreSet(this.state, 32)) {
/*  569 */       return -1L;
/*      */     }
/*  571 */     return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel(this), position, count);
/*      */   }
/*      */ 
/*      */   
/*      */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  576 */     if (Bits.anyAreSet(this.state, 32)) {
/*  577 */       return -1L;
/*      */     }
/*  579 */     return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel(this), count, throughBuffer, (WritableByteChannel)target);
/*      */   }
/*      */ 
/*      */   
/*      */   public int read(ByteBuffer dst) throws IOException {
/*  584 */     if (Bits.anyAreSet(this.state, 32)) {
/*  585 */       return -1;
/*      */     }
/*  587 */     return (int)doUnwrap(new ByteBuffer[] { dst }, 0, 1);
/*      */   }
/*      */ 
/*      */   
/*      */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  592 */     if (Bits.anyAreSet(this.state, 32)) {
/*  593 */       return -1L;
/*      */     }
/*  595 */     return doUnwrap(dsts, offs, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public XnioWorker getWorker() {
/*  600 */     return this.delegate.getWorker();
/*      */   }
/*      */   
/*      */   private Executor getDelegatedTaskExecutor() {
/*  604 */     return (this.delegatedTaskExecutor == null) ? (Executor)getWorker() : this.delegatedTaskExecutor;
/*      */   }
/*      */   
/*      */   void notifyWriteClosed() {
/*  608 */     if (Bits.anyAreSet(this.state, 8192)) {
/*      */       return;
/*      */     }
/*  611 */     boolean runListener = (isWriteResumed() && Bits.anyAreSet(this.state, 4096));
/*  612 */     this.connection.writeClosed();
/*  613 */     this.engine.closeOutbound();
/*  614 */     this.state |= 0x2100;
/*  615 */     if (Bits.anyAreSet(this.state, 16384)) {
/*  616 */       closed();
/*      */     }
/*  618 */     if (Bits.anyAreSet(this.state, 1)) {
/*  619 */       notifyReadClosed();
/*      */     }
/*  621 */     this.state &= 0xFFFFFFFD;
/*      */     
/*  623 */     if (runListener) {
/*  624 */       runWriteListener();
/*      */     }
/*      */   }
/*      */   
/*      */   void notifyReadClosed() {
/*  629 */     if (Bits.anyAreSet(this.state, 16384)) {
/*      */       return;
/*      */     }
/*  632 */     boolean runListener = (isReadResumed() && Bits.anyAreSet(this.state, 4096));
/*  633 */     this.connection.readClosed();
/*      */     
/*      */     try {
/*  636 */       this.engine.closeInbound();
/*  637 */     } catch (SSLException e) {
/*  638 */       UndertowLogger.REQUEST_IO_LOGGER.trace("Exception closing read side of SSL channel", e);
/*  639 */       if (Bits.allAreClear(this.state, 8192) && isWriteResumed()) {
/*  640 */         runWriteListener();
/*      */       }
/*      */     } 
/*      */     
/*  644 */     this.state |= 0x40A0;
/*  645 */     if (Bits.anyAreSet(this.state, 8192)) {
/*  646 */       closed();
/*      */     }
/*  648 */     if (Bits.anyAreSet(this.state, 2)) {
/*  649 */       notifyWriteClosed();
/*      */     }
/*  651 */     if (runListener) {
/*  652 */       runReadListener(false);
/*      */     }
/*      */   }
/*      */   
/*      */   public void startHandshake() throws SSLException {
/*  657 */     this.state |= 0x1;
/*  658 */     this.engine.beginHandshake();
/*      */   }
/*      */   
/*      */   public SSLSession getSslSession() {
/*  662 */     return this.engine.getSession();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void doHandshake() throws IOException {
/*  672 */     doUnwrap(null, 0, 0);
/*  673 */     doWrap(null, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized long doUnwrap(ByteBuffer[] userBuffers, int off, int len) throws IOException {
/*  688 */     if (Bits.anyAreSet(this.state, 4096)) {
/*  689 */       throw new ClosedChannelException();
/*      */     }
/*  691 */     if (this.outstandingTasks > 0) {
/*  692 */       return 0L;
/*      */     }
/*  694 */     if (Bits.anyAreSet(this.state, 1)) {
/*  695 */       doWrap(null, 0, 0);
/*  696 */       if (Bits.allAreClear(this.state, 2)) {
/*  697 */         return 0L;
/*      */       }
/*      */     } 
/*  700 */     boolean bytesProduced = false;
/*  701 */     PooledByteBuffer unwrappedData = this.unwrappedData;
/*      */     
/*  703 */     if (unwrappedData != null && 
/*  704 */       userBuffers != null) {
/*  705 */       long copied = Buffers.copy(userBuffers, off, len, unwrappedData.getBuffer());
/*  706 */       if (!unwrappedData.getBuffer().hasRemaining()) {
/*  707 */         unwrappedData.close();
/*  708 */         this.unwrappedData = null;
/*      */       } 
/*  710 */       if (copied > 0L) {
/*  711 */         this.readListenerInvocationCount = 0;
/*      */       }
/*  713 */       return copied;
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*      */       SSLEngineResult result;
/*      */ 
/*      */       
/*  721 */       if (Bits.allAreClear(this.state, 16)) {
/*  722 */         int i; if (this.dataToUnwrap == null) {
/*  723 */           this.dataToUnwrap = this.bufferPool.allocate();
/*      */         }
/*      */         
/*      */         try {
/*  727 */           i = this.source.read(this.dataToUnwrap.getBuffer());
/*  728 */         } catch (IOException|RuntimeException|Error e) {
/*  729 */           this.dataToUnwrap.close();
/*  730 */           this.dataToUnwrap = null;
/*  731 */           throw e;
/*      */         } 
/*  733 */         this.dataToUnwrap.getBuffer().flip();
/*  734 */         if (i == -1) {
/*  735 */           this.dataToUnwrap.close();
/*  736 */           this.dataToUnwrap = null;
/*  737 */           notifyReadClosed();
/*  738 */           return -1L;
/*  739 */         }  if (i == 0 && this.engine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
/*      */ 
/*      */           
/*  742 */           if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
/*  743 */             this.dataToUnwrap.close();
/*  744 */             this.dataToUnwrap = null;
/*      */           } 
/*  746 */           return 0L;
/*      */         } 
/*      */       } 
/*  749 */       int dataToUnwrapLength = this.dataToUnwrap.getBuffer().remaining();
/*      */       
/*  751 */       long l1 = 0L;
/*  752 */       if (userBuffers != null) {
/*  753 */         l1 = Buffers.remaining((Buffer[])userBuffers);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  759 */       boolean unwrapBufferUsed = false;
/*      */       try {
/*  761 */         if (userBuffers != null) {
/*  762 */           result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), userBuffers, off, len);
/*  763 */           if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/*      */ 
/*      */             
/*  766 */             unwrappedData = this.bufferPool.allocate();
/*  767 */             ByteBuffer[] d = new ByteBuffer[len + 1];
/*  768 */             System.arraycopy(userBuffers, off, d, 0, len);
/*  769 */             d[len] = unwrappedData.getBuffer();
/*  770 */             result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), d);
/*  771 */             unwrapBufferUsed = true;
/*      */           } 
/*  773 */           bytesProduced = (result.bytesProduced() > 0);
/*      */         } else {
/*  775 */           unwrapBufferUsed = true;
/*  776 */           if (unwrappedData == null) {
/*  777 */             unwrappedData = this.bufferPool.allocate();
/*      */           } else {
/*  779 */             unwrappedData.getBuffer().compact();
/*      */           } 
/*  781 */           result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), unwrappedData.getBuffer());
/*  782 */           bytesProduced = (result.bytesProduced() > 0);
/*      */         } 
/*      */       } finally {
/*  785 */         if (unwrapBufferUsed) {
/*  786 */           unwrappedData.getBuffer().flip();
/*  787 */           if (!unwrappedData.getBuffer().hasRemaining()) {
/*  788 */             unwrappedData.close();
/*  789 */             unwrappedData = null;
/*      */           } 
/*      */         } 
/*  792 */         this.unwrappedData = unwrappedData;
/*      */       } 
/*      */       
/*  795 */       if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
/*  796 */         if (this.dataToUnwrap != null) {
/*  797 */           this.dataToUnwrap.close();
/*  798 */           this.dataToUnwrap = null;
/*      */         } 
/*  800 */         notifyReadClosed();
/*  801 */         return -1L;
/*      */       } 
/*  803 */       if (!handleHandshakeResult(result)) {
/*  804 */         if (this.dataToUnwrap.getBuffer().hasRemaining() && result
/*  805 */           .getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW && this.dataToUnwrap
/*  806 */           .getBuffer().remaining() != dataToUnwrapLength) {
/*  807 */           this.state |= 0x10;
/*      */         } else {
/*  809 */           this.state &= 0xFFFFFFEF;
/*      */         } 
/*  811 */         return 0L;
/*      */       } 
/*  813 */       if (result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
/*  814 */         this.state &= 0xFFFFFFEF;
/*  815 */       } else if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/*  816 */         UndertowLogger.REQUEST_LOGGER.sslBufferOverflow(this);
/*  817 */         IoUtils.safeClose((Closeable)this.delegate);
/*  818 */       } else if (this.dataToUnwrap.getBuffer().hasRemaining() && this.dataToUnwrap.getBuffer().remaining() != dataToUnwrapLength) {
/*  819 */         this.state |= 0x10;
/*      */       } else {
/*  821 */         this.state &= 0xFFFFFFEF;
/*      */       } 
/*  823 */       if (userBuffers == null) {
/*  824 */         return 0L;
/*      */       }
/*  826 */       long res = l1 - Buffers.remaining((Buffer[])userBuffers);
/*  827 */       if (res > 0L)
/*      */       {
/*  829 */         this.readListenerInvocationCount = 0;
/*      */       }
/*  831 */       return res;
/*      */     }
/*  833 */     catch (SSLException e) {
/*      */ 
/*      */       
/*      */       try {
/*      */         try {
/*  838 */           clearWriteRequiresRead();
/*  839 */           doWrap(null, 0, 0);
/*  840 */           flush();
/*  841 */         } catch (Exception e2) {
/*  842 */           UndertowLogger.REQUEST_LOGGER.debug("Failed to write out final SSL record", e2);
/*      */         } 
/*  844 */         close();
/*  845 */       } catch (Throwable ex) {
/*      */         
/*  847 */         UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doUnwrap", ex);
/*      */       } 
/*  849 */       throw e;
/*  850 */     } catch (RuntimeException|IOException|Error e) {
/*      */       try {
/*  852 */         close();
/*  853 */       } catch (Throwable ex) {
/*      */         
/*  855 */         UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doUnwrap", ex);
/*      */       } 
/*  857 */       throw e;
/*      */     } finally {
/*  859 */       boolean requiresListenerInvocation = false;
/*      */       
/*  861 */       if (bytesProduced || (unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining())) {
/*  862 */         requiresListenerInvocation = true;
/*      */       }
/*  864 */       if (this.dataToUnwrap != null)
/*      */       {
/*  866 */         if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
/*  867 */           this.dataToUnwrap.close();
/*  868 */           this.dataToUnwrap = null;
/*  869 */           this.state &= 0xFFFFFFEF;
/*  870 */         } else if (Bits.allAreClear(this.state, 16)) {
/*      */           
/*  872 */           this.dataToUnwrap.getBuffer().compact();
/*      */         } else {
/*      */           
/*  875 */           requiresListenerInvocation = true;
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/*  880 */       if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
/*  881 */         runReadListener(false);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized long doWrap(ByteBuffer[] userBuffers, int off, int len) throws IOException {
/*  899 */     if (Bits.anyAreSet(this.state, 4096)) {
/*  900 */       throw new ClosedChannelException();
/*      */     }
/*  902 */     if (this.outstandingTasks > 0) {
/*  903 */       return 0L;
/*      */     }
/*  905 */     if (Bits.anyAreSet(this.state, 2)) {
/*  906 */       doUnwrap(null, 0, 0);
/*  907 */       if (Bits.allAreClear(this.state, 1)) {
/*  908 */         return 0L;
/*      */       }
/*      */     } 
/*  911 */     if (this.wrappedData != null) {
/*  912 */       int res = this.sink.write(this.wrappedData.getBuffer());
/*  913 */       if (res == 0 || this.wrappedData.getBuffer().hasRemaining()) {
/*  914 */         return 0L;
/*      */       }
/*  916 */       this.wrappedData.getBuffer().clear();
/*      */     } else {
/*  918 */       this.wrappedData = this.bufferPool.allocate();
/*      */     } 
/*      */     try {
/*  921 */       SSLEngineResult result = wrapAndFlip(userBuffers, off, len);
/*      */       
/*  923 */       if (result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW)
/*  924 */         throw new IOException("underflow"); 
/*  925 */       if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW)
/*      */       {
/*  927 */         if (!this.wrappedData.getBuffer().hasRemaining()) {
/*  928 */           if (this.wrappedData.getBuffer().capacity() < this.engine.getSession().getPacketBufferSize()) {
/*  929 */             this.wrappedData.close();
/*  930 */             int bufferSize = this.engine.getSession().getPacketBufferSize();
/*  931 */             UndertowLogger.REQUEST_IO_LOGGER.tracev("Expanded buffer enabled due to overflow with empty buffer, buffer size is %s", 
/*  932 */                 Integer.valueOf(bufferSize));
/*  933 */             if (expandedBufferPool == null || expandedBufferPool.getBufferSize() < bufferSize)
/*  934 */               expandedBufferPool = (ByteBufferPool)new DefaultByteBufferPool(false, bufferSize, -1, 12); 
/*  935 */             this.wrappedData = expandedBufferPool.allocate();
/*  936 */             result = wrapAndFlip(userBuffers, off, len);
/*  937 */             if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW && 
/*  938 */               !this.wrappedData.getBuffer().hasRemaining())
/*  939 */               throw new IOException("overflow"); 
/*      */           } else {
/*  941 */             throw new IOException("overflow");
/*      */           } 
/*      */         }
/*      */       }
/*      */       
/*  946 */       if (this.wrappedData.getBuffer().hasRemaining()) {
/*  947 */         this.sink.write(this.wrappedData.getBuffer());
/*      */       }
/*      */       
/*  950 */       if (this.wrappedData.getBuffer().hasRemaining()) {
/*  951 */         return result.bytesConsumed();
/*      */       }
/*      */       
/*  954 */       if (!handleHandshakeResult(result)) {
/*  955 */         return 0L;
/*      */       }
/*  957 */       if (result.getStatus() == SSLEngineResult.Status.CLOSED && userBuffers != null) {
/*  958 */         notifyWriteClosed();
/*  959 */         throw new ClosedChannelException();
/*      */       } 
/*      */       
/*  962 */       return result.bytesConsumed();
/*  963 */     } catch (RuntimeException|IOException|Error e) {
/*      */       try {
/*  965 */         close();
/*  966 */       } catch (Throwable ex) {
/*  967 */         UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doWrap()", ex);
/*      */       } 
/*  969 */       throw e;
/*      */     } finally {
/*      */       
/*  972 */       if (this.wrappedData != null && 
/*  973 */         !this.wrappedData.getBuffer().hasRemaining()) {
/*  974 */         this.wrappedData.close();
/*  975 */         this.wrappedData = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private SSLEngineResult wrapAndFlip(ByteBuffer[] userBuffers, int off, int len) throws IOException {
/*  982 */     SSLEngineResult result = null;
/*  983 */     while (result == null || (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP && result.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW)) {
/*  984 */       if (userBuffers == null) {
/*  985 */         result = this.engine.wrap(EMPTY_BUFFER, this.wrappedData.getBuffer()); continue;
/*      */       } 
/*  987 */       result = this.engine.wrap(userBuffers, off, len, this.wrappedData.getBuffer());
/*      */     } 
/*      */     
/*  990 */     this.wrappedData.getBuffer().flip();
/*  991 */     return result;
/*      */   }
/*      */   
/*      */   private boolean handleHandshakeResult(SSLEngineResult result) throws IOException {
/*  995 */     switch (result.getHandshakeStatus()) {
/*      */       case NEED_TASK:
/*  997 */         this.state |= 0x800;
/*  998 */         clearReadRequiresWrite();
/*  999 */         clearWriteRequiresRead();
/* 1000 */         runTasks();
/* 1001 */         return false;
/*      */       
/*      */       case NEED_UNWRAP:
/* 1004 */         clearReadRequiresWrite();
/* 1005 */         this.state |= 0x802;
/* 1006 */         this.sink.suspendWrites();
/* 1007 */         if (Bits.anyAreSet(this.state, 8)) {
/* 1008 */           this.source.resumeReads();
/*      */         }
/*      */         
/* 1011 */         return false;
/*      */       
/*      */       case NEED_WRAP:
/* 1014 */         clearWriteRequiresRead();
/* 1015 */         this.state |= 0x801;
/* 1016 */         this.source.suspendReads();
/* 1017 */         if (Bits.anyAreSet(this.state, 4)) {
/* 1018 */           this.sink.resumeWrites();
/*      */         }
/* 1020 */         return false;
/*      */       
/*      */       case FINISHED:
/* 1023 */         if (Bits.anyAreSet(this.state, 2048)) {
/* 1024 */           this.state &= 0xFFFFF7FF;
/* 1025 */           this.handshakeCallback.run();
/*      */         } 
/*      */         break;
/*      */     } 
/* 1029 */     clearReadRequiresWrite();
/* 1030 */     clearWriteRequiresRead();
/* 1031 */     return true;
/*      */   }
/*      */   
/*      */   private void clearReadRequiresWrite() {
/* 1035 */     if (Bits.anyAreSet(this.state, 1)) {
/* 1036 */       this.state &= 0xFFFFFFFE;
/* 1037 */       if (Bits.anyAreSet(this.state, 4)) {
/* 1038 */         resumeReads(false);
/*      */       }
/* 1040 */       if (Bits.allAreClear(this.state, 8)) {
/* 1041 */         this.sink.suspendWrites();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void clearWriteRequiresRead() {
/* 1047 */     if (Bits.anyAreSet(this.state, 2)) {
/* 1048 */       this.state &= 0xFFFFFFFD;
/* 1049 */       if (Bits.anyAreSet(this.state, 8)) {
/* 1050 */         wakeupWrites();
/*      */       }
/* 1052 */       if (Bits.allAreClear(this.state, 4)) {
/* 1053 */         this.source.suspendReads();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void closed() {
/* 1059 */     if (Bits.anyAreSet(this.state, 4096)) {
/*      */       return;
/*      */     }
/* 1062 */     synchronized (this) {
/* 1063 */       this.state |= 0x1660;
/* 1064 */       notifyReadClosed();
/* 1065 */       notifyWriteClosed();
/* 1066 */       if (this.dataToUnwrap != null) {
/* 1067 */         this.dataToUnwrap.close();
/* 1068 */         this.dataToUnwrap = null;
/*      */       } 
/* 1070 */       if (this.unwrappedData != null) {
/* 1071 */         this.unwrappedData.close();
/* 1072 */         this.unwrappedData = null;
/*      */       } 
/* 1074 */       if (this.wrappedData != null) {
/* 1075 */         this.wrappedData.close();
/* 1076 */         this.wrappedData = null;
/*      */       } 
/* 1078 */       if (Bits.allAreClear(this.state, 256)) {
/* 1079 */         this.engine.closeOutbound();
/*      */       }
/* 1081 */       if (Bits.allAreClear(this.state, 128)) {
/*      */         try {
/* 1083 */           this.engine.closeInbound();
/* 1084 */         } catch (SSLException e) {
/* 1085 */           UndertowLogger.REQUEST_LOGGER.ioException(e);
/* 1086 */         } catch (Throwable t) {
/* 1087 */           UndertowLogger.REQUEST_LOGGER.handleUnexpectedFailure(t);
/*      */         } 
/*      */       }
/*      */     } 
/* 1091 */     IoUtils.safeClose((Closeable)this.delegate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void runTasks() throws IOException {
/* 1101 */     this.delegate.getSinkChannel().suspendWrites();
/* 1102 */     this.delegate.getSourceChannel().suspendReads();
/* 1103 */     List<Runnable> tasks = new ArrayList<>();
/* 1104 */     Runnable t = this.engine.getDelegatedTask();
/* 1105 */     while (t != null) {
/* 1106 */       tasks.add(t);
/* 1107 */       t = this.engine.getDelegatedTask();
/*      */     } 
/*      */     
/* 1110 */     synchronized (this) {
/* 1111 */       this.outstandingTasks += tasks.size();
/* 1112 */       for (Runnable task : tasks) {
/* 1113 */         Runnable wrappedTask = new Runnable()
/*      */           {
/*      */             public void run() {
/*      */               try {
/* 1117 */                 task.run();
/*      */               } finally {
/* 1119 */                 synchronized (SslConduit.this) {
/* 1120 */                   if (SslConduit.this.outstandingTasks == 1) {
/* 1121 */                     SslConduit.this.getWriteThread().execute(new Runnable()
/*      */                         {
/*      */                           public void run() {
/* 1124 */                             synchronized (SslConduit.this) {
/* 1125 */                               SslConduit.this.notifyAll();
/*      */                               
/* 1127 */                               --SslConduit.this.outstandingTasks;
/*      */                               try {
/* 1129 */                                 SslConduit.this.doHandshake();
/* 1130 */                               } catch (IOException|RuntimeException|Error e) {
/* 1131 */                                 UndertowLogger.REQUEST_LOGGER.debug("Closing SSLConduit after exception on handshake", e);
/* 1132 */                                 IoUtils.safeClose((Closeable)SslConduit.this.connection);
/*      */                               } 
/* 1134 */                               if (Bits.anyAreSet(SslConduit.this.state, 4)) {
/* 1135 */                                 SslConduit.this.wakeupReads();
/*      */                               }
/* 1137 */                               if (Bits.anyAreSet(SslConduit.this.state, 8)) {
/* 1138 */                                 SslConduit.this.resumeWrites();
/*      */                               }
/*      */                             } 
/*      */                           }
/*      */                         });
/*      */                   } else {
/* 1144 */                     SslConduit.this.outstandingTasks--;
/*      */                   } 
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           };
/*      */         try {
/* 1151 */           getDelegatedTaskExecutor().execute(wrappedTask);
/* 1152 */         } catch (RejectedExecutionException e) {
/* 1153 */           UndertowLogger.REQUEST_IO_LOGGER.sslEngineDelegatedTaskRejected(e);
/* 1154 */           IoUtils.safeClose((Closeable)this.connection);
/* 1155 */           throw DelegatedTaskRejectedClosedChannelException.INSTANCE;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class DelegatedTaskRejectedClosedChannelException
/*      */     extends ClosedChannelException
/*      */   {
/* 1167 */     private static final DelegatedTaskRejectedClosedChannelException INSTANCE = new DelegatedTaskRejectedClosedChannelException();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Throwable fillInStackTrace() {
/* 1173 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Throwable initCause(Throwable ignored) {
/* 1179 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void setStackTrace(StackTraceElement[] ignored) {}
/*      */   }
/*      */ 
/*      */   
/*      */   public SSLEngine getSSLEngine() {
/* 1189 */     return this.engine;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {
/* 1196 */     closed();
/*      */   }
/*      */ 
/*      */   
/*      */   private class SslReadReadyHandler
/*      */     implements ReadReadyHandler
/*      */   {
/*      */     private final ReadReadyHandler delegateHandler;
/*      */ 
/*      */     
/*      */     private SslReadReadyHandler(ReadReadyHandler delegateHandler) {
/* 1207 */       this.delegateHandler = delegateHandler;
/*      */     }
/*      */ 
/*      */     
/*      */     public void readReady() {
/* 1212 */       if (Bits.anyAreSet(SslConduit.this.state, 2) && Bits.anyAreSet(SslConduit.this.state, 12) && !Bits.anyAreSet(SslConduit.this.state, 128)) {
/*      */         try {
/* 1214 */           SslConduit.this.invokingReadListenerHandshake = true;
/* 1215 */           SslConduit.this.doHandshake();
/* 1216 */         } catch (IOException e) {
/* 1217 */           UndertowLogger.REQUEST_LOGGER.ioException(e);
/* 1218 */           IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/* 1219 */         } catch (Throwable t) {
/* 1220 */           UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 1221 */           IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/*      */         } finally {
/* 1223 */           SslConduit.this.invokingReadListenerHandshake = false;
/*      */         } 
/*      */         
/* 1226 */         if (!Bits.anyAreSet(SslConduit.this.state, 4) && !Bits.allAreSet(SslConduit.this.state, 10)) {
/* 1227 */           SslConduit.this.delegate.getSourceChannel().suspendReads();
/*      */         }
/*      */       } 
/*      */       
/* 1231 */       boolean noProgress = false;
/* 1232 */       int initialDataToUnwrap = -1;
/* 1233 */       int initialUnwrapped = -1;
/* 1234 */       if (Bits.anyAreSet(SslConduit.this.state, 4)) {
/* 1235 */         if (this.delegateHandler == null) {
/* 1236 */           ChannelListener<? super ConduitStreamSourceChannel> readListener = SslConduit.this.connection.getSourceChannel().getReadListener();
/* 1237 */           if (readListener == null) {
/* 1238 */             SslConduit.this.suspendReads();
/*      */           } else {
/* 1240 */             if (Bits.anyAreSet(SslConduit.this.state, 16)) {
/* 1241 */               initialDataToUnwrap = SslConduit.this.dataToUnwrap.getBuffer().remaining();
/*      */             }
/* 1243 */             if (SslConduit.this.unwrappedData != null) {
/* 1244 */               initialUnwrapped = SslConduit.this.unwrappedData.getBuffer().remaining();
/*      */             }
/* 1246 */             ChannelListeners.invokeChannelListener((Channel)SslConduit.this.connection.getSourceChannel(), readListener);
/* 1247 */             if (Bits.anyAreSet(SslConduit.this.state, 16) && initialDataToUnwrap == SslConduit.this.dataToUnwrap.getBuffer().remaining()) {
/* 1248 */               noProgress = true;
/* 1249 */             } else if (SslConduit.this.unwrappedData != null && SslConduit.this.unwrappedData.getBuffer().remaining() == initialUnwrapped) {
/* 1250 */               noProgress = true;
/*      */             } 
/*      */           } 
/*      */         } else {
/* 1254 */           this.delegateHandler.readReady();
/*      */         } 
/*      */       }
/* 1257 */       if (Bits.anyAreSet(SslConduit.this.state, 4) && (SslConduit.this.unwrappedData != null || Bits.anyAreSet(SslConduit.this.state, 16))) {
/* 1258 */         if (Bits.anyAreSet(SslConduit.this.state, 16384)) {
/* 1259 */           if (SslConduit.this.unwrappedData != null) {
/* 1260 */             SslConduit.this.unwrappedData.close();
/*      */           }
/* 1262 */           if (SslConduit.this.dataToUnwrap != null) {
/* 1263 */             SslConduit.this.dataToUnwrap.close();
/*      */           }
/* 1265 */           SslConduit.this.unwrappedData = null;
/* 1266 */           SslConduit.this.dataToUnwrap = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/* 1273 */         else if ((!Bits.anyAreSet(SslConduit.this.state, 1) || SslConduit.this.wrappedData == null) && SslConduit.this.outstandingTasks == 0 && !noProgress) {
/* 1274 */           SslConduit.this.runReadListener(false);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void forceTermination() {
/*      */       try {
/* 1283 */         if (this.delegateHandler != null) {
/* 1284 */           this.delegateHandler.forceTermination();
/*      */         }
/*      */       } finally {
/* 1287 */         IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void terminated() {
/* 1293 */       ChannelListeners.invokeChannelListener((Channel)SslConduit.this.connection.getSourceChannel(), SslConduit.this.connection.getSourceChannel().getCloseListener());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class SslWriteReadyHandler
/*      */     implements WriteReadyHandler
/*      */   {
/*      */     private final WriteReadyHandler delegateHandler;
/*      */ 
/*      */     
/*      */     private SslWriteReadyHandler(WriteReadyHandler delegateHandler) {
/* 1306 */       this.delegateHandler = delegateHandler;
/*      */     }
/*      */ 
/*      */     
/*      */     public void forceTermination() {
/*      */       try {
/* 1312 */         if (this.delegateHandler != null) {
/* 1313 */           this.delegateHandler.forceTermination();
/*      */         }
/*      */       } finally {
/* 1316 */         IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void terminated() {
/* 1322 */       ChannelListeners.invokeChannelListener((Channel)SslConduit.this.connection.getSinkChannel(), SslConduit.this.connection.getSinkChannel().getCloseListener());
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeReady() {
/* 1327 */       if (Bits.anyAreSet(SslConduit.this.state, 1)) {
/* 1328 */         if (Bits.anyAreSet(SslConduit.this.state, 4)) {
/* 1329 */           SslConduit.this.readReadyHandler.readReady();
/*      */         } else {
/*      */           try {
/* 1332 */             SslConduit.this.doHandshake();
/* 1333 */           } catch (IOException e) {
/* 1334 */             UndertowLogger.REQUEST_LOGGER.ioException(e);
/* 1335 */             IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/* 1336 */           } catch (Throwable t) {
/* 1337 */             UndertowLogger.REQUEST_LOGGER.handleUnexpectedFailure(t);
/* 1338 */             IoUtils.safeClose((Closeable)SslConduit.this.delegate);
/*      */           } 
/*      */         } 
/*      */       }
/* 1342 */       if (Bits.anyAreSet(SslConduit.this.state, 8)) {
/* 1343 */         if (this.delegateHandler == null) {
/* 1344 */           ChannelListener<? super ConduitStreamSinkChannel> writeListener = SslConduit.this.connection.getSinkChannel().getWriteListener();
/* 1345 */           if (writeListener == null) {
/* 1346 */             SslConduit.this.suspendWrites();
/*      */           } else {
/* 1348 */             ChannelListeners.invokeChannelListener((Channel)SslConduit.this.connection.getSinkChannel(), writeListener);
/*      */           } 
/*      */         } else {
/* 1351 */           this.delegateHandler.writeReady();
/*      */         } 
/*      */       }
/* 1354 */       if (!Bits.anyAreSet(SslConduit.this.state, 9)) {
/* 1355 */         SslConduit.this.delegate.getSinkChannel().suspendWrites();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSslEngine(SSLEngine engine) {
/* 1361 */     this.engine = engine;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1366 */     return "SslConduit{state=" + this.state + ", outstandingTasks=" + this.outstandingTasks + ", wrappedData=" + this.wrappedData + ", dataToUnwrap=" + this.dataToUnwrap + ", unwrappedData=" + this.unwrappedData + '}';
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SslConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */