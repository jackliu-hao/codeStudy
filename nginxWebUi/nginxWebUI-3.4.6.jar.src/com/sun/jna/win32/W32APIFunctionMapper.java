/*    */ package com.sun.jna.win32;
/*    */ 
/*    */ import com.sun.jna.FunctionMapper;
/*    */ import com.sun.jna.NativeLibrary;
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
/*    */ public class W32APIFunctionMapper
/*    */   implements FunctionMapper
/*    */ {
/* 35 */   public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
/* 36 */   public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);
/*    */   
/*    */   protected W32APIFunctionMapper(boolean unicode) {
/* 39 */     this.suffix = unicode ? "W" : "A";
/*    */   }
/*    */   
/*    */   private final String suffix;
/*    */   
/*    */   public String getFunctionName(NativeLibrary library, Method method) {
/* 45 */     String name = method.getName();
/* 46 */     if (!name.endsWith("W") && !name.endsWith("A")) {
/*    */       try {
/* 48 */         name = library.getFunction(name + this.suffix, 63).getName();
/*    */       }
/* 50 */       catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
/*    */     }
/*    */ 
/*    */     
/* 54 */     return name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\win32\W32APIFunctionMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */