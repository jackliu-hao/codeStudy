package cn.hutool.core.lang.tree;

import cn.hutool.core.comparator.CompareUtil;
import java.io.Serializable;

public interface Node<T> extends Comparable<Node<T>>, Serializable {
   T getId();

   Node<T> setId(T var1);

   T getParentId();

   Node<T> setParentId(T var1);

   CharSequence getName();

   Node<T> setName(CharSequence var1);

   Comparable<?> getWeight();

   Node<T> setWeight(Comparable<?> var1);

   default int compareTo(Node node) {
      if (null == node) {
         return 1;
      } else {
         Comparable weight = this.getWeight();
         Comparable weightOther = node.getWeight();
         return CompareUtil.compare(weight, weightOther);
      }
   }
}
