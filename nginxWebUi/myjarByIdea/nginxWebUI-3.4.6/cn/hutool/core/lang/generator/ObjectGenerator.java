package cn.hutool.core.lang.generator;

import cn.hutool.core.util.ReflectUtil;

public class ObjectGenerator<T> implements Generator<T> {
   private final Class<T> clazz;

   public ObjectGenerator(Class<T> clazz) {
      this.clazz = clazz;
   }

   public T next() {
      return ReflectUtil.newInstanceIfPossible(this.clazz);
   }
}
