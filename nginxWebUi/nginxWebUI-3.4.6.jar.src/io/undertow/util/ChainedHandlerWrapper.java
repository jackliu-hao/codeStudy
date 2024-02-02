/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import java.util.List;
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
/*    */ public class ChainedHandlerWrapper
/*    */   implements HandlerWrapper
/*    */ {
/*    */   private final List<HandlerWrapper> handlers;
/*    */   
/*    */   public ChainedHandlerWrapper(List<HandlerWrapper> handlers) {
/* 36 */     this.handlers = handlers;
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpHandler wrap(HttpHandler handler) {
/* 41 */     HttpHandler cur = handler;
/* 42 */     for (HandlerWrapper h : this.handlers) {
/* 43 */       cur = h.wrap(cur);
/*    */     }
/* 45 */     return cur;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ChainedHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */