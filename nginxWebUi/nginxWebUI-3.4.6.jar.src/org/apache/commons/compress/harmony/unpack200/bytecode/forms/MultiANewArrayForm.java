/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
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
/*    */ public class MultiANewArrayForm
/*    */   extends ClassRefForm
/*    */ {
/*    */   public MultiANewArrayForm(int opcode, String name, int[] rewrite) {
/* 31 */     super(opcode, name, rewrite);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
/* 48 */     super.setByteCodeOperands(byteCode, operandManager, codeLength);
/*    */ 
/*    */     
/* 51 */     int dimension = operandManager.nextByte();
/* 52 */     byteCode.setOperandByte(dimension, 2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\MultiANewArrayForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */