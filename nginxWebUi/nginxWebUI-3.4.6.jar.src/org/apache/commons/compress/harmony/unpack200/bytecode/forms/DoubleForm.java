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
/*    */ 
/*    */ public class DoubleForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   public DoubleForm(int opcode, String name, int[] rewrite) {
/* 29 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 34 */     return operandManager.nextDoubleRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 39 */     return 5;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\DoubleForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */