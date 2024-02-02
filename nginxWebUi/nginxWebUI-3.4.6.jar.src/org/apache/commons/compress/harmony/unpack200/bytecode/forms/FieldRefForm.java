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
/*    */ public class FieldRefForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   public FieldRefForm(int opcode, String name, int[] rewrite) {
/* 28 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 33 */     return operandManager.nextFieldRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 38 */     return 10;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\FieldRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */