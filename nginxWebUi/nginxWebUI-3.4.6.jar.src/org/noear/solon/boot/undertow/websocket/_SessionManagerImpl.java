/*    */ package org.noear.solon.boot.undertow.websocket;
/*    */ 
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import org.noear.solon.core.SignalType;
/*    */ import org.noear.solon.core.message.Session;
/*    */ import org.noear.solon.socketd.SessionManager;
/*    */ 
/*    */ public class _SessionManagerImpl
/*    */   extends SessionManager
/*    */ {
/*    */   protected SignalType signalType() {
/* 14 */     return SignalType.WEBSOCKET;
/*    */   }
/*    */ 
/*    */   
/*    */   public Session getSession(Object conn) {
/* 19 */     if (conn instanceof WebSocketChannel) {
/* 20 */       return _SocketServerSession.get((WebSocketChannel)conn);
/*    */     }
/* 22 */     throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Collection<Session> getOpenSessions() {
/* 28 */     return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeSession(Object conn) {
/* 33 */     if (conn instanceof WebSocketChannel) {
/* 34 */       _SocketServerSession.remove((WebSocketChannel)conn);
/*    */     } else {
/* 36 */       throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\websocket\_SessionManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */