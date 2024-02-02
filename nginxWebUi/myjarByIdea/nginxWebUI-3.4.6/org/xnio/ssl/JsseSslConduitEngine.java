package org.xnio.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.Pool;
import org.xnio.Pooled;
import org.xnio._private.Messages;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

final class JsseSslConduitEngine {
   private static final Logger log = Logger.getLogger("org.xnio.conduits");
   private static final String FQCN = JsseSslConduitEngine.class.getName();
   private static final int NEED_WRAP = 1;
   private static final int READ_SHUT_DOWN = 2;
   private static final int BUFFER_UNDERFLOW = 4;
   private static final int READ_FLAGS = Bits.intBitMask(0, 15);
   private static final int NEED_UNWRAP = 65536;
   private static final int WRITE_SHUT_DOWN = 131072;
   private static final int WRITE_COMPLETE = 262144;
   private static final int FIRST_HANDSHAKE = 4194304;
   private static final int ENGINE_CLOSED = 8388608;
   private static final int WRITE_FLAGS = Bits.intBitMask(16, 31);
   private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
   private final SSLEngine engine;
   private final Pooled<ByteBuffer> receiveBuffer;
   private final Pooled<ByteBuffer> sendBuffer;
   private ByteBuffer expandedSendBuffer;
   private final Pooled<ByteBuffer> readBuffer;
   private final StreamSinkConduit sinkConduit;
   private final StreamSourceConduit sourceConduit;
   private final JsseSslStreamConnection connection;
   private volatile int state;
   private static final AtomicIntegerFieldUpdater<JsseSslConduitEngine> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JsseSslConduitEngine.class, "state");
   private volatile Thread readWaiter;
   private volatile Thread writeWaiter;
   private static final AtomicReferenceFieldUpdater<JsseSslConduitEngine, Thread> readWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(JsseSslConduitEngine.class, Thread.class, "readWaiter");
   private static final AtomicReferenceFieldUpdater<JsseSslConduitEngine, Thread> writeWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(JsseSslConduitEngine.class, Thread.class, "writeWaiter");
   private int failureCount = 0;

   JsseSslConduitEngine(JsseSslStreamConnection connection, StreamSinkConduit sinkConduit, StreamSourceConduit sourceConduit, SSLEngine engine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) {
      if (connection == null) {
         throw Messages.msg.nullParameter("connection");
      } else if (sinkConduit == null) {
         throw Messages.msg.nullParameter("sinkConduit");
      } else if (sourceConduit == null) {
         throw Messages.msg.nullParameter("sourceConduit");
      } else if (engine == null) {
         throw Messages.msg.nullParameter("engine");
      } else if (socketBufferPool == null) {
         throw Messages.msg.nullParameter("socketBufferPool");
      } else if (applicationBufferPool == null) {
         throw Messages.msg.nullParameter("applicationBufferPool");
      } else {
         this.connection = connection;
         this.sinkConduit = sinkConduit;
         this.sourceConduit = sourceConduit;
         this.engine = engine;
         this.state = 4194304;
         SSLSession session = engine.getSession();
         int packetBufferSize = session.getPacketBufferSize();
         boolean ok = false;
         this.receiveBuffer = socketBufferPool.allocate();

         try {
            ((ByteBuffer)this.receiveBuffer.getResource()).flip();
            this.sendBuffer = socketBufferPool.allocate();

            try {
               if (((ByteBuffer)this.receiveBuffer.getResource()).capacity() < packetBufferSize || ((ByteBuffer)this.sendBuffer.getResource()).capacity() < packetBufferSize) {
                  this.expandedSendBuffer = ByteBuffer.allocate(packetBufferSize);
               }

               this.readBuffer = applicationBufferPool.allocate();
               ok = true;
            } finally {
               if (!ok) {
                  this.sendBuffer.free();
               }

            }
         } finally {
            if (!ok) {
               this.receiveBuffer.free();
            }

         }

      }
   }

   public void beginHandshake() throws IOException {
      this.engine.beginHandshake();
   }

   public SSLSession getSession() {
      return this.engine.getSession();
   }

   public int wrap(ByteBuffer src) throws IOException {
      return this.wrap(src, false);
   }

   public long wrap(ByteBuffer[] srcs, int offset, int length) throws IOException {
      assert !Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      if (length < 1) {
         return 0L;
      } else if (Bits.allAreSet(this.state, 262144)) {
         throw new ClosedChannelException();
      } else {
         long bytesConsumed = 0L;

         try {
            boolean run;
            do {
               SSLEngineResult result;
               synchronized(this.getWrapLock()) {
                  run = this.handleWrapResult(result = this.engineWrap(srcs, offset, length, this.getSendBuffer()), false);
                  bytesConsumed += (long)result.bytesConsumed();
               }

               run = run && (this.handleHandshake(result, true) || !this.isUnwrapNeeded() && Buffers.hasRemaining(srcs, offset, length));
            } while(run);

            return bytesConsumed;
         } catch (SSLHandshakeException var14) {
            try {
               synchronized(this.getWrapLock()) {
                  this.engine.wrap(EMPTY_BUFFER, this.getSendBuffer());
                  this.doFlush();
               }
            } catch (IOException var12) {
            }

            throw var14;
         }
      }
   }

   public ByteBuffer getWrappedBuffer() {
      assert Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      return Bits.allAreSet(stateUpdater.get(this), 8388608) ? Buffers.EMPTY_BYTE_BUFFER : this.getSendBuffer();
   }

   public Object getWrapLock() {
      return this.sendBuffer;
   }

   private int wrap(ByteBuffer src, boolean isCloseExpected) throws IOException {
      assert !Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      if (Bits.allAreSet(this.state, 262144)) {
         throw new ClosedChannelException();
      } else {
         this.clearFlags(4194304);
         int bytesConsumed = 0;

         try {
            boolean run;
            do {
               SSLEngineResult result;
               synchronized(this.getWrapLock()) {
                  run = this.handleWrapResult(result = this.engineWrap(src, this.getSendBuffer()), isCloseExpected);
                  bytesConsumed += result.bytesConsumed();
               }

               run = run && bytesConsumed == 0 && (this.handleHandshake(result, true) || !this.isUnwrapNeeded() && src.hasRemaining());
            } while(run);

            return bytesConsumed;
         } catch (SSLHandshakeException var12) {
            try {
               synchronized(this.getWrapLock()) {
                  this.engine.wrap(EMPTY_BUFFER, this.getSendBuffer());
                  this.doFlush();
               }
            } catch (IOException var10) {
            }

            throw var12;
         }
      }
   }

   private SSLEngineResult engineWrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dest) throws SSLException {
      assert Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)"Wrapping %s into %s", srcs, dest);

      try {
         return this.engine.wrap(srcs, offset, length, dest);
      } catch (SSLHandshakeException var8) {
         try {
            this.engine.wrap(srcs, offset, length, dest);
            this.doFlush();
         } catch (IOException var7) {
         }

         throw var8;
      }
   }

   private SSLEngineResult engineWrap(ByteBuffer src, ByteBuffer dest) throws SSLException {
      assert Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)"Wrapping %s into %s", src, dest);
      return this.engine.wrap(src, dest);
   }

   private boolean handleWrapResult(SSLEngineResult result, boolean closeExpected) throws IOException {
      assert Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)"Wrap result is %s", (Object)result);
      switch (result.getStatus()) {
         case BUFFER_UNDERFLOW:
            assert result.bytesConsumed() == 0;

            assert result.bytesProduced() == 0;
            break;
         case BUFFER_OVERFLOW:
            assert result.bytesConsumed() == 0;

            assert result.bytesProduced() == 0;

            ByteBuffer buffer = this.getSendBuffer();
            int res;
            if (buffer.position() == 0) {
               res = this.engine.getSession().getPacketBufferSize();
               if (buffer.capacity() >= res) {
                  throw Messages.msg.wrongBufferExpansion();
               }

               Messages.msg.expandedSslBufferEnabled(res);
               this.expandedSendBuffer = ByteBuffer.allocate(res);
               break;
            } else {
               buffer.flip();

               boolean var5;
               try {
                  do {
                     if (!buffer.hasRemaining()) {
                        return true;
                     }

                     res = this.sinkConduit.write(buffer);
                  } while(res != 0);

                  var5 = false;
               } finally {
                  buffer.compact();
               }

               return var5;
            }
         case CLOSED:
            if (!closeExpected) {
               throw new ClosedChannelException();
            }
         case OK:
            if (result.bytesConsumed() == 0 && result.bytesProduced() > 0 && !this.doFlush()) {
               return false;
            }
            break;
         default:
            throw Messages.msg.unexpectedWrapResult(result.getStatus());
      }

      return true;
   }

   private boolean handleHandshake(SSLEngineResult result, boolean write) throws IOException {
      assert !Thread.holdsLock(this.getUnwrapLock());

      if (this.isWrapNeeded()) {
         synchronized(this.getWrapLock()) {
            if (this.doFlush()) {
               this.clearNeedWrap();
            }
         }
      }

      boolean newResult = false;

      while(true) {
         ByteBuffer buffer;
         switch (result.getHandshakeStatus()) {
            case FINISHED:
               this.clearNeedUnwrap();
               this.connection.handleHandshakeFinished();
               return true;
            case NOT_HANDSHAKING:
               this.clearNeedUnwrap();
               return false;
            case NEED_WRAP:
               this.clearNeedUnwrap();
               if (write) {
                  return true;
               }

               buffer = this.getSendBuffer();
               synchronized(this.getWrapLock()) {
                  if (this.doFlush()) {
                     if (result.getStatus() == Status.CLOSED) {
                        return false;
                     }

                     if (this.handleWrapResult(result = this.engineWrap(Buffers.EMPTY_BYTE_BUFFER, buffer), true) && this.doFlush()) {
                        newResult = true;
                        this.clearNeedWrap();
                        break;
                     }

                     this.needWrap();
                     return false;
                  }

                  assert !this.isUnwrapNeeded();

                  this.needWrap();
                  return false;
               }
            case NEED_UNWRAP:
               if (!write) {
                  return newResult;
               }

               synchronized(this.getWrapLock()) {
                  this.doFlush();
               }

               buffer = (ByteBuffer)this.receiveBuffer.getResource();
               ByteBuffer unwrappedBuffer = (ByteBuffer)this.readBuffer.getResource();
               if (result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP && this.engine.isOutboundDone()) {
                  synchronized(this.getUnwrapLock()) {
                     buffer.compact();
                     this.sourceConduit.read(buffer);
                     buffer.flip();
                     if (buffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
                        this.sourceConduit.wakeupReads();
                     }

                     return false;
                  }
               }

               synchronized(this.getUnwrapLock()) {
                  int unwrapResult = this.handleUnwrapResult(result = this.engineUnwrap(buffer, unwrappedBuffer));
                  if (buffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
                     this.sourceConduit.wakeupReads();
                  }

                  if (unwrapResult >= 0) {
                     if (result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP && result.bytesConsumed() <= 0) {
                        assert !this.isWrapNeeded();

                        this.needUnwrap();
                        return false;
                     }

                     this.clearNeedUnwrap();
                  } else if (unwrapResult == -1 && result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                     if (!Bits.allAreSet(this.state, 2)) {
                        throw new ClosedChannelException();
                     }

                     return false;
                  }
                  break;
               }
            case NEED_TASK:
               synchronized(this.engine) {
                  Runnable task;
                  while((task = this.engine.getDelegatedTask()) != null) {
                     try {
                        task.run();
                     } catch (Exception var11) {
                        throw new IOException(var11);
                     }
                  }

                  return true;
               }
            default:
               throw Messages.msg.unexpectedHandshakeStatus(result.getHandshakeStatus());
         }
      }
   }

   public int unwrap(ByteBuffer dst) throws IOException {
      return (int)this.unwrap(new ByteBuffer[]{dst}, 0, 1);
   }

   public long unwrap(ByteBuffer[] dsts, int offset, int length) throws IOException {
      assert !Thread.holdsLock(this.getUnwrapLock());

      assert !Thread.holdsLock(this.getWrapLock());

      if (dsts.length != 0 && length != 0 && !this.isClosed()) {
         this.clearFlags(4194308);
         ByteBuffer buffer = (ByteBuffer)this.receiveBuffer.getResource();
         ByteBuffer unwrappedBuffer = (ByteBuffer)this.readBuffer.getResource();
         long total = 0L;
         synchronized(this.getUnwrapLock()) {
            if (unwrappedBuffer.position() > 0) {
               total += (long)this.copyUnwrappedData(dsts, offset, length, unwrappedBuffer);
            }
         }

         int res = false;

         int res;
         SSLEngineResult result;
         try {
            do {
               do {
                  synchronized(this.getUnwrapLock()) {
                     if (!Buffers.hasRemaining(dsts, offset, length)) {
                        if (unwrappedBuffer.hasRemaining() && this.sourceConduit.isReadResumed()) {
                           this.sourceConduit.wakeupReads();
                        }

                        return total;
                     }

                     res = this.handleUnwrapResult(result = this.engineUnwrap(buffer, unwrappedBuffer));
                     if (unwrappedBuffer.position() > 0) {
                        total += (long)this.copyUnwrappedData(dsts, offset, length, unwrappedBuffer);
                     }
                  }
               } while(this.handleHandshake(result, false));
            } while(res > 0);
         } catch (SSLHandshakeException var16) {
            try {
               synchronized(this.getWrapLock()) {
                  this.engine.wrap(EMPTY_BUFFER, this.getSendBuffer());
                  this.doFlush();
               }
            } catch (IOException var14) {
            }

            throw var16;
         }

         if (total == 0L && res == -1) {
            return -1L;
         } else {
            int old;
            if (res == 0 && result.getStatus() == Status.BUFFER_UNDERFLOW) {
               do {
                  old = this.state;
               } while(!stateUpdater.compareAndSet(this, old, old | 4));
            }

            return total;
         }
      } else {
         return 0L;
      }
   }

   public ByteBuffer getUnwrapBuffer() {
      assert Thread.holdsLock(this.getUnwrapLock());

      assert !Thread.holdsLock(this.getWrapLock());

      return (ByteBuffer)this.receiveBuffer.getResource();
   }

   public Object getUnwrapLock() {
      return this.receiveBuffer;
   }

   private SSLEngineResult engineUnwrap(ByteBuffer buffer, ByteBuffer unwrappedBuffer) throws IOException {
      assert Thread.holdsLock(this.getUnwrapLock());

      if (!buffer.hasRemaining()) {
         buffer.compact();
         this.sourceConduit.read(buffer);
         buffer.flip();
      }

      log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)"Unwrapping %s into %s", buffer, unwrappedBuffer);
      return this.engine.unwrap(buffer, unwrappedBuffer);
   }

   private int copyUnwrappedData(ByteBuffer[] dsts, int offset, int length, ByteBuffer unwrappedBuffer) {
      assert Thread.holdsLock(this.getUnwrapLock());

      unwrappedBuffer.flip();

      int var5;
      try {
         var5 = Buffers.copy(dsts, offset, length, unwrappedBuffer);
      } finally {
         unwrappedBuffer.compact();
      }

      return var5;
   }

   private int handleUnwrapResult(SSLEngineResult result) throws IOException {
      assert Thread.holdsLock(this.getUnwrapLock());

      log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)"Unwrap result is %s", (Object)result);
      switch (result.getStatus()) {
         case BUFFER_UNDERFLOW:
            assert result.bytesConsumed() == 0;

            assert result.bytesProduced() == 0;

            ByteBuffer buffer = (ByteBuffer)this.receiveBuffer.getResource();
            synchronized(this.getUnwrapLock()) {
               buffer.compact();

               int var4;
               try {
                  var4 = this.sourceConduit.read(buffer);
               } finally {
                  buffer.flip();
               }

               return var4;
            }
         case BUFFER_OVERFLOW:
            assert result.bytesConsumed() == 0;

            assert result.bytesProduced() == 0;

            return 0;
         case CLOSED:
            if (result.bytesConsumed() > 0) {
               return result.bytesConsumed();
            }

            return -1;
         case OK:
            return result.bytesConsumed();
         default:
            throw Messages.msg.unexpectedUnwrapResult(result.getStatus());
      }
   }

   public boolean flush() throws IOException {
      int oldState = stateUpdater.get(this);
      if (Bits.allAreSet(oldState, 262144)) {
         return true;
      } else {
         synchronized(this.getWrapLock()) {
            if (!Bits.allAreSet(oldState, 131072)) {
               return true;
            }

            if (!this.wrapCloseMessage()) {
               return false;
            }
         }

         for(int newState = oldState | 262144; !stateUpdater.compareAndSet(this, oldState, newState); newState = oldState | 262144) {
            oldState = stateUpdater.get(this);
            if (Bits.allAreSet(oldState, 262144)) {
               return true;
            }
         }

         if (Bits.allAreSet(oldState, 2)) {
            this.closeEngine();
         }

         return true;
      }
   }

   private boolean wrapCloseMessage() throws IOException {
      assert !Thread.holdsLock(this.getUnwrapLock());

      assert Thread.holdsLock(this.getWrapLock());

      if (this.sinkConduit.isWriteShutdown()) {
         return true;
      } else {
         if (!this.engine.isOutboundDone() || !this.engine.isInboundDone()) {
            while(true) {
               SSLEngineResult result;
               if (!this.handleWrapResult(result = this.engineWrap(Buffers.EMPTY_BYTE_BUFFER, this.getSendBuffer()), true)) {
                  return false;
               }

               if (!this.handleHandshake(result, true) || result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP && this.engine.isOutboundDone()) {
                  this.handleWrapResult(result = this.engineWrap(Buffers.EMPTY_BYTE_BUFFER, this.getSendBuffer()), true);
                  if (!this.engine.isOutboundDone() || result.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING && result.getHandshakeStatus() != HandshakeStatus.NEED_UNWRAP) {
                     return false;
                  }
                  break;
               }
            }
         }

         return true;
      }
   }

   private boolean doFlush() throws IOException {
      assert Thread.holdsLock(this.getWrapLock());

      assert !Thread.holdsLock(this.getUnwrapLock());

      ByteBuffer buffer = this.getSendBuffer();
      buffer.flip();

      while(true) {
         boolean var3;
         try {
            if (!buffer.hasRemaining()) {
               return this.sinkConduit.flush();
            }

            int res = this.sinkConduit.write(buffer);
            if (res != 0) {
               continue;
            }

            var3 = false;
         } finally {
            buffer.compact();
         }

         return var3;
      }
   }

   private void closeEngine() throws IOException {
      int old = this.setFlags(8388608);
      if (!Bits.allAreSet(old, 8388608)) {
         try {
            synchronized(this.getWrapLock()) {
               if (!this.doFlush()) {
                  throw Messages.msg.unflushedData();
               }
            }
         } finally {
            this.sourceConduit.terminateReads();
            this.sinkConduit.terminateWrites();
            this.connection.readClosed();
            this.connection.writeClosed();
            this.readBuffer.free();
            this.receiveBuffer.free();
            this.sendBuffer.free();
         }

      }
   }

   public void closeOutbound() throws IOException {
      if (!this.isOutboundClosed()) {
         int old = this.setFlags(131072);
         boolean sentCloseMessage = true;

         try {
            if (Bits.allAreClear(old, 131072)) {
               this.engine.closeOutbound();
               synchronized(this.getWrapLock()) {
                  sentCloseMessage = this.wrapCloseMessage() && this.flush();
               }
            }

            if (!Bits.allAreClear(old, 2) && sentCloseMessage) {
               this.closeEngine();
            }

         } catch (Exception var7) {
            try {
               this.closeEngine();
            } catch (Exception var5) {
               Messages.msg.failedToCloseSSLEngine(var7, var5);
            }

            if (var7 instanceof IOException) {
               throw (IOException)var7;
            } else {
               throw (RuntimeException)var7;
            }
         }
      }
   }

   void close() throws IOException {
      try {
         this.closeInbound();
      } catch (Throwable var4) {
         try {
            this.closeOutbound();
         } catch (Throwable var3) {
            var3.addSuppressed(var4);
            throw var3;
         }

         throw var4;
      }

      this.closeOutbound();
   }

   public boolean isOutboundClosed() {
      return Bits.allAreSet(stateUpdater.get(this), 131072);
   }

   public void awaitCanWrap() throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 131072) && Bits.allAreSet(oldState, 65536)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)writeWaiterUpdater.getAndSet(this, thread);

         try {
            if (Bits.anyAreSet(oldState = this.state, 131072)) {
               return;
            }

            if (Bits.allAreSet(oldState, 65536)) {
               this.unwrap(Buffers.EMPTY_BYTE_BUFFER);
            }

            LockSupport.park(this);
            if (thread.isInterrupted()) {
               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public void awaitCanWrap(long time, TimeUnit timeUnit) throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 131072) && Bits.allAreSet(oldState, 65536)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)writeWaiterUpdater.getAndSet(this, thread);
         long duration = timeUnit.toNanos(time);

         try {
            if (Bits.anyAreSet(oldState = this.state, 131072)) {
               return;
            }

            if (Bits.allAreSet(oldState, 65536)) {
               this.unwrap(Buffers.EMPTY_BYTE_BUFFER);
            }

            LockSupport.parkNanos(this, duration);
            if (thread.isInterrupted()) {
               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public void closeInbound() throws IOException {
      int old = this.setFlags(2);
      boolean sentCloseMessage = true;

      try {
         if (Bits.allAreSet(old, 131072) && !Bits.allAreSet(old, 262144)) {
            synchronized(this.getWrapLock()) {
               sentCloseMessage = this.wrapCloseMessage() && this.flush();
            }
         }

         if (Bits.allAreSet(old, 262144) && sentCloseMessage) {
            this.closeEngine();
         }

      } catch (Exception var6) {
         this.closeEngine();
         if (var6 instanceof IOException) {
            throw (IOException)var6;
         } else {
            throw (RuntimeException)var6;
         }
      }
   }

   public boolean isInboundClosed() {
      return Bits.allAreSet(this.state, 2);
   }

   public boolean isClosed() {
      return Bits.allAreSet(this.state, 8388608);
   }

   public void awaitCanUnwrap() throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 2) && Bits.anyAreSet(oldState, 1)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)readWaiterUpdater.getAndSet(this, thread);

         try {
            if (Bits.anyAreSet(oldState = this.state, 2)) {
               return;
            }

            if (Bits.allAreSet(oldState, 1)) {
               this.wrap(Buffers.EMPTY_BYTE_BUFFER);
            }

            LockSupport.park(this);
            if (thread.isInterrupted()) {
               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public void awaitCanUnwrap(long time, TimeUnit timeUnit) throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 2) && Bits.anyAreSet(oldState, 1)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)readWaiterUpdater.getAndSet(this, thread);
         long duration = timeUnit.toNanos(time);

         try {
            if (!Bits.anyAreSet(oldState = this.state, 2)) {
               if (Bits.allAreSet(oldState, 1)) {
                  this.wrap(Buffers.EMPTY_BYTE_BUFFER);
               }

               LockSupport.parkNanos(this, duration);
               if (!thread.isInterrupted()) {
                  return;
               }

               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public boolean isFirstHandshake() {
      return Bits.allAreSet(this.state, 4194304);
   }

   SSLEngine getEngine() {
      return this.engine;
   }

   private void needWrap() {
      this.setFlags(1);
   }

   private boolean isWrapNeeded() {
      return Bits.allAreSet(this.state, 1);
   }

   private void clearNeedWrap() {
      this.clearFlags(1);
   }

   private void needUnwrap() {
      this.setFlags(65536);
   }

   private boolean isUnwrapNeeded() {
      return Bits.allAreSet(this.state, 65536);
   }

   private boolean isUnderflow() {
      return Bits.allAreSet(this.state, 4);
   }

   private void clearNeedUnwrap() {
      this.clearFlags(65536);
   }

   private int setFlags(int flags) {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & flags) == flags) {
            return oldState;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState | flags));

      return oldState;
   }

   private int clearFlags(int flags) {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & flags) == 0) {
            return oldState;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState & ~flags));

      return oldState;
   }

   public boolean isDataAvailable() {
      synchronized(this.getUnwrapLock()) {
         boolean var10000;
         try {
            var10000 = ((ByteBuffer)this.readBuffer.getResource()).position() > 0 || ((ByteBuffer)this.receiveBuffer.getResource()).hasRemaining() && !this.isUnderflow();
         } catch (IllegalStateException var4) {
            return false;
         }

         return var10000;
      }
   }

   private final ByteBuffer getSendBuffer() {
      return this.expandedSendBuffer != null ? this.expandedSendBuffer : (ByteBuffer)this.sendBuffer.getResource();
   }
}
