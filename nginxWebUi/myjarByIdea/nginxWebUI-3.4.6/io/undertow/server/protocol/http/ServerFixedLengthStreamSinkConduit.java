package io.undertow.server.protocol.http;

import io.undertow.conduits.AbstractFixedLengthStreamSinkConduit;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import org.xnio.conduits.StreamSinkConduit;

public class ServerFixedLengthStreamSinkConduit extends AbstractFixedLengthStreamSinkConduit {
   private HttpServerExchange exchange;

   public ServerFixedLengthStreamSinkConduit(StreamSinkConduit next, boolean configurable, boolean propagateClose) {
      super(next, 1L, configurable, propagateClose);
   }

   void reset(long contentLength, HttpServerExchange exchange) {
      this.exchange = exchange;
      super.reset(contentLength, !exchange.isPersistent());
   }

   void clearExchange() {
      this.channelFinished();
   }

   protected void channelFinished() {
      if (this.exchange != null) {
         HttpServerExchange exchange = this.exchange;
         this.exchange = null;
         Connectors.terminateResponse(exchange);
      }

   }
}
