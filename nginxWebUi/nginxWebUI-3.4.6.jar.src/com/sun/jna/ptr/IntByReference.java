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
/*    */ 
/*    */ public class IntByReference
/*    */   extends ByReference
/*    */ {
/*    */   public IntByReference() {
/* 31 */     this(0);
/*    */   }
/*    */   
/*    */   public IntByReference(int value) {
/* 35 */     super(4);
/* 36 */     setValue(value);
/*    */   }
/*    */   
/*    */   public void setValue(int value) {
/* 40 */     getPointer().setInt(0L, value);
/*    */   }
/*    */   
/*    */   public int getValue() {
/* 44 */     return getPointer().getInt(0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return String.format("int@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Integer.valueOf(getValue()) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\IntByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */