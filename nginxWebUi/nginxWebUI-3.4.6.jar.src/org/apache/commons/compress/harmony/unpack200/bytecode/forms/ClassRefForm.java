/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*    */ import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
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
/*    */ public class ClassRefForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   protected boolean widened;
/*    */   
/*    */   public ClassRefForm(int opcode, String name, int[] rewrite) {
/* 33 */     super(opcode, name, rewrite);
/*    */   }
/*    */   
/*    */   public ClassRefForm(int opcode, String name, int[] rewrite, boolean widened) {
/* 37 */     this(opcode, name, rewrite);
/* 38 */     this.widened = widened;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 45 */     if (offset != 0) {
/* 46 */       super.setNestedEntries(byteCode, operandManager, offset - 1);
/*    */ 
/*    */ 
/*    */       
/*    */       return;
/*    */     } 
/*    */ 
/*    */     
/* 54 */     SegmentConstantPool globalPool = operandManager.globalConstantPool();
/* 55 */     ClassFileEntry[] nested = null;
/*    */     
/* 57 */     nested = new ClassFileEntry[] { (ClassFileEntry)globalPool.getClassPoolEntry(operandManager.getCurrentClass()) };
/* 58 */     byteCode.setNested(nested);
/* 59 */     byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 64 */     return operandManager.nextClassRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 69 */     return 7;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\ClassRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */