package io.undertow.servlet.handlers.security;

import io.undertow.security.impl.SingleSignOnAuthenticationMechanism;
import io.undertow.security.impl.SingleSignOnManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import java.security.AccessController;

public class ServletSingleSignOnAuthenticationMechanism extends SingleSignOnAuthenticationMechanism {
   public ServletSingleSignOnAuthenticationMechanism(SingleSignOnManager storage) {
      super(storage);
   }

   protected Session getSession(HttpServerExchange exchange) {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      HttpSessionImpl session = servletRequestContext.getCurrentServletContext().getSession(exchange, true);
      return System.getSecurityManager() == null ? session.getSession() : (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(session));
   }
}
