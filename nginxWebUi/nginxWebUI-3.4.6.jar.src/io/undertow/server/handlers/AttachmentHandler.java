/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.Handlers;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.AttachmentKey;
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
/*    */ public class AttachmentHandler<T>
/*    */   implements HttpHandler
/*    */ {
/*    */   private final AttachmentKey<T> key;
/*    */   private volatile T instance;
/*    */   private volatile HttpHandler next;
/*    */   
/*    */   public AttachmentHandler(AttachmentKey<T> key, HttpHandler next, T instance) {
/* 38 */     this.next = next;
/* 39 */     this.key = key;
/* 40 */     this.instance = instance;
/*    */   }
/*    */   
/*    */   public AttachmentHandler(AttachmentKey<T> key, HttpHandler next) {
/* 44 */     this(key, next, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 49 */     exchange.putAttachment(this.key, this.instance);
/* 50 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public T getInstance() {
/* 54 */     return this.instance;
/*    */   }
/*    */   
/*    */   public void setInstance(T instance) {
/* 58 */     this.instance = instance;
/*    */   }
/*    */   
/*    */   public HttpHandler getNext() {
/* 62 */     return this.next;
/*    */   }
/*    */   
/*    */   public void setNext(HttpHandler next) {
/* 66 */     Handlers.handlerNotNull(next);
/* 67 */     this.next = next;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\AttachmentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */