/*     */ package org.apache.commons.compress.harmony.pack200;
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
/*     */ public class IntList
/*     */ {
/*     */   private int[] array;
/*     */   private int firstIndex;
/*     */   private int lastIndex;
/*     */   private int modCount;
/*     */   
/*     */   public IntList() {
/*  36 */     this(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntList(int capacity) {
/*  45 */     if (capacity < 0) {
/*  46 */       throw new IllegalArgumentException();
/*     */     }
/*  48 */     this.firstIndex = this.lastIndex = 0;
/*  49 */     this.array = new int[capacity];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean add(int object) {
/*  59 */     if (this.lastIndex == this.array.length) {
/*  60 */       growAtEnd(1);
/*     */     }
/*  62 */     this.array[this.lastIndex++] = object;
/*  63 */     this.modCount++;
/*  64 */     return true;
/*     */   }
/*     */   
/*     */   public void add(int location, int object) {
/*  68 */     int size = this.lastIndex - this.firstIndex;
/*  69 */     if (0 < location && location < size) {
/*  70 */       if (this.firstIndex == 0 && this.lastIndex == this.array.length) {
/*  71 */         growForInsert(location, 1);
/*  72 */       } else if ((location < size / 2 && this.firstIndex > 0) || this.lastIndex == this.array.length) {
/*  73 */         System.arraycopy(this.array, this.firstIndex, this.array, --this.firstIndex, location);
/*     */       } else {
/*  75 */         int index = location + this.firstIndex;
/*  76 */         System.arraycopy(this.array, index, this.array, index + 1, size - location);
/*  77 */         this.lastIndex++;
/*     */       } 
/*  79 */       this.array[location + this.firstIndex] = object;
/*  80 */     } else if (location == 0) {
/*  81 */       if (this.firstIndex == 0) {
/*  82 */         growAtFront(1);
/*     */       }
/*  84 */       this.array[--this.firstIndex] = object;
/*  85 */     } else if (location == size) {
/*  86 */       if (this.lastIndex == this.array.length) {
/*  87 */         growAtEnd(1);
/*     */       }
/*  89 */       this.array[this.lastIndex++] = object;
/*     */     } else {
/*  91 */       throw new IndexOutOfBoundsException();
/*     */     } 
/*     */     
/*  94 */     this.modCount++;
/*     */   }
/*     */   
/*     */   public void clear() {
/*  98 */     if (this.firstIndex != this.lastIndex) {
/*  99 */       Arrays.fill(this.array, this.firstIndex, this.lastIndex, -1);
/* 100 */       this.firstIndex = this.lastIndex = 0;
/* 101 */       this.modCount++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int get(int location) {
/* 106 */     if (0 <= location && location < this.lastIndex - this.firstIndex) {
/* 107 */       return this.array[this.firstIndex + location];
/*     */     }
/* 109 */     throw new IndexOutOfBoundsException("" + location);
/*     */   }
/*     */   
/*     */   private void growAtEnd(int required) {
/* 113 */     int size = this.lastIndex - this.firstIndex;
/* 114 */     if (this.firstIndex >= required - this.array.length - this.lastIndex) {
/* 115 */       int newLast = this.lastIndex - this.firstIndex;
/* 116 */       if (size > 0) {
/* 117 */         System.arraycopy(this.array, this.firstIndex, this.array, 0, size);
/*     */       }
/* 119 */       this.firstIndex = 0;
/* 120 */       this.lastIndex = newLast;
/*     */     } else {
/* 122 */       int increment = size / 2;
/* 123 */       if (required > increment) {
/* 124 */         increment = required;
/*     */       }
/* 126 */       if (increment < 12) {
/* 127 */         increment = 12;
/*     */       }
/* 129 */       int[] newArray = new int[size + increment];
/* 130 */       if (size > 0) {
/* 131 */         System.arraycopy(this.array, this.firstIndex, newArray, 0, size);
/* 132 */         this.firstIndex = 0;
/* 133 */         this.lastIndex = size;
/*     */       } 
/* 135 */       this.array = newArray;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void growAtFront(int required) {
/* 140 */     int size = this.lastIndex - this.firstIndex;
/* 141 */     if (this.array.length - this.lastIndex + this.firstIndex >= required) {
/* 142 */       int newFirst = this.array.length - size;
/* 143 */       if (size > 0) {
/* 144 */         System.arraycopy(this.array, this.firstIndex, this.array, newFirst, size);
/*     */       }
/* 146 */       this.firstIndex = newFirst;
/* 147 */       this.lastIndex = this.array.length;
/*     */     } else {
/* 149 */       int increment = size / 2;
/* 150 */       if (required > increment) {
/* 151 */         increment = required;
/*     */       }
/* 153 */       if (increment < 12) {
/* 154 */         increment = 12;
/*     */       }
/* 156 */       int[] newArray = new int[size + increment];
/* 157 */       if (size > 0) {
/* 158 */         System.arraycopy(this.array, this.firstIndex, newArray, newArray.length - size, size);
/*     */       }
/* 160 */       this.firstIndex = newArray.length - size;
/* 161 */       this.lastIndex = newArray.length;
/* 162 */       this.array = newArray;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void growForInsert(int location, int required) {
/* 167 */     int size = this.lastIndex - this.firstIndex;
/* 168 */     int increment = size / 2;
/* 169 */     if (required > increment) {
/* 170 */       increment = required;
/*     */     }
/* 172 */     if (increment < 12) {
/* 173 */       increment = 12;
/*     */     }
/* 175 */     int[] newArray = new int[size + increment];
/* 176 */     int newFirst = increment - required;
/*     */ 
/*     */     
/* 179 */     System.arraycopy(this.array, location + this.firstIndex, newArray, newFirst + location + required, size - location);
/*     */     
/* 181 */     System.arraycopy(this.array, this.firstIndex, newArray, newFirst, location);
/* 182 */     this.firstIndex = newFirst;
/* 183 */     this.lastIndex = size + increment;
/*     */     
/* 185 */     this.array = newArray;
/*     */   }
/*     */   
/*     */   public void increment(int location) {
/* 189 */     if (0 > location || location >= this.lastIndex - this.firstIndex) {
/* 190 */       throw new IndexOutOfBoundsException("" + location);
/*     */     }
/* 192 */     this.array[this.firstIndex + location] = this.array[this.firstIndex + location] + 1;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 196 */     return (this.lastIndex == this.firstIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public int remove(int location) {
/* 201 */     int result, size = this.lastIndex - this.firstIndex;
/* 202 */     if (0 > location || location >= size) {
/* 203 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 205 */     if (location == size - 1) {
/* 206 */       result = this.array[--this.lastIndex];
/* 207 */       this.array[this.lastIndex] = 0;
/* 208 */     } else if (location == 0) {
/* 209 */       result = this.array[this.firstIndex];
/* 210 */       this.array[this.firstIndex++] = 0;
/*     */     } else {
/* 212 */       int elementIndex = this.firstIndex + location;
/* 213 */       result = this.array[elementIndex];
/* 214 */       if (location < size / 2) {
/* 215 */         System.arraycopy(this.array, this.firstIndex, this.array, this.firstIndex + 1, location);
/* 216 */         this.array[this.firstIndex++] = 0;
/*     */       } else {
/* 218 */         System.arraycopy(this.array, elementIndex + 1, this.array, elementIndex, size - location - 1);
/* 219 */         this.array[--this.lastIndex] = 0;
/*     */       } 
/*     */     } 
/* 222 */     if (this.firstIndex == this.lastIndex) {
/* 223 */       this.firstIndex = this.lastIndex = 0;
/*     */     }
/*     */     
/* 226 */     this.modCount++;
/* 227 */     return result;
/*     */   }
/*     */   
/*     */   public int size() {
/* 231 */     return this.lastIndex - this.firstIndex;
/*     */   }
/*     */   
/*     */   public int[] toArray() {
/* 235 */     int size = this.lastIndex - this.firstIndex;
/* 236 */     int[] result = new int[size];
/* 237 */     System.arraycopy(this.array, this.firstIndex, result, 0, size);
/* 238 */     return result;
/*     */   }
/*     */   
/*     */   public void addAll(IntList list) {
/* 242 */     growAtEnd(list.size());
/* 243 */     for (int i = 0; i < list.size(); i++)
/* 244 */       add(list.get(i)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\IntList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */