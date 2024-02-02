package cn.hutool.core.bean;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.WeakConcurrentMap;

public enum BeanDescCache {
   INSTANCE;

   private final WeakConcurrentMap<Class<?>, BeanDesc> bdCache = new WeakConcurrentMap();

   public BeanDesc getBeanDesc(Class<?> beanClass, Func0<BeanDesc> supplier) {
      return (BeanDesc)this.bdCache.computeIfAbsent(beanClass, (key) -> {
         return (BeanDesc)supplier.callWithRuntimeException();
      });
   }

   public void clear() {
      this.bdCache.clear();
   }
}
