/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.SimpleNumber;
/*    */ import freemarker.template.TemplateMethodModelEx;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class OverloadedMethodsModel
/*    */   implements TemplateMethodModelEx, TemplateSequenceModel
/*    */ {
/*    */   private final Object object;
/*    */   private final OverloadedMethods overloadedMethods;
/*    */   private final BeansWrapper wrapper;
/*    */   
/*    */   OverloadedMethodsModel(Object object, OverloadedMethods overloadedMethods, BeansWrapper wrapper) {
/* 45 */     this.object = object;
/* 46 */     this.overloadedMethods = overloadedMethods;
/* 47 */     this.wrapper = wrapper;
/*    */   }
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
/*    */   public Object exec(List arguments) throws TemplateModelException {
/* 60 */     MemberAndArguments maa = this.overloadedMethods.getMemberAndArguments(arguments, this.wrapper);
/*    */     try {
/* 62 */       return maa.invokeMethod(this.wrapper, this.object);
/* 63 */     } catch (Exception e) {
/* 64 */       if (e instanceof TemplateModelException) throw (TemplateModelException)e;
/*    */       
/* 66 */       throw _MethodUtil.newInvocationTemplateModelException(this.object, maa
/*    */           
/* 68 */           .getCallableMemberDescriptor(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateModel get(int index) throws TemplateModelException {
/* 75 */     return (TemplateModel)exec(Collections.singletonList(new SimpleNumber(
/* 76 */             Integer.valueOf(index))));
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 81 */     throw new TemplateModelException("?size is unsupported for " + getClass().getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedMethodsModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */