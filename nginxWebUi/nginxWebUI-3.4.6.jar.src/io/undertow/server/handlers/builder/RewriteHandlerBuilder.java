/*    */ package io.undertow.server.handlers.builder;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.attribute.ExchangeAttributes;
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.SetAttributeHandler;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ public class RewriteHandlerBuilder
/*    */   implements HandlerBuilder
/*    */ {
/*    */   public String name() {
/* 39 */     return "rewrite";
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Class<?>> parameters() {
/* 44 */     return Collections.singletonMap("value", ExchangeAttribute.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<String> requiredParameters() {
/* 49 */     return Collections.singleton("value");
/*    */   }
/*    */ 
/*    */   
/*    */   public String defaultParameter() {
/* 54 */     return "value";
/*    */   }
/*    */ 
/*    */   
/*    */   public HandlerWrapper build(Map<String, Object> config) {
/* 59 */     final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/*    */     
/* 61 */     return new HandlerWrapper()
/*    */       {
/*    */         public HttpHandler wrap(HttpHandler handler) {
/* 64 */           return (HttpHandler)new SetAttributeHandler(handler, ExchangeAttributes.relativePath(), value)
/*    */             {
/*    */               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 67 */                 UndertowLogger.PREDICATE_LOGGER.debugf("Request rewritten to [%s] for %s.", getValue().readAttribute(exchange), exchange);
/* 68 */                 super.handleRequest(exchange);
/*    */               }
/*    */               
/*    */               public String toString() {
/* 72 */                 return "rewrite( '" + getValue().toString() + "' )";
/*    */               }
/*    */             };
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\RewriteHandlerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */