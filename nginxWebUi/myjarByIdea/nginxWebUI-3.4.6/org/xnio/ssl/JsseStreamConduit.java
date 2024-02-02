package org.xnio.ssl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.Pool;
import org.xnio.Pooled;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

final class JsseStreamConduit implements StreamSourceConduit, StreamSinkConduit, Runnable {
   private static final boolean TRACE_SSL = Boolean.getBoolean("org.xnio.ssl.TRACE_SSL");
   private final JsseSslConnection connection;
   private final SSLEngine engine;
   private final StreamSourceConduit sourceConduit;
   private final StreamSinkConduit sinkConduit;
   private final Pooled<ByteBuffer> receiveBuffer;
   private final Pooled<ByteBuffer> sendBuffer;
   private final Pooled<ByteBuffer> readBuffer;
   private int state = 262144;
   private int tasks;
   private ReadReadyHandler readReadyHandler;
   private WriteReadyHandler writeReadyHandler;
   private static final int FLAG_TLS = 131072;
   private static final int FLAG_INLINE_TASKS = 262144;
   private static final int FLAG_TASK_QUEUED = 524288;
   private static final int FLAG_NEED_ENGINE_TASK = 1048576;
   private static final int FLAG_FLUSH_NEEDED = 2097152;
   private static final int READ_FLAG_SHUTDOWN = 1;
   private static final int READ_FLAG_EOF = 2;
   private static final int READ_FLAG_RESUMED = 4;
   private static final int READ_FLAG_UP_RESUMED = 8;
   private static final int READ_FLAG_WAKEUP = 16;
   private static final int READ_FLAG_READY = 32;
   private static final int READ_FLAG_NEEDS_WRITE = 64;
   private static final int WRITE_FLAG_SHUTDOWN = 256;
   private static final int WRITE_FLAG_SHUTDOWN2 = 512;
   private static final int WRITE_FLAG_SHUTDOWN3 = 1024;
   private static final int WRITE_FLAG_FINISHED = 2048;
   private static final int WRITE_FLAG_RESUMED = 4096;
   private static final int WRITE_FLAG_UP_RESUMED = 8192;
   private static final int WRITE_FLAG_WAKEUP = 16384;
   private static final int WRITE_FLAG_READY = 32768;
   private static final int WRITE_FLAG_NEEDS_READ = 65536;
   private final WriteReadyHandler writeReady = new WriteReadyHandler() {
      public void forceTermination() {
         if (Bits.anyAreClear(JsseStreamConduit.this.state, 2048)) {
            JsseStreamConduit.this.state = JsseStreamConduit.this.state | 3840;
         }

         WriteReadyHandler writeReadyHandler = JsseStreamConduit.this.writeReadyHandler;
         if (writeReadyHandler != null) {
            try {
               writeReadyHandler.forceTermination();
            } catch (Throwable var3) {
            }
         }

      }

      public void terminated() {
         if (Bits.anyAreClear(JsseStreamConduit.this.state, 2048)) {
            JsseStreamConduit.this.state = JsseStreamConduit.this.state | 3840;
         }

         WriteReadyHandler writeReadyHandler = JsseStreamConduit.this.writeReadyHandler;
         if (writeReadyHandler != null) {
            try {
               writeReadyHandler.terminated();
            } catch (Throwable var3) {
            }
         }

      }

      public void writeReady() {
         JsseStreamConduit.this.writeReady();
      }
   };
   private final ReadReadyHandler readReady = new ReadReadyHandler() {
      public void forceTermination() {
         if (Bits.anyAreClear(JsseStreamConduit.this.state, 1)) {
            JsseStreamConduit.this.state = JsseStreamConduit.this.state | 1;
         }

         ReadReadyHandler readReadyHandler = JsseStreamConduit.this.readReadyHandler;
         if (readReadyHandler != null) {
            try {
               readReadyHandler.forceTermination();
            } catch (Throwable var3) {
            }
         }

      }

      public void terminated() {
         if (Bits.anyAreClear(JsseStreamConduit.this.state, 1)) {
            JsseStreamConduit.this.state = JsseStreamConduit.this.state | 1;
         }

         ReadReadyHandler readReadyHandler = JsseStreamConduit.this.readReadyHandler;
         if (readReadyHandler != null) {
            try {
               readReadyHandler.terminated();
            } catch (Throwable var3) {
            }
         }

      }

      public void readReady() {
         JsseStreamConduit.this.readReady();
      }
   };
   private final ByteBuffer[] readBufferHolder = new ByteBuffer[1];
   private final ByteBuffer[] writeBufferHolder = new ByteBuffer[1];
   private static final ByteBuffer[] NO_BUFFERS = new ByteBuffer[0];
   private static final int IO_GOAL_READ = 0;
   private static final int IO_GOAL_WRITE = 1;
   private static final int IO_GOAL_FLUSH = 2;
   private static final int IO_GOAL_WRITE_FINAL = 3;

   JsseStreamConduit(JsseSslConnection connection, SSLEngine engine, StreamSourceConduit sourceConduit, StreamSinkConduit sinkConduit, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) {
      boolean ok = false;
      SSLSession session = engine.getSession();
      int packetBufferSize = session.getPacketBufferSize();
      Pooled<ByteBuffer> receiveBuffer = socketBufferPool.allocate();

      Pooled sendBuffer;
      Pooled readBuffer;
      try {
         ((ByteBuffer)receiveBuffer.getResource()).flip();
         sendBuffer = socketBufferPool.allocate();

         try {
            if (((ByteBuffer)receiveBuffer.getResource()).capacity() < packetBufferSize || ((ByteBuffer)sendBuffer.getResource()).capacity() < packetBufferSize) {
               throw Messages.msg.socketBufferTooSmall();
            }

            int applicationBufferSize = session.getApplicationBufferSize();
            readBuffer = applicationBufferPool.allocate();

            try {
               if (((ByteBuffer)readBuffer.getResource()).capacity() < applicationBufferSize) {
                  throw Messages.msg.appBufferTooSmall();
               }

               ok = true;
            } finally {
               if (!ok) {
                  readBuffer.free();
               }

            }
         } finally {
            if (!ok) {
               sendBuffer.free();
            }

         }
      } finally {
         if (!ok) {
            receiveBuffer.free();
         }

      }

      this.receiveBuffer = receiveBuffer;
      this.sendBuffer = sendBuffer;
      this.readBuffer = readBuffer;
      ((ByteBuffer)receiveBuffer.getResource()).clear().limit(0);
      if (sourceConduit.getReadThread() != sinkConduit.getWriteThread()) {
         throw new IllegalArgumentException("Source and sink thread mismatch");
      } else {
         this.connection = connection;
         this.engine = engine;
         this.sourceConduit = sourceConduit;
         this.sinkConduit = sinkConduit;
         sourceConduit.setReadReadyHandler(this.readReady);
         sinkConduit.setWriteReadyHandler(this.writeReady);
      }
   }

   public String getStatus() {
      StringBuilder b = new StringBuilder();
      b.append("General flags:");
      int state = this.state;
      if (Bits.allAreSet(state, 131072)) {
         b.append(" TLS");
      }

      if (Bits.allAreSet(state, 262144)) {
         b.append(" INLINE_TASKS");
      }

      if (Bits.allAreSet(state, 524288)) {
         b.append(" TASK_QUEUED");
      }

      if (Bits.allAreSet(state, 1048576)) {
         b.append(" NEED_ENGINE_TASK");
      }

      if (Bits.allAreSet(state, 2097152)) {
         b.append(" FLUSH_NEEDED");
      }

      b.append("\nRead flags:");
      if (Bits.allAreSet(state, 1)) {
         b.append(" SHUTDOWN");
      }

      if (Bits.allAreSet(state, 2)) {
         b.append(" EOF");
      }

      if (Bits.allAreSet(state, 4)) {
         b.append(" RESUMED");
      }

      if (Bits.allAreSet(state, 8)) {
         b.append(" UP_RESUMED");
      }

      if (Bits.allAreSet(state, 16)) {
         b.append(" WAKEUP");
      }

      if (Bits.allAreSet(state, 32)) {
         b.append(" READY");
      }

      if (Bits.allAreSet(state, 64)) {
         b.append(" NEEDS_WRITE");
      }

      b.append("\nWrite flags:");
      if (Bits.allAreSet(state, 256)) {
         b.append(" SHUTDOWN");
      }

      if (Bits.allAreSet(state, 512)) {
         b.append(" SHUTDOWN2");
      }

      if (Bits.allAreSet(state, 1024)) {
         b.append(" SHUTDOWN3");
      }

      if (Bits.allAreSet(state, 2048)) {
         b.append(" FINISHED");
      }

      if (Bits.allAreSet(state, 4096)) {
         b.append(" RESUMED");
      }

      if (Bits.allAreSet(state, 8192)) {
         b.append(" UP_RESUMED");
      }

      if (Bits.allAreSet(state, 16384)) {
         b.append(" WAKEUP");
      }

      if (Bits.allAreSet(state, 32768)) {
         b.append(" READY");
      }

      if (Bits.allAreSet(state, 65536)) {
         b.append(" NEEDS_READ");
      }

      b.append('\n');
      return b.toString();
   }

   public String toString() {
      return String.format("JSSE Stream Conduit for %s, status:%n%s", this.connection, this.getStatus());
   }

   public XnioWorker getWorker() {
      return this.connection.getIoThread().getWorker();
   }

   public XnioIoThread getReadThread() {
      return this.connection.getIoThread();
   }

   public XnioIoThread getWriteThread() {
      return this.connection.getIoThread();
   }

   void beginHandshake() throws IOException {
      int state = this.state;
      if (Bits.anyAreSet(state, 258)) {
         throw new ClosedChannelException();
      } else {
         if (Bits.allAreClear(state, 131072)) {
            this.state = state | 131072;
         }

         this.engine.beginHandshake();
      }
   }

   SSLSession getSslSession() {
      return Bits.allAreSet(this.state, 131072) ? this.engine.getSession() : null;
   }

   SSLEngine getEngine() {
      return this.engine;
   }

   boolean isTls() {
      return Bits.allAreSet(this.state, 131072);
   }

   boolean markTerminated() {
      this.readBuffer.free();
      this.receiveBuffer.free();
      this.sendBuffer.free();
      if (Bits.anyAreClear(this.state, 2049)) {
         this.state |= 3841;
         return true;
      } else {
         return false;
      }
   }

   public void run() {
      assert Thread.currentThread() == this.getWriteThread();

      int state = this.state;
      boolean flagTaskQueued = Bits.allAreSet(state, 524288);
      boolean modify = flagTaskQueued;
      boolean queueTask = false;
      state &= -524289;

      try {
         if (Bits.allAreSet(state, 1048576)) {
            throw new UnsupportedOperationException();
         }

         if (Bits.anyAreSet(state, 16384) || Bits.allAreSet(state, 36864)) {
            WriteReadyHandler writeReadyHandler = this.writeReadyHandler;
            if (Bits.allAreSet(state, 16384)) {
               state = state & -16385 | 4096;
               modify = true;
            }

            if (writeReadyHandler != null) {
               if (Bits.allAreSet(state, 4096)) {
                  try {
                     if (modify) {
                        modify = false;
                        this.state = state;
                     }

                     writeReadyHandler.writeReady();
                  } catch (Throwable var27) {
                  } finally {
                     state = this.state & -524289;
                     modify = true;
                  }

                  if (Bits.allAreSet(state, 4096)) {
                     if (!Bits.allAreSet(state, 32768) && Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 8)) {
                        state |= 8;
                        modify = true;
                        this.sourceConduit.resumeReads();
                     } else if (Bits.allAreClear(state, 8192)) {
                        this.sinkConduit.resumeWrites();
                     }
                  }
               } else if (Bits.allAreClear(state, 68) && Bits.allAreSet(state, 8192)) {
                  state &= -8193;
                  modify = true;
                  this.suspendWrites();
               }
            } else {
               state &= -4097;
               modify = true;
               if (Bits.allAreClear(state, 68) && Bits.allAreSet(state, 8192)) {
                  state &= -8193;
                  modify = true;
                  this.suspendWrites();
               }
            }
         }

         if (Bits.anyAreSet(state, 16) || Bits.allAreSet(state, 36)) {
            ReadReadyHandler readReadyHandler = this.readReadyHandler;
            if (Bits.allAreSet(state, 16)) {
               state = state & -17 | 4;
               modify = true;
            }

            if (readReadyHandler != null) {
               if (Bits.allAreSet(state, 4)) {
                  try {
                     if (modify) {
                        modify = false;
                        this.state = state;
                     }

                     readReadyHandler.readReady();
                  } catch (Throwable var25) {
                  } finally {
                     state = this.state & -524289;
                     modify = true;
                  }

                  if (Bits.allAreSet(state, 4)) {
                     if (Bits.allAreSet(state, 32)) {
                        if (!flagTaskQueued) {
                           state |= 524288;
                           queueTask = true;
                           modify = true;
                        }
                     } else if (Bits.allAreSet(state, 64) && Bits.allAreClear(state, 8192)) {
                        state |= 8192;
                        modify = true;
                        this.sinkConduit.resumeWrites();
                     } else if (Bits.allAreClear(state, 8)) {
                        this.sourceConduit.resumeReads();
                     }
                  }
               } else if (Bits.allAreClear(state, 69632) && Bits.allAreSet(state, 8)) {
                  state &= -9;
                  modify = true;
                  this.suspendReads();
               }
            } else {
               state &= -5;
               modify = true;
               if (Bits.allAreClear(state, 69632) && Bits.allAreSet(state, 8)) {
                  state &= -9;
                  this.suspendReads();
               }
            }
         }
      } finally {
         if (modify) {
            this.state = state;
            if (queueTask) {
               this.getReadThread().execute(this);
            }
         }

      }

   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.readReadyHandler = handler;
   }

   public void writeReady() {
      int state = this.state;
      state |= 32768;
      if (Bits.allAreSet(state, 64)) {
         state |= 32;
      }

      this.state = state;
      if (Bits.allAreClear(state, 524288)) {
         this.run();
      }

      state = this.state;
      if (this.sinkConduit.isWriteResumed() && Bits.allAreClear(state, 4160)) {
         this.sinkConduit.suspendWrites();
      }

      if (this.sourceConduit.isReadResumed() && Bits.allAreClear(state, 65540)) {
         this.sourceConduit.suspendReads();
      }

   }

   public void readReady() {
      int state = this.state;
      state |= 32;
      if (Bits.allAreSet(state, 65536)) {
         state |= 32768;
      }

      this.state = state;
      if (Bits.allAreClear(state, 524288)) {
         this.run();
      }

      state = this.state;
      if (this.sourceConduit.isReadResumed() && Bits.allAreClear(state, 65540)) {
         this.sourceConduit.suspendReads();
      }

      if (this.sinkConduit.isWriteResumed() && Bits.allAreClear(state, 4160)) {
         this.sinkConduit.suspendWrites();
      }

   }

   public void suspendWrites() {
      int state = this.state;

      try {
         if (Bits.allAreSet(state, 4096)) {
            state &= -4097;
            if (Bits.allAreSet(state, 8192) && Bits.allAreClear(state, 64)) {
               state &= -8193;
               this.sinkConduit.suspendWrites();
            }

            if (Bits.allAreSet(state, 8) && Bits.allAreClear(state, 4)) {
               state &= -9;
               this.sourceConduit.suspendReads();
            }
         }
      } finally {
         this.state = state;
      }

   }

   public void resumeWrites() {
      int state = this.state;
      if (Bits.allAreClear(state, 4096)) {
         if (Bits.allAreSet(state, 2048)) {
            this.wakeupWrites();
            return;
         }

         boolean queueTask = false;

         try {
            state |= 4096;
            if (Bits.allAreSet(state, 32768)) {
               if (queueTask = Bits.allAreClear(state, 524288)) {
                  state |= 524288;
               }
            } else if (Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 8)) {
               state |= 8;
               this.sourceConduit.resumeReads();
            } else if (Bits.allAreClear(state, 8192)) {
               state |= 8192;
               this.sinkConduit.resumeWrites();
            }
         } finally {
            this.state = state;
            if (queueTask) {
               this.getReadThread().execute(this);
            }

         }
      }

   }

   public void wakeupWrites() {
      int state = this.state;
      if (Bits.allAreClear(state, 16384)) {
         if (Bits.allAreClear(state, 524288)) {
            this.state = state | 16384 | 524288;
            this.getReadThread().execute(this);
         } else {
            this.state = state | 16384;
         }
      }

   }

   public void terminateWrites() throws IOException {
      int state = this.state;
      if (Bits.allAreClear(state, 2048)) {
         this.state = state | 256;
         if (Bits.allAreSet(state, 131072)) {
            try {
               if (this.engine.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
                  this.engine.closeOutbound();
               }

               this.performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0);
               if (Bits.allAreSet(this.state, 2048)) {
                  this.sinkConduit.terminateWrites();
               }
            } catch (Throwable var5) {
               this.state |= 2048;

               try {
                  this.sinkConduit.truncateWrites();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }
         } else {
            this.sinkConduit.terminateWrites();
         }
      }

   }

   public void truncateWrites() throws IOException {
      int state = this.state;
      if (Bits.allAreClear(state, 256)) {
         if (Bits.allAreSet(state, 131072)) {
            try {
               state |= 3328;

               try {
                  this.engine.closeOutbound();
               } catch (Throwable var9) {
                  try {
                     this.sinkConduit.truncateWrites();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }

                  throw var9;
               }

               this.sinkConduit.truncateWrites();
            } finally {
               this.state = state;
            }
         } else {
            this.state = state | 256 | 1024 | 2048;
            this.sinkConduit.truncateWrites();
         }
      }

   }

   public boolean isWriteResumed() {
      return Bits.anyAreSet(this.state, 20480);
   }

   public boolean isWriteShutdown() {
      return Bits.allAreSet(this.state, 256);
   }

   public void awaitWritable() throws IOException {
      int state = this.state;

      while(Bits.allAreSet(state, 1048576)) {
         synchronized(this) {
            while(this.tasks != 0) {
               try {
                  this.wait();
               } catch (InterruptedException var5) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }
            }

            state &= -1048577;
            this.state = state;
         }
      }

      if (Bits.allAreClear(state, 32768)) {
         if (Bits.allAreSet(state, 65536)) {
            this.sourceConduit.awaitReadable();
         } else {
            this.sinkConduit.awaitWritable();
         }
      }

   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      int state = this.state;
      long nanos = timeUnit.toNanos(time);

      while(Bits.allAreSet(state, 1048576)) {
         synchronized(this) {
            long start = System.nanoTime();

            while(this.tasks != 0) {
               try {
                  if (nanos <= 0L) {
                     return;
                  }

                  this.wait(nanos / 1000000L, (int)(nanos % 1000000L));
                  nanos -= -start + (start = System.nanoTime());
               } catch (InterruptedException var12) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }
            }

            state &= -1048577;
            this.state = state;
         }
      }

      if (Bits.allAreClear(state, 32768)) {
         if (Bits.allAreSet(state, 65536)) {
            this.sourceConduit.awaitReadable(nanos, TimeUnit.NANOSECONDS);
         } else {
            this.sinkConduit.awaitWritable(nanos, TimeUnit.NANOSECONDS);
         }
      }

   }

   public void suspendReads() {
      int state = this.state;

      try {
         if (Bits.allAreSet(state, 4)) {
            state &= -5;
            if (Bits.allAreSet(state, 8) && Bits.allAreClear(state, 65536)) {
               state &= -9;
               this.sourceConduit.suspendReads();
            }

            if (Bits.allAreSet(state, 8192) && Bits.allAreClear(state, 4096)) {
               state &= -8193;
               this.sinkConduit.suspendWrites();
            }
         }
      } finally {
         this.state = state;
      }

   }

   public void resumeReads() {
      int state = this.state;
      boolean queueTask = false;
      if (Bits.allAreClear(state, 4)) {
         try {
            state |= 4;
            if (Bits.allAreClear(state, 4096)) {
               state |= 32;
            }

            if (Bits.allAreSet(state, 32)) {
               if (queueTask = Bits.allAreClear(state, 524288)) {
                  state |= 524288;
               }
            } else if (Bits.allAreSet(state, 64) && Bits.allAreClear(state, 8192)) {
               state |= 8192;
               this.sinkConduit.resumeWrites();
            } else if (Bits.allAreClear(state, 8)) {
               state |= 8;
               this.sourceConduit.resumeReads();
            }
         } finally {
            this.state = state;
            if (queueTask) {
               this.getReadThread().execute(this);
            }

         }
      }

   }

   public void wakeupReads() {
      int state = this.state;
      if (Bits.allAreClear(state, 16)) {
         if (Bits.allAreClear(state, 524288)) {
            this.state = state | 16 | 524288;
            this.getReadThread().execute(this);
         } else {
            this.state = state | 16;
         }
      }

   }

   public void terminateReads() throws IOException {
      int state = this.state;
      if (Bits.allAreClear(state, 1)) {
         if (Bits.allAreClear(state, 131072)) {
            this.sourceConduit.terminateReads();
         } else {
            this.state = state | 1;
            if (Bits.allAreClear(state, 2)) {
               this.performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0);
               if (Bits.allAreSet(state, 65536) && Bits.allAreClear(state, 2)) {
                  return;
               }

               if (!this.engine.isInboundDone() && this.engine.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
                  this.engine.closeInbound();
               }

               long res = this.performIO(0, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0);
               if (res == -1L) {
                  this.state |= 2;
               }
            }

            if (Bits.allAreClear(this.state, 2) || ((ByteBuffer)this.receiveBuffer.getResource()).hasRemaining()) {
               EOFException exception = Messages.msg.connectionClosedEarly();

               try {
                  this.sourceConduit.terminateReads();
               } catch (IOException var4) {
                  exception.addSuppressed(var4);
               }

               throw exception;
            }

            this.sourceConduit.terminateReads();
         }
      }

   }

   public boolean isReadResumed() {
      return Bits.anyAreSet(this.state, 20);
   }

   public boolean isReadShutdown() {
      return Bits.allAreSet(this.state, 1);
   }

   public void awaitReadable() throws IOException {
      int state = this.state;

      while(Bits.allAreSet(state, 1048576)) {
         synchronized(this) {
            while(this.tasks != 0) {
               try {
                  this.wait();
               } catch (InterruptedException var5) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }
            }

            state &= -1048577;
            this.state = state;
         }
      }

      if (Bits.allAreClear(state, 32)) {
         if (Bits.allAreSet(state, 64)) {
            this.sinkConduit.awaitWritable();
         } else {
            this.sourceConduit.awaitReadable();
         }
      }

   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      int state = this.state;
      long nanos = timeUnit.toNanos(time);

      while(Bits.allAreSet(state, 1048576)) {
         synchronized(this) {
            long start = System.nanoTime();

            while(this.tasks != 0) {
               try {
                  if (nanos <= 0L) {
                     return;
                  }

                  this.wait(nanos / 1000000L, (int)(nanos % 1000000L));
                  nanos -= -start + (start = System.nanoTime());
               } catch (InterruptedException var12) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }
            }

            state &= -1048577;
            this.state = state;
         }
      }

      if (Bits.allAreClear(state, 32)) {
         if (Bits.allAreSet(state, 64)) {
            this.sinkConduit.awaitWritable(nanos, TimeUnit.NANOSECONDS);
         } else {
            this.sourceConduit.awaitReadable(nanos, TimeUnit.NANOSECONDS);
         }
      }

   }

   public int read(ByteBuffer dst) throws IOException {
      int state = this.state;
      if (Bits.anyAreSet(state, 1)) {
         return -1;
      } else {
         int var4;
         if (!Bits.anyAreSet(state, 2)) {
            if (Bits.allAreClear(state, 131072)) {
               int res = this.sourceConduit.read(dst);
               if (res == 0) {
                  if (Bits.allAreSet(state, 32)) {
                     this.state = state & -33;
                  }
               } else if (res == -1) {
                  this.state = (state | 2) & -33;
               }

               return res;
            } else {
               ByteBuffer[] readBufferHolder = this.readBufferHolder;
               readBufferHolder[0] = dst;

               try {
                  var4 = (int)this.performIO(0, NO_BUFFERS, 0, 0, readBufferHolder, 0, 1);
               } finally {
                  readBufferHolder[0] = null;
               }

               return var4;
            }
         } else if (((ByteBuffer)this.readBuffer.getResource()).position() > 0) {
            ByteBuffer readBufferResource = (ByteBuffer)this.readBuffer.getResource();
            readBufferResource.flip();

            try {
               if (TRACE_SSL) {
                  Messages.msg.tracef("TLS copy unwrapped data from %s to %s", Buffers.debugString(readBufferResource), Buffers.debugString(dst));
               }

               var4 = Buffers.copy(dst, readBufferResource);
            } finally {
               readBufferResource.compact();
            }

            return var4;
         } else {
            return -1;
         }
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      int state = this.state;
      if (Bits.anyAreSet(state, 1)) {
         return -1L;
      } else if (!Bits.anyAreSet(state, 2)) {
         if (Bits.allAreClear(state, 131072)) {
            long res = this.sourceConduit.read(dsts, offs, len);
            if (res == 0L) {
               if (Bits.allAreSet(state, 32)) {
                  this.state = state & -33;
               }
            } else if (res == -1L) {
               this.state = (state | 2) & -33;
            }

            return res;
         } else {
            return this.performIO(0, NO_BUFFERS, 0, 0, dsts, offs, len);
         }
      } else if (((ByteBuffer)this.readBuffer.getResource()).position() > 0) {
         ByteBuffer readBufferResource = (ByteBuffer)this.readBuffer.getResource();
         readBufferResource.flip();

         long var6;
         try {
            if (TRACE_SSL) {
               Messages.msg.tracef("TLS copy unwrapped data from %s to %s", Buffers.debugString(readBufferResource), Buffers.debugString(dsts, offs, len));
            }

            var6 = (long)Buffers.copy(dsts, offs, len, readBufferResource);
         } finally {
            readBufferResource.compact();
         }

         return var6;
      } else {
         return -1L;
      }
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return Bits.allAreClear(this.state, 131072) ? this.sourceConduit.transferTo(position, count, target) : target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Bits.allAreClear(this.state, 131072) ? this.sourceConduit.transferTo(count, throughBuffer, target) : Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int write(ByteBuffer src) throws IOException {
      if (Bits.allAreSet(this.state, 256)) {
         throw new ClosedChannelException();
      } else if (Bits.allAreClear(this.state, 131072)) {
         return this.sinkConduit.write(src);
      } else {
         ByteBuffer[] writeBufferHolder = this.writeBufferHolder;
         writeBufferHolder[0] = src;

         int var3;
         try {
            var3 = (int)this.write(writeBufferHolder, 0, 1);
         } finally {
            writeBufferHolder[0] = null;
         }

         return var3;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (Bits.allAreSet(this.state, 256)) {
         throw new ClosedChannelException();
      } else if (Bits.allAreClear(this.state, 131072)) {
         return this.sinkConduit.writeFinal(src);
      } else {
         ByteBuffer[] writeBufferHolder = this.writeBufferHolder;
         writeBufferHolder[0] = src;

         int var3;
         try {
            var3 = (int)this.writeFinal(writeBufferHolder, 0, 1);
         } finally {
            writeBufferHolder[0] = null;
         }

         return var3;
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (Bits.allAreSet(this.state, 256)) {
         throw new ClosedChannelException();
      } else if (Bits.allAreClear(this.state, 131072)) {
         return this.sinkConduit.write(srcs, offs, len);
      } else {
         long r1 = Buffers.remaining(srcs, offs, len);
         this.performIO(1, srcs, offs, len, NO_BUFFERS, 0, 0);
         return r1 - Buffers.remaining(srcs, offs, len);
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (Bits.allAreSet(this.state, 256)) {
         throw new ClosedChannelException();
      } else if (Bits.allAreClear(this.state, 131072)) {
         return this.sinkConduit.writeFinal(srcs, offs, len);
      } else {
         long r1 = Buffers.remaining(srcs, offs, len);
         this.performIO(3, srcs, offs, len, NO_BUFFERS, 0, 0);
         return r1 - Buffers.remaining(srcs, offs, len);
      }
   }

   public boolean flush() throws IOException {
      int state = this.state;
      if (Bits.allAreSet(state, 2048)) {
         return true;
      } else if (Bits.allAreSet(state, 1024)) {
         if (this.sinkConduit.flush()) {
            this.state = state | 2048;
            return true;
         } else {
            return false;
         }
      } else if (Bits.allAreClear(state, 131072)) {
         boolean flushed = this.sinkConduit.flush();
         if (Bits.allAreSet(state, 256) && flushed) {
            this.state = state | 512 | 1024 | 2048;
         }

         return flushed;
      } else if (Bits.allAreSet(state, 256)) {
         return this.performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0) != 0L;
      } else {
         return this.performIO(2, NO_BUFFERS, 0, 0, NO_BUFFERS, 0, 0) != 0L;
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return Bits.allAreClear(this.state, 131072) ? this.sinkConduit.transferFrom(src, position, count) : src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Bits.allAreClear(this.state, 131072) ? this.sinkConduit.transferFrom(source, count, throughBuffer) : Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   private static long actualIOResult(long xfer, int goal, boolean flushed, boolean eof) {
      long result = goal == 2 && flushed ? 1L : (goal == 0 && eof && xfer == 0L ? -1L : xfer);
      if (TRACE_SSL) {
         Messages.msg.tracef("returned TLS result %d", result);
      }

      return result;
   }

   private static String decodeGoal(int goal) {
      switch (goal) {
         case 0:
            return "READ";
         case 1:
            return "WRITE";
         case 2:
            return "FLUSH";
         case 3:
            return "WRITE_FINAL";
         default:
            return "UNKNOWN(" + goal + ")";
      }
   }

   private long performIO(int goal, ByteBuffer[] srcs, int srcOff, int srcLen, ByteBuffer[] dsts, int dstOff, int dstLen) throws IOException {
      if (TRACE_SSL) {
         Messages.msg.tracef("performing TLS I/O operation, goal %s, src: %s, dst: %s", decodeGoal(goal), Buffers.debugString(srcs, srcOff, srcLen), Buffers.debugString(dsts, dstOff, dstLen));
      }

      assert srcs == NO_BUFFERS || dsts == NO_BUFFERS;

      int state = this.state;

      assert !Bits.allAreSet(state, 65600);

      if (Bits.allAreSet(state, 1048576)) {
         return 0L;
      } else {
         SSLEngine engine = this.engine;
         ByteBuffer sendBuffer = (ByteBuffer)this.sendBuffer.getResource();
         ByteBuffer receiveBuffer = (ByteBuffer)this.receiveBuffer.getResource();
         ByteBuffer readBuffer = (ByteBuffer)this.readBuffer.getResource();
         ByteBuffer[] realDsts = (ByteBuffer[])Arrays.copyOfRange(dsts, dstOff, dstLen + 1);
         realDsts[dstLen] = readBuffer;
         long remaining = Math.max(Buffers.remaining(srcs, srcOff, srcLen), Buffers.remaining(dsts, dstOff, dstLen));
         boolean wrap = goal == 0 ? Bits.anyAreSet(state, 2097216) : Bits.allAreSet(state, 2097152) || Bits.allAreClear(state, 65536);
         boolean unwrap = !wrap;
         boolean flushed = false;
         boolean eof = false;
         boolean readBlocked = false;
         boolean writeBlocked = false;
         boolean copiedUnwrappedBytes = false;
         boolean wakeupReads = false;
         int rv = false;
         long xfer = 0L;
         if (TRACE_SSL) {
            Messages.msg.trace("TLS perform IO");
         }

         try {
            label4367:
            while(true) {
               if (TRACE_SSL) {
                  Messages.msg.trace("TLS begin IO operation");
               }

               long preRem;
               if (goal == 0 && remaining > 0L && readBuffer.position() > 0) {
                  readBuffer.flip();

                  int rv;
                  try {
                     if (TRACE_SSL) {
                        Messages.msg.tracef("TLS copy unwrapped data from %s to %s", Buffers.debugString(readBuffer), Buffers.debugString(dsts, dstOff, dstLen));
                     }

                     rv = Buffers.copy(dsts, dstOff, dstLen, readBuffer);
                  } finally {
                     readBuffer.compact();
                  }

                  if (rv > 0) {
                     copiedUnwrappedBytes = true;
                     xfer += (long)rv;
                     if ((remaining -= (long)rv) == 0L) {
                        preRem = actualIOResult(xfer, goal, flushed, eof);
                        return preRem;
                     }
                  }
               }

               assert !wrap || !unwrap;

               SSLEngineResult result;
               if (wrap) {
                  if (TRACE_SSL) {
                     Messages.msg.tracef("TLS wrap from %s to %s", Buffers.debugString(srcs, srcOff, srcLen), Buffers.debugString(sendBuffer));
                  }

                  result = engine.wrap(srcs, srcOff, srcLen, sendBuffer);
                  int res;
                  label4361:
                  switch (result.getStatus()) {
                     case BUFFER_UNDERFLOW:
                        assert result.bytesConsumed() == 0;

                        assert result.bytesProduced() == 0;

                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS wrap operation UNDERFLOW");
                        }
                        break;
                     case BUFFER_OVERFLOW:
                        assert result.bytesConsumed() == 0;

                        assert result.bytesProduced() == 0;

                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS wrap operation OVERFLOW");
                        }

                        if (sendBuffer.position() == 0) {
                           throw Messages.msg.wrongBufferExpansion();
                        }

                        sendBuffer.flip();

                        try {
                           while(sendBuffer.hasRemaining()) {
                              if (TRACE_SSL) {
                                 Messages.msg.tracef("TLS wrap operation send %s", Buffers.debugString(sendBuffer));
                              }

                              res = this.sinkConduit.write(sendBuffer);
                              if (res == 0) {
                                 writeBlocked = true;
                                 state &= -32769;

                                 assert goal != 2 || xfer == 0L;

                                 flushed = false;
                                 wrap = false;
                                 break label4361;
                              }
                           }
                        } finally {
                           sendBuffer.compact();
                        }

                        if ((goal == 2 || Bits.allAreSet(state, 2097152)) && (flushed = this.sinkConduit.flush())) {
                           state &= -2097153;
                        }

                        if (goal == 2 && Bits.allAreSet(state, 256)) {
                           state |= 512;
                        }
                        break;
                     case CLOSED:
                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS wrap operation CLOSED");
                        }

                        if (Bits.allAreClear(state, 256) && result.bytesProduced() == 0) {
                           if (goal != 2) {
                              state &= -65601;
                              state |= 3840;
                              ClosedChannelException exception = new ClosedChannelException();

                              try {
                                 this.sinkConduit.truncateWrites();
                              } catch (IOException var93) {
                                 exception.addSuppressed(var93);
                              }

                              throw exception;
                           }

                           wrap = false;
                           if ((goal == 2 || Bits.allAreSet(state, 2097152)) && (flushed = this.sinkConduit.flush())) {
                              state &= -2097153;
                           }
                           break;
                        } else if (Bits.allAreSet(state, 512)) {
                           state |= 1024;
                        }
                     case OK:
                        if (TRACE_SSL) {
                           Messages.msg.tracef("TLS wrap operation OK consumed: %d produced: %d", result.bytesConsumed(), result.bytesProduced());
                        }

                        state &= -65601;
                        res = result.bytesConsumed();
                        if (goal == 0) {
                           assert res == 0;

                           wrap = false;
                           unwrap = true;
                        } else {
                           if (res > 0 || remaining == 0L) {
                              assert remaining != 0L || res == 0;

                              wrap = false;
                           }

                           xfer += (long)res;
                           remaining -= (long)res;
                        }

                        sendBuffer.flip();

                        try {
                           flushed = false;

                           while(sendBuffer.hasRemaining()) {
                              int res = Bits.allAreSet(state, 1024) ? this.sinkConduit.writeFinal(sendBuffer) : this.sinkConduit.write(sendBuffer);
                              if (res == 0) {
                                 writeBlocked = true;
                                 wrap = false;
                                 break;
                              }
                           }
                        } finally {
                           sendBuffer.compact();
                        }

                        if (sendBuffer.position() == 0) {
                           if ((goal == 2 || Bits.allAreSet(state, 2097152)) && (flushed = this.sinkConduit.flush())) {
                              state &= -2097153;
                           }

                           if (Bits.allAreSet(state, 256)) {
                              if (Bits.allAreClear(state, 512)) {
                                 assert sendBuffer.position() == 0;

                                 state |= 512;
                                 if (result.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
                                    state |= 1024;
                                 }
                              }

                              if (Bits.allAreSet(state, 1024)) {
                                 if (goal == 2 || this.sinkConduit.flush()) {
                                    state |= 2048;
                                 }

                                 this.sinkConduit.terminateWrites();
                              }
                           }
                        }
                        break;
                     default:
                        throw Messages.msg.unexpectedWrapResult(result.getStatus());
                  }
               } else {
                  if (!unwrap) {
                     preRem = actualIOResult(xfer, goal, flushed, eof);
                     return preRem;
                  }

                  if (TRACE_SSL) {
                     Messages.msg.tracef("TLS unwrap from %s to %s", Buffers.debugString(receiveBuffer), Buffers.debugString(realDsts, 0, dstLen + 1));
                  }

                  assert realDsts.length == 1 || realDsts[0] == dsts[dstOff];

                  assert realDsts[dstLen] == readBuffer;

                  preRem = Buffers.remaining(dsts, dstOff, dstLen);
                  result = engine.unwrap(receiveBuffer, realDsts, 0, dstLen + 1);
                  long userProduced = preRem - Buffers.remaining(dsts, dstOff, dstLen);
                  int res;
                  switch (result.getStatus()) {
                     case BUFFER_UNDERFLOW:
                        assert result.bytesConsumed() == 0;

                        assert result.bytesProduced() == 0;

                        assert userProduced == 0L;

                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS unwrap operation UNDERFLOW");
                        }

                        receiveBuffer.compact();

                        try {
                           res = this.sourceConduit.read(receiveBuffer);
                           if (TRACE_SSL) {
                              Messages.msg.tracef("TLS unwrap operation read %s", Buffers.debugString(receiveBuffer));
                           }

                           if (res == -1) {
                              state &= -33;
                              engine.closeInbound();
                           } else if (res == 0) {
                              readBlocked = true;
                              state &= -33;
                              unwrap = false;
                           } else if (receiveBuffer.hasRemaining()) {
                              do {
                                 res = this.sourceConduit.read(receiveBuffer);
                              } while(res > 0 && receiveBuffer.hasRemaining());

                              if (res == 0) {
                                 state &= -33;
                              }
                           }
                           break;
                        } finally {
                           receiveBuffer.flip();
                        }
                     case BUFFER_OVERFLOW:
                        assert result.bytesConsumed() == 0;

                        assert result.bytesProduced() == 0;

                        assert userProduced == 0L;

                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS unwrap operation OVERFLOW");
                        }

                        if (!copiedUnwrappedBytes) {
                           long var110 = actualIOResult(xfer, goal, flushed, eof);
                           return var110;
                        }

                        unwrap = false;
                        break;
                     case CLOSED:
                        if (result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                           receiveBuffer.compact();

                           try {
                              res = this.sourceConduit.read(receiveBuffer);
                              if (TRACE_SSL) {
                                 Messages.msg.tracef("TLS unwrap operation read %s", Buffers.debugString(receiveBuffer));
                              }

                              long var34;
                              if (res == -1) {
                                 state &= -33;
                                 engine.closeInbound();
                                 var34 = actualIOResult(xfer, goal, flushed, eof);
                                 return var34;
                              }

                              if (res == 0) {
                                 readBlocked = true;
                                 state &= -33;
                                 unwrap = false;
                                 var34 = actualIOResult(xfer, goal, flushed, eof);
                                 return var34;
                              }

                              if (receiveBuffer.hasRemaining()) {
                                 do {
                                    res = this.sourceConduit.read(receiveBuffer);
                                 } while(res > 0 && receiveBuffer.hasRemaining());

                                 if (res == 0) {
                                    state &= -33;
                                 }
                              }
                           } finally {
                              receiveBuffer.flip();
                           }
                        } else {
                           if (TRACE_SSL) {
                              Messages.msg.trace("TLS unwrap operation CLOSED");
                           }

                           state &= -65601;
                           if (goal == 0) {
                              xfer += userProduced;
                              remaining -= userProduced;
                              state = state & -33 | 2;
                           } else {
                              wakeupReads = true;
                           }

                           eof = true;
                           unwrap = false;
                           if (goal == 2) {
                              wrap = true;
                           }
                        }
                        break;
                     case OK:
                        if (TRACE_SSL) {
                           Messages.msg.tracef("TLS unwrap operation OK consumed: %d produced: %d", result.bytesConsumed(), result.bytesProduced());
                        }

                        if (Bits.allAreClear(state, 32)) {
                           state |= 32;
                        }

                        state &= -65601;
                        if (goal == 0) {
                           xfer += userProduced;
                           remaining -= userProduced;
                        } else {
                           wrap = true;
                           unwrap = false;
                           if (result.bytesProduced() > 0) {
                              wakeupReads = true;
                           }
                        }
                        break;
                     default:
                        throw Messages.msg.unexpectedUnwrapResult(result.getStatus());
                  }
               }

               SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();

               label4243:
               while(true) {
                  switch (handshakeStatus) {
                     case FINISHED:
                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS handshake FINISHED");
                        }

                        this.connection.invokeHandshakeListener();
                     case NOT_HANDSHAKING:
                        if (Bits.allAreSet(state, 256)) {
                           engine.closeOutbound();
                        }
                        continue label4367;
                     case NEED_TASK:
                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS handshake NEED_TASK");
                        }

                        if (xfer != 0L) {
                           preRem = actualIOResult(xfer, goal, flushed, eof);
                           return preRem;
                        }

                        if (!Bits.allAreSet(state, 262144)) {
                           state |= 1048576;
                           ArrayList<Runnable> tasks = new ArrayList(4);

                           while(true) {
                              Runnable task = engine.getDelegatedTask();
                              if (task == null) {
                                 int size = tasks.size();
                                 synchronized(this) {
                                    this.tasks = size;
                                 }

                                 for(int i = 0; i < size; ++i) {
                                    this.getWorker().execute(new TaskWrapper((Runnable)tasks.get(i)));
                                 }

                                 long var109 = actualIOResult(xfer, goal, flushed, eof);
                                 return var109;
                              }

                              tasks.add(task);
                           }
                        }

                        while(true) {
                           Runnable task = engine.getDelegatedTask();
                           if (task == null) {
                              handshakeStatus = engine.getHandshakeStatus();
                              continue label4243;
                           }

                           try {
                              task.run();
                           } catch (Throwable var95) {
                              throw new SSLException("Delegated task threw an exception", var95);
                           }
                        }
                     case NEED_WRAP:
                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS handshake NEED_WRAP");
                        }

                        state |= 2097216;
                        if (writeBlocked) {
                           preRem = actualIOResult(xfer, goal, flushed, eof);
                           return preRem;
                        }

                        wrap = true;
                        unwrap = false;
                        continue label4367;
                     case NEED_UNWRAP:
                        if (TRACE_SSL) {
                           Messages.msg.trace("TLS handshake NEED_UNWRAP");
                        }

                        if (wrap && !flushed && !this.sinkConduit.flush()) {
                           state |= 2097152;
                        }

                        state |= 65536;
                        if (!readBlocked) {
                           wrap = false;
                           unwrap = true;
                           continue label4367;
                        }

                        preRem = actualIOResult(xfer, goal, flushed, eof);
                        return preRem;
                     default:
                        throw Messages.msg.unexpectedHandshakeStatus(result.getHandshakeStatus());
                  }
               }
            }
         } finally {
            this.state = state;
            if (wakeupReads) {
               this.wakeupReads();
            }

         }
      }
   }

   class TaskWrapper implements Runnable {
      private final Runnable task;

      TaskWrapper(Runnable task) {
         this.task = task;
      }

      public void run() {
         boolean var9 = false;

         try {
            var9 = true;
            this.task.run();
            var9 = false;
         } finally {
            if (var9) {
               synchronized(JsseStreamConduit.this) {
                  if (JsseStreamConduit.this.tasks-- == 1) {
                     JsseStreamConduit.this.notifyAll();
                  }

               }
            }
         }

         synchronized(JsseStreamConduit.this) {
            if (JsseStreamConduit.this.tasks-- == 1) {
               JsseStreamConduit.this.notifyAll();
            }

         }
      }
   }
}
