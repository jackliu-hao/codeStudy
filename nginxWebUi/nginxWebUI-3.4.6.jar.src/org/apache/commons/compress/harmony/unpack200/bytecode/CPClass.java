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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CPClass
/*     */   extends ConstantPoolEntry
/*     */ {
/*     */   private int index;
/*     */   public String name;
/*     */   private final CPUTF8 utf8;
/*     */   private boolean hashcodeComputed;
/*     */   private int cachedHashCode;
/*     */   
/*     */   public CPClass(CPUTF8 name, int globalIndex) {
/*  41 */     super((byte)7, globalIndex);
/*  42 */     if (name == null) {
/*  43 */       throw new NullPointerException("Null arguments are not allowed");
/*     */     }
/*  45 */     this.name = name.underlyingString();
/*  46 */     this.utf8 = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  51 */     if (this == obj) {
/*  52 */       return true;
/*     */     }
/*  54 */     if (obj == null) {
/*  55 */       return false;
/*     */     }
/*  57 */     if (getClass() != obj.getClass()) {
/*  58 */       return false;
/*     */     }
/*  60 */     CPClass other = (CPClass)obj;
/*  61 */     return this.utf8.equals(other.utf8);
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  66 */     return new ClassFileEntry[] { this.utf8 };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void generateHashCode() {
/*  73 */     this.hashcodeComputed = true;
/*  74 */     this.cachedHashCode = this.utf8.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  79 */     if (!this.hashcodeComputed) {
/*  80 */       generateHashCode();
/*     */     }
/*  82 */     return this.cachedHashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  87 */     super.resolve(pool);
/*  88 */     this.index = pool.indexOf(this.utf8);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  93 */     return "Class: " + getName();
/*     */   }
/*     */   
/*     */   public String getName() {
/*  97 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 102 */     dos.writeShort(this.index);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */