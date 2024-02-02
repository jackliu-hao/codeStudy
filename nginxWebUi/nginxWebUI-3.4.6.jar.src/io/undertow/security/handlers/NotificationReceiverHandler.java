/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.security.api.NotificationReceiver;
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.Collection;
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
/*    */ public class NotificationReceiverHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   private final NotificationReceiver[] receivers;
/*    */   
/*    */   public NotificationReceiverHandler(HttpHandler next, Collection<NotificationReceiver> receivers) {
/* 39 */     this.next = next;
/* 40 */     this.receivers = receivers.<NotificationReceiver>toArray(new NotificationReceiver[receivers.size()]);
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 45 */     SecurityContext sc = exchange.getSecurityContext();
/* 46 */     for (int i = 0; i < this.receivers.length; i++) {
/* 47 */       sc.registerNotificationReceiver(this.receivers[i]);
/*    */     }
/*    */     
/* 50 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\NotificationReceiverHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */