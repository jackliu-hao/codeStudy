package io.undertow.servlet.spec;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.util.IteratorEnumeration;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class HttpSessionImpl implements HttpSession {
   private static final RuntimePermission PERMISSION = new RuntimePermission("io.undertow.servlet.spec.UNWRAP_HTTP_SESSION");
   public static final String IO_UNDERTOW = "io.undertow";
   private final Session session;
   private final ServletContext servletContext;
   private final boolean newSession;
   private volatile boolean invalid;
   private final ServletRequestContext servletRequestContext;

   private HttpSessionImpl(Session session, ServletContext servletContext, boolean newSession, ServletRequestContext servletRequestContext) {
      this.session = session;
      this.servletContext = servletContext;
      this.newSession = newSession;
      this.servletRequestContext = servletRequestContext;
   }

   public static HttpSessionImpl forSession(Session session, ServletContext servletContext, boolean newSession) {
      ServletRequestContext current = ServletRequestContext.current();
      if (current == null) {
         return new HttpSessionImpl(session, servletContext, newSession, (ServletRequestContext)null);
      } else {
         HttpSessionImpl httpSession = current.getSession();
         if (httpSession == null) {
            httpSession = new HttpSessionImpl(session, servletContext, newSession, current);
            current.setSession(httpSession);
         } else if (httpSession.session != session) {
            httpSession = new HttpSessionImpl(session, servletContext, newSession, current);
         }

         return httpSession;
      }
   }

   public long getCreationTime() {
      return this.session.getCreationTime();
   }

   public String getId() {
      return this.session.getId();
   }

   public long getLastAccessedTime() {
      return this.session.getLastAccessedTime();
   }

   public ServletContext getServletContext() {
      return this.servletContext;
   }

   public void setMaxInactiveInterval(int interval) {
      this.session.setMaxInactiveInterval(interval);
   }

   public int getMaxInactiveInterval() {
      return this.session.getMaxInactiveInterval();
   }

   public HttpSessionContext getSessionContext() {
      return null;
   }

   public Object getAttribute(String name) {
      if (name.startsWith("io.undertow")) {
         throw new SecurityException();
      } else {
         return this.session.getAttribute(name);
      }
   }

   public Object getValue(String name) {
      if (name.startsWith("io.undertow")) {
         throw new SecurityException();
      } else {
         return this.getAttribute(name);
      }
   }

   public Enumeration<String> getAttributeNames() {
      Set<String> attributeNames = this.getFilteredAttributeNames();
      return new IteratorEnumeration(attributeNames.iterator());
   }

   private Set<String> getFilteredAttributeNames() {
      Set<String> attributeNames = new HashSet(this.session.getAttributeNames());
      Iterator<String> it = attributeNames.iterator();

      while(it.hasNext()) {
         if (((String)it.next()).startsWith("io.undertow")) {
            it.remove();
         }
      }

      return attributeNames;
   }

   public String[] getValueNames() {
      Set<String> names = this.getFilteredAttributeNames();
      String[] ret = new String[names.size()];
      int i = 0;

      String name;
      for(Iterator var4 = names.iterator(); var4.hasNext(); ret[i++] = name) {
         name = (String)var4.next();
      }

      return ret;
   }

   public void setAttribute(String name, Object value) {
      if (name.startsWith("io.undertow")) {
         throw new SecurityException();
      } else {
         if (value == null) {
            this.removeAttribute(name);
         } else {
            this.session.setAttribute(name, value);
         }

      }
   }

   public void putValue(String name, Object value) {
      this.setAttribute(name, value);
   }

   public void removeAttribute(String name) {
      if (name.startsWith("io.undertow")) {
         throw new SecurityException();
      } else {
         this.session.removeAttribute(name);
      }
   }

   public void removeValue(String name) {
      this.removeAttribute(name);
   }

   public void invalidate() {
      this.invalid = true;
      if (this.servletRequestContext == null) {
         this.session.invalidate((HttpServerExchange)null);
      } else if (this.servletRequestContext.getOriginalRequest().getServletContext() == this.servletContext) {
         this.session.invalidate(this.servletRequestContext.getOriginalRequest().getExchange());
      } else {
         this.session.invalidate((HttpServerExchange)null);
      }

   }

   public boolean isNew() {
      if (this.invalid) {
         throw UndertowServletMessages.MESSAGES.sessionIsInvalid();
      } else {
         return this.newSession;
      }
   }

   public Session getSession() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(PERMISSION);
      }

      return this.session;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         HttpSessionImpl that = (HttpSessionImpl)o;
         return this.session.getId().equals(that.session.getId());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.session.getId().hashCode();
   }

   public boolean isInvalid() {
      if (this.invalid) {
         return true;
      } else {
         try {
            this.session.getMaxInactiveInterval();
            return false;
         } catch (IllegalStateException var2) {
            return true;
         }
      }
   }

   public static class UnwrapSessionAction implements PrivilegedAction<Session> {
      private final HttpSessionImpl session;

      public UnwrapSessionAction(HttpSession session) {
         this.session = (HttpSessionImpl)session;
      }

      public Session run() {
         return this.session.getSession();
      }
   }
}
