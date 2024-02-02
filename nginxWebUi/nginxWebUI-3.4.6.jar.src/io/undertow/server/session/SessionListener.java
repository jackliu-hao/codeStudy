/*    */ package io.undertow.server.session;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public interface SessionListener
/*    */ {
/*    */   default void sessionCreated(Session session, HttpServerExchange exchange) {}
/*    */   
/*    */   default void sessionDestroyed(Session session, HttpServerExchange exchange, SessionDestroyedReason reason) {}
/*    */   
/*    */   default void attributeAdded(Session session, String name, Object value) {}
/*    */   
/*    */   default void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {}
/*    */   
/*    */   default void attributeRemoved(Session session, String name, Object oldValue) {}
/*    */   
/*    */   default void sessionIdChanged(Session session, String oldSessionId) {}
/*    */   
/*    */   public enum SessionDestroyedReason
/*    */   {
/* 62 */     INVALIDATED,
/* 63 */     TIMEOUT,
/* 64 */     UNDEPLOY;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */