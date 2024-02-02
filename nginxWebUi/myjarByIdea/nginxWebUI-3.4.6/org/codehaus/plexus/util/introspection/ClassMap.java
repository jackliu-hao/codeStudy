package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

public class ClassMap {
   private static final CacheMiss CACHE_MISS = new CacheMiss();
   private static final Object OBJECT = new Object();
   private Class clazz;
   private Map methodCache = new Hashtable();
   private MethodMap methodMap = new MethodMap();

   public ClassMap(Class clazz) {
      this.clazz = clazz;
      this.populateMethodCache();
   }

   Class getCachedClass() {
      return this.clazz;
   }

   public Method findMethod(String name, Object[] params) throws MethodMap.AmbiguousException {
      String methodKey = makeMethodKey(name, params);
      Object cacheEntry = this.methodCache.get(methodKey);
      if (cacheEntry == CACHE_MISS) {
         return null;
      } else {
         if (cacheEntry == null) {
            try {
               cacheEntry = this.methodMap.find(name, params);
            } catch (MethodMap.AmbiguousException var6) {
               this.methodCache.put(methodKey, CACHE_MISS);
               throw var6;
            }

            if (cacheEntry == null) {
               this.methodCache.put(methodKey, CACHE_MISS);
            } else {
               this.methodCache.put(methodKey, cacheEntry);
            }
         }

         return (Method)cacheEntry;
      }
   }

   private void populateMethodCache() {
      Method[] methods = getAccessibleMethods(this.clazz);

      for(int i = 0; i < methods.length; ++i) {
         Method method = methods[i];
         Method publicMethod = getPublicMethod(method);
         if (publicMethod != null) {
            this.methodMap.add(publicMethod);
            this.methodCache.put(this.makeMethodKey(publicMethod), publicMethod);
         }
      }

   }

   private String makeMethodKey(Method method) {
      Class[] parameterTypes = method.getParameterTypes();
      StringBuffer methodKey = new StringBuffer(method.getName());

      for(int j = 0; j < parameterTypes.length; ++j) {
         if (parameterTypes[j].isPrimitive()) {
            if (parameterTypes[j].equals(Boolean.TYPE)) {
               methodKey.append("java.lang.Boolean");
            } else if (parameterTypes[j].equals(Byte.TYPE)) {
               methodKey.append("java.lang.Byte");
            } else if (parameterTypes[j].equals(Character.TYPE)) {
               methodKey.append("java.lang.Character");
            } else if (parameterTypes[j].equals(Double.TYPE)) {
               methodKey.append("java.lang.Double");
            } else if (parameterTypes[j].equals(Float.TYPE)) {
               methodKey.append("java.lang.Float");
            } else if (parameterTypes[j].equals(Integer.TYPE)) {
               methodKey.append("java.lang.Integer");
            } else if (parameterTypes[j].equals(Long.TYPE)) {
               methodKey.append("java.lang.Long");
            } else if (parameterTypes[j].equals(Short.TYPE)) {
               methodKey.append("java.lang.Short");
            }
         } else {
            methodKey.append(parameterTypes[j].getName());
         }
      }

      return methodKey.toString();
   }

   private static String makeMethodKey(String method, Object[] params) {
      StringBuffer methodKey = (new StringBuffer()).append(method);

      for(int j = 0; j < params.length; ++j) {
         Object arg = params[j];
         if (arg == null) {
            arg = OBJECT;
         }

         methodKey.append(arg.getClass().getName());
      }

      return methodKey.toString();
   }

   private static Method[] getAccessibleMethods(Class clazz) {
      Method[] methods = clazz.getMethods();
      if (Modifier.isPublic(clazz.getModifiers())) {
         return methods;
      } else {
         MethodInfo[] methodInfos = new MethodInfo[methods.length];

         int upcastCount;
         for(upcastCount = methods.length; upcastCount-- > 0; methodInfos[upcastCount] = new MethodInfo(methods[upcastCount])) {
         }

         upcastCount = getAccessibleMethods(clazz, methodInfos, 0);
         if (upcastCount < methods.length) {
            methods = new Method[upcastCount];
         }

         int j = 0;

         for(int i = 0; i < methodInfos.length; ++i) {
            MethodInfo methodInfo = methodInfos[i];
            if (methodInfo.upcast) {
               methods[j++] = methodInfo.method;
            }
         }

         return methods;
      }
   }

   private static int getAccessibleMethods(Class clazz, MethodInfo[] methodInfos, int upcastCount) {
      int l = methodInfos.length;
      if (Modifier.isPublic(clazz.getModifiers())) {
         for(int i = 0; i < l && upcastCount < l; ++i) {
            try {
               MethodInfo methodInfo = methodInfos[i];
               if (!methodInfo.upcast) {
                  methodInfo.tryUpcasting(clazz);
                  ++upcastCount;
               }
            } catch (NoSuchMethodException var7) {
            }
         }

         if (upcastCount == l) {
            return upcastCount;
         }
      }

      Class superclazz = clazz.getSuperclass();
      if (superclazz != null) {
         upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);
         if (upcastCount == l) {
            return upcastCount;
         }
      }

      Class[] interfaces = clazz.getInterfaces();
      int i = interfaces.length;

      do {
         if (i-- <= 0) {
            return upcastCount;
         }

         upcastCount = getAccessibleMethods(interfaces[i], methodInfos, upcastCount);
      } while(upcastCount != l);

      return upcastCount;
   }

   public static Method getPublicMethod(Method method) {
      Class clazz = method.getDeclaringClass();
      return (clazz.getModifiers() & 1) != 0 ? method : getPublicMethod(clazz, method.getName(), method.getParameterTypes());
   }

   private static Method getPublicMethod(Class clazz, String name, Class[] paramTypes) {
      if ((clazz.getModifiers() & 1) != 0) {
         try {
            return clazz.getMethod(name, paramTypes);
         } catch (NoSuchMethodException var7) {
            return null;
         }
      } else {
         Class superclazz = clazz.getSuperclass();
         if (superclazz != null) {
            Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);
            if (superclazzMethod != null) {
               return superclazzMethod;
            }
         }

         Class[] interfaces = clazz.getInterfaces();

         for(int i = 0; i < interfaces.length; ++i) {
            Method interfaceMethod = getPublicMethod(interfaces[i], name, paramTypes);
            if (interfaceMethod != null) {
               return interfaceMethod;
            }
         }

         return null;
      }
   }

   private static final class MethodInfo {
      Method method = null;
      String name;
      Class[] parameterTypes;
      boolean upcast;

      MethodInfo(Method method) {
         this.name = method.getName();
         this.parameterTypes = method.getParameterTypes();
         this.upcast = false;
      }

      void tryUpcasting(Class clazz) throws NoSuchMethodException {
         this.method = clazz.getMethod(this.name, this.parameterTypes);
         this.name = null;
         this.parameterTypes = null;
         this.upcast = true;
      }
   }

   private static final class CacheMiss {
      private CacheMiss() {
      }

      // $FF: synthetic method
      CacheMiss(Object x0) {
         this();
      }
   }
}
