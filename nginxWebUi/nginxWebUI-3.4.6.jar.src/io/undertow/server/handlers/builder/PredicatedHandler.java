/*    */ package io.undertow.server.handlers.builder;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
/*    */ import io.undertow.server.HandlerWrapper;
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
/*    */ public class PredicatedHandler
/*    */ {
/*    */   private final Predicate predicate;
/*    */   private final HandlerWrapper handler;
/*    */   private final HandlerWrapper elseHandler;
/*    */   
/*    */   public PredicatedHandler(Predicate predicate, HandlerWrapper handler) {
/* 33 */     this(predicate, handler, null);
/*    */   }
/*    */   
/*    */   public PredicatedHandler(Predicate predicate, HandlerWrapper handler, HandlerWrapper elseHandler) {
/* 37 */     this.predicate = predicate;
/* 38 */     this.handler = handler;
/* 39 */     this.elseHandler = elseHandler;
/*    */   }
/*    */   
/*    */   public Predicate getPredicate() {
/* 43 */     return this.predicate;
/*    */   }
/*    */   
/*    */   public HandlerWrapper getHandler() {
/* 47 */     return this.handler;
/*    */   }
/*    */   
/*    */   public HandlerWrapper getElseHandler() {
/* 51 */     return this.elseHandler;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\PredicatedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */