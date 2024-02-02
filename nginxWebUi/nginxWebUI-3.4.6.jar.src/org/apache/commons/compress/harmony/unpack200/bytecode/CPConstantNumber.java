/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
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
/*    */ public abstract class CPConstantNumber
/*    */   extends CPConstant
/*    */ {
/*    */   public CPConstantNumber(byte tag, Object value, int globalIndex) {
/* 25 */     super(tag, value, globalIndex);
/*    */   }
/*    */   
/*    */   protected Number getNumber() {
/* 29 */     return (Number)getValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPConstantNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */