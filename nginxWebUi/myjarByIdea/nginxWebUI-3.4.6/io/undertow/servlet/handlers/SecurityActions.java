package io.undertow.servlet.handlers;

import io.undertow.server.session.Session;
import io.undertow.servlet.spec.HttpSessionImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletContext;

class SecurityActions {
   static HttpSessionImpl forSession(final Session session, final ServletContext servletContext, final boolean newSession) {
      return System.getSecurityManager() == null ? HttpSessionImpl.forSession(session, servletContext, newSession) : (HttpSessionImpl)AccessController.doPrivileged(new PrivilegedAction<HttpSessionImpl>() {
         public HttpSessionImpl run() {
            return HttpSessionImpl.forSession(session, servletContext, newSession);
         }
      });
   }

   static ServletRequestContext requireCurrentServletRequestContext() {
      return System.getSecurityManager() == null ? ServletRequestContext.requireCurrent() : (ServletRequestContext)AccessController.doPrivileged(new PrivilegedAction<ServletRequestContext>() {
         public ServletRequestContext run() {
            return ServletRequestContext.requireCurrent();
         }
      });
   }
}
