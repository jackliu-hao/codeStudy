/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HandlerPipeline
/*    */   implements Handler
/*    */ {
/* 16 */   private List<Handler> chain = new LinkedList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HandlerPipeline next(Handler handler) {
/* 22 */     this.chain.add(handler);
/* 23 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HandlerPipeline prev(Handler handler) {
/* 30 */     this.chain.add(0, handler);
/* 31 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(Context ctx) throws Throwable {
/* 36 */     for (Handler h : this.chain) {
/* 37 */       if (ctx.getHandled()) {
/*    */         break;
/*    */       }
/*    */       
/* 41 */       h.handle(ctx);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\HandlerPipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */