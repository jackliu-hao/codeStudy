package io.undertow.protocols.http2;

import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;

public class AbstractHttp2StreamSinkChannel extends AbstractFramedStreamSinkChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel> {
   AbstractHttp2StreamSinkChannel(Http2Channel channel) {
      super(channel);
   }

   protected boolean isLastFrame() {
      return false;
   }
}
