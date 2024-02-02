package io.undertow.io;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.xnio.Buffers;
import org.xnio.IoUtils;

public class BlockingSenderImpl implements Sender {
   private final HttpServerExchange exchange;
   private final OutputStream outputStream;
   private volatile Thread inCall;
   private volatile Thread sendThread;
   private ByteBuffer[] next;
   private FileChannel pendingFile;
   private IoCallback queuedCallback;

   public BlockingSenderImpl(HttpServerExchange exchange, OutputStream outputStream) {
      this.exchange = exchange;
      this.outputStream = outputStream;
   }

   public void send(ByteBuffer buffer, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(new ByteBuffer[]{buffer}, callback);
      } else {
         long responseContentLength = this.exchange.getResponseContentLength();
         if (responseContentLength > 0L && (long)buffer.remaining() > responseContentLength) {
            callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength((long)buffer.remaining(), responseContentLength));
         } else {
            if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
               this.exchange.setResponseContentLength((long)buffer.remaining());
            }

            if (this.writeBuffer(buffer, callback)) {
               this.invokeOnComplete(callback);
            }

         }
      }
   }

   public void send(ByteBuffer[] buffer, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(buffer, callback);
      } else {
         long responseContentLength = this.exchange.getResponseContentLength();
         if (responseContentLength > 0L && Buffers.remaining(buffer) > responseContentLength) {
            callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(Buffers.remaining(buffer), responseContentLength));
         } else {
            if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
               this.exchange.setResponseContentLength(Buffers.remaining(buffer));
            }

            if (this.writeBuffer(buffer, callback)) {
               this.invokeOnComplete(callback);
            }
         }
      }
   }

   public void send(ByteBuffer buffer) {
      this.send(buffer, IoCallback.END_EXCHANGE);
   }

   public void send(ByteBuffer[] buffer) {
      this.send(buffer, IoCallback.END_EXCHANGE);
   }

   public void send(String data, IoCallback callback) {
      byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(new ByteBuffer[]{ByteBuffer.wrap(bytes)}, callback);
      } else {
         long responseContentLength = this.exchange.getResponseContentLength();
         if (responseContentLength > 0L && (long)bytes.length > responseContentLength) {
            callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength((long)bytes.length, responseContentLength));
         } else {
            if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
               this.exchange.setResponseContentLength((long)bytes.length);
            }

            try {
               this.outputStream.write(bytes);
               this.invokeOnComplete(callback);
            } catch (IOException var6) {
               callback.onException(this.exchange, this, var6);
            }

         }
      }
   }

   public void send(String data, Charset charset, IoCallback callback) {
      byte[] bytes = data.getBytes(charset);
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(new ByteBuffer[]{ByteBuffer.wrap(bytes)}, callback);
      } else {
         long responseContentLength = this.exchange.getResponseContentLength();
         if (responseContentLength > 0L && (long)bytes.length > responseContentLength) {
            callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength((long)bytes.length, responseContentLength));
         } else {
            if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
               this.exchange.setResponseContentLength((long)bytes.length);
            }

            try {
               this.outputStream.write(bytes);
               this.invokeOnComplete(callback);
            } catch (IOException var7) {
               callback.onException(this.exchange, this, var7);
            }

         }
      }
   }

   public void send(String data) {
      this.send(data, IoCallback.END_EXCHANGE);
   }

   public void send(String data, Charset charset) {
      this.send(data, charset, IoCallback.END_EXCHANGE);
   }

   public void transferFrom(FileChannel source, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(source, callback);
      } else {
         this.performTransfer(source, callback);
         this.invokeOnComplete(callback);
      }
   }

   private void performTransfer(FileChannel source, IoCallback callback) {
      if (this.outputStream instanceof BufferWritableOutputStream) {
         try {
            ((BufferWritableOutputStream)this.outputStream).transferFrom(source);
         } catch (IOException var20) {
            callback.onException(this.exchange, this, var20);
         }
      } else {
         try {
            PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
            Throwable var4 = null;

            try {
               ByteBuffer buffer = pooled.getBuffer();
               long pos = source.position();
               long size = source.size();

               while(size - pos > 0L) {
                  int ret = source.read(buffer);
                  if (ret <= 0) {
                     break;
                  }

                  pos += (long)ret;
                  this.outputStream.write(buffer.array(), buffer.arrayOffset(), ret);
                  buffer.clear();
               }

               if (pos != size) {
                  throw new EOFException("Unexpected EOF reading file");
               }
            } catch (Throwable var21) {
               var4 = var21;
               throw var21;
            } finally {
               if (pooled != null) {
                  if (var4 != null) {
                     try {
                        pooled.close();
                     } catch (Throwable var19) {
                        var4.addSuppressed(var19);
                     }
                  } else {
                     pooled.close();
                  }
               }

            }
         } catch (IOException var23) {
            callback.onException(this.exchange, this, var23);
         }
      }

   }

   public void close(IoCallback callback) {
      try {
         this.outputStream.close();
         this.invokeOnComplete(callback);
      } catch (IOException var3) {
         callback.onException(this.exchange, this, var3);
      }

   }

   public void close() {
      IoUtils.safeClose((Closeable)this.outputStream);
   }

   private boolean writeBuffer(ByteBuffer buffer, IoCallback callback) {
      return this.writeBuffer(new ByteBuffer[]{buffer}, callback);
   }

   private boolean writeBuffer(ByteBuffer[] buffers, IoCallback callback) {
      if (this.outputStream instanceof BufferWritableOutputStream) {
         try {
            ((BufferWritableOutputStream)this.outputStream).write(buffers);
            return true;
         } catch (IOException var24) {
            callback.onException(this.exchange, this, var24);
            return false;
         }
      } else {
         ByteBuffer[] var3 = buffers;
         int var4 = buffers.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer buffer = var3[var5];
            if (buffer.hasArray()) {
               try {
                  this.outputStream.write(buffer.array(), buffer.arrayOffset(), buffer.remaining());
               } catch (IOException var25) {
                  callback.onException(this.exchange, this, var25);
                  return false;
               }
            } else {
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
               Throwable var8 = null;

               while(true) {
                  boolean var11;
                  try {
                     if (!buffer.hasRemaining()) {
                        break;
                     }

                     int toRead = Math.min(buffer.remaining(), pooled.getBuffer().remaining());
                     buffer.get(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), toRead);

                     try {
                        this.outputStream.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), toRead);
                        continue;
                     } catch (IOException var26) {
                        callback.onException(this.exchange, this, var26);
                        var11 = false;
                     }
                  } catch (Throwable var27) {
                     var8 = var27;
                     throw var27;
                  } finally {
                     if (pooled != null) {
                        if (var8 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var23) {
                              var8.addSuppressed(var23);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }

                  return var11;
               }
            }
         }

         return true;
      }
   }

   private void invokeOnComplete(IoCallback callback) {
      this.sendThread = null;
      this.inCall = Thread.currentThread();

      try {
         callback.onComplete(this.exchange, this);
      } finally {
         this.inCall = null;
      }

      if (Thread.currentThread() == this.sendThread) {
         do {
            if (this.next == null && this.pendingFile == null) {
               return;
            }

            ByteBuffer[] next = this.next;
            IoCallback queuedCallback = this.queuedCallback;
            FileChannel file = this.pendingFile;
            this.next = null;
            this.queuedCallback = null;
            this.pendingFile = null;
            if (next != null) {
               ByteBuffer[] var5 = next;
               int var6 = next.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  ByteBuffer buffer = var5[var7];
                  this.writeBuffer(buffer, queuedCallback);
               }
            } else if (file != null) {
               this.performTransfer(file, queuedCallback);
            }

            this.sendThread = null;
            this.inCall = Thread.currentThread();

            try {
               queuedCallback.onComplete(this.exchange, this);
            } finally {
               this.inCall = null;
            }
         } while(Thread.currentThread() == this.sendThread);

      }
   }

   private void queue(ByteBuffer[] byteBuffers, IoCallback ioCallback) {
      if (this.next != null) {
         throw UndertowMessages.MESSAGES.dataAlreadyQueued();
      } else {
         this.next = byteBuffers;
         this.queuedCallback = ioCallback;
      }
   }

   private void queue(FileChannel source, IoCallback ioCallback) {
      if (this.pendingFile != null) {
         throw UndertowMessages.MESSAGES.dataAlreadyQueued();
      } else {
         this.pendingFile = source;
         this.queuedCallback = ioCallback;
      }
   }
}
