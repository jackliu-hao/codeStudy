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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CPMethodRef
/*    */   extends CPRef
/*    */ {
/*    */   private boolean hashcodeComputed;
/*    */   private int cachedHashCode;
/*    */   
/*    */   public CPMethodRef(CPClass className, CPNameAndType descriptor, int globalIndex) {
/* 25 */     super((byte)10, className, descriptor, globalIndex);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 30 */     return new ClassFileEntry[] { this.className, this.nameAndType };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void generateHashCode() {
/* 37 */     this.hashcodeComputed = true;
/* 38 */     int PRIME = 31;
/* 39 */     int result = 1;
/* 40 */     result = 31 * result + this.className.hashCode();
/* 41 */     result = 31 * result + this.nameAndType.hashCode();
/* 42 */     this.cachedHashCode = result;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 47 */     if (!this.hashcodeComputed) {
/* 48 */       generateHashCode();
/*    */     }
/* 50 */     return this.cachedHashCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPMethodRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */