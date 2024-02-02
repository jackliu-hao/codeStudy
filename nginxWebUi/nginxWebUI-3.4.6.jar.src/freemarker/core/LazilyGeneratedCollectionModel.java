/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelIterator;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class LazilyGeneratedCollectionModel
/*    */   extends SingleIterationCollectionModel
/*    */ {
/*    */   private final boolean sequence;
/*    */   
/*    */   protected LazilyGeneratedCollectionModel(TemplateModelIterator iterator, boolean sequence) {
/* 52 */     super(iterator);
/* 53 */     this.sequence = sequence;
/*    */   }
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
/*    */   final boolean isSequence() {
/* 67 */     return this.sequence;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   final LazilyGeneratedCollectionModel withIsSequenceTrue() {
/* 75 */     return isSequence() ? this : withIsSequenceFromFalseToTrue();
/*    */   }
/*    */   
/*    */   protected abstract LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazilyGeneratedCollectionModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */