package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;

public class AbstractHttp2StreamSourceChannel extends AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel> {
   AbstractHttp2StreamSourceChannel(Http2Channel framedChannel) {
      super(framedChannel);
   }

   AbstractHttp2StreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining) {
      super(framedChannel, data, frameDataRemaining);
   }

   protected void handleHeaderData(FrameHeaderData headerData) {
   }

   protected Http2Channel getFramedChannel() {
      return (Http2Channel)super.getFramedChannel();
   }

   public Http2Channel getHttp2Channel() {
      return this.getFramedChannel();
   }

   protected void lastFrame() {
      super.lastFrame();
   }

   void rstStream() {
      this.rstStream(8);
   }

   void rstStream(int error) {
   }

   protected void markStreamBroken() {
      super.markStreamBroken();
   }
}
