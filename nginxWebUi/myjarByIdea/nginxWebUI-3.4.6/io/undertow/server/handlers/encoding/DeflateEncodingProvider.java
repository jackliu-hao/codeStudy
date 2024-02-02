package io.undertow.server.handlers.encoding;

import io.undertow.UndertowLogger;
import io.undertow.conduits.DeflatingStreamSinkConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.ObjectPool;
import java.util.zip.Deflater;
import org.xnio.conduits.StreamSinkConduit;

public class DeflateEncodingProvider implements ContentEncodingProvider {
   private final ObjectPool<Deflater> deflaterPool;

   public DeflateEncodingProvider() {
      this(8);
   }

   public DeflateEncodingProvider(int deflateLevel) {
      this(DeflatingStreamSinkConduit.newInstanceDeflaterPool(deflateLevel));
   }

   public DeflateEncodingProvider(ObjectPool<Deflater> deflaterPool) {
      this.deflaterPool = deflaterPool;
   }

   public ConduitWrapper<StreamSinkConduit> getResponseWrapper() {
      return new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            UndertowLogger.REQUEST_LOGGER.tracef("Created DEFLATE response conduit for %s", exchange);
            return new DeflatingStreamSinkConduit(factory, exchange, DeflateEncodingProvider.this.deflaterPool);
         }
      };
   }
}
