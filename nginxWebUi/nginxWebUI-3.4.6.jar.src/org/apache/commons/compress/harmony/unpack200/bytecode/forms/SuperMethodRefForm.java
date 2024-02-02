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
/*    */ 
/*    */ public class SuperMethodRefForm
/*    */   extends ClassSpecificReferenceForm
/*    */ {
/*    */   public SuperMethodRefForm(int opcode, String name, int[] rewrite) {
/* 30 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 35 */     return operandManager.nextSuperMethodRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 40 */     return 11;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String context(OperandManager operandManager) {
/* 45 */     return operandManager.getSuperClass();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\SuperMethodRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */