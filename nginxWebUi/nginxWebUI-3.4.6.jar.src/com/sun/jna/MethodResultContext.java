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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MethodResultContext
/*    */   extends FunctionResultContext
/*    */ {
/*    */   private final Method method;
/*    */   
/*    */   MethodResultContext(Class<?> resultClass, Function function, Object[] args, Method method) {
/* 35 */     super(resultClass, function, args);
/* 36 */     this.method = method;
/*    */   }
/*    */ 
/*    */   
/*    */   public Method getMethod() {
/* 41 */     return this.method;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\MethodResultContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */