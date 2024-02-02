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
/*    */ public class ThisInitMethodRefForm
/*    */   extends InitMethodReferenceForm
/*    */ {
/*    */   public ThisInitMethodRefForm(int opcode, String name, int[] rewrite) {
/* 27 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String context(OperandManager operandManager) {
/* 32 */     return operandManager.getCurrentClass();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\ThisInitMethodRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */