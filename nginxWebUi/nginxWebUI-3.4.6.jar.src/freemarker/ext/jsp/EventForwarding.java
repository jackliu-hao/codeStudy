/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EventListener;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletContextAttributeEvent;
/*     */ import javax.servlet.ServletContextAttributeListener;
/*     */ import javax.servlet.ServletContextEvent;
/*     */ import javax.servlet.ServletContextListener;
/*     */ import javax.servlet.http.HttpSessionAttributeListener;
/*     */ import javax.servlet.http.HttpSessionBindingEvent;
/*     */ import javax.servlet.http.HttpSessionEvent;
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
/*     */ public class EventForwarding
/*     */   implements ServletContextAttributeListener, ServletContextListener, HttpSessionListener, HttpSessionAttributeListener
/*     */ {
/*  50 */   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
/*     */   
/*  52 */   private static final String ATTR_NAME = EventForwarding.class.getName();
/*     */   
/*  54 */   private final List servletContextAttributeListeners = new ArrayList();
/*  55 */   private final List servletContextListeners = new ArrayList();
/*  56 */   private final List httpSessionAttributeListeners = new ArrayList();
/*  57 */   private final List httpSessionListeners = new ArrayList();
/*     */   
/*     */   void addListeners(List listeners) {
/*  60 */     for (Iterator<EventListener> iter = listeners.iterator(); iter.hasNext();) {
/*  61 */       addListener(iter.next());
/*     */     }
/*     */   }
/*     */   
/*     */   private void addListener(EventListener listener) {
/*  66 */     boolean added = false;
/*  67 */     if (listener instanceof ServletContextAttributeListener) {
/*  68 */       addListener(this.servletContextAttributeListeners, listener);
/*  69 */       added = true;
/*     */     } 
/*  71 */     if (listener instanceof ServletContextListener) {
/*  72 */       addListener(this.servletContextListeners, listener);
/*  73 */       added = true;
/*     */     } 
/*  75 */     if (listener instanceof HttpSessionAttributeListener) {
/*  76 */       addListener(this.httpSessionAttributeListeners, listener);
/*  77 */       added = true;
/*     */     } 
/*  79 */     if (listener instanceof HttpSessionListener) {
/*  80 */       addListener(this.httpSessionListeners, listener);
/*  81 */       added = true;
/*     */     } 
/*  83 */     if (!added) {
/*  84 */       LOG.warn("Listener of class " + listener
/*  85 */           .getClass().getName() + "wasn't registered as it doesn't implement any of the recognized listener interfaces.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static EventForwarding getInstance(ServletContext context) {
/*  92 */     return (EventForwarding)context.getAttribute(ATTR_NAME);
/*     */   }
/*     */   private void addListener(List<EventListener> listeners, EventListener listener) {
/*  95 */     synchronized (listeners) {
/*  96 */       listeners.add(listener);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeAdded(ServletContextAttributeEvent arg0) {
/* 102 */     synchronized (this.servletContextAttributeListeners) {
/* 103 */       int s = this.servletContextAttributeListeners.size();
/* 104 */       for (int i = 0; i < s; i++) {
/* 105 */         ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeAdded(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeRemoved(ServletContextAttributeEvent arg0) {
/* 112 */     synchronized (this.servletContextAttributeListeners) {
/* 113 */       int s = this.servletContextAttributeListeners.size();
/* 114 */       for (int i = 0; i < s; i++) {
/* 115 */         ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeRemoved(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeReplaced(ServletContextAttributeEvent arg0) {
/* 122 */     synchronized (this.servletContextAttributeListeners) {
/* 123 */       int s = this.servletContextAttributeListeners.size();
/* 124 */       for (int i = 0; i < s; i++) {
/* 125 */         ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeReplaced(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void contextInitialized(ServletContextEvent arg0) {
/* 132 */     arg0.getServletContext().setAttribute(ATTR_NAME, this);
/*     */     
/* 134 */     synchronized (this.servletContextListeners) {
/* 135 */       int s = this.servletContextListeners.size();
/* 136 */       for (int i = 0; i < s; i++) {
/* 137 */         ((ServletContextListener)this.servletContextListeners.get(i)).contextInitialized(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void contextDestroyed(ServletContextEvent arg0) {
/* 144 */     synchronized (this.servletContextListeners) {
/* 145 */       int s = this.servletContextListeners.size();
/* 146 */       for (int i = s - 1; i >= 0; i--) {
/* 147 */         ((ServletContextListener)this.servletContextListeners.get(i)).contextDestroyed(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionCreated(HttpSessionEvent arg0) {
/* 154 */     synchronized (this.httpSessionListeners) {
/* 155 */       int s = this.httpSessionListeners.size();
/* 156 */       for (int i = 0; i < s; i++) {
/* 157 */         ((HttpSessionListener)this.httpSessionListeners.get(i)).sessionCreated(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionDestroyed(HttpSessionEvent arg0) {
/* 164 */     synchronized (this.httpSessionListeners) {
/* 165 */       int s = this.httpSessionListeners.size();
/* 166 */       for (int i = s - 1; i >= 0; i--) {
/* 167 */         ((HttpSessionListener)this.httpSessionListeners.get(i)).sessionDestroyed(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeAdded(HttpSessionBindingEvent arg0) {
/* 174 */     synchronized (this.httpSessionAttributeListeners) {
/* 175 */       int s = this.httpSessionAttributeListeners.size();
/* 176 */       for (int i = 0; i < s; i++) {
/* 177 */         ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeAdded(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeRemoved(HttpSessionBindingEvent arg0) {
/* 184 */     synchronized (this.httpSessionAttributeListeners) {
/* 185 */       int s = this.httpSessionAttributeListeners.size();
/* 186 */       for (int i = 0; i < s; i++) {
/* 187 */         ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeRemoved(arg0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attributeReplaced(HttpSessionBindingEvent arg0) {
/* 194 */     synchronized (this.httpSessionAttributeListeners) {
/* 195 */       int s = this.httpSessionAttributeListeners.size();
/* 196 */       for (int i = 0; i < s; i++)
/* 197 */         ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeReplaced(arg0); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\EventForwarding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */