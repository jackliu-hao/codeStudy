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
/*     */ public class TableSwitchForm
/*     */   extends SwitchForm
/*     */ {
/*     */   public TableSwitchForm(int opcode, String name) {
/*  25 */     super(opcode, name);
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
/*     */   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
/*  39 */     int case_count = operandManager.nextCaseCount();
/*  40 */     int default_pc = operandManager.nextLabel();
/*  41 */     int case_value = -1;
/*  42 */     case_value = operandManager.nextCaseValues();
/*     */     
/*  44 */     int[] case_pcs = new int[case_count];
/*  45 */     for (int index = 0; index < case_count; index++) {
/*  46 */       case_pcs[index] = operandManager.nextLabel();
/*     */     }
/*     */     
/*  49 */     int[] labelsArray = new int[case_count + 1];
/*  50 */     labelsArray[0] = default_pc;
/*  51 */     for (int i = 1; i < case_count + 1; i++) {
/*  52 */       labelsArray[i] = case_pcs[i - 1];
/*     */     }
/*  54 */     byteCode.setByteCodeTargets(labelsArray);
/*     */     
/*  56 */     int lowValue = case_value;
/*  57 */     int highValue = lowValue + case_count - 1;
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
/*  70 */     int padLength = 3 - codeLength % 4;
/*  71 */     int rewriteSize = 1 + padLength + 4 + 4 + 4 + 4 * case_pcs.length;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     int[] newRewrite = new int[rewriteSize];
/*  77 */     int rewriteIndex = 0;
/*     */ 
/*     */ 
/*     */     
/*  81 */     newRewrite[rewriteIndex++] = byteCode.getOpcode();
/*     */ 
/*     */     
/*  84 */     for (int j = 0; j < padLength; j++) {
/*  85 */       newRewrite[rewriteIndex++] = 0;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  90 */     newRewrite[rewriteIndex++] = -1;
/*  91 */     newRewrite[rewriteIndex++] = -1;
/*  92 */     newRewrite[rewriteIndex++] = -1;
/*  93 */     newRewrite[rewriteIndex++] = -1;
/*     */ 
/*     */     
/*  96 */     int lowbyteIndex = rewriteIndex;
/*  97 */     setRewrite4Bytes(lowValue, lowbyteIndex, newRewrite);
/*  98 */     rewriteIndex += 4;
/*     */ 
/*     */     
/* 101 */     int highbyteIndex = rewriteIndex;
/* 102 */     setRewrite4Bytes(highValue, highbyteIndex, newRewrite);
/* 103 */     rewriteIndex += 4;
/*     */ 
/*     */ 
/*     */     
/* 107 */     for (int k = 0; k < case_count; k++) {
/*     */       
/* 109 */       newRewrite[rewriteIndex++] = -1;
/* 110 */       newRewrite[rewriteIndex++] = -1;
/* 111 */       newRewrite[rewriteIndex++] = -1;
/* 112 */       newRewrite[rewriteIndex++] = -1;
/*     */     } 
/* 114 */     byteCode.setRewrite(newRewrite);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\TableSwitchForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */