package org.codehaus.plexus.util.introspection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

public class ReflectionValueExtractor {
   private static final Class[] CLASS_ARGS = new Class[0];
   private static final Object[] OBJECT_ARGS = new Object[0];
   private static final Map classMaps = new WeakHashMap();
   private static final Pattern INDEXED_PROPS = Pattern.compile("(\\w+)\\[(\\d+)\\]");
   private static final Pattern MAPPED_PROPS = Pattern.compile("(\\w+)\\((.+)\\)");

   private ReflectionValueExtractor() {
   }

   public static Object evaluate(String expression, Object root) throws Exception {
      return evaluate(expression, root, true);
   }

   public static Object evaluate(String expression, Object root, boolean trimRootToken) throws Exception {
      if (trimRootToken) {
         expression = expression.substring(expression.indexOf(46) + 1);
      }

      Object value = root;
      StringTokenizer parser = new StringTokenizer(expression, ".");

      while(parser.hasMoreTokens()) {
         if (value == null) {
            return null;
         }

         String token = parser.nextToken();
         ClassMap classMap = getClassMap(value.getClass());
         Object[] localParams = OBJECT_ARGS;
         Matcher matcher = INDEXED_PROPS.matcher(token);
         Method method;
         String methodBase;
         String methodName;
         if (matcher.find()) {
            methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
            methodName = "get" + methodBase;
            method = classMap.findMethod(methodName, CLASS_ARGS);
            value = method.invoke(value, OBJECT_ARGS);
            classMap = getClassMap(value.getClass());
            if (classMap.getCachedClass().isArray()) {
               value = Arrays.asList((Object[])value);
               classMap = getClassMap(value.getClass());
            }

            if (!(value instanceof List)) {
               throw new Exception("The token '" + token + "' refers to a java.util.List or an array, but the value seems is an instance of '" + value.getClass() + "'.");
            }

            localParams = new Object[]{Integer.valueOf(matcher.group(2))};
            method = classMap.findMethod("get", localParams);
         } else {
            matcher = MAPPED_PROPS.matcher(token);
            if (matcher.find()) {
               methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
               methodName = "get" + methodBase;
               method = classMap.findMethod(methodName, CLASS_ARGS);
               value = method.invoke(value, OBJECT_ARGS);
               classMap = getClassMap(value.getClass());
               if (!(value instanceof Map)) {
                  throw new Exception("The token '" + token + "' refers to a java.util.Map, but the value seems is an instance of '" + value.getClass() + "'.");
               }

               localParams = new Object[]{matcher.group(2)};
               method = classMap.findMethod("get", localParams);
            } else {
               methodBase = StringUtils.capitalizeFirstLetter(token);
               methodName = "get" + methodBase;
               method = classMap.findMethod(methodName, CLASS_ARGS);
               if (method == null) {
                  methodName = "is" + methodBase;
                  method = classMap.findMethod(methodName, CLASS_ARGS);
               }
            }
         }

         if (method == null) {
            return null;
         }

         try {
            value = method.invoke(value, localParams);
         } catch (InvocationTargetException var12) {
            if (var12.getCause() instanceof IndexOutOfBoundsException) {
               return null;
            }

            throw var12;
         }
      }

      return value;
   }

   private static ClassMap getClassMap(Class clazz) {
      ClassMap classMap = (ClassMap)classMaps.get(clazz);
      if (classMap == null) {
         classMap = new ClassMap(clazz);
         classMaps.put(clazz, classMap);
      }

      return classMap;
   }
}
