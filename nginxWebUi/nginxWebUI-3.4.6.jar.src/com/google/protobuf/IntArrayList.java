/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class IntArrayList
/*     */   extends AbstractProtobufList<Integer>
/*     */   implements Internal.IntList, RandomAccess, PrimitiveNonBoxingCollection
/*     */ {
/*  48 */   private static final IntArrayList EMPTY_LIST = new IntArrayList(new int[0], 0); private int[] array;
/*     */   static {
/*  50 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   private int size;
/*     */   public static IntArrayList emptyList() {
/*  54 */     return EMPTY_LIST;
/*     */   }
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
/*     */   IntArrayList() {
/*  68 */     this(new int[10], 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IntArrayList(int[] other, int size) {
/*  75 */     this.array = other;
/*  76 */     this.size = size;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void removeRange(int fromIndex, int toIndex) {
/*  81 */     ensureIsMutable();
/*  82 */     if (toIndex < fromIndex) {
/*  83 */       throw new IndexOutOfBoundsException("toIndex < fromIndex");
/*     */     }
/*     */     
/*  86 */     System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
/*  87 */     this.size -= toIndex - fromIndex;
/*  88 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  93 */     if (this == o) {
/*  94 */       return true;
/*     */     }
/*  96 */     if (!(o instanceof IntArrayList)) {
/*  97 */       return super.equals(o);
/*     */     }
/*  99 */     IntArrayList other = (IntArrayList)o;
/* 100 */     if (this.size != other.size) {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     int[] arr = other.array;
/* 105 */     for (int i = 0; i < this.size; i++) {
/* 106 */       if (this.array[i] != arr[i]) {
/* 107 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 111 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 116 */     int result = 1;
/* 117 */     for (int i = 0; i < this.size; i++) {
/* 118 */       result = 31 * result + this.array[i];
/*     */     }
/* 120 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Internal.IntList mutableCopyWithCapacity(int capacity) {
/* 125 */     if (capacity < this.size) {
/* 126 */       throw new IllegalArgumentException();
/*     */     }
/* 128 */     return new IntArrayList(Arrays.copyOf(this.array, capacity), this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer get(int index) {
/* 133 */     return Integer.valueOf(getInt(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int index) {
/* 138 */     ensureIndexInRange(index);
/* 139 */     return this.array[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 144 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer set(int index, Integer element) {
/* 149 */     return Integer.valueOf(setInt(index, element.intValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int setInt(int index, int element) {
/* 154 */     ensureIsMutable();
/* 155 */     ensureIndexInRange(index);
/* 156 */     int previousValue = this.array[index];
/* 157 */     this.array[index] = element;
/* 158 */     return previousValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Integer element) {
/* 163 */     addInt(element.intValue());
/* 164 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Integer element) {
/* 169 */     addInt(index, element.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addInt(int element) {
/* 175 */     ensureIsMutable();
/* 176 */     if (this.size == this.array.length) {
/*     */       
/* 178 */       int length = this.size * 3 / 2 + 1;
/* 179 */       int[] newArray = new int[length];
/*     */       
/* 181 */       System.arraycopy(this.array, 0, newArray, 0, this.size);
/* 182 */       this.array = newArray;
/*     */     } 
/*     */     
/* 185 */     this.array[this.size++] = element;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addInt(int index, int element) {
/* 190 */     ensureIsMutable();
/* 191 */     if (index < 0 || index > this.size) {
/* 192 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */     
/* 195 */     if (this.size < this.array.length) {
/*     */       
/* 197 */       System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
/*     */     } else {
/*     */       
/* 200 */       int length = this.size * 3 / 2 + 1;
/* 201 */       int[] newArray = new int[length];
/*     */ 
/*     */       
/* 204 */       System.arraycopy(this.array, 0, newArray, 0, index);
/*     */ 
/*     */       
/* 207 */       System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
/* 208 */       this.array = newArray;
/*     */     } 
/*     */     
/* 211 */     this.array[index] = element;
/* 212 */     this.size++;
/* 213 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends Integer> collection) {
/* 218 */     ensureIsMutable();
/*     */     
/* 220 */     Internal.checkNotNull(collection);
/*     */ 
/*     */     
/* 223 */     if (!(collection instanceof IntArrayList)) {
/* 224 */       return super.addAll(collection);
/*     */     }
/*     */     
/* 227 */     IntArrayList list = (IntArrayList)collection;
/* 228 */     if (list.size == 0) {
/* 229 */       return false;
/*     */     }
/*     */     
/* 232 */     int overflow = Integer.MAX_VALUE - this.size;
/* 233 */     if (overflow < list.size)
/*     */     {
/* 235 */       throw new OutOfMemoryError();
/*     */     }
/*     */     
/* 238 */     int newSize = this.size + list.size;
/* 239 */     if (newSize > this.array.length) {
/* 240 */       this.array = Arrays.copyOf(this.array, newSize);
/*     */     }
/*     */     
/* 243 */     System.arraycopy(list.array, 0, this.array, this.size, list.size);
/* 244 */     this.size = newSize;
/* 245 */     this.modCount++;
/* 246 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 251 */     ensureIsMutable();
/* 252 */     for (int i = 0; i < this.size; i++) {
/* 253 */       if (o.equals(Integer.valueOf(this.array[i]))) {
/* 254 */         System.arraycopy(this.array, i + 1, this.array, i, this.size - i - 1);
/* 255 */         this.size--;
/* 256 */         this.modCount++;
/* 257 */         return true;
/*     */       } 
/*     */     } 
/* 260 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer remove(int index) {
/* 265 */     ensureIsMutable();
/* 266 */     ensureIndexInRange(index);
/* 267 */     int value = this.array[index];
/* 268 */     if (index < this.size - 1) {
/* 269 */       System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
/*     */     }
/* 271 */     this.size--;
/* 272 */     this.modCount++;
/* 273 */     return Integer.valueOf(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureIndexInRange(int index) {
/* 283 */     if (index < 0 || index >= this.size) {
/* 284 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */   }
/*     */   
/*     */   private String makeOutOfBoundsExceptionMessage(int index) {
/* 289 */     return "Index:" + index + ", Size:" + this.size;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\IntArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */