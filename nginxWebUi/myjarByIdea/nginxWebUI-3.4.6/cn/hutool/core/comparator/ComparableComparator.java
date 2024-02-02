package cn.hutool.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

public class ComparableComparator<E extends Comparable<? super E>> implements Comparator<E>, Serializable {
   private static final long serialVersionUID = 3020871676147289162L;
   public static final ComparableComparator INSTANCE = new ComparableComparator();

   public int compare(E obj1, E obj2) {
      return obj1.compareTo(obj2);
   }

   public int hashCode() {
      return "ComparableComparator".hashCode();
   }

   public boolean equals(Object object) {
      return this == object || null != object && object.getClass().equals(this.getClass());
   }
}
