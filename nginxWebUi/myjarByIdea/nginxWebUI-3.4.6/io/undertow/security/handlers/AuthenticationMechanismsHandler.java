package io.undertow.security.handlers;

import io.undertow.Handlers;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismContext;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import java.util.List;

public class AuthenticationMechanismsHandler implements HttpHandler {
   private volatile HttpHandler next;
   private final AuthenticationMechanism[] authenticationMechanisms;

   public AuthenticationMechanismsHandler(HttpHandler next, List<AuthenticationMechanism> authenticationMechanisms) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.next = next;
      this.authenticationMechanisms = (AuthenticationMechanism[])authenticationMechanisms.toArray(new AuthenticationMechanism[authenticationMechanisms.size()]);
   }

   public AuthenticationMechanismsHandler(List<AuthenticationMechanism> authenticationHandlers) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.authenticationMechanisms = (AuthenticationMechanism[])authenticationHandlers.toArray(new AuthenticationMechanism[authenticationHandlers.size()]);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      SecurityContext sc = exchange.getSecurityContext();
      if (sc != null && sc instanceof AuthenticationMechanismContext) {
         AuthenticationMechanismContext amc = (AuthenticationMechanismContext)sc;

         for(int i = 0; i < this.authenticationMechanisms.length; ++i) {
            amc.addAuthenticationMechanism(this.authenticationMechanisms[i]);
         }
      }

      this.next.handleRequest(exchange);
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public AuthenticationMechanismsHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }
}
