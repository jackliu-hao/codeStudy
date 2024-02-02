/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public final class ByteStack
/*     */ {
/*     */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*     */   private int size;
/*  27 */   private byte[] array = Utils.EMPTY_BYTES;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void push(byte paramByte) {
/*  37 */     int i = this.size;
/*  38 */     int j = this.array.length;
/*  39 */     if (i >= j) {
/*  40 */       grow(j);
/*     */     }
/*  42 */     this.array[i] = paramByte;
/*  43 */     this.size = i + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte pop() {
/*  54 */     int i = this.size - 1;
/*  55 */     if (i < 0) {
/*  56 */       throw new NoSuchElementException();
/*     */     }
/*  58 */     this.size = i;
/*  59 */     return this.array[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int poll(int paramInt) {
/*  70 */     int i = this.size - 1;
/*  71 */     if (i < 0) {
/*  72 */       return paramInt;
/*     */     }
/*  74 */     this.size = i;
/*  75 */     return this.array[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int peek(int paramInt) {
/*  86 */     int i = this.size - 1;
/*  87 */     if (i < 0) {
/*  88 */       return paramInt;
/*     */     }
/*  90 */     return this.array[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  99 */     return (this.size == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 108 */     return this.size;
/*     */   }
/*     */   
/*     */   private void grow(int paramInt) {
/* 112 */     if (paramInt == 0)
/* 113 */     { paramInt = 16; }
/* 114 */     else { if (paramInt >= 2147483639)
/* 115 */         throw new OutOfMemoryError(); 
/* 116 */       if ((paramInt <<= 1) < 0)
/* 117 */         paramInt = 2147483639;  }
/*     */     
/* 119 */     this.array = Arrays.copyOf(this.array, paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\ByteStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */