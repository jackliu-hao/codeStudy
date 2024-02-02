/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateScalarModel;
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
/*    */ public class StringModel
/*    */   extends BeanModel
/*    */   implements TemplateScalarModel
/*    */ {
/* 34 */   static final ModelFactory FACTORY = new ModelFactory()
/*    */     {
/*    */       
/*    */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*    */       {
/* 39 */         return (TemplateModel)new StringModel(object, (BeansWrapper)wrapper);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static final String TO_STRING_NOT_EXPOSED = "[toString not exposed]";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringModel(Object object, BeansWrapper wrapper) {
/* 56 */     super(object, wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 67 */     boolean exposeToString = (this.wrapper.getMemberAccessPolicy().isToStringAlwaysExposed() || !this.wrapper.getClassIntrospector().get(this.object.getClass()).containsKey(ClassIntrospector.TO_STRING_HIDDEN_FLAG_KEY));
/* 68 */     return exposeToString ? this.object.toString() : "[toString not exposed]";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\StringModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */