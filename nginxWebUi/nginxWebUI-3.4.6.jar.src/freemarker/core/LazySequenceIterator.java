/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.TemplateSequenceModel;
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
/*    */ class LazySequenceIterator
/*    */   implements TemplateModelIterator
/*    */ {
/*    */   private final TemplateSequenceModel sequence;
/*    */   private Integer size;
/* 35 */   private int index = 0;
/*    */   
/*    */   LazySequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
/* 38 */     this.sequence = sequence;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel next() throws TemplateModelException {
/* 43 */     return this.sequence.get(this.index++);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 48 */     if (this.size == null) {
/*    */       try {
/* 50 */         this.size = Integer.valueOf(this.sequence.size());
/* 51 */       } catch (TemplateModelException e) {
/* 52 */         throw new RuntimeException("Error when getting sequence size", e);
/*    */       } 
/*    */     }
/* 55 */     return (this.index < this.size.intValue());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazySequenceIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */