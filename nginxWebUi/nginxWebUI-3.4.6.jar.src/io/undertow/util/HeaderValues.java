/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public final class HeaderValues
/*     */   extends AbstractCollection<String>
/*     */   implements Deque<String>, List<String>, RandomAccess
/*     */ {
/*  38 */   private static final String[] NO_STRINGS = new String[0];
/*     */   final HttpString key;
/*     */   byte size;
/*     */   Object value;
/*     */   
/*     */   HeaderValues(HttpString key) {
/*  44 */     this.key = key;
/*     */   }
/*     */   
/*     */   public HttpString getHeaderName() {
/*  48 */     return this.key;
/*     */   }
/*     */   
/*     */   public int size() {
/*  52 */     return this.size;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  56 */     return (this.size == 0);
/*     */   }
/*     */   
/*     */   public void clear() {
/*  60 */     byte size = this.size;
/*  61 */     if (size == 0)
/*  62 */       return;  clearInternal();
/*     */   }
/*     */   
/*     */   private void clearInternal() {
/*  66 */     Object value = this.value;
/*  67 */     if (value instanceof String[]) {
/*  68 */       String[] strings = (String[])value;
/*  69 */       int len = strings.length;
/*  70 */       Arrays.fill((Object[])strings, 0, len, (Object)null);
/*     */     } else {
/*  72 */       this.value = null;
/*     */     } 
/*  74 */     this.size = 0;
/*     */   }
/*     */   
/*     */   private int index(int idx) {
/*  78 */     assert idx >= 0;
/*  79 */     assert idx < this.size;
/*  80 */     int len = ((String[])this.value).length;
/*  81 */     if (idx > len) {
/*  82 */       idx -= len;
/*     */     }
/*  84 */     return idx;
/*     */   }
/*     */   
/*     */   public ListIterator<String> listIterator() {
/*  88 */     return iterator(0, true);
/*     */   }
/*     */   
/*     */   public ListIterator<String> listIterator(int index) {
/*  92 */     return iterator(index, true);
/*     */   }
/*     */   
/*     */   public Iterator<String> iterator() {
/*  96 */     return iterator(0, true);
/*     */   }
/*     */   
/*     */   public Iterator<String> descendingIterator() {
/* 100 */     return iterator(0, false);
/*     */   }
/*     */   
/*     */   private ListIterator<String> iterator(final int start, final boolean forwards) {
/* 104 */     return new ListIterator<String>() {
/* 105 */         int idx = start;
/* 106 */         int returned = -1;
/*     */         
/*     */         public boolean hasNext() {
/* 109 */           return (this.idx < HeaderValues.this.size);
/*     */         }
/*     */         
/*     */         public boolean hasPrevious() {
/* 113 */           return (this.idx > 0);
/*     */         }
/*     */ 
/*     */         
/*     */         public String next() {
/*     */           try {
/* 119 */             if (forwards) {
/* 120 */               int i = this.idx;
/* 121 */               String str = HeaderValues.this.get(i);
/* 122 */               this.returned = i;
/* 123 */               this.idx = i + 1;
/* 124 */               return str;
/*     */             } 
/* 126 */             int idx = this.idx - 1;
/* 127 */             String next = HeaderValues.this.get(idx);
/* 128 */             this.idx = this.returned = idx;
/*     */             
/* 130 */             return next;
/* 131 */           } catch (IndexOutOfBoundsException e) {
/* 132 */             throw new NoSuchElementException();
/*     */           } 
/*     */         }
/*     */         
/*     */         public int nextIndex() {
/* 137 */           return this.idx;
/*     */         }
/*     */         
/*     */         public String previous() {
/*     */           try {
/*     */             String prev;
/* 143 */             if (forwards) {
/* 144 */               int idx = this.idx - 1;
/* 145 */               prev = HeaderValues.this.get(idx);
/* 146 */               this.idx = this.returned = idx;
/*     */             } else {
/* 148 */               int idx = this.idx;
/* 149 */               prev = HeaderValues.this.get(idx);
/* 150 */               this.returned = idx;
/* 151 */               this.idx = idx + 1;
/* 152 */               return prev;
/*     */             } 
/* 154 */             return prev;
/* 155 */           } catch (IndexOutOfBoundsException e) {
/* 156 */             throw new NoSuchElementException();
/*     */           } 
/*     */         }
/*     */         
/*     */         public int previousIndex() {
/* 161 */           return this.idx - 1;
/*     */         }
/*     */         
/*     */         public void remove() {
/* 165 */           if (this.returned == -1) {
/* 166 */             throw new IllegalStateException();
/*     */           }
/* 168 */           HeaderValues.this.remove(this.returned);
/* 169 */           this.returned = -1;
/*     */         }
/*     */         
/*     */         public void set(String headerValue) {
/* 173 */           if (this.returned == -1) {
/* 174 */             throw new IllegalStateException();
/*     */           }
/* 176 */           HeaderValues.this.set(this.returned, headerValue);
/*     */         }
/*     */         
/*     */         public void add(String headerValue) {
/* 180 */           if (this.returned == -1) {
/* 181 */             throw new IllegalStateException();
/*     */           }
/* 183 */           int idx = this.idx;
/* 184 */           HeaderValues.this.add(idx, headerValue);
/* 185 */           this.idx = idx + 1;
/* 186 */           this.returned = -1;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public boolean offerFirst(String headerValue) {
/* 192 */     int size = this.size;
/* 193 */     if (headerValue == null || size == 127) return false; 
/* 194 */     Object value = this.value;
/* 195 */     if (value instanceof String[]) {
/* 196 */       String[] strings = (String[])value;
/* 197 */       int len = strings.length;
/* 198 */       if (size == len) {
/* 199 */         String[] newStrings = new String[len + 2];
/* 200 */         System.arraycopy(strings, 0, newStrings, 1, len);
/* 201 */         newStrings[0] = headerValue;
/* 202 */         this.value = newStrings;
/*     */       } else {
/* 204 */         System.arraycopy(strings, 0, strings, 1, strings.length - 1);
/* 205 */         strings[0] = headerValue;
/*     */       } 
/* 207 */       this.size = (byte)(size + 1);
/*     */     }
/* 209 */     else if (size == 0) {
/* 210 */       this.value = headerValue;
/* 211 */       this.size = 1;
/*     */     } else {
/* 213 */       this.value = new String[] { headerValue, (String)value, null, null };
/* 214 */       this.size = 2;
/*     */     } 
/*     */     
/* 217 */     return true;
/*     */   }
/*     */   
/*     */   public boolean offerLast(String headerValue) {
/* 221 */     int size = this.size;
/* 222 */     if (headerValue == null || size == 127) return false; 
/* 223 */     Object value = this.value;
/* 224 */     if (value instanceof String[]) {
/* 225 */       offerLastMultiValue(headerValue, size, (String[])value);
/*     */     }
/* 227 */     else if (size == 0) {
/* 228 */       this.value = headerValue;
/* 229 */       this.size = 1;
/*     */     } else {
/* 231 */       this.value = new String[] { (String)value, headerValue, null, null };
/* 232 */       this.size = 2;
/*     */     } 
/*     */     
/* 235 */     return true;
/*     */   }
/*     */   
/*     */   private void offerLastMultiValue(String headerValue, int size, String[] value) {
/* 239 */     String[] strings = value;
/* 240 */     int len = strings.length;
/* 241 */     if (size == len) {
/* 242 */       String[] newStrings = new String[len + 2];
/* 243 */       System.arraycopy(strings, 0, newStrings, 0, len);
/* 244 */       newStrings[len] = headerValue;
/* 245 */       this.value = newStrings;
/*     */     } else {
/* 247 */       strings[size] = headerValue;
/*     */     } 
/* 249 */     this.size = (byte)(size + 1);
/*     */   }
/*     */   
/*     */   private boolean offer(int idx, String headerValue) {
/* 253 */     int size = this.size;
/* 254 */     if (idx < 0 || idx > size || size == 127 || headerValue == null) return false; 
/* 255 */     if (idx == 0) return offerFirst(headerValue); 
/* 256 */     if (idx == size) return offerLast(headerValue); 
/* 257 */     assert size >= 2;
/* 258 */     Object value = this.value;
/* 259 */     assert value instanceof String[];
/* 260 */     String[] strings = (String[])value;
/* 261 */     int len = strings.length;
/*     */     
/* 263 */     if (size == len) {
/*     */       
/* 265 */       int newLen = len + 2;
/* 266 */       String[] newStrings = new String[newLen];
/* 267 */       System.arraycopy(value, 0, newStrings, 0, idx);
/* 268 */       System.arraycopy(value, idx, newStrings, idx + 1, len - idx);
/*     */ 
/*     */       
/* 271 */       newStrings[idx] = headerValue;
/* 272 */       this.value = newStrings;
/*     */     } else {
/* 274 */       System.arraycopy(value, idx, value, idx + 1, len - idx);
/*     */ 
/*     */       
/* 277 */       strings[idx] = headerValue;
/*     */     } 
/* 279 */     this.size = (byte)(size + 1);
/* 280 */     return true;
/*     */   }
/*     */   
/*     */   public String pollFirst() {
/* 284 */     byte size = this.size;
/* 285 */     if (size == 0) return null;
/*     */     
/* 287 */     Object value = this.value;
/* 288 */     if (value instanceof String) {
/* 289 */       this.size = 0;
/* 290 */       this.value = null;
/* 291 */       return (String)value;
/*     */     } 
/* 293 */     String[] strings = (String[])value;
/* 294 */     String ret = strings[0];
/* 295 */     System.arraycopy(strings, 1, strings, 0, strings.length - 1);
/* 296 */     this.size = (byte)(size - 1);
/* 297 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String pollLast() {
/* 302 */     byte size = this.size;
/* 303 */     if (size == 0) return null;
/*     */     
/* 305 */     Object value = this.value;
/* 306 */     if (value instanceof String) {
/* 307 */       this.size = 0;
/* 308 */       this.value = null;
/* 309 */       return (String)value;
/*     */     } 
/* 311 */     String[] strings = (String[])value;
/* 312 */     int idx = this.size = (byte)(size - 1);
/* 313 */     int len = strings.length;
/* 314 */     if (idx > len) idx -= len; 
/*     */     try {
/* 316 */       return strings[idx];
/*     */     } finally {
/* 318 */       strings[idx] = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String remove(int idx) {
/* 324 */     int size = this.size;
/* 325 */     if (idx < 0 || idx >= size) throw new IndexOutOfBoundsException(); 
/* 326 */     if (idx == 0) return removeFirst(); 
/* 327 */     if (idx == size - 1) return removeLast(); 
/* 328 */     assert size > 2;
/*     */     
/* 330 */     String[] value = (String[])this.value;
/* 331 */     int len = value.length;
/* 332 */     String ret = value[idx];
/* 333 */     System.arraycopy(value, idx + 1, value, idx, len - idx - 1);
/* 334 */     value[len - 1] = null;
/* 335 */     this.size = (byte)(size - 1);
/* 336 */     return ret;
/*     */   }
/*     */   
/*     */   public String get(int idx) {
/* 340 */     if (idx > this.size) {
/* 341 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 343 */     Object value = this.value;
/* 344 */     assert value != null;
/* 345 */     if (value instanceof String) {
/* 346 */       assert this.size == 1;
/* 347 */       return (String)value;
/*     */     } 
/* 349 */     String[] a = (String[])value;
/* 350 */     return a[index(idx)];
/*     */   }
/*     */   
/*     */   public int indexOf(Object o) {
/* 354 */     if (o == null || this.size == 0) return -1; 
/* 355 */     if (this.value instanceof String[]) {
/* 356 */       String[] list = (String[])this.value;
/* 357 */       int len = list.length;
/* 358 */       for (int i = 0; i < this.size; i++) {
/* 359 */         if (((i > len) ? list[i - len] : list[i]).equals(o)) {
/* 360 */           return i;
/*     */         }
/*     */       } 
/* 363 */     } else if (o.equals(this.value)) {
/* 364 */       return 0;
/*     */     } 
/* 366 */     return -1;
/*     */   }
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 370 */     if (o == null || this.size == 0) return -1; 
/* 371 */     if (this.value instanceof String[]) {
/* 372 */       String[] list = (String[])this.value;
/* 373 */       int len = list.length;
/*     */       
/* 375 */       for (int i = this.size - 1; i >= 0; i--) {
/* 376 */         int idx = i;
/* 377 */         if (((idx > len) ? list[idx - len] : list[idx]).equals(o)) {
/* 378 */           return i;
/*     */         }
/*     */       } 
/* 381 */     } else if (o.equals(this.value)) {
/* 382 */       return 0;
/*     */     } 
/* 384 */     return -1;
/*     */   }
/*     */   
/*     */   public String set(int index, String element) {
/* 388 */     if (element == null) throw new IllegalArgumentException();
/*     */     
/* 390 */     byte size = this.size;
/* 391 */     if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
/*     */     
/* 393 */     Object value = this.value;
/* 394 */     if (size == 1 && value instanceof String)
/* 395 */       try { return (String)value; }
/*     */       finally
/* 397 */       { this.value = element; }
/*     */        
/* 399 */     String[] list = (String[])value;
/* 400 */     int i = index(index);
/*     */     try {
/* 402 */       return list[i];
/*     */     } finally {
/* 404 */       list[i] = element;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends String> c) {
/* 410 */     int size = this.size;
/* 411 */     if (index < 0 || index > size) throw new IndexOutOfBoundsException(); 
/* 412 */     Iterator<? extends String> iterator = c.iterator();
/* 413 */     boolean result = false;
/* 414 */     while (iterator.hasNext()) {
/* 415 */       result |= offer(index, iterator.next());
/*     */     }
/* 417 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> subList(int fromIndex, int toIndex) {
/* 422 */     if (fromIndex < 0 || toIndex > this.size || fromIndex > toIndex) throw new IndexOutOfBoundsException(); 
/* 423 */     int len = toIndex - fromIndex;
/* 424 */     String[] strings = new String[len];
/* 425 */     for (int i = 0; i < len; i++) {
/* 426 */       strings[i] = get(i + fromIndex);
/*     */     }
/* 428 */     return Arrays.asList(strings);
/*     */   }
/*     */   
/*     */   public String[] toArray() {
/* 432 */     int size = this.size;
/* 433 */     if (size == 0) {
/* 434 */       return NO_STRINGS;
/*     */     }
/* 436 */     Object v = this.value;
/* 437 */     if (v instanceof String) return new String[] { (String)v }; 
/* 438 */     String[] list = (String[])v;
/* 439 */     int len = list.length;
/* 440 */     int copyEnd = size;
/* 441 */     if (copyEnd < len) {
/* 442 */       return Arrays.<String>copyOfRange(list, 0, copyEnd);
/*     */     }
/* 444 */     String[] ret = Arrays.<String>copyOfRange(list, 0, copyEnd);
/* 445 */     System.arraycopy(list, 0, ret, len, copyEnd - len);
/* 446 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/* 451 */     int size = this.size;
/* 452 */     if (size == 0) return a; 
/* 453 */     int inLen = a.length;
/* 454 */     T[] arrayOfT = (inLen < size) ? Arrays.<T>copyOfRange(a, inLen, inLen + size) : a;
/* 455 */     Object v = this.value;
/* 456 */     if (v instanceof String) {
/* 457 */       arrayOfT[0] = (T)v;
/*     */     } else {
/* 459 */       System.arraycopy(v, 0, arrayOfT, 0, size);
/*     */     } 
/* 461 */     return arrayOfT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFirst(String s) {
/* 471 */     if (s == null)
/* 472 */       return;  if (!offerFirst(s)) throw new IllegalStateException(); 
/*     */   }
/*     */   
/*     */   public void addLast(String s) {
/* 476 */     if (s == null)
/* 477 */       return;  if (!offerLast(s)) throw new IllegalStateException(); 
/*     */   }
/*     */   
/*     */   public void add(int index, String s) {
/* 481 */     if (s == null)
/* 482 */       return;  if (!offer(index, s)) throw new IllegalStateException(); 
/*     */   }
/*     */   
/*     */   public boolean contains(Object o) {
/* 486 */     return (indexOf(o) != -1);
/*     */   }
/*     */   
/*     */   public String peekFirst() {
/* 490 */     return (this.size == 0) ? null : get(0);
/*     */   }
/*     */   
/*     */   public String peekLast() {
/* 494 */     return (this.size == 0) ? null : get(this.size - 1);
/*     */   }
/*     */   
/*     */   public boolean removeFirstOccurrence(Object o) {
/* 498 */     int i = indexOf(o);
/* 499 */     return (i != -1 && remove(i) != null);
/*     */   }
/*     */   
/*     */   public boolean removeLastOccurrence(Object o) {
/* 503 */     int i = lastIndexOf(o);
/* 504 */     return (i != -1 && remove(i) != null);
/*     */   }
/*     */   
/*     */   public boolean add(String s) {
/* 508 */     addLast(s);
/* 509 */     return true;
/*     */   }
/*     */   
/*     */   public void push(String s) {
/* 513 */     addFirst(s);
/*     */   }
/*     */   
/*     */   public String pop() {
/* 517 */     return removeFirst();
/*     */   }
/*     */   
/*     */   public boolean offer(String s) {
/* 521 */     return offerLast(s);
/*     */   }
/*     */   
/*     */   public String poll() {
/* 525 */     return pollFirst();
/*     */   }
/*     */   
/*     */   public String peek() {
/* 529 */     return peekFirst();
/*     */   }
/*     */   
/*     */   public String remove() {
/* 533 */     return removeFirst();
/*     */   }
/*     */   
/*     */   public String removeFirst() {
/* 537 */     String s = pollFirst();
/* 538 */     if (s == null) {
/* 539 */       throw new NoSuchElementException();
/*     */     }
/* 541 */     return s;
/*     */   }
/*     */   
/*     */   public String removeLast() {
/* 545 */     String s = pollLast();
/* 546 */     if (s == null) {
/* 547 */       throw new NoSuchElementException();
/*     */     }
/* 549 */     return s;
/*     */   }
/*     */   
/*     */   public String getFirst() {
/* 553 */     String s = peekFirst();
/* 554 */     if (s == null) {
/* 555 */       throw new NoSuchElementException();
/*     */     }
/* 557 */     return s;
/*     */   }
/*     */   
/*     */   public String getLast() {
/* 561 */     String s = peekLast();
/* 562 */     if (s == null) {
/* 563 */       throw new NoSuchElementException();
/*     */     }
/* 565 */     return s;
/*     */   }
/*     */   
/*     */   public String element() {
/* 569 */     return getFirst();
/*     */   }
/*     */   
/*     */   public boolean remove(Object obj) {
/* 573 */     return removeFirstOccurrence(obj);
/*     */   }
/*     */   
/*     */   public boolean addAll(Collection<? extends String> c) {
/* 577 */     for (String s : c) {
/* 578 */       add(s);
/*     */     }
/* 580 */     return !c.isEmpty();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\HeaderValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */