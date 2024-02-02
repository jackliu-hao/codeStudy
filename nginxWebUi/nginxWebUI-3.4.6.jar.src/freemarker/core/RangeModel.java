/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.SimpleNumber;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import java.io.Serializable;
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
/*    */ abstract class RangeModel
/*    */   implements TemplateSequenceModel, Serializable
/*    */ {
/*    */   private final int begin;
/*    */   
/*    */   public RangeModel(int begin) {
/* 32 */     this.begin = begin;
/*    */   }
/*    */   
/*    */   final int getBegining() {
/* 36 */     return this.begin;
/*    */   }
/*    */ 
/*    */   
/*    */   public final TemplateModel get(int index) throws TemplateModelException {
/* 41 */     if (index < 0 || index >= size()) {
/* 42 */       throw new _TemplateModelException(new Object[] { "Range item index ", Integer.valueOf(index), " is out of bounds." });
/*    */     }
/* 44 */     long value = this.begin + getStep() * index;
/* 45 */     return (value <= 2147483647L) ? (TemplateModel)new SimpleNumber((int)value) : (TemplateModel)new SimpleNumber(value);
/*    */   }
/*    */   
/*    */   abstract int getStep();
/*    */   
/*    */   abstract boolean isRightUnbounded();
/*    */   
/*    */   abstract boolean isRightAdaptive();
/*    */   
/*    */   abstract boolean isAffectedByStringSlicingBug();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RangeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */