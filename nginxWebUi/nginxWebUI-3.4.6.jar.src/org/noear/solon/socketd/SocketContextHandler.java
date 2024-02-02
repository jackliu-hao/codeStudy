/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.message.Message;
/*    */ import org.noear.solon.core.message.Session;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SocketContextHandler
/*    */ {
/* 16 */   public static final SocketContextHandler instance = new SocketContextHandler();
/*    */   
/*    */   public void handle(Session session, Message message) {
/* 19 */     if (message == null) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 24 */     if (Utils.isEmpty(message.resourceDescriptor())) {
/*    */       return;
/*    */     }
/*    */     
/*    */     try {
/* 29 */       SocketContext ctx = new SocketContext(session, message);
/*    */       
/* 31 */       Solon.app().tryHandle((Context)ctx);
/*    */       
/* 33 */       if (ctx.getHandled() || ctx.status() != 404) {
/* 34 */         ctx.commit();
/*    */       }
/* 36 */     } catch (Throwable ex) {
/*    */ 
/*    */       
/* 39 */       EventBus.push(ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SocketContextHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */