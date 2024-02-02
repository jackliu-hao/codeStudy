/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core._DelayedFTLTypeDescription;
/*     */ import freemarker.core._DelayedToString;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SimpleMethodModel
/*     */   extends SimpleMethod
/*     */   implements TemplateMethodModelEx, TemplateSequenceModel, _UnexpectedTypeErrorExplainerTemplateModel
/*     */ {
/*     */   private final Object object;
/*     */   private final BeansWrapper wrapper;
/*     */   
/*     */   SimpleMethodModel(Object object, Method method, Class[] argTypes, BeansWrapper wrapper) {
/*  61 */     super(method, argTypes);
/*  62 */     this.object = object;
/*  63 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object exec(List arguments) throws TemplateModelException {
/*     */     try {
/*  73 */       return this.wrapper.invokeMethod(this.object, (Method)getMember(), 
/*  74 */           unwrapArguments(arguments, this.wrapper));
/*  75 */     } catch (TemplateModelException e) {
/*  76 */       throw e;
/*  77 */     } catch (Exception e) {
/*  78 */       throw _MethodUtil.newInvocationTemplateModelException(this.object, getMember(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*  84 */     return (TemplateModel)exec(Collections.singletonList(new SimpleNumber(
/*  85 */             Integer.valueOf(index))));
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() throws TemplateModelException {
/*  90 */     throw new _TemplateModelException((new _ErrorDescriptionBuilder(new Object[] { "Getting the number of items or listing the items is not supported on this ", new _DelayedFTLTypeDescription(this), " value, because this value wraps the following Java method, not a real listable value: ", new _DelayedToString(
/*     */ 
/*     */ 
/*     */               
/*  94 */               getMember())
/*  95 */           })).tips(new Object[] { "Maybe you should to call this method first and then do something with its return value.", "obj.someMethod(i) and obj.someMethod[i] does the same for this method, hence it's a \"+sequence\"." }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return getMember().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] explainTypeError(Class[] expectedClasses) {
/* 111 */     Member member = getMember();
/* 112 */     if (!(member instanceof Method)) {
/* 113 */       return null;
/*     */     }
/* 115 */     Method m = (Method)member;
/*     */     
/* 117 */     Class<?> returnType = m.getReturnType();
/* 118 */     if (returnType == null || returnType == void.class || returnType == Void.class) {
/* 119 */       return null;
/*     */     }
/*     */     
/* 122 */     String mName = m.getName();
/* 123 */     if (mName.startsWith("get") && mName.length() > 3 && Character.isUpperCase(mName.charAt(3)) && (m
/* 124 */       .getParameterTypes()).length == 0) {
/* 125 */       return new Object[] { "Maybe using obj.something instead of obj.getSomething will yield the desired value." };
/*     */     }
/* 127 */     if (mName.startsWith("is") && mName.length() > 2 && Character.isUpperCase(mName.charAt(2)) && (m
/* 128 */       .getParameterTypes()).length == 0) {
/* 129 */       return new Object[] { "Maybe using obj.something instead of obj.isSomething will yield the desired value." };
/*     */     }
/*     */     
/* 132 */     return new Object[] { "Maybe using obj.something(", 
/*     */         
/* 134 */         ((m.getParameterTypes()).length != 0) ? "params" : "", ") instead of obj.something will yield the desired value" };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\SimpleMethodModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */