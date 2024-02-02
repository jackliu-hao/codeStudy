package io.undertow.servlet.spec;

import io.undertow.server.session.Session;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletContext;

class SecurityActions {
   static ServletRequestContext currentServletRequestContext() {
      return System.getSecurityManager() == null ? ServletRequestContext.current() : (ServletRequestContext)AccessController.doPrivileged(new PrivilegedAction<ServletRequestContext>() {
         public ServletRequestContext run() {
            return ServletRequestContext.current();
         }
      });
   }

   static HttpSessionImpl forSession(final Session session, final ServletContext servletContext, final boolean newSession) {
      return System.getSecurityManager() == null ? HttpSessionImpl.forSession(session, servletContext, newSession) : (HttpSessionImpl)AccessController.doPrivileged(new PrivilegedAction<HttpSessionImpl>() {
         public HttpSessionImpl run() {
            return HttpSessionImpl.forSession(session, servletContext, newSession);
         }
      });
   }

   static void setCurrentRequestContext(final ServletRequestContext servletRequestContext) {
      if (System.getSecurityManager() == null) {
         ServletRequestContext.setCurrentRequestContext(servletRequestContext);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               ServletRequestContext.setCurrentRequestContext(servletRequestContext);
               return null;
            }
         });
      }

   }

   static void clearCurrentServletAttachments() {
      if (System.getSecurityManager() == null) {
         ServletRequestContext.clearCurrentServletAttachments();
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               ServletRequestContext.clearCurrentServletAttachments();
               return null;
            }
         });
      }

   }

   static ServletRequestContext requireCurrentServletRequestContext() {
      return System.getSecurityManager() == null ? ServletRequestContext.requireCurrent() : (ServletRequestContext)AccessController.doPrivileged(new PrivilegedAction<ServletRequestContext>() {
         public ServletRequestContext run() {
            return ServletRequestContext.requireCurrent();
         }
      });
   }
}
