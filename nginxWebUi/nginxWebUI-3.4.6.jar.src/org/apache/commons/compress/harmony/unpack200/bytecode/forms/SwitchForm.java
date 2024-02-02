/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
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
/*    */ public abstract class SwitchForm
/*    */   extends VariableInstructionForm
/*    */ {
/*    */   public SwitchForm(int opcode, String name) {
/* 25 */     super(opcode, name);
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
/* 39 */     int[] originalTargets = byteCode.getByteCodeTargets();
/* 40 */     int numberOfLabels = originalTargets.length;
/* 41 */     int[] replacementTargets = new int[numberOfLabels];
/*    */     
/* 43 */     int sourceIndex = byteCode.getByteCodeIndex();
/* 44 */     int sourceValue = ((Integer)codeAttribute.byteCodeOffsets.get(sourceIndex)).intValue();
/* 45 */     for (int index = 0; index < numberOfLabels; index++) {
/* 46 */       int absoluteInstructionTargetIndex = sourceIndex + originalTargets[index];
/*    */       
/* 48 */       int targetValue = ((Integer)codeAttribute.byteCodeOffsets.get(absoluteInstructionTargetIndex)).intValue();
/* 49 */       replacementTargets[index] = targetValue - sourceValue;
/*    */     } 
/* 51 */     int[] rewriteArray = byteCode.getRewrite();
/* 52 */     for (int i = 0; i < numberOfLabels; i++)
/* 53 */       setRewrite4Bytes(replacementTargets[i], rewriteArray); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\SwitchForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */