/*      */ package org.xnio.ssl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*      */ import java.util.concurrent.locks.LockSupport;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLEngineResult;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLHandshakeException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.jboss.logging.Logger;
/*      */ import org.xnio.Bits;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.Pool;
/*      */ import org.xnio.Pooled;
/*      */ import org.xnio._private.Messages;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
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
/*      */ final class JsseSslConduitEngine
/*      */ {
/*   62 */   private static final Logger log = Logger.getLogger("org.xnio.conduits");
/*   63 */   private static final String FQCN = JsseSslConduitEngine.class.getName();
/*      */   
/*      */   private static final int NEED_WRAP = 1;
/*      */   
/*      */   private static final int READ_SHUT_DOWN = 2;
/*      */   
/*      */   private static final int BUFFER_UNDERFLOW = 4;
/*   70 */   private static final int READ_FLAGS = Bits.intBitMask(0, 15);
/*      */   
/*      */   private static final int NEED_UNWRAP = 65536;
/*      */   
/*      */   private static final int WRITE_SHUT_DOWN = 131072;
/*      */   
/*      */   private static final int WRITE_COMPLETE = 262144;
/*      */   
/*      */   private static final int FIRST_HANDSHAKE = 4194304;
/*      */   private static final int ENGINE_CLOSED = 8388608;
/*   80 */   private static final int WRITE_FLAGS = Bits.intBitMask(16, 31);
/*      */   
/*   82 */   private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
/*      */ 
/*      */   
/*      */   private final SSLEngine engine;
/*      */ 
/*      */   
/*      */   private final Pooled<ByteBuffer> receiveBuffer;
/*      */ 
/*      */   
/*      */   private final Pooled<ByteBuffer> sendBuffer;
/*      */ 
/*      */   
/*      */   private ByteBuffer expandedSendBuffer;
/*      */   
/*      */   private final Pooled<ByteBuffer> readBuffer;
/*      */   
/*      */   private final StreamSinkConduit sinkConduit;
/*      */   
/*      */   private final StreamSourceConduit sourceConduit;
/*      */   
/*      */   private final JsseSslStreamConnection connection;
/*      */   
/*      */   private volatile int state;
/*      */   
/*  106 */   private static final AtomicIntegerFieldUpdater<JsseSslConduitEngine> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JsseSslConduitEngine.class, "state");
/*      */   
/*      */   private volatile Thread readWaiter;
/*      */   
/*      */   private volatile Thread writeWaiter;
/*      */   
/*  112 */   private static final AtomicReferenceFieldUpdater<JsseSslConduitEngine, Thread> readWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(JsseSslConduitEngine.class, Thread.class, "readWaiter");
/*  113 */   private static final AtomicReferenceFieldUpdater<JsseSslConduitEngine, Thread> writeWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(JsseSslConduitEngine.class, Thread.class, "writeWaiter");
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
/*      */   JsseSslConduitEngine(JsseSslStreamConnection connection, StreamSinkConduit sinkConduit, StreamSourceConduit sourceConduit, SSLEngine engine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) {
/*  126 */     if (connection == null) {
/*  127 */       throw Messages.msg.nullParameter("connection");
/*      */     }
/*  129 */     if (sinkConduit == null) {
/*  130 */       throw Messages.msg.nullParameter("sinkConduit");
/*      */     }
/*  132 */     if (sourceConduit == null) {
/*  133 */       throw Messages.msg.nullParameter("sourceConduit");
/*      */     }
/*  135 */     if (engine == null) {
/*  136 */       throw Messages.msg.nullParameter("engine");
/*      */     }
/*  138 */     if (socketBufferPool == null) {
/*  139 */       throw Messages.msg.nullParameter("socketBufferPool");
/*      */     }
/*  141 */     if (applicationBufferPool == null) {
/*  142 */       throw Messages.msg.nullParameter("applicationBufferPool");
/*      */     }
/*  144 */     this.connection = connection;
/*  145 */     this.sinkConduit = sinkConduit;
/*  146 */     this.sourceConduit = sourceConduit;
/*  147 */     this.engine = engine;
/*  148 */     this.state = 4194304;
/*  149 */     SSLSession session = engine.getSession();
/*  150 */     int packetBufferSize = session.getPacketBufferSize();
/*  151 */     boolean ok = false;
/*  152 */     this.receiveBuffer = socketBufferPool.allocate();
/*      */     try {
/*  154 */       ((ByteBuffer)this.receiveBuffer.getResource()).flip();
/*  155 */       this.sendBuffer = socketBufferPool.allocate();
/*      */       try {
/*  157 */         if (((ByteBuffer)this.receiveBuffer.getResource()).capacity() < packetBufferSize || ((ByteBuffer)this.sendBuffer.getResource()).capacity() < packetBufferSize)
/*      */         {
/*  159 */           this.expandedSendBuffer = ByteBuffer.allocate(packetBufferSize);
/*      */         }
/*  161 */         this.readBuffer = applicationBufferPool.allocate();
/*  162 */         ok = true;
/*      */       } finally {
/*  164 */         if (!ok) this.sendBuffer.free(); 
/*      */       } 
/*      */     } finally {
/*  167 */       if (!ok) this.receiveBuffer.free();
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void beginHandshake() throws IOException {
/*  177 */     this.engine.beginHandshake();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SSLSession getSession() {
/*  184 */     return this.engine.getSession();
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
/*      */ 
/*      */ 
/*      */   
/*      */   public int wrap(ByteBuffer src) throws IOException {
/*  203 */     return wrap(src, false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long wrap(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  224 */     assert !Thread.holdsLock(getWrapLock());
/*  225 */     assert !Thread.holdsLock(getUnwrapLock());
/*  226 */     if (length < 1) {
/*  227 */       return 0L;
/*      */     }
/*  229 */     if (Bits.allAreSet(this.state, 262144))
/*      */     {
/*  231 */       throw new ClosedChannelException();
/*      */     }
/*  233 */     long bytesConsumed = 0L;
/*      */     try {
/*      */       boolean run;
/*      */       do {
/*      */         SSLEngineResult result;
/*  238 */         synchronized (getWrapLock()) {
/*  239 */           run = handleWrapResult(result = engineWrap(srcs, offset, length, getSendBuffer()), false);
/*  240 */           bytesConsumed += result.bytesConsumed();
/*      */         } 
/*      */         
/*  243 */         run = (run && (handleHandshake(result, true) || (!isUnwrapNeeded() && Buffers.hasRemaining((Buffer[])srcs, offset, length))));
/*  244 */       } while (run);
/*  245 */     } catch (SSLHandshakeException e) {
/*      */       SSLEngineResult result; try {
/*  247 */         synchronized (getWrapLock()) {
/*  248 */           this.engine.wrap(EMPTY_BUFFER, getSendBuffer());
/*  249 */           doFlush();
/*      */         } 
/*  251 */       } catch (IOException iOException) {}
/*  252 */       throw result;
/*      */     } 
/*  254 */     return bytesConsumed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuffer getWrappedBuffer() {
/*  265 */     assert Thread.holdsLock(getWrapLock());
/*  266 */     assert !Thread.holdsLock(getUnwrapLock());
/*  267 */     return Bits.allAreSet(stateUpdater.get(this), 8388608) ? Buffers.EMPTY_BYTE_BUFFER : getSendBuffer();
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
/*      */   public Object getWrapLock() {
/*  280 */     return this.sendBuffer;
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
/*      */   private int wrap(ByteBuffer src, boolean isCloseExpected) throws IOException {
/*  294 */     assert !Thread.holdsLock(getWrapLock());
/*  295 */     assert !Thread.holdsLock(getUnwrapLock());
/*  296 */     if (Bits.allAreSet(this.state, 262144))
/*      */     {
/*  298 */       throw new ClosedChannelException();
/*      */     }
/*  300 */     clearFlags(4194304);
/*  301 */     int bytesConsumed = 0;
/*      */     try {
/*      */       boolean run;
/*      */       do {
/*      */         SSLEngineResult result;
/*  306 */         synchronized (getWrapLock()) {
/*  307 */           run = handleWrapResult(result = engineWrap(src, getSendBuffer()), isCloseExpected);
/*  308 */           bytesConsumed += result.bytesConsumed();
/*      */         } 
/*      */         
/*  311 */         run = (run && bytesConsumed == 0 && (handleHandshake(result, true) || (!isUnwrapNeeded() && src.hasRemaining())));
/*  312 */       } while (run);
/*  313 */     } catch (SSLHandshakeException e) {
/*      */       SSLEngineResult result; try {
/*  315 */         synchronized (getWrapLock()) {
/*  316 */           this.engine.wrap(EMPTY_BUFFER, getSendBuffer());
/*  317 */           doFlush();
/*      */         } 
/*  319 */       } catch (IOException iOException) {}
/*  320 */       throw result;
/*      */     } 
/*  322 */     return bytesConsumed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult engineWrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dest) throws SSLException {
/*  329 */     assert Thread.holdsLock(getWrapLock());
/*  330 */     assert !Thread.holdsLock(getUnwrapLock());
/*  331 */     log.logf(FQCN, Logger.Level.TRACE, null, "Wrapping %s into %s", srcs, dest);
/*      */     try {
/*  333 */       return this.engine.wrap(srcs, offset, length, dest);
/*  334 */     } catch (SSLHandshakeException e) {
/*      */       try {
/*  336 */         this.engine.wrap(srcs, offset, length, dest);
/*  337 */         doFlush();
/*  338 */       } catch (IOException iOException) {}
/*  339 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult engineWrap(ByteBuffer src, ByteBuffer dest) throws SSLException {
/*  347 */     assert Thread.holdsLock(getWrapLock());
/*  348 */     assert !Thread.holdsLock(getUnwrapLock());
/*  349 */     log.logf(FQCN, Logger.Level.TRACE, null, "Wrapping %s into %s", src, dest);
/*  350 */     return this.engine.wrap(src, dest);
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
/*      */   private boolean handleWrapResult(SSLEngineResult result, boolean closeExpected) throws IOException {
/*      */     ByteBuffer buffer;
/*  363 */     assert Thread.holdsLock(getWrapLock());
/*  364 */     assert !Thread.holdsLock(getUnwrapLock());
/*  365 */     log.logf(FQCN, Logger.Level.TRACE, null, "Wrap result is %s", result);
/*  366 */     switch (result.getStatus()) {
/*      */       case FINISHED:
/*  368 */         assert result.bytesConsumed() == 0;
/*  369 */         assert result.bytesProduced() == 0;
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
/*      */ 
/*      */ 
/*      */         
/*  421 */         return true;case NOT_HANDSHAKING: assert result.bytesConsumed() == 0; assert result.bytesProduced() == 0; buffer = getSendBuffer(); if (buffer.position() == 0) { int bufferSize = this.engine.getSession().getPacketBufferSize(); if (buffer.capacity() < bufferSize) { Messages.msg.expandedSslBufferEnabled(bufferSize); this.expandedSendBuffer = ByteBuffer.allocate(bufferSize); } else { throw Messages.msg.wrongBufferExpansion(); }  } else { buffer.flip(); try { while (buffer.hasRemaining()) { int res = this.sinkConduit.write(buffer); if (res == 0) return false;  }  } finally { buffer.compact(); }  }  return true;case NEED_WRAP: if (!closeExpected) throw new ClosedChannelException(); case NEED_UNWRAP: if (result.bytesConsumed() == 0 && result.bytesProduced() > 0 && !doFlush()) return false;  return true;
/*      */     } 
/*      */     throw Messages.msg.unexpectedWrapResult(result.getStatus());
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
/*      */   private boolean handleHandshake(SSLEngineResult result, boolean write) throws IOException {
/*  436 */     assert !Thread.holdsLock(getUnwrapLock());
/*      */     
/*  438 */     if (isWrapNeeded()) {
/*  439 */       synchronized (getWrapLock()) {
/*  440 */         if (doFlush()) {
/*  441 */           clearNeedWrap();
/*      */         }
/*      */       } 
/*      */     }
/*  445 */     boolean newResult = false; while (true) {
/*      */       ByteBuffer buffer; ByteBuffer unwrappedBuffer;
/*  447 */       switch (result.getHandshakeStatus()) {
/*      */         case FINISHED:
/*  449 */           clearNeedUnwrap();
/*  450 */           this.connection.handleHandshakeFinished();
/*      */           
/*  452 */           return true;
/*      */ 
/*      */         
/*      */         case NOT_HANDSHAKING:
/*  456 */           clearNeedUnwrap();
/*  457 */           return false;
/*      */ 
/*      */         
/*      */         case NEED_WRAP:
/*  461 */           clearNeedUnwrap();
/*      */           
/*  463 */           if (write) {
/*  464 */             return true;
/*      */           }
/*  466 */           buffer = getSendBuffer();
/*      */ 
/*      */           
/*  469 */           synchronized (getWrapLock()) {
/*      */ 
/*      */             
/*  472 */             if (doFlush()) {
/*  473 */               if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
/*  474 */                 return false;
/*      */               }
/*  476 */               if (!handleWrapResult(result = engineWrap(Buffers.EMPTY_BYTE_BUFFER, buffer), true) || !doFlush()) {
/*  477 */                 needWrap();
/*  478 */                 return false;
/*      */               } 
/*      */               
/*  481 */               newResult = true;
/*  482 */               clearNeedWrap();
/*      */               continue;
/*      */             } 
/*  485 */             assert !isUnwrapNeeded();
/*      */             
/*  487 */             needWrap();
/*      */             
/*  489 */             return false;
/*      */           } 
/*      */ 
/*      */         
/*      */         case NEED_UNWRAP:
/*  494 */           if (!write) {
/*  495 */             return newResult;
/*      */           }
/*  497 */           synchronized (getWrapLock()) {
/*      */             
/*  499 */             doFlush();
/*      */           } 
/*  501 */           buffer = (ByteBuffer)this.receiveBuffer.getResource();
/*  502 */           unwrappedBuffer = (ByteBuffer)this.readBuffer.getResource();
/*      */           
/*  504 */           if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP && this.engine.isOutboundDone()) {
/*  505 */             synchronized (getUnwrapLock()) {
/*  506 */               buffer.compact();
/*  507 */               this.sourceConduit.read(buffer);
/*  508 */               buffer.flip();
/*  509 */               if (buffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
/*  510 */                 this.sourceConduit.wakeupReads();
/*      */               }
/*  512 */               return false;
/*      */             } 
/*      */           }
/*  515 */           synchronized (getUnwrapLock()) {
/*      */             
/*  517 */             int unwrapResult = handleUnwrapResult(result = engineUnwrap(buffer, unwrappedBuffer));
/*  518 */             if (buffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
/*  519 */               this.sourceConduit.wakeupReads();
/*      */             }
/*  521 */             if (unwrapResult >= 0) {
/*      */               
/*  523 */               if (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || result.bytesConsumed() > 0) {
/*  524 */                 clearNeedUnwrap();
/*      */                 continue;
/*      */               } 
/*  527 */               assert !isWrapNeeded();
/*      */               
/*  529 */               needUnwrap();
/*  530 */               return false;
/*  531 */             }  if (unwrapResult == -1 && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
/*  532 */               if (!Bits.allAreSet(this.state, 2))
/*      */               {
/*  534 */                 throw new ClosedChannelException();
/*      */               }
/*  536 */               return false;
/*      */             } 
/*      */           } 
/*      */           continue;
/*      */ 
/*      */         
/*      */         case NEED_TASK:
/*  543 */           synchronized (this.engine) {
/*      */             Runnable task;
/*  545 */             while ((task = this.engine.getDelegatedTask()) != null) {
/*      */               try {
/*  547 */                 task.run();
/*  548 */               } catch (Exception e) {
/*  549 */                 throw new IOException(e);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */           
/*  554 */           return true;
/*      */       }  break;
/*      */     } 
/*  557 */     throw Messages.msg.unexpectedHandshakeStatus(result.getHandshakeStatus());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int unwrap(ByteBuffer dst) throws IOException {
/*  578 */     return (int)unwrap(new ByteBuffer[] { dst }, 0, 1);
/*      */   }
/*      */   
/*  581 */   private int failureCount = 0;
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
/*      */   public long unwrap(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*      */     SSLEngineResult result;
/*  601 */     assert !Thread.holdsLock(getUnwrapLock());
/*  602 */     assert !Thread.holdsLock(getWrapLock());
/*  603 */     if (dsts.length == 0 || length == 0 || isClosed()) {
/*  604 */       return 0L;
/*      */     }
/*  606 */     clearFlags(4194308);
/*  607 */     ByteBuffer buffer = (ByteBuffer)this.receiveBuffer.getResource();
/*  608 */     ByteBuffer unwrappedBuffer = (ByteBuffer)this.readBuffer.getResource();
/*  609 */     long total = 0L;
/*      */     
/*  611 */     synchronized (getUnwrapLock()) {
/*  612 */       if (unwrappedBuffer.position() > 0) {
/*  613 */         total += copyUnwrappedData(dsts, offset, length, unwrappedBuffer);
/*      */       }
/*      */     } 
/*  616 */     int res = 0;
/*      */     try {
/*      */       do {
/*  619 */         synchronized (getUnwrapLock()) {
/*  620 */           if (!Buffers.hasRemaining((Buffer[])dsts, offset, length)) {
/*  621 */             if (unwrappedBuffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
/*  622 */               this.sourceConduit.wakeupReads();
/*      */             }
/*  624 */             return total;
/*      */           } 
/*  626 */           res = handleUnwrapResult(result = engineUnwrap(buffer, unwrappedBuffer));
/*  627 */           if (unwrappedBuffer.position() > 0)
/*      */           {
/*      */             
/*  630 */             total += copyUnwrappedData(dsts, offset, length, unwrappedBuffer);
/*      */           }
/*      */         } 
/*  633 */       } while (handleHandshake(result, false) || res > 0);
/*  634 */     } catch (SSLHandshakeException e) {
/*      */       try {
/*  636 */         synchronized (getWrapLock()) {
/*  637 */           this.engine.wrap(EMPTY_BUFFER, getSendBuffer());
/*  638 */           doFlush();
/*      */         } 
/*  640 */       } catch (IOException iOException) {}
/*  641 */       throw e;
/*      */     } 
/*  643 */     if (total == 0L && 
/*  644 */       res == -1) {
/*  645 */       return -1L;
/*      */     }
/*      */     
/*  648 */     if (res == 0 && result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
/*      */       int old;
/*      */       do {
/*  651 */         old = this.state;
/*  652 */       } while (!stateUpdater.compareAndSet(this, old, old | 0x4));
/*      */     } 
/*  654 */     return total;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuffer getUnwrapBuffer() {
/*  665 */     assert Thread.holdsLock(getUnwrapLock());
/*  666 */     assert !Thread.holdsLock(getWrapLock());
/*  667 */     return (ByteBuffer)this.receiveBuffer.getResource();
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
/*      */   public Object getUnwrapLock() {
/*  680 */     return this.receiveBuffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SSLEngineResult engineUnwrap(ByteBuffer buffer, ByteBuffer unwrappedBuffer) throws IOException {
/*  687 */     assert Thread.holdsLock(getUnwrapLock());
/*  688 */     if (!buffer.hasRemaining()) {
/*  689 */       buffer.compact();
/*  690 */       this.sourceConduit.read(buffer);
/*  691 */       buffer.flip();
/*      */     } 
/*  693 */     log.logf(FQCN, Logger.Level.TRACE, null, "Unwrapping %s into %s", buffer, unwrappedBuffer);
/*  694 */     return this.engine.unwrap(buffer, unwrappedBuffer);
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
/*      */   private int copyUnwrappedData(ByteBuffer[] dsts, int offset, int length, ByteBuffer unwrappedBuffer) {
/*  707 */     assert Thread.holdsLock(getUnwrapLock());
/*  708 */     unwrappedBuffer.flip();
/*      */     try {
/*  710 */       return Buffers.copy(dsts, offset, length, unwrappedBuffer);
/*      */     } finally {
/*  712 */       unwrappedBuffer.compact();
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
/*      */   private int handleUnwrapResult(SSLEngineResult result) throws IOException {
/*      */     ByteBuffer buffer;
/*  726 */     assert Thread.holdsLock(getUnwrapLock());
/*  727 */     log.logf(FQCN, Logger.Level.TRACE, null, "Unwrap result is %s", result);
/*  728 */     switch (result.getStatus()) {
/*      */       case NOT_HANDSHAKING:
/*  730 */         assert result.bytesConsumed() == 0;
/*  731 */         assert result.bytesProduced() == 0;
/*      */         
/*  733 */         return 0;
/*      */       
/*      */       case FINISHED:
/*  736 */         assert result.bytesConsumed() == 0;
/*  737 */         assert result.bytesProduced() == 0;
/*      */         
/*  739 */         buffer = (ByteBuffer)this.receiveBuffer.getResource();
/*  740 */         synchronized (getUnwrapLock()) {
/*  741 */           buffer.compact();
/*      */           try {
/*  743 */             return this.sourceConduit.read(buffer);
/*      */           } finally {
/*  745 */             buffer.flip();
/*      */           } 
/*      */         } 
/*      */ 
/*      */       
/*      */       case NEED_WRAP:
/*  751 */         if (result.bytesConsumed() > 0) {
/*  752 */           return result.bytesConsumed();
/*      */         }
/*  754 */         return -1;
/*      */ 
/*      */       
/*      */       case NEED_UNWRAP:
/*  758 */         return result.bytesConsumed();
/*      */     } 
/*      */     
/*  761 */     throw Messages.msg.unexpectedUnwrapResult(result.getStatus());
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
/*      */ 
/*      */   
/*      */   public boolean flush() throws IOException {
/*  779 */     int oldState = stateUpdater.get(this);
/*  780 */     if (Bits.allAreSet(oldState, 262144)) {
/*  781 */       return true;
/*      */     }
/*  783 */     synchronized (getWrapLock()) {
/*  784 */       if (Bits.allAreSet(oldState, 131072)) {
/*  785 */         if (!wrapCloseMessage()) {
/*  786 */           return false;
/*      */         }
/*      */       } else {
/*  789 */         return true;
/*      */       } 
/*      */     } 
/*      */     
/*  793 */     int newState = oldState | 0x40000;
/*  794 */     while (!stateUpdater.compareAndSet(this, oldState, newState)) {
/*  795 */       oldState = stateUpdater.get(this);
/*  796 */       if (Bits.allAreSet(oldState, 262144)) {
/*  797 */         return true;
/*      */       }
/*  799 */       newState = oldState | 0x40000;
/*      */     } 
/*      */     
/*  802 */     if (Bits.allAreSet(oldState, 2)) {
/*  803 */       closeEngine();
/*      */     }
/*  805 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean wrapCloseMessage() throws IOException {
/*  815 */     assert !Thread.holdsLock(getUnwrapLock());
/*  816 */     assert Thread.holdsLock(getWrapLock());
/*  817 */     if (this.sinkConduit.isWriteShutdown()) {
/*  818 */       return true;
/*      */     }
/*  820 */     if (!this.engine.isOutboundDone() || !this.engine.isInboundDone()) {
/*      */       
/*      */       do {
/*  823 */         if (!handleWrapResult(result = engineWrap(Buffers.EMPTY_BYTE_BUFFER, getSendBuffer()), true)) {
/*  824 */           return false;
/*      */         }
/*  826 */       } while (handleHandshake(result, true) && (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || !this.engine.isOutboundDone())); SSLEngineResult result;
/*  827 */       handleWrapResult(result = engineWrap(Buffers.EMPTY_BYTE_BUFFER, getSendBuffer()), true);
/*  828 */       if (!this.engine.isOutboundDone() || (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && result
/*  829 */         .getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_UNWRAP)) {
/*  830 */         return false;
/*      */       }
/*      */     } 
/*  833 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean doFlush() throws IOException {
/*  844 */     assert Thread.holdsLock(getWrapLock());
/*  845 */     assert !Thread.holdsLock(getUnwrapLock());
/*      */     
/*  847 */     ByteBuffer buffer = getSendBuffer();
/*  848 */     buffer.flip();
/*      */     try {
/*  850 */       while (buffer.hasRemaining()) {
/*  851 */         int res = this.sinkConduit.write(buffer);
/*  852 */         if (res == 0) {
/*  853 */           return false;
/*      */         }
/*      */       } 
/*      */     } finally {
/*  857 */       buffer.compact();
/*      */     } 
/*  859 */     return this.sinkConduit.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void closeEngine() throws IOException {
/*  868 */     int old = setFlags(8388608);
/*      */     
/*  870 */     if (Bits.allAreSet(old, 8388608)) {
/*      */       return;
/*      */     }
/*      */     try {
/*  874 */       synchronized (getWrapLock()) {
/*  875 */         if (!doFlush()) {
/*  876 */           throw Messages.msg.unflushedData();
/*      */         }
/*      */       } 
/*      */     } finally {
/*  880 */       this.sourceConduit.terminateReads();
/*  881 */       this.sinkConduit.terminateWrites();
/*  882 */       this.connection.readClosed();
/*  883 */       this.connection.writeClosed();
/*  884 */       this.readBuffer.free();
/*  885 */       this.receiveBuffer.free();
/*  886 */       this.sendBuffer.free();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void closeOutbound() throws IOException {
/*  896 */     if (isOutboundClosed())
/*      */       return; 
/*  898 */     int old = setFlags(131072);
/*  899 */     boolean sentCloseMessage = true;
/*      */     try {
/*  901 */       if (Bits.allAreClear(old, 131072)) {
/*  902 */         this.engine.closeOutbound();
/*  903 */         synchronized (getWrapLock()) {
/*  904 */           sentCloseMessage = (wrapCloseMessage() && flush());
/*      */         } 
/*      */       } 
/*  907 */       if (!Bits.allAreClear(old, 2) && sentCloseMessage) {
/*  908 */         closeEngine();
/*      */       }
/*  910 */     } catch (Exception e) {
/*      */       
/*      */       try {
/*  913 */         closeEngine();
/*  914 */       } catch (Exception closeEngineException) {
/*  915 */         Messages.msg.failedToCloseSSLEngine(e, closeEngineException);
/*      */       } 
/*  917 */       if (e instanceof IOException) {
/*  918 */         throw (IOException)e;
/*      */       }
/*  920 */       throw (RuntimeException)e;
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
/*      */   void close() throws IOException {
/*      */     try {
/*  933 */       closeInbound();
/*  934 */     } catch (Throwable t) {
/*      */       try {
/*  936 */         closeOutbound();
/*  937 */       } catch (Throwable t2) {
/*  938 */         t2.addSuppressed(t);
/*  939 */         throw t2;
/*      */       } 
/*  941 */       throw t;
/*      */     } 
/*  943 */     closeOutbound();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOutboundClosed() {
/*  952 */     return Bits.allAreSet(stateUpdater.get(this), 131072);
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
/*      */   public void awaitCanWrap() throws IOException {
/*  964 */     int oldState = this.state;
/*  965 */     if (Bits.anyAreSet(oldState, 131072) || !Bits.allAreSet(oldState, 65536)) {
/*      */       return;
/*      */     }
/*  968 */     Thread thread = Thread.currentThread();
/*  969 */     Thread next = writeWaiterUpdater.getAndSet(this, thread);
/*      */     try {
/*  971 */       if (Bits.anyAreSet(oldState = this.state, 131072)) {
/*      */         return;
/*      */       }
/*  974 */       if (Bits.allAreSet(oldState, 65536)) {
/*  975 */         unwrap(Buffers.EMPTY_BYTE_BUFFER);
/*      */       }
/*  977 */       LockSupport.park(this);
/*  978 */       if (thread.isInterrupted()) {
/*  979 */         throw Messages.msg.interruptedIO();
/*      */       }
/*      */     } finally {
/*      */       
/*  983 */       if (next != null) LockSupport.unpark(next);
/*      */     
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
/*      */   public void awaitCanWrap(long time, TimeUnit timeUnit) throws IOException {
/*  998 */     int oldState = this.state;
/*  999 */     if (Bits.anyAreSet(oldState, 131072) || !Bits.allAreSet(oldState, 65536)) {
/*      */       return;
/*      */     }
/* 1002 */     Thread thread = Thread.currentThread();
/* 1003 */     Thread next = writeWaiterUpdater.getAndSet(this, thread);
/* 1004 */     long duration = timeUnit.toNanos(time);
/*      */     try {
/* 1006 */       if (Bits.anyAreSet(oldState = this.state, 131072)) {
/*      */         return;
/*      */       }
/* 1009 */       if (Bits.allAreSet(oldState, 65536)) {
/* 1010 */         unwrap(Buffers.EMPTY_BYTE_BUFFER);
/*      */       }
/* 1012 */       LockSupport.parkNanos(this, duration);
/* 1013 */       if (thread.isInterrupted()) {
/* 1014 */         throw Messages.msg.interruptedIO();
/*      */       }
/*      */     } finally {
/*      */       
/* 1018 */       if (next != null) LockSupport.unpark(next);
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void closeInbound() throws IOException {
/* 1028 */     int old = setFlags(2);
/* 1029 */     boolean sentCloseMessage = true;
/*      */     try {
/* 1031 */       if (Bits.allAreSet(old, 131072) && !Bits.allAreSet(old, 262144)) {
/* 1032 */         synchronized (getWrapLock()) {
/* 1033 */           sentCloseMessage = (wrapCloseMessage() && flush());
/*      */         } 
/*      */       }
/* 1036 */       if (Bits.allAreSet(old, 262144) && sentCloseMessage) {
/* 1037 */         closeEngine();
/*      */       }
/* 1039 */     } catch (Exception e) {
/*      */       
/* 1041 */       closeEngine();
/* 1042 */       if (e instanceof IOException) {
/* 1043 */         throw (IOException)e;
/*      */       }
/* 1045 */       throw (RuntimeException)e;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInboundClosed() {
/* 1056 */     return Bits.allAreSet(this.state, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/* 1064 */     return Bits.allAreSet(this.state, 8388608);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void awaitCanUnwrap() throws IOException {
/* 1073 */     int oldState = this.state;
/* 1074 */     if (Bits.anyAreSet(oldState, 2) || !Bits.anyAreSet(oldState, 1)) {
/*      */       return;
/*      */     }
/* 1077 */     Thread thread = Thread.currentThread();
/* 1078 */     Thread next = readWaiterUpdater.getAndSet(this, thread);
/*      */     try {
/* 1080 */       if (Bits.anyAreSet(oldState = this.state, 2)) {
/*      */         return;
/*      */       }
/* 1083 */       if (Bits.allAreSet(oldState, 1)) {
/* 1084 */         wrap(Buffers.EMPTY_BYTE_BUFFER);
/*      */       }
/* 1086 */       LockSupport.park(this);
/* 1087 */       if (thread.isInterrupted()) {
/* 1088 */         throw Messages.msg.interruptedIO();
/*      */       }
/*      */     } finally {
/*      */       
/* 1092 */       if (next != null) LockSupport.unpark(next);
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void awaitCanUnwrap(long time, TimeUnit timeUnit) throws IOException {
/* 1104 */     int oldState = this.state;
/* 1105 */     if (Bits.anyAreSet(oldState, 2) || !Bits.anyAreSet(oldState, 1)) {
/*      */       return;
/*      */     }
/* 1108 */     Thread thread = Thread.currentThread();
/* 1109 */     Thread next = readWaiterUpdater.getAndSet(this, thread);
/* 1110 */     long duration = timeUnit.toNanos(time);
/*      */     try {
/* 1112 */       if (Bits.anyAreSet(oldState = this.state, 2)) {
/*      */         return;
/*      */       }
/* 1115 */       if (Bits.allAreSet(oldState, 1)) {
/* 1116 */         wrap(Buffers.EMPTY_BYTE_BUFFER);
/*      */       }
/* 1118 */       LockSupport.parkNanos(this, duration);
/* 1119 */       if (thread.isInterrupted()) {
/* 1120 */         throw Messages.msg.interruptedIO();
/*      */       }
/*      */     } finally {
/*      */       
/* 1124 */       if (next != null) LockSupport.unpark(next); 
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isFirstHandshake() {
/* 1129 */     return Bits.allAreSet(this.state, 4194304);
/*      */   }
/*      */   
/*      */   SSLEngine getEngine() {
/* 1133 */     return this.engine;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void needWrap() {
/* 1140 */     setFlags(1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isWrapNeeded() {
/* 1147 */     return Bits.allAreSet(this.state, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void clearNeedWrap() {
/* 1154 */     clearFlags(1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void needUnwrap() {
/* 1161 */     setFlags(65536);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isUnwrapNeeded() {
/* 1168 */     return Bits.allAreSet(this.state, 65536);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isUnderflow() {
/* 1175 */     return Bits.allAreSet(this.state, 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void clearNeedUnwrap() {
/* 1182 */     clearFlags(65536);
/*      */   }
/*      */ 
/*      */   
/*      */   private int setFlags(int flags) {
/*      */     while (true) {
/* 1188 */       int oldState = this.state;
/* 1189 */       if ((oldState & flags) == flags) {
/* 1190 */         return oldState;
/*      */       }
/* 1192 */       if (stateUpdater.compareAndSet(this, oldState, oldState | flags))
/* 1193 */         return oldState; 
/*      */     } 
/*      */   }
/*      */   
/*      */   private int clearFlags(int flags) {
/*      */     while (true) {
/* 1199 */       int oldState = this.state;
/* 1200 */       if ((oldState & flags) == 0) {
/* 1201 */         return oldState;
/*      */       }
/* 1203 */       if (stateUpdater.compareAndSet(this, oldState, oldState & (flags ^ 0xFFFFFFFF)))
/* 1204 */         return oldState; 
/*      */     } 
/*      */   }
/*      */   public boolean isDataAvailable() {
/* 1208 */     synchronized (getUnwrapLock()) {
/*      */       
/* 1210 */       return (((ByteBuffer)this.readBuffer.getResource()).position() > 0 || (((ByteBuffer)this.receiveBuffer.getResource()).hasRemaining() && !isUnderflow()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ByteBuffer getSendBuffer() {
/* 1218 */     return (this.expandedSendBuffer != null) ? this.expandedSendBuffer : (ByteBuffer)this.sendBuffer.getResource();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslConduitEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */