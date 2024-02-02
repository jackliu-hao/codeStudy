package io.undertow.protocols.ssl;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.DefaultByteBufferPool;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

public class SslConduit implements StreamSourceConduit, StreamSinkConduit {
   public static final int MAX_READ_LISTENER_INVOCATIONS = Integer.getInteger("io.undertow.ssl.max-read-listener-invocations", 100);
   private static final int FLAG_READ_REQUIRES_WRITE = 1;
   private static final int FLAG_WRITE_REQUIRES_READ = 2;
   private static final int FLAG_READS_RESUMED = 4;
   private static final int FLAG_WRITES_RESUMED = 8;
   private static final int FLAG_DATA_TO_UNWRAP = 16;
   private static final int FLAG_READ_SHUTDOWN = 32;
   private static final int FLAG_WRITE_SHUTDOWN = 64;
   private static final int FLAG_ENGINE_INBOUND_SHUTDOWN = 128;
   private static final int FLAG_ENGINE_OUTBOUND_SHUTDOWN = 256;
   private static final int FLAG_DELEGATE_SINK_SHUTDOWN = 512;
   private static final int FLAG_DELEGATE_SOURCE_SHUTDOWN = 1024;
   private static final int FLAG_IN_HANDSHAKE = 2048;
   private static final int FLAG_CLOSED = 4096;
   private static final int FLAG_WRITE_CLOSED = 8192;
   private static final int FLAG_READ_CLOSED = 16384;
   public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
   private static volatile ByteBufferPool expandedBufferPool;
   private final UndertowSslConnection connection;
   private final StreamConnection delegate;
   private final Executor delegatedTaskExecutor;
   private SSLEngine engine;
   private final StreamSinkConduit sink;
   private final StreamSourceConduit source;
   private final ByteBufferPool bufferPool;
   private final Runnable handshakeCallback;
   private volatile int state = 0;
   private volatile int outstandingTasks = 0;
   private volatile PooledByteBuffer wrappedData;
   private volatile PooledByteBuffer dataToUnwrap;
   private volatile PooledByteBuffer unwrappedData;
   private SslWriteReadyHandler writeReadyHandler;
   private SslReadReadyHandler readReadyHandler;
   private int readListenerInvocationCount;
   private boolean invokingReadListenerHandshake = false;
   private final Runnable runReadListenerCommand = new Runnable() {
      public void run() {
         int count = SslConduit.this.readListenerInvocationCount;

         try {
            SslConduit.this.readReadyHandler.readReady();
         } finally {
            if (count == SslConduit.this.readListenerInvocationCount) {
               SslConduit.this.readListenerInvocationCount = 0;
            }

         }

      }
   };
   private final Runnable runReadListenerAndResumeCommand = new Runnable() {
      public void run() {
         if (Bits.allAreSet(SslConduit.this.state, 4)) {
            SslConduit.this.delegate.getSourceChannel().resumeReads();
         }

         SslConduit.this.runReadListenerCommand.run();
      }
   };

   SslConduit(UndertowSslConnection connection, StreamConnection delegate, SSLEngine engine, Executor delegatedTaskExecutor, ByteBufferPool bufferPool, Runnable handshakeCallback) {
      this.connection = connection;
      this.delegate = delegate;
      this.handshakeCallback = handshakeCallback;
      this.sink = delegate.getSinkChannel().getConduit();
      this.source = delegate.getSourceChannel().getConduit();
      this.engine = engine;
      this.delegatedTaskExecutor = delegatedTaskExecutor;
      this.bufferPool = bufferPool;
      delegate.getSourceChannel().getConduit().setReadReadyHandler(this.readReadyHandler = new SslReadReadyHandler((ReadReadyHandler)null));
      delegate.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler = new SslWriteReadyHandler((WriteReadyHandler)null));
      if (engine.getUseClientMode()) {
         this.state = 2049;
      } else {
         this.state = 2050;
      }

   }

   public void terminateReads() throws IOException {
      this.state |= 32;
      this.notifyReadClosed();
   }

   public boolean isReadShutdown() {
      return Bits.anyAreSet(this.state, 32);
   }

   public void resumeReads() {
      if (!Bits.anyAreSet(this.state, 4)) {
         this.resumeReads(false);
      }
   }

   public void suspendReads() {
      this.state &= -5;
      if (!Bits.allAreSet(this.state, 10)) {
         this.delegate.getSourceChannel().suspendReads();
      }

   }

   public void wakeupReads() {
      this.resumeReads(true);
   }

   private void resumeReads(boolean wakeup) {
      this.state |= 4;
      if (Bits.anyAreSet(this.state, 1)) {
         this.delegate.getSinkChannel().resumeWrites();
      } else if (!Bits.anyAreSet(this.state, 16) && !wakeup && this.unwrappedData == null) {
         this.delegate.getSourceChannel().resumeReads();
      } else {
         this.runReadListener(true);
      }

   }

   private void runReadListener(boolean resumeInListener) {
      try {
         if (this.readListenerInvocationCount++ == MAX_READ_LISTENER_INVOCATIONS) {
            UndertowLogger.REQUEST_LOGGER.sslReadLoopDetected(this);
            IoUtils.safeClose(this.connection, this.delegate);
            this.close();
            return;
         }

         if (resumeInListener) {
            this.delegate.getIoThread().execute(this.runReadListenerAndResumeCommand);
         } else {
            this.delegate.getIoThread().execute(this.runReadListenerCommand);
         }
      } catch (Throwable var3) {
         IoUtils.safeClose(this.connection, this.delegate);
         UndertowLogger.REQUEST_IO_LOGGER.debugf(var3, "Failed to queue read listener invocation", new Object[0]);
      }

   }

   private void runWriteListener() {
      try {
         this.delegate.getIoThread().execute(new Runnable() {
            public void run() {
               SslConduit.this.writeReadyHandler.writeReady();
            }
         });
      } catch (Throwable var2) {
         IoUtils.safeClose(this.connection, this.delegate);
         UndertowLogger.REQUEST_IO_LOGGER.debugf(var2, "Failed to queue read listener invocation", new Object[0]);
      }

   }

   public boolean isReadResumed() {
      return Bits.anyAreSet(this.state, 4);
   }

   public void awaitReadable() throws IOException {
      synchronized(this) {
         if (this.outstandingTasks > 0) {
            try {
               this.wait();
            } catch (InterruptedException var4) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException();
            }

            return;
         }
      }

      if (this.unwrappedData == null) {
         if (!Bits.anyAreSet(this.state, 16)) {
            if (Bits.anyAreSet(this.state, 1)) {
               this.awaitWritable();
            } else {
               this.source.awaitReadable();
            }
         }
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      synchronized(this) {
         if (this.outstandingTasks > 0) {
            try {
               this.wait(timeUnit.toMillis(time));
            } catch (InterruptedException var7) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException();
            }

            return;
         }
      }

      if (this.unwrappedData == null) {
         if (!Bits.anyAreSet(this.state, 16)) {
            if (Bits.anyAreSet(this.state, 1)) {
               this.awaitWritable(time, timeUnit);
            } else {
               this.source.awaitReadable(time, timeUnit);
            }
         }
      }
   }

   public XnioIoThread getReadThread() {
      return this.delegate.getIoThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.delegate.getSourceChannel().getConduit().setReadReadyHandler(this.readReadyHandler = new SslReadReadyHandler(handler));
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 64)) {
         throw new ClosedChannelException();
      } else {
         return src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.anyAreSet(this.state, 64)) {
         throw new ClosedChannelException();
      } else {
         return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
      }
   }

   public int write(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 64)) {
         throw new ClosedChannelException();
      } else {
         return (int)this.doWrap(new ByteBuffer[]{src}, 0, 1);
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 64)) {
         throw new ClosedChannelException();
      } else {
         return this.doWrap(srcs, offs, len);
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 64)) {
         throw new ClosedChannelException();
      } else {
         return Conduits.writeFinalBasic(this, src);
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public void terminateWrites() throws IOException {
      this.state |= 64;
   }

   public boolean isWriteShutdown() {
      return false;
   }

   public void resumeWrites() {
      this.state |= 8;
      if (Bits.anyAreSet(this.state, 2)) {
         this.delegate.getSourceChannel().resumeReads();
      } else {
         this.delegate.getSinkChannel().resumeWrites();
      }

   }

   public void suspendWrites() {
      this.state &= -9;
      if (!Bits.allAreSet(this.state, 5)) {
         this.delegate.getSinkChannel().suspendWrites();
      }

   }

   public void wakeupWrites() {
      this.state |= 8;
      this.getWriteThread().execute(new Runnable() {
         public void run() {
            SslConduit.this.resumeWrites();
            SslConduit.this.writeReadyHandler.writeReady();
         }
      });
   }

   public boolean isWriteResumed() {
      return Bits.anyAreSet(this.state, 8);
   }

   public void awaitWritable() throws IOException {
      if (!Bits.anyAreSet(this.state, 64)) {
         if (this.outstandingTasks > 0) {
            synchronized(this) {
               if (this.outstandingTasks > 0) {
                  try {
                     this.wait();
                  } catch (InterruptedException var4) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  }

                  return;
               }
            }
         }

         if (Bits.anyAreSet(this.state, 2)) {
            this.awaitReadable();
         } else {
            this.sink.awaitWritable();
         }
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      if (!Bits.anyAreSet(this.state, 64)) {
         if (this.outstandingTasks > 0) {
            synchronized(this) {
               if (this.outstandingTasks > 0) {
                  try {
                     this.wait(timeUnit.toMillis(time));
                  } catch (InterruptedException var7) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  }

                  return;
               }
            }
         }

         if (Bits.anyAreSet(this.state, 2)) {
            this.awaitReadable(time, timeUnit);
         } else {
            this.sink.awaitWritable();
         }
      }
   }

   public XnioIoThread getWriteThread() {
      return this.delegate.getIoThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.delegate.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler = new SslWriteReadyHandler(handler));
   }

   public void truncateWrites() throws IOException {
      try {
         this.notifyWriteClosed();
      } finally {
         this.delegate.getSinkChannel().close();
      }

   }

   public boolean flush() throws IOException {
      if (Bits.anyAreSet(this.state, 512)) {
         return this.sink.flush();
      } else {
         if (this.wrappedData != null) {
            this.doWrap((ByteBuffer[])null, 0, 0);
            if (this.wrappedData != null) {
               return false;
            }
         }

         if (Bits.allAreSet(this.state, 64)) {
            if (Bits.allAreClear(this.state, 256)) {
               this.state |= 256;
               this.engine.closeOutbound();
               this.doWrap((ByteBuffer[])null, 0, 0);
               if (this.wrappedData != null) {
                  return false;
               }
            } else if (this.wrappedData != null && Bits.allAreClear(this.state, 512)) {
               this.doWrap((ByteBuffer[])null, 0, 0);
               if (this.wrappedData != null) {
                  return false;
               }
            }

            if (Bits.allAreClear(this.state, 512)) {
               this.sink.terminateWrites();
               this.state |= 512;
               this.notifyWriteClosed();
            }

            boolean result = this.sink.flush();
            if (result && Bits.anyAreSet(this.state, 16384)) {
               this.closed();
            }

            return result;
         } else {
            return this.sink.flush();
         }
      }
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return Bits.anyAreSet(this.state, 32) ? -1L : target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Bits.anyAreSet(this.state, 32) ? -1L : IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      return Bits.anyAreSet(this.state, 32) ? -1 : (int)this.doUnwrap(new ByteBuffer[]{dst}, 0, 1);
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return Bits.anyAreSet(this.state, 32) ? -1L : this.doUnwrap(dsts, offs, len);
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   private Executor getDelegatedTaskExecutor() {
      return (Executor)(this.delegatedTaskExecutor == null ? this.getWorker() : this.delegatedTaskExecutor);
   }

   void notifyWriteClosed() {
      if (!Bits.anyAreSet(this.state, 8192)) {
         boolean runListener = this.isWriteResumed() && Bits.anyAreSet(this.state, 4096);
         this.connection.writeClosed();
         this.engine.closeOutbound();
         this.state |= 8448;
         if (Bits.anyAreSet(this.state, 16384)) {
            this.closed();
         }

         if (Bits.anyAreSet(this.state, 1)) {
            this.notifyReadClosed();
         }

         this.state &= -3;
         if (runListener) {
            this.runWriteListener();
         }

      }
   }

   void notifyReadClosed() {
      if (!Bits.anyAreSet(this.state, 16384)) {
         boolean runListener = this.isReadResumed() && Bits.anyAreSet(this.state, 4096);
         this.connection.readClosed();

         try {
            this.engine.closeInbound();
         } catch (SSLException var3) {
            UndertowLogger.REQUEST_IO_LOGGER.trace("Exception closing read side of SSL channel", var3);
            if (Bits.allAreClear(this.state, 8192) && this.isWriteResumed()) {
               this.runWriteListener();
            }
         }

         this.state |= 16544;
         if (Bits.anyAreSet(this.state, 8192)) {
            this.closed();
         }

         if (Bits.anyAreSet(this.state, 2)) {
            this.notifyWriteClosed();
         }

         if (runListener) {
            this.runReadListener(false);
         }

      }
   }

   public void startHandshake() throws SSLException {
      this.state |= 1;
      this.engine.beginHandshake();
   }

   public SSLSession getSslSession() {
      return this.engine.getSession();
   }

   private void doHandshake() throws IOException {
      this.doUnwrap((ByteBuffer[])null, 0, 0);
      this.doWrap((ByteBuffer[])null, 0, 0);
   }

   private synchronized long doUnwrap(ByteBuffer[] userBuffers, int off, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 4096)) {
         throw new ClosedChannelException();
      } else if (this.outstandingTasks > 0) {
         return 0L;
      } else {
         if (Bits.anyAreSet(this.state, 1)) {
            this.doWrap((ByteBuffer[])null, 0, 0);
            if (Bits.allAreClear(this.state, 2)) {
               return 0L;
            }
         }

         boolean bytesProduced = false;
         PooledByteBuffer unwrappedData = this.unwrappedData;
         if (unwrappedData != null && userBuffers != null) {
            long copied = (long)Buffers.copy(userBuffers, off, len, unwrappedData.getBuffer());
            if (!unwrappedData.getBuffer().hasRemaining()) {
               unwrappedData.close();
               this.unwrappedData = null;
            }

            if (copied > 0L) {
               this.readListenerInvocationCount = 0;
            }

            return copied;
         } else {
            boolean var26 = false;

            long var8;
            boolean requiresListenerInvocation;
            label1450: {
               long var13;
               label1451: {
                  long res;
                  boolean requiresListenerInvocation;
                  label1452: {
                     label1453: {
                        label1454: {
                           try {
                              var26 = true;
                              if (Bits.allAreClear(this.state, 16)) {
                                 if (this.dataToUnwrap == null) {
                                    this.dataToUnwrap = this.bufferPool.allocate();
                                 }

                                 int res;
                                 try {
                                    res = this.source.read(this.dataToUnwrap.getBuffer());
                                 } catch (RuntimeException | Error | IOException var38) {
                                    this.dataToUnwrap.close();
                                    this.dataToUnwrap = null;
                                    throw var38;
                                 }

                                 this.dataToUnwrap.getBuffer().flip();
                                 if (res == -1) {
                                    this.dataToUnwrap.close();
                                    this.dataToUnwrap = null;
                                    this.notifyReadClosed();
                                    var8 = -1L;
                                    var26 = false;
                                    break label1453;
                                 }

                                 if (res == 0 && this.engine.getHandshakeStatus() == HandshakeStatus.FINISHED) {
                                    if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                                       this.dataToUnwrap.close();
                                       this.dataToUnwrap = null;
                                    }

                                    var8 = 0L;
                                    var26 = false;
                                    break label1450;
                                 }
                              }

                              int dataToUnwrapLength = this.dataToUnwrap.getBuffer().remaining();
                              long original = 0L;
                              if (userBuffers != null) {
                                 original = Buffers.remaining(userBuffers);
                              }

                              requiresListenerInvocation = false;

                              SSLEngineResult result;
                              try {
                                 if (userBuffers != null) {
                                    result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), userBuffers, off, len);
                                    if (result.getStatus() == Status.BUFFER_OVERFLOW) {
                                       unwrappedData = this.bufferPool.allocate();
                                       ByteBuffer[] d = new ByteBuffer[len + 1];
                                       System.arraycopy(userBuffers, off, d, 0, len);
                                       d[len] = unwrappedData.getBuffer();
                                       result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), d);
                                       requiresListenerInvocation = true;
                                    }

                                    bytesProduced = result.bytesProduced() > 0;
                                 } else {
                                    requiresListenerInvocation = true;
                                    if (unwrappedData == null) {
                                       unwrappedData = this.bufferPool.allocate();
                                    } else {
                                       unwrappedData.getBuffer().compact();
                                    }

                                    result = this.engine.unwrap(this.dataToUnwrap.getBuffer(), unwrappedData.getBuffer());
                                    bytesProduced = result.bytesProduced() > 0;
                                 }
                              } finally {
                                 if (requiresListenerInvocation) {
                                    unwrappedData.getBuffer().flip();
                                    if (!unwrappedData.getBuffer().hasRemaining()) {
                                       unwrappedData.close();
                                       unwrappedData = null;
                                    }
                                 }

                                 this.unwrappedData = unwrappedData;
                              }

                              if (result.getStatus() == Status.CLOSED) {
                                 if (this.dataToUnwrap != null) {
                                    this.dataToUnwrap.close();
                                    this.dataToUnwrap = null;
                                 }

                                 this.notifyReadClosed();
                                 res = -1L;
                                 var26 = false;
                                 break label1454;
                              }

                              if (!this.handleHandshakeResult(result)) {
                                 if (this.dataToUnwrap.getBuffer().hasRemaining() && result.getStatus() != Status.BUFFER_UNDERFLOW && this.dataToUnwrap.getBuffer().remaining() != dataToUnwrapLength) {
                                    this.state |= 16;
                                 } else {
                                    this.state &= -17;
                                 }

                                 res = 0L;
                                 var26 = false;
                                 break label1452;
                              }

                              if (result.getStatus() == Status.BUFFER_UNDERFLOW) {
                                 this.state &= -17;
                              } else if (result.getStatus() == Status.BUFFER_OVERFLOW) {
                                 UndertowLogger.REQUEST_LOGGER.sslBufferOverflow(this);
                                 IoUtils.safeClose((Closeable)this.delegate);
                              } else if (this.dataToUnwrap.getBuffer().hasRemaining() && this.dataToUnwrap.getBuffer().remaining() != dataToUnwrapLength) {
                                 this.state |= 16;
                              } else {
                                 this.state &= -17;
                              }

                              if (userBuffers != null) {
                                 res = original - Buffers.remaining(userBuffers);
                                 if (res > 0L) {
                                    this.readListenerInvocationCount = 0;
                                 }

                                 var13 = res;
                                 var26 = false;
                                 break label1451;
                              }

                              res = 0L;
                              var26 = false;
                           } catch (SSLException var40) {
                              try {
                                 try {
                                    this.clearWriteRequiresRead();
                                    this.doWrap((ByteBuffer[])null, 0, 0);
                                    this.flush();
                                 } catch (Exception var35) {
                                    UndertowLogger.REQUEST_LOGGER.debug("Failed to write out final SSL record", var35);
                                 }

                                 this.close();
                              } catch (Throwable var36) {
                                 UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doUnwrap", var36);
                              }

                              throw var40;
                           } catch (IOException | Error | RuntimeException var41) {
                              try {
                                 this.close();
                              } catch (Throwable var37) {
                                 UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doUnwrap", var37);
                              }

                              throw var41;
                           } finally {
                              if (var26) {
                                 boolean requiresListenerInvocation = false;
                                 if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                                    requiresListenerInvocation = true;
                                 }

                                 if (this.dataToUnwrap != null) {
                                    if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                                       this.dataToUnwrap.close();
                                       this.dataToUnwrap = null;
                                       this.state &= -17;
                                    } else if (Bits.allAreClear(this.state, 16)) {
                                       this.dataToUnwrap.getBuffer().compact();
                                    } else {
                                       requiresListenerInvocation = true;
                                    }
                                 }

                                 if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                                    this.runReadListener(false);
                                 }

                              }
                           }

                           requiresListenerInvocation = false;
                           if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                              requiresListenerInvocation = true;
                           }

                           if (this.dataToUnwrap != null) {
                              if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                                 this.dataToUnwrap.close();
                                 this.dataToUnwrap = null;
                                 this.state &= -17;
                              } else if (Bits.allAreClear(this.state, 16)) {
                                 this.dataToUnwrap.getBuffer().compact();
                              } else {
                                 requiresListenerInvocation = true;
                              }
                           }

                           if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                              this.runReadListener(false);
                           }

                           return res;
                        }

                        requiresListenerInvocation = false;
                        if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                           requiresListenerInvocation = true;
                        }

                        if (this.dataToUnwrap != null) {
                           if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                              this.dataToUnwrap.close();
                              this.dataToUnwrap = null;
                              this.state &= -17;
                           } else if (Bits.allAreClear(this.state, 16)) {
                              this.dataToUnwrap.getBuffer().compact();
                           } else {
                              requiresListenerInvocation = true;
                           }
                        }

                        if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                           this.runReadListener(false);
                        }

                        return res;
                     }

                     requiresListenerInvocation = false;
                     if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                        requiresListenerInvocation = true;
                     }

                     if (this.dataToUnwrap != null) {
                        if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                           this.dataToUnwrap.close();
                           this.dataToUnwrap = null;
                           this.state &= -17;
                        } else if (Bits.allAreClear(this.state, 16)) {
                           this.dataToUnwrap.getBuffer().compact();
                        } else {
                           requiresListenerInvocation = true;
                        }
                     }

                     if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                        this.runReadListener(false);
                     }

                     return var8;
                  }

                  requiresListenerInvocation = false;
                  if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                     requiresListenerInvocation = true;
                  }

                  if (this.dataToUnwrap != null) {
                     if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                        this.dataToUnwrap.close();
                        this.dataToUnwrap = null;
                        this.state &= -17;
                     } else if (Bits.allAreClear(this.state, 16)) {
                        this.dataToUnwrap.getBuffer().compact();
                     } else {
                        requiresListenerInvocation = true;
                     }
                  }

                  if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                     this.runReadListener(false);
                  }

                  return res;
               }

               boolean requiresListenerInvocation = false;
               if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
                  requiresListenerInvocation = true;
               }

               if (this.dataToUnwrap != null) {
                  if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                     this.dataToUnwrap.close();
                     this.dataToUnwrap = null;
                     this.state &= -17;
                  } else if (Bits.allAreClear(this.state, 16)) {
                     this.dataToUnwrap.getBuffer().compact();
                  } else {
                     requiresListenerInvocation = true;
                  }
               }

               if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
                  this.runReadListener(false);
               }

               return var13;
            }

            requiresListenerInvocation = false;
            if (bytesProduced || unwrappedData != null && unwrappedData.isOpen() && unwrappedData.getBuffer().hasRemaining()) {
               requiresListenerInvocation = true;
            }

            if (this.dataToUnwrap != null) {
               if (!this.dataToUnwrap.getBuffer().hasRemaining()) {
                  this.dataToUnwrap.close();
                  this.dataToUnwrap = null;
                  this.state &= -17;
               } else if (Bits.allAreClear(this.state, 16)) {
                  this.dataToUnwrap.getBuffer().compact();
               } else {
                  requiresListenerInvocation = true;
               }
            }

            if (requiresListenerInvocation && (Bits.anyAreSet(this.state, 4) || Bits.allAreSet(this.state, 10)) && !this.invokingReadListenerHandshake) {
               this.runReadListener(false);
            }

            return var8;
         }
      }
   }

   private synchronized long doWrap(ByteBuffer[] userBuffers, int off, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 4096)) {
         throw new ClosedChannelException();
      } else if (this.outstandingTasks > 0) {
         return 0L;
      } else {
         if (Bits.anyAreSet(this.state, 2)) {
            this.doUnwrap((ByteBuffer[])null, 0, 0);
            if (Bits.allAreClear(this.state, 1)) {
               return 0L;
            }
         }

         if (this.wrappedData != null) {
            int res = this.sink.write(this.wrappedData.getBuffer());
            if (res == 0 || this.wrappedData.getBuffer().hasRemaining()) {
               return 0L;
            }

            this.wrappedData.getBuffer().clear();
         } else {
            this.wrappedData = this.bufferPool.allocate();
         }

         long var15;
         try {
            SSLEngineResult result = this.wrapAndFlip(userBuffers, off, len);
            if (result.getStatus() == Status.BUFFER_UNDERFLOW) {
               throw new IOException("underflow");
            }

            if (result.getStatus() == Status.BUFFER_OVERFLOW && !this.wrappedData.getBuffer().hasRemaining()) {
               if (this.wrappedData.getBuffer().capacity() >= this.engine.getSession().getPacketBufferSize()) {
                  throw new IOException("overflow");
               }

               this.wrappedData.close();
               int bufferSize = this.engine.getSession().getPacketBufferSize();
               UndertowLogger.REQUEST_IO_LOGGER.tracev("Expanded buffer enabled due to overflow with empty buffer, buffer size is %s", bufferSize);
               if (expandedBufferPool == null || expandedBufferPool.getBufferSize() < bufferSize) {
                  expandedBufferPool = new DefaultByteBufferPool(false, bufferSize, -1, 12);
               }

               this.wrappedData = expandedBufferPool.allocate();
               result = this.wrapAndFlip(userBuffers, off, len);
               if (result.getStatus() == Status.BUFFER_OVERFLOW && !this.wrappedData.getBuffer().hasRemaining()) {
                  throw new IOException("overflow");
               }
            }

            if (this.wrappedData.getBuffer().hasRemaining()) {
               this.sink.write(this.wrappedData.getBuffer());
            }

            if (this.wrappedData.getBuffer().hasRemaining()) {
               var15 = (long)result.bytesConsumed();
               return var15;
            }

            if (this.handleHandshakeResult(result)) {
               if (result.getStatus() == Status.CLOSED && userBuffers != null) {
                  this.notifyWriteClosed();
                  throw new ClosedChannelException();
               }

               var15 = (long)result.bytesConsumed();
               return var15;
            }

            var15 = 0L;
         } catch (IOException | Error | RuntimeException var12) {
            try {
               this.close();
            } catch (Throwable var11) {
               UndertowLogger.REQUEST_LOGGER.debug("Exception closing SSLConduit after exception in doWrap()", var11);
            }

            throw var12;
         } finally {
            if (this.wrappedData != null && !this.wrappedData.getBuffer().hasRemaining()) {
               this.wrappedData.close();
               this.wrappedData = null;
            }

         }

         return var15;
      }
   }

   private SSLEngineResult wrapAndFlip(ByteBuffer[] userBuffers, int off, int len) throws IOException {
      SSLEngineResult result = null;

      while(result == null || result.getHandshakeStatus() == HandshakeStatus.NEED_WRAP && result.getStatus() != Status.BUFFER_OVERFLOW) {
         if (userBuffers == null) {
            result = this.engine.wrap(EMPTY_BUFFER, this.wrappedData.getBuffer());
         } else {
            result = this.engine.wrap(userBuffers, off, len, this.wrappedData.getBuffer());
         }
      }

      this.wrappedData.getBuffer().flip();
      return result;
   }

   private boolean handleHandshakeResult(SSLEngineResult result) throws IOException {
      switch (result.getHandshakeStatus()) {
         case NEED_TASK:
            this.state |= 2048;
            this.clearReadRequiresWrite();
            this.clearWriteRequiresRead();
            this.runTasks();
            return false;
         case NEED_UNWRAP:
            this.clearReadRequiresWrite();
            this.state |= 2050;
            this.sink.suspendWrites();
            if (Bits.anyAreSet(this.state, 8)) {
               this.source.resumeReads();
            }

            return false;
         case NEED_WRAP:
            this.clearWriteRequiresRead();
            this.state |= 2049;
            this.source.suspendReads();
            if (Bits.anyAreSet(this.state, 4)) {
               this.sink.resumeWrites();
            }

            return false;
         case FINISHED:
            if (Bits.anyAreSet(this.state, 2048)) {
               this.state &= -2049;
               this.handshakeCallback.run();
            }
         default:
            this.clearReadRequiresWrite();
            this.clearWriteRequiresRead();
            return true;
      }
   }

   private void clearReadRequiresWrite() {
      if (Bits.anyAreSet(this.state, 1)) {
         this.state &= -2;
         if (Bits.anyAreSet(this.state, 4)) {
            this.resumeReads(false);
         }

         if (Bits.allAreClear(this.state, 8)) {
            this.sink.suspendWrites();
         }
      }

   }

   private void clearWriteRequiresRead() {
      if (Bits.anyAreSet(this.state, 2)) {
         this.state &= -3;
         if (Bits.anyAreSet(this.state, 8)) {
            this.wakeupWrites();
         }

         if (Bits.allAreClear(this.state, 4)) {
            this.source.suspendReads();
         }
      }

   }

   private void closed() {
      if (!Bits.anyAreSet(this.state, 4096)) {
         synchronized(this) {
            this.state |= 5728;
            this.notifyReadClosed();
            this.notifyWriteClosed();
            if (this.dataToUnwrap != null) {
               this.dataToUnwrap.close();
               this.dataToUnwrap = null;
            }

            if (this.unwrappedData != null) {
               this.unwrappedData.close();
               this.unwrappedData = null;
            }

            if (this.wrappedData != null) {
               this.wrappedData.close();
               this.wrappedData = null;
            }

            if (Bits.allAreClear(this.state, 256)) {
               this.engine.closeOutbound();
            }

            if (Bits.allAreClear(this.state, 128)) {
               try {
                  this.engine.closeInbound();
               } catch (SSLException var4) {
                  UndertowLogger.REQUEST_LOGGER.ioException(var4);
               } catch (Throwable var5) {
                  UndertowLogger.REQUEST_LOGGER.handleUnexpectedFailure(var5);
               }
            }
         }

         IoUtils.safeClose((Closeable)this.delegate);
      }
   }

   private void runTasks() throws IOException {
      this.delegate.getSinkChannel().suspendWrites();
      this.delegate.getSourceChannel().suspendReads();
      List<Runnable> tasks = new ArrayList();

      for(Runnable t = this.engine.getDelegatedTask(); t != null; t = this.engine.getDelegatedTask()) {
         tasks.add(t);
      }

      synchronized(this) {
         this.outstandingTasks += tasks.size();
         Iterator var4 = tasks.iterator();

         while(var4.hasNext()) {
            final Runnable task = (Runnable)var4.next();
            Runnable wrappedTask = new Runnable() {
               public void run() {
                  boolean var9 = false;

                  try {
                     var9 = true;
                     task.run();
                     var9 = false;
                  } finally {
                     if (var9) {
                        synchronized(SslConduit.this) {
                           if (SslConduit.this.outstandingTasks == 1) {
                              SslConduit.this.getWriteThread().execute(new Runnable() {
                                 public void run() {
                                    synchronized(SslConduit.this) {
                                       SslConduit.this.notifyAll();
                                       --SslConduit.this.outstandingTasks;

                                       try {
                                          SslConduit.this.doHandshake();
                                       } catch (RuntimeException | Error | IOException var4) {
                                          UndertowLogger.REQUEST_LOGGER.debug("Closing SSLConduit after exception on handshake", var4);
                                          IoUtils.safeClose((Closeable)SslConduit.this.connection);
                                       }

                                       if (Bits.anyAreSet(SslConduit.this.state, 4)) {
                                          SslConduit.this.wakeupReads();
                                       }

                                       if (Bits.anyAreSet(SslConduit.this.state, 8)) {
                                          SslConduit.this.resumeWrites();
                                       }

                                    }
                                 }
                              });
                           } else {
                              SslConduit.this.outstandingTasks--;
                           }

                        }
                     }
                  }

                  synchronized(SslConduit.this) {
                     if (SslConduit.this.outstandingTasks == 1) {
                        SslConduit.this.getWriteThread().execute(new Runnable() {
                           public void run() {
                              synchronized(SslConduit.this) {
                                 SslConduit.this.notifyAll();
                                 --SslConduit.this.outstandingTasks;

                                 try {
                                    SslConduit.this.doHandshake();
                                 } catch (RuntimeException | Error | IOException var4) {
                                    UndertowLogger.REQUEST_LOGGER.debug("Closing SSLConduit after exception on handshake", var4);
                                    IoUtils.safeClose((Closeable)SslConduit.this.connection);
                                 }

                                 if (Bits.anyAreSet(SslConduit.this.state, 4)) {
                                    SslConduit.this.wakeupReads();
                                 }

                                 if (Bits.anyAreSet(SslConduit.this.state, 8)) {
                                    SslConduit.this.resumeWrites();
                                 }

                              }
                           }
                        });
                     } else {
                        SslConduit.this.outstandingTasks--;
                     }

                  }
               }
            };

            try {
               this.getDelegatedTaskExecutor().execute(wrappedTask);
            } catch (RejectedExecutionException var9) {
               UndertowLogger.REQUEST_IO_LOGGER.sslEngineDelegatedTaskRejected(var9);
               IoUtils.safeClose((Closeable)this.connection);
               throw SslConduit.DelegatedTaskRejectedClosedChannelException.INSTANCE;
            }
         }

      }
   }

   public SSLEngine getSSLEngine() {
      return this.engine;
   }

   public void close() {
      this.closed();
   }

   public void setSslEngine(SSLEngine engine) {
      this.engine = engine;
   }

   public String toString() {
      return "SslConduit{state=" + this.state + ", outstandingTasks=" + this.outstandingTasks + ", wrappedData=" + this.wrappedData + ", dataToUnwrap=" + this.dataToUnwrap + ", unwrappedData=" + this.unwrappedData + '}';
   }

   private class SslWriteReadyHandler implements WriteReadyHandler {
      private final WriteReadyHandler delegateHandler;

      private SslWriteReadyHandler(WriteReadyHandler delegateHandler) {
         this.delegateHandler = delegateHandler;
      }

      public void forceTermination() {
         try {
            if (this.delegateHandler != null) {
               this.delegateHandler.forceTermination();
            }
         } finally {
            IoUtils.safeClose((Closeable)SslConduit.this.delegate);
         }

      }

      public void terminated() {
         ChannelListeners.invokeChannelListener(SslConduit.this.connection.getSinkChannel(), SslConduit.this.connection.getSinkChannel().getCloseListener());
      }

      public void writeReady() {
         if (Bits.anyAreSet(SslConduit.this.state, 1)) {
            if (Bits.anyAreSet(SslConduit.this.state, 4)) {
               SslConduit.this.readReadyHandler.readReady();
            } else {
               try {
                  SslConduit.this.doHandshake();
               } catch (IOException var2) {
                  UndertowLogger.REQUEST_LOGGER.ioException(var2);
                  IoUtils.safeClose((Closeable)SslConduit.this.delegate);
               } catch (Throwable var3) {
                  UndertowLogger.REQUEST_LOGGER.handleUnexpectedFailure(var3);
                  IoUtils.safeClose((Closeable)SslConduit.this.delegate);
               }
            }
         }

         if (Bits.anyAreSet(SslConduit.this.state, 8)) {
            if (this.delegateHandler == null) {
               ChannelListener<? super ConduitStreamSinkChannel> writeListener = SslConduit.this.connection.getSinkChannel().getWriteListener();
               if (writeListener == null) {
                  SslConduit.this.suspendWrites();
               } else {
                  ChannelListeners.invokeChannelListener(SslConduit.this.connection.getSinkChannel(), writeListener);
               }
            } else {
               this.delegateHandler.writeReady();
            }
         }

         if (!Bits.anyAreSet(SslConduit.this.state, 9)) {
            SslConduit.this.delegate.getSinkChannel().suspendWrites();
         }

      }

      // $FF: synthetic method
      SslWriteReadyHandler(WriteReadyHandler x1, Object x2) {
         this(x1);
      }
   }

   private class SslReadReadyHandler implements ReadReadyHandler {
      private final ReadReadyHandler delegateHandler;

      private SslReadReadyHandler(ReadReadyHandler delegateHandler) {
         this.delegateHandler = delegateHandler;
      }

      public void readReady() {
         if (Bits.anyAreSet(SslConduit.this.state, 2) && Bits.anyAreSet(SslConduit.this.state, 12) && !Bits.anyAreSet(SslConduit.this.state, 128)) {
            try {
               SslConduit.this.invokingReadListenerHandshake = true;
               SslConduit.this.doHandshake();
            } catch (IOException var8) {
               UndertowLogger.REQUEST_LOGGER.ioException(var8);
               IoUtils.safeClose((Closeable)SslConduit.this.delegate);
            } catch (Throwable var9) {
               UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var9);
               IoUtils.safeClose((Closeable)SslConduit.this.delegate);
            } finally {
               SslConduit.this.invokingReadListenerHandshake = false;
            }

            if (!Bits.anyAreSet(SslConduit.this.state, 4) && !Bits.allAreSet(SslConduit.this.state, 10)) {
               SslConduit.this.delegate.getSourceChannel().suspendReads();
            }
         }

         boolean noProgress = false;
         int initialDataToUnwrap = -1;
         int initialUnwrapped = -1;
         if (Bits.anyAreSet(SslConduit.this.state, 4)) {
            if (this.delegateHandler == null) {
               ChannelListener<? super ConduitStreamSourceChannel> readListener = SslConduit.this.connection.getSourceChannel().getReadListener();
               if (readListener == null) {
                  SslConduit.this.suspendReads();
               } else {
                  if (Bits.anyAreSet(SslConduit.this.state, 16)) {
                     initialDataToUnwrap = SslConduit.this.dataToUnwrap.getBuffer().remaining();
                  }

                  if (SslConduit.this.unwrappedData != null) {
                     initialUnwrapped = SslConduit.this.unwrappedData.getBuffer().remaining();
                  }

                  ChannelListeners.invokeChannelListener(SslConduit.this.connection.getSourceChannel(), readListener);
                  if (Bits.anyAreSet(SslConduit.this.state, 16) && initialDataToUnwrap == SslConduit.this.dataToUnwrap.getBuffer().remaining()) {
                     noProgress = true;
                  } else if (SslConduit.this.unwrappedData != null && SslConduit.this.unwrappedData.getBuffer().remaining() == initialUnwrapped) {
                     noProgress = true;
                  }
               }
            } else {
               this.delegateHandler.readReady();
            }
         }

         if (Bits.anyAreSet(SslConduit.this.state, 4) && (SslConduit.this.unwrappedData != null || Bits.anyAreSet(SslConduit.this.state, 16))) {
            if (Bits.anyAreSet(SslConduit.this.state, 16384)) {
               if (SslConduit.this.unwrappedData != null) {
                  SslConduit.this.unwrappedData.close();
               }

               if (SslConduit.this.dataToUnwrap != null) {
                  SslConduit.this.dataToUnwrap.close();
               }

               SslConduit.this.unwrappedData = null;
               SslConduit.this.dataToUnwrap = null;
            } else if ((!Bits.anyAreSet(SslConduit.this.state, 1) || SslConduit.this.wrappedData == null) && SslConduit.this.outstandingTasks == 0 && !noProgress) {
               SslConduit.this.runReadListener(false);
            }
         }

      }

      public void forceTermination() {
         try {
            if (this.delegateHandler != null) {
               this.delegateHandler.forceTermination();
            }
         } finally {
            IoUtils.safeClose((Closeable)SslConduit.this.delegate);
         }

      }

      public void terminated() {
         ChannelListeners.invokeChannelListener(SslConduit.this.connection.getSourceChannel(), SslConduit.this.connection.getSourceChannel().getCloseListener());
      }

      // $FF: synthetic method
      SslReadReadyHandler(ReadReadyHandler x1, Object x2) {
         this(x1);
      }
   }

   private static final class DelegatedTaskRejectedClosedChannelException extends ClosedChannelException {
      private static final DelegatedTaskRejectedClosedChannelException INSTANCE = new DelegatedTaskRejectedClosedChannelException();

      public Throwable fillInStackTrace() {
         return this;
      }

      public Throwable initCause(Throwable ignored) {
         return this;
      }

      public void setStackTrace(StackTraceElement[] ignored) {
      }
   }
}
