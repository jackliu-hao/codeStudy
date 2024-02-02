package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartitionIter<T> implements IterableIter<List<T>>, Serializable {
   private static final long serialVersionUID = 1L;
   protected final Iterator<T> iterator;
   protected final int partitionSize;

   public PartitionIter(Iterator<T> iterator, int partitionSize) {
      this.iterator = iterator;
      this.partitionSize = partitionSize;
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public List<T> next() {
      List<T> list = new ArrayList(this.partitionSize);

      for(int i = 0; i < this.partitionSize && this.iterator.hasNext(); ++i) {
         list.add(this.iterator.next());
      }

      return list;
   }
}
