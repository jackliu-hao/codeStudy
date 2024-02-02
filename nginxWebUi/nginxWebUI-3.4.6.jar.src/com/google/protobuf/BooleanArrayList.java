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
/*     */ final class BooleanArrayList
/*     */   extends AbstractProtobufList<Boolean>
/*     */   implements Internal.BooleanList, RandomAccess, PrimitiveNonBoxingCollection
/*     */ {
/*  48 */   private static final BooleanArrayList EMPTY_LIST = new BooleanArrayList(new boolean[0], 0); private boolean[] array;
/*     */   static {
/*  50 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   private int size;
/*     */   public static BooleanArrayList emptyList() {
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
/*     */   BooleanArrayList() {
/*  68 */     this(new boolean[10], 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BooleanArrayList(boolean[] other, int size) {
/*  76 */     this.array = other;
/*  77 */     this.size = size;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void removeRange(int fromIndex, int toIndex) {
/*  82 */     ensureIsMutable();
/*  83 */     if (toIndex < fromIndex) {
/*  84 */       throw new IndexOutOfBoundsException("toIndex < fromIndex");
/*     */     }
/*     */     
/*  87 */     System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
/*  88 */     this.size -= toIndex - fromIndex;
/*  89 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  94 */     if (this == o) {
/*  95 */       return true;
/*     */     }
/*  97 */     if (!(o instanceof BooleanArrayList)) {
/*  98 */       return super.equals(o);
/*     */     }
/* 100 */     BooleanArrayList other = (BooleanArrayList)o;
/* 101 */     if (this.size != other.size) {
/* 102 */       return false;
/*     */     }
/*     */     
/* 105 */     boolean[] arr = other.array;
/* 106 */     for (int i = 0; i < this.size; i++) {
/* 107 */       if (this.array[i] != arr[i]) {
/* 108 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 117 */     int result = 1;
/* 118 */     for (int i = 0; i < this.size; i++) {
/* 119 */       result = 31 * result + Internal.hashBoolean(this.array[i]);
/*     */     }
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Internal.BooleanList mutableCopyWithCapacity(int capacity) {
/* 126 */     if (capacity < this.size) {
/* 127 */       throw new IllegalArgumentException();
/*     */     }
/* 129 */     return new BooleanArrayList(Arrays.copyOf(this.array, capacity), this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean get(int index) {
/* 134 */     return Boolean.valueOf(getBoolean(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean(int index) {
/* 139 */     ensureIndexInRange(index);
/* 140 */     return this.array[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 145 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean set(int index, Boolean element) {
/* 150 */     return Boolean.valueOf(setBoolean(index, element.booleanValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setBoolean(int index, boolean element) {
/* 155 */     ensureIsMutable();
/* 156 */     ensureIndexInRange(index);
/* 157 */     boolean previousValue = this.array[index];
/* 158 */     this.array[index] = element;
/* 159 */     return previousValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Boolean element) {
/* 164 */     addBoolean(element.booleanValue());
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Boolean element) {
/* 170 */     addBoolean(index, element.booleanValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addBoolean(boolean element) {
/* 176 */     ensureIsMutable();
/* 177 */     if (this.size == this.array.length) {
/*     */       
/* 179 */       int length = this.size * 3 / 2 + 1;
/* 180 */       boolean[] newArray = new boolean[length];
/*     */       
/* 182 */       System.arraycopy(this.array, 0, newArray, 0, this.size);
/* 183 */       this.array = newArray;
/*     */     } 
/*     */     
/* 186 */     this.array[this.size++] = element;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addBoolean(int index, boolean element) {
/* 191 */     ensureIsMutable();
/* 192 */     if (index < 0 || index > this.size) {
/* 193 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */     
/* 196 */     if (this.size < this.array.length) {
/*     */       
/* 198 */       System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
/*     */     } else {
/*     */       
/* 201 */       int length = this.size * 3 / 2 + 1;
/* 202 */       boolean[] newArray = new boolean[length];
/*     */ 
/*     */       
/* 205 */       System.arraycopy(this.array, 0, newArray, 0, index);
/*     */ 
/*     */       
/* 208 */       System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
/* 209 */       this.array = newArray;
/*     */     } 
/*     */     
/* 212 */     this.array[index] = element;
/* 213 */     this.size++;
/* 214 */     this.modCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends Boolean> collection) {
/* 219 */     ensureIsMutable();
/*     */     
/* 221 */     Internal.checkNotNull(collection);
/*     */ 
/*     */     
/* 224 */     if (!(collection instanceof BooleanArrayList)) {
/* 225 */       return super.addAll(collection);
/*     */     }
/*     */     
/* 228 */     BooleanArrayList list = (BooleanArrayList)collection;
/* 229 */     if (list.size == 0) {
/* 230 */       return false;
/*     */     }
/*     */     
/* 233 */     int overflow = Integer.MAX_VALUE - this.size;
/* 234 */     if (overflow < list.size)
/*     */     {
/* 236 */       throw new OutOfMemoryError();
/*     */     }
/*     */     
/* 239 */     int newSize = this.size + list.size;
/* 240 */     if (newSize > this.array.length) {
/* 241 */       this.array = Arrays.copyOf(this.array, newSize);
/*     */     }
/*     */     
/* 244 */     System.arraycopy(list.array, 0, this.array, this.size, list.size);
/* 245 */     this.size = newSize;
/* 246 */     this.modCount++;
/* 247 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 252 */     ensureIsMutable();
/* 253 */     for (int i = 0; i < this.size; i++) {
/* 254 */       if (o.equals(Boolean.valueOf(this.array[i]))) {
/* 255 */         System.arraycopy(this.array, i + 1, this.array, i, this.size - i - 1);
/* 256 */         this.size--;
/* 257 */         this.modCount++;
/* 258 */         return true;
/*     */       } 
/*     */     } 
/* 261 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean remove(int index) {
/* 266 */     ensureIsMutable();
/* 267 */     ensureIndexInRange(index);
/* 268 */     boolean value = this.array[index];
/* 269 */     if (index < this.size - 1) {
/* 270 */       System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
/*     */     }
/* 272 */     this.size--;
/* 273 */     this.modCount++;
/* 274 */     return Boolean.valueOf(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureIndexInRange(int index) {
/* 284 */     if (index < 0 || index >= this.size) {
/* 285 */       throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
/*     */     }
/*     */   }
/*     */   
/*     */   private String makeOutOfBoundsExceptionMessage(int index) {
/* 290 */     return "Index:" + index + ", Size:" + this.size;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BooleanArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */