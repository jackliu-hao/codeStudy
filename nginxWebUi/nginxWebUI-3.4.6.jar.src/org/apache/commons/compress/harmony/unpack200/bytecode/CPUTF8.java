/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
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
/*    */ public class CPUTF8
/*    */   extends ConstantPoolEntry
/*    */ {
/*    */   private final String utf8;
/*    */   private boolean hashcodeComputed;
/*    */   private int cachedHashCode;
/*    */   
/*    */   public CPUTF8(String utf8, int globalIndex) {
/* 37 */     super((byte)1, globalIndex);
/* 38 */     this.utf8 = utf8;
/* 39 */     if (utf8 == null) {
/* 40 */       throw new NullPointerException("Null arguments are not allowed");
/*    */     }
/*    */   }
/*    */   
/*    */   public CPUTF8(String string) {
/* 45 */     this(string, -1);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 50 */     if (this == obj) {
/* 51 */       return true;
/*    */     }
/* 53 */     if (obj == null) {
/* 54 */       return false;
/*    */     }
/* 56 */     if (getClass() != obj.getClass()) {
/* 57 */       return false;
/*    */     }
/* 59 */     CPUTF8 other = (CPUTF8)obj;
/* 60 */     return this.utf8.equals(other.utf8);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void generateHashCode() {
/* 67 */     this.hashcodeComputed = true;
/* 68 */     int PRIME = 31;
/* 69 */     this.cachedHashCode = 31 + this.utf8.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 74 */     if (!this.hashcodeComputed) {
/* 75 */       generateHashCode();
/*    */     }
/* 77 */     return this.cachedHashCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     return "UTF8: " + this.utf8;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 87 */     dos.writeUTF(this.utf8);
/*    */   }
/*    */   
/*    */   public String underlyingString() {
/* 91 */     return this.utf8;
/*    */   }
/*    */   
/*    */   public void setGlobalIndex(int index) {
/* 95 */     this.globalIndex = index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPUTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */