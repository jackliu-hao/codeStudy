/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
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
/*    */ public abstract class SingleByteReferenceForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   protected boolean widened;
/*    */   
/*    */   public SingleByteReferenceForm(int opcode, String name, int[] rewrite) {
/* 32 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract int getOffset(OperandManager paramOperandManager);
/*    */ 
/*    */   
/*    */   protected abstract int getPoolID();
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 44 */     super.setNestedEntries(byteCode, operandManager, offset);
/* 45 */     if (this.widened) {
/* 46 */       byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */     } else {
/* 48 */       byteCode.setNestedPositions(new int[][] { { 0, 1 } });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean nestedMustStartClassPool() {
/* 54 */     return !this.widened;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\SingleByteReferenceForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */