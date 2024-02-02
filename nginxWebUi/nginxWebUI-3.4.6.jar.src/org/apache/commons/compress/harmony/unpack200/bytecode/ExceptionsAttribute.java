/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
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
/*     */ public class ExceptionsAttribute
/*     */   extends Attribute
/*     */ {
/*     */   private static CPUTF8 attributeName;
/*     */   private transient int[] exceptionIndexes;
/*     */   private final CPClass[] exceptions;
/*     */   
/*     */   private static int hashCode(Object[] array) {
/*  31 */     int prime = 31;
/*  32 */     if (array == null) {
/*  33 */       return 0;
/*     */     }
/*  35 */     int result = 1;
/*  36 */     for (int index = 0; index < array.length; index++) {
/*  37 */       result = 31 * result + ((array[index] == null) ? 0 : array[index].hashCode());
/*     */     }
/*  39 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExceptionsAttribute(CPClass[] exceptions) {
/*  47 */     super(attributeName);
/*  48 */     this.exceptions = exceptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  53 */     if (this == obj) {
/*  54 */       return true;
/*     */     }
/*  56 */     if (!super.equals(obj)) {
/*  57 */       return false;
/*     */     }
/*  59 */     if (getClass() != obj.getClass()) {
/*  60 */       return false;
/*     */     }
/*  62 */     ExceptionsAttribute other = (ExceptionsAttribute)obj;
/*  63 */     if (!Arrays.equals((Object[])this.exceptions, (Object[])other.exceptions)) {
/*  64 */       return false;
/*     */     }
/*  66 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  71 */     return 2 + 2 * this.exceptions.length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  76 */     ClassFileEntry[] result = new ClassFileEntry[this.exceptions.length + 1];
/*  77 */     for (int i = 0; i < this.exceptions.length; i++) {
/*  78 */       result[i] = this.exceptions[i];
/*     */     }
/*  80 */     result[this.exceptions.length] = getAttributeName();
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  86 */     int prime = 31;
/*  87 */     int result = super.hashCode();
/*  88 */     result = 31 * result + hashCode((Object[])this.exceptions);
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  94 */     super.resolve(pool);
/*  95 */     this.exceptionIndexes = new int[this.exceptions.length];
/*  96 */     for (int i = 0; i < this.exceptions.length; i++) {
/*  97 */       this.exceptions[i].resolve(pool);
/*  98 */       this.exceptionIndexes[i] = pool.indexOf(this.exceptions[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     StringBuffer sb = new StringBuffer();
/* 105 */     sb.append("Exceptions: ");
/* 106 */     for (int i = 0; i < this.exceptions.length; i++) {
/* 107 */       sb.append(this.exceptions[i]);
/* 108 */       sb.append(' ');
/*     */     } 
/* 110 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 115 */     dos.writeShort(this.exceptionIndexes.length);
/* 116 */     for (int i = 0; i < this.exceptionIndexes.length; i++) {
/* 117 */       dos.writeShort(this.exceptionIndexes[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 122 */     attributeName = cpUTF8Value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ExceptionsAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */