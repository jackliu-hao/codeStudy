/*    */ package com.sun.jna;
/*    */ 
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
/*    */ public class CallbackParameterContext
/*    */   extends FromNativeContext
/*    */ {
/*    */   private Method method;
/*    */   private Object[] args;
/*    */   private int index;
/*    */   
/*    */   CallbackParameterContext(Class<?> javaType, Method m, Object[] args, int index) {
/* 34 */     super(javaType);
/* 35 */     this.method = m;
/* 36 */     this.args = args;
/* 37 */     this.index = index;
/*    */   }
/* 39 */   public Method getMethod() { return this.method; }
/* 40 */   public Object[] getArguments() { return this.args; } public int getIndex() {
/* 41 */     return this.index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\CallbackParameterContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */