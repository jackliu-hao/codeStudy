package io.undertow.io;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;

public class UndertowOutputStream extends OutputStream implements BufferWritableOutputStream {
   private final HttpServerExchange exchange;
   private ByteBuffer buffer;
   private PooledByteBuffer pooledBuffer;
   private StreamSinkChannel channel;
   private int state;
   private long written;
   private final long contentLength;
   private static final int FLAG_CLOSED = 1;
   private static final int FLAG_WRITE_STARTED = 2;
   private static final int MAX_BUFFERS_TO_ALLOCATE = 10;

   public UndertowOutputStream(HttpServerExchange exchange) {
      this.exchange = exchange;
      this.contentLength = exchange.getResponseContentLength();
   }

   public void resetBuffer() {
      if (Bits.anyAreSet(this.state, 2)) {
         throw UndertowMessages.MESSAGES.cannotResetBuffer();
      } else {
         this.buffer = null;
         IoUtils.safeClose((Closeable)this.pooledBuffer);
         this.pooledBuffer = null;
         this.written = 0L;
      }
   }

   public long getBytesWritten() {
      return this.written;
   }

   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b}, 0, 1);
   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (len >= 1) {
         if (this.exchange.isInIoThread()) {
            throw UndertowMessages.MESSAGES.blockingIoFromIOThread();
         } else if (Bits.anyAreSet(this.state, 1)) {
            throw UndertowMessages.MESSAGES.streamIsClosed();
         } else {
            ByteBuffer buffer = this.buffer();
            if ((long)len != this.contentLength - this.written && buffer.remaining() >= len) {
               buffer.put(b, off, len);
               if (buffer.remaining() == 0) {
                  this.writeBufferBlocking(false);
               }
            } else if (buffer.remaining() < len) {
               StreamSinkChannel channel = this.channel;
               if (channel == null) {
                  this.channel = channel = this.exchange.getResponseChannel();
               }

               ByteBufferPool bufferPool = this.exchange.getConnection().getByteBufferPool();
               ByteBuffer[] buffers = new ByteBuffer[11];
               PooledByteBuffer[] pooledBuffers = new PooledByteBuffer[10];
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
                  for(i = 0; i < 10; ++i) {
                     PooledByteBuffer pooled = bufferPool.allocate();
                     pooledBuffers[bufferCount - 1] = pooled;
                     buffers[bufferCount++] = pooled.getBuffer();
                     ByteBuffer cb = pooled.getBuffer();
                     int toWrite = len - bytesWritten;
                     if (toWrite <= cb.remaining()) {
                        cb.put(b, bytesWritten + off, len - bytesWritten);
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

                  while(true) {
                     if (bytesWritten >= len) {
                        buffer.clear();
                        var20 = false;
                        break;
                     }

                     bufferCount = 0;

                     for(i = 0; i < 11; ++i) {
                        ByteBuffer cb = buffers[i];
                        cb.clear();
                        ++bufferCount;
                        int toWrite = len - bytesWritten;
                        if (toWrite <= cb.remaining()) {
                           cb.put(b, bytesWritten + off, len - bytesWritten);
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
                  }
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
            } else {
               buffer.put(b, off, len);
               if (buffer.remaining() == 0) {
                  this.writeBufferBlocking(false);
               }
            }

            this.updateWritten((long)len);
         }
      }
   }

   public void write(ByteBuffer[] buffers) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         int len = 0;
         ByteBuffer[] var3 = buffers;
         int var4 = buffers.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer buf = var3[var5];
            len += buf.remaining();
         }

         if (len >= 1) {
            if (this.written == 0L && (long)len == this.contentLength) {
               if (this.channel == null) {
                  this.channel = this.exchange.getResponseChannel();
               }

               Channels.writeBlocking(this.channel, buffers, 0, buffers.length);
               this.state |= 2;
            } else {
               ByteBuffer buffer = this.buffer();
               if (len < buffer.remaining()) {
                  Buffers.copy(buffer, buffers, 0, buffers.length);
               } else {
                  if (this.channel == null) {
                     this.channel = this.exchange.getResponseChannel();
                  }

                  if (buffer.position() == 0) {
                     Channels.writeBlocking(this.channel, buffers, 0, buffers.length);
                  } else {
                     ByteBuffer[] newBuffers = new ByteBuffer[buffers.length + 1];
                     buffer.flip();
                     newBuffers[0] = buffer;
                     System.arraycopy(buffers, 0, newBuffers, 1, buffers.length);
                     Channels.writeBlocking(this.channel, newBuffers, 0, newBuffers.length);
                     buffer.clear();
                  }

                  this.state |= 2;
               }
            }

            this.updateWritten((long)len);
         }
      }
   }

   public void write(ByteBuffer byteBuffer) throws IOException {
      this.write(new ByteBuffer[]{byteBuffer});
   }

   void updateWritten(long len) throws IOException {
      this.written += len;
      if (this.contentLength != -1L && this.written >= this.contentLength) {
         this.flush();
         this.close();
      }

   }

   public void flush() throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         if (this.buffer != null && this.buffer.position() != 0) {
            this.writeBufferBlocking(false);
         }

         if (this.channel == null) {
            this.channel = this.exchange.getResponseChannel();
         }

         Channels.flushBlocking(this.channel);
      }
   }

   private void writeBufferBlocking(boolean writeFinal) throws IOException {
      if (this.channel == null) {
         this.channel = this.exchange.getResponseChannel();
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
      this.state |= 2;
   }

   public void transferFrom(FileChannel source) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         if (this.buffer != null && this.buffer.position() != 0) {
            this.writeBufferBlocking(false);
         }

         if (this.channel == null) {
            this.channel = this.exchange.getResponseChannel();
         }

         long position = source.position();
         long size = source.size();
         Channels.transferBlocking(this.channel, source, position, size);
         this.updateWritten(size - position);
      }
   }

   public void close() throws IOException {
      if (!Bits.anyAreSet(this.state, 1)) {
         try {
            this.state |= 1;
            if (Bits.anyAreClear(this.state, 2) && this.channel == null && !isHeadRequestWithContentLength(this.exchange)) {
               if (this.buffer == null) {
                  this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
               } else {
                  this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + this.buffer.position());
               }
            }

            if (this.buffer != null) {
               this.writeBufferBlocking(true);
            }

            if (this.channel == null) {
               this.channel = this.exchange.getResponseChannel();
            }

            if (this.channel == null) {
               return;
            }

            StreamSinkChannel channel = this.channel;
            channel.shutdownWrites();
            Channels.flushBlocking(channel);
         } finally {
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.buffer = null;
            } else {
               this.buffer = null;
            }

         }

      }
   }

   private static boolean isHeadRequestWithContentLength(HttpServerExchange exchange) {
      return Methods.HEAD.equals(exchange.getRequestMethod()) && exchange.getResponseHeaders().contains(Headers.CONTENT_LENGTH);
   }

   private ByteBuffer buffer() {
      ByteBuffer buffer = this.buffer;
      if (buffer != null) {
         return buffer;
      } else {
         this.pooledBuffer = this.exchange.getConnection().getByteBufferPool().allocate();
         this.buffer = this.pooledBuffer.getBuffer();
         return this.buffer;
      }
   }
}
