/*    */ package org.noear.solon.core.route;
/*    */ 
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Endpoint;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RouterHandler
/*    */   implements Handler
/*    */ {
/*    */   private Router router;
/*    */   
/*    */   public RouterHandler(Router router) {
/* 18 */     bind(router);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void bind(Router router) {
/* 25 */     this.router = router;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handle(Context ctx) throws Throwable {
/* 32 */     if (ctx.getHandled()) {
/*    */       return;
/*    */     }
/*    */     
/* 36 */     boolean _handled = false;
/* 37 */     boolean _throwabled = false;
/*    */ 
/*    */     
/*    */     try {
/* 41 */       handleMultiple(ctx, Endpoint.before);
/*    */ 
/*    */       
/* 44 */       if (!ctx.getHandled()) {
/*    */         
/* 46 */         _handled = handleOne(ctx, Endpoint.main);
/*    */         
/* 48 */         ctx.setHandled(_handled);
/*    */       } 
/* 50 */     } catch (Throwable e) {
/* 51 */       _throwabled = true;
/* 52 */       if (ctx.errors == null) {
/* 53 */         ctx.errors = e;
/* 54 */         EventBus.push(e);
/*    */       } 
/* 56 */       throw e;
/*    */     } finally {
/*    */       
/* 59 */       handleMultiple(ctx, Endpoint.after);
/*    */ 
/*    */       
/* 62 */       if (!_throwabled && 
/* 63 */         ctx.status() < 1) {
/* 64 */         if (_handled) {
/* 65 */           ctx.status(200);
/*    */         } else {
/* 67 */           ctx.status(404);
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean handleOne(Context ctx, Endpoint endpoint) throws Throwable {
/* 78 */     Handler h = this.router.matchOne(ctx, endpoint);
/*    */     
/* 80 */     if (h != null) {
/* 81 */       h.handle(ctx);
/* 82 */       return (ctx.status() != 404);
/*    */     } 
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void handleMultiple(Context ctx, Endpoint endpoint) throws Throwable {
/* 92 */     for (Handler h : this.router.matchAll(ctx, endpoint))
/* 93 */       h.handle(ctx); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RouterHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */