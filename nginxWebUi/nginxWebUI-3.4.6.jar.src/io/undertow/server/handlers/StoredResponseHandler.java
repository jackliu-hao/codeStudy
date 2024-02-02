/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.conduits.StoredResponseStreamSinkConduit;
/*    */ import io.undertow.server.ConduitWrapper;
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import io.undertow.util.ConduitFactory;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.xnio.conduits.Conduit;
/*    */ import org.xnio.conduits.StreamSinkConduit;
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
/*    */ public class StoredResponseHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public StoredResponseHandler(HttpHandler next) {
/* 48 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 53 */     exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*    */         {
/*    */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 56 */             return (StreamSinkConduit)new StoredResponseStreamSinkConduit((StreamSinkConduit)factory.create(), exchange);
/*    */           }
/*    */         });
/* 59 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "store-response()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 71 */       return "store-response";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 76 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 81 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 86 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 91 */       return new HandlerWrapper()
/*    */         {
/*    */           public HttpHandler wrap(HttpHandler handler) {
/* 94 */             return new StoredResponseHandler(handler);
/*    */           }
/*    */         };
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\StoredResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */