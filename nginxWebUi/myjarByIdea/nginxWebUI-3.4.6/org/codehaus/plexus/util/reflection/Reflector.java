package org.codehaus.plexus.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Reflector {
   private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
   private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
   private HashMap classMaps = new HashMap();
   // $FF: synthetic field
   static Class class$java$lang$Object;

   public Object newInstance(Class theClass, Object[] params) throws ReflectorException {
      if (params == null) {
         params = new Object[0];
      }

      Class[] paramTypes = new Class[params.length];
      int i = 0;

      for(int len = params.length; i < len; ++i) {
         paramTypes[i] = params[i].getClass();
      }

      try {
         Constructor con = this.getConstructor(theClass, paramTypes);
         if (con != null) {
            return con.newInstance(params);
         } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Constructor not found for class: ");
            buffer.append(theClass.getName());
            buffer.append(" with specified or ancestor parameter classes: ");

            for(int i = 0; i < paramTypes.length; ++i) {
               buffer.append(paramTypes[i].getName());
               buffer.append(',');
            }

            buffer.setLength(buffer.length() - 1);
            throw new ReflectorException(buffer.toString());
         }
      } catch (InstantiationException var7) {
         throw new ReflectorException(var7);
      } catch (InvocationTargetException var8) {
         throw new ReflectorException(var8);
      } catch (IllegalAccessException var9) {
         throw new ReflectorException(var9);
      }
   }

   public Object getSingleton(Class theClass, Object[] initParams) throws ReflectorException {
      Class[] paramTypes = new Class[initParams.length];
      int i = 0;

      for(int len = initParams.length; i < len; ++i) {
         paramTypes[i] = initParams[i].getClass();
      }

      try {
         Method method = this.getMethod(theClass, "getInstance", paramTypes);
         return method.invoke((Object)null, initParams);
      } catch (InvocationTargetException var6) {
         throw new ReflectorException(var6);
      } catch (IllegalAccessException var7) {
         throw new ReflectorException(var7);
      }
   }

   public Object invoke(Object target, String methodName, Object[] params) throws ReflectorException {
      if (params == null) {
         params = new Object[0];
      }

      Class[] paramTypes = new Class[params.length];
      int i = 0;

      for(int len = params.length; i < len; ++i) {
         paramTypes[i] = params[i].getClass();
      }

      try {
         Method method = this.getMethod(target.getClass(), methodName, paramTypes);
         if (method != null) {
            return method.invoke(target, params);
         } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");

            for(int i = 0; i < paramTypes.length; ++i) {
               buffer.append(paramTypes[i].getName());
               buffer.append(',');
            }

            buffer.setLength(buffer.length() - 1);
            throw new ReflectorException(buffer.toString());
         }
      } catch (InvocationTargetException var8) {
         throw new ReflectorException(var8);
      } catch (IllegalAccessException var9) {
         throw new ReflectorException(var9);
      }
   }

   public Object getStaticField(Class targetClass, String fieldName) throws ReflectorException {
      try {
         Field field = targetClass.getField(fieldName);
         return field.get((Object)null);
      } catch (SecurityException var4) {
         throw new ReflectorException(var4);
      } catch (NoSuchFieldException var5) {
         throw new ReflectorException(var5);
      } catch (IllegalArgumentException var6) {
         throw new ReflectorException(var6);
      } catch (IllegalAccessException var7) {
         throw new ReflectorException(var7);
      }
   }

   public Object getField(Object target, String fieldName) throws ReflectorException {
      return this.getField(target, fieldName, false);
   }

   public Object getField(Object target, String fieldName, boolean breakAccessibility) throws ReflectorException {
      Class targetClass = target.getClass();

      while(targetClass != null) {
         try {
            Field field = targetClass.getDeclaredField(fieldName);
            boolean accessibilityBroken = false;
            if (!field.isAccessible() && breakAccessibility) {
               field.setAccessible(true);
               accessibilityBroken = true;
            }

            Object result = field.get(target);
            if (accessibilityBroken) {
               field.setAccessible(false);
            }

            return result;
         } catch (SecurityException var8) {
            throw new ReflectorException(var8);
         } catch (NoSuchFieldException var9) {
            if (targetClass == (class$java$lang$Object == null ? (class$java$lang$Object = class$("java.lang.Object")) : class$java$lang$Object)) {
               throw new ReflectorException(var9);
            }

            targetClass = targetClass.getSuperclass();
         } catch (IllegalAccessException var10) {
            throw new ReflectorException(var10);
         }
      }

      return null;
   }

   public Object invokeStatic(Class targetClass, String methodName, Object[] params) throws ReflectorException {
      if (params == null) {
         params = new Object[0];
      }

      Class[] paramTypes = new Class[params.length];
      int i = 0;

      for(int len = params.length; i < len; ++i) {
         paramTypes[i] = params[i].getClass();
      }

      try {
         Method method = this.getMethod(targetClass, methodName, paramTypes);
         if (method != null) {
            return method.invoke((Object)null, params);
         } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Singleton-producing method named '" + methodName + "' not found with specified parameter classes: ");

            for(int i = 0; i < paramTypes.length; ++i) {
               buffer.append(paramTypes[i].getName());
               buffer.append(',');
            }

            buffer.setLength(buffer.length() - 1);
            throw new ReflectorException(buffer.toString());
         }
      } catch (InvocationTargetException var8) {
         throw new ReflectorException(var8);
      } catch (IllegalAccessException var9) {
         throw new ReflectorException(var9);
      }
   }

   public Constructor getConstructor(Class targetClass, Class[] params) throws ReflectorException {
      Map constructorMap = this.getConstructorMap(targetClass);
      StringBuffer key = new StringBuffer(200);
      key.append("(");
      int i = 0;

      for(int len = params.length; i < len; ++i) {
         key.append(params[i].getName());
         key.append(",");
      }

      if (params.length > 0) {
         key.setLength(key.length() - 1);
      }

      key.append(")");
      Constructor constructor = null;
      String paramKey = key.toString();
      synchronized(paramKey.intern()) {
         constructor = (Constructor)constructorMap.get(paramKey);
         if (constructor == null) {
            Constructor[] cands = targetClass.getConstructors();
            int i = 0;

            for(int len = cands.length; i < len; ++i) {
               Class[] types = cands[i].getParameterTypes();
               if (params.length == types.length) {
                  int j = 0;

                  for(int len2 = params.length; j < len2; ++j) {
                     if (!types[j].isAssignableFrom(params[j])) {
                     }
                  }

                  constructor = cands[i];
                  constructorMap.put(paramKey, constructor);
               }
            }
         }
      }

      if (constructor == null) {
         throw new ReflectorException("Error retrieving constructor object for: " + targetClass.getName() + paramKey);
      } else {
         return constructor;
      }
   }

   public Object getObjectProperty(Object target, String propertyName) throws ReflectorException {
      Object returnValue = null;
      if (propertyName != null && propertyName.trim().length() >= 1) {
         String beanAccessor = "get" + Character.toUpperCase(propertyName.charAt(0));
         if (propertyName.trim().length() > 1) {
            beanAccessor = beanAccessor + propertyName.substring(1).trim();
         }

         Class targetClass = target.getClass();
         Class[] emptyParams = new Class[0];
         Method method = this._getMethod(targetClass, beanAccessor, emptyParams);
         if (method == null) {
            method = this._getMethod(targetClass, propertyName, emptyParams);
         }

         if (method != null) {
            try {
               method.invoke(target);
            } catch (IllegalAccessException var11) {
               throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", var11);
            } catch (InvocationTargetException var12) {
               throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", var12);
            }
         }

         if (method != null) {
            try {
               returnValue = method.invoke(target);
            } catch (IllegalAccessException var9) {
               throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", var9);
            } catch (InvocationTargetException var10) {
               throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", var10);
            }
         } else {
            returnValue = this.getField(target, propertyName, true);
            if (method == null && returnValue == null) {
               throw new ReflectorException("Neither method: '" + propertyName + "' nor bean accessor: '" + beanAccessor + "' can be found for class: '" + targetClass + "', and retrieval of field: '" + propertyName + "' returned null as value.");
            }
         }

         return returnValue;
      } else {
         throw new ReflectorException("Cannot retrieve value for empty property.");
      }
   }

   public Method getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
      Method method = this._getMethod(targetClass, methodName, params);
      if (method == null) {
         throw new ReflectorException("Method: '" + methodName + "' not found in class: '" + targetClass + "'");
      } else {
         return method;
      }
   }

   private Method _getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
      Map methodMap = this.getMethodMap(targetClass, methodName);
      StringBuffer key = new StringBuffer(200);
      key.append("(");
      int i = 0;

      for(int len = params.length; i < len; ++i) {
         key.append(params[i].getName());
         key.append(",");
      }

      key.append(")");
      Method method = null;
      String paramKey = key.toString();
      synchronized(paramKey.intern()) {
         method = (Method)methodMap.get(paramKey);
         if (method == null) {
            Method[] cands = targetClass.getMethods();
            int i = 0;

            for(int len = cands.length; i < len; ++i) {
               String name = cands[i].getName();
               if (methodName.equals(name)) {
                  Class[] types = cands[i].getParameterTypes();
                  if (params.length == types.length) {
                     int j = 0;

                     for(int len2 = params.length; j < len2; ++j) {
                        if (!types[j].isAssignableFrom(params[j])) {
                        }
                     }

                     method = cands[i];
                     methodMap.put(paramKey, method);
                  }
               }
            }
         }

         return method;
      }
   }

   private Map getConstructorMap(Class theClass) throws ReflectorException {
      return this.getMethodMap(theClass, "$$CONSTRUCTOR$$");
   }

   private Map getMethodMap(Class theClass, String methodName) throws ReflectorException {
      Map methodMap = null;
      if (theClass == null) {
         return null;
      } else {
         String className = theClass.getName();
         synchronized(className.intern()) {
            Map classMethods = (Map)this.classMaps.get(className);
            if (classMethods == null) {
               Map classMethods = new HashMap();
               methodMap = new HashMap();
               classMethods.put(methodName, methodMap);
               this.classMaps.put(className, classMethods);
            } else {
               String key = className + "::" + methodName;
               synchronized(key.intern()) {
                  methodMap = (Map)classMethods.get(methodName);
                  if (methodMap == null) {
                     methodMap = new HashMap();
                     classMethods.put(methodName, methodMap);
                  }
               }
            }

            return (Map)methodMap;
         }
      }
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }
}
