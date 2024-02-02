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
/*    */ public class LongForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   public LongForm(int opcode, String name, int[] rewrite) {
/* 28 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 33 */     return operandManager.nextLongRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 38 */     return 4;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\LongForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */