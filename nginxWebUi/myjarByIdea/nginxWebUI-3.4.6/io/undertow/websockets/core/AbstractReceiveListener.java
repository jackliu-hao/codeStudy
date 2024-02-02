package io.undertow.websockets.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Pooled;

public abstract class AbstractReceiveListener implements ChannelListener<WebSocketChannel> {
   public void handleEvent(WebSocketChannel channel) {
      try {
         StreamSourceFrameChannel result = (StreamSourceFrameChannel)channel.receive();
         if (result == null) {
            return;
         }

         if (result.getType() == WebSocketFrameType.BINARY) {
            this.onBinary(channel, result);
         } else if (result.getType() == WebSocketFrameType.TEXT) {
            this.onText(channel, result);
         } else if (result.getType() == WebSocketFrameType.PONG) {
            this.onPong(channel, result);
         } else if (result.getType() == WebSocketFrameType.PING) {
            this.onPing(channel, result);
         } else if (result.getType() == WebSocketFrameType.CLOSE) {
            this.onClose(channel, result);
         }
      } catch (IOException var3) {
         this.onError(channel, var3);
      }

   }

   protected void onPing(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
      this.bufferFullMessage(channel);
   }

   protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
      this.bufferFullMessage(channel);
   }

   protected void onPong(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
      this.bufferFullMessage(messageChannel);
   }

   protected void onText(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
      this.bufferFullMessage(messageChannel);
   }

   protected void onBinary(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
      this.bufferFullMessage(messageChannel);
   }

   protected void onError(WebSocketChannel channel, Throwable error) {
      IoUtils.safeClose((Closeable)channel);
   }

   protected final void bufferFullMessage(StreamSourceFrameChannel messageChannel) {
      if (messageChannel.getType() == WebSocketFrameType.TEXT) {
         this.readBufferedText(messageChannel, new BufferedTextMessage(this.getMaxTextBufferSize(), true));
      } else if (messageChannel.getType() == WebSocketFrameType.BINARY) {
         this.readBufferedBinary(messageChannel, false, new BufferedBinaryMessage(this.getMaxBinaryBufferSize(), true));
      } else if (messageChannel.getType() == WebSocketFrameType.PONG) {
         this.readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(this.getMaxPongBufferSize(), true));
      } else if (messageChannel.getType() == WebSocketFrameType.PING) {
         this.readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(this.getMaxPingBufferSize(), true));
      } else if (messageChannel.getType() == WebSocketFrameType.CLOSE) {
         this.readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(this.getMaxCloseBufferSize(), true));
      }

   }

   protected long getMaxBinaryBufferSize() {
      return -1L;
   }

   protected long getMaxPongBufferSize() {
      return -1L;
   }

   protected long getMaxCloseBufferSize() {
      return -1L;
   }

   protected long getMaxPingBufferSize() {
      return -1L;
   }

   protected long getMaxTextBufferSize() {
      return -1L;
   }

   private void readBufferedBinary(final StreamSourceFrameChannel messageChannel, final boolean controlFrame, final BufferedBinaryMessage buffer) {
      buffer.read(messageChannel, new WebSocketCallback<BufferedBinaryMessage>() {
         public void complete(WebSocketChannel channel, BufferedBinaryMessage context) {
            try {
               WebSocketFrameType type = messageChannel.getType();
               if (!controlFrame) {
                  AbstractReceiveListener.this.onFullBinaryMessage(channel, buffer);
               } else if (type == WebSocketFrameType.PONG) {
                  AbstractReceiveListener.this.onFullPongMessage(channel, buffer);
               } else if (type == WebSocketFrameType.PING) {
                  AbstractReceiveListener.this.onFullPingMessage(channel, buffer);
               } else if (type == WebSocketFrameType.CLOSE) {
                  AbstractReceiveListener.this.onFullCloseMessage(channel, buffer);
               }
            } catch (IOException var4) {
               AbstractReceiveListener.this.onError(channel, var4);
            }

         }

         public void onError(WebSocketChannel channel, BufferedBinaryMessage context, Throwable throwable) {
            context.getData().close();
            AbstractReceiveListener.this.onError(channel, throwable);
         }
      });
   }

   private void readBufferedText(StreamSourceFrameChannel messageChannel, final BufferedTextMessage textMessage) {
      textMessage.read(messageChannel, new WebSocketCallback<BufferedTextMessage>() {
         public void complete(WebSocketChannel channel, BufferedTextMessage context) {
            try {
               AbstractReceiveListener.this.onFullTextMessage(channel, textMessage);
            } catch (IOException var4) {
               AbstractReceiveListener.this.onError(channel, var4);
            }

         }

         public void onError(WebSocketChannel channel, BufferedTextMessage context, Throwable throwable) {
            AbstractReceiveListener.this.onError(channel, throwable);
         }
      });
   }

   protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
   }

   protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
      message.getData().free();
   }

   protected void onFullPingMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
      Pooled<ByteBuffer[]> data = message.getData();
      WebSockets.sendPong((ByteBuffer[])((ByteBuffer[])data.getResource()), channel, new FreeDataCallback(data));
   }

   protected void onFullPongMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
      message.getData().free();
   }

   protected void onFullCloseMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
      Pooled<ByteBuffer[]> data = message.getData();

      try {
         CloseMessage cm = new CloseMessage((ByteBuffer[])data.getResource());
         this.onCloseMessage(cm, channel);
         if (!channel.isCloseFrameSent()) {
            WebSockets.sendClose((CloseMessage)cm, channel, (WebSocketCallback)null);
         }
      } finally {
         data.close();
      }

   }

   protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
   }

   private static class FreeDataCallback implements WebSocketCallback<Void> {
      private final Pooled<ByteBuffer[]> data;

      FreeDataCallback(Pooled<ByteBuffer[]> data) {
         this.data = data;
      }

      public void complete(WebSocketChannel channel, Void context) {
         this.data.close();
      }

      public void onError(WebSocketChannel channel, Void context, Throwable throwable) {
         this.data.close();
      }
   }
}
