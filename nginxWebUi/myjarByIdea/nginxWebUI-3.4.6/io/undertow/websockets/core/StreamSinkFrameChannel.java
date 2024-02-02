package io.undertow.websockets.core;

import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;

public abstract class StreamSinkFrameChannel extends AbstractFramedStreamSinkChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel> {
   private final WebSocketFrameType type;
   private int rsv;

   protected StreamSinkFrameChannel(WebSocketChannel channel, WebSocketFrameType type) {
      super(channel);
      this.type = type;
   }

   public int getRsv() {
      return this.rsv;
   }

   public void setRsv(int rsv) {
      if (!this.areExtensionsSupported() && rsv != 0) {
         throw WebSocketMessages.MESSAGES.extensionsNotSupported();
      } else {
         this.rsv = rsv;
      }
   }

   public boolean isFragmentationSupported() {
      return false;
   }

   public boolean areExtensionsSupported() {
      return false;
   }

   public WebSocketFrameType getType() {
      return this.type;
   }

   public WebSocketChannel getWebSocketChannel() {
      return (WebSocketChannel)this.getChannel();
   }

   protected boolean isLastFrame() {
      return this.type == WebSocketFrameType.CLOSE;
   }

   public boolean isFinalFragment() {
      return super.isFinalFrameQueued();
   }
}
