/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.noear.solon.core.SignalType;
/*    */ import org.noear.solon.core.message.Session;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SessionManager
/*    */ {
/*    */   private static SessionManager socket;
/*    */   private static SessionManager websocket;
/*    */   
/*    */   public static void register(SessionManager factory) {
/* 19 */     if (factory.signalType() == SignalType.SOCKET) {
/* 20 */       socket = factory;
/*    */     }
/*    */     
/* 23 */     if (factory.signalType() == SignalType.WEBSOCKET) {
/* 24 */       websocket = factory;
/*    */     }
/*    */   }
/*    */   
/*    */   public static SessionManager socket() {
/* 29 */     if (socket == null) {
/* 30 */       throw new IllegalArgumentException("Socket session manager uninitialized");
/*    */     }
/*    */     
/* 33 */     return socket;
/*    */   }
/*    */   
/*    */   public static SessionManager websocket() {
/* 37 */     if (websocket == null) {
/* 38 */       throw new IllegalArgumentException("WeSocket session manager uninitialized");
/*    */     }
/*    */     
/* 41 */     return websocket;
/*    */   }
/*    */   
/*    */   protected abstract SignalType signalType();
/*    */   
/*    */   public abstract Session getSession(Object paramObject);
/*    */   
/*    */   public abstract Collection<Session> getOpenSessions();
/*    */   
/*    */   public abstract void removeSession(Object paramObject);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */