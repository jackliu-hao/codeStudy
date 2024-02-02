/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.AttachmentKey;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionHandler
/*    */   implements HttpHandler
/*    */ {
/* 17 */   public static final AttachmentKey<Throwable> THROWABLE = AttachmentKey.create(Throwable.class);
/*    */   
/*    */   private final HttpHandler handler;
/* 20 */   private final List<ExceptionHandlerHolder<?>> exceptionHandlers = new CopyOnWriteArrayList<>();
/*    */   
/*    */   public ExceptionHandler(HttpHandler handler) {
/* 23 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*    */     try {
/* 29 */       this.handler.handleRequest(exchange);
/* 30 */     } catch (Throwable throwable) {
/* 31 */       for (ExceptionHandlerHolder<?> holder : this.exceptionHandlers) {
/* 32 */         if (holder.getClazz().isInstance(throwable)) {
/* 33 */           exchange.putAttachment(THROWABLE, throwable);
/* 34 */           holder.getHandler().handleRequest(exchange);
/*    */           return;
/*    */         } 
/*    */       } 
/* 38 */       throw throwable;
/*    */     } 
/*    */   }
/*    */   
/*    */   public <T extends Throwable> ExceptionHandler addExceptionHandler(Class<T> clazz, HttpHandler handler) {
/* 43 */     this.exceptionHandlers.add(new ExceptionHandlerHolder(clazz, handler));
/* 44 */     return this;
/*    */   }
/*    */   
/*    */   private static class ExceptionHandlerHolder<T extends Throwable> {
/*    */     private final Class<T> clazz;
/*    */     private final HttpHandler handler;
/*    */     
/*    */     ExceptionHandlerHolder(Class<T> clazz, HttpHandler handler) {
/* 52 */       this.clazz = clazz;
/* 53 */       this.handler = handler;
/*    */     }
/*    */     public Class<T> getClazz() {
/* 56 */       return this.clazz;
/*    */     }
/*    */     public HttpHandler getHandler() {
/* 59 */       return this.handler;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */