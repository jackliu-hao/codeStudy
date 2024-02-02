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
/*    */ public class NarrowClassRefForm
/*    */   extends ClassRefForm
/*    */ {
/*    */   public NarrowClassRefForm(int opcode, String name, int[] rewrite) {
/* 30 */     super(opcode, name, rewrite);
/*    */   }
/*    */   
/*    */   public NarrowClassRefForm(int opcode, String name, int[] rewrite, boolean widened) {
/* 34 */     super(opcode, name, rewrite, widened);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 40 */     super.setNestedEntries(byteCode, operandManager, offset);
/* 41 */     if (!this.widened) {
/* 42 */       byteCode.setNestedPositions(new int[][] { { 0, 1 } });
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean nestedMustStartClassPool() {
/* 48 */     return !this.widened;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\NarrowClassRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */