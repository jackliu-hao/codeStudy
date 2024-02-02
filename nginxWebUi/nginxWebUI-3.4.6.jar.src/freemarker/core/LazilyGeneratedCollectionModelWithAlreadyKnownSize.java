/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
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
/*    */ final class LazilyGeneratedCollectionModelWithAlreadyKnownSize
/*    */   extends LazilyGeneratedCollectionModelEx
/*    */ {
/*    */   private final int size;
/*    */   
/*    */   LazilyGeneratedCollectionModelWithAlreadyKnownSize(TemplateModelIterator iterator, int size, boolean sequence) {
/* 29 */     super(iterator, sequence);
/* 30 */     this.size = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 35 */     return this.size;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 40 */     return (this.size == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   protected LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue() {
/* 45 */     return new LazilyGeneratedCollectionModelWithAlreadyKnownSize(getIterator(), this.size, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazilyGeneratedCollectionModelWithAlreadyKnownSize.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */