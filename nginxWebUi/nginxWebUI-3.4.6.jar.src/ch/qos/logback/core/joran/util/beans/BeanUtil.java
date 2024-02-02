/*     */ package ch.qos.logback.core.joran.util.beans;
/*     */ 
/*     */ import java.lang.reflect.Method;
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
/*     */ public class BeanUtil
/*     */ {
/*     */   public static final String PREFIX_GETTER_IS = "is";
/*     */   public static final String PREFIX_GETTER_GET = "get";
/*     */   public static final String PREFIX_SETTER = "set";
/*     */   public static final String PREFIX_ADDER = "add";
/*     */   
/*     */   public static boolean isAdder(Method method) {
/*  24 */     int parameterCount = getParameterCount(method);
/*  25 */     if (parameterCount != 1) {
/*  26 */       return false;
/*     */     }
/*  28 */     Class<?> returnType = method.getReturnType();
/*  29 */     if (returnType != void.class) {
/*  30 */       return false;
/*     */     }
/*  32 */     String methodName = method.getName();
/*  33 */     return methodName.startsWith("add");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isGetter(Method method) {
/*  42 */     int parameterCount = getParameterCount(method);
/*  43 */     if (parameterCount > 0) {
/*  44 */       return false;
/*     */     }
/*  46 */     Class<?> returnType = method.getReturnType();
/*  47 */     if (returnType == void.class) {
/*  48 */       return false;
/*     */     }
/*  50 */     String methodName = method.getName();
/*  51 */     if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
/*  52 */       return false;
/*     */     }
/*  54 */     if (methodName.startsWith("is") && 
/*  55 */       !returnType.equals(boolean.class) && !returnType.equals(Boolean.class)) {
/*  56 */       return false;
/*     */     }
/*     */     
/*  59 */     return true;
/*     */   }
/*     */   
/*     */   private static int getParameterCount(Method method) {
/*  63 */     return (method.getParameterTypes()).length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSetter(Method method) {
/*  72 */     int parameterCount = getParameterCount(method);
/*  73 */     if (parameterCount != 1) {
/*  74 */       return false;
/*     */     }
/*  76 */     Class<?> returnType = method.getReturnType();
/*  77 */     if (returnType != void.class) {
/*  78 */       return false;
/*     */     }
/*  80 */     String methodName = method.getName();
/*  81 */     if (!methodName.startsWith("set")) {
/*  82 */       return false;
/*     */     }
/*  84 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPropertyName(Method method) {
/*  92 */     String methodName = method.getName();
/*  93 */     String rawPropertyName = getSubstringIfPrefixMatches(methodName, "get");
/*  94 */     if (rawPropertyName == null) {
/*  95 */       rawPropertyName = getSubstringIfPrefixMatches(methodName, "set");
/*     */     }
/*  97 */     if (rawPropertyName == null) {
/*  98 */       rawPropertyName = getSubstringIfPrefixMatches(methodName, "is");
/*     */     }
/* 100 */     if (rawPropertyName == null) {
/* 101 */       rawPropertyName = getSubstringIfPrefixMatches(methodName, "add");
/*     */     }
/* 103 */     return toLowerCamelCase(rawPropertyName);
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
/*     */   public static String toLowerCamelCase(String string) {
/* 115 */     if (string == null) {
/* 116 */       return null;
/*     */     }
/* 118 */     if (string.isEmpty()) {
/* 119 */       return string;
/*     */     }
/* 121 */     if (string.length() > 1 && Character.isUpperCase(string.charAt(1)) && Character.isUpperCase(string.charAt(0))) {
/* 122 */       return string;
/*     */     }
/* 124 */     char[] chars = string.toCharArray();
/* 125 */     chars[0] = Character.toLowerCase(chars[0]);
/* 126 */     return new String(chars);
/*     */   }
/*     */   
/*     */   private static String getSubstringIfPrefixMatches(String wholeString, String prefix) {
/* 130 */     if (wholeString.startsWith(prefix)) {
/* 131 */       return wholeString.substring(prefix.length());
/*     */     }
/* 133 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\jora\\util\beans\BeanUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */