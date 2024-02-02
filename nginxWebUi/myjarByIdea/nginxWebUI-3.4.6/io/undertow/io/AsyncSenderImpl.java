package io.undertow.io;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.xnio.Buffers;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

public class AsyncSenderImpl implements Sender {
   private StreamSinkChannel channel;
   private final HttpServerExchange exchange;
   private PooledByteBuffer[] pooledBuffers = null;
   private FileChannel fileChannel;
   private IoCallback callback;
   private ByteBuffer[] buffer;
   private volatile Thread writeThread;
   private volatile Thread inCallback;
   private ChannelListener<StreamSinkChannel> writeListener;
   private TransferTask transferTask;

   public AsyncSenderImpl(HttpServerExchange exchange) {
      this.exchange = exchange;
   }

   public void send(ByteBuffer buffer, IoCallback callback) {
      this.writeThread = Thread.currentThread();
      if (callback == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
      } else if (!this.exchange.getConnection().isOpen()) {
         this.invokeOnException(callback, new ClosedChannelException());
      } else {
         if (this.exchange.isResponseComplete()) {
            this.invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
         }

         if (this.buffer == null && this.fileChannel == null) {
            long responseContentLength = this.exchange.getResponseContentLength();
            if (responseContentLength > 0L && (long)buffer.remaining() > responseContentLength) {
               this.invokeOnException(callback, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength((long)buffer.remaining(), responseContentLength));
            } else {
               StreamSinkChannel channel = this.channel;
               if (channel == null) {
                  if (callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
                     this.exchange.setResponseContentLength((long)buffer.remaining());
                  }

                  this.channel = channel = this.exchange.getResponseChannel();
                  if (channel == null) {
                     throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
                  }
               }

               this.callback = callback;
               if (this.inCallback == Thread.currentThread()) {
                  this.buffer = new ByteBuffer[]{buffer};
               } else {
                  while(true) {
                     try {
                        if (buffer.remaining() == 0) {
                           callback.onComplete(this.exchange, this);
                           return;
                        }

                        int res = channel.write(buffer);
                        if (res == 0) {
                           this.buffer = new ByteBuffer[]{buffer};
                           this.callback = callback;
                           if (this.writeListener == null) {
                              this.initWriteListener();
                           }

                           channel.getWriteSetter().set(this.writeListener);
                           channel.resumeWrites();
                           return;
                        }

                        if (buffer.hasRemaining()) {
                           continue;
                        }

                        this.invokeOnComplete();
                     } catch (IOException var7) {
                        this.invokeOnException(callback, var7);
                     }

                     return;
                  }
               }
            }
         } else {
            throw UndertowMessages.MESSAGES.dataAlreadyQueued();
         }
      }
   }

   public void send(ByteBuffer[] buffer, IoCallback callback) {
      this.writeThread = Thread.currentThread();
      if (callback == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
      } else if (!this.exchange.getConnection().isOpen()) {
         this.invokeOnException(callback, new ClosedChannelException());
      } else {
         if (this.exchange.isResponseComplete()) {
            this.invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
         }

         if (this.buffer != null) {
            throw UndertowMessages.MESSAGES.dataAlreadyQueued();
         } else {
            this.callback = callback;
            if (this.inCallback == Thread.currentThread()) {
               this.buffer = buffer;
            } else {
               long totalToWrite = Buffers.remaining(buffer);
               long responseContentLength = this.exchange.getResponseContentLength();
               if (responseContentLength > 0L && totalToWrite > responseContentLength) {
                  this.invokeOnException(callback, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(totalToWrite, responseContentLength));
               } else {
                  StreamSinkChannel channel = this.channel;
                  if (channel == null) {
                     if (callback == IoCallback.END_EXCHANGE && responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
                        this.exchange.setResponseContentLength(totalToWrite);
                     }

                     this.channel = channel = this.exchange.getResponseChannel();
                     if (channel == null) {
                        throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
                     }
                  }

                  long total = totalToWrite;
                  long written = 0L;

                  try {
                     do {
                        long res = channel.write(buffer);
                        written += res;
                        if (res == 0L) {
                           this.buffer = buffer;
                           this.callback = callback;
                           if (this.writeListener == null) {
                              this.initWriteListener();
                           }

                           channel.getWriteSetter().set(this.writeListener);
                           channel.resumeWrites();
                           return;
                        }
                     } while(written < total);

                     this.invokeOnComplete();
                  } catch (IOException var14) {
                     this.invokeOnException(callback, var14);
                  }

               }
            }
         }
      }
   }

   public void transferFrom(FileChannel source, IoCallback callback) {
      this.writeThread = Thread.currentThread();
      if (callback == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
      } else if (!this.exchange.getConnection().isOpen()) {
         this.invokeOnException(callback, new ClosedChannelException());
      } else {
         if (this.exchange.isResponseComplete()) {
            this.invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
         }

         if (this.fileChannel == null && this.buffer == null) {
            this.callback = callback;
            this.fileChannel = source;
            if (this.inCallback != Thread.currentThread()) {
               if (this.transferTask == null) {
                  this.transferTask = new TransferTask();
               }

               if (this.exchange.isInIoThread()) {
                  this.exchange.dispatch((Runnable)this.transferTask);
               } else {
                  this.transferTask.run();
               }
            }
         } else {
            throw UndertowMessages.MESSAGES.dataAlreadyQueued();
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
      this.send(data, StandardCharsets.UTF_8, callback);
   }

   public void send(String data, Charset charset, IoCallback callback) {
      this.writeThread = Thread.currentThread();
      if (!this.exchange.getConnection().isOpen()) {
         this.invokeOnException(callback, new ClosedChannelException());
      } else {
         if (this.exchange.isResponseComplete()) {
            this.invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
         }

         ByteBuffer bytes = ByteBuffer.wrap(data.getBytes(charset));
         if (bytes.remaining() == 0) {
            callback.onComplete(this.exchange, this);
         } else {
            int i = 0;

            ByteBuffer[] bufs;
            for(bufs = null; bytes.hasRemaining(); ++i) {
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
               if (bufs == null) {
                  int noBufs = (bytes.remaining() + pooled.getBuffer().remaining() - 1) / pooled.getBuffer().remaining();
                  this.pooledBuffers = new PooledByteBuffer[noBufs];
                  bufs = new ByteBuffer[noBufs];
               }

               this.pooledBuffers[i] = pooled;
               bufs[i] = pooled.getBuffer();
               Buffers.copy(pooled.getBuffer(), bytes);
               pooled.getBuffer().flip();
            }

            this.send(bufs, callback);
         }

      }
   }

   public void send(String data) {
      this.send(data, IoCallback.END_EXCHANGE);
   }

   public void send(String data, Charset charset) {
      this.send(data, charset, IoCallback.END_EXCHANGE);
   }

   public void close(final IoCallback callback) {
      try {
         StreamSinkChannel channel = this.channel;
         if (channel == null) {
            if (this.exchange.getResponseContentLength() == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
               this.exchange.setResponseContentLength(0L);
            }

            this.channel = channel = this.exchange.getResponseChannel();
            if (channel == null) {
               throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
            }
         }

         channel.shutdownWrites();
         if (!channel.flush()) {
            channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
               public void handleEvent(StreamSinkChannel channel) {
                  if (callback != null) {
                     callback.onComplete(AsyncSenderImpl.this.exchange, AsyncSenderImpl.this);
                  }

               }
            }, new ChannelExceptionHandler<StreamSinkChannel>() {
               public void handleException(StreamSinkChannel channel, IOException exception) {
                  try {
                     if (callback != null) {
                        AsyncSenderImpl.this.invokeOnException(callback, exception);
                     }
                  } finally {
                     IoUtils.safeClose((Closeable)channel);
                  }

               }
            }));
            channel.resumeWrites();
         } else if (callback != null) {
            callback.onComplete(this.exchange, this);
         }
      } catch (IOException var3) {
         if (callback != null) {
            this.invokeOnException(callback, var3);
         }
      }

   }

   public void close() {
      this.close((IoCallback)null);
   }

   private void invokeOnComplete() {
      while(true) {
         if (this.pooledBuffers != null) {
            PooledByteBuffer[] var1 = this.pooledBuffers;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               PooledByteBuffer buffer = var1[var3];
               buffer.close();
            }

            this.pooledBuffers = null;
         }

         IoCallback callback = this.callback;
         this.buffer = null;
         this.fileChannel = null;
         this.callback = null;
         this.writeThread = null;
         this.inCallback = Thread.currentThread();

         try {
            callback.onComplete(this.exchange, this);
         } finally {
            this.inCallback = null;
         }

         if (Thread.currentThread() != this.writeThread) {
            return;
         }

         StreamSinkChannel channel = this.channel;
         if (this.buffer != null) {
            long t = Buffers.remaining(this.buffer);
            long total = t;
            long written = 0L;

            try {
               while(true) {
                  long res = channel.write(this.buffer);
                  written += res;
                  if (res == 0L) {
                     if (this.writeListener == null) {
                        this.initWriteListener();
                     }

                     channel.getWriteSetter().set(this.writeListener);
                     channel.resumeWrites();
                     return;
                  }

                  if (written >= total) {
                     break;
                  }
               }
            } catch (IOException var13) {
               this.invokeOnException(callback, var13);
            }
         } else {
            if (this.fileChannel != null) {
               if (this.transferTask == null) {
                  this.transferTask = new TransferTask();
               }

               if (this.transferTask.run(false)) {
                  continue;
               }

               return;
            }

            return;
         }
      }
   }

   private void invokeOnException(IoCallback callback, IOException e) {
      if (this.pooledBuffers != null) {
         PooledByteBuffer[] var3 = this.pooledBuffers;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            PooledByteBuffer buffer = var3[var5];
            buffer.close();
         }

         this.pooledBuffers = null;
      }

      callback.onException(this.exchange, this, e);
   }

   private void initWriteListener() {
      this.writeListener = new ChannelListener<StreamSinkChannel>() {
         public void handleEvent(StreamSinkChannel streamSinkChannel) {
            try {
               long toWrite = Buffers.remaining(AsyncSenderImpl.this.buffer);
               long written = 0L;

               while(written < toWrite) {
                  long res = streamSinkChannel.write(AsyncSenderImpl.this.buffer, 0, AsyncSenderImpl.this.buffer.length);
                  written += res;
                  if (res == 0L) {
                     return;
                  }
               }

               streamSinkChannel.suspendWrites();
               AsyncSenderImpl.this.invokeOnComplete();
            } catch (IOException var8) {
               streamSinkChannel.suspendWrites();
               AsyncSenderImpl.this.invokeOnException(AsyncSenderImpl.this.callback, var8);
            }

         }
      };
   }

   public class TransferTask implements Runnable, ChannelListener<StreamSinkChannel> {
      public boolean run(boolean complete) {
         try {
            FileChannel source = AsyncSenderImpl.this.fileChannel;
            long pos = source.position();
            long size = source.size();
            StreamSinkChannel dest = AsyncSenderImpl.this.channel;
            if (dest == null) {
               if (AsyncSenderImpl.this.callback == IoCallback.END_EXCHANGE && AsyncSenderImpl.this.exchange.getResponseContentLength() == -1L && !AsyncSenderImpl.this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
                  AsyncSenderImpl.this.exchange.setResponseContentLength(size);
               }

               AsyncSenderImpl.this.channel = dest = AsyncSenderImpl.this.exchange.getResponseChannel();
               if (dest == null) {
                  throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
               }
            }

            while(size - pos > 0L) {
               long ret = dest.transferFrom(source, pos, size - pos);
               pos += ret;
               if (ret == 0L) {
                  source.position(pos);
                  dest.getWriteSetter().set(this);
                  dest.resumeWrites();
                  return false;
               }
            }

            if (complete) {
               AsyncSenderImpl.this.invokeOnComplete();
            }
         } catch (IOException var10) {
            AsyncSenderImpl.this.invokeOnException(AsyncSenderImpl.this.callback, var10);
         }

         return true;
      }

      public void handleEvent(StreamSinkChannel channel) {
         channel.suspendWrites();
         channel.getWriteSetter().set((ChannelListener)null);
         AsyncSenderImpl.this.exchange.dispatch((Runnable)this);
      }

      public void run() {
         this.run(true);
      }
   }
}
