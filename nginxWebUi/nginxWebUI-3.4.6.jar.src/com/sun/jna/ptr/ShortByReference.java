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
/*    */ public class ShortByReference
/*    */   extends ByReference
/*    */ {
/*    */   public ShortByReference() {
/* 31 */     this((short)0);
/*    */   }
/*    */   
/*    */   public ShortByReference(short value) {
/* 35 */     super(2);
/* 36 */     setValue(value);
/*    */   }
/*    */   
/*    */   public void setValue(short value) {
/* 40 */     getPointer().setShort(0L, value);
/*    */   }
/*    */   
/*    */   public short getValue() {
/* 44 */     return getPointer().getShort(0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return String.format("short@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Short.valueOf(getValue()) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\ShortByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */