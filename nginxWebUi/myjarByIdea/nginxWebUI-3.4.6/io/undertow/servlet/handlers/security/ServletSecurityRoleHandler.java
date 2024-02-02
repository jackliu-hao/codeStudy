package io.undertow.servlet.handlers.security;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.AuthorizationManager;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletSecurityRoleHandler implements HttpHandler {
   private final HttpHandler next;
   private final AuthorizationManager authorizationManager;

   public ServletSecurityRoleHandler(HttpHandler next, AuthorizationManager authorizationManager) {
      this.next = next;
      this.authorizationManager = authorizationManager;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      ServletRequest request = servletRequestContext.getServletRequest();
      if (request.getDispatcherType() == DispatcherType.REQUEST) {
         List<SingleConstraintMatch> constraints = servletRequestContext.getRequiredConstrains();
         SecurityContext sc = exchange.getSecurityContext();
         if (!this.authorizationManager.canAccessResource(constraints, sc.getAuthenticatedAccount(), servletRequestContext.getCurrentServlet().getManagedServlet().getServletInfo(), servletRequestContext.getOriginalRequest(), servletRequestContext.getDeployment())) {
            HttpServletResponse response = (HttpServletResponse)servletRequestContext.getServletResponse();
            response.sendError(403);
            return;
         }
      }

      this.next.handleRequest(exchange);
   }
}
