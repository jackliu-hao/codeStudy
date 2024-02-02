package cn.hutool.core.comparator;

import cn.hutool.core.lang.Assert;
import java.util.Comparator;

public class InstanceComparator<T> implements Comparator<T> {
   private final boolean atEndIfMiss;
   private final Class<?>[] instanceOrder;

   public InstanceComparator(Class<?>... instanceOrder) {
      this(false, instanceOrder);
   }

   public InstanceComparator(boolean atEndIfMiss, Class<?>... instanceOrder) {
      Assert.notNull(instanceOrder, "'instanceOrder' array must not be null");
      this.atEndIfMiss = atEndIfMiss;
      this.instanceOrder = instanceOrder;
   }

   public int compare(T o1, T o2) {
      int i1 = this.getOrder(o1);
      int i2 = this.getOrder(o2);
      return Integer.compare(i1, i2);
   }

   private int getOrder(T object) {
      if (object != null) {
         for(int i = 0; i < this.instanceOrder.length; ++i) {
            if (this.instanceOrder[i].isInstance(object)) {
               return i;
            }
         }
      }

      return this.atEndIfMiss ? this.instanceOrder.length : -1;
   }
}
