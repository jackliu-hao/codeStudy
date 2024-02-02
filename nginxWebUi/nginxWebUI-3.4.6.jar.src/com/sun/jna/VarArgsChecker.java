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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class VarArgsChecker
/*    */ {
/*    */   private VarArgsChecker() {}
/*    */   
/*    */   private static final class RealVarArgsChecker
/*    */     extends VarArgsChecker
/*    */   {
/*    */     private RealVarArgsChecker() {}
/*    */     
/*    */     boolean isVarArgs(Method m) {
/* 47 */       return m.isVarArgs();
/*    */     }
/*    */ 
/*    */     
/*    */     int fixedArgs(Method m) {
/* 52 */       return m.isVarArgs() ? ((m.getParameterTypes()).length - 1) : 0;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   private static final class NoVarArgsChecker
/*    */     extends VarArgsChecker
/*    */   {
/*    */     private NoVarArgsChecker() {}
/*    */     
/*    */     boolean isVarArgs(Method m) {
/* 63 */       return false;
/*    */     }
/*    */     
/*    */     int fixedArgs(Method m) {
/* 67 */       return 0;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static VarArgsChecker create() {
/*    */     try {
/* 79 */       Method isVarArgsMethod = Method.class.getMethod("isVarArgs", new Class[0]);
/* 80 */       if (isVarArgsMethod != null)
/*    */       {
/* 82 */         return new RealVarArgsChecker();
/*    */       }
/* 84 */       return new NoVarArgsChecker();
/*    */     }
/* 86 */     catch (NoSuchMethodException e) {
/* 87 */       return new NoVarArgsChecker();
/* 88 */     } catch (SecurityException e) {
/* 89 */       return new NoVarArgsChecker();
/*    */     } 
/*    */   }
/*    */   
/*    */   abstract boolean isVarArgs(Method paramMethod);
/*    */   
/*    */   abstract int fixedArgs(Method paramMethod);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\VarArgsChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */