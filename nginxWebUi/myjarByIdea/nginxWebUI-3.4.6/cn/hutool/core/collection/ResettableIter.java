package cn.hutool.core.collection;

import java.util.Iterator;

public interface ResettableIter<E> extends Iterator<E> {
   void reset();
}
