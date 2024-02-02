package org.antlr.v4.runtime.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class IntegerList {
   private static int[] EMPTY_DATA = new int[0];
   private static final int INITIAL_SIZE = 4;
   private static final int MAX_ARRAY_SIZE = 2147483639;
   private int[] _data;
   private int _size;

   public IntegerList() {
      this._data = EMPTY_DATA;
   }

   public IntegerList(int capacity) {
      if (capacity < 0) {
         throw new IllegalArgumentException();
      } else {
         if (capacity == 0) {
            this._data = EMPTY_DATA;
         } else {
            this._data = new int[capacity];
         }

      }
   }

   public IntegerList(IntegerList list) {
      this._data = (int[])list._data.clone();
      this._size = list._size;
   }

   public IntegerList(Collection<Integer> list) {
      this(list.size());
      Iterator i$ = list.iterator();

      while(i$.hasNext()) {
         Integer value = (Integer)i$.next();
         this.add(value);
      }

   }

   public final void add(int value) {
      if (this._data.length == this._size) {
         this.ensureCapacity(this._size + 1);
      }

      this._data[this._size] = value;
      ++this._size;
   }

   public final void addAll(int[] array) {
      this.ensureCapacity(this._size + array.length);
      System.arraycopy(array, 0, this._data, this._size, array.length);
      this._size += array.length;
   }

   public final void addAll(IntegerList list) {
      this.ensureCapacity(this._size + list._size);
      System.arraycopy(list._data, 0, this._data, this._size, list._size);
      this._size += list._size;
   }

   public final void addAll(Collection<Integer> list) {
      this.ensureCapacity(this._size + list.size());
      int current = 0;

      for(Iterator i$ = list.iterator(); i$.hasNext(); ++current) {
         int x = (Integer)i$.next();
         this._data[this._size + current] = x;
      }

      this._size += list.size();
   }

   public final int get(int index) {
      if (index >= 0 && index < this._size) {
         return this._data[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final boolean contains(int value) {
      for(int i = 0; i < this._size; ++i) {
         if (this._data[i] == value) {
            return true;
         }
      }

      return false;
   }

   public final int set(int index, int value) {
      if (index >= 0 && index < this._size) {
         int previous = this._data[index];
         this._data[index] = value;
         return previous;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int removeAt(int index) {
      int value = this.get(index);
      System.arraycopy(this._data, index + 1, this._data, index, this._size - index - 1);
      this._data[this._size - 1] = 0;
      --this._size;
      return value;
   }

   public final void removeRange(int fromIndex, int toIndex) {
      if (fromIndex >= 0 && toIndex >= 0 && fromIndex <= this._size && toIndex <= this._size) {
         if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
         } else {
            System.arraycopy(this._data, toIndex, this._data, fromIndex, this._size - toIndex);
            Arrays.fill(this._data, this._size - (toIndex - fromIndex), this._size, 0);
            this._size -= toIndex - fromIndex;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final boolean isEmpty() {
      return this._size == 0;
   }

   public final int size() {
      return this._size;
   }

   public final void trimToSize() {
      if (this._data.length != this._size) {
         this._data = Arrays.copyOf(this._data, this._size);
      }
   }

   public final void clear() {
      Arrays.fill(this._data, 0, this._size, 0);
      this._size = 0;
   }

   public final int[] toArray() {
      return this._size == 0 ? EMPTY_DATA : Arrays.copyOf(this._data, this._size);
   }

   public final void sort() {
      Arrays.sort(this._data, 0, this._size);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof IntegerList)) {
         return false;
      } else {
         IntegerList other = (IntegerList)o;
         if (this._size != other._size) {
            return false;
         } else {
            for(int i = 0; i < this._size; ++i) {
               if (this._data[i] != other._data[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hashCode = 1;

      for(int i = 0; i < this._size; ++i) {
         hashCode = 31 * hashCode + this._data[i];
      }

      return hashCode;
   }

   public String toString() {
      return Arrays.toString(this.toArray());
   }

   public final int binarySearch(int key) {
      return Arrays.binarySearch(this._data, 0, this._size, key);
   }

   public final int binarySearch(int fromIndex, int toIndex, int key) {
      if (fromIndex >= 0 && toIndex >= 0 && fromIndex <= this._size && toIndex <= this._size) {
         if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
         } else {
            return Arrays.binarySearch(this._data, fromIndex, toIndex, key);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void ensureCapacity(int capacity) {
      if (capacity >= 0 && capacity <= 2147483639) {
         int newLength;
         if (this._data.length == 0) {
            newLength = 4;
         } else {
            newLength = this._data.length;
         }

         while(true) {
            do {
               if (newLength >= capacity) {
                  this._data = Arrays.copyOf(this._data, newLength);
                  return;
               }

               newLength *= 2;
            } while(newLength >= 0 && newLength <= 2147483639);

            newLength = 2147483639;
         }
      } else {
         throw new OutOfMemoryError();
      }
   }
}
