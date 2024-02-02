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
/*    */ public class DoubleByReference
/*    */   extends ByReference
/*    */ {
/*    */   public DoubleByReference() {
/* 30 */     this(0.0D);
/*    */   }
/*    */   
/*    */   public DoubleByReference(double value) {
/* 34 */     super(8);
/* 35 */     setValue(value);
/*    */   }
/*    */   
/*    */   public void setValue(double value) {
/* 39 */     getPointer().setDouble(0L, value);
/*    */   }
/*    */   
/*    */   public double getValue() {
/* 43 */     return getPointer().getDouble(0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return String.format("double@0x%x=%s", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Double.valueOf(getValue()) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\DoubleByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */