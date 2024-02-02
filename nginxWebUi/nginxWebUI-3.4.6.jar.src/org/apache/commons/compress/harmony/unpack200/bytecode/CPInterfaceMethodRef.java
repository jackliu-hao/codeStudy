/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
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
/*    */ public class CPInterfaceMethodRef
/*    */   extends CPRef
/*    */ {
/*    */   private boolean hashcodeComputed;
/*    */   private int cachedHashCode;
/*    */   
/*    */   public CPInterfaceMethodRef(CPClass className, CPNameAndType descriptor, int globalIndex) {
/* 22 */     super((byte)11, className, descriptor, globalIndex);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int invokeInterfaceCount() {
/* 32 */     return this.nameAndType.invokeInterfaceCount();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void generateHashCode() {
/* 39 */     this.hashcodeComputed = true;
/* 40 */     int PRIME = 31;
/* 41 */     int result = 1;
/* 42 */     result = 31 * result + this.className.hashCode();
/* 43 */     result = 31 * result + this.nameAndType.hashCode();
/* 44 */     this.cachedHashCode = result;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     if (!this.hashcodeComputed) {
/* 50 */       generateHashCode();
/*    */     }
/* 52 */     return this.cachedHashCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPInterfaceMethodRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */