/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.io.IOException;
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
/*    */ public class DefaultResourceSupplier
/*    */   implements ResourceSupplier
/*    */ {
/*    */   private final ResourceManager resourceManager;
/*    */   
/*    */   public DefaultResourceSupplier(ResourceManager resourceManager) {
/* 34 */     this.resourceManager = resourceManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public Resource getResource(HttpServerExchange exchange, String path) throws IOException {
/* 39 */     return this.resourceManager.getResource(path);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\DefaultResourceSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */