/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.management.GarbageCollectorMXBean;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.OperatingSystemMXBean;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ public class Utils
/*     */ {
/*  32 */   public static final byte[] EMPTY_BYTES = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   public static final int[] EMPTY_INT_ARRAY = new int[0];
/*     */   
/*  39 */   private static final HashMap<String, byte[]> RESOURCES = (HashMap)new HashMap<>();
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
/*     */   public static int indexOf(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/*  57 */     if (paramArrayOfbyte2.length == 0) {
/*  58 */       return paramInt;
/*     */     }
/*  60 */     if (paramInt > paramArrayOfbyte1.length) {
/*  61 */       return -1;
/*     */     }
/*  63 */     int i = paramArrayOfbyte1.length - paramArrayOfbyte2.length + 1;
/*  64 */     int j = paramArrayOfbyte2.length;
/*     */     
/*  66 */     for (; paramInt < i; paramInt++) {
/*  67 */       byte b = 0; while (true) { if (b < j) {
/*  68 */           if (paramArrayOfbyte1[paramInt + b] != paramArrayOfbyte2[b])
/*     */             break;  b++;
/*     */           continue;
/*     */         } 
/*  72 */         return paramInt; }
/*     */     
/*  74 */     }  return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getByteArrayHash(byte[] paramArrayOfbyte) {
/*  84 */     int i = paramArrayOfbyte.length;
/*  85 */     int j = i;
/*  86 */     if (i < 50) {
/*  87 */       for (byte b = 0; b < i; b++) {
/*  88 */         j = 31 * j + paramArrayOfbyte[b];
/*     */       }
/*     */     } else {
/*  91 */       int k = i / 16; int m;
/*  92 */       for (m = 0; m < 4; m++) {
/*  93 */         j = 31 * j + paramArrayOfbyte[m];
/*  94 */         j = 31 * j + paramArrayOfbyte[--i];
/*     */       } 
/*  96 */       for (m = 4 + k; m < i; m += k) {
/*  97 */         j = 31 * j + paramArrayOfbyte[m];
/*     */       }
/*     */     } 
/* 100 */     return j;
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
/*     */   public static boolean compareSecure(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/* 113 */     if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null) {
/* 114 */       return (paramArrayOfbyte1 == null && paramArrayOfbyte2 == null);
/*     */     }
/* 116 */     int i = paramArrayOfbyte1.length;
/* 117 */     if (i != paramArrayOfbyte2.length) {
/* 118 */       return false;
/*     */     }
/* 120 */     if (i == 0) {
/* 121 */       return true;
/*     */     }
/*     */     
/* 124 */     int j = 0;
/* 125 */     for (byte b = 0; b < i; b++)
/*     */     {
/* 127 */       j |= paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b];
/*     */     }
/* 129 */     return (j == 0);
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
/*     */   public static byte[] copy(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/* 141 */     int i = paramArrayOfbyte1.length;
/* 142 */     if (i > paramArrayOfbyte2.length) {
/* 143 */       paramArrayOfbyte2 = new byte[i];
/*     */     }
/* 145 */     System.arraycopy(paramArrayOfbyte1, 0, paramArrayOfbyte2, 0, i);
/* 146 */     return paramArrayOfbyte2;
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
/*     */   public static byte[] newBytes(int paramInt) {
/* 164 */     if (paramInt == 0) {
/* 165 */       return EMPTY_BYTES;
/*     */     }
/*     */     try {
/* 168 */       return new byte[paramInt];
/* 169 */     } catch (OutOfMemoryError outOfMemoryError1) {
/* 170 */       OutOfMemoryError outOfMemoryError2 = new OutOfMemoryError("Requested memory: " + paramInt);
/* 171 */       outOfMemoryError2.initCause(outOfMemoryError1);
/* 172 */       throw outOfMemoryError2;
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
/*     */   public static byte[] copyBytes(byte[] paramArrayOfbyte, int paramInt) {
/* 193 */     if (paramInt == 0) {
/* 194 */       return EMPTY_BYTES;
/*     */     }
/*     */     try {
/* 197 */       return Arrays.copyOf(paramArrayOfbyte, paramInt);
/* 198 */     } catch (OutOfMemoryError outOfMemoryError1) {
/* 199 */       OutOfMemoryError outOfMemoryError2 = new OutOfMemoryError("Requested memory: " + paramInt);
/* 200 */       outOfMemoryError2.initCause(outOfMemoryError1);
/* 201 */       throw outOfMemoryError2;
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
/*     */   public static byte[] cloneByteArray(byte[] paramArrayOfbyte) {
/* 213 */     if (paramArrayOfbyte == null) {
/* 214 */       return null;
/*     */     }
/* 216 */     int i = paramArrayOfbyte.length;
/* 217 */     if (i == 0) {
/* 218 */       return EMPTY_BYTES;
/*     */     }
/* 220 */     return Arrays.copyOf(paramArrayOfbyte, i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getMemoryUsed() {
/* 230 */     collectGarbage();
/* 231 */     Runtime runtime = Runtime.getRuntime();
/* 232 */     return runtime.totalMemory() - runtime.freeMemory() >> 10L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getMemoryFree() {
/* 242 */     collectGarbage();
/* 243 */     return Runtime.getRuntime().freeMemory() >> 10L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getMemoryMax() {
/* 252 */     return Runtime.getRuntime().maxMemory() >> 10L;
/*     */   }
/*     */   
/*     */   public static long getGarbageCollectionTime() {
/* 256 */     long l = 0L;
/* 257 */     for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
/* 258 */       long l1 = garbageCollectorMXBean.getCollectionTime();
/* 259 */       if (l1 > 0L) {
/* 260 */         l += l1;
/*     */       }
/*     */     } 
/* 263 */     return l;
/*     */   }
/*     */   
/*     */   public static long getGarbageCollectionCount() {
/* 267 */     long l = 0L;
/* 268 */     int i = 0;
/* 269 */     for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
/* 270 */       long l1 = garbageCollectorMXBean.getCollectionTime();
/* 271 */       if (l1 > 0L) {
/* 272 */         l += l1;
/* 273 */         i += (garbageCollectorMXBean.getMemoryPoolNames()).length;
/*     */       } 
/*     */     } 
/* 276 */     i = Math.max(i, 1);
/* 277 */     return (l + (i >> 1)) / i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void collectGarbage() {
/* 284 */     Runtime runtime = Runtime.getRuntime();
/* 285 */     long l = getGarbageCollectionCount();
/* 286 */     while (l == getGarbageCollectionCount()) {
/* 287 */       runtime.gc();
/* 288 */       Thread.yield();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ArrayList<T> newSmallArrayList() {
/* 299 */     return new ArrayList<>(4);
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
/*     */   public static <X> void sortTopN(X[] paramArrayOfX, int paramInt1, int paramInt2, Comparator<? super X> paramComparator) {
/* 313 */     int i = paramArrayOfX.length - 1;
/* 314 */     if (i > 0 && paramInt2 > paramInt1) {
/* 315 */       partialQuickSort(paramArrayOfX, 0, i, paramComparator, paramInt1, paramInt2 - 1);
/* 316 */       Arrays.sort(paramArrayOfX, paramInt1, paramInt2, paramComparator);
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
/*     */   private static <X> void partialQuickSort(X[] paramArrayOfX, int paramInt1, int paramInt2, Comparator<? super X> paramComparator, int paramInt3, int paramInt4) {
/* 342 */     if (paramInt1 >= paramInt3 && paramInt2 <= paramInt4) {
/*     */       return;
/*     */     }
/*     */     
/* 346 */     int i = paramInt1, j = paramInt2;
/*     */ 
/*     */     
/* 349 */     int k = paramInt1 + MathUtils.randomInt(paramInt2 - paramInt1);
/* 350 */     X x1 = paramArrayOfX[k];
/* 351 */     int m = paramInt1 + paramInt2 >>> 1;
/* 352 */     X x2 = paramArrayOfX[m];
/* 353 */     paramArrayOfX[m] = x1;
/* 354 */     paramArrayOfX[k] = x2;
/* 355 */     while (i <= j) {
/* 356 */       while (paramComparator.compare(paramArrayOfX[i], x1) < 0) {
/* 357 */         i++;
/*     */       }
/* 359 */       while (paramComparator.compare(paramArrayOfX[j], x1) > 0) {
/* 360 */         j--;
/*     */       }
/* 362 */       if (i <= j) {
/* 363 */         x2 = paramArrayOfX[i];
/* 364 */         paramArrayOfX[i++] = paramArrayOfX[j];
/* 365 */         paramArrayOfX[j--] = x2;
/*     */       } 
/*     */     } 
/* 368 */     if (paramInt1 < j && paramInt3 <= j) {
/* 369 */       partialQuickSort(paramArrayOfX, paramInt1, j, paramComparator, paramInt3, paramInt4);
/*     */     }
/* 371 */     if (i < paramInt2 && i <= paramInt4) {
/* 372 */       partialQuickSort(paramArrayOfX, i, paramInt2, paramComparator, paramInt3, paramInt4);
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
/*     */   public static byte[] getResource(String paramString) throws IOException {
/* 384 */     byte[] arrayOfByte = RESOURCES.get(paramString);
/* 385 */     if (arrayOfByte == null) {
/* 386 */       arrayOfByte = loadResource(paramString);
/* 387 */       if (arrayOfByte != null) {
/* 388 */         RESOURCES.put(paramString, arrayOfByte);
/*     */       }
/*     */     } 
/* 391 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   private static byte[] loadResource(String paramString) throws IOException {
/* 395 */     InputStream inputStream = Utils.class.getResourceAsStream("data.zip");
/* 396 */     if (inputStream == null) {
/* 397 */       inputStream = Utils.class.getResourceAsStream(paramString);
/* 398 */       if (inputStream == null) {
/* 399 */         return null;
/*     */       }
/* 401 */       return IOUtils.readBytesAndClose(inputStream, 0);
/*     */     } 
/*     */     
/* 404 */     try (ZipInputStream null = new ZipInputStream(inputStream)) {
/*     */       while (true) {
/* 406 */         ZipEntry zipEntry = zipInputStream.getNextEntry();
/* 407 */         if (zipEntry == null) {
/*     */           break;
/*     */         }
/* 410 */         String str = zipEntry.getName();
/* 411 */         if (!str.startsWith("/")) {
/* 412 */           str = "/" + str;
/*     */         }
/* 414 */         if (str.equals(paramString)) {
/* 415 */           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 416 */           IOUtils.copy(zipInputStream, byteArrayOutputStream);
/* 417 */           zipInputStream.closeEntry();
/* 418 */           return byteArrayOutputStream.toByteArray();
/*     */         } 
/* 420 */         zipInputStream.closeEntry();
/*     */       } 
/* 422 */     } catch (IOException iOException) {
/*     */       
/* 424 */       iOException.printStackTrace();
/*     */     } 
/* 426 */     return null;
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
/*     */   public static Object callStaticMethod(String paramString, Object... paramVarArgs) throws Exception {
/* 442 */     int i = paramString.lastIndexOf('.');
/* 443 */     String str1 = paramString.substring(0, i);
/* 444 */     String str2 = paramString.substring(i + 1);
/* 445 */     return callMethod(null, Class.forName(str1), str2, paramVarArgs);
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
/*     */   public static Object callMethod(Object paramObject, String paramString, Object... paramVarArgs) throws Exception {
/* 463 */     return callMethod(paramObject, paramObject.getClass(), paramString, paramVarArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object callMethod(Object paramObject, Class<?> paramClass, String paramString, Object... paramVarArgs) throws Exception {
/* 470 */     Method method = null;
/* 471 */     int i = 0;
/* 472 */     boolean bool = (paramObject == null);
/* 473 */     for (Method method1 : paramClass.getMethods()) {
/* 474 */       if (Modifier.isStatic(method1.getModifiers()) == bool && method1
/* 475 */         .getName().equals(paramString)) {
/* 476 */         int j = match(method1.getParameterTypes(), paramVarArgs);
/* 477 */         if (j > i) {
/* 478 */           i = j;
/* 479 */           method = method1;
/*     */         } 
/*     */       } 
/*     */     } 
/* 483 */     if (method == null) {
/* 484 */       throw new NoSuchMethodException(paramString);
/*     */     }
/* 486 */     return method.invoke(paramObject, paramVarArgs);
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
/*     */   public static Object newInstance(String paramString, Object... paramVarArgs) throws Exception {
/* 501 */     Constructor<?> constructor = null;
/* 502 */     int i = 0;
/* 503 */     for (Constructor<?> constructor1 : Class.forName(paramString).getConstructors()) {
/* 504 */       int j = match(constructor1.getParameterTypes(), paramVarArgs);
/* 505 */       if (j > i) {
/* 506 */         i = j;
/* 507 */         constructor = constructor1;
/*     */       } 
/*     */     } 
/* 510 */     if (constructor == null) {
/* 511 */       throw new NoSuchMethodException(paramString);
/*     */     }
/* 513 */     return constructor.newInstance(paramVarArgs);
/*     */   }
/*     */   
/*     */   private static int match(Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject) {
/* 517 */     int i = paramArrayOfClass.length;
/* 518 */     if (i == paramArrayOfObject.length) {
/* 519 */       byte b1 = 1;
/* 520 */       for (byte b2 = 0; b2 < i; b2++) {
/* 521 */         Class<?> clazz1 = getNonPrimitiveClass(paramArrayOfClass[b2]);
/* 522 */         Object object = paramArrayOfObject[b2];
/* 523 */         Class<?> clazz2 = (object == null) ? null : object.getClass();
/* 524 */         if (clazz1 == clazz2) {
/* 525 */           b1++;
/* 526 */         } else if (clazz2 != null) {
/*     */           
/* 528 */           if (!clazz1.isAssignableFrom(clazz2))
/* 529 */             return 0; 
/*     */         } 
/*     */       } 
/* 532 */       return b1;
/*     */     } 
/* 534 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getNonPrimitiveClass(Class<?> paramClass) {
/* 544 */     if (!paramClass.isPrimitive())
/* 545 */       return paramClass; 
/* 546 */     if (paramClass == boolean.class)
/* 547 */       return Boolean.class; 
/* 548 */     if (paramClass == byte.class)
/* 549 */       return Byte.class; 
/* 550 */     if (paramClass == char.class)
/* 551 */       return Character.class; 
/* 552 */     if (paramClass == double.class)
/* 553 */       return Double.class; 
/* 554 */     if (paramClass == float.class)
/* 555 */       return Float.class; 
/* 556 */     if (paramClass == int.class)
/* 557 */       return Integer.class; 
/* 558 */     if (paramClass == long.class)
/* 559 */       return Long.class; 
/* 560 */     if (paramClass == short.class)
/* 561 */       return Short.class; 
/* 562 */     if (paramClass == void.class) {
/* 563 */       return Void.class;
/*     */     }
/* 565 */     return paramClass;
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
/*     */   public static boolean parseBoolean(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/* 582 */     if (paramString == null) {
/* 583 */       return paramBoolean1;
/*     */     }
/* 585 */     switch (paramString.length()) {
/*     */       case 1:
/* 587 */         if (paramString.equals("1") || paramString.equalsIgnoreCase("t") || paramString.equalsIgnoreCase("y")) {
/* 588 */           return true;
/*     */         }
/* 590 */         if (paramString.equals("0") || paramString.equalsIgnoreCase("f") || paramString.equalsIgnoreCase("n")) {
/* 591 */           return false;
/*     */         }
/*     */         break;
/*     */       case 2:
/* 595 */         if (paramString.equalsIgnoreCase("no")) {
/* 596 */           return false;
/*     */         }
/*     */         break;
/*     */       case 3:
/* 600 */         if (paramString.equalsIgnoreCase("yes")) {
/* 601 */           return true;
/*     */         }
/*     */         break;
/*     */       case 4:
/* 605 */         if (paramString.equalsIgnoreCase("true")) {
/* 606 */           return true;
/*     */         }
/*     */         break;
/*     */       case 5:
/* 610 */         if (paramString.equalsIgnoreCase("false"))
/* 611 */           return false; 
/*     */         break;
/*     */     } 
/* 614 */     if (paramBoolean2) {
/* 615 */       throw new IllegalArgumentException(paramString);
/*     */     }
/* 617 */     return paramBoolean1;
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
/*     */   public static String getProperty(String paramString1, String paramString2) {
/*     */     try {
/* 630 */       return System.getProperty(paramString1, paramString2);
/* 631 */     } catch (SecurityException securityException) {
/* 632 */       return paramString2;
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
/*     */   public static int getProperty(String paramString, int paramInt) {
/* 645 */     String str = getProperty(paramString, (String)null);
/* 646 */     if (str != null) {
/*     */       try {
/* 648 */         return Integer.decode(str).intValue();
/* 649 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/* 653 */     return paramInt;
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
/*     */   public static boolean getProperty(String paramString, boolean paramBoolean) {
/* 665 */     return parseBoolean(getProperty(paramString, (String)null), paramBoolean, false);
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
/*     */   public static int scaleForAvailableMemory(int paramInt) {
/* 677 */     long l = Runtime.getRuntime().maxMemory();
/* 678 */     if (l != Long.MAX_VALUE)
/*     */     {
/* 680 */       return (int)(paramInt * l / 1073741824L);
/*     */     }
/*     */     
/*     */     try {
/* 684 */       OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 691 */       Method method = Class.forName("com.sun.management.OperatingSystemMXBean").getMethod("getTotalPhysicalMemorySize", new Class[0]);
/* 692 */       long l1 = ((Number)method.invoke(operatingSystemMXBean, new Object[0])).longValue();
/* 693 */       return (int)(paramInt * l1 / 1073741824L);
/* 694 */     } catch (Exception exception) {
/*     */     
/* 696 */     } catch (Error error) {}
/*     */ 
/*     */     
/* 699 */     return paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long currentNanoTime() {
/* 709 */     long l = System.nanoTime();
/* 710 */     if (l == 0L) {
/* 711 */       l = 1L;
/*     */     }
/* 713 */     return l;
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
/*     */   public static long currentNanoTimePlusMillis(int paramInt) {
/* 726 */     return nanoTimePlusMillis(System.nanoTime(), paramInt);
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
/*     */   public static long nanoTimePlusMillis(long paramLong, int paramInt) {
/* 741 */     long l = paramLong + paramInt * 1000000L;
/* 742 */     if (l == 0L) {
/* 743 */       l = 1L;
/*     */     }
/* 745 */     return l;
/*     */   }
/*     */   
/*     */   public static interface ClassFactory {
/*     */     boolean match(String param1String);
/*     */     
/*     */     Class<?> loadClass(String param1String) throws ClassNotFoundException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */