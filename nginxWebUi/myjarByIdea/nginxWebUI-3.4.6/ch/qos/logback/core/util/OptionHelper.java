package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.subst.NodeToStringTransformer;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Properties;

public class OptionHelper {
   static final String DELIM_START = "${";
   static final char DELIM_STOP = '}';
   static final String DELIM_DEFAULT = ":-";
   static final int DELIM_START_LEN = 2;
   static final int DELIM_STOP_LEN = 1;
   static final int DELIM_DEFAULT_LEN = 2;
   static final String _IS_UNDEFINED = "_IS_UNDEFINED";

   public static Object instantiateByClassName(String className, Class<?> superClass, Context context) throws IncompatibleClassException, DynamicClassLoadingException {
      ClassLoader classLoader = Loader.getClassLoaderOfObject(context);
      return instantiateByClassName(className, superClass, classLoader);
   }

   public static Object instantiateByClassNameAndParameter(String className, Class<?> superClass, Context context, Class<?> type, Object param) throws IncompatibleClassException, DynamicClassLoadingException {
      ClassLoader classLoader = Loader.getClassLoaderOfObject(context);
      return instantiateByClassNameAndParameter(className, superClass, classLoader, type, param);
   }

   public static Object instantiateByClassName(String className, Class<?> superClass, ClassLoader classLoader) throws IncompatibleClassException, DynamicClassLoadingException {
      return instantiateByClassNameAndParameter(className, superClass, (ClassLoader)classLoader, (Class)null, (Object)null);
   }

   public static Object instantiateByClassNameAndParameter(String className, Class<?> superClass, ClassLoader classLoader, Class<?> type, Object parameter) throws IncompatibleClassException, DynamicClassLoadingException {
      if (className == null) {
         throw new NullPointerException();
      } else {
         try {
            Class<?> classObj = null;
            classObj = classLoader.loadClass(className);
            if (!superClass.isAssignableFrom(classObj)) {
               throw new IncompatibleClassException(superClass, classObj);
            } else if (type == null) {
               return classObj.newInstance();
            } else {
               Constructor<?> constructor = classObj.getConstructor(type);
               return constructor.newInstance(parameter);
            }
         } catch (IncompatibleClassException var7) {
            throw var7;
         } catch (Throwable var8) {
            throw new DynamicClassLoadingException("Failed to instantiate type " + className, var8);
         }
      }
   }

   public static String substVars(String val, PropertyContainer pc1) {
      return substVars(val, pc1, (PropertyContainer)null);
   }

   public static String substVars(String input, PropertyContainer pc0, PropertyContainer pc1) {
      try {
         return NodeToStringTransformer.substituteVariable(input, pc0, pc1);
      } catch (ScanException var4) {
         throw new IllegalArgumentException("Failed to parse input [" + input + "]", var4);
      }
   }

   public static String propertyLookup(String key, PropertyContainer pc1, PropertyContainer pc2) {
      String value = null;
      value = pc1.getProperty(key);
      if (value == null && pc2 != null) {
         value = pc2.getProperty(key);
      }

      if (value == null) {
         value = getSystemProperty(key, (String)null);
      }

      if (value == null) {
         value = getEnv(key);
      }

      return value;
   }

   public static String getSystemProperty(String key, String def) {
      try {
         return System.getProperty(key, def);
      } catch (SecurityException var3) {
         return def;
      }
   }

   public static String getEnv(String key) {
      try {
         return System.getenv(key);
      } catch (SecurityException var2) {
         return null;
      }
   }

   public static String getSystemProperty(String key) {
      try {
         return System.getProperty(key);
      } catch (SecurityException var2) {
         return null;
      }
   }

   public static void setSystemProperties(ContextAware contextAware, Properties props) {
      Iterator var2 = props.keySet().iterator();

      while(var2.hasNext()) {
         Object o = var2.next();
         String key = (String)o;
         String value = props.getProperty(key);
         setSystemProperty(contextAware, key, value);
      }

   }

   public static void setSystemProperty(ContextAware contextAware, String key, String value) {
      try {
         System.setProperty(key, value);
      } catch (SecurityException var4) {
         contextAware.addError("Failed to set system property [" + key + "]", var4);
      }

   }

   public static Properties getSystemProperties() {
      try {
         return System.getProperties();
      } catch (SecurityException var1) {
         return new Properties();
      }
   }

   public static String[] extractDefaultReplacement(String key) {
      String[] result = new String[2];
      if (key == null) {
         return result;
      } else {
         result[0] = key;
         int d = key.indexOf(":-");
         if (d != -1) {
            result[0] = key.substring(0, d);
            result[1] = key.substring(d + 2);
         }

         return result;
      }
   }

   public static boolean toBoolean(String value, boolean dEfault) {
      if (value == null) {
         return dEfault;
      } else {
         String trimmedVal = value.trim();
         if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
         } else {
            return "false".equalsIgnoreCase(trimmedVal) ? false : dEfault;
         }
      }
   }

   public static boolean isEmpty(String str) {
      return str == null || str.length() == 0;
   }
}
