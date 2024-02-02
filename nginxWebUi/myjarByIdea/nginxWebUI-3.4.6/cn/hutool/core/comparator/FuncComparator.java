package cn.hutool.core.comparator;

import cn.hutool.core.util.ObjectUtil;
import java.util.Comparator;
import java.util.function.Function;

public class FuncComparator<T> extends NullComparator<T> {
   private static final long serialVersionUID = 1L;
   private final Function<T, Comparable<?>> func;

   public FuncComparator(boolean nullGreater, Function<T, Comparable<?>> func) {
      super(nullGreater, (Comparator)null);
      this.func = func;
   }

   protected int doCompare(T a, T b) {
      Comparable v1;
      Comparable v2;
      try {
         v1 = (Comparable)this.func.apply(a);
         v2 = (Comparable)this.func.apply(b);
      } catch (Exception var6) {
         throw new ComparatorException(var6);
      }

      return this.compare(a, b, v1, v2);
   }

   private int compare(T o1, T o2, Comparable v1, Comparable v2) {
      int result = ObjectUtil.compare(v1, v2);
      if (0 == result) {
         result = CompareUtil.compare(o1, o2, this.nullGreater);
      }

      return result;
   }
}
