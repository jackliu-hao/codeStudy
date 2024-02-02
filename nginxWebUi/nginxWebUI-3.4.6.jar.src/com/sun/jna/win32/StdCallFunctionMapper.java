/*    */ package com.sun.jna.win32;
/*    */ 
/*    */ import com.sun.jna.Function;
/*    */ import com.sun.jna.FunctionMapper;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.NativeLibrary;
/*    */ import com.sun.jna.NativeMapped;
/*    */ import com.sun.jna.NativeMappedConverter;
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
/*    */ 
/*    */ public class StdCallFunctionMapper
/*    */   implements FunctionMapper
/*    */ {
/*    */   protected int getArgumentNativeStackSize(Class<?> cls) {
/* 48 */     if (NativeMapped.class.isAssignableFrom(cls)) {
/* 49 */       cls = NativeMappedConverter.getInstance(cls).nativeType();
/*    */     }
/* 51 */     if (cls.isArray()) {
/* 52 */       return Native.POINTER_SIZE;
/*    */     }
/*    */     try {
/* 55 */       return Native.getNativeSize(cls);
/* 56 */     } catch (IllegalArgumentException e) {
/* 57 */       throw new IllegalArgumentException("Unknown native stack allocation size for " + cls);
/*    */     } 
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
/*    */   public String getFunctionName(NativeLibrary library, Method method) {
/* 70 */     String name = method.getName();
/* 71 */     int pop = 0;
/* 72 */     Class<?>[] argTypes = method.getParameterTypes();
/* 73 */     for (Class<?> cls : argTypes) {
/* 74 */       pop += getArgumentNativeStackSize(cls);
/*    */     }
/*    */     
/* 77 */     String decorated = name + "@" + pop;
/* 78 */     int conv = 63;
/*    */     try {
/* 80 */       Function func = library.getFunction(decorated, conv);
/* 81 */       name = func.getName();
/* 82 */     } catch (UnsatisfiedLinkError e) {
/*    */       
/*    */       try {
/* 85 */         Function func = library.getFunction("_" + decorated, conv);
/* 86 */         name = func.getName();
/* 87 */       } catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 92 */     return name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\win32\StdCallFunctionMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */