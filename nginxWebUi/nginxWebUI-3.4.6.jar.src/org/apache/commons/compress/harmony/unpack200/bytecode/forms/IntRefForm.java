/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
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
/*    */ public class IntRefForm
/*    */   extends SingleByteReferenceForm
/*    */ {
/*    */   public IntRefForm(int opcode, String name, int[] rewrite) {
/* 28 */     super(opcode, name, rewrite);
/*    */   }
/*    */   
/*    */   public IntRefForm(int opcode, String name, int[] rewrite, boolean widened) {
/* 32 */     this(opcode, name, rewrite);
/* 33 */     this.widened = widened;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 38 */     return operandManager.nextIntRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 43 */     return 2;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\IntRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */