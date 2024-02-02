package cn.hutool.core.collection;

import java.util.List;
import java.util.RandomAccess;

public class RandomAccessAvgPartition<T> extends AvgPartition<T> implements RandomAccess {
   public RandomAccessAvgPartition(List<T> list, int limit) {
      super(list, limit);
   }
}
