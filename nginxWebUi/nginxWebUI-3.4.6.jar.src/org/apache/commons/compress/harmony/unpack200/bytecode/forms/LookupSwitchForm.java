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
/*     */ public class LookupSwitchForm
/*     */   extends SwitchForm
/*     */ {
/*     */   public LookupSwitchForm(int opcode, String name) {
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
/*  41 */     int[] case_values = new int[case_count];
/*  42 */     for (int index = 0; index < case_count; index++) {
/*  43 */       case_values[index] = operandManager.nextCaseValues();
/*     */     }
/*  45 */     int[] case_pcs = new int[case_count];
/*  46 */     for (int i = 0; i < case_count; i++) {
/*  47 */       case_pcs[i] = operandManager.nextLabel();
/*     */     }
/*     */     
/*  50 */     int[] labelsArray = new int[case_count + 1];
/*  51 */     labelsArray[0] = default_pc;
/*  52 */     for (int j = 1; j < case_count + 1; j++) {
/*  53 */       labelsArray[j] = case_pcs[j - 1];
/*     */     }
/*  55 */     byteCode.setByteCodeTargets(labelsArray);
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
/*  69 */     int padLength = 3 - codeLength % 4;
/*  70 */     int rewriteSize = 1 + padLength + 4 + 4 + 4 * case_values.length + 4 * case_pcs.length;
/*     */ 
/*     */ 
/*     */     
/*  74 */     int[] newRewrite = new int[rewriteSize];
/*  75 */     int rewriteIndex = 0;
/*     */ 
/*     */ 
/*     */     
/*  79 */     newRewrite[rewriteIndex++] = byteCode.getOpcode();
/*     */ 
/*     */     
/*  82 */     for (int k = 0; k < padLength; k++) {
/*  83 */       newRewrite[rewriteIndex++] = 0;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  88 */     newRewrite[rewriteIndex++] = -1;
/*  89 */     newRewrite[rewriteIndex++] = -1;
/*  90 */     newRewrite[rewriteIndex++] = -1;
/*  91 */     newRewrite[rewriteIndex++] = -1;
/*     */ 
/*     */     
/*  94 */     int npairsIndex = rewriteIndex;
/*  95 */     setRewrite4Bytes(case_values.length, npairsIndex, newRewrite);
/*  96 */     rewriteIndex += 4;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     for (int m = 0; m < case_values.length; m++) {
/*     */       
/* 103 */       setRewrite4Bytes(case_values[m], rewriteIndex, newRewrite);
/* 104 */       rewriteIndex += 4;
/*     */       
/* 106 */       newRewrite[rewriteIndex++] = -1;
/* 107 */       newRewrite[rewriteIndex++] = -1;
/* 108 */       newRewrite[rewriteIndex++] = -1;
/* 109 */       newRewrite[rewriteIndex++] = -1;
/*     */     } 
/* 111 */     byteCode.setRewrite(newRewrite);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\LookupSwitchForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */