/*    */ package org.antlr.v4.runtime.atn;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PredictionContextCache
/*    */ {
/* 41 */   protected final Map<PredictionContext, PredictionContext> cache = new HashMap<PredictionContext, PredictionContext>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PredictionContext add(PredictionContext ctx) {
/* 49 */     if (ctx == PredictionContext.EMPTY) return PredictionContext.EMPTY; 
/* 50 */     PredictionContext existing = this.cache.get(ctx);
/* 51 */     if (existing != null)
/*    */     {
/* 53 */       return existing;
/*    */     }
/* 55 */     this.cache.put(ctx, ctx);
/* 56 */     return ctx;
/*    */   }
/*    */   
/*    */   public PredictionContext get(PredictionContext ctx) {
/* 60 */     return this.cache.get(ctx);
/*    */   }
/*    */   
/*    */   public int size() {
/* 64 */     return this.cache.size();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PredictionContextCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */