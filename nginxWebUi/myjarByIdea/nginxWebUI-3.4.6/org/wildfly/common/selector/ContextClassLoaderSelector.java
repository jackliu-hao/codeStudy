package org.wildfly.common.selector;

/** @deprecated */
@Deprecated
class ContextClassLoaderSelector extends Selector<ClassLoader> {
   public ClassLoader get() {
      return Thread.currentThread().getContextClassLoader();
   }
}
