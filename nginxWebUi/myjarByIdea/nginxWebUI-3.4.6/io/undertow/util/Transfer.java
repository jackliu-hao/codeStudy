package io.undertow.util;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public class Transfer {
   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, ByteBufferPool pool) {
      if (pool == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("pool");
      } else {
         PooledByteBuffer allocated = pool.allocate();
         boolean free = true;

         try {
            ByteBuffer buffer = allocated.getBuffer();

            while(true) {
               long read;
               try {
                  read = (long)source.read(buffer);
                  buffer.flip();
               } catch (IOException var19) {
                  ChannelListeners.invokeChannelExceptionHandler(source, readExceptionHandler, var19);
                  return;
               }

               if (read != 0L || buffer.hasRemaining()) {
                  if (read == -1L && !buffer.hasRemaining()) {
                     done(source, sink, sourceListener, sinkListener);
                     return;
                  }

                  while(buffer.hasRemaining()) {
                     int res;
                     try {
                        res = sink.write(buffer);
                     } catch (IOException var18) {
                        ChannelListeners.invokeChannelExceptionHandler(sink, writeExceptionHandler, var18);
                        return;
                     }

                     if (res == 0) {
                        break;
                     }
                  }

                  if (!buffer.hasRemaining()) {
                     buffer.clear();
                     continue;
                  }
               }

               PooledByteBuffer current = null;
               if (buffer.hasRemaining()) {
                  current = allocated;
                  free = false;
               }

               TransferListener<I, O> listener = new TransferListener(pool, current, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, read == -1L);
               sink.getWriteSetter().set(listener);
               source.getReadSetter().set(listener);
               if (current == null || buffer.capacity() != buffer.remaining()) {
                  source.resumeReads();
               }

               if (current != null) {
                  sink.resumeWrites();
               }

               return;
            }
         } finally {
            if (free) {
               allocated.close();
            }

         }
      }
   }

   private static <I extends StreamSourceChannel, O extends StreamSinkChannel> void done(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener) {
      Channels.setReadListener(source, sourceListener);
      if (sourceListener == null) {
         source.suspendReads();
      } else {
         source.wakeupReads();
      }

      Channels.setWriteListener(sink, sinkListener);
      if (sinkListener == null) {
         sink.suspendWrites();
      } else {
         sink.wakeupWrites();
      }

   }

   static final class TransferListener<I extends StreamSourceChannel, O extends StreamSinkChannel> implements ChannelListener<Channel> {
      private PooledByteBuffer pooledBuffer;
      private final ByteBufferPool pool;
      private final I source;
      private final O sink;
      private final ChannelListener<? super I> sourceListener;
      private final ChannelListener<? super O> sinkListener;
      private final ChannelExceptionHandler<? super O> writeExceptionHandler;
      private final ChannelExceptionHandler<? super I> readExceptionHandler;
      private boolean sourceDone;
      private boolean done = false;

      TransferListener(ByteBufferPool pool, PooledByteBuffer pooledBuffer, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super O> writeExceptionHandler, ChannelExceptionHandler<? super I> readExceptionHandler, boolean sourceDone) {
         this.pool = pool;
         this.pooledBuffer = pooledBuffer;
         this.source = source;
         this.sink = sink;
         this.sourceListener = sourceListener;
         this.sinkListener = sinkListener;
         this.writeExceptionHandler = writeExceptionHandler;
         this.readExceptionHandler = readExceptionHandler;
         this.sourceDone = sourceDone;
      }

      public void handleEvent(Channel channel) {
         if (this.done) {
            if (channel instanceof StreamSinkChannel) {
               ((StreamSinkChannel)channel).suspendWrites();
            } else if (channel instanceof StreamSourceChannel) {
               ((StreamSourceChannel)channel).suspendReads();
            }

         } else {
            boolean noWrite = false;
            if (this.pooledBuffer == null) {
               this.pooledBuffer = this.pool.allocate();
               noWrite = true;
            } else if (channel instanceof StreamSourceChannel) {
               noWrite = true;
               this.pooledBuffer.getBuffer().compact();
            }

            ByteBuffer buffer = this.pooledBuffer.getBuffer();

            try {
               while(true) {
                  boolean writeFailed = false;
                  if (!noWrite) {
                     while(buffer.hasRemaining()) {
                        int res;
                        try {
                           res = this.sink.write(buffer);
                        } catch (IOException var14) {
                           this.pooledBuffer.close();
                           this.pooledBuffer = null;
                           this.done = true;
                           ChannelListeners.invokeChannelExceptionHandler(this.sink, this.writeExceptionHandler, var14);
                           return;
                        }

                        if (res == 0) {
                           writeFailed = true;
                           break;
                        }
                     }

                     if (this.sourceDone && !buffer.hasRemaining()) {
                        this.done = true;
                        Transfer.done(this.source, this.sink, this.sourceListener, this.sinkListener);
                        return;
                     }

                     buffer.compact();
                  }

                  noWrite = false;
                  if (buffer.hasRemaining() && !this.sourceDone) {
                     long read;
                     try {
                        read = (long)this.source.read(buffer);
                        buffer.flip();
                     } catch (IOException var13) {
                        this.pooledBuffer.close();
                        this.pooledBuffer = null;
                        this.done = true;
                        ChannelListeners.invokeChannelExceptionHandler(this.source, this.readExceptionHandler, var13);
                        return;
                     }

                     if (read != 0L) {
                        if (read != -1L) {
                           continue;
                        }

                        this.sourceDone = true;
                        if (buffer.hasRemaining()) {
                           continue;
                        }

                        this.done = true;
                        Transfer.done(this.source, this.sink, this.sourceListener, this.sinkListener);
                        return;
                     }
                  } else {
                     buffer.flip();
                     if (!writeFailed) {
                        continue;
                     }
                  }

                  if (!buffer.hasRemaining()) {
                     this.sink.suspendWrites();
                  } else if (!this.sink.isWriteResumed()) {
                     this.sink.resumeWrites();
                  }

                  if (buffer.remaining() == buffer.capacity()) {
                     this.source.suspendReads();
                  } else if (!this.source.isReadResumed()) {
                     this.source.resumeReads();
                     return;
                  }

                  return;
               }
            } finally {
               if (this.pooledBuffer != null && !buffer.hasRemaining()) {
                  this.pooledBuffer.close();
                  this.pooledBuffer = null;
               }

            }
         }
      }

      public String toString() {
         return "Transfer channel listener (" + this.source + " to " + this.sink + ") -> (" + this.sourceListener + " and " + this.sinkListener + ")";
      }
   }
}
