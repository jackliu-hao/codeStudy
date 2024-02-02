package io.undertow.server.handlers;

import io.undertow.conduits.StoredResponseStreamSinkConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.ConduitFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.xnio.conduits.StreamSinkConduit;

public class StoredResponseHandler implements HttpHandler {
   private final HttpHandler next;

   public StoredResponseHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            return new StoredResponseStreamSinkConduit((StreamSinkConduit)factory.create(), exchange);
         }
      });
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "store-response()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "store-response";
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
               return new StoredResponseHandler(handler);
            }
         };
      }
   }
}
