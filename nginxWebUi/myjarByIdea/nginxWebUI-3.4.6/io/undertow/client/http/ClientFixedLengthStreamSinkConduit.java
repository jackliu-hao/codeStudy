package io.undertow.client.http;

import io.undertow.conduits.AbstractFixedLengthStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

class ClientFixedLengthStreamSinkConduit extends AbstractFixedLengthStreamSinkConduit {
   private final HttpClientExchange exchange;

   ClientFixedLengthStreamSinkConduit(StreamSinkConduit next, long contentLength, boolean configurable, boolean propagateClose, HttpClientExchange exchange) {
      super(next, contentLength, configurable, propagateClose);
      this.exchange = exchange;
   }

   protected void channelFinished() {
      this.exchange.terminateRequest();
   }
}
