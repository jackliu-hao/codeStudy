package io.undertow.websockets.core;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.ImmediatePooled;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xnio.ChannelListener;
import org.xnio.Pooled;

public class BufferedBinaryMessage {
   private final boolean bufferFullMessage;
   private List<PooledByteBuffer> data;
   private PooledByteBuffer current;
   private final long maxMessageSize;
   private long currentSize;
   private boolean complete;

   public BufferedBinaryMessage(long maxMessageSize, boolean bufferFullMessage) {
      this.data = new ArrayList(1);
      this.bufferFullMessage = bufferFullMessage;
      this.maxMessageSize = maxMessageSize;
   }

   public BufferedBinaryMessage(boolean bufferFullMessage) {
      this(-1L, bufferFullMessage);
   }

   public void readBlocking(StreamSourceFrameChannel channel) throws IOException {
      if (this.current == null) {
         this.current = channel.getWebSocketChannel().getBufferPool().allocate();
      }

      while(true) {
         int res = channel.read(this.current.getBuffer());
         if (res == -1) {
            this.complete = true;
            return;
         }

         if (res == 0) {
            channel.awaitReadable();
         }

         this.checkMaxSize(channel, res);
         if (this.bufferFullMessage) {
            this.dealWithFullBuffer(channel);
         } else if (!this.current.getBuffer().hasRemaining()) {
            return;
         }
      }
   }

   private void dealWithFullBuffer(StreamSourceFrameChannel channel) {
      if (!this.current.getBuffer().hasRemaining()) {
         this.current.getBuffer().flip();
         this.data.add(this.current);
         this.current = channel.getWebSocketChannel().getBufferPool().allocate();
      }

   }

   public void read(StreamSourceFrameChannel channel, final WebSocketCallback<BufferedBinaryMessage> callback) {
      try {
         while(true) {
            if (this.current == null) {
               this.current = channel.getWebSocketChannel().getBufferPool().allocate();
            }

            int res = channel.read(this.current.getBuffer());
            if (res == -1) {
               this.complete = true;
               callback.complete(channel.getWebSocketChannel(), this);
               return;
            }

            if (res == 0) {
               if (!this.bufferFullMessage) {
                  callback.complete(channel.getWebSocketChannel(), this);
               }

               channel.getReadSetter().set(new ChannelListener<StreamSourceFrameChannel>() {
                  public void handleEvent(StreamSourceFrameChannel channel) {
                     if (!BufferedBinaryMessage.this.complete) {
                        try {
                           while(true) {
                              if (BufferedBinaryMessage.this.current == null) {
                                 BufferedBinaryMessage.this.current = channel.getWebSocketChannel().getBufferPool().allocate();
                              }

                              int res = channel.read(BufferedBinaryMessage.this.current.getBuffer());
                              if (res == -1) {
                                 BufferedBinaryMessage.this.complete = true;
                                 channel.suspendReads();
                                 callback.complete(channel.getWebSocketChannel(), BufferedBinaryMessage.this);
                                 return;
                              }

                              if (res == 0) {
                                 return;
                              }

                              BufferedBinaryMessage.this.checkMaxSize(channel, res);
                              if (BufferedBinaryMessage.this.bufferFullMessage) {
                                 BufferedBinaryMessage.this.dealWithFullBuffer(channel);
                              } else if (!BufferedBinaryMessage.this.current.getBuffer().hasRemaining()) {
                                 callback.complete(channel.getWebSocketChannel(), BufferedBinaryMessage.this);
                              }
                           }
                        } catch (IOException var3) {
                           channel.suspendReads();
                           callback.onError(channel.getWebSocketChannel(), BufferedBinaryMessage.this, var3);
                        }
                     }
                  }
               });
               channel.resumeReads();
               return;
            }

            this.checkMaxSize(channel, res);
            if (this.bufferFullMessage) {
               this.dealWithFullBuffer(channel);
            } else if (!this.current.getBuffer().hasRemaining()) {
               callback.complete(channel.getWebSocketChannel(), this);
            }
         }
      } catch (IOException var4) {
         callback.onError(channel.getWebSocketChannel(), this, var4);
      }
   }

   private void checkMaxSize(StreamSourceFrameChannel channel, int res) throws IOException {
      this.currentSize += (long)res;
      if (this.maxMessageSize > 0L && this.currentSize > this.maxMessageSize) {
         this.getData().free();
         WebSockets.sendClose((CloseMessage)(new CloseMessage(1009, WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize))), channel.getWebSocketChannel(), (WebSocketCallback)null);
         throw new IOException(WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize));
      }
   }

   public Pooled<ByteBuffer[]> getData() {
      if (this.current == null) {
         return new ImmediatePooled(new ByteBuffer[0]);
      } else if (this.data.isEmpty()) {
         PooledByteBuffer current = this.current;
         current.getBuffer().flip();
         this.current = null;
         ByteBuffer[] data = new ByteBuffer[]{current.getBuffer()};
         return new PooledByteBufferArray(Collections.singletonList(current), data);
      } else {
         this.current.getBuffer().flip();
         this.data.add(this.current);
         this.current = null;
         ByteBuffer[] ret = new ByteBuffer[this.data.size()];

         for(int i = 0; i < this.data.size(); ++i) {
            ret[i] = ((PooledByteBuffer)this.data.get(i)).getBuffer();
         }

         List<PooledByteBuffer> data = this.data;
         this.data = new ArrayList();
         return new PooledByteBufferArray(data, ret);
      }
   }

   public boolean isComplete() {
      return this.complete;
   }

   private static final class PooledByteBufferArray implements Pooled<ByteBuffer[]> {
      private final List<PooledByteBuffer> pooled;
      private final ByteBuffer[] data;

      private PooledByteBufferArray(List<PooledByteBuffer> pooled, ByteBuffer[] data) {
         this.pooled = pooled;
         this.data = data;
      }

      public void discard() {
         Iterator var1 = this.pooled.iterator();

         while(var1.hasNext()) {
            PooledByteBuffer item = (PooledByteBuffer)var1.next();
            item.close();
         }

      }

      public void free() {
         Iterator var1 = this.pooled.iterator();

         while(var1.hasNext()) {
            PooledByteBuffer item = (PooledByteBuffer)var1.next();
            item.close();
         }

      }

      public ByteBuffer[] getResource() throws IllegalStateException {
         return this.data;
      }

      public void close() {
         this.free();
      }

      // $FF: synthetic method
      PooledByteBufferArray(List x0, ByteBuffer[] x1, Object x2) {
         this(x0, x1);
      }
   }
}
