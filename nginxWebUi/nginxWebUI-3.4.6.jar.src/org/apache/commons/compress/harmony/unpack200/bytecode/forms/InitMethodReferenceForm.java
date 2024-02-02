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
/*    */ 
/*    */ public abstract class InitMethodReferenceForm
/*    */   extends ClassSpecificReferenceForm
/*    */ {
/*    */   public InitMethodReferenceForm(int opcode, String name, int[] rewrite) {
/* 32 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected abstract String context(OperandManager paramOperandManager);
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 40 */     return 11;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 45 */     return operandManager.nextInitRef();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 51 */     SegmentConstantPool globalPool = operandManager.globalConstantPool();
/* 52 */     ClassFileEntry[] nested = null;
/*    */     
/* 54 */     nested = new ClassFileEntry[] { (ClassFileEntry)globalPool.getInitMethodPoolEntry(11, offset, context(operandManager)) };
/* 55 */     byteCode.setNested(nested);
/* 56 */     byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\InitMethodReferenceForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */