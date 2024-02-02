/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Attribute
/*     */   extends ClassFileEntry
/*     */ {
/*     */   protected final CPUTF8 attributeName;
/*     */   private int attributeNameIndex;
/*     */   
/*     */   public Attribute(CPUTF8 attributeName) {
/*  32 */     this.attributeName = attributeName;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(DataOutputStream dos) throws IOException {
/*  37 */     dos.writeShort(this.attributeNameIndex);
/*  38 */     dos.writeInt(getLength());
/*  39 */     writeBody(dos);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  44 */     if (this == obj) {
/*  45 */       return true;
/*     */     }
/*  47 */     if (obj == null) {
/*  48 */       return false;
/*     */     }
/*  50 */     if (getClass() != obj.getClass()) {
/*  51 */       return false;
/*     */     }
/*  53 */     Attribute other = (Attribute)obj;
/*  54 */     if (this.attributeName == null) {
/*  55 */       if (other.attributeName != null) {
/*  56 */         return false;
/*     */       }
/*  58 */     } else if (!this.attributeName.equals(other.attributeName)) {
/*  59 */       return false;
/*     */     } 
/*  61 */     return true;
/*     */   }
/*     */   
/*     */   protected CPUTF8 getAttributeName() {
/*  65 */     return this.attributeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int getLength();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getLengthIncludingHeader() {
/*  78 */     return getLength() + 2 + 4;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  83 */     return new ClassFileEntry[] { getAttributeName() };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasBCIRenumbering() {
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSourceFileAttribute() {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 107 */     int PRIME = 31;
/* 108 */     int result = 1;
/* 109 */     result = 31 * result + ((this.attributeName == null) ? 0 : this.attributeName.hashCode());
/* 110 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/* 115 */     super.resolve(pool);
/* 116 */     this.attributeNameIndex = pool.indexOf(this.attributeName);
/*     */   }
/*     */   
/*     */   protected abstract void writeBody(DataOutputStream paramDataOutputStream) throws IOException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */