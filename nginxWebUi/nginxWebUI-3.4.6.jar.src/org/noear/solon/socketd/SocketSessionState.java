/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.noear.solon.core.handle.SessionState;
/*    */ import org.noear.solon.core.message.Session;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SocketSessionState
/*    */   implements SessionState
/*    */ {
/*    */   Session session;
/*    */   
/*    */   public SocketSessionState(Session session) {
/* 15 */     this.session = session;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean replaceable() {
/* 20 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionId() {
/* 25 */     return this.session.sessionId();
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionChangeId() {
/* 30 */     return this.session.sessionId();
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<String> sessionKeys() {
/* 35 */     return this.session.attrMap().keySet();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object sessionGet(String key) {
/* 40 */     return this.session.attr(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionSet(String key, Object val) {
/* 45 */     if (val == null) {
/* 46 */       sessionRemove(key);
/*    */     } else {
/* 48 */       this.session.attrSet(key, val);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionRemove(String key) {
/* 54 */     this.session.attrMap().remove(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionClear() {
/* 59 */     this.session.attrMap().clear();
/*    */   }
/*    */   
/*    */   public void sessionReset() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SocketSessionState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */