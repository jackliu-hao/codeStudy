/*    */ package io.undertow.server.session;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.ListIterator;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SessionListeners
/*    */ {
/* 36 */   private final List<SessionListener> sessionListeners = new CopyOnWriteArrayList<>();
/*    */   
/*    */   public void addSessionListener(SessionListener listener) {
/* 39 */     this.sessionListeners.add(listener);
/*    */   }
/*    */   
/*    */   public boolean removeSessionListener(SessionListener listener) {
/* 43 */     return this.sessionListeners.remove(listener);
/*    */   }
/*    */   
/*    */   public void clear() {
/* 47 */     this.sessionListeners.clear();
/*    */   }
/*    */   
/*    */   public void sessionCreated(Session session, HttpServerExchange exchange) {
/* 51 */     for (SessionListener listener : this.sessionListeners) {
/* 52 */       listener.sessionCreated(session, exchange);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
/* 58 */     List<SessionListener> listeners = new ArrayList<>(this.sessionListeners);
/* 59 */     ListIterator<SessionListener> iterator = listeners.listIterator(listeners.size());
/* 60 */     while (iterator.hasPrevious()) {
/* 61 */       ((SessionListener)iterator.previous()).sessionDestroyed(session, exchange, reason);
/*    */     }
/*    */   }
/*    */   
/*    */   public void attributeAdded(Session session, String name, Object value) {
/* 66 */     for (SessionListener listener : this.sessionListeners) {
/* 67 */       listener.attributeAdded(session, name, value);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
/* 73 */     for (SessionListener listener : this.sessionListeners) {
/* 74 */       listener.attributeUpdated(session, name, newValue, oldValue);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void attributeRemoved(Session session, String name, Object oldValue) {
/* 80 */     for (SessionListener listener : this.sessionListeners) {
/* 81 */       listener.attributeRemoved(session, name, oldValue);
/*    */     }
/*    */   }
/*    */   
/*    */   public void sessionIdChanged(Session session, String oldSessionId) {
/* 86 */     for (SessionListener listener : this.sessionListeners)
/* 87 */       listener.sessionIdChanged(session, oldSessionId); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionListeners.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */