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
/*    */ public class LongByReference
/*    */   extends ByReference
/*    */ {
/*    */   public LongByReference() {
/* 30 */     this(0L);
/*    */   }
/*    */   
/*    */   public LongByReference(long value) {
/* 34 */     super(8);
/* 35 */     setValue(value);
/*    */   }
/*    */   
/*    */   public void setValue(long value) {
/* 39 */     getPointer().setLong(0L, value);
/*    */   }
/*    */   
/*    */   public long getValue() {
/* 43 */     return getPointer().getLong(0L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return String.format("long@0x%1$x=0x%2$x (%2$d)", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())), Long.valueOf(getValue()) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\ptr\LongByReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */