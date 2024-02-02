/*    */ package com.sun.jna;
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
/*    */ public class FunctionParameterContext
/*    */   extends ToNativeContext
/*    */ {
/*    */   private Function function;
/*    */   private Object[] args;
/*    */   private int index;
/*    */   
/*    */   FunctionParameterContext(Function f, Object[] args, int index) {
/* 34 */     this.function = f;
/* 35 */     this.args = args;
/* 36 */     this.index = index;
/*    */   }
/*    */   public Function getFunction() {
/* 39 */     return this.function;
/*    */   }
/* 41 */   public Object[] getParameters() { return this.args; } public int getParameterIndex() {
/* 42 */     return this.index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\FunctionParameterContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */