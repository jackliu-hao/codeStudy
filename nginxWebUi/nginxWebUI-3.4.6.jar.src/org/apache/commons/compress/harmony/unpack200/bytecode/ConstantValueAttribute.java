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
/*    */ public class ConstantValueAttribute
/*    */   extends Attribute
/*    */ {
/*    */   private int constantIndex;
/*    */   private final ClassFileEntry entry;
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 34 */     attributeName = cpUTF8Value;
/*    */   }
/*    */   
/*    */   public ConstantValueAttribute(ClassFileEntry entry) {
/* 38 */     super(attributeName);
/* 39 */     if (entry == null) {
/* 40 */       throw new NullPointerException();
/*    */     }
/* 42 */     this.entry = entry;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 47 */     if (this == obj) {
/* 48 */       return true;
/*    */     }
/* 50 */     if (!super.equals(obj)) {
/* 51 */       return false;
/*    */     }
/* 53 */     if (getClass() != obj.getClass()) {
/* 54 */       return false;
/*    */     }
/* 56 */     ConstantValueAttribute other = (ConstantValueAttribute)obj;
/* 57 */     if (this.entry == null) {
/* 58 */       if (other.entry != null) {
/* 59 */         return false;
/*    */       }
/* 61 */     } else if (!this.entry.equals(other.entry)) {
/* 62 */       return false;
/*    */     } 
/* 64 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 69 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 74 */     return new ClassFileEntry[] { getAttributeName(), this.entry };
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 79 */     int PRIME = 31;
/* 80 */     int result = super.hashCode();
/* 81 */     result = 31 * result + ((this.entry == null) ? 0 : this.entry.hashCode());
/* 82 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 87 */     super.resolve(pool);
/* 88 */     this.entry.resolve(pool);
/* 89 */     this.constantIndex = pool.indexOf(this.entry);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 94 */     return "Constant:" + this.entry;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 99 */     dos.writeShort(this.constantIndex);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ConstantValueAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */