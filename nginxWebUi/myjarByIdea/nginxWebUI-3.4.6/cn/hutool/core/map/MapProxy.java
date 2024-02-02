package cn.hutool.core.map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapProxy implements Map<Object, Object>, OptNullBasicTypeFromObjectGetter<Object>, InvocationHandler, Serializable {
   private static final long serialVersionUID = 1L;
   Map map;

   public static MapProxy create(Map<?, ?> map) {
      return map instanceof MapProxy ? (MapProxy)map : new MapProxy(map);
   }

   public MapProxy(Map<?, ?> map) {
      this.map = map;
   }

   public Object getObj(Object key, Object defaultValue) {
      Object value = this.map.get(key);
      return null != value ? value : defaultValue;
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.map.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.map.containsValue(value);
   }

   public Object get(Object key) {
      return this.map.get(key);
   }

   public Object put(Object key, Object value) {
      return this.map.put(key, value);
   }

   public Object remove(Object key) {
      return this.map.remove(key);
   }

   public void putAll(Map<?, ?> m) {
      this.map.putAll(m);
   }

   public void clear() {
      this.map.clear();
   }

   public Set<Object> keySet() {
      return this.map.keySet();
   }

   public Collection<Object> values() {
      return this.map.values();
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return this.map.entrySet();
   }

   public Object invoke(Object proxy, Method method, Object[] args) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      String methodName;
      if (ArrayUtil.isEmpty((Object[])parameterTypes)) {
         Class<?> returnType = method.getReturnType();
         if (Void.TYPE != returnType) {
            methodName = method.getName();
            String fieldName = null;
            if (methodName.startsWith("get")) {
               fieldName = StrUtil.removePreAndLowerFirst(methodName, 3);
            } else if (BooleanUtil.isBoolean(returnType) && methodName.startsWith("is")) {
               fieldName = StrUtil.removePreAndLowerFirst(methodName, 2);
            } else {
               if ("hashCode".equals(methodName)) {
                  return this.hashCode();
               }

               if ("toString".equals(methodName)) {
                  return this.toString();
               }
            }

            if (StrUtil.isNotBlank(fieldName)) {
               if (!this.containsKey(fieldName)) {
                  fieldName = StrUtil.toUnderlineCase(fieldName);
               }

               return Convert.convert(method.getGenericReturnType(), this.get(fieldName));
            }
         }
      } else if (1 == parameterTypes.length) {
         String methodName = method.getName();
         if (methodName.startsWith("set")) {
            methodName = StrUtil.removePreAndLowerFirst(methodName, 3);
            if (StrUtil.isNotBlank(methodName)) {
               this.put(methodName, args[0]);
               Class<?> returnType = method.getReturnType();
               if (returnType.isInstance(proxy)) {
                  return proxy;
               }
            }
         } else if ("equals".equals(methodName)) {
            return this.equals(args[0]);
         }
      }

      throw new UnsupportedOperationException(method.toGenericString());
   }

   public <T> T toProxyBean(Class<T> interfaceClass) {
      return Proxy.newProxyInstance(ClassLoaderUtil.getClassLoader(), new Class[]{interfaceClass}, this);
   }
}
