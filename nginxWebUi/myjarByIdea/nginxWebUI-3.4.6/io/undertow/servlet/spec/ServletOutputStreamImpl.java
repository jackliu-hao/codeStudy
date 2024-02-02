package io.undertow.servlet.spec;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.io.BufferWritableOutputStream;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.Headers;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.servlet.DispatcherType;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.WriteListener;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;

public class ServletOutputStreamImpl extends ServletOutputStream implements BufferWritableOutputStream {
   private final ServletRequestContext servletRequestContext;
   private PooledByteBuffer pooledBuffer;
   private ByteBuffer buffer;
   private Integer bufferSize;
   private StreamSinkChannel channel;
   private long written;
   private volatile int state;
   private volatile boolean asyncIoStarted;
   private AsyncContextImpl asyncContext;
   private WriteListener listener;
   private WriteChannelListener internalListener;
   private ByteBuffer[] buffersToWrite;
   private FileChannel pendingFile;
   private static final int FLAG_CLOSED = 1;
   private static final int FLAG_WRITE_STARTED = 2;
   private static final int FLAG_READY = 4;
   private static final int FLAG_DELEGATE_SHUTDOWN = 8;
   private static final int FLAG_IN_CALLBACK = 16;
   private static final int MAX_BUFFERS_TO_ALLOCATE = 6;
   private static final AtomicIntegerFieldUpdater<ServletOutputStreamImpl> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ServletOutputStreamImpl.class, "state");

   public ServletOutputStreamImpl(ServletRequestContext servletRequestContext) {
      this.servletRequestContext = servletRequestContext;
   }

   public ServletOutputStreamImpl(ServletRequestContext servletRequestContext, int bufferSize) {
      this.bufferSize = bufferSize;
      this.servletRequestContext = servletRequestContext;
   }

   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b}, 0, 1);
   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (!Bits.anyAreSet(this.state, 1) && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         if (len >= 1) {
            if (this.listener == null) {
               ByteBuffer buffer = this.buffer();
               if (buffer.remaining() < len) {
                  this.writeTooLargeForBuffer(b, off, len, buffer);
               } else {
                  buffer.put(b, off, len);
                  if (buffer.remaining() == 0) {
                     this.writeBufferBlocking(false);
                  }
               }

               this.updateWritten((long)len);
            } else {
               this.writeAsync(b, off, len);
            }

         }
      } else {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      }
   }

   private void writeTooLargeForBuffer(byte[] b, int off, int len, ByteBuffer buffer) throws IOException {
      StreamSinkChannel channel = this.channel;
      if (channel == null) {
         this.channel = channel = this.servletRequestContext.getExchange().getResponseChannel();
      }

      ByteBufferPool bufferPool = this.servletRequestContext.getExchange().getConnection().getByteBufferPool();
      ByteBuffer[] buffers = new ByteBuffer[7];
      PooledByteBuffer[] pooledBuffers = new PooledByteBuffer[6];
      boolean var20 = false;

      int bytesWritten;
      try {
         var20 = true;
         buffers[0] = buffer;
         bytesWritten = 0;
         int rem = buffer.remaining();
         buffer.put(b, bytesWritten + off, rem);
         buffer.flip();
         bytesWritten += rem;
         int bufferCount = 1;

         int i;
         for(i = 0; i < 6; ++i) {
            PooledByteBuffer pooled = bufferPool.allocate();
            pooledBuffers[bufferCount - 1] = pooled;
            buffers[bufferCount++] = pooled.getBuffer();
            ByteBuffer cb = pooled.getBuffer();
            int toWrite = len - bytesWritten;
            if (toWrite <= cb.remaining()) {
               cb.put(b, bytesWritten + off, toWrite);
               bytesWritten = len;
               cb.flip();
               break;
            }

            rem = cb.remaining();
            cb.put(b, bytesWritten + off, rem);
            cb.flip();
            bytesWritten += rem;
         }

         Channels.writeBlocking(channel, buffers, 0, bufferCount);

         for(; bytesWritten < len; Channels.writeBlocking(channel, buffers, 0, bufferCount)) {
            bufferCount = 0;

            for(i = 0; i < 7; ++i) {
               ByteBuffer cb = buffers[i];
               cb.clear();
               ++bufferCount;
               int toWrite = len - bytesWritten;
               if (toWrite <= cb.remaining()) {
                  cb.put(b, bytesWritten + off, toWrite);
                  bytesWritten = len;
                  cb.flip();
                  break;
               }

               rem = cb.remaining();
               cb.put(b, bytesWritten + off, rem);
               cb.flip();
               bytesWritten += rem;
            }
         }

         buffer.clear();
         var20 = false;
      } finally {
         if (var20) {
            for(int i = 0; i < pooledBuffers.length; ++i) {
               PooledByteBuffer p = pooledBuffers[i];
               if (p == null) {
                  break;
               }

               p.close();
            }

         }
      }

      for(bytesWritten = 0; bytesWritten < pooledBuffers.length; ++bytesWritten) {
         PooledByteBuffer p = pooledBuffers[bytesWritten];
         if (p == null) {
            break;
         }

         p.close();
      }

   }

   private void writeAsync(byte[] b, int off, int len) throws IOException {
      if (Bits.anyAreClear(this.state, 4)) {
         throw UndertowServletMessages.MESSAGES.streamNotReady();
      } else {
         try {
            ByteBuffer buffer = this.buffer();
            if (buffer.remaining() > len) {
               buffer.put(b, off, len);
            } else {
               buffer.flip();
               ByteBuffer userBuffer = ByteBuffer.wrap(b, off, len);
               ByteBuffer[] bufs = new ByteBuffer[]{buffer, userBuffer};
               long toWrite = Buffers.remaining(bufs);
               long written = 0L;
               this.createChannel();
               this.setFlags(2);

               do {
                  long res = this.channel.write(bufs);
                  written += res;
                  if (res == 0L) {
                     ByteBuffer copy = ByteBuffer.allocate(userBuffer.remaining());
                     copy.put(userBuffer);
                     copy.flip();
                     this.buffersToWrite = new ByteBuffer[]{buffer, copy};
                     this.clearFlags(4);
                     return;
                  }
               } while(written < toWrite);

               buffer.clear();
            }
         } finally {
            this.updateWrittenAsync((long)len);
         }
      }
   }

   public void write(ByteBuffer[] buffers) throws IOException {
      if (!Bits.anyAreSet(this.state, 1) && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         int len = 0;
         ByteBuffer[] var3 = buffers;
         int var4 = buffers.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer buf = var3[var5];
            len += buf.remaining();
         }

         if (len >= 1) {
            ByteBuffer buffer;
            ByteBuffer[] newBuffers;
            if (this.listener == null) {
               if (this.written == 0L && (long)len == this.servletRequestContext.getOriginalResponse().getContentLength()) {
                  if (this.channel == null) {
                     this.channel = this.servletRequestContext.getExchange().getResponseChannel();
                  }

                  Channels.writeBlocking(this.channel, buffers, 0, buffers.length);
                  this.setFlags(2);
               } else {
                  buffer = this.buffer();
                  if (len < buffer.remaining()) {
                     Buffers.copy(buffer, buffers, 0, buffers.length);
                  } else {
                     if (this.channel == null) {
                        this.channel = this.servletRequestContext.getExchange().getResponseChannel();
                     }

                     if (buffer.position() == 0) {
                        Channels.writeBlocking(this.channel, buffers, 0, buffers.length);
                     } else {
                        newBuffers = new ByteBuffer[buffers.length + 1];
                        buffer.flip();
                        newBuffers[0] = buffer;
                        System.arraycopy(buffers, 0, newBuffers, 1, buffers.length);
                        Channels.writeBlocking(this.channel, newBuffers, 0, newBuffers.length);
                        buffer.clear();
                     }

                     this.setFlags(2);
                  }
               }

               this.updateWritten((long)len);
            } else if (Bits.anyAreClear(this.state, 4)) {
               throw UndertowServletMessages.MESSAGES.streamNotReady();
            } else {
               try {
                  buffer = this.buffer();
                  if (buffer.remaining() > len) {
                     Buffers.copy(buffer, buffers, 0, buffers.length);
                  } else {
                     newBuffers = new ByteBuffer[buffers.length + 1];
                     buffer.flip();
                     newBuffers[0] = buffer;
                     System.arraycopy(buffers, 0, newBuffers, 1, buffers.length);
                     long toWrite = Buffers.remaining(newBuffers);
                     long written = 0L;
                     this.createChannel();
                     this.setFlags(2);

                     do {
                        long res = this.channel.write(newBuffers);
                        written += res;
                        if (res == 0L) {
                           ByteBuffer copy = ByteBuffer.allocate((int)Buffers.remaining(buffers));
                           Buffers.copy(copy, buffers, 0, buffers.length);
                           copy.flip();
                           this.buffersToWrite = new ByteBuffer[]{buffer, copy};
                           this.clearFlags(4);
                           this.channel.resumeWrites();
                           return;
                        }
                     } while(written < toWrite);

                     buffer.clear();
                  }
               } finally {
                  this.updateWrittenAsync((long)len);
               }
            }
         }
      } else {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      }
   }

   public void write(ByteBuffer byteBuffer) throws IOException {
      this.write(new ByteBuffer[]{byteBuffer});
   }

   void updateWritten(long len) throws IOException {
      this.written += len;
      long contentLength = this.servletRequestContext.getOriginalResponse().getContentLength();
      if (contentLength != -1L && this.written >= contentLength) {
         this.close();
      }

   }

   void updateWrittenAsync(long len) throws IOException {
      this.written += len;
      long contentLength = this.servletRequestContext.getOriginalResponse().getContentLength();
      if (contentLength != -1L && this.written >= contentLength) {
         this.setFlags(1);
         if (this.buffersToWrite == null && this.pendingFile == null && this.flushBufferAsync(true)) {
            this.channel.shutdownWrites();
            this.setFlags(8);
            this.channel.flush();
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.buffer = null;
               this.pooledBuffer = null;
            }
         }
      }

   }

   private boolean flushBufferAsync(boolean writeFinal) throws IOException {
      ByteBuffer[] bufs = this.buffersToWrite;
      if (bufs == null) {
         ByteBuffer buffer = this.buffer;
         if (buffer == null || buffer.position() == 0) {
            return true;
         }

         buffer.flip();
         bufs = new ByteBuffer[]{buffer};
      }

      long toWrite = Buffers.remaining(bufs);
      if (toWrite == 0L) {
         this.buffer.clear();
         return true;
      } else {
         this.setFlags(2);
         this.createChannel();
         long written = 0L;

         do {
            long res;
            if (writeFinal) {
               res = this.channel.writeFinal(bufs);
            } else {
               res = this.channel.write(bufs);
            }

            written += res;
            if (res == 0L) {
               this.clearFlags(4);
               this.buffersToWrite = bufs;
               this.channel.resumeWrites();
               return false;
            }
         } while(written < toWrite);

         this.buffer.clear();
         return true;
      }
   }

   ByteBuffer underlyingBuffer() {
      return Bits.anyAreSet(this.state, 1) ? null : this.buffer();
   }

   public void flush() throws IOException {
      if (this.servletRequestContext.getOriginalRequest().getDispatcherType() != DispatcherType.INCLUDE && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         if (this.servletRequestContext.getDeployment().getDeploymentInfo().isIgnoreFlush() && this.servletRequestContext.getExchange().isRequestComplete() && this.servletRequestContext.getOriginalResponse().getHeader("Transfer-Encoding") == null) {
            this.servletRequestContext.getOriginalResponse().setIgnoredFlushPerformed(true);
         } else {
            try {
               this.flushInternal();
            } catch (IOException var3) {
               HttpServletRequestImpl request = this.servletRequestContext.getOriginalRequest();
               if (request.isAsyncStarted() || request.getDispatcherType() == DispatcherType.ASYNC) {
                  this.servletRequestContext.getExchange().unDispatch();
                  this.servletRequestContext.getOriginalRequest().getAsyncContextInternal().handleError(var3);
                  throw var3;
               }
            }

         }
      }
   }

   public void flushInternal() throws IOException {
      if (this.listener == null) {
         if (Bits.anyAreSet(this.state, 1)) {
            return;
         }

         if (this.buffer != null && this.buffer.position() != 0) {
            this.writeBufferBlocking(false);
         }

         if (this.channel == null) {
            this.channel = this.servletRequestContext.getExchange().getResponseChannel();
         }

         Channels.flushBlocking(this.channel);
      } else {
         if (Bits.anyAreClear(this.state, 4)) {
            return;
         }

         this.createChannel();
         if (this.buffer == null || this.buffer.position() == 0) {
            this.channel.flush();
            return;
         }

         this.setFlags(2);
         this.buffer.flip();

         long res;
         do {
            res = (long)this.channel.write(this.buffer);
         } while(this.buffer.hasRemaining() && res != 0L);

         if (!this.buffer.hasRemaining()) {
            this.channel.flush();
         }

         this.buffer.compact();
      }

   }

   public void transferFrom(FileChannel source) throws IOException {
      if (!Bits.anyAreSet(this.state, 1) && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         long pos;
         long size;
         if (this.listener == null) {
            if (this.buffer != null && this.buffer.position() != 0) {
               this.writeBufferBlocking(false);
            }

            if (this.channel == null) {
               this.channel = this.servletRequestContext.getExchange().getResponseChannel();
            }

            pos = source.position();
            size = source.size() - pos;
            Channels.transferBlocking(this.channel, source, pos, size);
            this.updateWritten(size);
         } else {
            this.setFlags(2);
            this.createChannel();
            pos = 0L;

            try {
               size = source.size();

               long ret;
               for(pos = source.position(); size - pos > 0L; pos += ret) {
                  ret = this.channel.transferFrom(this.pendingFile, pos, size - pos);
                  if (ret <= 0L) {
                     this.clearFlags(4);
                     this.pendingFile = source;
                     source.position(pos);
                     this.channel.resumeWrites();
                     return;
                  }
               }

            } finally {
               this.updateWrittenAsync(pos - source.position());
            }
         }
      } else {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      }
   }

   private void writeBufferBlocking(boolean writeFinal) throws IOException {
      if (this.channel == null) {
         this.channel = this.servletRequestContext.getExchange().getResponseChannel();
      }

      this.buffer.flip();

      while(this.buffer.hasRemaining()) {
         if (writeFinal) {
            this.channel.writeFinal(this.buffer);
         } else {
            this.channel.write(this.buffer);
         }

         if (this.buffer.hasRemaining()) {
            this.channel.awaitWritable();
         }
      }

      this.buffer.clear();
      this.setFlags(2);
   }

   public void close() throws IOException {
      if (this.servletRequestContext.getOriginalRequest().getDispatcherType() != DispatcherType.INCLUDE && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         if (this.listener == null) {
            if (Bits.anyAreSet(this.state, 1)) {
               return;
            }

            this.setFlags(1);
            this.clearFlags(4);
            if (Bits.allAreClear(this.state, 2) && this.channel == null && this.servletRequestContext.getOriginalResponse().getHeader("Content-Length") == null && this.servletRequestContext.getOriginalResponse().getHeader("Transfer-Encoding") == null && this.servletRequestContext.getExchange().getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER) == null && this.servletRequestContext.getExchange().getAttachment(HttpAttachments.RESPONSE_TRAILERS) == null) {
               if (this.buffer == null) {
                  this.servletRequestContext.getExchange().getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
               } else {
                  this.servletRequestContext.getExchange().getResponseHeaders().put(Headers.CONTENT_LENGTH, Integer.toString(this.buffer.position()));
               }
            }

            try {
               if (this.buffer != null) {
                  this.writeBufferBlocking(true);
               }

               if (this.channel == null) {
                  this.channel = this.servletRequestContext.getExchange().getResponseChannel();
               }

               this.setFlags(8);
               StreamSinkChannel channel = this.channel;
               if (channel != null) {
                  channel.shutdownWrites();
                  Channels.flushBlocking(channel);
               }
            } catch (RuntimeException | Error | IOException var5) {
               IoUtils.safeClose((Closeable)this.channel);
               throw var5;
            } finally {
               if (this.pooledBuffer != null) {
                  this.pooledBuffer.close();
                  this.buffer = null;
               } else {
                  this.buffer = null;
               }

            }
         } else {
            this.closeAsync();
         }

      }
   }

   public void closeAsync() throws IOException {
      if (!Bits.anyAreSet(this.state, 1) && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         if (!this.servletRequestContext.getExchange().isInIoThread()) {
            this.servletRequestContext.getExchange().getIoThread().execute(new Runnable() {
               public void run() {
                  try {
                     ServletOutputStreamImpl.this.closeAsync();
                  } catch (IOException var2) {
                     UndertowLogger.REQUEST_IO_LOGGER.closeAsyncFailed(var2);
                  }

               }
            });
         } else {
            try {
               this.setFlags(1);
               this.clearFlags(4);
               if (Bits.allAreClear(this.state, 2) && this.channel == null && this.servletRequestContext.getOriginalResponse().getHeader("Transfer-Encoding") == null) {
                  if (this.buffer == null) {
                     this.servletRequestContext.getOriginalResponse().setHeader(Headers.CONTENT_LENGTH, "0");
                  } else {
                     this.servletRequestContext.getOriginalResponse().setHeader(Headers.CONTENT_LENGTH, Integer.toString(this.buffer.position()));
                  }
               }

               this.createChannel();
               if (this.buffer != null) {
                  if (!this.flushBufferAsync(true)) {
                     return;
                  }

                  if (this.pooledBuffer != null) {
                     this.pooledBuffer.close();
                     this.buffer = null;
                  } else {
                     this.buffer = null;
                  }
               }

               this.channel.shutdownWrites();
               this.setFlags(8);
               if (!this.channel.flush()) {
                  this.channel.resumeWrites();
               }

            } catch (RuntimeException | Error | IOException var2) {
               if (this.pooledBuffer != null) {
                  this.pooledBuffer.close();
                  this.pooledBuffer = null;
                  this.buffer = null;
               }

               throw var2;
            }
         }
      }
   }

   private void createChannel() {
      if (this.channel == null) {
         this.channel = this.servletRequestContext.getExchange().getResponseChannel();
         if (this.internalListener != null) {
            this.channel.getWriteSetter().set(this.internalListener);
         }
      }

   }

   private ByteBuffer buffer() {
      ByteBuffer buffer = this.buffer;
      if (buffer != null) {
         return buffer;
      } else if (this.bufferSize != null) {
         this.buffer = ByteBuffer.allocateDirect(this.bufferSize);
         return this.buffer;
      } else {
         this.pooledBuffer = this.servletRequestContext.getExchange().getConnection().getByteBufferPool().allocate();
         this.buffer = this.pooledBuffer.getBuffer();
         return this.buffer;
      }
   }

   public void resetBuffer() {
      if (Bits.allAreClear(this.state, 2)) {
         if (this.pooledBuffer != null) {
            this.pooledBuffer.close();
            this.pooledBuffer = null;
         }

         this.buffer = null;
         this.written = 0L;
      } else {
         throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
      }
   }

   public void setBufferSize(int size) {
      if (this.buffer == null && !this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
         this.bufferSize = size;
      } else {
         throw UndertowServletMessages.MESSAGES.contentHasBeenWritten();
      }
   }

   public boolean isClosed() {
      return Bits.anyAreSet(this.state, 1);
   }

   public boolean isReady() {
      if (this.listener == null) {
         throw UndertowServletMessages.MESSAGES.streamNotInAsyncMode();
      } else if (!this.asyncIoStarted) {
         return false;
      } else if (!Bits.anyAreSet(this.state, 4)) {
         if (this.channel != null) {
            this.channel.resumeWrites();
         }

         return false;
      } else {
         return true;
      }
   }

   public void setWriteListener(WriteListener writeListener) {
      if (writeListener == null) {
         throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
      } else if (this.listener != null) {
         throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
      } else {
         ServletRequest servletRequest = this.servletRequestContext.getOriginalRequest();
         if (!servletRequest.isAsyncStarted()) {
            throw UndertowServletMessages.MESSAGES.asyncNotStarted();
         } else {
            this.asyncContext = (AsyncContextImpl)servletRequest.getAsyncContext();
            this.listener = writeListener;
            this.internalListener = new WriteChannelListener();
            if (this.channel != null) {
               this.channel.getWriteSetter().set(this.internalListener);
            }

            this.asyncContext.addAsyncTask(new Runnable() {
               public void run() {
                  ServletOutputStreamImpl.this.asyncIoStarted = true;
                  if (ServletOutputStreamImpl.this.channel == null) {
                     ServletOutputStreamImpl.this.servletRequestContext.getExchange().getIoThread().execute(new Runnable() {
                        public void run() {
                           ServletOutputStreamImpl.this.internalListener.handleEvent((StreamSinkChannel)null);
                        }
                     });
                  } else {
                     ServletOutputStreamImpl.this.channel.resumeWrites();
                  }

               }
            });
         }
      }
   }

   ServletRequestContext getServletRequestContext() {
      return this.servletRequestContext;
   }

   private void setFlags(int flags) {
      int old;
      do {
         old = this.state;
      } while(!stateUpdater.compareAndSet(this, old, old | flags));

   }

   private void clearFlags(int flags) {
      int old;
      do {
         old = this.state;
      } while(!stateUpdater.compareAndSet(this, old, old & ~flags));

   }

   private class WriteChannelListener implements ChannelListener<StreamSinkChannel> {
      private WriteChannelListener() {
      }

      public void handleEvent(StreamSinkChannel aChannel) {
         if (Bits.anyAreSet(ServletOutputStreamImpl.this.state, 8)) {
            try {
               ServletOutputStreamImpl.this.channel.flush();
            } catch (Throwable var16) {
               this.handleError(var16);
            }
         } else {
            long size;
            long pos;
            long ret;
            if (ServletOutputStreamImpl.this.buffersToWrite != null) {
               size = Buffers.remaining(ServletOutputStreamImpl.this.buffersToWrite);
               pos = 0L;
               if (size > 0L) {
                  do {
                     try {
                        ret = ServletOutputStreamImpl.this.channel.write(ServletOutputStreamImpl.this.buffersToWrite);
                        pos += ret;
                        if (ret == 0L) {
                           return;
                        }
                     } catch (Throwable var20) {
                        this.handleError(var20);
                        return;
                     }
                  } while(pos < size);
               }

               ServletOutputStreamImpl.this.buffersToWrite = null;
               ServletOutputStreamImpl.this.buffer.clear();
            }

            if (ServletOutputStreamImpl.this.pendingFile != null) {
               try {
                  size = ServletOutputStreamImpl.this.pendingFile.size();

                  for(pos = ServletOutputStreamImpl.this.pendingFile.position(); size - pos > 0L; pos += ret) {
                     ret = ServletOutputStreamImpl.this.channel.transferFrom(ServletOutputStreamImpl.this.pendingFile, pos, size - pos);
                     if (ret <= 0L) {
                        ServletOutputStreamImpl.this.pendingFile.position(pos);
                        return;
                     }
                  }

                  ServletOutputStreamImpl.this.pendingFile = null;
               } catch (Throwable var21) {
                  this.handleError(var21);
                  return;
               }
            }

            if (Bits.anyAreSet(ServletOutputStreamImpl.this.state, 1)) {
               try {
                  if (ServletOutputStreamImpl.this.pooledBuffer != null) {
                     ServletOutputStreamImpl.this.pooledBuffer.close();
                     ServletOutputStreamImpl.this.buffer = null;
                  } else {
                     ServletOutputStreamImpl.this.buffer = null;
                  }

                  ServletOutputStreamImpl.this.channel.shutdownWrites();
                  ServletOutputStreamImpl.this.setFlags(8);
                  ServletOutputStreamImpl.this.channel.flush();
               } catch (Throwable var19) {
                  this.handleError(var19);
                  return;
               }
            } else {
               if (ServletOutputStreamImpl.this.asyncContext.isDispatched()) {
                  ServletOutputStreamImpl.this.channel.suspendWrites();
                  return;
               }

               ServletOutputStreamImpl.this.setFlags(4);

               try {
                  ServletOutputStreamImpl.this.setFlags(16);
                  if (ServletOutputStreamImpl.this.channel != null) {
                     ServletOutputStreamImpl.this.channel.suspendWrites();
                  }

                  ServletOutputStreamImpl.this.servletRequestContext.getCurrentServletContext().invokeOnWritePossible(ServletOutputStreamImpl.this.servletRequestContext.getExchange(), ServletOutputStreamImpl.this.listener);
               } catch (Throwable var17) {
                  IoUtils.safeClose((Closeable)ServletOutputStreamImpl.this.channel);
               } finally {
                  ServletOutputStreamImpl.this.clearFlags(16);
               }
            }

         }
      }

      private void handleError(final Throwable t) {
         try {
            ServletOutputStreamImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(ServletOutputStreamImpl.this.servletRequestContext.getExchange(), new Runnable() {
               public void run() {
                  ServletOutputStreamImpl.this.listener.onError(t);
               }
            });
         } finally {
            IoUtils.safeClose(ServletOutputStreamImpl.this.channel, ServletOutputStreamImpl.this.servletRequestContext.getExchange().getConnection());
            if (ServletOutputStreamImpl.this.pooledBuffer != null) {
               ServletOutputStreamImpl.this.pooledBuffer.close();
               ServletOutputStreamImpl.this.pooledBuffer = null;
               ServletOutputStreamImpl.this.buffer = null;
            }

         }

      }

      // $FF: synthetic method
      WriteChannelListener(Object x1) {
         this();
      }
   }
}
