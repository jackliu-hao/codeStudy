package org.wildfly.common.selector;

import org.wildfly.common.Assert;

/** @deprecated */
@Deprecated
public final class GlobalSelector<T> extends Selector<T> {
   private final T instance;

   public GlobalSelector(T instance) {
      Assert.checkNotNullParam("instance", instance);
      this.instance = instance;
   }

   public T get() {
      return this.instance;
   }
}
