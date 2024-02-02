package cn.hutool.core.collection;

import java.util.AbstractList;
import java.util.List;

public class Partition<T> extends AbstractList<List<T>> {
   protected final List<T> list;
   protected final int size;

   public Partition(List<T> list, int size) {
      this.list = list;
      this.size = Math.min(size, list.size());
   }

   public List<T> get(int index) {
      int start = index * this.size;
      int end = Math.min(start + this.size, this.list.size());
      return this.list.subList(start, end);
   }

   public int size() {
      int size = this.size;
      int total = this.list.size();
      int length = total / size;
      if (total % size > 0) {
         ++length;
      }

      return length;
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }
}
