/*     */ package cn.hutool.core.exceptions;
/*     */ 
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class ExceptionUtil
/*     */ {
/*     */   public static String getMessage(Throwable e) {
/*  31 */     if (null == e) {
/*  32 */       return "null";
/*     */     }
/*  34 */     return StrUtil.format("{}: {}", new Object[] { e.getClass().getSimpleName(), e.getMessage() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSimpleMessage(Throwable e) {
/*  44 */     return (null == e) ? "null" : e.getMessage();
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
/*     */   public static RuntimeException wrapRuntime(Throwable throwable) {
/*  56 */     if (throwable instanceof RuntimeException) {
/*  57 */       return (RuntimeException)throwable;
/*     */     }
/*  59 */     return new RuntimeException(throwable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeException wrapRuntime(String message) {
/*  70 */     return new RuntimeException(message);
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
/*     */   public static <T extends Throwable> T wrap(Throwable throwable, Class<T> wrapThrowable) {
/*  84 */     if (wrapThrowable.isInstance(throwable)) {
/*  85 */       return (T)throwable;
/*     */     }
/*  87 */     return (T)ReflectUtil.newInstance(wrapThrowable, new Object[] { throwable });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void wrapAndThrow(Throwable throwable) {
/*  97 */     if (throwable instanceof RuntimeException) {
/*  98 */       throw (RuntimeException)throwable;
/*     */     }
/* 100 */     if (throwable instanceof Error) {
/* 101 */       throw (Error)throwable;
/*     */     }
/* 103 */     throw new UndeclaredThrowableException(throwable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void wrapRuntimeAndThrow(String message) {
/* 113 */     throw new RuntimeException(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable unwrap(Throwable wrapped) {
/* 123 */     Throwable unwrapped = wrapped;
/*     */     while (true) {
/* 125 */       while (unwrapped instanceof InvocationTargetException)
/* 126 */         unwrapped = ((InvocationTargetException)unwrapped).getTargetException(); 
/* 127 */       if (unwrapped instanceof UndeclaredThrowableException) {
/* 128 */         unwrapped = ((UndeclaredThrowableException)unwrapped).getUndeclaredThrowable(); continue;
/*     */       }  break;
/* 130 */     }  return unwrapped;
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
/*     */   public static StackTraceElement[] getStackElements() {
/* 142 */     return Thread.currentThread().getStackTrace();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement getStackElement(int i) {
/* 153 */     return Thread.currentThread().getStackTrace()[i];
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
/*     */   public static StackTraceElement getStackElement(String fqcn, int i) {
/* 165 */     StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
/* 166 */     int index = ArrayUtil.matchIndex(ele -> StrUtil.equals(fqcn, ele.getClassName()), (Object[])stackTraceArray);
/* 167 */     if (index > 0) {
/* 168 */       return stackTraceArray[index + i];
/*     */     }
/*     */     
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement getRootStackElement() {
/* 181 */     StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
/* 182 */     return Thread.currentThread().getStackTrace()[stackElements.length - 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stacktraceToOneLineString(Throwable throwable) {
/* 192 */     return stacktraceToOneLineString(throwable, 3000);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stacktraceToOneLineString(Throwable throwable, int limit) {
/* 203 */     Map<Character, String> replaceCharToStrMap = new HashMap<>();
/* 204 */     replaceCharToStrMap.put(Character.valueOf('\r'), " ");
/* 205 */     replaceCharToStrMap.put(Character.valueOf('\n'), " ");
/* 206 */     replaceCharToStrMap.put(Character.valueOf('\t'), " ");
/*     */     
/* 208 */     return stacktraceToString(throwable, limit, replaceCharToStrMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stacktraceToString(Throwable throwable) {
/* 218 */     return stacktraceToString(throwable, 3000);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stacktraceToString(Throwable throwable, int limit) {
/* 229 */     return stacktraceToString(throwable, limit, null);
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
/*     */   public static String stacktraceToString(Throwable throwable, int limit, Map<Character, String> replaceCharToStrMap) {
/* 241 */     FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
/* 242 */     throwable.printStackTrace(new PrintStream((OutputStream)baos));
/*     */     
/* 244 */     String exceptionStr = baos.toString();
/* 245 */     int length = exceptionStr.length();
/* 246 */     if (limit < 0 || limit > length) {
/* 247 */       limit = length;
/*     */     }
/*     */     
/* 250 */     if (MapUtil.isNotEmpty(replaceCharToStrMap)) {
/* 251 */       StringBuilder sb = StrUtil.builder();
/*     */ 
/*     */       
/* 254 */       for (int i = 0; i < limit; i++) {
/* 255 */         char c = exceptionStr.charAt(i);
/* 256 */         String value = replaceCharToStrMap.get(Character.valueOf(c));
/* 257 */         if (null != value) {
/* 258 */           sb.append(value);
/*     */         } else {
/* 260 */           sb.append(c);
/*     */         } 
/*     */       } 
/* 263 */       return sb.toString();
/*     */     } 
/* 265 */     if (limit == length) {
/* 266 */       return exceptionStr;
/*     */     }
/* 268 */     return StrUtil.subPre(exceptionStr, limit);
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
/*     */   public static boolean isCausedBy(Throwable throwable, Class<? extends Exception>... causeClasses) {
/* 282 */     return (null != getCausedBy(throwable, causeClasses));
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
/*     */   public static Throwable getCausedBy(Throwable throwable, Class<? extends Exception>... causeClasses) {
/* 295 */     Throwable cause = throwable;
/* 296 */     while (cause != null) {
/* 297 */       for (Class<? extends Exception> causeClass : causeClasses) {
/* 298 */         if (causeClass.isInstance(cause)) {
/* 299 */           return cause;
/*     */         }
/*     */       } 
/* 302 */       cause = cause.getCause();
/*     */     } 
/* 304 */     return null;
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
/*     */   public static boolean isFromOrSuppressedThrowable(Throwable throwable, Class<? extends Throwable> exceptionClass) {
/* 316 */     return (convertFromOrSuppressedThrowable(throwable, exceptionClass, true) != null);
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
/*     */   public static boolean isFromOrSuppressedThrowable(Throwable throwable, Class<? extends Throwable> exceptionClass, boolean checkCause) {
/* 329 */     return (convertFromOrSuppressedThrowable(throwable, exceptionClass, checkCause) != null);
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
/*     */   public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable throwable, Class<T> exceptionClass) {
/* 342 */     return convertFromOrSuppressedThrowable(throwable, exceptionClass, true);
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
/*     */   public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable throwable, Class<T> exceptionClass, boolean checkCause) {
/* 357 */     if (throwable == null || exceptionClass == null) {
/* 358 */       return null;
/*     */     }
/* 360 */     if (exceptionClass.isAssignableFrom(throwable.getClass())) {
/* 361 */       return (T)throwable;
/*     */     }
/* 363 */     if (checkCause) {
/* 364 */       Throwable cause = throwable.getCause();
/* 365 */       if (cause != null && exceptionClass.isAssignableFrom(cause.getClass())) {
/* 366 */         return (T)cause;
/*     */       }
/*     */     } 
/* 369 */     Throwable[] throwables = throwable.getSuppressed();
/* 370 */     if (ArrayUtil.isNotEmpty((Object[])throwables)) {
/* 371 */       for (Throwable throwable1 : throwables) {
/* 372 */         if (exceptionClass.isAssignableFrom(throwable1.getClass())) {
/* 373 */           return (T)throwable1;
/*     */         }
/*     */       } 
/*     */     }
/* 377 */     return null;
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
/*     */   public static List<Throwable> getThrowableList(Throwable throwable) {
/* 393 */     List<Throwable> list = new ArrayList<>();
/* 394 */     while (throwable != null && false == list.contains(throwable)) {
/* 395 */       list.add(throwable);
/* 396 */       throwable = throwable.getCause();
/*     */     } 
/* 398 */     return list;
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
/*     */   public static Throwable getRootCause(Throwable throwable) {
/* 414 */     List<Throwable> list = getThrowableList(throwable);
/* 415 */     return (list.size() < 1) ? null : list.get(list.size() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getRootCauseMessage(Throwable th) {
/* 426 */     return getMessage(getRootCause(th));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\ExceptionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */