package cn.hutool.core.comparator;

import cn.hutool.core.lang.Chain;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ComparatorChain<E> implements Chain<Comparator<E>, ComparatorChain<E>>, Comparator<E>, Serializable {
   private static final long serialVersionUID = -2426725788913962429L;
   private final List<Comparator<E>> chain;
   private final BitSet orderingBits;
   private boolean lock;

   public static <E> ComparatorChain<E> of(Comparator<E> comparator) {
      return of(comparator, false);
   }

   public static <E> ComparatorChain<E> of(Comparator<E> comparator, boolean reverse) {
      return new ComparatorChain(comparator, reverse);
   }

   @SafeVarargs
   public static <E> ComparatorChain<E> of(Comparator<E>... comparators) {
      return of(Arrays.asList(comparators));
   }

   public static <E> ComparatorChain<E> of(List<Comparator<E>> comparators) {
      return new ComparatorChain(comparators);
   }

   public static <E> ComparatorChain<E> of(List<Comparator<E>> comparators, BitSet bits) {
      return new ComparatorChain(comparators, bits);
   }

   public ComparatorChain() {
      this(new ArrayList(), new BitSet());
   }

   public ComparatorChain(Comparator<E> comparator) {
      this(comparator, false);
   }

   public ComparatorChain(Comparator<E> comparator, boolean reverse) {
      this.lock = false;
      this.chain = new ArrayList(1);
      this.chain.add(comparator);
      this.orderingBits = new BitSet(1);
      if (reverse) {
         this.orderingBits.set(0);
      }

   }

   public ComparatorChain(List<Comparator<E>> list) {
      this(list, new BitSet(list.size()));
   }

   public ComparatorChain(List<Comparator<E>> list, BitSet bits) {
      this.lock = false;
      this.chain = list;
      this.orderingBits = bits;
   }

   public ComparatorChain<E> addComparator(Comparator<E> comparator) {
      return this.addComparator(comparator, false);
   }

   public ComparatorChain<E> addComparator(Comparator<E> comparator, boolean reverse) {
      this.checkLocked();
      this.chain.add(comparator);
      if (reverse) {
         this.orderingBits.set(this.chain.size() - 1);
      }

      return this;
   }

   public ComparatorChain<E> setComparator(int index, Comparator<E> comparator) throws IndexOutOfBoundsException {
      return this.setComparator(index, comparator, false);
   }

   public ComparatorChain<E> setComparator(int index, Comparator<E> comparator, boolean reverse) {
      this.checkLocked();
      this.chain.set(index, comparator);
      if (reverse) {
         this.orderingBits.set(index);
      } else {
         this.orderingBits.clear(index);
      }

      return this;
   }

   public ComparatorChain<E> setForwardSort(int index) {
      this.checkLocked();
      this.orderingBits.clear(index);
      return this;
   }

   public ComparatorChain<E> setReverseSort(int index) {
      this.checkLocked();
      this.orderingBits.set(index);
      return this;
   }

   public int size() {
      return this.chain.size();
   }

   public boolean isLocked() {
      return this.lock;
   }

   public Iterator<Comparator<E>> iterator() {
      return this.chain.iterator();
   }

   public ComparatorChain<E> addChain(Comparator<E> element) {
      return this.addComparator(element);
   }

   public int compare(E o1, E o2) throws UnsupportedOperationException {
      if (!this.lock) {
         this.checkChainIntegrity();
         this.lock = true;
      }

      Iterator<Comparator<E>> comparators = this.chain.iterator();

      for(int comparatorIndex = 0; comparators.hasNext(); ++comparatorIndex) {
         Comparator<? super E> comparator = (Comparator)comparators.next();
         int retval = comparator.compare(o1, o2);
         if (retval != 0) {
            if (this.orderingBits.get(comparatorIndex)) {
               retval = retval > 0 ? -1 : 1;
            }

            return retval;
         }
      }

      return 0;
   }

   public int hashCode() {
      int hash = 0;
      if (null != this.chain) {
         hash ^= this.chain.hashCode();
      }

      if (null != this.orderingBits) {
         hash ^= this.orderingBits.hashCode();
      }

      return hash;
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (null == object) {
         return false;
      } else if (!object.getClass().equals(this.getClass())) {
         return false;
      } else {
         ComparatorChain<?> otherChain = (ComparatorChain)object;
         return Objects.equals(this.orderingBits, otherChain.orderingBits) && this.chain.equals(otherChain.chain);
      }
   }

   private void checkLocked() {
      if (this.lock) {
         throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
      }
   }

   private void checkChainIntegrity() {
      if (this.chain.size() == 0) {
         throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
      }
   }
}
