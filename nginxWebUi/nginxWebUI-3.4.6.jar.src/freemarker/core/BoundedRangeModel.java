/*    */ package freemarker.core;
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
/*    */ final class BoundedRangeModel
/*    */   extends RangeModel
/*    */ {
/*    */   private final int step;
/*    */   private final int size;
/*    */   private final boolean rightAdaptive;
/*    */   private final boolean affectedByStringSlicingBug;
/*    */   
/*    */   BoundedRangeModel(int begin, int end, boolean inclusiveEnd, boolean rightAdaptive) {
/* 38 */     super(begin);
/* 39 */     this.step = (begin <= end) ? 1 : -1;
/* 40 */     this.size = Math.abs(end - begin) + (inclusiveEnd ? 1 : 0);
/* 41 */     this.rightAdaptive = rightAdaptive;
/* 42 */     this.affectedByStringSlicingBug = inclusiveEnd;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 47 */     return this.size;
/*    */   }
/*    */ 
/*    */   
/*    */   int getStep() {
/* 52 */     return this.step;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isRightUnbounded() {
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isRightAdaptive() {
/* 62 */     return this.rightAdaptive;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isAffectedByStringSlicingBug() {
/* 67 */     return this.affectedByStringSlicingBug;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BoundedRangeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */