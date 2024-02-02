package io.undertow.websockets.core;

import io.undertow.connector.PooledByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public class BufferedTextMessage {
   private final UTF8Output data;
   private final boolean bufferFullMessage;
   private final long maxMessageSize;
   private boolean complete;
   long currentSize;

   public BufferedTextMessage(long maxMessageSize, boolean bufferFullMessage) {
      this.data = new UTF8Output();
      this.maxMessageSize = maxMessageSize;
      this.bufferFullMessage = bufferFullMessage;
   }

   public BufferedTextMessage(boolean bufferFullMessage) {
      this(-1L, bufferFullMessage);
   }

   private void checkMaxSize(StreamSourceFrameChannel channel, int res) throws IOException {
      if (res > 0) {
         this.currentSize += (long)res;
      }

      if (this.maxMessageSize > 0L && this.currentSize > this.maxMessageSize) {
         WebSockets.sendClose((CloseMessage)(new CloseMessage(1009, WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize))), channel.getWebSocketChannel(), (WebSocketCallback)null);
         throw new IOException(WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize));
      }
   }

   public void readBlocking(StreamSourceFrameChannel channel) throws IOException {
      PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
      ByteBuffer buffer = pooled.getBuffer();

      try {
         while(true) {
            int res = channel.read(buffer);
            if (res == -1) {
               buffer.flip();
               this.data.write(buffer);
               this.complete = true;
               return;
            }

            if (res == 0) {
               channel.awaitReadable();
            }

            this.checkMaxSize(channel, res);
            if (!buffer.hasRemaining()) {
               buffer.flip();
               this.data.write(buffer);
               buffer.compact();
               if (!this.bufferFullMessage) {
                  return;
               }
            }
         }
      } finally {
         pooled.close();
      }
   }

   public void read(StreamSourceFrameChannel channel, final WebSocketCallback<BufferedTextMessage> callback) {
      PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
      ByteBuffer buffer = pooled.getBuffer();

      try {
         while(true) {
            int res = channel.read(buffer);
            if (res == -1) {
               this.complete = true;
               buffer.flip();
               this.data.write(buffer);
               callback.complete(channel.getWebSocketChannel(), this);
               return;
            }

            if (res == 0) {
               buffer.flip();
               if (buffer.hasRemaining()) {
                  this.data.write(buffer);
                  if (!this.bufferFullMessage) {
                     callback.complete(channel.getWebSocketChannel(), this);
                  }
               }

               channel.getReadSetter().set(new ChannelListener<StreamSourceFrameChannel>() {
                  public void handleEvent(StreamSourceFrameChannel channel) {
                     if (!BufferedTextMessage.this.complete) {
                        PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
                        ByteBuffer buffer = pooled.getBuffer();

                        try {
                           while(true) {
                              int res = channel.read(buffer);
                              if (res == -1) {
                                 BufferedTextMessage.this.checkMaxSize(channel, res);
                                 buffer.flip();
                                 BufferedTextMessage.this.data.write(buffer);
                                 BufferedTextMessage.this.complete = true;
                                 callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this);
                                 return;
                              }

                              if (res == 0) {
                                 buffer.flip();
                                 if (buffer.hasRemaining()) {
                                    BufferedTextMessage.this.data.write(buffer);
                                    if (!BufferedTextMessage.this.bufferFullMessage) {
                                       callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this);
                                    }
                                 }
                                 break;
                              }

                              if (!buffer.hasRemaining()) {
                                 buffer.flip();
                                 BufferedTextMessage.this.data.write(buffer);
                                 buffer.clear();
                                 if (!BufferedTextMessage.this.bufferFullMessage) {
                                    callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this);
                                 }
                              }
                           }
                        } catch (IOException var8) {
                           callback.onError(channel.getWebSocketChannel(), BufferedTextMessage.this, var8);
                           return;
                        } finally {
                           pooled.close();
                        }

                     }
                  }
               });
               channel.resumeReads();
               return;
            }

            this.checkMaxSize(channel, res);
            if (!buffer.hasRemaining()) {
               buffer.flip();
               this.data.write(buffer);
               buffer.clear();
               if (!this.bufferFullMessage) {
                  callback.complete(channel.getWebSocketChannel(), this);
               }
            }
         }
      } catch (IOException var9) {
         callback.onError(channel.getWebSocketChannel(), this, var9);
      } finally {
         pooled.close();
      }

   }

   public String getData() {
      return this.data.extract();
   }

   public boolean isComplete() {
      return this.complete;
   }
}
