package cn.hutool.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class NullComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;
   protected final boolean nullGreater;
   protected final Comparator<T> comparator;

   public NullComparator(boolean nullGreater, Comparator<? super T> comparator) {
      this.nullGreater = nullGreater;
      this.comparator = comparator;
   }

   public int compare(T a, T b) {
      if (a == b) {
         return 0;
      } else if (a == null) {
         return this.nullGreater ? 1 : -1;
      } else if (b == null) {
         return this.nullGreater ? -1 : 1;
      } else {
         return this.doCompare(a, b);
      }
   }

   public Comparator<T> thenComparing(Comparator<? super T> other) {
      Objects.requireNonNull(other);
      return new NullComparator(this.nullGreater, this.comparator == null ? other : this.comparator.thenComparing(other));
   }

   public Comparator<T> reversed() {
      return new NullComparator(!this.nullGreater, this.comparator == null ? null : this.comparator.reversed());
   }

   protected int doCompare(T a, T b) {
      if (null == this.comparator) {
         return a instanceof Comparable && b instanceof Comparable ? ((Comparable)a).compareTo(b) : 0;
      } else {
         return this.comparator.compare(a, b);
      }
   }
}
