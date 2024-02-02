/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
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
/*    */ public abstract class BCIRenumberedAttribute
/*    */   extends Attribute
/*    */ {
/*    */   protected boolean renumbered;
/*    */   
/*    */   public boolean hasBCIRenumbering() {
/* 39 */     return true;
/*    */   }
/*    */   
/*    */   public BCIRenumberedAttribute(CPUTF8 attributeName) {
/* 43 */     super(attributeName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract int getLength();
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract void writeBody(DataOutputStream paramDataOutputStream) throws IOException;
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract String toString();
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract int[] getStartPCs();
/*    */ 
/*    */ 
/*    */   
/*    */   public void renumber(List byteCodeOffsets) throws Pack200Exception {
/* 66 */     if (this.renumbered) {
/* 67 */       throw new Error("Trying to renumber a line number table that has already been renumbered");
/*    */     }
/* 69 */     this.renumbered = true;
/* 70 */     int[] startPCs = getStartPCs();
/* 71 */     for (int index = 0; index < startPCs.length; index++)
/* 72 */       startPCs[index] = ((Integer)byteCodeOffsets.get(startPCs[index])).intValue(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\BCIRenumberedAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */