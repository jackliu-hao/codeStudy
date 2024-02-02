package cn.hutool.core.lang.loader;

import java.io.Serializable;

public abstract class LazyLoader<T> implements Loader<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private volatile T object;

   public T get() {
      T result = this.object;
      if (result == null) {
         synchronized(this) {
            result = this.object;
            if (result == null) {
               this.object = result = this.init();
            }
         }
      }

      return result;
   }

   protected abstract T init();
}
