/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
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
/*    */ public class CPMethod
/*    */   extends CPMember
/*    */ {
/*    */   private boolean hashcodeComputed;
/*    */   private int cachedHashCode;
/*    */   
/*    */   public CPMethod(CPUTF8 name, CPUTF8 descriptor, long flags, List attributes) {
/* 27 */     super(name, descriptor, flags, attributes);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 32 */     return "Method: " + this.name + "(" + this.descriptor + ")";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void generateHashCode() {
/* 39 */     this.hashcodeComputed = true;
/* 40 */     int PRIME = 31;
/* 41 */     int result = 1;
/* 42 */     result = 31 * result + this.name.hashCode();
/* 43 */     result = 31 * result + this.descriptor.hashCode();
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */