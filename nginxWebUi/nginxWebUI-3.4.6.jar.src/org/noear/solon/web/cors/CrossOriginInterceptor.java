/*    */ package org.noear.solon.web.cors;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.core.handle.Action;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ import org.noear.solon.web.cors.annotation.CrossOrigin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CrossOriginInterceptor
/*    */   implements Handler
/*    */ {
/* 16 */   Map<CrossOrigin, CrossHandler> handlerMap = new HashMap<>();
/*    */ 
/*    */   
/*    */   public void handle(Context ctx) throws Throwable {
/* 20 */     if (ctx.getHandled()) {
/*    */       return;
/*    */     }
/*    */     
/* 24 */     Action action = ctx.action();
/*    */     
/* 26 */     if (action != null) {
/* 27 */       CrossOrigin anno = (CrossOrigin)action.method().getAnnotation(CrossOrigin.class);
/* 28 */       if (anno == null) {
/* 29 */         anno = (CrossOrigin)action.controller().annotationGet(CrossOrigin.class);
/*    */       }
/*    */       
/* 32 */       if (anno == null) {
/*    */         return;
/*    */       }
/*    */       
/* 36 */       handleDo(ctx, anno);
/*    */     } 
/*    */   }
/*    */   
/*    */   protected void handleDo(Context ctx, CrossOrigin anno) throws Throwable {
/* 41 */     CrossHandler handler = this.handlerMap.get(anno);
/*    */     
/* 43 */     if (handler == null) {
/* 44 */       synchronized (anno) {
/* 45 */         handler = this.handlerMap.get(anno);
/*    */         
/* 47 */         if (handler == null) {
/* 48 */           handler = new CrossHandler(anno);
/*    */           
/* 50 */           this.handlerMap.put(anno, handler);
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 55 */     handler.handle(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\cors\CrossOriginInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */