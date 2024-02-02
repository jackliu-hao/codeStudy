/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.SimpleScalar;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateScalarModel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringArraySequence
/*    */   implements TemplateSequenceModel
/*    */ {
/*    */   private String[] stringArray;
/*    */   private TemplateScalarModel[] array;
/*    */   
/*    */   public StringArraySequence(String[] stringArray) {
/* 40 */     this.stringArray = stringArray;
/*    */   }
/*    */   
/*    */   public TemplateModel get(int index) {
/*    */     SimpleScalar simpleScalar;
/* 45 */     if (this.array == null) {
/* 46 */       this.array = new TemplateScalarModel[this.stringArray.length];
/*    */     }
/* 48 */     TemplateScalarModel result = this.array[index];
/* 49 */     if (result == null) {
/* 50 */       simpleScalar = new SimpleScalar(this.stringArray[index]);
/* 51 */       this.array[index] = (TemplateScalarModel)simpleScalar;
/*    */     } 
/* 53 */     return (TemplateModel)simpleScalar;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 58 */     return this.stringArray.length;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\StringArraySequence.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */