package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.spi.ContextAware;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class StringToObjectConverter {
   private static final Class<?>[] STING_CLASS_PARAMETER = new Class[]{String.class};

   public static boolean canBeBuiltFromSimpleString(Class<?> parameterClass) {
      Package p = parameterClass.getPackage();
      if (parameterClass.isPrimitive()) {
         return true;
      } else if (p != null && "java.lang".equals(p.getName())) {
         return true;
      } else if (followsTheValueOfConvention(parameterClass)) {
         return true;
      } else if (parameterClass.isEnum()) {
         return true;
      } else {
         return isOfTypeCharset(parameterClass);
      }
   }

   public static Object convertArg(ContextAware ca, String val, Class<?> type) {
      if (val == null) {
         return null;
      } else {
         String v = val.trim();
         if (String.class.isAssignableFrom(type)) {
            return v;
         } else if (Integer.TYPE.isAssignableFrom(type)) {
            return new Integer(v);
         } else if (Long.TYPE.isAssignableFrom(type)) {
            return new Long(v);
         } else if (Float.TYPE.isAssignableFrom(type)) {
            return new Float(v);
         } else if (Double.TYPE.isAssignableFrom(type)) {
            return new Double(v);
         } else {
            if (Boolean.TYPE.isAssignableFrom(type)) {
               if ("true".equalsIgnoreCase(v)) {
                  return Boolean.TRUE;
               }

               if ("false".equalsIgnoreCase(v)) {
                  return Boolean.FALSE;
               }
            } else {
               if (type.isEnum()) {
                  return convertToEnum(ca, v, type);
               }

               if (followsTheValueOfConvention(type)) {
                  return convertByValueOfMethod(ca, type, v);
               }

               if (isOfTypeCharset(type)) {
                  return convertToCharset(ca, val);
               }
            }

            return null;
         }
      }
   }

   private static boolean isOfTypeCharset(Class<?> type) {
      return Charset.class.isAssignableFrom(type);
   }

   private static Charset convertToCharset(ContextAware ca, String val) {
      try {
         return Charset.forName(val);
      } catch (UnsupportedCharsetException var3) {
         ca.addError("Failed to get charset [" + val + "]", var3);
         return null;
      }
   }

   public static Method getValueOfMethod(Class<?> type) {
      try {
         return type.getMethod("valueOf", STING_CLASS_PARAMETER);
      } catch (NoSuchMethodException var2) {
         return null;
      } catch (SecurityException var3) {
         return null;
      }
   }

   private static boolean followsTheValueOfConvention(Class<?> parameterClass) {
      Method valueOfMethod = getValueOfMethod(parameterClass);
      if (valueOfMethod == null) {
         return false;
      } else {
         int mod = valueOfMethod.getModifiers();
         return Modifier.isStatic(mod);
      }
   }

   private static Object convertByValueOfMethod(ContextAware ca, Class<?> type, String val) {
      try {
         Method valueOfMethod = type.getMethod("valueOf", STING_CLASS_PARAMETER);
         return valueOfMethod.invoke((Object)null, val);
      } catch (Exception var4) {
         ca.addError("Failed to invoke valueOf{} method in class [" + type.getName() + "] with value [" + val + "]");
         return null;
      }
   }

   private static Object convertToEnum(ContextAware ca, String val, Class<? extends Enum> enumType) {
      return Enum.valueOf(enumType, val);
   }

   boolean isBuildableFromSimpleString() {
      return false;
   }
}
