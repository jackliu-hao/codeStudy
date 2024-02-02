package io.undertow.server.handlers.form;

import io.undertow.Handlers;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EagerFormParsingHandler implements HttpHandler {
   private volatile HttpHandler next;
   private final FormParserFactory formParserFactory;
   public static final HandlerWrapper WRAPPER = new HandlerWrapper() {
      public HttpHandler wrap(HttpHandler handler) {
         return new EagerFormParsingHandler(handler);
      }
   };

   public EagerFormParsingHandler(FormParserFactory formParserFactory) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.formParserFactory = formParserFactory;
   }

   public EagerFormParsingHandler() {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.formParserFactory = FormParserFactory.builder().build();
   }

   public EagerFormParsingHandler(HttpHandler next) {
      this();
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      FormDataParser parser = this.formParserFactory.createParser(exchange);
      if (parser == null) {
         this.next.handleRequest(exchange);
      } else {
         if (exchange.isBlocking()) {
            exchange.putAttachment(FormDataParser.FORM_DATA, parser.parseBlocking());
            this.next.handleRequest(exchange);
         } else {
            parser.parse(this.next);
         }

      }
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public EagerFormParsingHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public String toString() {
      return "eager-form-parser()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "eager-form-parser";
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
         return EagerFormParsingHandler.WRAPPER;
      }
   }
}
