package org.antlr.v4.runtime.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class Array2DHashSet<T> implements Set<T> {
   public static final int INITAL_CAPACITY = 16;
   public static final int INITAL_BUCKET_CAPACITY = 8;
   public static final double LOAD_FACTOR = 0.75;
   protected final AbstractEqualityComparator<? super T> comparator;
   protected T[][] buckets;
   protected int n;
   protected int threshold;
   protected int currentPrime;
   protected int initialBucketCapacity;

   public Array2DHashSet() {
      this((AbstractEqualityComparator)null, 16, 8);
   }

   public Array2DHashSet(AbstractEqualityComparator<? super T> comparator) {
      this(comparator, 16, 8);
   }

   public Array2DHashSet(AbstractEqualityComparator<? super T> comparator, int initialCapacity, int initialBucketCapacity) {
      this.n = 0;
      this.threshold = 12;
      this.currentPrime = 1;
      this.initialBucketCapacity = 8;
      if (comparator == null) {
         comparator = ObjectEqualityComparator.INSTANCE;
      }

      this.comparator = (AbstractEqualityComparator)comparator;
      this.buckets = this.createBuckets(initialCapacity);
      this.initialBucketCapacity = initialBucketCapacity;
   }

   public final T getOrAdd(T o) {
      if (this.n > this.threshold) {
         this.expand();
      }

      return this.getOrAddImpl(o);
   }

   protected T getOrAddImpl(T o) {
      int b = this.getBucket(o);
      T[] bucket = this.buckets[b];
      if (bucket == null) {
         bucket = this.createBucket(this.initialBucketCapacity);
         bucket[0] = o;
         this.buckets[b] = bucket;
         ++this.n;
         return o;
      } else {
         int i;
         for(i = 0; i < bucket.length; ++i) {
            T existing = bucket[i];
            if (existing == null) {
               bucket[i] = o;
               ++this.n;
               return o;
            }

            if (this.comparator.equals(existing, o)) {
               return existing;
            }
         }

         i = bucket.length;
         bucket = Arrays.copyOf(bucket, bucket.length * 2);
         this.buckets[b] = bucket;
         bucket[i] = o;
         ++this.n;
         return o;
      }
   }

   public T get(T o) {
      if (o == null) {
         return o;
      } else {
         int b = this.getBucket(o);
         T[] bucket = this.buckets[b];
         if (bucket == null) {
            return null;
         } else {
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T e = arr$[i$];
               if (e == null) {
                  return null;
               }

               if (this.comparator.equals(e, o)) {
                  return e;
               }
            }

            return null;
         }
      }
   }

   protected final int getBucket(T o) {
      int hash = this.comparator.hashCode(o);
      int b = hash & this.buckets.length - 1;
      return b;
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      Object[][] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket != null) {
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T o = arr$[i$];
               if (o == null) {
                  break;
               }

               hash = MurmurHash.update(hash, this.comparator.hashCode(o));
            }
         }
      }

      hash = MurmurHash.finish(hash, this.size());
      return hash;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Array2DHashSet)) {
         return false;
      } else {
         Array2DHashSet<?> other = (Array2DHashSet)o;
         if (other.size() != this.size()) {
            return false;
         } else {
            boolean same = this.containsAll(other);
            return same;
         }
      }
   }

   protected void expand() {
      T[][] old = this.buckets;
      this.currentPrime += 4;
      int newCapacity = this.buckets.length * 2;
      T[][] newTable = this.createBuckets(newCapacity);
      int[] newBucketLengths = new int[newTable.length];
      this.buckets = newTable;
      this.threshold = (int)((double)newCapacity * 0.75);
      int oldSize = this.size();
      Object[][] arr$ = old;
      int len$ = old.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket != null) {
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T o = arr$[i$];
               if (o == null) {
                  break;
               }

               int b = this.getBucket(o);
               int bucketLength = newBucketLengths[b];
               Object[] newBucket;
               if (bucketLength == 0) {
                  newBucket = this.createBucket(this.initialBucketCapacity);
                  newTable[b] = newBucket;
               } else {
                  newBucket = newTable[b];
                  if (bucketLength == newBucket.length) {
                     newBucket = Arrays.copyOf(newBucket, newBucket.length * 2);
                     newTable[b] = newBucket;
                  }
               }

               newBucket[bucketLength] = o;
               int var10002 = newBucketLengths[b]++;
            }
         }
      }

      assert this.n == oldSize;

   }

   public final boolean add(T t) {
      T existing = this.getOrAdd(t);
      return existing == t;
   }

   public final int size() {
      return this.n;
   }

   public final boolean isEmpty() {
      return this.n == 0;
   }

   public final boolean contains(Object o) {
      return this.containsFast(this.asElementType(o));
   }

   public boolean containsFast(T obj) {
      if (obj == null) {
         return false;
      } else {
         return this.get(obj) != null;
      }
   }

   public Iterator<T> iterator() {
      return new SetIterator(this.toArray());
   }

   public T[] toArray() {
      T[] a = this.createBucket(this.size());
      int i = 0;
      Object[][] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket != null) {
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T o = arr$[i$];
               if (o == null) {
                  break;
               }

               a[i++] = o;
            }
         }
      }

      return a;
   }

   public <U> U[] toArray(U[] a) {
      if (a.length < this.size()) {
         a = Arrays.copyOf(a, this.size());
      }

      int i = 0;
      Object[][] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket != null) {
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T o = arr$[i$];
               if (o == null) {
                  break;
               }

               a[i++] = o;
            }
         }
      }

      return a;
   }

   public final boolean remove(Object o) {
      return this.removeFast(this.asElementType(o));
   }

   public boolean removeFast(T obj) {
      if (obj == null) {
         return false;
      } else {
         int b = this.getBucket(obj);
         T[] bucket = this.buckets[b];
         if (bucket == null) {
            return false;
         } else {
            for(int i = 0; i < bucket.length; ++i) {
               T e = bucket[i];
               if (e == null) {
                  return false;
               }

               if (this.comparator.equals(e, obj)) {
                  System.arraycopy(bucket, i + 1, bucket, i, bucket.length - i - 1);
                  bucket[bucket.length - 1] = null;
                  --this.n;
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsAll(Collection<?> collection) {
      if (collection instanceof Array2DHashSet) {
         Array2DHashSet<?> s = (Array2DHashSet)collection;
         Object[][] arr$ = s.buckets;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object[] bucket = arr$[i$];
            if (bucket != null) {
               Object[] arr$ = bucket;
               int len$ = bucket.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Object o = arr$[i$];
                  if (o == null) {
                     break;
                  }

                  if (!this.containsFast(this.asElementType(o))) {
                     return false;
                  }
               }
            }
         }
      } else {
         Iterator i$ = collection.iterator();

         while(i$.hasNext()) {
            Object o = i$.next();
            if (!this.containsFast(this.asElementType(o))) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean addAll(Collection<? extends T> c) {
      boolean changed = false;
      Iterator i$ = c.iterator();

      while(i$.hasNext()) {
         T o = i$.next();
         T existing = this.getOrAdd(o);
         if (existing != o) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean retainAll(Collection<?> c) {
      int newsize = 0;
      Object[][] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket != null) {
            int i = 0;

            int j;
            for(j = 0; i < bucket.length && bucket[i] != null; ++i) {
               if (c.contains(bucket[i])) {
                  if (i != j) {
                     bucket[j] = bucket[i];
                  }

                  ++j;
                  ++newsize;
               }
            }

            for(newsize += j; j < i; ++j) {
               bucket[j] = null;
            }
         }
      }

      boolean changed = newsize != this.n;
      this.n = newsize;
      return changed;
   }

   public boolean removeAll(Collection<?> c) {
      boolean changed = false;

      Object o;
      for(Iterator i$ = c.iterator(); i$.hasNext(); changed |= this.removeFast(this.asElementType(o))) {
         o = i$.next();
      }

      return changed;
   }

   public void clear() {
      this.buckets = this.createBuckets(16);
      this.n = 0;
   }

   public String toString() {
      if (this.size() == 0) {
         return "{}";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append('{');
         boolean first = true;
         Object[][] arr$ = this.buckets;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            T[] bucket = arr$[i$];
            if (bucket != null) {
               Object[] arr$ = bucket;
               int len$ = bucket.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  T o = arr$[i$];
                  if (o == null) {
                     break;
                  }

                  if (first) {
                     first = false;
                  } else {
                     buf.append(", ");
                  }

                  buf.append(o.toString());
               }
            }
         }

         buf.append('}');
         return buf.toString();
      }
   }

   public String toTableString() {
      StringBuilder buf = new StringBuilder();
      Object[][] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T[] bucket = arr$[i$];
         if (bucket == null) {
            buf.append("null\n");
         } else {
            buf.append('[');
            boolean first = true;
            Object[] arr$ = bucket;
            int len$ = bucket.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               T o = arr$[i$];
               if (first) {
                  first = false;
               } else {
                  buf.append(" ");
               }

               if (o == null) {
                  buf.append("_");
               } else {
                  buf.append(o.toString());
               }
            }

            buf.append("]\n");
         }
      }

      return buf.toString();
   }

   protected T asElementType(Object o) {
      return o;
   }

   protected T[][] createBuckets(int capacity) {
      return (Object[][])(new Object[capacity][]);
   }

   protected T[] createBucket(int capacity) {
      return (Object[])(new Object[capacity]);
   }

   protected class SetIterator implements Iterator<T> {
      final T[] data;
      int nextIndex = 0;
      boolean removed = true;

      public SetIterator(T[] data) {
         this.data = data;
      }

      public boolean hasNext() {
         return this.nextIndex < this.data.length;
      }

      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.removed = false;
            return this.data[this.nextIndex++];
         }
      }

      public void remove() {
         if (this.removed) {
            throw new IllegalStateException();
         } else {
            Array2DHashSet.this.remove(this.data[this.nextIndex - 1]);
            this.removed = true;
         }
      }
   }
}
