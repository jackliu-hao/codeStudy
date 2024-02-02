package org.codehaus.plexus.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {
   // $FF: synthetic field
   static Class class$java$lang$Object;

   public static Field getFieldByNameIncludingSuperclasses(String fieldName, Class clazz) {
      Field retValue = null;

      try {
         retValue = clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException var5) {
         Class superclass = clazz.getSuperclass();
         if (superclass != null) {
            retValue = getFieldByNameIncludingSuperclasses(fieldName, superclass);
         }
      }

      return retValue;
   }

   public static List getFieldsIncludingSuperclasses(Class clazz) {
      List fields = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
      Class superclass = clazz.getSuperclass();
      if (superclass != null) {
         fields.addAll(getFieldsIncludingSuperclasses(superclass));
      }

      return fields;
   }

   public static Method getSetter(String fieldName, Class clazz) {
      Method[] methods = clazz.getMethods();
      fieldName = "set" + StringUtils.capitalizeFirstLetter(fieldName);

      for(int i = 0; i < methods.length; ++i) {
         Method method = methods[i];
         if (method.getName().equals(fieldName) && isSetter(method)) {
            return method;
         }
      }

      return null;
   }

   public static List getSetters(Class clazz) {
      Method[] methods = clazz.getMethods();
      List list = new ArrayList();

      for(int i = 0; i < methods.length; ++i) {
         Method method = methods[i];
         if (isSetter(method)) {
            list.add(method);
         }
      }

      return list;
   }

   public static Class getSetterType(Method method) {
      if (!isSetter(method)) {
         throw new RuntimeException("The method " + method.getDeclaringClass().getName() + "." + method.getName() + " is not a setter.");
      } else {
         return method.getParameterTypes()[0];
      }
   }

   public static void setVariableValueInObject(Object object, String variable, Object value) throws IllegalAccessException {
      Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
      field.setAccessible(true);
      field.set(object, value);
   }

   public static Object getValueIncludingSuperclasses(String variable, Object object) throws IllegalAccessException {
      Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
      field.setAccessible(true);
      return field.get(object);
   }

   public static Map getVariablesAndValuesIncludingSuperclasses(Object object) throws IllegalAccessException {
      HashMap map = new HashMap();
      gatherVariablesAndValuesIncludingSuperclasses(object, map);
      return map;
   }

   public static boolean isSetter(Method method) {
      return method.getReturnType().equals(Void.TYPE) && !Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1;
   }

   private static void gatherVariablesAndValuesIncludingSuperclasses(Object object, Map map) throws IllegalAccessException {
      Class clazz = object.getClass();
      Field[] fields = clazz.getDeclaredFields();
      AccessibleObject.setAccessible(fields, true);

      for(int i = 0; i < fields.length; ++i) {
         Field field = fields[i];
         map.put(field.getName(), field.get(object));
      }

      Class superclass = clazz.getSuperclass();
      if (!(class$java$lang$Object == null ? (class$java$lang$Object = class$("java.lang.Object")) : class$java$lang$Object).equals(superclass)) {
         gatherVariablesAndValuesIncludingSuperclasses(superclass, map);
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
