/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
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
/*    */ public class IMethodRefForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   public IMethodRefForm(int opcode, String name, int[] rewrite) {
/* 31 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 36 */     return operandManager.nextIMethodRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 41 */     return 12;
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
/*    */   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
/* 56 */     super.setByteCodeOperands(byteCode, operandManager, codeLength);
/* 57 */     int count = ((CPInterfaceMethodRef)byteCode.getNestedClassFileEntries()[0]).invokeInterfaceCount();
/* 58 */     byteCode.getRewrite()[3] = count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\IMethodRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */