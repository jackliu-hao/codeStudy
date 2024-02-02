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
/*     */ 
/*     */ 
/*     */ public abstract class CPRef
/*     */   extends ConstantPoolEntry
/*     */ {
/*     */   CPClass className;
/*     */   transient int classNameIndex;
/*     */   protected CPNameAndType nameAndType;
/*     */   transient int nameAndTypeIndex;
/*     */   protected String cachedToString;
/*     */   
/*     */   public CPRef(byte type, CPClass className, CPNameAndType descriptor, int globalIndex) {
/*  43 */     super(type, globalIndex);
/*  44 */     this.className = className;
/*  45 */     this.nameAndType = descriptor;
/*  46 */     if (descriptor == null || className == null) {
/*  47 */       throw new NullPointerException("Null arguments are not allowed");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  53 */     if (this == obj) {
/*  54 */       return true;
/*     */     }
/*  56 */     if (obj == null) {
/*  57 */       return false;
/*     */     }
/*  59 */     if (getClass() != obj.getClass()) {
/*  60 */       return false;
/*     */     }
/*  62 */     if (hashCode() != obj.hashCode()) {
/*  63 */       return false;
/*     */     }
/*  65 */     CPRef other = (CPRef)obj;
/*  66 */     if (!this.className.equals(other.className)) {
/*  67 */       return false;
/*     */     }
/*  69 */     if (!this.nameAndType.equals(other.nameAndType)) {
/*  70 */       return false;
/*     */     }
/*  72 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  77 */     ClassFileEntry[] entries = new ClassFileEntry[2];
/*  78 */     entries[0] = this.className;
/*  79 */     entries[1] = this.nameAndType;
/*  80 */     return entries;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  85 */     super.resolve(pool);
/*  86 */     this.nameAndTypeIndex = pool.indexOf(this.nameAndType);
/*  87 */     this.classNameIndex = pool.indexOf(this.className);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  94 */     if (this.cachedToString == null) {
/*     */       String type;
/*  96 */       if (getTag() == 9) {
/*  97 */         type = "FieldRef";
/*  98 */       } else if (getTag() == 10) {
/*  99 */         type = "MethoddRef";
/* 100 */       } else if (getTag() == 11) {
/* 101 */         type = "InterfaceMethodRef";
/*     */       } else {
/* 103 */         type = "unknown";
/*     */       } 
/* 105 */       this.cachedToString = type + ": " + this.className + "#" + this.nameAndType;
/*     */     } 
/* 107 */     return this.cachedToString;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 112 */     dos.writeShort(this.classNameIndex);
/* 113 */     dos.writeShort(this.nameAndTypeIndex);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */