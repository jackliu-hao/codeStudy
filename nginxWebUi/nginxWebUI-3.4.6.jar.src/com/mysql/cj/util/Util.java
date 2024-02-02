/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ 
/*     */ 
/*     */ public class Util
/*     */ {
/*  62 */   private static int jvmVersion = 8;
/*     */   
/*  64 */   private static int jvmUpdateNumber = -1;
/*     */   
/*     */   static {
/*  67 */     int startPos = Constants.JVM_VERSION.indexOf('.');
/*  68 */     int endPos = startPos + 1;
/*  69 */     if (startPos != -1) {
/*  70 */       while (Character.isDigit(Constants.JVM_VERSION.charAt(endPos)) && ++endPos < Constants.JVM_VERSION.length());
/*     */     }
/*     */ 
/*     */     
/*  74 */     startPos++;
/*  75 */     if (endPos > startPos) {
/*  76 */       jvmVersion = Integer.parseInt(Constants.JVM_VERSION.substring(startPos, endPos));
/*     */     }
/*  78 */     startPos = Constants.JVM_VERSION.indexOf("_");
/*  79 */     endPos = startPos + 1;
/*  80 */     if (startPos != -1) {
/*  81 */       while (Character.isDigit(Constants.JVM_VERSION.charAt(endPos)) && ++endPos < Constants.JVM_VERSION.length());
/*     */     }
/*     */ 
/*     */     
/*  85 */     startPos++;
/*  86 */     if (endPos > startPos) {
/*  87 */       jvmUpdateNumber = Integer.parseInt(Constants.JVM_VERSION.substring(startPos, endPos));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getJVMVersion() {
/*  93 */     return jvmVersion;
/*     */   }
/*     */   
/*     */   public static boolean jvmMeetsMinimum(int version, int updateNumber) {
/*  97 */     return (getJVMVersion() > version || (getJVMVersion() == version && getJVMUpdateNumber() >= updateNumber));
/*     */   }
/*     */   
/*     */   public static int getJVMUpdateNumber() {
/* 101 */     return jvmUpdateNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCommunityEdition(String serverVersion) {
/* 112 */     return !isEnterpriseEdition(serverVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEnterpriseEdition(String serverVersion) {
/* 123 */     return (serverVersion.contains("enterprise") || serverVersion.contains("commercial") || serverVersion.contains("advanced"));
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
/*     */   public static String stackTraceToString(Throwable ex) {
/* 136 */     StringBuilder traceBuf = new StringBuilder();
/* 137 */     traceBuf.append(Messages.getString("Util.1"));
/*     */     
/* 139 */     if (ex != null) {
/* 140 */       traceBuf.append(ex.getClass().getName());
/*     */       
/* 142 */       String message = ex.getMessage();
/*     */       
/* 144 */       if (message != null) {
/* 145 */         traceBuf.append(Messages.getString("Util.2"));
/* 146 */         traceBuf.append(message);
/*     */       } 
/*     */       
/* 149 */       StringWriter out = new StringWriter();
/*     */       
/* 151 */       PrintWriter printOut = new PrintWriter(out);
/*     */       
/* 153 */       ex.printStackTrace(printOut);
/*     */       
/* 155 */       traceBuf.append(Messages.getString("Util.3"));
/* 156 */       traceBuf.append(out.toString());
/*     */     } 
/*     */     
/* 159 */     traceBuf.append(Messages.getString("Util.4"));
/*     */     
/* 161 */     return traceBuf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor, String errorMessage) {
/*     */     try {
/* 167 */       return handleNewInstance(Class.forName(className).getConstructor(argTypes), args, exceptionInterceptor);
/* 168 */     } catch (SecurityException|NoSuchMethodException|ClassNotFoundException e) {
/* 169 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, errorMessage, e, exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) {
/* 174 */     return getInstance(className, argTypes, args, exceptionInterceptor, "Can't instantiate required class");
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
/*     */   public static Object handleNewInstance(Constructor<?> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor) {
/*     */     try {
/* 192 */       return ctor.newInstance(args);
/* 193 */     } catch (IllegalArgumentException|InstantiationException|IllegalAccessException e) {
/* 194 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Can't instantiate required class", e, exceptionInterceptor);
/* 195 */     } catch (InvocationTargetException e) {
/* 196 */       Throwable target = e.getTargetException();
/*     */       
/* 198 */       if (target instanceof ExceptionInInitializerError) {
/* 199 */         target = ((ExceptionInInitializerError)target).getException();
/* 200 */       } else if (target instanceof CJException) {
/* 201 */         throw (CJException)target;
/*     */       } 
/*     */       
/* 204 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, target.getMessage(), target, exceptionInterceptor);
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
/*     */   public static boolean interfaceExists(String hostname) {
/*     */     try {
/* 218 */       Class<?> networkInterfaceClass = Class.forName("java.net.NetworkInterface"); return 
/* 219 */         (networkInterfaceClass.getMethod("getByName", (Class[])null).invoke(networkInterfaceClass, new Object[] { hostname }) != null);
/* 220 */     } catch (Throwable t) {
/* 221 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
/* 226 */     Map<Object, Object> diffMap = new HashMap<>();
/*     */     
/* 228 */     for (Map.Entry<?, ?> entry : map1.entrySet()) {
/* 229 */       Object key = entry.getKey();
/*     */       
/* 231 */       Number value1 = null;
/* 232 */       Number value2 = null;
/*     */       
/* 234 */       if (entry.getValue() instanceof Number) {
/*     */         
/* 236 */         value1 = (Number)entry.getValue();
/* 237 */         value2 = (Number)map2.get(key);
/*     */       } else {
/*     */         try {
/* 240 */           value1 = new Double(entry.getValue().toString());
/* 241 */           value2 = new Double(map2.get(key).toString());
/* 242 */         } catch (NumberFormatException nfe) {
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */       
/* 247 */       if (value1.equals(value2)) {
/*     */         continue;
/*     */       }
/*     */       
/* 251 */       if (value1 instanceof Byte) {
/* 252 */         diffMap.put(key, Byte.valueOf((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue()))); continue;
/* 253 */       }  if (value1 instanceof Short) {
/* 254 */         diffMap.put(key, Short.valueOf((short)(((Short)value2).shortValue() - ((Short)value1).shortValue()))); continue;
/* 255 */       }  if (value1 instanceof Integer) {
/* 256 */         diffMap.put(key, Integer.valueOf(((Integer)value2).intValue() - ((Integer)value1).intValue())); continue;
/* 257 */       }  if (value1 instanceof Long) {
/* 258 */         diffMap.put(key, Long.valueOf(((Long)value2).longValue() - ((Long)value1).longValue())); continue;
/* 259 */       }  if (value1 instanceof Float) {
/* 260 */         diffMap.put(key, Float.valueOf(((Float)value2).floatValue() - ((Float)value1).floatValue())); continue;
/* 261 */       }  if (value1 instanceof Double) {
/* 262 */         diffMap.put(key, Double.valueOf((((Double)value2).shortValue() - ((Double)value1).shortValue()))); continue;
/* 263 */       }  if (value1 instanceof BigDecimal) {
/* 264 */         diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1)); continue;
/* 265 */       }  if (value1 instanceof BigInteger) {
/* 266 */         diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1));
/*     */       }
/*     */     } 
/*     */     
/* 270 */     return diffMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> List<T> loadClasses(String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor) {
/* 275 */     List<T> instances = new LinkedList<>();
/*     */     
/* 277 */     List<String> interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
/*     */     
/* 279 */     String className = null;
/*     */     
/*     */     try {
/* 282 */       for (int i = 0, s = interceptorsToCreate.size(); i < s; i++) {
/* 283 */         className = interceptorsToCreate.get(i);
/*     */         
/* 285 */         T instance = (T)Class.forName(className).newInstance();
/*     */         
/* 287 */         instances.add(instance);
/*     */       }
/*     */     
/* 290 */     } catch (Throwable t) {
/* 291 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString(errorMessageKey, new Object[] { className }), t, exceptionInterceptor);
/*     */     } 
/*     */ 
/*     */     
/* 295 */     return instances;
/*     */   }
/*     */ 
/*     */   
/* 299 */   private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJdbcInterface(Class<?> clazz) {
/* 309 */     if (isJdbcInterfaceCache.containsKey(clazz)) {
/* 310 */       return ((Boolean)isJdbcInterfaceCache.get(clazz)).booleanValue();
/*     */     }
/*     */     
/* 313 */     if (clazz.isInterface()) {
/*     */       try {
/* 315 */         if (isJdbcPackage(clazz.getPackage().getName())) {
/* 316 */           isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 317 */           return true;
/*     */         } 
/* 319 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 327 */     for (Class<?> iface : clazz.getInterfaces()) {
/* 328 */       if (isJdbcInterface(iface)) {
/* 329 */         isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 330 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 334 */     if (clazz.getSuperclass() != null && isJdbcInterface(clazz.getSuperclass())) {
/* 335 */       isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 336 */       return true;
/*     */     } 
/*     */     
/* 339 */     isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(false));
/* 340 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isJdbcPackage(String packageName) {
/* 351 */     return (packageName != null && (packageName
/* 352 */       .startsWith("java.sql") || packageName.startsWith("javax.sql") || packageName.startsWith("com.mysql.cj.jdbc")));
/*     */   }
/*     */ 
/*     */   
/* 356 */   private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = (ConcurrentMap)new ConcurrentHashMap<>();
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
/*     */   public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
/* 368 */     Class<?>[] implementedInterfaces = implementedInterfacesCache.get(clazz);
/* 369 */     if (implementedInterfaces != null) {
/* 370 */       return implementedInterfaces;
/*     */     }
/*     */     
/* 373 */     Set<Class<?>> interfaces = new LinkedHashSet<>();
/* 374 */     Class<?> superClass = clazz;
/*     */     do {
/* 376 */       Collections.addAll(interfaces, superClass.getInterfaces());
/* 377 */     } while ((superClass = superClass.getSuperclass()) != null);
/*     */     
/* 379 */     implementedInterfaces = (Class[])interfaces.<Class<?>[]>toArray((Class<?>[][])new Class[interfaces.size()]);
/* 380 */     Class<?>[] oldValue = implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
/* 381 */     if (oldValue != null) {
/* 382 */       implementedInterfaces = oldValue;
/*     */     }
/*     */     
/* 385 */     return implementedInterfaces;
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
/*     */   public static long secondsSinceMillis(long timeInMillis) {
/* 397 */     return (System.currentTimeMillis() - timeInMillis) / 1000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int truncateAndConvertToInt(long longValue) {
/* 408 */     return (longValue > 2147483647L) ? Integer.MAX_VALUE : ((longValue < -2147483648L) ? Integer.MIN_VALUE : (int)longValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] truncateAndConvertToInt(long[] longArray) {
/* 419 */     int[] intArray = new int[longArray.length];
/*     */     
/* 421 */     for (int i = 0; i < longArray.length; i++) {
/* 422 */       intArray[i] = (longArray[i] > 2147483647L) ? Integer.MAX_VALUE : ((longArray[i] < -2147483648L) ? Integer.MIN_VALUE : (int)longArray[i]);
/*     */     }
/* 424 */     return intArray;
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
/*     */   public static String getPackageName(Class<?> clazz) {
/* 436 */     String fqcn = clazz.getName();
/* 437 */     int classNameStartsAt = fqcn.lastIndexOf('.');
/* 438 */     if (classNameStartsAt > 0) {
/* 439 */       return fqcn.substring(0, classNameStartsAt);
/*     */     }
/* 441 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isRunningOnWindows() {
/* 451 */     return (StringUtils.indexOfIgnoreCase(Constants.OS_NAME, "WINDOWS") != -1);
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
/*     */   public static int readFully(Reader reader, char[] buf, int length) throws IOException {
/* 471 */     int numCharsRead = 0;
/*     */     
/* 473 */     while (numCharsRead < length) {
/* 474 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*     */       
/* 476 */       if (count < 0) {
/*     */         break;
/*     */       }
/*     */       
/* 480 */       numCharsRead += count;
/*     */     } 
/*     */     
/* 483 */     return numCharsRead;
/*     */   }
/*     */   
/*     */   public static final int readBlock(InputStream i, byte[] b, ExceptionInterceptor exceptionInterceptor) {
/*     */     try {
/* 488 */       return i.read(b);
/* 489 */     } catch (Throwable ex) {
/* 490 */       throw ExceptionFactory.createException(Messages.getString("Util.5") + ex.getClass().getName(), exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final int readBlock(InputStream i, byte[] b, int length, ExceptionInterceptor exceptionInterceptor) {
/*     */     try {
/* 496 */       int lengthToRead = length;
/*     */       
/* 498 */       if (lengthToRead > b.length) {
/* 499 */         lengthToRead = b.length;
/*     */       }
/*     */       
/* 502 */       return i.read(b, 0, lengthToRead);
/* 503 */     } catch (Throwable ex) {
/* 504 */       throw ExceptionFactory.createException(Messages.getString("Util.5") + ex.getClass().getName(), exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */