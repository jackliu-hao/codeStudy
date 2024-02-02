/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
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
/*     */ public abstract class VariableInstructionForm
/*     */   extends ByteCodeForm
/*     */ {
/*     */   public VariableInstructionForm(int opcode, String name) {
/*  26 */     super(opcode, name);
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
/*     */   public void setRewrite4Bytes(int operand, int[] rewrite) {
/*  43 */     int firstOperandPosition = -1;
/*     */ 
/*     */     
/*  46 */     for (int index = 0; index < rewrite.length - 3; index++) {
/*  47 */       if (rewrite[index] == -1 && rewrite[index + 1] == -1 && rewrite[index + 2] == -1 && rewrite[index + 3] == -1) {
/*     */         
/*  49 */         firstOperandPosition = index;
/*     */         break;
/*     */       } 
/*     */     } 
/*  53 */     setRewrite4Bytes(operand, firstOperandPosition, rewrite);
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
/*     */   public void setRewrite4Bytes(int operand, int absPosition, int[] rewrite) {
/*  65 */     if (absPosition < 0) {
/*  66 */       throw new Error("Trying to rewrite " + this + " but there is no room for 4 bytes");
/*     */     }
/*     */     
/*  69 */     int byteCodeRewriteLength = rewrite.length;
/*     */     
/*  71 */     if (absPosition + 3 > byteCodeRewriteLength) {
/*  72 */       throw new Error("Trying to rewrite " + this + " with an int at position " + absPosition + " but this won't fit in the rewrite array");
/*     */     }
/*     */ 
/*     */     
/*  76 */     rewrite[absPosition] = (0xFF000000 & operand) >> 24;
/*  77 */     rewrite[absPosition + 1] = (0xFF0000 & operand) >> 16;
/*  78 */     rewrite[absPosition + 2] = (0xFF00 & operand) >> 8;
/*  79 */     rewrite[absPosition + 3] = 0xFF & operand;
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
/*     */   public void setRewrite2Bytes(int operand, int absPosition, int[] rewrite) {
/*  91 */     if (absPosition < 0) {
/*  92 */       throw new Error("Trying to rewrite " + this + " but there is no room for 4 bytes");
/*     */     }
/*     */     
/*  95 */     int byteCodeRewriteLength = rewrite.length;
/*     */     
/*  97 */     if (absPosition + 1 > byteCodeRewriteLength) {
/*  98 */       throw new Error("Trying to rewrite " + this + " with an int at position " + absPosition + " but this won't fit in the rewrite array");
/*     */     }
/*     */ 
/*     */     
/* 102 */     rewrite[absPosition] = (0xFF00 & operand) >> 8;
/* 103 */     rewrite[absPosition + 1] = 0xFF & operand;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\VariableInstructionForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */