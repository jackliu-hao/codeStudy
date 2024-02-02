/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
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
/*     */ public class Array2DHashSet<T>
/*     */   implements Set<T>
/*     */ {
/*     */   public static final int INITAL_CAPACITY = 16;
/*     */   public static final int INITAL_BUCKET_CAPACITY = 8;
/*     */   public static final double LOAD_FACTOR = 0.75D;
/*     */   protected final AbstractEqualityComparator<? super T> comparator;
/*     */   protected T[][] buckets;
/*  51 */   protected int n = 0;
/*     */   
/*  53 */   protected int threshold = 12;
/*     */   
/*  55 */   protected int currentPrime = 1;
/*  56 */   protected int initialBucketCapacity = 8;
/*     */   
/*     */   public Array2DHashSet() {
/*  59 */     this(null, 16, 8);
/*     */   }
/*     */   
/*     */   public Array2DHashSet(AbstractEqualityComparator<? super T> comparator) {
/*  63 */     this(comparator, 16, 8);
/*     */   }
/*     */   
/*     */   public Array2DHashSet(AbstractEqualityComparator<? super T> comparator, int initialCapacity, int initialBucketCapacity) {
/*  67 */     if (comparator == null) {
/*  68 */       comparator = ObjectEqualityComparator.INSTANCE;
/*     */     }
/*     */     
/*  71 */     this.comparator = comparator;
/*  72 */     this.buckets = createBuckets(initialCapacity);
/*  73 */     this.initialBucketCapacity = initialBucketCapacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T getOrAdd(T o) {
/*  82 */     if (this.n > this.threshold) expand(); 
/*  83 */     return getOrAddImpl(o);
/*     */   }
/*     */   
/*     */   protected T getOrAddImpl(T o) {
/*  87 */     int b = getBucket(o);
/*  88 */     T[] bucket = this.buckets[b];
/*     */ 
/*     */     
/*  91 */     if (bucket == null) {
/*  92 */       bucket = createBucket(this.initialBucketCapacity);
/*  93 */       bucket[0] = o;
/*  94 */       this.buckets[b] = bucket;
/*  95 */       this.n++;
/*  96 */       return o;
/*     */     } 
/*     */ 
/*     */     
/* 100 */     for (int i = 0; i < bucket.length; i++) {
/* 101 */       T existing = bucket[i];
/* 102 */       if (existing == null) {
/* 103 */         bucket[i] = o;
/* 104 */         this.n++;
/* 105 */         return o;
/*     */       } 
/* 107 */       if (this.comparator.equals(existing, o)) return existing;
/*     */     
/*     */     } 
/*     */     
/* 111 */     int oldLength = bucket.length;
/* 112 */     bucket = Arrays.copyOf(bucket, bucket.length * 2);
/* 113 */     this.buckets[b] = bucket;
/* 114 */     bucket[oldLength] = o;
/* 115 */     this.n++;
/* 116 */     return o;
/*     */   }
/*     */   
/*     */   public T get(T o) {
/* 120 */     if (o == null) return o; 
/* 121 */     int b = getBucket(o);
/* 122 */     T[] bucket = this.buckets[b];
/* 123 */     if (bucket == null) return null; 
/* 124 */     for (T e : bucket) {
/* 125 */       if (e == null) return null; 
/* 126 */       if (this.comparator.equals(e, o)) return e; 
/*     */     } 
/* 128 */     return null;
/*     */   }
/*     */   
/*     */   protected final int getBucket(T o) {
/* 132 */     int hash = this.comparator.hashCode(o);
/* 133 */     int b = hash & this.buckets.length - 1;
/* 134 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 139 */     int hash = MurmurHash.initialize();
/* 140 */     for (T[] bucket : this.buckets) {
/* 141 */       if (bucket != null) {
/* 142 */         for (T o : bucket) {
/* 143 */           if (o == null)
/* 144 */             break;  hash = MurmurHash.update(hash, this.comparator.hashCode(o));
/*     */         } 
/*     */       }
/*     */     } 
/* 148 */     hash = MurmurHash.finish(hash, size());
/* 149 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 154 */     if (o == this) return true; 
/* 155 */     if (!(o instanceof Array2DHashSet)) return false; 
/* 156 */     Array2DHashSet<?> other = (Array2DHashSet)o;
/* 157 */     if (other.size() != size()) return false; 
/* 158 */     boolean same = containsAll(other);
/* 159 */     return same;
/*     */   }
/*     */   
/*     */   protected void expand() {
/* 163 */     T[][] old = this.buckets;
/* 164 */     this.currentPrime += 4;
/* 165 */     int newCapacity = this.buckets.length * 2;
/* 166 */     T[][] newTable = createBuckets(newCapacity);
/* 167 */     int[] newBucketLengths = new int[newTable.length];
/* 168 */     this.buckets = newTable;
/* 169 */     this.threshold = (int)(newCapacity * 0.75D);
/*     */ 
/*     */     
/* 172 */     int oldSize = size();
/* 173 */     for (T[] bucket : old) {
/* 174 */       if (bucket != null)
/*     */       {
/*     */ 
/*     */         
/* 178 */         for (T o : bucket) {
/* 179 */           T[] newBucket; if (o == null) {
/*     */             break;
/*     */           }
/*     */           
/* 183 */           int b = getBucket(o);
/* 184 */           int bucketLength = newBucketLengths[b];
/*     */           
/* 186 */           if (bucketLength == 0) {
/*     */             
/* 188 */             newBucket = createBucket(this.initialBucketCapacity);
/* 189 */             newTable[b] = newBucket;
/*     */           } else {
/*     */             
/* 192 */             newBucket = newTable[b];
/* 193 */             if (bucketLength == newBucket.length) {
/*     */               
/* 195 */               newBucket = Arrays.copyOf(newBucket, newBucket.length * 2);
/* 196 */               newTable[b] = newBucket;
/*     */             } 
/*     */           } 
/*     */           
/* 200 */           newBucket[bucketLength] = o;
/* 201 */           newBucketLengths[b] = newBucketLengths[b] + 1;
/*     */         } 
/*     */       }
/*     */     } 
/* 205 */     assert this.n == oldSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean add(T t) {
/* 210 */     T existing = getOrAdd(t);
/* 211 */     return (existing == t);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int size() {
/* 216 */     return this.n;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isEmpty() {
/* 221 */     return (this.n == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean contains(Object o) {
/* 226 */     return containsFast(asElementType(o));
/*     */   }
/*     */   
/*     */   public boolean containsFast(T obj) {
/* 230 */     if (obj == null) {
/* 231 */       return false;
/*     */     }
/*     */     
/* 234 */     return (get(obj) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/* 239 */     return new SetIterator(toArray());
/*     */   }
/*     */ 
/*     */   
/*     */   public T[] toArray() {
/* 244 */     T[] a = createBucket(size());
/* 245 */     int i = 0;
/* 246 */     for (T[] bucket : this.buckets) {
/* 247 */       if (bucket != null)
/*     */       {
/*     */ 
/*     */         
/* 251 */         for (T o : bucket) {
/* 252 */           if (o == null) {
/*     */             break;
/*     */           }
/*     */           
/* 256 */           a[i++] = o;
/*     */         } 
/*     */       }
/*     */     } 
/* 260 */     return a;
/*     */   }
/*     */ 
/*     */   
/*     */   public <U> U[] toArray(U[] a) {
/* 265 */     if (a.length < size()) {
/* 266 */       a = Arrays.copyOf(a, size());
/*     */     }
/*     */     
/* 269 */     int i = 0;
/* 270 */     for (T[] bucket : this.buckets) {
/* 271 */       if (bucket != null)
/*     */       {
/*     */ 
/*     */         
/* 275 */         for (T o : bucket) {
/* 276 */           if (o == null) {
/*     */             break;
/*     */           }
/*     */ 
/*     */           
/* 281 */           T t1 = o;
/* 282 */           a[i++] = (U)t1;
/*     */         }  } 
/*     */     } 
/* 285 */     return a;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean remove(Object o) {
/* 290 */     return removeFast(asElementType(o));
/*     */   }
/*     */   
/*     */   public boolean removeFast(T obj) {
/* 294 */     if (obj == null) {
/* 295 */       return false;
/*     */     }
/*     */     
/* 298 */     int b = getBucket(obj);
/* 299 */     T[] bucket = this.buckets[b];
/* 300 */     if (bucket == null)
/*     */     {
/* 302 */       return false;
/*     */     }
/*     */     
/* 305 */     for (int i = 0; i < bucket.length; i++) {
/* 306 */       T e = bucket[i];
/* 307 */       if (e == null)
/*     */       {
/* 309 */         return false;
/*     */       }
/*     */       
/* 312 */       if (this.comparator.equals(e, obj)) {
/*     */         
/* 314 */         System.arraycopy(bucket, i + 1, bucket, i, bucket.length - i - 1);
/* 315 */         bucket[bucket.length - 1] = null;
/* 316 */         this.n--;
/* 317 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 321 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> collection) {
/* 326 */     if (collection instanceof Array2DHashSet) {
/* 327 */       Array2DHashSet<?> s = (Array2DHashSet)collection;
/* 328 */       for (T[] arrayOfT : s.buckets) {
/* 329 */         if (arrayOfT != null)
/* 330 */           for (T o : arrayOfT) {
/* 331 */             if (o == null)
/* 332 */               break;  if (!containsFast(asElementType(o))) return false;
/*     */           
/*     */           }  
/*     */       } 
/*     */     } else {
/* 337 */       for (Object o : collection) {
/* 338 */         if (!containsFast(asElementType(o))) return false; 
/*     */       } 
/*     */     } 
/* 341 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends T> c) {
/* 346 */     boolean changed = false;
/* 347 */     for (T o : c) {
/* 348 */       T existing = getOrAdd(o);
/* 349 */       if (existing != o) changed = true; 
/*     */     } 
/* 351 */     return changed;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 356 */     int newsize = 0;
/* 357 */     for (T[] bucket : this.buckets) {
/* 358 */       if (bucket != null) {
/*     */         int i;
/*     */ 
/*     */         
/*     */         int j;
/*     */         
/* 364 */         for (i = 0, j = 0; i < bucket.length && 
/* 365 */           bucket[i] != null; i++) {
/*     */ 
/*     */ 
/*     */           
/* 369 */           if (c.contains(bucket[i])) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 375 */             if (i != j) {
/* 376 */               bucket[j] = bucket[i];
/*     */             }
/*     */             
/* 379 */             j++;
/* 380 */             newsize++;
/*     */           } 
/*     */         } 
/* 383 */         newsize += j;
/*     */         
/* 385 */         while (j < i) {
/* 386 */           bucket[j] = null;
/* 387 */           j++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 391 */     boolean changed = (newsize != this.n);
/* 392 */     this.n = newsize;
/* 393 */     return changed;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 398 */     boolean changed = false;
/* 399 */     for (Object o : c) {
/* 400 */       changed |= removeFast(asElementType(o));
/*     */     }
/*     */     
/* 403 */     return changed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 408 */     this.buckets = createBuckets(16);
/* 409 */     this.n = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 414 */     if (size() == 0) return "{}";
/*     */     
/* 416 */     StringBuilder buf = new StringBuilder();
/* 417 */     buf.append('{');
/* 418 */     boolean first = true;
/* 419 */     for (T[] bucket : this.buckets) {
/* 420 */       if (bucket != null)
/* 421 */         for (T o : bucket) {
/* 422 */           if (o == null)
/* 423 */             break;  if (first) { first = false; }
/* 424 */           else { buf.append(", "); }
/* 425 */            buf.append(o.toString());
/*     */         }  
/*     */     } 
/* 428 */     buf.append('}');
/* 429 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toTableString() {
/* 433 */     StringBuilder buf = new StringBuilder();
/* 434 */     for (T[] bucket : this.buckets) {
/* 435 */       if (bucket == null) {
/* 436 */         buf.append("null\n");
/*     */       } else {
/*     */         
/* 439 */         buf.append('[');
/* 440 */         boolean first = true;
/* 441 */         for (T o : bucket) {
/* 442 */           if (first) { first = false; }
/* 443 */           else { buf.append(" "); }
/* 444 */            if (o == null) { buf.append("_"); }
/* 445 */           else { buf.append(o.toString()); }
/*     */         
/* 447 */         }  buf.append("]\n");
/*     */       } 
/* 449 */     }  return buf.toString();
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
/*     */   protected T asElementType(Object o) {
/* 467 */     return (T)o;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T[][] createBuckets(int capacity) {
/* 478 */     return (T[][])new Object[capacity][];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T[] createBucket(int capacity) {
/* 489 */     return (T[])new Object[capacity];
/*     */   }
/*     */   
/*     */   protected class SetIterator implements Iterator<T> {
/*     */     final T[] data;
/* 494 */     int nextIndex = 0;
/*     */     boolean removed = true;
/*     */     
/*     */     public SetIterator(T[] data) {
/* 498 */       this.data = data;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 503 */       return (this.nextIndex < this.data.length);
/*     */     }
/*     */ 
/*     */     
/*     */     public T next() {
/* 508 */       if (!hasNext()) {
/* 509 */         throw new NoSuchElementException();
/*     */       }
/*     */       
/* 512 */       this.removed = false;
/* 513 */       return this.data[this.nextIndex++];
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 518 */       if (this.removed) {
/* 519 */         throw new IllegalStateException();
/*     */       }
/*     */       
/* 522 */       Array2DHashSet.this.remove(this.data[this.nextIndex - 1]);
/* 523 */       this.removed = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\Array2DHashSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */