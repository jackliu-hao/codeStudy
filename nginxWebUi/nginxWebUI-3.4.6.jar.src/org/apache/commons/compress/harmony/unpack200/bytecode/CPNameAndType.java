/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.compress.harmony.unpack200.SegmentUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CPNameAndType
/*     */   extends ConstantPoolEntry
/*     */ {
/*     */   CPUTF8 descriptor;
/*     */   transient int descriptorIndex;
/*     */   CPUTF8 name;
/*     */   transient int nameIndex;
/*     */   private boolean hashcodeComputed;
/*     */   private int cachedHashCode;
/*     */   
/*     */   public CPNameAndType(CPUTF8 name, CPUTF8 descriptor, int globalIndex) {
/*  46 */     super((byte)12, globalIndex);
/*  47 */     this.name = name;
/*  48 */     this.descriptor = descriptor;
/*  49 */     if (name == null || descriptor == null) {
/*  50 */       throw new NullPointerException("Null arguments are not allowed");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  56 */     return new ClassFileEntry[] { this.name, this.descriptor };
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  61 */     super.resolve(pool);
/*  62 */     this.descriptorIndex = pool.indexOf(this.descriptor);
/*  63 */     this.nameIndex = pool.indexOf(this.name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  73 */     dos.writeShort(this.nameIndex);
/*  74 */     dos.writeShort(this.descriptorIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  79 */     return "NameAndType: " + this.name + "(" + this.descriptor + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void generateHashCode() {
/*  86 */     this.hashcodeComputed = true;
/*  87 */     int PRIME = 31;
/*  88 */     int result = 1;
/*  89 */     result = 31 * result + this.descriptor.hashCode();
/*  90 */     result = 31 * result + this.name.hashCode();
/*  91 */     this.cachedHashCode = result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  96 */     if (!this.hashcodeComputed) {
/*  97 */       generateHashCode();
/*     */     }
/*  99 */     return this.cachedHashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 104 */     if (this == obj) {
/* 105 */       return true;
/*     */     }
/* 107 */     if (obj == null) {
/* 108 */       return false;
/*     */     }
/* 110 */     if (getClass() != obj.getClass()) {
/* 111 */       return false;
/*     */     }
/* 113 */     CPNameAndType other = (CPNameAndType)obj;
/* 114 */     if (!this.descriptor.equals(other.descriptor)) {
/* 115 */       return false;
/*     */     }
/* 117 */     if (!this.name.equals(other.name)) {
/* 118 */       return false;
/*     */     }
/* 120 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int invokeInterfaceCount() {
/* 130 */     return 1 + SegmentUtils.countInvokeInterfaceArgs(this.descriptor.underlyingString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPNameAndType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */