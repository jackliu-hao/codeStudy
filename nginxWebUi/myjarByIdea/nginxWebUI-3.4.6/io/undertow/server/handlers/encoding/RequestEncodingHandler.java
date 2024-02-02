package io.undertow.server.handlers.encoding;

import io.undertow.conduits.GzipStreamSourceConduit;
import io.undertow.conduits.InflatingStreamSourceConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.xnio.conduits.StreamSourceConduit;

public class RequestEncodingHandler implements HttpHandler {
   private final HttpHandler next;
   private final Map<String, ConduitWrapper<StreamSourceConduit>> requestEncodings = new CopyOnWriteMap();

   public RequestEncodingHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ConduitWrapper<StreamSourceConduit> encodings = (ConduitWrapper)this.requestEncodings.get(exchange.getRequestHeaders().getFirst(Headers.CONTENT_ENCODING));
      if (encodings != null && exchange.isRequestChannelAvailable()) {
         exchange.addRequestWrapper(encodings);
         exchange.getRequestHeaders().remove(Headers.CONTENT_ENCODING);
      }

      this.next.handleRequest(exchange);
   }

   public RequestEncodingHandler addEncoding(String name, ConduitWrapper<StreamSourceConduit> wrapper) {
      this.requestEncodings.put(name, wrapper);
      return this;
   }

   public RequestEncodingHandler removeEncoding(String encoding) {
      this.requestEncodings.remove(encoding);
      return this;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public String toString() {
      return "uncompress()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "uncompress";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return (new RequestEncodingHandler(handler)).addEncoding("gzip", GzipStreamSourceConduit.WRAPPER).addEncoding("deflate", InflatingStreamSourceConduit.WRAPPER);
            }
         };
      }
   }
}
