/*    */ package org.noear.solon.core.message;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ListenerPipeline
/*    */   implements Listener
/*    */ {
/* 15 */   private List<Listener> chain = new LinkedList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ListenerPipeline next(Listener listener) {
/* 21 */     this.chain.add(listener);
/* 22 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ListenerPipeline prev(Listener listener) {
/* 29 */     this.chain.add(0, listener);
/* 30 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onOpen(Session session) {
/* 38 */     for (Listener l : this.chain) {
/* 39 */       l.onOpen(session);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onMessage(Session session, Message message) throws IOException {
/* 48 */     for (Listener l : this.chain) {
/* 49 */       l.onMessage(session, message);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onClose(Session session) {
/* 58 */     for (Listener l : this.chain) {
/* 59 */       l.onClose(session);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onError(Session session, Throwable error) {
/* 68 */     for (Listener l : this.chain)
/* 69 */       l.onError(session, error); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\ListenerPipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */