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
/*     */ public class CPFieldRef
/*     */   extends ConstantPoolEntry
/*     */ {
/*     */   CPClass className;
/*     */   transient int classNameIndex;
/*     */   private final CPNameAndType nameAndType;
/*     */   transient int nameAndTypeIndex;
/*     */   private boolean hashcodeComputed;
/*     */   private int cachedHashCode;
/*     */   
/*     */   public CPFieldRef(CPClass className, CPNameAndType descriptor, int globalIndex) {
/*  33 */     super((byte)9, globalIndex);
/*  34 */     this.className = className;
/*  35 */     this.nameAndType = descriptor;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  40 */     return new ClassFileEntry[] { this.className, this.nameAndType };
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  45 */     super.resolve(pool);
/*  46 */     this.nameAndTypeIndex = pool.indexOf(this.nameAndType);
/*  47 */     this.classNameIndex = pool.indexOf(this.className);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  52 */     dos.writeShort(this.classNameIndex);
/*  53 */     dos.writeShort(this.nameAndTypeIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  58 */     return "FieldRef: " + this.className + "#" + this.nameAndType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void generateHashCode() {
/*  65 */     this.hashcodeComputed = true;
/*  66 */     int PRIME = 31;
/*  67 */     int result = 1;
/*  68 */     result = 31 * result + ((this.className == null) ? 0 : this.className.hashCode());
/*  69 */     result = 31 * result + ((this.nameAndType == null) ? 0 : this.nameAndType.hashCode());
/*  70 */     this.cachedHashCode = result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  75 */     if (!this.hashcodeComputed) {
/*  76 */       generateHashCode();
/*     */     }
/*  78 */     return this.cachedHashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  83 */     if (this == obj) {
/*  84 */       return true;
/*     */     }
/*  86 */     if (obj == null) {
/*  87 */       return false;
/*     */     }
/*  89 */     if (getClass() != obj.getClass()) {
/*  90 */       return false;
/*     */     }
/*  92 */     CPFieldRef other = (CPFieldRef)obj;
/*  93 */     if (this.className == null) {
/*  94 */       if (other.className != null) {
/*  95 */         return false;
/*     */       }
/*  97 */     } else if (!this.className.equals(other.className)) {
/*  98 */       return false;
/*     */     } 
/* 100 */     if (this.nameAndType == null) {
/* 101 */       if (other.nameAndType != null) {
/* 102 */         return false;
/*     */       }
/* 104 */     } else if (!this.nameAndType.equals(other.nameAndType)) {
/* 105 */       return false;
/*     */     } 
/* 107 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPFieldRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */