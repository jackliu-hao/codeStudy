package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OriginHandler implements HttpHandler {
   private volatile HttpHandler originFailedHandler;
   private volatile Set<String> allowedOrigins;
   private volatile boolean requireAllOrigins;
   private volatile boolean requireOriginHeader;
   private volatile HttpHandler next;

   public OriginHandler() {
      this.originFailedHandler = ResponseCodeHandler.HANDLE_403;
      this.allowedOrigins = new HashSet();
      this.requireAllOrigins = true;
      this.requireOriginHeader = true;
      this.next = ResponseCodeHandler.HANDLE_404;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      List<String> origin = exchange.getRequestHeaders().get(Headers.ORIGIN);
      if (origin == null) {
         if (this.requireOriginHeader) {
            if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
               UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s due to lack of Origin: header", exchange.getRequestPath());
            }

            this.originFailedHandler.handleRequest(exchange);
            return;
         }
      } else {
         boolean found = false;
         boolean requireAllOrigins = this.requireAllOrigins;
         Iterator var5 = origin.iterator();

         while(var5.hasNext()) {
            String header = (String)var5.next();
            if (this.allowedOrigins.contains(header)) {
               found = true;
               if (!requireAllOrigins) {
                  break;
               }
            } else if (requireAllOrigins) {
               if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
                  UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s due to Origin %s not being in the allowed origins list", exchange.getRequestPath(), header);
               }

               this.originFailedHandler.handleRequest(exchange);
               return;
            }
         }

         if (!found) {
            if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
               UndertowLogger.REQUEST_LOGGER.debugf("Refusing request for %s as none of the specified origins %s were in the allowed origins list", exchange.getRequestPath(), origin);
            }

            this.originFailedHandler.handleRequest(exchange);
            return;
         }
      }

      this.next.handleRequest(exchange);
   }

   public synchronized OriginHandler addAllowedOrigin(String origin) {
      Set<String> allowedOrigins = new HashSet(this.allowedOrigins);
      allowedOrigins.add(origin);
      this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
      return this;
   }

   public synchronized OriginHandler addAllowedOrigins(Collection<String> origins) {
      Set<String> allowedOrigins = new HashSet(this.allowedOrigins);
      allowedOrigins.addAll(origins);
      this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
      return this;
   }

   public synchronized OriginHandler addAllowedOrigins(String... origins) {
      Set<String> allowedOrigins = new HashSet(this.allowedOrigins);
      allowedOrigins.addAll(Arrays.asList(origins));
      this.allowedOrigins = Collections.unmodifiableSet(allowedOrigins);
      return this;
   }

   public synchronized Set<String> getAllowedOrigins() {
      return this.allowedOrigins;
   }

   public synchronized OriginHandler clearAllowedOrigins() {
      this.allowedOrigins = Collections.emptySet();
      return this;
   }

   public boolean isRequireAllOrigins() {
      return this.requireAllOrigins;
   }

   public OriginHandler setRequireAllOrigins(boolean requireAllOrigins) {
      this.requireAllOrigins = requireAllOrigins;
      return this;
   }

   public boolean isRequireOriginHeader() {
      return this.requireOriginHeader;
   }

   public OriginHandler setRequireOriginHeader(boolean requireOriginHeader) {
      this.requireOriginHeader = requireOriginHeader;
      return this;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public OriginHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public HttpHandler getOriginFailedHandler() {
      return this.originFailedHandler;
   }

   public OriginHandler setOriginFailedHandler(HttpHandler originFailedHandler) {
      Handlers.handlerNotNull(originFailedHandler);
      this.originFailedHandler = originFailedHandler;
      return this;
   }
}
