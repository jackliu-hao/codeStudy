/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import sun.misc.Unsafe;
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
/*     */ final class UnsafeUtil
/*     */ {
/*  44 */   private static final Logger logger = Logger.getLogger(UnsafeUtil.class.getName());
/*  45 */   private static final Unsafe UNSAFE = getUnsafe();
/*  46 */   private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
/*  47 */   private static final boolean IS_ANDROID_64 = determineAndroidSupportByAddressSize(long.class);
/*  48 */   private static final boolean IS_ANDROID_32 = determineAndroidSupportByAddressSize(int.class);
/*  49 */   private static final MemoryAccessor MEMORY_ACCESSOR = getMemoryAccessor();
/*     */   
/*  51 */   private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS = supportsUnsafeByteBufferOperations();
/*  52 */   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = supportsUnsafeArrayOperations();
/*     */   
/*  54 */   static final long BYTE_ARRAY_BASE_OFFSET = arrayBaseOffset(byte[].class);
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final long BOOLEAN_ARRAY_BASE_OFFSET = arrayBaseOffset(boolean[].class);
/*  59 */   private static final long BOOLEAN_ARRAY_INDEX_SCALE = arrayIndexScale(boolean[].class);
/*     */   
/*  61 */   private static final long INT_ARRAY_BASE_OFFSET = arrayBaseOffset(int[].class);
/*  62 */   private static final long INT_ARRAY_INDEX_SCALE = arrayIndexScale(int[].class);
/*     */   
/*  64 */   private static final long LONG_ARRAY_BASE_OFFSET = arrayBaseOffset(long[].class);
/*  65 */   private static final long LONG_ARRAY_INDEX_SCALE = arrayIndexScale(long[].class);
/*     */   
/*  67 */   private static final long FLOAT_ARRAY_BASE_OFFSET = arrayBaseOffset(float[].class);
/*  68 */   private static final long FLOAT_ARRAY_INDEX_SCALE = arrayIndexScale(float[].class);
/*     */   
/*  70 */   private static final long DOUBLE_ARRAY_BASE_OFFSET = arrayBaseOffset(double[].class);
/*  71 */   private static final long DOUBLE_ARRAY_INDEX_SCALE = arrayIndexScale(double[].class);
/*     */   
/*  73 */   private static final long OBJECT_ARRAY_BASE_OFFSET = arrayBaseOffset(Object[].class);
/*  74 */   private static final long OBJECT_ARRAY_INDEX_SCALE = arrayIndexScale(Object[].class);
/*     */   
/*  76 */   private static final long BUFFER_ADDRESS_OFFSET = fieldOffset(bufferAddressField());
/*     */   
/*     */   private static final int STRIDE = 8;
/*     */   private static final int STRIDE_ALIGNMENT_MASK = 7;
/*  80 */   private static final int BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 0x7L);
/*     */ 
/*     */   
/*  83 */   static final boolean IS_BIG_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean hasUnsafeArrayOperations() {
/*  88 */     return HAS_UNSAFE_ARRAY_OPERATIONS;
/*     */   }
/*     */   
/*     */   static boolean hasUnsafeByteBufferOperations() {
/*  92 */     return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
/*     */   }
/*     */   
/*     */   static boolean isAndroid64() {
/*  96 */     return IS_ANDROID_64;
/*     */   }
/*     */ 
/*     */   
/*     */   static <T> T allocateInstance(Class<T> clazz) {
/*     */     try {
/* 102 */       return (T)UNSAFE.allocateInstance(clazz);
/* 103 */     } catch (InstantiationException e) {
/* 104 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static long objectFieldOffset(Field field) {
/* 109 */     return MEMORY_ACCESSOR.objectFieldOffset(field);
/*     */   }
/*     */   
/*     */   private static int arrayBaseOffset(Class<?> clazz) {
/* 113 */     return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(clazz) : -1;
/*     */   }
/*     */   
/*     */   private static int arrayIndexScale(Class<?> clazz) {
/* 117 */     return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(clazz) : -1;
/*     */   }
/*     */   
/*     */   static byte getByte(Object target, long offset) {
/* 121 */     return MEMORY_ACCESSOR.getByte(target, offset);
/*     */   }
/*     */   
/*     */   static void putByte(Object target, long offset, byte value) {
/* 125 */     MEMORY_ACCESSOR.putByte(target, offset, value);
/*     */   }
/*     */   
/*     */   static int getInt(Object target, long offset) {
/* 129 */     return MEMORY_ACCESSOR.getInt(target, offset);
/*     */   }
/*     */   
/*     */   static void putInt(Object target, long offset, int value) {
/* 133 */     MEMORY_ACCESSOR.putInt(target, offset, value);
/*     */   }
/*     */   
/*     */   static long getLong(Object target, long offset) {
/* 137 */     return MEMORY_ACCESSOR.getLong(target, offset);
/*     */   }
/*     */   
/*     */   static void putLong(Object target, long offset, long value) {
/* 141 */     MEMORY_ACCESSOR.putLong(target, offset, value);
/*     */   }
/*     */   
/*     */   static boolean getBoolean(Object target, long offset) {
/* 145 */     return MEMORY_ACCESSOR.getBoolean(target, offset);
/*     */   }
/*     */   
/*     */   static void putBoolean(Object target, long offset, boolean value) {
/* 149 */     MEMORY_ACCESSOR.putBoolean(target, offset, value);
/*     */   }
/*     */   
/*     */   static float getFloat(Object target, long offset) {
/* 153 */     return MEMORY_ACCESSOR.getFloat(target, offset);
/*     */   }
/*     */   
/*     */   static void putFloat(Object target, long offset, float value) {
/* 157 */     MEMORY_ACCESSOR.putFloat(target, offset, value);
/*     */   }
/*     */   
/*     */   static double getDouble(Object target, long offset) {
/* 161 */     return MEMORY_ACCESSOR.getDouble(target, offset);
/*     */   }
/*     */   
/*     */   static void putDouble(Object target, long offset, double value) {
/* 165 */     MEMORY_ACCESSOR.putDouble(target, offset, value);
/*     */   }
/*     */   
/*     */   static Object getObject(Object target, long offset) {
/* 169 */     return MEMORY_ACCESSOR.getObject(target, offset);
/*     */   }
/*     */   
/*     */   static void putObject(Object target, long offset, Object value) {
/* 173 */     MEMORY_ACCESSOR.putObject(target, offset, value);
/*     */   }
/*     */   
/*     */   static byte getByte(byte[] target, long index) {
/* 177 */     return MEMORY_ACCESSOR.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
/*     */   }
/*     */   
/*     */   static void putByte(byte[] target, long index, byte value) {
/* 181 */     MEMORY_ACCESSOR.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
/*     */   }
/*     */   
/*     */   static int getInt(int[] target, long index) {
/* 185 */     return MEMORY_ACCESSOR.getInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE);
/*     */   }
/*     */   
/*     */   static void putInt(int[] target, long index, int value) {
/* 189 */     MEMORY_ACCESSOR.putInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */   
/*     */   static long getLong(long[] target, long index) {
/* 193 */     return MEMORY_ACCESSOR.getLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE);
/*     */   }
/*     */ 
/*     */   
/*     */   static void putLong(long[] target, long index, long value) {
/* 198 */     MEMORY_ACCESSOR.putLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean getBoolean(boolean[] target, long index) {
/* 203 */     return MEMORY_ACCESSOR.getBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE);
/*     */   }
/*     */ 
/*     */   
/*     */   static void putBoolean(boolean[] target, long index, boolean value) {
/* 208 */     MEMORY_ACCESSOR.putBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static float getFloat(float[] target, long index) {
/* 213 */     return MEMORY_ACCESSOR.getFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE);
/*     */   }
/*     */ 
/*     */   
/*     */   static void putFloat(float[] target, long index, float value) {
/* 218 */     MEMORY_ACCESSOR.putFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static double getDouble(double[] target, long index) {
/* 223 */     return MEMORY_ACCESSOR.getDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE);
/*     */   }
/*     */ 
/*     */   
/*     */   static void putDouble(double[] target, long index, double value) {
/* 228 */     MEMORY_ACCESSOR.putDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static Object getObject(Object[] target, long index) {
/* 233 */     return MEMORY_ACCESSOR.getObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE);
/*     */   }
/*     */ 
/*     */   
/*     */   static void putObject(Object[] target, long index, Object value) {
/* 238 */     MEMORY_ACCESSOR.putObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
/* 243 */     MEMORY_ACCESSOR.copyMemory(src, srcIndex, targetOffset, length);
/*     */   }
/*     */   
/*     */   static void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
/* 247 */     MEMORY_ACCESSOR.copyMemory(srcOffset, target, targetIndex, length);
/*     */   }
/*     */   
/*     */   static void copyMemory(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
/* 251 */     System.arraycopy(src, (int)srcIndex, target, (int)targetIndex, (int)length);
/*     */   }
/*     */   
/*     */   static byte getByte(long address) {
/* 255 */     return MEMORY_ACCESSOR.getByte(address);
/*     */   }
/*     */   
/*     */   static void putByte(long address, byte value) {
/* 259 */     MEMORY_ACCESSOR.putByte(address, value);
/*     */   }
/*     */   
/*     */   static int getInt(long address) {
/* 263 */     return MEMORY_ACCESSOR.getInt(address);
/*     */   }
/*     */   
/*     */   static void putInt(long address, int value) {
/* 267 */     MEMORY_ACCESSOR.putInt(address, value);
/*     */   }
/*     */   
/*     */   static long getLong(long address) {
/* 271 */     return MEMORY_ACCESSOR.getLong(address);
/*     */   }
/*     */   
/*     */   static void putLong(long address, long value) {
/* 275 */     MEMORY_ACCESSOR.putLong(address, value);
/*     */   }
/*     */ 
/*     */   
/*     */   static long addressOffset(ByteBuffer buffer) {
/* 280 */     return MEMORY_ACCESSOR.getLong(buffer, BUFFER_ADDRESS_OFFSET);
/*     */   }
/*     */   
/*     */   static Object getStaticObject(Field field) {
/* 284 */     return MEMORY_ACCESSOR.getStaticObject(field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Unsafe getUnsafe() {
/* 291 */     Unsafe unsafe = null;
/*     */     
/*     */     try {
/* 294 */       unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>()
/*     */           {
/*     */             public Unsafe run() throws Exception
/*     */             {
/* 298 */               Class<Unsafe> k = Unsafe.class;
/*     */               
/* 300 */               for (Field f : k.getDeclaredFields()) {
/* 301 */                 f.setAccessible(true);
/* 302 */                 Object x = f.get(null);
/* 303 */                 if (k.isInstance(x)) {
/* 304 */                   return k.cast(x);
/*     */                 }
/*     */               } 
/*     */               
/* 308 */               return null;
/*     */             }
/*     */           });
/* 311 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */     
/* 315 */     return unsafe;
/*     */   }
/*     */ 
/*     */   
/*     */   private static MemoryAccessor getMemoryAccessor() {
/* 320 */     if (UNSAFE == null) {
/* 321 */       return null;
/*     */     }
/* 323 */     if (Android.isOnAndroidDevice()) {
/* 324 */       if (IS_ANDROID_64)
/* 325 */         return new Android64MemoryAccessor(UNSAFE); 
/* 326 */       if (IS_ANDROID_32) {
/* 327 */         return new Android32MemoryAccessor(UNSAFE);
/*     */       }
/* 329 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 333 */     return new JvmMemoryAccessor(UNSAFE);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean supportsUnsafeArrayOperations() {
/* 338 */     if (UNSAFE == null) {
/* 339 */       return false;
/*     */     }
/*     */     try {
/* 342 */       Class<?> clazz = UNSAFE.getClass();
/* 343 */       clazz.getMethod("objectFieldOffset", new Class[] { Field.class });
/* 344 */       clazz.getMethod("arrayBaseOffset", new Class[] { Class.class });
/* 345 */       clazz.getMethod("arrayIndexScale", new Class[] { Class.class });
/* 346 */       clazz.getMethod("getInt", new Class[] { Object.class, long.class });
/* 347 */       clazz.getMethod("putInt", new Class[] { Object.class, long.class, int.class });
/* 348 */       clazz.getMethod("getLong", new Class[] { Object.class, long.class });
/* 349 */       clazz.getMethod("putLong", new Class[] { Object.class, long.class, long.class });
/* 350 */       clazz.getMethod("getObject", new Class[] { Object.class, long.class });
/* 351 */       clazz.getMethod("putObject", new Class[] { Object.class, long.class, Object.class });
/* 352 */       if (Android.isOnAndroidDevice()) {
/* 353 */         return true;
/*     */       }
/* 355 */       clazz.getMethod("getByte", new Class[] { Object.class, long.class });
/* 356 */       clazz.getMethod("putByte", new Class[] { Object.class, long.class, byte.class });
/* 357 */       clazz.getMethod("getBoolean", new Class[] { Object.class, long.class });
/* 358 */       clazz.getMethod("putBoolean", new Class[] { Object.class, long.class, boolean.class });
/* 359 */       clazz.getMethod("getFloat", new Class[] { Object.class, long.class });
/* 360 */       clazz.getMethod("putFloat", new Class[] { Object.class, long.class, float.class });
/* 361 */       clazz.getMethod("getDouble", new Class[] { Object.class, long.class });
/* 362 */       clazz.getMethod("putDouble", new Class[] { Object.class, long.class, double.class });
/*     */       
/* 364 */       return true;
/* 365 */     } catch (Throwable e) {
/* 366 */       logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + e);
/*     */ 
/*     */ 
/*     */       
/* 370 */       return false;
/*     */     } 
/*     */   }
/*     */   private static boolean supportsUnsafeByteBufferOperations() {
/* 374 */     if (UNSAFE == null) {
/* 375 */       return false;
/*     */     }
/*     */     try {
/* 378 */       Class<?> clazz = UNSAFE.getClass();
/*     */       
/* 380 */       clazz.getMethod("objectFieldOffset", new Class[] { Field.class });
/* 381 */       clazz.getMethod("getLong", new Class[] { Object.class, long.class });
/*     */       
/* 383 */       if (bufferAddressField() == null) {
/* 384 */         return false;
/*     */       }
/*     */       
/* 387 */       if (Android.isOnAndroidDevice()) {
/* 388 */         return true;
/*     */       }
/* 390 */       clazz.getMethod("getByte", new Class[] { long.class });
/* 391 */       clazz.getMethod("putByte", new Class[] { long.class, byte.class });
/* 392 */       clazz.getMethod("getInt", new Class[] { long.class });
/* 393 */       clazz.getMethod("putInt", new Class[] { long.class, int.class });
/* 394 */       clazz.getMethod("getLong", new Class[] { long.class });
/* 395 */       clazz.getMethod("putLong", new Class[] { long.class, long.class });
/* 396 */       clazz.getMethod("copyMemory", new Class[] { long.class, long.class, long.class });
/* 397 */       clazz.getMethod("copyMemory", new Class[] { Object.class, long.class, Object.class, long.class, long.class });
/* 398 */       return true;
/* 399 */     } catch (Throwable e) {
/* 400 */       logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + e);
/*     */ 
/*     */ 
/*     */       
/* 404 */       return false;
/*     */     } 
/*     */   }
/*     */   private static boolean determineAndroidSupportByAddressSize(Class<?> addressClass) {
/* 408 */     if (!Android.isOnAndroidDevice()) {
/* 409 */       return false;
/*     */     }
/*     */     try {
/* 412 */       Class<?> clazz = MEMORY_CLASS;
/* 413 */       clazz.getMethod("peekLong", new Class[] { addressClass, boolean.class });
/* 414 */       clazz.getMethod("pokeLong", new Class[] { addressClass, long.class, boolean.class });
/* 415 */       clazz.getMethod("pokeInt", new Class[] { addressClass, int.class, boolean.class });
/* 416 */       clazz.getMethod("peekInt", new Class[] { addressClass, boolean.class });
/* 417 */       clazz.getMethod("pokeByte", new Class[] { addressClass, byte.class });
/* 418 */       clazz.getMethod("peekByte", new Class[] { addressClass });
/* 419 */       clazz.getMethod("pokeByteArray", new Class[] { addressClass, byte[].class, int.class, int.class });
/* 420 */       clazz.getMethod("peekByteArray", new Class[] { addressClass, byte[].class, int.class, int.class });
/* 421 */       return true;
/* 422 */     } catch (Throwable t) {
/* 423 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Field bufferAddressField() {
/* 429 */     if (Android.isOnAndroidDevice()) {
/*     */ 
/*     */       
/* 432 */       Field field1 = field(Buffer.class, "effectiveDirectAddress");
/* 433 */       if (field1 != null) {
/* 434 */         return field1;
/*     */       }
/*     */     } 
/* 437 */     Field field = field(Buffer.class, "address");
/* 438 */     return (field != null && field.getType() == long.class) ? field : null;
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
/*     */   private static int firstDifferingByteIndexNativeEndian(long left, long right) {
/* 452 */     int n = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(left ^ right) : Long.numberOfTrailingZeros(left ^ right);
/* 453 */     return n >> 3;
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
/*     */   static int mismatch(byte[] left, int leftOff, byte[] right, int rightOff, int length) {
/* 465 */     if (leftOff < 0 || rightOff < 0 || length < 0 || leftOff + length > left.length || rightOff + length > right.length)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 470 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 473 */     int index = 0;
/* 474 */     if (HAS_UNSAFE_ARRAY_OPERATIONS) {
/* 475 */       int leftAlignment = BYTE_ARRAY_ALIGNMENT + leftOff & 0x7;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 482 */       for (; index < length && (leftAlignment & 0x7) != 0; 
/* 483 */         index++, leftAlignment++) {
/* 484 */         if (left[leftOff + index] != right[rightOff + index]) {
/* 485 */           return index;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 491 */       int strideLength = (length - index & 0xFFFFFFF8) + index;
/*     */ 
/*     */ 
/*     */       
/* 495 */       for (; index < strideLength; index += 8) {
/* 496 */         long leftLongWord = getLong(left, BYTE_ARRAY_BASE_OFFSET + leftOff + index);
/* 497 */         long rightLongWord = getLong(right, BYTE_ARRAY_BASE_OFFSET + rightOff + index);
/* 498 */         if (leftLongWord != rightLongWord)
/*     */         {
/* 500 */           return index + firstDifferingByteIndexNativeEndian(leftLongWord, rightLongWord);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 507 */     for (; index < length; index++) {
/* 508 */       if (left[leftOff + index] != right[rightOff + index]) {
/* 509 */         return index;
/*     */       }
/*     */     } 
/* 512 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long fieldOffset(Field field) {
/* 520 */     return (field == null || MEMORY_ACCESSOR == null) ? -1L : MEMORY_ACCESSOR.objectFieldOffset(field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Field field(Class<?> clazz, String fieldName) {
/*     */     Field field;
/*     */     try {
/* 529 */       field = clazz.getDeclaredField(fieldName);
/* 530 */     } catch (Throwable t) {
/*     */       
/* 532 */       field = null;
/*     */     } 
/* 534 */     return field;
/*     */   }
/*     */   
/*     */   private static abstract class MemoryAccessor
/*     */   {
/*     */     Unsafe unsafe;
/*     */     
/*     */     MemoryAccessor(Unsafe unsafe) {
/* 542 */       this.unsafe = unsafe;
/*     */     }
/*     */     
/*     */     public final long objectFieldOffset(Field field) {
/* 546 */       return this.unsafe.objectFieldOffset(field);
/*     */     }
/*     */     
/*     */     public abstract byte getByte(Object param1Object, long param1Long);
/*     */     
/*     */     public abstract void putByte(Object param1Object, long param1Long, byte param1Byte);
/*     */     
/*     */     public final int getInt(Object target, long offset) {
/* 554 */       return this.unsafe.getInt(target, offset);
/*     */     }
/*     */     
/*     */     public final void putInt(Object target, long offset, int value) {
/* 558 */       this.unsafe.putInt(target, offset, value);
/*     */     }
/*     */     
/*     */     public final long getLong(Object target, long offset) {
/* 562 */       return this.unsafe.getLong(target, offset);
/*     */     }
/*     */     
/*     */     public final void putLong(Object target, long offset, long value) {
/* 566 */       this.unsafe.putLong(target, offset, value);
/*     */     }
/*     */     
/*     */     public abstract boolean getBoolean(Object param1Object, long param1Long);
/*     */     
/*     */     public abstract void putBoolean(Object param1Object, long param1Long, boolean param1Boolean);
/*     */     
/*     */     public abstract float getFloat(Object param1Object, long param1Long);
/*     */     
/*     */     public abstract void putFloat(Object param1Object, long param1Long, float param1Float);
/*     */     
/*     */     public abstract double getDouble(Object param1Object, long param1Long);
/*     */     
/*     */     public abstract void putDouble(Object param1Object, long param1Long, double param1Double);
/*     */     
/*     */     public final Object getObject(Object target, long offset) {
/* 582 */       return this.unsafe.getObject(target, offset);
/*     */     }
/*     */     
/*     */     public final void putObject(Object target, long offset, Object value) {
/* 586 */       this.unsafe.putObject(target, offset, value);
/*     */     }
/*     */     
/*     */     public final int arrayBaseOffset(Class<?> clazz) {
/* 590 */       return this.unsafe.arrayBaseOffset(clazz);
/*     */     }
/*     */     
/*     */     public final int arrayIndexScale(Class<?> clazz) {
/* 594 */       return this.unsafe.arrayIndexScale(clazz);
/*     */     }
/*     */     
/*     */     public abstract byte getByte(long param1Long);
/*     */     
/*     */     public abstract void putByte(long param1Long, byte param1Byte);
/*     */     
/*     */     public abstract int getInt(long param1Long);
/*     */     
/*     */     public abstract void putInt(long param1Long, int param1Int);
/*     */     
/*     */     public abstract long getLong(long param1Long);
/*     */     
/*     */     public abstract void putLong(long param1Long1, long param1Long2);
/*     */     
/*     */     public abstract Object getStaticObject(Field param1Field);
/*     */     
/*     */     public abstract void copyMemory(long param1Long1, byte[] param1ArrayOfbyte, long param1Long2, long param1Long3);
/*     */     
/*     */     public abstract void copyMemory(byte[] param1ArrayOfbyte, long param1Long1, long param1Long2, long param1Long3);
/*     */   }
/*     */   
/*     */   private static final class JvmMemoryAccessor
/*     */     extends MemoryAccessor {
/*     */     JvmMemoryAccessor(Unsafe unsafe) {
/* 619 */       super(unsafe);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(long address) {
/* 624 */       return this.unsafe.getByte(address);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putByte(long address, byte value) {
/* 629 */       this.unsafe.putByte(address, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getInt(long address) {
/* 634 */       return this.unsafe.getInt(address);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putInt(long address, int value) {
/* 639 */       this.unsafe.putInt(address, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public long getLong(long address) {
/* 644 */       return this.unsafe.getLong(address);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putLong(long address, long value) {
/* 649 */       this.unsafe.putLong(address, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(Object target, long offset) {
/* 654 */       return this.unsafe.getByte(target, offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putByte(Object target, long offset, byte value) {
/* 659 */       this.unsafe.putByte(target, offset, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean getBoolean(Object target, long offset) {
/* 664 */       return this.unsafe.getBoolean(target, offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putBoolean(Object target, long offset, boolean value) {
/* 669 */       this.unsafe.putBoolean(target, offset, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public float getFloat(Object target, long offset) {
/* 674 */       return this.unsafe.getFloat(target, offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putFloat(Object target, long offset, float value) {
/* 679 */       this.unsafe.putFloat(target, offset, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public double getDouble(Object target, long offset) {
/* 684 */       return this.unsafe.getDouble(target, offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public void putDouble(Object target, long offset, double value) {
/* 689 */       this.unsafe.putDouble(target, offset, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
/* 694 */       this.unsafe.copyMemory(null, srcOffset, target, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + targetIndex, length);
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
/* 699 */       this.unsafe.copyMemory(src, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + srcIndex, null, targetOffset, length);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getStaticObject(Field field) {
/* 704 */       return getObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field));
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Android64MemoryAccessor
/*     */     extends MemoryAccessor {
/*     */     Android64MemoryAccessor(Unsafe unsafe) {
/* 711 */       super(unsafe);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(long address) {
/* 716 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putByte(long address, byte value) {
/* 721 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getInt(long address) {
/* 726 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putInt(long address, int value) {
/* 731 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getLong(long address) {
/* 736 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putLong(long address, long value) {
/* 741 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(Object target, long offset) {
/* 746 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 747 */         return UnsafeUtil.getByteBigEndian(target, offset);
/*     */       }
/* 749 */       return UnsafeUtil.getByteLittleEndian(target, offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void putByte(Object target, long offset, byte value) {
/* 755 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 756 */         UnsafeUtil.putByteBigEndian(target, offset, value);
/*     */       } else {
/* 758 */         UnsafeUtil.putByteLittleEndian(target, offset, value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean getBoolean(Object target, long offset) {
/* 764 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 765 */         return UnsafeUtil.getBooleanBigEndian(target, offset);
/*     */       }
/* 767 */       return UnsafeUtil.getBooleanLittleEndian(target, offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void putBoolean(Object target, long offset, boolean value) {
/* 773 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 774 */         UnsafeUtil.putBooleanBigEndian(target, offset, value);
/*     */       } else {
/* 776 */         UnsafeUtil.putBooleanLittleEndian(target, offset, value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public float getFloat(Object target, long offset) {
/* 782 */       return Float.intBitsToFloat(getInt(target, offset));
/*     */     }
/*     */ 
/*     */     
/*     */     public void putFloat(Object target, long offset, float value) {
/* 787 */       putInt(target, offset, Float.floatToIntBits(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public double getDouble(Object target, long offset) {
/* 792 */       return Double.longBitsToDouble(getLong(target, offset));
/*     */     }
/*     */ 
/*     */     
/*     */     public void putDouble(Object target, long offset, double value) {
/* 797 */       putLong(target, offset, Double.doubleToLongBits(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
/* 802 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
/* 807 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getStaticObject(Field field) {
/*     */       try {
/* 813 */         return field.get(null);
/* 814 */       } catch (IllegalAccessException e) {
/* 815 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Android32MemoryAccessor
/*     */     extends MemoryAccessor
/*     */   {
/*     */     private static final long SMALL_ADDRESS_MASK = -1L;
/*     */     
/*     */     private static int smallAddress(long address) {
/* 827 */       return (int)(0xFFFFFFFFFFFFFFFFL & address);
/*     */     }
/*     */     
/*     */     Android32MemoryAccessor(Unsafe unsafe) {
/* 831 */       super(unsafe);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(long address) {
/* 836 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putByte(long address, byte value) {
/* 841 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getInt(long address) {
/* 846 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putInt(long address, int value) {
/* 851 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getLong(long address) {
/* 856 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void putLong(long address, long value) {
/* 861 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public byte getByte(Object target, long offset) {
/* 866 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 867 */         return UnsafeUtil.getByteBigEndian(target, offset);
/*     */       }
/* 869 */       return UnsafeUtil.getByteLittleEndian(target, offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void putByte(Object target, long offset, byte value) {
/* 875 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 876 */         UnsafeUtil.putByteBigEndian(target, offset, value);
/*     */       } else {
/* 878 */         UnsafeUtil.putByteLittleEndian(target, offset, value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean getBoolean(Object target, long offset) {
/* 884 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 885 */         return UnsafeUtil.getBooleanBigEndian(target, offset);
/*     */       }
/* 887 */       return UnsafeUtil.getBooleanLittleEndian(target, offset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void putBoolean(Object target, long offset, boolean value) {
/* 893 */       if (UnsafeUtil.IS_BIG_ENDIAN) {
/* 894 */         UnsafeUtil.putBooleanBigEndian(target, offset, value);
/*     */       } else {
/* 896 */         UnsafeUtil.putBooleanLittleEndian(target, offset, value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public float getFloat(Object target, long offset) {
/* 902 */       return Float.intBitsToFloat(getInt(target, offset));
/*     */     }
/*     */ 
/*     */     
/*     */     public void putFloat(Object target, long offset, float value) {
/* 907 */       putInt(target, offset, Float.floatToIntBits(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public double getDouble(Object target, long offset) {
/* 912 */       return Double.longBitsToDouble(getLong(target, offset));
/*     */     }
/*     */ 
/*     */     
/*     */     public void putDouble(Object target, long offset, double value) {
/* 917 */       putLong(target, offset, Double.doubleToLongBits(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
/* 922 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
/* 927 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getStaticObject(Field field) {
/*     */       try {
/* 933 */         return field.get(null);
/* 934 */       } catch (IllegalAccessException e) {
/* 935 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static byte getByteBigEndian(Object target, long offset) {
/* 941 */     return (byte)(getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)(((offset ^ 0xFFFFFFFFFFFFFFFFL) & 0x3L) << 3L) & 0xFF);
/*     */   }
/*     */   
/*     */   private static byte getByteLittleEndian(Object target, long offset) {
/* 945 */     return (byte)(getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)((offset & 0x3L) << 3L) & 0xFF);
/*     */   }
/*     */   
/*     */   private static void putByteBigEndian(Object target, long offset, byte value) {
/* 949 */     int intValue = getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
/* 950 */     int shift = (((int)offset ^ 0xFFFFFFFF) & 0x3) << 3;
/* 951 */     int output = intValue & (255 << shift ^ 0xFFFFFFFF) | (0xFF & value) << shift;
/* 952 */     putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
/*     */   }
/*     */   
/*     */   private static void putByteLittleEndian(Object target, long offset, byte value) {
/* 956 */     int intValue = getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
/* 957 */     int shift = ((int)offset & 0x3) << 3;
/* 958 */     int output = intValue & (255 << shift ^ 0xFFFFFFFF) | (0xFF & value) << shift;
/* 959 */     putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
/*     */   }
/*     */   
/*     */   private static boolean getBooleanBigEndian(Object target, long offset) {
/* 963 */     return (getByteBigEndian(target, offset) != 0);
/*     */   }
/*     */   
/*     */   private static boolean getBooleanLittleEndian(Object target, long offset) {
/* 967 */     return (getByteLittleEndian(target, offset) != 0);
/*     */   }
/*     */   
/*     */   private static void putBooleanBigEndian(Object target, long offset, boolean value) {
/* 971 */     putByteBigEndian(target, offset, (byte)(value ? 1 : 0));
/*     */   }
/*     */   
/*     */   private static void putBooleanLittleEndian(Object target, long offset, boolean value) {
/* 975 */     putByteLittleEndian(target, offset, (byte)(value ? 1 : 0));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnsafeUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */