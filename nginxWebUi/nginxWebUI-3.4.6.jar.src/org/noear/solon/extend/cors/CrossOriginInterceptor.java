/*    */ package org.noear.solon.extend.cors;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.core.handle.Action;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ import org.noear.solon.extend.cors.annotation.CrossOrigin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class CrossOriginInterceptor
/*    */   implements Handler
/*    */ {
/* 17 */   Map<CrossOrigin, CrossHandler> handlerMap = new HashMap<>();
/*    */ 
/*    */   
/*    */   public void handle(Context ctx) throws Throwable {
/* 21 */     if (ctx.getHandled()) {
/*    */       return;
/*    */     }
/*    */     
/* 25 */     Action action = ctx.action();
/*    */     
/* 27 */     if (action != null) {
/* 28 */       CrossOrigin anno = (CrossOrigin)action.method().getAnnotation(CrossOrigin.class);
/* 29 */       if (anno == null) {
/* 30 */         anno = (CrossOrigin)action.controller().annotationGet(CrossOrigin.class);
/*    */       }
/*    */       
/* 33 */       if (anno == null) {
/*    */         return;
/*    */       }
/*    */       
/* 37 */       handleDo(ctx, anno);
/*    */     } 
/*    */   }
/*    */   
/*    */   protected void handleDo(Context ctx, CrossOrigin anno) throws Throwable {
/* 42 */     CrossHandler handler = this.handlerMap.get(anno);
/*    */     
/* 44 */     if (handler == null) {
/* 45 */       synchronized (anno) {
/* 46 */         handler = this.handlerMap.get(anno);
/*    */         
/* 48 */         if (handler == null) {
/* 49 */           handler = new CrossHandler(anno);
/*    */           
/* 51 */           this.handlerMap.put(anno, handler);
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 56 */     handler.handle(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\extend\cors\CrossOriginInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */