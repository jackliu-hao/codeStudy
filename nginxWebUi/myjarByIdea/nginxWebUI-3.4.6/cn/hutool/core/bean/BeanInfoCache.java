package cn.hutool.core.bean;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.ReferenceConcurrentMap;
import cn.hutool.core.map.WeakConcurrentMap;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.function.Function;

public enum BeanInfoCache {
   INSTANCE;

   private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> pdCache = new WeakConcurrentMap();
   private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> ignoreCasePdCache = new WeakConcurrentMap();

   public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase) {
      return (Map)this.getCache(ignoreCase).get(beanClass);
   }

   public Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass, boolean ignoreCase, Func0<Map<String, PropertyDescriptor>> supplier) {
      return (Map)this.getCache(ignoreCase).computeIfAbsent(beanClass, (Function)((key) -> {
         return (Map)supplier.callWithRuntimeException();
      }));
   }

   public void putPropertyDescriptorMap(Class<?> beanClass, Map<String, PropertyDescriptor> fieldNamePropertyDescriptorMap, boolean ignoreCase) {
      this.getCache(ignoreCase).put(beanClass, fieldNamePropertyDescriptorMap);
   }

   public void clear() {
      this.pdCache.clear();
      this.ignoreCasePdCache.clear();
   }

   private ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> getCache(boolean ignoreCase) {
      return ignoreCase ? this.ignoreCasePdCache : this.pdCache;
   }
}
