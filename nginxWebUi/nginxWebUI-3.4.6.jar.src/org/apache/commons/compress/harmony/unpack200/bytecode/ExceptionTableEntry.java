/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
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
/*    */ public class ExceptionTableEntry
/*    */ {
/*    */   private final int startPC;
/*    */   private final int endPC;
/*    */   private final int handlerPC;
/*    */   private final CPClass catchType;
/*    */   private int startPcRenumbered;
/*    */   private int endPcRenumbered;
/*    */   private int handlerPcRenumbered;
/*    */   private int catchTypeIndex;
/*    */   
/*    */   public ExceptionTableEntry(int startPC, int endPC, int handlerPC, CPClass catchType) {
/* 51 */     this.startPC = startPC;
/* 52 */     this.endPC = endPC;
/* 53 */     this.handlerPC = handlerPC;
/* 54 */     this.catchType = catchType;
/*    */   }
/*    */   
/*    */   public void write(DataOutputStream dos) throws IOException {
/* 58 */     dos.writeShort(this.startPcRenumbered);
/* 59 */     dos.writeShort(this.endPcRenumbered);
/* 60 */     dos.writeShort(this.handlerPcRenumbered);
/* 61 */     dos.writeShort(this.catchTypeIndex);
/*    */   }
/*    */   
/*    */   public void renumber(List<Integer> byteCodeOffsets) {
/* 65 */     this.startPcRenumbered = ((Integer)byteCodeOffsets.get(this.startPC)).intValue();
/* 66 */     int endPcIndex = this.startPC + this.endPC;
/* 67 */     this.endPcRenumbered = ((Integer)byteCodeOffsets.get(endPcIndex)).intValue();
/* 68 */     int handlerPcIndex = endPcIndex + this.handlerPC;
/* 69 */     this.handlerPcRenumbered = ((Integer)byteCodeOffsets.get(handlerPcIndex)).intValue();
/*    */   }
/*    */   
/*    */   public CPClass getCatchType() {
/* 73 */     return this.catchType;
/*    */   }
/*    */   
/*    */   public void resolve(ClassConstantPool pool) {
/* 77 */     if (this.catchType == null) {
/*    */ 
/*    */       
/* 80 */       this.catchTypeIndex = 0;
/*    */       return;
/*    */     } 
/* 83 */     this.catchType.resolve(pool);
/* 84 */     this.catchTypeIndex = pool.indexOf(this.catchType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ExceptionTableEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */