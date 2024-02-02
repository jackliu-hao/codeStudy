/*    */ package org.antlr.v4.runtime.atn;
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
/*    */ public class EmptyPredictionContext
/*    */   extends SingletonPredictionContext
/*    */ {
/*    */   public EmptyPredictionContext() {
/* 35 */     super(null, 2147483647);
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 39 */     return true;
/*    */   }
/*    */   
/*    */   public int size() {
/* 43 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public PredictionContext getParent(int index) {
/* 48 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getReturnState(int index) {
/* 53 */     return this.returnState;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 58 */     return (this == o);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 63 */     return "$";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\EmptyPredictionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */