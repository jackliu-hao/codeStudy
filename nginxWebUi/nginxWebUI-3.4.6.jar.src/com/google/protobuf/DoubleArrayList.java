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
/*     */ final class DoubleArrayList
/*     */   extends AbstractProtobufList<Double>
/*     */   implements Internal.DoubleList, RandomAccess, PrimitiveNonBoxingCollection
/*     */ {
/*  48 */   private static final DoubleArrayList EMPTY_LIST = new DoubleArrayList(new double[0], 0); private double[] array;
/*     */   static {
/*  50 */     EMPTY_LIST.makeImmutable();
/*     */   }
/*     */   private int size;
/*     */   public static DoubleArrayList emptyList() {
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
/*     */   DoubleArrayList() {
/*  68 */     this(new double[10], 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DoubleArrayList(double[] other, int size) {
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
/*  96 */     if (!(o instanceof DoubleArrayList)) {
/*  97 */       return super.equals(o);
/*     */     }
/*  99 */     DoubleArrayList other = (DoubleArrayList)o;
/* 100 */     if (this.size != other.size) {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     double[] arr = other.array;
/* 105 */     for (int i = 0; i < this.size; i++) {
/* 106 */       if (Double.doubleToLongBits(this.array[i]) != Double.doubleToLongBits(arr[i])) {
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
/* 118 */       long bits = Double.doubleToLongBits(this.array[i]);
/* 119 */       result = 31 * result + Internal.hashLong(bits);
/*     */     } 
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Internal.DoubleList mutableCopyWithCapacity(int capacity) {
/* 126 */     if (capacity < this.size) {
/* 127 */       throw new IllegalArgumentException();
/*     */     }
/* 129 */     return new DoubleArrayList(Arrays.copyOf(this.array, capacity), this.size);
/*     */   }
/*     */ 
/*     */   
/*     */   public Double get(int index) {
/* 134 */     return Double.valueOf(getDouble(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(int index) {
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
/*     */   public Double set(int index, Double element) {
/* 150 */     return Double.valueOf(setDouble(index, element.doubleValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public double setDouble(int index, double element) {
/* 155 */     ensureIsMutable();
/* 156 */     ensureIndexInRange(index);
/* 157 */     double previousValue = this.array[index];
/* 158 */     this.array[index] = element;
/* 159 */     return previousValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Double element) {
/* 164 */     addDouble(element.doubleValue());
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Double element) {
/* 170 */     addDouble(index, element.doubleValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDouble(double element) {
/* 176 */     ensureIsMutable();
/* 177 */     if (this.size == this.array.length) {
/*     */       
/* 179 */       int length = this.size * 3 / 2 + 1;
/* 180 */       double[] newArray = new double[length];
/*     */       
/* 182 */       System.arraycopy(this.array, 0, newArray, 0, this.size);
/* 183 */       this.array = newArray;
/*     */     } 
/*     */     
/* 186 */     this.array[this.size++] = element;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addDouble(int index, double element) {
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
/* 202 */       double[] newArray = new double[length];
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
/*     */   public boolean addAll(Collection<? extends Double> collection) {
/* 219 */     ensureIsMutable();
/*     */     
/* 221 */     Internal.checkNotNull(collection);
/*     */ 
/*     */     
/* 224 */     if (!(collection instanceof DoubleArrayList)) {
/* 225 */       return super.addAll(collection);
/*     */     }
/*     */     
/* 228 */     DoubleArrayList list = (DoubleArrayList)collection;
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
/* 254 */       if (o.equals(Double.valueOf(this.array[i]))) {
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
/*     */   public Double remove(int index) {
/* 266 */     ensureIsMutable();
/* 267 */     ensureIndexInRange(index);
/* 268 */     double value = this.array[index];
/* 269 */     if (index < this.size - 1) {
/* 270 */       System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
/*     */     }
/* 272 */     this.size--;
/* 273 */     this.modCount++;
/* 274 */     return Double.valueOf(value);
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\DoubleArrayList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */