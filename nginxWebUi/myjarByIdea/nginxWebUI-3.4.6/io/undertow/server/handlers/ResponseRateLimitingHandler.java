package io.undertow.server.handlers;

import io.undertow.conduits.RateLimitingStreamSinkConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.ConduitFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.xnio.conduits.StreamSinkConduit;

public class ResponseRateLimitingHandler implements HttpHandler {
   private final long time;
   private final int bytes;
   private final HttpHandler next;
   private final ConduitWrapper<StreamSinkConduit> WRAPPER = new ConduitWrapper<StreamSinkConduit>() {
      public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
         return new RateLimitingStreamSinkConduit((StreamSinkConduit)factory.create(), ResponseRateLimitingHandler.this.bytes, ResponseRateLimitingHandler.this.time, TimeUnit.MILLISECONDS);
      }
   };

   public ResponseRateLimitingHandler(HttpHandler next, int bytes, long time, TimeUnit timeUnit) {
      this.time = timeUnit.toMillis(time);
      this.bytes = bytes;
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addResponseWrapper(this.WRAPPER);
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "response-rate-limit( bytes=" + this.bytes + ", time=" + this.time + " )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final long time;
      private final int bytes;

      private Wrapper(int bytes, long time, TimeUnit timeUnit) {
         this.time = timeUnit.toMillis(time);
         this.bytes = bytes;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new ResponseRateLimitingHandler(handler, this.bytes, this.time, TimeUnit.MILLISECONDS);
      }

      // $FF: synthetic method
      Wrapper(int x0, long x1, TimeUnit x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "response-rate-limit";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> ret = new HashMap();
         ret.put("bytes", Integer.class);
         ret.put("time", Long.class);
         return ret;
      }

      public Set<String> requiredParameters() {
         return new HashSet(Arrays.asList("bytes", "time"));
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((Integer)config.get("bytes"), (Long)config.get("time"), TimeUnit.MILLISECONDS);
      }
   }
}
