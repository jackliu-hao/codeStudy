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
/*    */ final class LazilyGeneratedCollectionModelWithUnknownSize
/*    */   extends LazilyGeneratedCollectionModel
/*    */ {
/*    */   public LazilyGeneratedCollectionModelWithUnknownSize(TemplateModelIterator iterator, boolean sequence) {
/* 26 */     super(iterator, sequence);
/*    */   }
/*    */ 
/*    */   
/*    */   protected LazilyGeneratedCollectionModelWithUnknownSize withIsSequenceFromFalseToTrue() {
/* 31 */     return new LazilyGeneratedCollectionModelWithUnknownSize(getIterator(), true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazilyGeneratedCollectionModelWithUnknownSize.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */