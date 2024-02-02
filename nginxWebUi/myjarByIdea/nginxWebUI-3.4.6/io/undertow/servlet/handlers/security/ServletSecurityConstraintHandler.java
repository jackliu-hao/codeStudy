package io.undertow.servlet.handlers.security;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.ArrayList;
import java.util.List;

public class ServletSecurityConstraintHandler implements HttpHandler {
   private final SecurityPathMatches securityPathMatches;
   private final HttpHandler next;

   public ServletSecurityConstraintHandler(SecurityPathMatches securityPathMatches, HttpHandler next) {
      this.securityPathMatches = securityPathMatches;
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String path = exchange.getRelativePath();
      SecurityPathMatch securityMatch = this.securityPathMatches.getSecurityInfo(path, exchange.getRequestMethod().toString());
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      List<SingleConstraintMatch> list = servletRequestContext.getRequiredConstrains();
      if (list == null) {
         servletRequestContext.setRequiredConstrains((List)(list = new ArrayList()));
      }

      ((List)list).add(securityMatch.getMergedConstraint());
      TransportGuaranteeType type = servletRequestContext.getTransportGuarenteeType();
      if (type == null || type.ordinal() < securityMatch.getTransportGuaranteeType().ordinal()) {
         servletRequestContext.setTransportGuarenteeType(securityMatch.getTransportGuaranteeType());
      }

      UndertowLogger.SECURITY_LOGGER.debugf("Security constraints for request %s are %s", exchange.getRequestURI(), list);
      this.next.handleRequest(exchange);
   }
}
