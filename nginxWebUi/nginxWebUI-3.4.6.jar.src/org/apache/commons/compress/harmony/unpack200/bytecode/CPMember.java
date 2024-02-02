/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class CPMember
/*     */   extends ClassFileEntry
/*     */ {
/*     */   List attributes;
/*     */   short flags;
/*     */   CPUTF8 name;
/*     */   transient int nameIndex;
/*     */   protected final CPUTF8 descriptor;
/*     */   transient int descriptorIndex;
/*     */   
/*     */   public CPMember(CPUTF8 name, CPUTF8 descriptor, long flags, List attributes) {
/*  46 */     this.name = name;
/*  47 */     this.descriptor = descriptor;
/*  48 */     this.flags = (short)(int)flags;
/*  49 */     this.attributes = (attributes == null) ? Collections.EMPTY_LIST : attributes;
/*  50 */     if (name == null || descriptor == null) {
/*  51 */       throw new NullPointerException("Null arguments are not allowed");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  57 */     int attributeCount = this.attributes.size();
/*  58 */     ClassFileEntry[] entries = new ClassFileEntry[attributeCount + 2];
/*  59 */     entries[0] = this.name;
/*  60 */     entries[1] = this.descriptor;
/*  61 */     for (int i = 0; i < attributeCount; i++) {
/*  62 */       entries[i + 2] = this.attributes.get(i);
/*     */     }
/*  64 */     return entries;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  69 */     super.resolve(pool);
/*  70 */     this.nameIndex = pool.indexOf(this.name);
/*  71 */     this.descriptorIndex = pool.indexOf(this.descriptor);
/*  72 */     for (int it = 0; it < this.attributes.size(); it++) {
/*  73 */       Attribute attribute = this.attributes.get(it);
/*  74 */       attribute.resolve(pool);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  80 */     int PRIME = 31;
/*  81 */     int result = 1;
/*  82 */     result = 31 * result + this.attributes.hashCode();
/*  83 */     result = 31 * result + this.descriptor.hashCode();
/*  84 */     result = 31 * result + this.flags;
/*  85 */     result = 31 * result + this.name.hashCode();
/*  86 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  91 */     return "CPMember: " + this.name + "(" + this.descriptor + ")";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  96 */     if (this == obj) {
/*  97 */       return true;
/*     */     }
/*  99 */     if (obj == null) {
/* 100 */       return false;
/*     */     }
/* 102 */     if (getClass() != obj.getClass()) {
/* 103 */       return false;
/*     */     }
/* 105 */     CPMember other = (CPMember)obj;
/* 106 */     if (!this.attributes.equals(other.attributes)) {
/* 107 */       return false;
/*     */     }
/* 109 */     if (!this.descriptor.equals(other.descriptor)) {
/* 110 */       return false;
/*     */     }
/* 112 */     if (this.flags != other.flags) {
/* 113 */       return false;
/*     */     }
/* 115 */     if (!this.name.equals(other.name)) {
/* 116 */       return false;
/*     */     }
/* 118 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(DataOutputStream dos) throws IOException {
/* 123 */     dos.writeShort(this.flags);
/* 124 */     dos.writeShort(this.nameIndex);
/* 125 */     dos.writeShort(this.descriptorIndex);
/* 126 */     int attributeCount = this.attributes.size();
/* 127 */     dos.writeShort(attributeCount);
/* 128 */     for (int i = 0; i < attributeCount; i++) {
/* 129 */       Attribute attribute = this.attributes.get(i);
/* 130 */       attribute.doWrite(dos);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CPMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */