/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import ch.qos.logback.core.spi.PropertyContainer;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import ch.qos.logback.core.subst.NodeToStringTransformer;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptionHelper
/*     */ {
/*     */   static final String DELIM_START = "${";
/*     */   static final char DELIM_STOP = '}';
/*     */   static final String DELIM_DEFAULT = ":-";
/*     */   static final int DELIM_START_LEN = 2;
/*     */   static final int DELIM_STOP_LEN = 1;
/*     */   static final int DELIM_DEFAULT_LEN = 2;
/*     */   static final String _IS_UNDEFINED = "_IS_UNDEFINED";
/*     */   
/*     */   public static Object instantiateByClassName(String className, Class<?> superClass, Context context) throws IncompatibleClassException, DynamicClassLoadingException {
/*  32 */     ClassLoader classLoader = Loader.getClassLoaderOfObject(context);
/*  33 */     return instantiateByClassName(className, superClass, classLoader);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object instantiateByClassNameAndParameter(String className, Class<?> superClass, Context context, Class<?> type, Object param) throws IncompatibleClassException, DynamicClassLoadingException {
/*  38 */     ClassLoader classLoader = Loader.getClassLoaderOfObject(context);
/*  39 */     return instantiateByClassNameAndParameter(className, superClass, classLoader, type, param);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object instantiateByClassName(String className, Class<?> superClass, ClassLoader classLoader) throws IncompatibleClassException, DynamicClassLoadingException {
/*  44 */     return instantiateByClassNameAndParameter(className, superClass, classLoader, (Class<?>)null, (Object)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object instantiateByClassNameAndParameter(String className, Class<?> superClass, ClassLoader classLoader, Class<?> type, Object parameter) throws IncompatibleClassException, DynamicClassLoadingException {
/*  50 */     if (className == null) {
/*  51 */       throw new NullPointerException();
/*     */     }
/*     */     try {
/*  54 */       Class<?> classObj = null;
/*  55 */       classObj = classLoader.loadClass(className);
/*  56 */       if (!superClass.isAssignableFrom(classObj)) {
/*  57 */         throw new IncompatibleClassException(superClass, classObj);
/*     */       }
/*  59 */       if (type == null) {
/*  60 */         return classObj.newInstance();
/*     */       }
/*  62 */       Constructor<?> constructor = classObj.getConstructor(new Class[] { type });
/*  63 */       return constructor.newInstance(new Object[] { parameter });
/*     */     }
/*  65 */     catch (IncompatibleClassException ice) {
/*  66 */       throw ice;
/*  67 */     } catch (Throwable t) {
/*  68 */       throw new DynamicClassLoadingException("Failed to instantiate type " + className, t);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String substVars(String val, PropertyContainer pc1) {
/* 103 */     return substVars(val, pc1, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String substVars(String input, PropertyContainer pc0, PropertyContainer pc1) {
/*     */     try {
/* 111 */       return NodeToStringTransformer.substituteVariable(input, pc0, pc1);
/* 112 */     } catch (ScanException e) {
/* 113 */       throw new IllegalArgumentException("Failed to parse input [" + input + "]", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static String propertyLookup(String key, PropertyContainer pc1, PropertyContainer pc2) {
/* 118 */     String value = null;
/*     */     
/* 120 */     value = pc1.getProperty(key);
/*     */ 
/*     */     
/* 123 */     if (value == null && pc2 != null) {
/* 124 */       value = pc2.getProperty(key);
/*     */     }
/*     */     
/* 127 */     if (value == null) {
/* 128 */       value = getSystemProperty(key, null);
/*     */     }
/* 130 */     if (value == null) {
/* 131 */       value = getEnv(key);
/*     */     }
/* 133 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSystemProperty(String key, String def) {
/*     */     try {
/* 147 */       return System.getProperty(key, def);
/* 148 */     } catch (SecurityException e) {
/* 149 */       return def;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getEnv(String key) {
/*     */     try {
/* 161 */       return System.getenv(key);
/* 162 */     } catch (SecurityException e) {
/* 163 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSystemProperty(String key) {
/*     */     try {
/* 176 */       return System.getProperty(key);
/* 177 */     } catch (SecurityException e) {
/* 178 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setSystemProperties(ContextAware contextAware, Properties props) {
/* 183 */     for (Object o : props.keySet()) {
/* 184 */       String key = (String)o;
/* 185 */       String value = props.getProperty(key);
/* 186 */       setSystemProperty(contextAware, key, value);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setSystemProperty(ContextAware contextAware, String key, String value) {
/*     */     try {
/* 192 */       System.setProperty(key, value);
/* 193 */     } catch (SecurityException e) {
/* 194 */       contextAware.addError("Failed to set system property [" + key + "]", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties getSystemProperties() {
/*     */     try {
/* 206 */       return System.getProperties();
/* 207 */     } catch (SecurityException e) {
/* 208 */       return new Properties();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] extractDefaultReplacement(String key) {
/* 221 */     String[] result = new String[2];
/* 222 */     if (key == null) {
/* 223 */       return result;
/*     */     }
/* 225 */     result[0] = key;
/* 226 */     int d = key.indexOf(":-");
/* 227 */     if (d != -1) {
/* 228 */       result[0] = key.substring(0, d);
/* 229 */       result[1] = key.substring(d + 2);
/*     */     } 
/* 231 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean toBoolean(String value, boolean dEfault) {
/* 242 */     if (value == null) {
/* 243 */       return dEfault;
/*     */     }
/*     */     
/* 246 */     String trimmedVal = value.trim();
/*     */     
/* 248 */     if ("true".equalsIgnoreCase(trimmedVal)) {
/* 249 */       return true;
/*     */     }
/*     */     
/* 252 */     if ("false".equalsIgnoreCase(trimmedVal)) {
/* 253 */       return false;
/*     */     }
/*     */     
/* 256 */     return dEfault;
/*     */   }
/*     */   
/*     */   public static boolean isEmpty(String str) {
/* 260 */     return (str == null || str.length() == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\OptionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */