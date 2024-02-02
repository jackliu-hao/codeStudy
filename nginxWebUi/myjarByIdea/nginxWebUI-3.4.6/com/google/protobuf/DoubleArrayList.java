package com.google.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class DoubleArrayList extends AbstractProtobufList<Double> implements Internal.DoubleList, RandomAccess, PrimitiveNonBoxingCollection {
   private static final DoubleArrayList EMPTY_LIST = new DoubleArrayList(new double[0], 0);
   private double[] array;
   private int size;

   public static DoubleArrayList emptyList() {
      return EMPTY_LIST;
   }

   DoubleArrayList() {
      this(new double[10], 0);
   }

   private DoubleArrayList(double[] other, int size) {
      this.array = other;
      this.size = size;
   }

   protected void removeRange(int fromIndex, int toIndex) {
      this.ensureIsMutable();
      if (toIndex < fromIndex) {
         throw new IndexOutOfBoundsException("toIndex < fromIndex");
      } else {
         System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
         this.size -= toIndex - fromIndex;
         ++this.modCount;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof DoubleArrayList)) {
         return super.equals(o);
      } else {
         DoubleArrayList other = (DoubleArrayList)o;
         if (this.size != other.size) {
            return false;
         } else {
            double[] arr = other.array;

            for(int i = 0; i < this.size; ++i) {
               if (Double.doubleToLongBits(this.array[i]) != Double.doubleToLongBits(arr[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int result = 1;

      for(int i = 0; i < this.size; ++i) {
         long bits = Double.doubleToLongBits(this.array[i]);
         result = 31 * result + Internal.hashLong(bits);
      }

      return result;
   }

   public Internal.DoubleList mutableCopyWithCapacity(int capacity) {
      if (capacity < this.size) {
         throw new IllegalArgumentException();
      } else {
         return new DoubleArrayList(Arrays.copyOf(this.array, capacity), this.size);
      }
   }

   public Double get(int index) {
      return this.getDouble(index);
   }

   public double getDouble(int index) {
      this.ensureIndexInRange(index);
      return this.array[index];
   }

   public int size() {
      return this.size;
   }

   public Double set(int index, Double element) {
      return this.setDouble(index, element);
   }

   public double setDouble(int index, double element) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      double previousValue = this.array[index];
      this.array[index] = element;
      return previousValue;
   }

   public boolean add(Double element) {
      this.addDouble(element);
      return true;
   }

   public void add(int index, Double element) {
      this.addDouble(index, element);
   }

   public void addDouble(double element) {
      this.ensureIsMutable();
      if (this.size == this.array.length) {
         int length = this.size * 3 / 2 + 1;
         double[] newArray = new double[length];
         System.arraycopy(this.array, 0, newArray, 0, this.size);
         this.array = newArray;
      }

      this.array[this.size++] = element;
   }

   private void addDouble(int index, double element) {
      this.ensureIsMutable();
      if (index >= 0 && index <= this.size) {
         if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
         } else {
            int length = this.size * 3 / 2 + 1;
            double[] newArray = new double[length];
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
         }

         this.array[index] = element;
         ++this.size;
         ++this.modCount;
      } else {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
      }
   }

   public boolean addAll(Collection<? extends Double> collection) {
      this.ensureIsMutable();
      Internal.checkNotNull(collection);
      if (!(collection instanceof DoubleArrayList)) {
         return super.addAll(collection);
      } else {
         DoubleArrayList list = (DoubleArrayList)collection;
         if (list.size == 0) {
            return false;
         } else {
            int overflow = Integer.MAX_VALUE - this.size;
            if (overflow < list.size) {
               throw new OutOfMemoryError();
            } else {
               int newSize = this.size + list.size;
               if (newSize > this.array.length) {
                  this.array = Arrays.copyOf(this.array, newSize);
               }

               System.arraycopy(list.array, 0, this.array, this.size, list.size);
               this.size = newSize;
               ++this.modCount;
               return true;
            }
         }
      }
   }

   public boolean remove(Object o) {
      this.ensureIsMutable();

      for(int i = 0; i < this.size; ++i) {
         if (o.equals(this.array[i])) {
            System.arraycopy(this.array, i + 1, this.array, i, this.size - i - 1);
            --this.size;
            ++this.modCount;
            return true;
         }
      }

      return false;
   }

   public Double remove(int index) {
      this.ensureIsMutable();
      this.ensureIndexInRange(index);
      double value = this.array[index];
      if (index < this.size - 1) {
         System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
      }

      --this.size;
      ++this.modCount;
      return value;
   }

   private void ensureIndexInRange(int index) {
      if (index < 0 || index >= this.size) {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
      }
   }

   private String makeOutOfBoundsExceptionMessage(int index) {
      return "Index:" + index + ", Size:" + this.size;
   }

   static {
      EMPTY_LIST.makeImmutable();
   }
}
