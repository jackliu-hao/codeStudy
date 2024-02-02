package cn.hutool.core.lang.loader;

import cn.hutool.core.lang.Assert;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyFunLoader<T> extends LazyLoader<T> {
   private static final long serialVersionUID = 1L;
   private Supplier<T> supplier;

   public static <T> LazyFunLoader<T> on(Supplier<T> supplier) {
      Assert.notNull(supplier, "supplier must be not null!");
      return new LazyFunLoader(supplier);
   }

   public LazyFunLoader(Supplier<T> supplier) {
      Assert.notNull(supplier);
      this.supplier = supplier;
   }

   protected T init() {
      T t = this.supplier.get();
      this.supplier = null;
      return t;
   }

   public boolean isInitialize() {
      return this.supplier == null;
   }

   public void ifInitialized(Consumer<T> consumer) {
      Assert.notNull(consumer);
      if (this.isInitialize()) {
         consumer.accept(this.get());
      }

   }
}
