/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Member;
/*    */ import java.lang.reflect.Method;
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
/*    */ 
/*    */ final class ReflectionCallableMemberDescriptor
/*    */   extends CallableMemberDescriptor
/*    */ {
/*    */   private final Member member;
/*    */   final Class[] paramTypes;
/*    */   
/*    */   ReflectionCallableMemberDescriptor(Method member, Class[] paramTypes) {
/* 44 */     this.member = member;
/* 45 */     this.paramTypes = paramTypes;
/*    */   }
/*    */   
/*    */   ReflectionCallableMemberDescriptor(Constructor member, Class[] paramTypes) {
/* 49 */     this.member = member;
/* 50 */     this.paramTypes = paramTypes;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
/* 56 */     return bw.invokeMethod(obj, (Method)this.member, args);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
/* 62 */     return ((Constructor)this.member).newInstance(args);
/*    */   }
/*    */ 
/*    */   
/*    */   String getDeclaration() {
/* 67 */     return _MethodUtil.toString(this.member);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isConstructor() {
/* 72 */     return this.member instanceof Constructor;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isStatic() {
/* 77 */     return ((this.member.getModifiers() & 0x8) != 0);
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isVarargs() {
/* 82 */     return _MethodUtil.isVarargs(this.member);
/*    */   }
/*    */ 
/*    */   
/*    */   Class[] getParamTypes() {
/* 87 */     return this.paramTypes;
/*    */   }
/*    */ 
/*    */   
/*    */   String getName() {
/* 92 */     return this.member.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ReflectionCallableMemberDescriptor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */