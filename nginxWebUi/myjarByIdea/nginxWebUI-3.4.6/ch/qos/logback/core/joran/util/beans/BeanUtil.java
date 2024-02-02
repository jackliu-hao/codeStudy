package ch.qos.logback.core.joran.util.beans;

import java.lang.reflect.Method;

public class BeanUtil {
   public static final String PREFIX_GETTER_IS = "is";
   public static final String PREFIX_GETTER_GET = "get";
   public static final String PREFIX_SETTER = "set";
   public static final String PREFIX_ADDER = "add";

   public static boolean isAdder(Method method) {
      int parameterCount = getParameterCount(method);
      if (parameterCount != 1) {
         return false;
      } else {
         Class<?> returnType = method.getReturnType();
         if (returnType != Void.TYPE) {
            return false;
         } else {
            String methodName = method.getName();
            return methodName.startsWith("add");
         }
      }
   }

   public static boolean isGetter(Method method) {
      int parameterCount = getParameterCount(method);
      if (parameterCount > 0) {
         return false;
      } else {
         Class<?> returnType = method.getReturnType();
         if (returnType == Void.TYPE) {
            return false;
         } else {
            String methodName = method.getName();
            if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
               return false;
            } else {
               return !methodName.startsWith("is") || returnType.equals(Boolean.TYPE) || returnType.equals(Boolean.class);
            }
         }
      }
   }

   private static int getParameterCount(Method method) {
      return method.getParameterTypes().length;
   }

   public static boolean isSetter(Method method) {
      int parameterCount = getParameterCount(method);
      if (parameterCount != 1) {
         return false;
      } else {
         Class<?> returnType = method.getReturnType();
         if (returnType != Void.TYPE) {
            return false;
         } else {
            String methodName = method.getName();
            return methodName.startsWith("set");
         }
      }
   }

   public static String getPropertyName(Method method) {
      String methodName = method.getName();
      String rawPropertyName = getSubstringIfPrefixMatches(methodName, "get");
      if (rawPropertyName == null) {
         rawPropertyName = getSubstringIfPrefixMatches(methodName, "set");
      }

      if (rawPropertyName == null) {
         rawPropertyName = getSubstringIfPrefixMatches(methodName, "is");
      }

      if (rawPropertyName == null) {
         rawPropertyName = getSubstringIfPrefixMatches(methodName, "add");
      }

      return toLowerCamelCase(rawPropertyName);
   }

   public static String toLowerCamelCase(String string) {
      if (string == null) {
         return null;
      } else if (string.isEmpty()) {
         return string;
      } else if (string.length() > 1 && Character.isUpperCase(string.charAt(1)) && Character.isUpperCase(string.charAt(0))) {
         return string;
      } else {
         char[] chars = string.toCharArray();
         chars[0] = Character.toLowerCase(chars[0]);
         return new String(chars);
      }
   }

   private static String getSubstringIfPrefixMatches(String wholeString, String prefix) {
      return wholeString.startsWith(prefix) ? wholeString.substring(prefix.length()) : null;
   }
}
