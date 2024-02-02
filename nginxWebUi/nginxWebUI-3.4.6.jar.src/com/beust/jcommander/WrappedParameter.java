/*     */ package com.beust.jcommander;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WrappedParameter
/*     */ {
/*     */   private Parameter m_parameter;
/*     */   private DynamicParameter m_dynamicParameter;
/*     */   
/*     */   public WrappedParameter(Parameter p) {
/*  14 */     this.m_parameter = p;
/*     */   }
/*     */   
/*     */   public WrappedParameter(DynamicParameter p) {
/*  18 */     this.m_dynamicParameter = p;
/*     */   }
/*     */   
/*     */   public Parameter getParameter() {
/*  22 */     return this.m_parameter;
/*     */   }
/*     */   
/*     */   public DynamicParameter getDynamicParameter() {
/*  26 */     return this.m_dynamicParameter;
/*     */   }
/*     */   
/*     */   public int arity() {
/*  30 */     return (this.m_parameter != null) ? this.m_parameter.arity() : 1;
/*     */   }
/*     */   
/*     */   public boolean hidden() {
/*  34 */     return (this.m_parameter != null) ? this.m_parameter.hidden() : this.m_dynamicParameter.hidden();
/*     */   }
/*     */   
/*     */   public boolean required() {
/*  38 */     return (this.m_parameter != null) ? this.m_parameter.required() : this.m_dynamicParameter.required();
/*     */   }
/*     */   
/*     */   public boolean password() {
/*  42 */     return (this.m_parameter != null) ? this.m_parameter.password() : false;
/*     */   }
/*     */   
/*     */   public String[] names() {
/*  46 */     return (this.m_parameter != null) ? this.m_parameter.names() : this.m_dynamicParameter.names();
/*     */   }
/*     */   
/*     */   public boolean variableArity() {
/*  50 */     return (this.m_parameter != null) ? this.m_parameter.variableArity() : false;
/*     */   }
/*     */   
/*     */   public Class<? extends IParameterValidator> validateWith() {
/*  54 */     return (this.m_parameter != null) ? this.m_parameter.validateWith() : this.m_dynamicParameter.validateWith();
/*     */   }
/*     */   
/*     */   public Class<? extends IValueValidator> validateValueWith() {
/*  58 */     return (this.m_parameter != null) ? this.m_parameter.validateValueWith() : this.m_dynamicParameter.validateValueWith();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean echoInput() {
/*  64 */     return (this.m_parameter != null) ? this.m_parameter.echoInput() : false;
/*     */   }
/*     */   
/*     */   public void addValue(Parameterized parameterized, Object object, Object value) {
/*  68 */     if (this.m_parameter != null) {
/*  69 */       parameterized.set(object, value);
/*     */     } else {
/*  71 */       String a = this.m_dynamicParameter.assignment();
/*  72 */       String sv = value.toString();
/*     */       
/*  74 */       int aInd = sv.indexOf(a);
/*  75 */       if (aInd == -1) {
/*  76 */         throw new ParameterException("Dynamic parameter expected a value of the form a" + a + "b" + " but got:" + sv);
/*     */       }
/*     */ 
/*     */       
/*  80 */       callPut(object, parameterized, sv.substring(0, aInd), sv.substring(aInd + 1));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void callPut(Object object, Parameterized parameterized, String key, String value) {
/*     */     try {
/*  87 */       Method m = findPut(parameterized.getType());
/*  88 */       m.invoke(parameterized.get(object), new Object[] { key, value });
/*  89 */     } catch (SecurityException e) {
/*  90 */       e.printStackTrace();
/*  91 */     } catch (IllegalAccessException e) {
/*  92 */       e.printStackTrace();
/*  93 */     } catch (InvocationTargetException e) {
/*  94 */       e.printStackTrace();
/*  95 */     } catch (NoSuchMethodException e) {
/*  96 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private Method findPut(Class<?> cls) throws SecurityException, NoSuchMethodException {
/* 101 */     return cls.getMethod("put", new Class[] { Object.class, Object.class });
/*     */   }
/*     */   
/*     */   public String getAssignment() {
/* 105 */     return (this.m_dynamicParameter != null) ? this.m_dynamicParameter.assignment() : "";
/*     */   }
/*     */   
/*     */   public boolean isHelp() {
/* 109 */     return (this.m_parameter != null && this.m_parameter.help());
/*     */   }
/*     */   
/*     */   public boolean isNonOverwritableForced() {
/* 113 */     return (this.m_parameter != null && this.m_parameter.forceNonOverwritable());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\WrappedParameter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */