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
/*    */ public class CPString
/*    */   extends CPConstant
/*    */ {
/*    */   private transient int nameIndex;
/*    */   private final CPUTF8 name;
/*    */   private boolean hashcodeComputed;
/*    */   private int cachedHashCode;
/*    */   
/*    */   public CPString(CPUTF8 value, int globalIndex) {
/* 31 */     super((byte)8, value, globalIndex);
/* 32 */     this.name = value;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 37 */     dos.writeShort(this.nameIndex);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     return "String: " + getValue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 52 */     super.resolve(pool);
/* 53 */     this.nameIndex = pool.indexOf(this.name);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 58 */     return new ClassFileEntry[] { this.name };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void generateHashCode() {
/* 65 */     this.hashcodeComputed = true;
/* 66 */     int PRIME = 31;
/* 67 */     int result = 1;
/* 68 */     result = 31 * result + this.name.hashCode();
/* 69 */     this.cachedHashCode = result;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 74 */     if (!this.hashcodeComputed) {
/* 75 */       generateHashCode();
/*    */     }
/* 77 */     return this.cachedHashCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */