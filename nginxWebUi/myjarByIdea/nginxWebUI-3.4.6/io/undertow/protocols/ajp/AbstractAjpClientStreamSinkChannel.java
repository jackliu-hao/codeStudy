package io.undertow.protocols.ajp;

import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;

public class AbstractAjpClientStreamSinkChannel extends AbstractFramedStreamSinkChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel> {
   protected AbstractAjpClientStreamSinkChannel(AjpClientChannel channel) {
      super(channel);
   }

   protected boolean isLastFrame() {
      return false;
   }
}
