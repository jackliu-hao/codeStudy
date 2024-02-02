/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletContextAttributeEvent;
/*     */ import javax.servlet.ServletContextAttributeListener;
/*     */ import javax.servlet.ServletContextEvent;
/*     */ import javax.servlet.ServletContextListener;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletRequestAttributeEvent;
/*     */ import javax.servlet.ServletRequestAttributeListener;
/*     */ import javax.servlet.ServletRequestEvent;
/*     */ import javax.servlet.ServletRequestListener;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionAttributeListener;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
/*     */ import javax.servlet.http.HttpSessionEvent;
/*     */ import javax.servlet.http.HttpSessionIdListener;
/*     */ import javax.servlet.http.HttpSessionListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ApplicationListeners
/*     */   implements Lifecycle
/*     */ {
/*  60 */   private static final ManagedListener[] EMPTY = new ManagedListener[0];
/*     */   
/*  62 */   private static final Class[] LISTENER_CLASSES = new Class[] { ServletContextListener.class, ServletContextAttributeListener.class, ServletRequestListener.class, ServletRequestAttributeListener.class, HttpSessionListener.class, HttpSessionAttributeListener.class, HttpSessionIdListener.class };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   private static final ThreadLocal<ListenerState> IN_PROGRAMATIC_SC_LISTENER_INVOCATION = new ThreadLocal<ListenerState>()
/*     */     {
/*     */       protected ApplicationListeners.ListenerState initialValue() {
/*  73 */         return ApplicationListeners.ListenerState.NO_LISTENER;
/*     */       }
/*     */     };
/*     */   
/*     */   private ServletContext servletContext;
/*  78 */   private final List<ManagedListener> allListeners = new ArrayList<>();
/*     */   private ManagedListener[] servletContextListeners;
/*     */   private ManagedListener[] servletContextAttributeListeners;
/*     */   private ManagedListener[] servletRequestListeners;
/*     */   private ManagedListener[] servletRequestAttributeListeners;
/*     */   private ManagedListener[] httpSessionListeners;
/*     */   private ManagedListener[] httpSessionAttributeListeners;
/*     */   private ManagedListener[] httpSessionIdListeners;
/*     */   private volatile boolean started = false;
/*     */   
/*     */   public ApplicationListeners(List<ManagedListener> allListeners, ServletContext servletContext) {
/*  89 */     this.servletContext = servletContext;
/*  90 */     this.servletContextListeners = EMPTY;
/*  91 */     this.servletContextAttributeListeners = EMPTY;
/*  92 */     this.servletRequestListeners = EMPTY;
/*  93 */     this.servletRequestAttributeListeners = EMPTY;
/*  94 */     this.httpSessionListeners = EMPTY;
/*  95 */     this.httpSessionAttributeListeners = EMPTY;
/*  96 */     this.httpSessionIdListeners = EMPTY;
/*  97 */     for (ManagedListener listener : allListeners) {
/*  98 */       addListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(ManagedListener listener) {
/* 103 */     if (ServletContextListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 104 */       ManagedListener[] old = this.servletContextListeners;
/* 105 */       this.servletContextListeners = new ManagedListener[old.length + 1];
/* 106 */       System.arraycopy(old, 0, this.servletContextListeners, 0, old.length);
/* 107 */       this.servletContextListeners[old.length] = listener;
/*     */     } 
/* 109 */     if (ServletContextAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/*     */       
/* 111 */       ManagedListener[] old = this.servletContextAttributeListeners;
/* 112 */       this.servletContextAttributeListeners = new ManagedListener[old.length + 1];
/* 113 */       System.arraycopy(old, 0, this.servletContextAttributeListeners, 0, old.length);
/* 114 */       this.servletContextAttributeListeners[old.length] = listener;
/*     */     } 
/* 116 */     if (ServletRequestListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 117 */       ManagedListener[] old = this.servletRequestListeners;
/* 118 */       this.servletRequestListeners = new ManagedListener[old.length + 1];
/* 119 */       System.arraycopy(old, 0, this.servletRequestListeners, 0, old.length);
/* 120 */       this.servletRequestListeners[old.length] = listener;
/*     */     } 
/* 122 */     if (ServletRequestAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 123 */       ManagedListener[] old = this.servletRequestAttributeListeners;
/* 124 */       this.servletRequestAttributeListeners = new ManagedListener[old.length + 1];
/* 125 */       System.arraycopy(old, 0, this.servletRequestAttributeListeners, 0, old.length);
/* 126 */       this.servletRequestAttributeListeners[old.length] = listener;
/*     */     } 
/* 128 */     if (HttpSessionListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 129 */       ManagedListener[] old = this.httpSessionListeners;
/* 130 */       this.httpSessionListeners = new ManagedListener[old.length + 1];
/* 131 */       System.arraycopy(old, 0, this.httpSessionListeners, 0, old.length);
/* 132 */       this.httpSessionListeners[old.length] = listener;
/*     */     } 
/* 134 */     if (HttpSessionAttributeListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 135 */       ManagedListener[] old = this.httpSessionAttributeListeners;
/* 136 */       this.httpSessionAttributeListeners = new ManagedListener[old.length + 1];
/* 137 */       System.arraycopy(old, 0, this.httpSessionAttributeListeners, 0, old.length);
/* 138 */       this.httpSessionAttributeListeners[old.length] = listener;
/*     */     } 
/* 140 */     if (HttpSessionIdListener.class.isAssignableFrom(listener.getListenerInfo().getListenerClass())) {
/* 141 */       ManagedListener[] old = this.httpSessionIdListeners;
/* 142 */       this.httpSessionIdListeners = new ManagedListener[old.length + 1];
/* 143 */       System.arraycopy(old, 0, this.httpSessionIdListeners, 0, old.length);
/* 144 */       this.httpSessionIdListeners[old.length] = listener;
/*     */     } 
/* 146 */     this.allListeners.add(listener);
/* 147 */     if (this.started) {
/*     */       try {
/* 149 */         listener.start();
/* 150 */       } catch (ServletException e) {
/* 151 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void start() throws ServletException {
/* 157 */     this.started = true;
/* 158 */     for (ManagedListener listener : this.allListeners) {
/* 159 */       listener.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public void stop() {
/* 164 */     if (this.started) {
/* 165 */       this.started = false;
/* 166 */       for (ManagedListener listener : this.allListeners) {
/* 167 */         listener.stop();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/* 174 */     return this.started;
/*     */   }
/*     */   
/*     */   public void contextInitialized() {
/* 178 */     if (!this.started) {
/*     */       return;
/*     */     }
/*     */     
/* 182 */     ServletContextEvent event = new ServletContextEvent(this.servletContext);
/* 183 */     for (int i = 0; i < this.servletContextListeners.length; i++) {
/* 184 */       ManagedListener listener = this.servletContextListeners[i];
/* 185 */       IN_PROGRAMATIC_SC_LISTENER_INVOCATION.set(listener.isProgramatic() ? ListenerState.PROGRAMATIC_LISTENER : ListenerState.DECLARED_LISTENER);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void contextDestroyed() {
/* 195 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 198 */     ServletContextEvent event = new ServletContextEvent(this.servletContext);
/* 199 */     for (int i = this.servletContextListeners.length - 1; i >= 0; i--) {
/* 200 */       ManagedListener listener = this.servletContextListeners[i];
/*     */       try {
/* 202 */         ((ServletContextListener)get(listener)).contextDestroyed(event);
/* 203 */       } catch (Throwable t) {
/* 204 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("contextDestroyed", listener.getListenerInfo().getListenerClass(), t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void servletContextAttributeAdded(String name, Object value) {
/* 210 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 213 */     ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);
/* 214 */     for (int i = 0; i < this.servletContextAttributeListeners.length; i++) {
/* 215 */       ManagedListener listener = this.servletContextAttributeListeners[i];
/*     */       try {
/* 217 */         ((ServletContextAttributeListener)get(listener)).attributeAdded(sre);
/* 218 */       } catch (Exception e) {
/* 219 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void servletContextAttributeRemoved(String name, Object value) {
/* 225 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 228 */     ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);
/* 229 */     for (int i = 0; i < this.servletContextAttributeListeners.length; i++) {
/* 230 */       ManagedListener listener = this.servletContextAttributeListeners[i];
/*     */       try {
/* 232 */         ((ServletContextAttributeListener)get(listener)).attributeRemoved(sre);
/* 233 */       } catch (Exception e) {
/* 234 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void servletContextAttributeReplaced(String name, Object value) {
/* 240 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 243 */     ServletContextAttributeEvent sre = new ServletContextAttributeEvent(this.servletContext, name, value);
/* 244 */     for (int i = 0; i < this.servletContextAttributeListeners.length; i++) {
/* 245 */       ManagedListener listener = this.servletContextAttributeListeners[i];
/*     */       try {
/* 247 */         ((ServletContextAttributeListener)get(listener)).attributeReplaced(sre);
/* 248 */       } catch (Exception e) {
/* 249 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void requestInitialized(ServletRequest request) {
/* 255 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 258 */     if (this.servletRequestListeners.length > 0) {
/* 259 */       int i = 0;
/* 260 */       ServletRequestEvent sre = new ServletRequestEvent(this.servletContext, request);
/*     */       try {
/* 262 */         for (; i < this.servletRequestListeners.length; i++) {
/* 263 */           ((ServletRequestListener)get(this.servletRequestListeners[i])).requestInitialized(sre);
/*     */         }
/* 265 */       } catch (RuntimeException e) {
/* 266 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestInitialized", this.servletRequestListeners[i].getListenerInfo().getListenerClass(), e);
/* 267 */         for (; i >= 0; i--) {
/* 268 */           ManagedListener listener = this.servletRequestListeners[i];
/*     */           try {
/* 270 */             ((ServletRequestListener)get(listener)).requestDestroyed(sre);
/* 271 */           } catch (Throwable t) {
/* 272 */             UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestDestroyed", listener.getListenerInfo().getListenerClass(), e);
/*     */           } 
/*     */         } 
/* 275 */         throw e;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void requestDestroyed(ServletRequest request) {
/* 281 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 284 */     if (this.servletRequestListeners.length > 0) {
/* 285 */       ServletRequestEvent sre = new ServletRequestEvent(this.servletContext, request);
/* 286 */       for (int i = this.servletRequestListeners.length - 1; i >= 0; i--) {
/* 287 */         ManagedListener listener = this.servletRequestListeners[i];
/*     */         try {
/* 289 */           ((ServletRequestListener)get(listener)).requestDestroyed(sre);
/* 290 */         } catch (Exception e) {
/* 291 */           UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("requestDestroyed", listener.getListenerInfo().getListenerClass(), e);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void servletRequestAttributeAdded(HttpServletRequest request, String name, Object value) {
/* 298 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 301 */     ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, (ServletRequest)request, name, value);
/* 302 */     for (int i = 0; i < this.servletRequestAttributeListeners.length; i++) {
/* 303 */       ManagedListener listener = this.servletRequestAttributeListeners[i];
/*     */       try {
/* 305 */         ((ServletRequestAttributeListener)get(listener)).attributeAdded(sre);
/* 306 */       } catch (Exception e) {
/* 307 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void servletRequestAttributeRemoved(HttpServletRequest request, String name, Object value) {
/* 314 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 317 */     ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, (ServletRequest)request, name, value);
/* 318 */     for (int i = 0; i < this.servletRequestAttributeListeners.length; i++) {
/* 319 */       ManagedListener listener = this.servletRequestAttributeListeners[i];
/*     */       try {
/* 321 */         ((ServletRequestAttributeListener)get(listener)).attributeRemoved(sre);
/* 322 */       } catch (Exception e) {
/* 323 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void servletRequestAttributeReplaced(HttpServletRequest request, String name, Object value) {
/* 329 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 332 */     ServletRequestAttributeEvent sre = new ServletRequestAttributeEvent(this.servletContext, (ServletRequest)request, name, value);
/* 333 */     for (int i = 0; i < this.servletRequestAttributeListeners.length; i++) {
/* 334 */       ManagedListener listener = this.servletRequestAttributeListeners[i];
/*     */       try {
/* 336 */         ((ServletRequestAttributeListener)get(listener)).attributeReplaced(sre);
/* 337 */       } catch (Exception e) {
/* 338 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sessionCreated(HttpSession session) {
/* 344 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 347 */     HttpSessionEvent sre = new HttpSessionEvent(session);
/* 348 */     for (int i = 0; i < this.httpSessionListeners.length; i++) {
/* 349 */       ManagedListener listener = this.httpSessionListeners[i];
/*     */       try {
/* 351 */         ((HttpSessionListener)get(listener)).sessionCreated(sre);
/* 352 */       } catch (Exception e) {
/* 353 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionCreated", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sessionDestroyed(HttpSession session) {
/* 359 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 362 */     HttpSessionEvent sre = new HttpSessionEvent(session);
/* 363 */     for (int i = this.httpSessionListeners.length - 1; i >= 0; i--) {
/* 364 */       ManagedListener listener = this.httpSessionListeners[i];
/*     */       try {
/* 366 */         ((HttpSessionListener)get(listener)).sessionDestroyed(sre);
/* 367 */       } catch (Exception e) {
/* 368 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionDestroyed", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void httpSessionAttributeAdded(HttpSession session, String name, Object value) {
/* 374 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 377 */     HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);
/* 378 */     for (int i = 0; i < this.httpSessionAttributeListeners.length; i++) {
/* 379 */       ManagedListener listener = this.httpSessionAttributeListeners[i];
/*     */       try {
/* 381 */         ((HttpSessionAttributeListener)get(listener)).attributeAdded(sre);
/* 382 */       } catch (Exception e) {
/* 383 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeAdded", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void httpSessionAttributeRemoved(HttpSession session, String name, Object value) {
/* 389 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 392 */     HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);
/* 393 */     for (int i = 0; i < this.httpSessionAttributeListeners.length; i++) {
/* 394 */       ManagedListener listener = this.httpSessionAttributeListeners[i];
/*     */       try {
/* 396 */         ((HttpSessionAttributeListener)get(this.httpSessionAttributeListeners[i])).attributeRemoved(sre);
/* 397 */       } catch (Exception e) {
/* 398 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeRemoved", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void httpSessionAttributeReplaced(HttpSession session, String name, Object value) {
/* 404 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 407 */     HttpSessionBindingEvent sre = new HttpSessionBindingEvent(session, name, value);
/* 408 */     for (int i = 0; i < this.httpSessionAttributeListeners.length; i++) {
/* 409 */       ManagedListener listener = this.httpSessionAttributeListeners[i];
/*     */       try {
/* 411 */         ((HttpSessionAttributeListener)get(listener)).attributeReplaced(sre);
/* 412 */       } catch (Exception e) {
/* 413 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("attributeReplaced", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void httpSessionIdChanged(HttpSession session, String oldSessionId) {
/* 419 */     if (!this.started) {
/*     */       return;
/*     */     }
/* 422 */     HttpSessionEvent sre = new HttpSessionEvent(session);
/* 423 */     for (int i = 0; i < this.httpSessionIdListeners.length; i++) {
/* 424 */       ManagedListener listener = this.httpSessionIdListeners[i];
/*     */       try {
/* 426 */         ((HttpSessionIdListener)get(listener)).sessionIdChanged(sre, oldSessionId);
/* 427 */       } catch (Exception e) {
/* 428 */         UndertowServletLogger.REQUEST_LOGGER.errorInvokingListener("sessionIdChanged", listener.getListenerInfo().getListenerClass(), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private <T> T get(ManagedListener listener) {
/* 434 */     return (T)listener.instance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListenerState listenerState() {
/* 441 */     return IN_PROGRAMATIC_SC_LISTENER_INVOCATION.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isListenerClass(Class<?> clazz) {
/* 449 */     for (Class c : LISTENER_CLASSES) {
/* 450 */       if (c.isAssignableFrom(clazz)) {
/* 451 */         return true;
/*     */       }
/*     */     } 
/* 454 */     return false;
/*     */   }
/*     */   
/*     */   public enum ListenerState {
/* 458 */     NO_LISTENER,
/* 459 */     DECLARED_LISTENER,
/* 460 */     PROGRAMATIC_LISTENER;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ApplicationListeners.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */