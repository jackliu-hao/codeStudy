package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.core.ManagedServlet;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;

public class ServletHandler implements HttpHandler {
   private final ManagedServlet managedServlet;

   public ServletHandler(ManagedServlet managedServlet) {
      this.managedServlet = managedServlet;
   }

   public void handleRequest(HttpServerExchange exchange) throws IOException, ServletException {
      if (this.managedServlet.isPermanentlyUnavailable()) {
         UndertowServletLogger.REQUEST_LOGGER.debugf("Returning 404 for servlet %s due to permanent unavailability", this.managedServlet.getServletInfo().getName());
         exchange.setStatusCode(404);
      } else if (this.managedServlet.isTemporarilyUnavailable()) {
         UndertowServletLogger.REQUEST_LOGGER.debugf("Returning 503 for servlet %s due to temporary unavailability", this.managedServlet.getServletInfo().getName());
         exchange.setStatusCode(503);
      } else {
         ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         if (!this.managedServlet.getServletInfo().isAsyncSupported()) {
            servletRequestContext.setAsyncSupported(false);
         }

         ServletRequest request = servletRequestContext.getServletRequest();
         ServletResponse response = servletRequestContext.getServletResponse();
         InstanceHandle<? extends Servlet> servlet = null;

         try {
            servlet = this.managedServlet.getServlet();
            ((Servlet)servlet.getInstance()).service(request, response);
         } catch (UnavailableException var10) {
            this.managedServlet.handleUnavailableException(var10);
            if (var10.isPermanent()) {
               exchange.setStatusCode(404);
            } else {
               exchange.setStatusCode(503);
            }
         } finally {
            if (servlet != null) {
               servlet.release();
            }

         }

      }
   }

   public ManagedServlet getManagedServlet() {
      return this.managedServlet;
   }
}
