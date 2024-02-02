/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModelEx;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
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
/*    */ class LazilyGeneratedCollectionModelWithSameSizeCollEx
/*    */   extends LazilyGeneratedCollectionModelEx
/*    */ {
/*    */   private final TemplateCollectionModelEx sizeSourceCollEx;
/*    */   
/*    */   public LazilyGeneratedCollectionModelWithSameSizeCollEx(TemplateModelIterator iterator, TemplateCollectionModelEx sizeSourceCollEx, boolean sequenceSourced) {
/* 36 */     super(iterator, sequenceSourced);
/* 37 */     NullArgumentException.check(sizeSourceCollEx);
/* 38 */     this.sizeSourceCollEx = sizeSourceCollEx;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 43 */     return this.sizeSourceCollEx.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() throws TemplateModelException {
/* 48 */     return this.sizeSourceCollEx.isEmpty();
/*    */   }
/*    */ 
/*    */   
/*    */   protected LazilyGeneratedCollectionModelWithSameSizeCollEx withIsSequenceFromFalseToTrue() {
/* 53 */     return new LazilyGeneratedCollectionModelWithSameSizeCollEx(getIterator(), this.sizeSourceCollEx, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazilyGeneratedCollectionModelWithSameSizeCollEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */