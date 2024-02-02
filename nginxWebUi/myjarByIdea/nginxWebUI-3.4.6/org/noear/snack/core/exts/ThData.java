package org.noear.snack.core.exts;

import java.util.function.Supplier;

public class ThData<T> extends ThreadLocal<T> {
   private Supplier<T> _def;

   public ThData(Supplier<T> def) {
      this._def = def;
   }

   protected T initialValue() {
      return this._def.get();
   }
}
