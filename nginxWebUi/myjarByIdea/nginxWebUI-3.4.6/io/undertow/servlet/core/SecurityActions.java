package io.undertow.servlet.core;

import io.undertow.server.HttpHandler;
import io.undertow.server.session.Session;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.handlers.ServletInitialHandler;
import io.undertow.servlet.handlers.ServletPathMatches;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletContext;

final class SecurityActions {
   private SecurityActions() {
   }

   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   static void setContextClassLoader(final ClassLoader classLoader) {
      if (System.getSecurityManager() == null) {
         Thread.currentThread().setContextClassLoader(classLoader);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               Thread.currentThread().setContextClassLoader(classLoader);
               return null;
            }
         });
      }

   }

   static String getSystemProperty(final String prop) {
      return System.getSecurityManager() == null ? System.getProperty(prop) : (String)AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            return System.getProperty(prop);
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

   static ServletRequestContext currentServletRequestContext() {
      return System.getSecurityManager() == null ? ServletRequestContext.current() : (ServletRequestContext)AccessController.doPrivileged(new PrivilegedAction<ServletRequestContext>() {
         public ServletRequestContext run() {
            return ServletRequestContext.current();
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

   static ServletInitialHandler createServletInitialHandler(final ServletPathMatches paths, final HttpHandler next, final Deployment deployment, final ServletContextImpl servletContext) {
      return System.getSecurityManager() == null ? new ServletInitialHandler(paths, next, deployment, servletContext) : (ServletInitialHandler)AccessController.doPrivileged(new PrivilegedAction<ServletInitialHandler>() {
         public ServletInitialHandler run() {
            return new ServletInitialHandler(paths, next, deployment, servletContext);
         }
      });
   }
}
