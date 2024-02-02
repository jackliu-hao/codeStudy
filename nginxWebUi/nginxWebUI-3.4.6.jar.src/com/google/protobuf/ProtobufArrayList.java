/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.RandomAccess;
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
/*     */ 
/*     */ 
/*     */ final class ProtobufArrayList<E>
/*     */   extends AbstractProtobufList<E>
/*     */   implements RandomAccess
/*     */ {
/*  40 */   private static final ProtobufArrayList<Object> EMPTY_LIST = new ProtobufArrayList((E[])new Object[0], 0);
/*     */   private E[] array;
/*     */   
/*     */   static {
/*  44 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   private int size;
/*     */   
/*     */   public static <E> ProtobufArrayList<E> emptyList() {
/*  49 */     return (ProtobufArrayList)EMPTY_LIST;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ProtobufArrayList() {
/*  57 */     this((E[])new Object[10], 0);
/*     */   }
/*     */   
/*     */   private ProtobufArrayList(E[] array, int size) {
/*  61 */     this.array = array;
/*  62 */     this.size = size;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtobufArrayList<E> mutableCopyWithCapacity(int capacity) {
/*  67 */     if (capacity < this.size) {
/*  68 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  71 */     E[] newArray = Arrays.copyOf(this.array, capacity);
/*     */     
/*  73 */     return new ProtobufArrayList(newArray, this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(E element) {
/*  78 */     ensureIsMutable();
/*     */     
/*  80 */     if (this.size == this.array.length) {
/*     */       
/*  82 */       int length = this.size * 3 / 2 + 1;
/*  83 */       E[] newArray = Arrays.copyOf(this.array, length);
/*     */       
/*  85 */       this.array = newArray;
/*     */     } 
/*     */     
/*  88 */     this.array[this.size++] = element;
/*  89 */     this.modCount++;
/*     */     
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, E element) {
/*  96 */     ensureIsMutable();
/*     */     
/*  98 */     if (index < 0 || index > this.size) {
/*  99 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */     
/* 102 */     if (this.size < this.array.length) {
/*     */       
/* 104 */       System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
/*     */     } else {
/*     */       
/* 107 */       int length = this.size * 3 / 2 + 1;
/* 108 */       E[] newArray = createArray(length);
/*     */ 
/*     */       
/* 111 */       System.arraycopy(this.array, 0, newArray, 0, index);
/*     */ 
/*     */       
/* 114 */       System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
/* 115 */       this.array = newArray;
/*     */     } 
/*     */     
/* 118 */     this.array[index] = element;
/* 119 */     this.size++;
/* 120 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public E get(int index) {
/* 125 */     ensureIndexInRange(index);
/* 126 */     return this.array[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public E remove(int index) {
/* 131 */     ensureIsMutable();
/* 132 */     ensureIndexInRange(index);
/*     */     
/* 134 */     E value = this.array[index];
/* 135 */     if (index < this.size - 1) {
/* 136 */       System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
/*     */     }
/*     */     
/* 139 */     this.size--;
/* 140 */     this.modCount++;
/* 141 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public E set(int index, E element) {
/* 146 */     ensureIsMutable();
/* 147 */     ensureIndexInRange(index);
/*     */     
/* 149 */     E toReturn = this.array[index];
/* 150 */     this.array[index] = element;
/*     */     
/* 152 */     this.modCount++;
/* 153 */     return toReturn;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 158 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   private static <E> E[] createArray(int capacity) {
/* 163 */     return (E[])new Object[capacity];
/*     */   }
/*     */   
/*     */   private void ensureIndexInRange(int index) {
/* 167 */     if (index < 0 || index >= this.size) {
/* 168 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */   }
/*     */   
/*     */   private String makeOutOfBoundsExceptionMessage(int index) {
/* 173 */     return "Index:" + index + ", Size:" + this.size;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ProtobufArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */