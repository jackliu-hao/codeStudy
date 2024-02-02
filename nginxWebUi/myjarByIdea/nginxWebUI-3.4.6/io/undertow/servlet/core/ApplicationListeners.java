package io.undertow.servlet.core;

import io.undertow.servlet.UndertowServletLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

public class ApplicationListeners implements Lifecycle {
   private static final ManagedListener[] EMPTY = new ManagedListener[0];
   private static final Class[] LISTENER_CLASSES = new Class[]{ServletContextListener.class, ServletContextAttributeListener.class, ServletRequestListener.class, ServletRequestAttributeListener.class, HttpSessionListener.class, HttpSessionAttributeListener.class, HttpSessionIdListener.class};
   private static final ThreadLocal<ListenerState> IN_PROGRAMATIC_SC_LISTENER_INVOCATION = new ThreadLocal<ListenerState>() {
      protected ListenerState initialValue() {
         return ApplicationListeners.ListenerState.NO_LISTENER;
      }
   };
   private ServletContext servletContext;
   private final List<ManagedListener> allListeners = new ArrayList();
   private ManagedListener[] servletContextListeners;
   private ManagedListener[] servletContextAttributeListeners;
   private ManagedListener[] servletRequestListeners;
   private ManagedListener[] servletRequestAttributeListeners;
   private ManagedListener[] httpSessionListeners;
   private ManagedListener[] httpSessionAttributeListeners;
   private ManagedListener[] httpSessionIdListeners;
   private volatile boolean started = false;

   public ApplicationListeners(List<ManagedListener> allListeners, ServletContext servletContext) {
      this.servletContext = servletContext;
      this.servletContextListeners = EMPTY;
      this.servletContextAttributeListeners = EMPTY;
      this.servletRequestListeners = EMPTY;
      this.servletRequestAttributeListeners = EMPTY;
      this.httpSessionListeners = EMPTY;
      this.httpSessionAttributeListeners = EMPTY;
      this.httpSessionIdListeners = EMPTY;
      Iterator var3 = allListeners.iterator();

      while(var3.hasNext()) {
         ManagedListener listener = (ManagedListener)var3.next();
         this.addListener(listener);
      }

   }

   public void addListener(ManagedListener listener) {
      ManagedListener[] old;
      if (ServletContextListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.servletContextListeners;
         this.servletContextListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.servletContextListeners, 0, old.length);
         this.servletContextListeners[old.length] = listener;
      }

      if (ServletContextAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.servletContextAttributeListeners;
         this.servletContextAttributeListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.servletContextAttributeListeners, 0, old.length);
         this.servletContextAttributeListeners[old.length] = listener;
      }

      if (ServletRequestListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.servletRequestListeners;
         this.servletRequestListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.servletRequestListeners, 0, old.length);
         this.servletRequestListeners[old.length] = listener;
      }

      if (ServletRequestAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.servletRequestAttributeListeners;
         this.servletRequestAttributeListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.servletRequestAttributeListeners, 0, old.length);
         this.servletRequestAttributeListeners[old.length] = listener;
      }

      if (HttpSessionListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.httpSessionListeners;
         this.httpSessionListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.httpSessionListeners, 0, old.length);
         this.httpSessionListeners[old.length] = listener;
      }

      if (HttpSessionAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.httpSessionAttributeListeners;
         this.httpSessionAttributeListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.httpSessionAttributeListeners, 0, old.length);
         this.httpSessionAttributeListeners[old.length] = listener;
      }

      if (HttpSessionIdListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
         old = this.httpSessionIdListeners;
         this.httpSessionIdListeners = new ManagedListener[old.length + 1];
         System.arraycopy(old, 0, this.httpSessionIdListeners, 0, old.length);
         this.httpSessionIdListeners[old.length] = listener;
      }

      this.allListeners.add(listener);
      if (this.started) {
         try {
            listener.start();
         } catch (ServletException var3) {
            throw new RuntimeException(var3);
         }
      }

   }

   public void start() throws ServletException {
      this.started = true;
      Iterator var1 = this.allListeners.iterator();

      while(var1.hasNext()) {
         ManagedListener listener = (ManagedListener)var1.next();
         listener.start();
      }

   }

   public void stop() {
      if (this.started) {
         this.started = false;
         Iterator var1 = this.allListeners.iterator();

         while(var1.hasNext()) {
            ManagedListener listener = (ManagedListener)var1.next();
            listener.stop();
         }
      }

   }

   public boolean isStarted() {
      return this.started;
   }

   public void contextInitialized() {
      if (this.started) {
         ServletContextEvent event = new ServletContextEvent(this.servletContext);

         for(int i = 0; i < this.servletContextListeners.length; ++i) {
            ManagedListener listener = this.servletContextListeners[i];
            IN_PROGRAMATIC_SC_LISTENER_INVOCATION.set(listener.isProgramatic() ? ApplicationListeners.ListenerState.PROGRAMATIC_LISTENER : ApplicationListeners.ListenerState.DECLARED_LISTENER);

            try {
               ((ServletContextListener)this.get(listener)).contextInitialized(event);
            } finally {
               IN_PROGRAMATIC_SC_LISTENER_INVOCATION.remove();
            }
         }

      }
   }

   public void contextDestroyed() {
      if (this.started) {
         ServletContextEvent event = new ServletContextEvent(this.servletContext);

         for(int i = this.servletContextListeners.length - 1; i >= 0; --i) {
            ManagedListener listener = this.servletContextListeners[i];

            try {
               ((ServletContextListener)this.get(listener)).contextDestroyed(event);
            } catch (Throwable var5) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("contextDestroyed", listener.getListenerInfo().getListenerClass(), var5);
            }
         }

      }
   }

   public void servletContextAttributeAdded(String name, Object value) {
      if (this.started) {
         ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);

         for(int i = 0; i < this.servletContextAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletContextAttributeListeners[i];

            try {
               ((ServletContextAttributeListener)this.get(listener)).attributeAdded(sre);
            } catch (Exception var7) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), var7);
            }
         }

      }
   }

   public void servletContextAttributeRemoved(String name, Object value) {
      if (this.started) {
         ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);

         for(int i = 0; i < this.servletContextAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletContextAttributeListeners[i];

            try {
               ((ServletContextAttributeListener)this.get(listener)).attributeRemoved(sre);
            } catch (Exception var7) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), var7);
            }
         }

      }
   }

   public void servletContextAttributeReplaced(String name, Object value) {
      if (this.started) {
         ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);

         for(int i = 0; i < this.servletContextAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletContextAttributeListeners[i];

            try {
               ((ServletContextAttributeListener)this.get(listener)).attributeReplaced(sre);
            } catch (Exception var7) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), var7);
            }
         }

      }
   }

   public void requestInitialized(ServletRequest request) {
      if (this.started) {
         if (this.servletRequestListeners.length > 0) {
            int i = 0;
            ServletRequestEvent sre = new ServletRequestEvent(this.servletContext, request);

            try {
               while(i < this.servletRequestListeners.length) {
                  ((ServletRequestListener)this.get(this.servletRequestListeners[i])).requestInitialized(sre);
                  ++i;
               }
            } catch (RuntimeException var8) {
               RuntimeException e = var8;
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestInitialized", this.servletRequestListeners[i].getListenerInfo().getListenerClass(), var8);

               for(; i >= 0; --i) {
                  ManagedListener listener = this.servletRequestListeners[i];

                  try {
                     ((ServletRequestListener)this.get(listener)).requestDestroyed(sre);
                  } catch (Throwable var7) {
                     UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestDestroyed", listener.getListenerInfo().getListenerClass(), e);
                  }
               }

               throw e;
            }
         }

      }
   }

   public void requestDestroyed(ServletRequest request) {
      if (this.started) {
         if (this.servletRequestListeners.length > 0) {
            ServletRequestEvent sre = new ServletRequestEvent(this.servletContext, request);

            for(int i = this.servletRequestListeners.length - 1; i >= 0; --i) {
               ManagedListener listener = this.servletRequestListeners[i];

               try {
                  ((ServletRequestListener)this.get(listener)).requestDestroyed(sre);
               } catch (Exception var6) {
                  UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestDestroyed", listener.getListenerInfo().getListenerClass(), var6);
               }
            }
         }

      }
   }

   public void servletRequestAttributeAdded(HttpServletRequest request, String name, Object value) {
      if (this.started) {
         ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, request, name, value);

         for(int i = 0; i < this.servletRequestAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletRequestAttributeListeners[i];

            try {
               ((ServletRequestAttributeListener)this.get(listener)).attributeAdded(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void servletRequestAttributeRemoved(HttpServletRequest request, String name, Object value) {
      if (this.started) {
         ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, request, name, value);

         for(int i = 0; i < this.servletRequestAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletRequestAttributeListeners[i];

            try {
               ((ServletRequestAttributeListener)this.get(listener)).attributeRemoved(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void servletRequestAttributeReplaced(HttpServletRequest request, String name, Object value) {
      if (this.started) {
         ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, request, name, value);

         for(int i = 0; i < this.servletRequestAttributeListeners.length; ++i) {
            ManagedListener listener = this.servletRequestAttributeListeners[i];

            try {
               ((ServletRequestAttributeListener)this.get(listener)).attributeReplaced(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void sessionCreated(HttpSession session) {
      if (this.started) {
         HttpSessionEvent sre = new HttpSessionEvent(session);

         for(int i = 0; i < this.httpSessionListeners.length; ++i) {
            ManagedListener listener = this.httpSessionListeners[i];

            try {
               ((HttpSessionListener)this.get(listener)).sessionCreated(sre);
            } catch (Exception var6) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionCreated", listener.getListenerInfo().getListenerClass(), var6);
            }
         }

      }
   }

   public void sessionDestroyed(HttpSession session) {
      if (this.started) {
         HttpSessionEvent sre = new HttpSessionEvent(session);

         for(int i = this.httpSessionListeners.length - 1; i >= 0; --i) {
            ManagedListener listener = this.httpSessionListeners[i];

            try {
               ((HttpSessionListener)this.get(listener)).sessionDestroyed(sre);
            } catch (Exception var6) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionDestroyed", listener.getListenerInfo().getListenerClass(), var6);
            }
         }

      }
   }

   public void httpSessionAttributeAdded(HttpSession session, String name, Object value) {
      if (this.started) {
         HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);

         for(int i = 0; i < this.httpSessionAttributeListeners.length; ++i) {
            ManagedListener listener = this.httpSessionAttributeListeners[i];

            try {
               ((HttpSessionAttributeListener)this.get(listener)).attributeAdded(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void httpSessionAttributeRemoved(HttpSession session, String name, Object value) {
      if (this.started) {
         HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);

         for(int i = 0; i < this.httpSessionAttributeListeners.length; ++i) {
            ManagedListener listener = this.httpSessionAttributeListeners[i];

            try {
               ((HttpSessionAttributeListener)this.get(this.httpSessionAttributeListeners[i])).attributeRemoved(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void httpSessionAttributeReplaced(HttpSession session, String name, Object value) {
      if (this.started) {
         HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);

         for(int i = 0; i < this.httpSessionAttributeListeners.length; ++i) {
            ManagedListener listener = this.httpSessionAttributeListeners[i];

            try {
               ((HttpSessionAttributeListener)this.get(listener)).attributeReplaced(sre);
            } catch (Exception var8) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), var8);
            }
         }

      }
   }

   public void httpSessionIdChanged(HttpSession session, String oldSessionId) {
      if (this.started) {
         HttpSessionEvent sre = new HttpSessionEvent(session);

         for(int i = 0; i < this.httpSessionIdListeners.length; ++i) {
            ManagedListener listener = this.httpSessionIdListeners[i];

            try {
               ((HttpSessionIdListener)this.get(listener)).sessionIdChanged(sre, oldSessionId);
            } catch (Exception var7) {
               UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionIdChanged", listener.getListenerInfo().getListenerClass(), var7);
            }
         }

      }
   }

   private <T> T get(ManagedListener listener) {
      return listener.instance();
   }

   public static ListenerState listenerState() {
      return (ListenerState)IN_PROGRAMATIC_SC_LISTENER_INVOCATION.get();
   }

   public static boolean isListenerClass(Class<?> clazz) {
      Class[] var1 = LISTENER_CLASSES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Class c = var1[var3];
         if (c.isAssignableFrom(clazz)) {
            return true;
         }
      }

      return false;
   }

   public static enum ListenerState {
      NO_LISTENER,
      DECLARED_LISTENER,
      PROGRAMATIC_LISTENER;
   }
}
