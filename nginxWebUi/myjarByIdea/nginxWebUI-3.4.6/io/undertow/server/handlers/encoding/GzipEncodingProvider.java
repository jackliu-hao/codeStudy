package io.undertow.server.handlers.encoding;

import io.undertow.UndertowLogger;
import io.undertow.conduits.DeflatingStreamSinkConduit;
import io.undertow.conduits.GzipStreamSinkConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.ObjectPool;
import java.util.zip.Deflater;
import org.xnio.conduits.StreamSinkConduit;

public class GzipEncodingProvider implements ContentEncodingProvider {
   private final ObjectPool<Deflater> deflaterPool;

   public GzipEncodingProvider() {
      this(-1);
   }

   public GzipEncodingProvider(int deflateLevel) {
      this(DeflatingStreamSinkConduit.newInstanceDeflaterPool(deflateLevel));
   }

   public GzipEncodingProvider(ObjectPool<Deflater> deflaterPool) {
      this.deflaterPool = deflaterPool;
   }

   public ConduitWrapper<StreamSinkConduit> getResponseWrapper() {
      return new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            UndertowLogger.REQUEST_LOGGER.tracef("Created GZIP response conduit for %s", exchange);
            return new GzipStreamSinkConduit(factory, exchange, GzipEncodingProvider.this.deflaterPool);
         }
      };
   }
}
