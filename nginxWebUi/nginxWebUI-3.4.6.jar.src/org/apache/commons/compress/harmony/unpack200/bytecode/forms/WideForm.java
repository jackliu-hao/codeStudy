/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*     */ 
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WideForm
/*     */   extends VariableInstructionForm
/*     */ {
/*     */   public WideForm(int opcode, String name) {
/*  29 */     super(opcode, name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
/*  44 */     int instruction = operandManager.nextWideByteCode();
/*  45 */     if (instruction == 132) {
/*  46 */       setByteCodeOperandsFormat2(instruction, byteCode, operandManager, codeLength);
/*     */     } else {
/*  48 */       setByteCodeOperandsFormat1(instruction, byteCode, operandManager, codeLength);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setByteCodeOperandsFormat1(int instruction, ByteCode byteCode, OperandManager operandManager, int codeLength) {
/*  71 */     int local = operandManager.nextLocal();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     int[] newRewrite = new int[4];
/*  80 */     int rewriteIndex = 0;
/*     */ 
/*     */ 
/*     */     
/*  84 */     newRewrite[rewriteIndex++] = byteCode.getOpcode();
/*     */ 
/*     */     
/*  87 */     newRewrite[rewriteIndex++] = instruction;
/*     */ 
/*     */     
/*  90 */     setRewrite2Bytes(local, rewriteIndex, newRewrite);
/*  91 */     rewriteIndex += 2;
/*     */     
/*  93 */     byteCode.setRewrite(newRewrite);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setByteCodeOperandsFormat2(int instruction, ByteCode byteCode, OperandManager operandManager, int codeLength) {
/* 108 */     int local = operandManager.nextLocal();
/* 109 */     int constWord = operandManager.nextShort();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     int[] newRewrite = new int[6];
/* 118 */     int rewriteIndex = 0;
/*     */ 
/*     */ 
/*     */     
/* 122 */     newRewrite[rewriteIndex++] = byteCode.getOpcode();
/*     */ 
/*     */     
/* 125 */     newRewrite[rewriteIndex++] = instruction;
/*     */ 
/*     */     
/* 128 */     setRewrite2Bytes(local, rewriteIndex, newRewrite);
/* 129 */     rewriteIndex += 2;
/*     */ 
/*     */     
/* 132 */     setRewrite2Bytes(constWord, rewriteIndex, newRewrite);
/* 133 */     rewriteIndex += 2;
/*     */ 
/*     */     
/* 136 */     byteCode.setRewrite(newRewrite);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\WideForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */