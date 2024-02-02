/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Function;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.PointerType;
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
/*    */ public abstract class COMInvoker
/*    */   extends PointerType
/*    */ {
/*    */   protected int _invokeNativeInt(int vtableId, Object[] args) {
/* 34 */     Pointer vptr = getPointer().getPointer(0L);
/*    */ 
/*    */     
/* 37 */     Function func = Function.getFunction(vptr.getPointer((vtableId * Native.POINTER_SIZE)));
/*    */     
/* 39 */     return func.invokeInt(args);
/*    */   }
/*    */   
/*    */   protected Object _invokeNativeObject(int vtableId, Object[] args, Class<?> returnType) {
/* 43 */     Pointer vptr = getPointer().getPointer(0L);
/*    */ 
/*    */     
/* 46 */     Function func = Function.getFunction(vptr.getPointer((vtableId * Native.POINTER_SIZE)));
/*    */     
/* 48 */     return func.invoke(returnType, args);
/*    */   }
/*    */   
/*    */   protected void _invokeNativeVoid(int vtableId, Object[] args) {
/* 52 */     Pointer vptr = getPointer().getPointer(0L);
/*    */ 
/*    */     
/* 55 */     Function func = Function.getFunction(vptr.getPointer((vtableId * Native.POINTER_SIZE)));
/*    */     
/* 57 */     func.invokeVoid(args);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMInvoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */