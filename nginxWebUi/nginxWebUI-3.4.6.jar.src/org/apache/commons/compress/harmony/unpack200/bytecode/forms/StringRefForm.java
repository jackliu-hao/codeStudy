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
/*    */ public class StringRefForm
/*    */   extends SingleByteReferenceForm
/*    */ {
/*    */   public StringRefForm(int opcode, String name, int[] rewrite) {
/* 33 */     super(opcode, name, rewrite);
/*    */   }
/*    */   
/*    */   public StringRefForm(int opcode, String name, int[] rewrite, boolean widened) {
/* 37 */     this(opcode, name, rewrite);
/* 38 */     this.widened = widened;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getOffset(OperandManager operandManager) {
/* 43 */     return operandManager.nextStringRef();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getPoolID() {
/* 48 */     return 6;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setNestedEntries(ByteCode byteCode, OperandManager operandManager, int offset) throws Pack200Exception {
/* 54 */     SegmentConstantPool globalPool = operandManager.globalConstantPool();
/* 55 */     ClassFileEntry[] nested = null;
/* 56 */     nested = new ClassFileEntry[] { globalPool.getValue(getPoolID(), offset) };
/* 57 */     byteCode.setNested(nested);
/* 58 */     if (this.widened) {
/* 59 */       byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */     } else {
/* 61 */       byteCode.setNestedPositions(new int[][] { { 0, 1 } });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\StringRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */