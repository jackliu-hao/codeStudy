package org.wildfly.common.selector;

import org.wildfly.common.Assert;

/** @deprecated */
@Deprecated
public final class ThreadLocalSelector<T> extends Selector<T> {
   private final ThreadLocal<? extends T> threadLocal;

   public ThreadLocalSelector(ThreadLocal<? extends T> threadLocal) {
      Assert.checkNotNullParam("threadLocal", threadLocal);
      this.threadLocal = threadLocal;
   }

   public T get() {
      return this.threadLocal.get();
   }
}
