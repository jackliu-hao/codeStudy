/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNumberModel;
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
/*    */ public class NumberModel
/*    */   extends BeanModel
/*    */   implements TemplateNumberModel
/*    */ {
/* 37 */   static final ModelFactory FACTORY = new ModelFactory()
/*    */     {
/*    */       
/*    */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*    */       {
/* 42 */         return (TemplateModel)new NumberModel((Number)object, (BeansWrapper)wrapper);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public NumberModel(Number number, BeansWrapper wrapper) {
/* 54 */     super(number, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 59 */     return (Number)this.object;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\NumberModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */