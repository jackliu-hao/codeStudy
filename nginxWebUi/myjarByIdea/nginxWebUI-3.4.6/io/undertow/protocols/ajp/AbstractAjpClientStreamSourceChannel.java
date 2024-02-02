package io.undertow.protocols.ajp;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;

public class AbstractAjpClientStreamSourceChannel extends AbstractFramedStreamSourceChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel> {
   public AbstractAjpClientStreamSourceChannel(AjpClientChannel framedChannel, PooledByteBuffer data, long frameDataRemaining) {
      super(framedChannel, data, frameDataRemaining);
   }
}
