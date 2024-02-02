package io.undertow.security.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.NetworkUtils;
import java.net.URI;
import java.net.URISyntaxException;

public class SinglePortConfidentialityHandler extends AbstractConfidentialityHandler {
   private final int redirectPort;

   public SinglePortConfidentialityHandler(HttpHandler next, int redirectPort) {
      super(next);
      this.redirectPort = redirectPort == 443 ? -1 : redirectPort;
   }

   protected URI getRedirectURI(HttpServerExchange exchange) throws URISyntaxException {
      return this.getRedirectURI(exchange, this.redirectPort);
   }

   protected URI getRedirectURI(HttpServerExchange exchange, int port) throws URISyntaxException {
      StringBuilder uriBuilder = new StringBuilder();
      uriBuilder.append("https://");
      uriBuilder.append(NetworkUtils.formatPossibleIpv6Address(exchange.getHostName()));
      if (port > 0) {
         uriBuilder.append(":").append(port);
      }

      String uri = exchange.getRequestURI();
      if (exchange.isHostIncludedInRequestURI()) {
         int slashCount = 0;

         for(int i = 0; i < uri.length(); ++i) {
            if (uri.charAt(i) == '/') {
               ++slashCount;
               if (slashCount == 3) {
                  uri = uri.substring(i);
                  break;
               }
            }
         }
      }

      uriBuilder.append(uri);
      String queryString = exchange.getQueryString();
      if (queryString != null && !queryString.isEmpty()) {
         uriBuilder.append("?").append(queryString);
      }

      return new URI(uriBuilder.toString());
   }
}
