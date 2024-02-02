/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
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
/*     */ public class IntegerList
/*     */ {
/*  43 */   private static int[] EMPTY_DATA = new int[0];
/*     */   
/*     */   private static final int INITIAL_SIZE = 4;
/*     */   
/*     */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*     */   
/*     */   private int[] _data;
/*     */   
/*     */   private int _size;
/*     */   
/*     */   public IntegerList() {
/*  54 */     this._data = EMPTY_DATA;
/*     */   }
/*     */   
/*     */   public IntegerList(int capacity) {
/*  58 */     if (capacity < 0) {
/*  59 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  62 */     if (capacity == 0) {
/*  63 */       this._data = EMPTY_DATA;
/*     */     } else {
/*  65 */       this._data = new int[capacity];
/*     */     } 
/*     */   }
/*     */   
/*     */   public IntegerList(IntegerList list) {
/*  70 */     this._data = (int[])list._data.clone();
/*  71 */     this._size = list._size;
/*     */   }
/*     */   
/*     */   public IntegerList(Collection<Integer> list) {
/*  75 */     this(list.size());
/*  76 */     for (Integer value : list) {
/*  77 */       add(value.intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public final void add(int value) {
/*  82 */     if (this._data.length == this._size) {
/*  83 */       ensureCapacity(this._size + 1);
/*     */     }
/*     */     
/*  86 */     this._data[this._size] = value;
/*  87 */     this._size++;
/*     */   }
/*     */   
/*     */   public final void addAll(int[] array) {
/*  91 */     ensureCapacity(this._size + array.length);
/*  92 */     System.arraycopy(array, 0, this._data, this._size, array.length);
/*  93 */     this._size += array.length;
/*     */   }
/*     */   
/*     */   public final void addAll(IntegerList list) {
/*  97 */     ensureCapacity(this._size + list._size);
/*  98 */     System.arraycopy(list._data, 0, this._data, this._size, list._size);
/*  99 */     this._size += list._size;
/*     */   }
/*     */   
/*     */   public final void addAll(Collection<Integer> list) {
/* 103 */     ensureCapacity(this._size + list.size());
/* 104 */     int current = 0;
/* 105 */     for (Iterator<Integer> i$ = list.iterator(); i$.hasNext(); ) { int x = ((Integer)i$.next()).intValue();
/* 106 */       this._data[this._size + current] = x;
/* 107 */       current++; }
/*     */     
/* 109 */     this._size += list.size();
/*     */   }
/*     */   
/*     */   public final int get(int index) {
/* 113 */     if (index < 0 || index >= this._size) {
/* 114 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 117 */     return this._data[index];
/*     */   }
/*     */   
/*     */   public final boolean contains(int value) {
/* 121 */     for (int i = 0; i < this._size; i++) {
/* 122 */       if (this._data[i] == value) {
/* 123 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 127 */     return false;
/*     */   }
/*     */   
/*     */   public final int set(int index, int value) {
/* 131 */     if (index < 0 || index >= this._size) {
/* 132 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 135 */     int previous = this._data[index];
/* 136 */     this._data[index] = value;
/* 137 */     return previous;
/*     */   }
/*     */   
/*     */   public final int removeAt(int index) {
/* 141 */     int value = get(index);
/* 142 */     System.arraycopy(this._data, index + 1, this._data, index, this._size - index - 1);
/* 143 */     this._data[this._size - 1] = 0;
/* 144 */     this._size--;
/* 145 */     return value;
/*     */   }
/*     */   
/*     */   public final void removeRange(int fromIndex, int toIndex) {
/* 149 */     if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
/* 150 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 152 */     if (fromIndex > toIndex) {
/* 153 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/* 156 */     System.arraycopy(this._data, toIndex, this._data, fromIndex, this._size - toIndex);
/* 157 */     Arrays.fill(this._data, this._size - toIndex - fromIndex, this._size, 0);
/* 158 */     this._size -= toIndex - fromIndex;
/*     */   }
/*     */   
/*     */   public final boolean isEmpty() {
/* 162 */     return (this._size == 0);
/*     */   }
/*     */   
/*     */   public final int size() {
/* 166 */     return this._size;
/*     */   }
/*     */   
/*     */   public final void trimToSize() {
/* 170 */     if (this._data.length == this._size) {
/*     */       return;
/*     */     }
/*     */     
/* 174 */     this._data = Arrays.copyOf(this._data, this._size);
/*     */   }
/*     */   
/*     */   public final void clear() {
/* 178 */     Arrays.fill(this._data, 0, this._size, 0);
/* 179 */     this._size = 0;
/*     */   }
/*     */   
/*     */   public final int[] toArray() {
/* 183 */     if (this._size == 0) {
/* 184 */       return EMPTY_DATA;
/*     */     }
/*     */     
/* 187 */     return Arrays.copyOf(this._data, this._size);
/*     */   }
/*     */   
/*     */   public final void sort() {
/* 191 */     Arrays.sort(this._data, 0, this._size);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 214 */     if (o == this) {
/* 215 */       return true;
/*     */     }
/*     */     
/* 218 */     if (!(o instanceof IntegerList)) {
/* 219 */       return false;
/*     */     }
/*     */     
/* 222 */     IntegerList other = (IntegerList)o;
/* 223 */     if (this._size != other._size) {
/* 224 */       return false;
/*     */     }
/*     */     
/* 227 */     for (int i = 0; i < this._size; i++) {
/* 228 */       if (this._data[i] != other._data[i]) {
/* 229 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 233 */     return true;
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
/*     */   public int hashCode() {
/* 247 */     int hashCode = 1;
/* 248 */     for (int i = 0; i < this._size; i++) {
/* 249 */       hashCode = 31 * hashCode + this._data[i];
/*     */     }
/*     */     
/* 252 */     return hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 260 */     return Arrays.toString(toArray());
/*     */   }
/*     */   
/*     */   public final int binarySearch(int key) {
/* 264 */     return Arrays.binarySearch(this._data, 0, this._size, key);
/*     */   }
/*     */   
/*     */   public final int binarySearch(int fromIndex, int toIndex, int key) {
/* 268 */     if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
/* 269 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 271 */     if (fromIndex > toIndex) {
/* 272 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/* 275 */     return Arrays.binarySearch(this._data, fromIndex, toIndex, key);
/*     */   }
/*     */   private void ensureCapacity(int capacity) {
/*     */     int newLength;
/* 279 */     if (capacity < 0 || capacity > 2147483639) {
/* 280 */       throw new OutOfMemoryError();
/*     */     }
/*     */ 
/*     */     
/* 284 */     if (this._data.length == 0) {
/* 285 */       newLength = 4;
/*     */     } else {
/* 287 */       newLength = this._data.length;
/*     */     } 
/*     */     
/* 290 */     while (newLength < capacity) {
/* 291 */       newLength *= 2;
/* 292 */       if (newLength < 0 || newLength > 2147483639) {
/* 293 */         newLength = 2147483639;
/*     */       }
/*     */     } 
/*     */     
/* 297 */     this._data = Arrays.copyOf(this._data, newLength);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\IntegerList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */