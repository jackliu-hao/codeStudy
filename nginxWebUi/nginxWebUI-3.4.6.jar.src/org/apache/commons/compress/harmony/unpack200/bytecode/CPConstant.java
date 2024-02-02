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
/*    */ public abstract class CPConstant
/*    */   extends ConstantPoolEntry
/*    */ {
/*    */   private final Object value;
/*    */   
/*    */   public CPConstant(byte tag, Object value, int globalIndex) {
/* 35 */     super(tag, globalIndex);
/* 36 */     this.value = value;
/* 37 */     if (value == null) {
/* 38 */       throw new NullPointerException("Null arguments are not allowed");
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 44 */     if (this == obj) {
/* 45 */       return true;
/*    */     }
/* 47 */     if (obj == null) {
/* 48 */       return false;
/*    */     }
/* 50 */     if (getClass() != obj.getClass()) {
/* 51 */       return false;
/*    */     }
/* 53 */     CPConstant other = (CPConstant)obj;
/* 54 */     if (this.value == null) {
/* 55 */       if (other.value != null) {
/* 56 */         return false;
/*    */       }
/* 58 */     } else if (!this.value.equals(other.value)) {
/* 59 */       return false;
/*    */     } 
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     int PRIME = 31;
/* 67 */     int result = 1;
/* 68 */     result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
/* 69 */     return result;
/*    */   }
/*    */   
/*    */   protected Object getValue() {
/* 73 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */