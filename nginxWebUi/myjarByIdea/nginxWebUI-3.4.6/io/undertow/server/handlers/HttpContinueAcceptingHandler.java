package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.protocol.http.HttpContinue;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class HttpContinueAcceptingHandler implements HttpHandler {
   private final HttpHandler next;
   private final Predicate accept;

   public HttpContinueAcceptingHandler(HttpHandler next, Predicate accept) {
      this.next = next;
      this.accept = accept;
   }

   public HttpContinueAcceptingHandler(HttpHandler next) {
      this(next, Predicates.truePredicate());
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (HttpContinue.requiresContinueResponse(exchange)) {
         if (this.accept.resolve(exchange)) {
            HttpContinue.sendContinueResponse(exchange, new IoCallback() {
               public void onComplete(HttpServerExchange exchange, Sender sender) {
                  exchange.dispatch(HttpContinueAcceptingHandler.this.next);
               }

               public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
                  UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
                  exchange.endExchange();
               }
            });
         } else {
            HttpContinue.rejectExchange(exchange);
         }
      } else {
         this.next.handleRequest(exchange);
      }

   }

   public String toString() {
      return "http-continue-accept()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "http-continue-accept";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return null;
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper(Predicates.truePredicate());
      }
   }

   public static final class Wrapper implements HandlerWrapper {
      private final Predicate predicate;

      public Wrapper(Predicate predicate) {
         this.predicate = predicate;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new HttpContinueAcceptingHandler(handler, this.predicate);
      }
   }
}
