package cn.hutool.core.collection;

import java.util.Iterator;

public interface ResettableIter<E> extends Iterator<E> {
  void reset();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\ResettableIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */