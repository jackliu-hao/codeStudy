/*    */ package io.undertow.server.handlers.builder;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.handlers.ResponseCodeHandler;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
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
/*    */ public class ResponseCodeHandlerBuilder
/*    */   implements HandlerBuilder
/*    */ {
/*    */   public String name() {
/* 36 */     return "response-code";
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Class<?>> parameters() {
/* 41 */     Map<String, Class<?>> parameters = new HashMap<>();
/* 42 */     parameters.put("value", Integer.class);
/* 43 */     return parameters;
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<String> requiredParameters() {
/* 48 */     Set<String> req = new HashSet<>();
/* 49 */     req.add("value");
/* 50 */     return req;
/*    */   }
/*    */ 
/*    */   
/*    */   public String defaultParameter() {
/* 55 */     return "value";
/*    */   }
/*    */ 
/*    */   
/*    */   public HandlerWrapper build(Map<String, Object> config) {
/* 60 */     final Integer value = (Integer)config.get("value");
/* 61 */     return new HandlerWrapper()
/*    */       {
/*    */         public HttpHandler wrap(HttpHandler handler) {
/* 64 */           return (HttpHandler)new ResponseCodeHandler(value.intValue());
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\ResponseCodeHandlerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */