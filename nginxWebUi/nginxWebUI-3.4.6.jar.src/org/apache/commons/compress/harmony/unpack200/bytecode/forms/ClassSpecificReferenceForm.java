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
/*    */ 
/*    */ 
/*    */ public abstract class ClassSpecificReferenceForm
/*    */   extends ReferenceForm
/*    */ {
/*    */   public ClassSpecificReferenceForm(int opcode, String name, int[] rewrite) {
/* 34 */     super(opcode, name, rewrite);
/*    */   }
/*    */ 
/*    */   
/*    */   protected abstract int getOffset(OperandManager paramOperandManager);
/*    */ 
/*    */   
/*    */   protected abstract int getPoolID();
/*    */ 
/*    */   
/*    */   protected abstract String context(OperandManager paramOperandManager);
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 48 */     SegmentConstantPool globalPool = operandManager.globalConstantPool();
/* 49 */     ClassFileEntry[] nested = null;
/*    */     
/* 51 */     nested = new ClassFileEntry[] { (ClassFileEntry)globalPool.getClassSpecificPoolEntry(getPoolID(), offset, context(operandManager)) };
/* 52 */     byteCode.setNested(nested);
/* 53 */     byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\ClassSpecificReferenceForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */