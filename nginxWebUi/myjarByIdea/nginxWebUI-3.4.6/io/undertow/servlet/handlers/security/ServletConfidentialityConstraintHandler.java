package io.undertow.servlet.handlers.security;

import io.undertow.security.handlers.SinglePortConfidentialityHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.AuthorizationManager;
import io.undertow.servlet.api.ConfidentialPortManager;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;

public class ServletConfidentialityConstraintHandler extends SinglePortConfidentialityHandler {
   private final ConfidentialPortManager portManager;

   public ServletConfidentialityConstraintHandler(ConfidentialPortManager portManager, HttpHandler next) {
      super(next, -1);
      this.portManager = portManager;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      AuthorizationManager authorizationManager = servletRequestContext.getDeployment().getDeploymentInfo().getAuthorizationManager();
      TransportGuaranteeType connectionGuarantee = servletRequestContext.getOriginalRequest().isSecure() ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE;
      TransportGuaranteeType transportGuarantee = authorizationManager.transportGuarantee(connectionGuarantee, servletRequestContext.getTransportGuarenteeType(), servletRequestContext.getOriginalRequest());
      servletRequestContext.setTransportGuarenteeType(transportGuarantee);
      if (TransportGuaranteeType.REJECTED == transportGuarantee) {
         HttpServletResponse response = (HttpServletResponse)servletRequestContext.getServletResponse();
         response.sendError(403);
      } else {
         super.handleRequest(exchange);
      }
   }

   protected boolean confidentialityRequired(HttpServerExchange exchange) {
      TransportGuaranteeType transportGuarantee = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getTransportGuarenteeType();
      return TransportGuaranteeType.CONFIDENTIAL == transportGuarantee || TransportGuaranteeType.INTEGRAL == transportGuarantee;
   }

   protected URI getRedirectURI(HttpServerExchange exchange) throws URISyntaxException {
      int port = this.portManager.getConfidentialPort(exchange);
      if (port < 0) {
         throw UndertowServletMessages.MESSAGES.noConfidentialPortAvailable();
      } else {
         return super.getRedirectURI(exchange, port);
      }
   }

   protected boolean isConfidential(HttpServerExchange exchange) {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      return src != null ? src.getOriginalRequest().isSecure() : super.isConfidential(exchange);
   }
}
