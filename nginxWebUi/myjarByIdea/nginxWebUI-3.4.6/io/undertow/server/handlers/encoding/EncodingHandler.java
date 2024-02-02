package io.undertow.server.handlers.encoding;

import io.undertow.Handlers;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EncodingHandler implements HttpHandler {
   private volatile HttpHandler next;
   private volatile HttpHandler noEncodingHandler;
   private final ContentEncodingRepository contentEncodingRepository;

   public EncodingHandler(HttpHandler next, ContentEncodingRepository contentEncodingRepository) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.noEncodingHandler = ResponseCodeHandler.HANDLE_406;
      this.next = next;
      this.contentEncodingRepository = contentEncodingRepository;
   }

   public EncodingHandler(ContentEncodingRepository contentEncodingRepository) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.noEncodingHandler = ResponseCodeHandler.HANDLE_406;
      this.contentEncodingRepository = contentEncodingRepository;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      AllowedContentEncodings encodings = this.contentEncodingRepository.getContentEncodings(exchange);
      if (encodings != null && exchange.isResponseChannelAvailable()) {
         if (encodings.isNoEncodingsAllowed()) {
            this.noEncodingHandler.handleRequest(exchange);
         } else {
            exchange.addResponseWrapper(encodings);
            exchange.putAttachment(AllowedContentEncodings.ATTACHMENT_KEY, encodings);
            this.next.handleRequest(exchange);
         }
      } else {
         this.next.handleRequest(exchange);
      }

   }

   public HttpHandler getNext() {
      return this.next;
   }

   public EncodingHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public HttpHandler getNoEncodingHandler() {
      return this.noEncodingHandler;
   }

   public EncodingHandler setNoEncodingHandler(HttpHandler noEncodingHandler) {
      Handlers.handlerNotNull(noEncodingHandler);
      this.noEncodingHandler = noEncodingHandler;
      return this;
   }

   public String toString() {
      return "compress()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "compress";
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
               return new EncodingHandler(handler, (new ContentEncodingRepository()).addEncodingHandler("gzip", new GzipEncodingProvider(), 100).addEncodingHandler("deflate", new DeflateEncodingProvider(), 10));
            }
         };
      }
   }
}
