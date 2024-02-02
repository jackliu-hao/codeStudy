/*    */ package com.sun.jna.ptr;
/*    */ 
/*    */ import com.sun.jna.Memory;
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.PointerType;
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
/*    */ public abstract class ByReference
/*    */   extends PointerType
/*    */ {
/*    */   protected ByReference(int dataSize) {
/* 57 */     setPointer((Pointer)new Memory(dataSize));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/*    */     try {
/* 63 */       Method getValue = getClass().getMethod("getValue", new Class[0]);
/* 64 */       Object value = getValue.invoke(this, new Object[0]);
/* 65 */       if (value == null) {
/* 66 */         return String.format("null@0x%x", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())) });
/*    */       }
/* 68 */       return String.format("%s@0x%x=%s", new Object[] { value.getClass().getSimpleName(), Long.valueOf(Pointer.nativeValue(getPointer())), value });
/*    */     }
/* 70 */     catch (Exception ex) {
/* 71 */       return String.format("ByReference Contract violated - %s#getValue raised exception: %s", new Object[] {
/* 72 */             getClass().getName(), ex.getMessage()
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\ByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */