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
/*     */ final class FloatArrayList
/*     */   extends AbstractProtobufList<Float>
/*     */   implements Internal.FloatList, RandomAccess, PrimitiveNonBoxingCollection
/*     */ {
/*  48 */   private static final FloatArrayList EMPTY_LIST = new FloatArrayList(new float[0], 0); private float[] array;
/*     */   static {
/*  50 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   private int size;
/*     */   public static FloatArrayList emptyList() {
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
/*     */   FloatArrayList() {
/*  68 */     this(new float[10], 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FloatArrayList(float[] other, int size) {
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
/*  96 */     if (!(o instanceof FloatArrayList)) {
/*  97 */       return super.equals(o);
/*     */     }
/*  99 */     FloatArrayList other = (FloatArrayList)o;
/* 100 */     if (this.size != other.size) {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     float[] arr = other.array;
/* 105 */     for (int i = 0; i < this.size; i++) {
/* 106 */       if (Float.floatToIntBits(this.array[i]) != Float.floatToIntBits(arr[i])) {
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
/* 118 */       result = 31 * result + Float.floatToIntBits(this.array[i]);
/*     */     }
/* 120 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Internal.FloatList mutableCopyWithCapacity(int capacity) {
/* 125 */     if (capacity < this.size) {
/* 126 */       throw new IllegalArgumentException();
/*     */     }
/* 128 */     return new FloatArrayList(Arrays.copyOf(this.array, capacity), this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public Float get(int index) {
/* 133 */     return Float.valueOf(getFloat(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat(int index) {
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
/*     */   public Float set(int index, Float element) {
/* 149 */     return Float.valueOf(setFloat(index, element.floatValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public float setFloat(int index, float element) {
/* 154 */     ensureIsMutable();
/* 155 */     ensureIndexInRange(index);
/* 156 */     float previousValue = this.array[index];
/* 157 */     this.array[index] = element;
/* 158 */     return previousValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Float element) {
/* 163 */     addFloat(element.floatValue());
/* 164 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Float element) {
/* 169 */     addFloat(index, element.floatValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFloat(float element) {
/* 175 */     ensureIsMutable();
/* 176 */     if (this.size == this.array.length) {
/*     */       
/* 178 */       int length = this.size * 3 / 2 + 1;
/* 179 */       float[] newArray = new float[length];
/*     */       
/* 181 */       System.arraycopy(this.array, 0, newArray, 0, this.size);
/* 182 */       this.array = newArray;
/*     */     } 
/*     */     
/* 185 */     this.array[this.size++] = element;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addFloat(int index, float element) {
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
/* 201 */       float[] newArray = new float[length];
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
/*     */   public boolean addAll(Collection<? extends Float> collection) {
/* 218 */     ensureIsMutable();
/*     */     
/* 220 */     Internal.checkNotNull(collection);
/*     */ 
/*     */     
/* 223 */     if (!(collection instanceof FloatArrayList)) {
/* 224 */       return super.addAll(collection);
/*     */     }
/*     */     
/* 227 */     FloatArrayList list = (FloatArrayList)collection;
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
/* 253 */       if (o.equals(Float.valueOf(this.array[i]))) {
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
/*     */   public Float remove(int index) {
/* 265 */     ensureIsMutable();
/* 266 */     ensureIndexInRange(index);
/* 267 */     float value = this.array[index];
/* 268 */     if (index < this.size - 1) {
/* 269 */       System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
/*     */     }
/* 271 */     this.size--;
/* 272 */     this.modCount++;
/* 273 */     return Float.valueOf(value);
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FloatArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */