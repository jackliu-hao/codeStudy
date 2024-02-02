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
/*    */ public class SingletonPredictionContext
/*    */   extends PredictionContext
/*    */ {
/*    */   public final PredictionContext parent;
/*    */   public final int returnState;
/*    */   
/*    */   SingletonPredictionContext(PredictionContext parent, int returnState) {
/* 38 */     super((parent != null) ? calculateHashCode(parent, returnState) : calculateEmptyHashCode());
/* 39 */     assert returnState != -1;
/* 40 */     this.parent = parent;
/* 41 */     this.returnState = returnState;
/*    */   }
/*    */   
/*    */   public static SingletonPredictionContext create(PredictionContext parent, int returnState) {
/* 45 */     if (returnState == Integer.MAX_VALUE && parent == null)
/*    */     {
/* 47 */       return EMPTY;
/*    */     }
/* 49 */     return new SingletonPredictionContext(parent, returnState);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 54 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public PredictionContext getParent(int index) {
/* 59 */     assert index == 0;
/* 60 */     return this.parent;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getReturnState(int index) {
/* 65 */     assert index == 0;
/* 66 */     return this.returnState;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 71 */     if (this == o) {
/* 72 */       return true;
/*    */     }
/* 74 */     if (!(o instanceof SingletonPredictionContext)) {
/* 75 */       return false;
/*    */     }
/*    */     
/* 78 */     if (hashCode() != o.hashCode()) {
/* 79 */       return false;
/*    */     }
/*    */     
/* 82 */     SingletonPredictionContext s = (SingletonPredictionContext)o;
/* 83 */     return (this.returnState == s.returnState && this.parent != null && this.parent.equals(s.parent));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 89 */     String up = (this.parent != null) ? this.parent.toString() : "";
/* 90 */     if (up.length() == 0) {
/* 91 */       if (this.returnState == Integer.MAX_VALUE) {
/* 92 */         return "$";
/*    */       }
/* 94 */       return String.valueOf(this.returnState);
/*    */     } 
/* 96 */     return String.valueOf(this.returnState) + " " + up;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\SingletonPredictionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */