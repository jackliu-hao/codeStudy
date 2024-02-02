/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelAdapter;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import freemarker.template.utility.UndeclaredThrowableException;
/*    */ import java.util.AbstractList;
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
/*    */ class SequenceAdapter
/*    */   extends AbstractList
/*    */   implements TemplateModelAdapter
/*    */ {
/*    */   private final BeansWrapper wrapper;
/*    */   private final TemplateSequenceModel model;
/*    */   
/*    */   SequenceAdapter(TemplateSequenceModel model, BeansWrapper wrapper) {
/* 37 */     this.model = model;
/* 38 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel getTemplateModel() {
/* 43 */     return (TemplateModel)this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/*    */     try {
/* 49 */       return this.model.size();
/* 50 */     } catch (TemplateModelException e) {
/* 51 */       throw new UndeclaredThrowableException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(int index) {
/*    */     try {
/* 58 */       return this.wrapper.unwrap(this.model.get(index));
/* 59 */     } catch (TemplateModelException e) {
/* 60 */       throw new UndeclaredThrowableException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public TemplateSequenceModel getTemplateSequenceModel() {
/* 65 */     return this.model;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\SequenceAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */