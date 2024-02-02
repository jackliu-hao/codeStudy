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
/*    */ class SequenceIterator
/*    */   implements TemplateModelIterator
/*    */ {
/*    */   private final TemplateSequenceModel sequence;
/*    */   private final int size;
/* 30 */   private int index = 0;
/*    */   
/*    */   SequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
/* 33 */     this.sequence = sequence;
/* 34 */     this.size = sequence.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel next() throws TemplateModelException {
/* 39 */     return this.sequence.get(this.index++);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 44 */     return (this.index < this.size);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\SequenceIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */