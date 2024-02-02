package io.undertow.server.handlers.encoding;

import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import org.xnio.conduits.StreamSinkConduit;

public interface ContentEncodingProvider {
   ContentEncodingProvider IDENTITY = new ContentEncodingProvider() {
      private final ConduitWrapper<StreamSinkConduit> CONDUIT_WRAPPER = new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            return (StreamSinkConduit)factory.create();
         }
      };

      public ConduitWrapper<StreamSinkConduit> getResponseWrapper() {
         return this.CONDUIT_WRAPPER;
      }
   };

   ConduitWrapper<StreamSinkConduit> getResponseWrapper();
}
