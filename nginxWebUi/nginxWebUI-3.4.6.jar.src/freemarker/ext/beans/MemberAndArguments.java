/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.lang.reflect.InvocationTargetException;
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
/*    */ class MemberAndArguments
/*    */   extends MaybeEmptyMemberAndArguments
/*    */ {
/*    */   private final CallableMemberDescriptor callableMemberDesc;
/*    */   private final Object[] args;
/*    */   
/*    */   MemberAndArguments(CallableMemberDescriptor memberDesc, Object[] args) {
/* 38 */     this.callableMemberDesc = memberDesc;
/* 39 */     this.args = args;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Object[] getArgs() {
/* 46 */     return this.args;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel invokeMethod(BeansWrapper bw, Object obj) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
/* 51 */     return this.callableMemberDesc.invokeMethod(bw, obj, this.args);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   Object invokeConstructor(BeansWrapper bw) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
/* 57 */     return this.callableMemberDesc.invokeConstructor(bw, this.args);
/*    */   }
/*    */   
/*    */   CallableMemberDescriptor getCallableMemberDescriptor() {
/* 61 */     return this.callableMemberDesc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MemberAndArguments.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */