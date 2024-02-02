/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import freemarker.template.utility.NullArgumentException;
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
/*    */ class LazilyGeneratedCollectionModelWithSameSizeSeq
/*    */   extends LazilyGeneratedCollectionModelEx
/*    */ {
/*    */   private final TemplateSequenceModel sizeSourceSeq;
/*    */   
/*    */   public LazilyGeneratedCollectionModelWithSameSizeSeq(TemplateModelIterator iterator, TemplateSequenceModel sizeSourceSeq) {
/* 36 */     super(iterator, true);
/* 37 */     NullArgumentException.check(sizeSourceSeq);
/* 38 */     this.sizeSourceSeq = sizeSourceSeq;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 43 */     return this.sizeSourceSeq.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() throws TemplateModelException {
/* 48 */     return (this.sizeSourceSeq.size() == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   protected LazilyGeneratedCollectionModelWithSameSizeSeq withIsSequenceFromFalseToTrue() {
/* 53 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazilyGeneratedCollectionModelWithSameSizeSeq.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */