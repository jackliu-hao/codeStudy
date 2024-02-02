/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.util.IteratorEnumeration;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.HttpSessionContext;
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
/*     */ public class HttpSessionImpl
/*     */   implements HttpSession
/*     */ {
/*  45 */   private static final RuntimePermission PERMISSION = new RuntimePermission("io.undertow.servlet.spec.UNWRAP_HTTP_SESSION");
/*     */   
/*     */   public static final String IO_UNDERTOW = "io.undertow";
/*     */   private final Session session;
/*     */   private final ServletContext servletContext;
/*     */   private final boolean newSession;
/*     */   private volatile boolean invalid;
/*     */   private final ServletRequestContext servletRequestContext;
/*     */   
/*     */   private HttpSessionImpl(Session session, ServletContext servletContext, boolean newSession, ServletRequestContext servletRequestContext) {
/*  55 */     this.session = session;
/*  56 */     this.servletContext = servletContext;
/*  57 */     this.newSession = newSession;
/*  58 */     this.servletRequestContext = servletRequestContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public static HttpSessionImpl forSession(Session session, ServletContext servletContext, boolean newSession) {
/*  63 */     ServletRequestContext current = ServletRequestContext.current();
/*  64 */     if (current == null) {
/*  65 */       return new HttpSessionImpl(session, servletContext, newSession, null);
/*     */     }
/*  67 */     HttpSessionImpl httpSession = current.getSession();
/*  68 */     if (httpSession == null) {
/*  69 */       httpSession = new HttpSessionImpl(session, servletContext, newSession, current);
/*  70 */       current.setSession(httpSession);
/*     */     }
/*  72 */     else if (httpSession.session != session) {
/*     */ 
/*     */       
/*  75 */       httpSession = new HttpSessionImpl(session, servletContext, newSession, current);
/*     */     } 
/*     */     
/*  78 */     return httpSession;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCreationTime() {
/*  84 */     return this.session.getCreationTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/*  89 */     return this.session.getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastAccessedTime() {
/*  94 */     return this.session.getLastAccessedTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletContext getServletContext() {
/*  99 */     return this.servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxInactiveInterval(int interval) {
/* 104 */     this.session.setMaxInactiveInterval(interval);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxInactiveInterval() {
/* 109 */     return this.session.getMaxInactiveInterval();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpSessionContext getSessionContext() {
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String name) {
/* 119 */     if (name.startsWith("io.undertow")) {
/* 120 */       throw new SecurityException();
/*     */     }
/* 122 */     return this.session.getAttribute(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValue(String name) {
/* 127 */     if (name.startsWith("io.undertow")) {
/* 128 */       throw new SecurityException();
/*     */     }
/* 130 */     return getAttribute(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration<String> getAttributeNames() {
/* 135 */     Set<String> attributeNames = getFilteredAttributeNames();
/* 136 */     return (Enumeration<String>)new IteratorEnumeration(attributeNames.iterator());
/*     */   }
/*     */   
/*     */   private Set<String> getFilteredAttributeNames() {
/* 140 */     Set<String> attributeNames = new HashSet<>(this.session.getAttributeNames());
/* 141 */     Iterator<String> it = attributeNames.iterator();
/* 142 */     while (it.hasNext()) {
/* 143 */       if (((String)it.next()).startsWith("io.undertow")) {
/* 144 */         it.remove();
/*     */       }
/*     */     } 
/* 147 */     return attributeNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getValueNames() {
/* 152 */     Set<String> names = getFilteredAttributeNames();
/* 153 */     String[] ret = new String[names.size()];
/* 154 */     int i = 0;
/* 155 */     for (String name : names) {
/* 156 */       ret[i++] = name;
/*     */     }
/* 158 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value) {
/* 163 */     if (name.startsWith("io.undertow")) {
/* 164 */       throw new SecurityException();
/*     */     }
/* 166 */     if (value == null) {
/* 167 */       removeAttribute(name);
/*     */     } else {
/* 169 */       this.session.setAttribute(name, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void putValue(String name, Object value) {
/* 175 */     setAttribute(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAttribute(String name) {
/* 180 */     if (name.startsWith("io.undertow")) {
/* 181 */       throw new SecurityException();
/*     */     }
/* 183 */     this.session.removeAttribute(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeValue(String name) {
/* 188 */     removeAttribute(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void invalidate() {
/* 193 */     this.invalid = true;
/* 194 */     if (this.servletRequestContext == null) {
/* 195 */       this.session.invalidate(null);
/*     */     }
/* 197 */     else if (this.servletRequestContext.getOriginalRequest().getServletContext() == this.servletContext) {
/* 198 */       this.session.invalidate(this.servletRequestContext.getOriginalRequest().getExchange());
/*     */     } else {
/* 200 */       this.session.invalidate(null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNew() {
/* 207 */     if (this.invalid) {
/* 208 */       throw UndertowServletMessages.MESSAGES.sessionIsInvalid();
/*     */     }
/* 210 */     return this.newSession;
/*     */   }
/*     */   
/*     */   public Session getSession() {
/* 214 */     SecurityManager sm = System.getSecurityManager();
/* 215 */     if (sm != null) {
/* 216 */       sm.checkPermission(PERMISSION);
/*     */     }
/* 218 */     return this.session;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 223 */     if (this == o) return true; 
/* 224 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 226 */     HttpSessionImpl that = (HttpSessionImpl)o;
/*     */     
/* 228 */     return this.session.getId().equals(that.session.getId());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 234 */     return this.session.getId().hashCode();
/*     */   }
/*     */   
/*     */   public boolean isInvalid() {
/* 238 */     if (this.invalid) {
/* 239 */       return true;
/*     */     }
/*     */     
/*     */     try {
/* 243 */       this.session.getMaxInactiveInterval();
/* 244 */       return false;
/* 245 */     } catch (IllegalStateException e) {
/* 246 */       return true;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class UnwrapSessionAction
/*     */     implements PrivilegedAction<Session> {
/*     */     private final HttpSessionImpl session;
/*     */     
/*     */     public UnwrapSessionAction(HttpSession session) {
/* 255 */       this.session = (HttpSessionImpl)session;
/*     */     }
/*     */ 
/*     */     
/*     */     public Session run() {
/* 260 */       return this.session.getSession();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\HttpSessionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */