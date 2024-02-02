package org.wildfly.common.flags;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;
import org.wildfly.common.Assert;

public abstract class Flags<E extends Enum<E>, This extends Flags<E, This>> extends AbstractSet<E> implements SortedSet<E> {
   final int bits;

   protected Flags(int bits) {
      this.bits = bits;
   }

   protected abstract This value(int var1);

   protected abstract This this_();

   protected abstract E itemOf(int var1);

   protected abstract E castItemOrNull(Object var1);

   protected abstract This castThis(Object var1);

   public final int size() {
      return Integer.bitCount(this.bits);
   }

   public final E first() {
      int bits = this.bits;
      if (bits == 0) {
         throw new NoSuchElementException();
      } else {
         return this.itemOf(Integer.numberOfTrailingZeros(Integer.lowestOneBit(bits)));
      }
   }

   public final E last() {
      int bits = this.bits;
      if (bits == 0) {
         throw new NoSuchElementException();
      } else {
         return this.itemOf(Integer.numberOfTrailingZeros(Integer.highestOneBit(bits)));
      }
   }

   public final Comparator<? super E> comparator() {
      return null;
   }

   public boolean isEmpty() {
      return this.bits == 0;
   }

   public final This headSet(E toElement) {
      Assert.checkNotNullParam("toElement", toElement);
      return this.value(this.bits & bitOf(toElement) - 1);
   }

   public final This tailSet(E fromElement) {
      Assert.checkNotNullParam("fromElement", fromElement);
      return this.value(this.bits & ~(bitOf(fromElement) - 1));
   }

   public final This subSet(E fromElement, E toElement) {
      Assert.checkNotNullParam("fromElement", fromElement);
      Assert.checkNotNullParam("toElement", toElement);
      return this.value(this.bits & bitOf(toElement) - 1 & ~(bitOf(fromElement) - 1));
   }

   public final Object[] toArray() {
      int b = this.bits;
      Object[] array = new Object[Integer.bitCount(b)];

      int lob;
      for(int idx = 0; Integer.bitCount(b) > 0; b ^= lob) {
         lob = Integer.lowestOneBit(b);
         array[idx + 1] = this.itemOf(Integer.numberOfTrailingZeros(lob));
      }

      return array;
   }

   public final <T> T[] toArray(T[] array) {
      int b = this.bits;
      int size = Integer.bitCount(b);
      if (size > array.length) {
         array = Arrays.copyOf(array, size);
      }

      int lob;
      for(int idx = 0; Integer.bitCount(b) > 0; b ^= lob) {
         lob = Integer.lowestOneBit(b);
         array[idx + 1] = this.itemOf(Integer.numberOfTrailingZeros(lob));
      }

      return array;
   }

   public final boolean contains(E flag) {
      return flag != null && (this.bits & bitOf(flag)) != 0;
   }

   public final boolean contains(Object o) {
      return this.contains(this.castItemOrNull(o));
   }

   public final boolean containsAll(Collection<?> c) {
      if (c.getClass() == this.getClass()) {
         return this.containsAll(this.castThis(c));
      } else {
         Iterator var2 = c.iterator();

         Object o;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            o = var2.next();
         } while(this.contains(o));

         return false;
      }
   }

   public final boolean containsAll(This other) {
      int otherBits = other.bits;
      return (this.bits & otherBits) == otherBits;
   }

   public final boolean containsAll(E flag1, E flag2) {
      return this.contains(flag1) && this.contains(flag2);
   }

   public final boolean containsAll(E flag1, E flag2, E flag3) {
      return this.containsAll(flag1, flag2) && this.contains(flag3);
   }

   public final boolean containsAny(This other) {
      return other != null && (this.bits & other.bits) != 0;
   }

   public final boolean containsAny(E flag1, E flag2) {
      return this.contains(flag1) || this.contains(flag2);
   }

   public final boolean containsAny(E flag1, E flag2, E flag3) {
      return this.containsAny(flag1, flag2) || this.contains(flag3);
   }

   public final This complement() {
      return this.value(~this.bits);
   }

   public final This with(E flag) {
      return flag == null ? this.this_() : this.value(this.bits | bitOf(flag));
   }

   public final This with(E flag1, E flag2) {
      return this.with(flag1).with(flag2);
   }

   public final This with(E flag1, E flag2, E flag3) {
      return this.with(flag1, flag2).with(flag3);
   }

   @SafeVarargs
   public final This with(E... flags) {
      if (flags == null) {
         return this.this_();
      } else {
         int b = this.bits;
         Enum[] var3 = flags;
         int var4 = flags.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            E flag = var3[var5];
            if (flag != null) {
               b |= bitOf(flag);
            }
         }

         return this.value(b);
      }
   }

   public final This with(This other) {
      return other == null ? this.this_() : this.value(this.bits | other.bits);
   }

   public final This without(E flag) {
      return flag == null ? this.this_() : this.value(this.bits & ~bitOf(flag));
   }

   public final This without(This other) {
      return other == null ? this.this_() : this.value(this.bits & ~other.bits);
   }

   public final boolean equals(Object o) {
      return o == this || o instanceof Set && this.equals((Set)o);
   }

   public final boolean equals(Set<?> o) {
      return o == this || o.containsAll(this) && this.containsAll((Collection)o);
   }

   public final boolean equals(This o) {
      return o == this;
   }

   public final int hashCode() {
      int hc = 0;

      int lob;
      for(int b = this.bits; b != 0; b ^= lob) {
         lob = Integer.lowestOneBit(b);
         hc += this.itemOf(Integer.numberOfTrailingZeros(lob)).hashCode();
      }

      return hc;
   }

   public final Iterator<E> iterator() {
      return new Iterator<E>() {
         int b;

         {
            this.b = Flags.this.bits;
         }

         public boolean hasNext() {
            return this.b != 0;
         }

         public E next() {
            int b = this.b;
            if (b == 0) {
               throw new NoSuchElementException();
            } else {
               int lob = Integer.lowestOneBit(b);
               E item = Flags.this.itemOf(Integer.numberOfTrailingZeros(lob));
               this.b = b ^ lob;
               return item;
            }
         }
      };
   }

   public final Iterator<E> descendingIterator() {
      return new Iterator<E>() {
         int b;

         {
            this.b = Flags.this.bits;
         }

         public boolean hasNext() {
            return this.b != 0;
         }

         public E next() {
            int b = this.b;
            if (b == 0) {
               throw new NoSuchElementException();
            } else {
               int hob = Integer.highestOneBit(b);
               E item = Flags.this.itemOf(Integer.numberOfTrailingZeros(hob));
               this.b = b ^ hob;
               return item;
            }
         }
      };
   }

   public void forEach(Consumer<? super E> action) {
      Assert.checkNotNullParam("action", action);

      int lob;
      for(int b = this.bits; b != 0; b ^= lob) {
         lob = Integer.lowestOneBit(b);
         action.accept(this.itemOf(Integer.numberOfTrailingZeros(lob)));
      }

   }

   public final String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.getClass().getSimpleName()).append('[');
      int bits = this.bits;
      if (bits != 0) {
         int lob = Integer.lowestOneBit(bits);
         buf.append(this.itemOf(Integer.numberOfTrailingZeros(lob)));

         for(bits ^= lob; bits != 0; bits ^= lob) {
            buf.append(' ');
            lob = Integer.lowestOneBit(bits);
            buf.append(this.itemOf(Integer.numberOfTrailingZeros(lob)));
         }
      }

      buf.append(']');
      return buf.toString();
   }

   private static int bitOf(Enum<?> item) {
      return 1 << item.ordinal();
   }
}
