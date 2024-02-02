/*     */ package org.h2.util;
/*     */ 
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
/*     */ public class IntArray
/*     */ {
/*     */   private int[] data;
/*     */   private int size;
/*     */   private int hash;
/*     */   
/*     */   public IntArray() {
/*  23 */     this(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntArray(int paramInt) {
/*  32 */     this.data = new int[paramInt];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntArray(int[] paramArrayOfint) {
/*  41 */     this.data = paramArrayOfint;
/*  42 */     this.size = paramArrayOfint.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(int paramInt) {
/*  51 */     if (this.size >= this.data.length) {
/*  52 */       ensureCapacity(this.size + this.size);
/*     */     }
/*  54 */     this.data[this.size++] = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int get(int paramInt) {
/*  64 */     if (paramInt >= this.size) {
/*  65 */       throw new ArrayIndexOutOfBoundsException("i=" + paramInt + " size=" + this.size);
/*     */     }
/*  67 */     return this.data[paramInt];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(int paramInt) {
/*  76 */     if (paramInt >= this.size) {
/*  77 */       throw new ArrayIndexOutOfBoundsException("i=" + paramInt + " size=" + this.size);
/*     */     }
/*  79 */     System.arraycopy(this.data, paramInt + 1, this.data, paramInt, this.size - paramInt - 1);
/*  80 */     this.size--;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ensureCapacity(int paramInt) {
/*  90 */     paramInt = Math.max(4, paramInt);
/*  91 */     if (paramInt >= this.data.length) {
/*  92 */       this.data = Arrays.copyOf(this.data, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*  98 */     if (!(paramObject instanceof IntArray)) {
/*  99 */       return false;
/*     */     }
/* 101 */     IntArray intArray = (IntArray)paramObject;
/* 102 */     if (hashCode() != intArray.hashCode() || this.size != intArray.size) {
/* 103 */       return false;
/*     */     }
/* 105 */     for (byte b = 0; b < this.size; b++) {
/* 106 */       if (this.data[b] != intArray.data[b]) {
/* 107 */         return false;
/*     */       }
/*     */     } 
/* 110 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 115 */     if (this.hash != 0) {
/* 116 */       return this.hash;
/*     */     }
/* 118 */     int i = this.size + 1;
/* 119 */     for (byte b = 0; b < this.size; b++) {
/* 120 */       i = i * 31 + this.data[b];
/*     */     }
/* 122 */     this.hash = i;
/* 123 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 132 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void toArray(int[] paramArrayOfint) {
/* 141 */     System.arraycopy(this.data, 0, paramArrayOfint, 0, this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 146 */     StringBuilder stringBuilder = new StringBuilder("{");
/* 147 */     for (byte b = 0; b < this.size; b++) {
/* 148 */       if (b > 0) {
/* 149 */         stringBuilder.append(", ");
/*     */       }
/* 151 */       stringBuilder.append(this.data[b]);
/*     */     } 
/* 153 */     return stringBuilder.append('}').toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeRange(int paramInt1, int paramInt2) {
/* 163 */     if (paramInt1 > paramInt2 || paramInt2 > this.size) {
/* 164 */       throw new ArrayIndexOutOfBoundsException("from=" + paramInt1 + " to=" + paramInt2 + " size=" + this.size);
/*     */     }
/* 166 */     System.arraycopy(this.data, paramInt2, this.data, paramInt1, this.size - paramInt2);
/* 167 */     this.size -= paramInt2 - paramInt1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\IntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */