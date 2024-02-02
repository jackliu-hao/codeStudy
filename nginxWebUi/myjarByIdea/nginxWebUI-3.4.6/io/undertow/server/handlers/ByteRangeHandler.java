package io.undertow.server.handlers;

import io.undertow.conduits.ConduitListener;
import io.undertow.conduits.HeadStreamSinkConduit;
import io.undertow.conduits.RangeStreamSinkConduit;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ResponseCommitListener;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.ByteRange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.DateUtils;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.xnio.conduits.StreamSinkConduit;

public class ByteRangeHandler implements HttpHandler {
   private final HttpHandler next;
   private final boolean sendAcceptRanges;
   private static final ResponseCommitListener ACCEPT_RANGE_LISTENER = new ResponseCommitListener() {
      public void beforeCommit(HttpServerExchange exchange) {
         if (!exchange.getResponseHeaders().contains(Headers.ACCEPT_RANGES)) {
            if (exchange.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
               exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "bytes");
            } else {
               exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "none");
            }
         }

      }
   };

   public ByteRangeHandler(HttpHandler next, boolean sendAcceptRanges) {
      this.next = next;
      this.sendAcceptRanges = sendAcceptRanges;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (!Methods.GET.equals(exchange.getRequestMethod()) && !Methods.HEAD.equals(exchange.getRequestMethod())) {
         this.next.handleRequest(exchange);
      } else {
         if (this.sendAcceptRanges) {
            exchange.addResponseCommitListener(ACCEPT_RANGE_LISTENER);
         }

         final ByteRange range = ByteRange.parse(exchange.getRequestHeaders().getFirst(Headers.RANGE));
         if (range != null && range.getRanges() == 1) {
            exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
               public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
                  if (exchange.getStatusCode() != 200) {
                     return (StreamSinkConduit)factory.create();
                  } else {
                     String length = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
                     if (length == null) {
                        return (StreamSinkConduit)factory.create();
                     } else {
                        long responseLength = Long.parseLong(length);
                        String lastModified = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
                        ByteRange.RangeResponseResult rangeResponse = range.getResponseResult(responseLength, exchange.getRequestHeaders().getFirst(Headers.IF_RANGE), lastModified == null ? null : DateUtils.parseDate(lastModified), exchange.getResponseHeaders().getFirst(Headers.ETAG));
                        if (rangeResponse != null) {
                           long start = rangeResponse.getStart();
                           long end = rangeResponse.getEnd();
                           exchange.setStatusCode(rangeResponse.getStatusCode());
                           exchange.getResponseHeaders().put(Headers.CONTENT_RANGE, rangeResponse.getContentRange());
                           exchange.setResponseContentLength(rangeResponse.getContentLength());
                           return (StreamSinkConduit)(rangeResponse.getStatusCode() == 416 ? new HeadStreamSinkConduit((StreamSinkConduit)factory.create(), (ConduitListener)null, true) : new RangeStreamSinkConduit((StreamSinkConduit)factory.create(), start, end, responseLength));
                        } else {
                           return (StreamSinkConduit)factory.create();
                        }
                     }
                  }
               }
            });
         }

         this.next.handleRequest(exchange);
      }
   }

   public String toString() {
      return "byte-range( " + this.sendAcceptRanges + " )";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "byte-range";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("send-accept-ranges", Boolean.TYPE);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return "send-accept-ranges";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         Boolean send = (Boolean)config.get("send-accept-ranges");
         return new Wrapper(send != null && send);
      }
   }

   public static class Wrapper implements HandlerWrapper {
      private final boolean sendAcceptRanges;

      public Wrapper(boolean sendAcceptRanges) {
         this.sendAcceptRanges = sendAcceptRanges;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new ByteRangeHandler(handler, this.sendAcceptRanges);
      }
   }
}
