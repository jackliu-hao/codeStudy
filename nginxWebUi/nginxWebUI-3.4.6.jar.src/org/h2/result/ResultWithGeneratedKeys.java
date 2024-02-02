/*    */ package org.h2.result;
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
/*    */ public class ResultWithGeneratedKeys
/*    */ {
/*    */   private final long updateCount;
/*    */   
/*    */   public static final class WithKeys
/*    */     extends ResultWithGeneratedKeys
/*    */   {
/*    */     private final ResultInterface generatedKeys;
/*    */     
/*    */     public WithKeys(long param1Long, ResultInterface param1ResultInterface) {
/* 27 */       super(param1Long);
/* 28 */       this.generatedKeys = param1ResultInterface;
/*    */     }
/*    */ 
/*    */     
/*    */     public ResultInterface getGeneratedKeys() {
/* 33 */       return this.generatedKeys;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ResultWithGeneratedKeys of(long paramLong) {
/* 45 */     return new ResultWithGeneratedKeys(paramLong);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   ResultWithGeneratedKeys(long paramLong) {
/* 51 */     this.updateCount = paramLong;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResultInterface getGeneratedKeys() {
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getUpdateCount() {
/* 69 */     return this.updateCount;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultWithGeneratedKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */