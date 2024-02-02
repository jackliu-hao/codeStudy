/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
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
/*    */ public class LabelForm
/*    */   extends ByteCodeForm
/*    */ {
/*    */   protected boolean widened;
/*    */   
/*    */   public LabelForm(int opcode, String name, int[] rewrite) {
/* 31 */     super(opcode, name, rewrite);
/*    */   }
/*    */   
/*    */   public LabelForm(int opcode, String name, int[] rewrite, boolean widened) {
/* 35 */     this(opcode, name, rewrite);
/* 36 */     this.widened = widened;
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
/*    */   public void fixUpByteCodeTargets(ByteCode byteCode, CodeAttribute codeAttribute) {
/* 50 */     int originalTarget = byteCode.getByteCodeTargets()[0];
/* 51 */     int sourceIndex = byteCode.getByteCodeIndex();
/* 52 */     int absoluteInstructionTargetIndex = sourceIndex + originalTarget;
/*    */     
/* 54 */     int targetValue = ((Integer)codeAttribute.byteCodeOffsets.get(absoluteInstructionTargetIndex)).intValue();
/* 55 */     int sourceValue = ((Integer)codeAttribute.byteCodeOffsets.get(sourceIndex)).intValue();
/*    */ 
/*    */     
/* 58 */     byteCode.setOperandSigned2Bytes(targetValue - sourceValue, 0);
/* 59 */     if (this.widened) {
/* 60 */       byteCode.setNestedPositions(new int[][] { { 0, 4 } });
/*    */     } else {
/* 62 */       byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */     } 
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
/* 78 */     byteCode.setByteCodeTargets(new int[] { operandManager.nextLabel() });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\LabelForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */