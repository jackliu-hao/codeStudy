package io.undertow.server.handlers.error;

import io.undertow.Handlers;
import io.undertow.io.Sender;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleErrorPageHandler implements HttpHandler {
   private volatile HttpHandler next;
   private volatile Set<Integer> responseCodes;
   private final DefaultResponseListener responseListener;

   public SimpleErrorPageHandler(HttpHandler next) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.responseCodes = null;
      this.responseListener = new DefaultResponseListener() {
         public boolean handleDefaultResponse(HttpServerExchange exchange) {
            if (!exchange.isResponseChannelAvailable()) {
               return false;
            } else {
               label18: {
                  Set<Integer> codes = SimpleErrorPageHandler.this.responseCodes;
                  if (codes == null) {
                     if (exchange.getStatusCode() >= 400) {
                        break label18;
                     }
                  } else if (codes.contains(exchange.getStatusCode())) {
                     break label18;
                  }

                  return false;
               }

               String errorPage = "<html><head><title>Error</title></head><body>" + exchange.getStatusCode() + " - " + StatusCodes.getReason(exchange.getStatusCode()) + "</body></html>";
               exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length());
               exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
               Sender sender = exchange.getResponseSender();
               sender.send(errorPage);
               return true;
            }
         }
      };
      this.next = next;
   }

   public SimpleErrorPageHandler() {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.responseCodes = null;
      this.responseListener = new DefaultResponseListener() {
         public boolean handleDefaultResponse(HttpServerExchange exchange) {
            if (!exchange.isResponseChannelAvailable()) {
               return false;
            } else {
               label18: {
                  Set<Integer> codes = SimpleErrorPageHandler.this.responseCodes;
                  if (codes == null) {
                     if (exchange.getStatusCode() >= 400) {
                        break label18;
                     }
                  } else if (codes.contains(exchange.getStatusCode())) {
                     break label18;
                  }

                  return false;
               }

               String errorPage = "<html><head><title>Error</title></head><body>" + exchange.getStatusCode() + " - " + StatusCodes.getReason(exchange.getStatusCode()) + "</body></html>";
               exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length());
               exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
               Sender sender = exchange.getResponseSender();
               sender.send(errorPage);
               return true;
            }
         }
      };
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addDefaultResponseListener(this.responseListener);
      this.next.handleRequest(exchange);
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public SimpleErrorPageHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public Set<Integer> getResponseCodes() {
      return Collections.unmodifiableSet(this.responseCodes);
   }

   public SimpleErrorPageHandler setResponseCodes(Set<Integer> responseCodes) {
      this.responseCodes = new HashSet(responseCodes);
      return this;
   }

   public SimpleErrorPageHandler setResponseCodes(Integer... responseCodes) {
      this.responseCodes = new HashSet(Arrays.asList(responseCodes));
      return this;
   }
}
