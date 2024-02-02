/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*    */ 
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*    */ import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*    */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
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
/*    */ public class NewClassRefForm
/*    */   extends ClassRefForm
/*    */ {
/*    */   public NewClassRefForm(int opcode, String name, int[] rewrite) {
/* 34 */     super(opcode, name, rewrite);
/*    */   }
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
/*    */   public void setByteCodeOperands(ByteCode byteCode, OperandManager operandManager, int codeLength) {
/* 48 */     ClassFileEntry[] nested = null;
/* 49 */     int offset = getOffset(operandManager);
/* 50 */     if (offset == 0) {
/*    */       
/* 52 */       SegmentConstantPool globalPool = operandManager.globalConstantPool();
/* 53 */       nested = new ClassFileEntry[] { (ClassFileEntry)globalPool.getClassPoolEntry(operandManager.getCurrentClass()) };
/* 54 */       byteCode.setNested(nested);
/* 55 */       byteCode.setNestedPositions(new int[][] { { 0, 2 } });
/*    */     } else {
/*    */ 
/*    */       
/*    */       try {
/*    */         
/* 61 */         setNestedEntries(byteCode, operandManager, offset);
/* 62 */       } catch (Pack200Exception ex) {
/* 63 */         throw new Error("Got a pack200 exception. What to do?");
/*    */       } 
/*    */     } 
/* 66 */     operandManager.setNewClass(((CPClass)byteCode.getNestedClassFileEntries()[0]).getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\NewClassRefForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */