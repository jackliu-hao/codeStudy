/*    */ package com.sun.jna.ptr;
/*    */ 
/*    */ import com.sun.jna.Pointer;
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
/*    */ public class FloatByReference
/*    */   extends ByReference
/*    */ {
/*    */   public FloatByReference() {
/* 30 */     this(0.0F);
/*    */   }
/*    */   
/*    */   public FloatByReference(float value) {
/* 34 */     super(4);
/* 35 */     setValue(value);
/*    */   }
/*    */   
/*    */   public void setValue(float value) {
/* 39 */     getPointer().setFloat(0L, value);
/*    */   }
/*    */   
/*    */   public float getValue() {
/* 43 */     return getPointer().getFloat(0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return String.format("float@0x%x=%s", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Float.valueOf(getValue()) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\FloatByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */