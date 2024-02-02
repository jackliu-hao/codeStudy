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
/*    */ abstract class RightUnboundedRangeModel
/*    */   extends RangeModel
/*    */ {
/*    */   RightUnboundedRangeModel(int begin) {
/* 25 */     super(begin);
/*    */   }
/*    */ 
/*    */   
/*    */   final int getStep() {
/* 30 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   final boolean isRightUnbounded() {
/* 35 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   final boolean isRightAdaptive() {
/* 40 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   final boolean isAffectedByStringSlicingBug() {
/* 45 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RightUnboundedRangeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */