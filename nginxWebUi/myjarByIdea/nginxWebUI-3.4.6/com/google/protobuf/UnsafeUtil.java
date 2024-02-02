package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class UnsafeUtil {
   private static final Logger logger = Logger.getLogger(UnsafeUtil.class.getName());
   private static final Unsafe UNSAFE = getUnsafe();
   private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
   private static final boolean IS_ANDROID_64;
   private static final boolean IS_ANDROID_32;
   private static final MemoryAccessor MEMORY_ACCESSOR;
   private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS;
   static final long BYTE_ARRAY_BASE_OFFSET;
   private static final long BOOLEAN_ARRAY_BASE_OFFSET;
   private static final long BOOLEAN_ARRAY_INDEX_SCALE;
   private static final long INT_ARRAY_BASE_OFFSET;
   private static final long INT_ARRAY_INDEX_SCALE;
   private static final long LONG_ARRAY_BASE_OFFSET;
   private static final long LONG_ARRAY_INDEX_SCALE;
   private static final long FLOAT_ARRAY_BASE_OFFSET;
   private static final long FLOAT_ARRAY_INDEX_SCALE;
   private static final long DOUBLE_ARRAY_BASE_OFFSET;
   private static final long DOUBLE_ARRAY_INDEX_SCALE;
   private static final long OBJECT_ARRAY_BASE_OFFSET;
   private static final long OBJECT_ARRAY_INDEX_SCALE;
   private static final long BUFFER_ADDRESS_OFFSET;
   private static final int STRIDE = 8;
   private static final int STRIDE_ALIGNMENT_MASK = 7;
   private static final int BYTE_ARRAY_ALIGNMENT;
   static final boolean IS_BIG_ENDIAN;

   private UnsafeUtil() {
   }

   static boolean hasUnsafeArrayOperations() {
      return HAS_UNSAFE_ARRAY_OPERATIONS;
   }

   static boolean hasUnsafeByteBufferOperations() {
      return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
   }

   static boolean isAndroid64() {
      return IS_ANDROID_64;
   }

   static <T> T allocateInstance(Class<T> clazz) {
      try {
         return UNSAFE.allocateInstance(clazz);
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      }
   }

   static long objectFieldOffset(java.lang.reflect.Field field) {
      return MEMORY_ACCESSOR.objectFieldOffset(field);
   }

   private static int arrayBaseOffset(Class<?> clazz) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(clazz) : -1;
   }

   private static int arrayIndexScale(Class<?> clazz) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(clazz) : -1;
   }

   static byte getByte(Object target, long offset) {
      return MEMORY_ACCESSOR.getByte(target, offset);
   }

   static void putByte(Object target, long offset, byte value) {
      MEMORY_ACCESSOR.putByte(target, offset, value);
   }

   static int getInt(Object target, long offset) {
      return MEMORY_ACCESSOR.getInt(target, offset);
   }

   static void putInt(Object target, long offset, int value) {
      MEMORY_ACCESSOR.putInt(target, offset, value);
   }

   static long getLong(Object target, long offset) {
      return MEMORY_ACCESSOR.getLong(target, offset);
   }

   static void putLong(Object target, long offset, long value) {
      MEMORY_ACCESSOR.putLong(target, offset, value);
   }

   static boolean getBoolean(Object target, long offset) {
      return MEMORY_ACCESSOR.getBoolean(target, offset);
   }

   static void putBoolean(Object target, long offset, boolean value) {
      MEMORY_ACCESSOR.putBoolean(target, offset, value);
   }

   static float getFloat(Object target, long offset) {
      return MEMORY_ACCESSOR.getFloat(target, offset);
   }

   static void putFloat(Object target, long offset, float value) {
      MEMORY_ACCESSOR.putFloat(target, offset, value);
   }

   static double getDouble(Object target, long offset) {
      return MEMORY_ACCESSOR.getDouble(target, offset);
   }

   static void putDouble(Object target, long offset, double value) {
      MEMORY_ACCESSOR.putDouble(target, offset, value);
   }

   static Object getObject(Object target, long offset) {
      return MEMORY_ACCESSOR.getObject(target, offset);
   }

   static void putObject(Object target, long offset, Object value) {
      MEMORY_ACCESSOR.putObject(target, offset, value);
   }

   static byte getByte(byte[] target, long index) {
      return MEMORY_ACCESSOR.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
   }

   static void putByte(byte[] target, long index, byte value) {
      MEMORY_ACCESSOR.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
   }

   static int getInt(int[] target, long index) {
      return MEMORY_ACCESSOR.getInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE);
   }

   static void putInt(int[] target, long index, int value) {
      MEMORY_ACCESSOR.putInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE, value);
   }

   static long getLong(long[] target, long index) {
      return MEMORY_ACCESSOR.getLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE);
   }

   static void putLong(long[] target, long index, long value) {
      MEMORY_ACCESSOR.putLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE, value);
   }

   static boolean getBoolean(boolean[] target, long index) {
      return MEMORY_ACCESSOR.getBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE);
   }

   static void putBoolean(boolean[] target, long index, boolean value) {
      MEMORY_ACCESSOR.putBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE, value);
   }

   static float getFloat(float[] target, long index) {
      return MEMORY_ACCESSOR.getFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE);
   }

   static void putFloat(float[] target, long index, float value) {
      MEMORY_ACCESSOR.putFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE, value);
   }

   static double getDouble(double[] target, long index) {
      return MEMORY_ACCESSOR.getDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE);
   }

   static void putDouble(double[] target, long index, double value) {
      MEMORY_ACCESSOR.putDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE, value);
   }

   static Object getObject(Object[] target, long index) {
      return MEMORY_ACCESSOR.getObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE);
   }

   static void putObject(Object[] target, long index, Object value) {
      MEMORY_ACCESSOR.putObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE, value);
   }

   static void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
      MEMORY_ACCESSOR.copyMemory(src, srcIndex, targetOffset, length);
   }

   static void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
      MEMORY_ACCESSOR.copyMemory(srcOffset, target, targetIndex, length);
   }

   static void copyMemory(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
      System.arraycopy(src, (int)srcIndex, target, (int)targetIndex, (int)length);
   }

   static byte getByte(long address) {
      return MEMORY_ACCESSOR.getByte(address);
   }

   static void putByte(long address, byte value) {
      MEMORY_ACCESSOR.putByte(address, value);
   }

   static int getInt(long address) {
      return MEMORY_ACCESSOR.getInt(address);
   }

   static void putInt(long address, int value) {
      MEMORY_ACCESSOR.putInt(address, value);
   }

   static long getLong(long address) {
      return MEMORY_ACCESSOR.getLong(address);
   }

   static void putLong(long address, long value) {
      MEMORY_ACCESSOR.putLong(address, value);
   }

   static long addressOffset(ByteBuffer buffer) {
      return MEMORY_ACCESSOR.getLong(buffer, BUFFER_ADDRESS_OFFSET);
   }

   static Object getStaticObject(java.lang.reflect.Field field) {
      return MEMORY_ACCESSOR.getStaticObject(field);
   }

   static Unsafe getUnsafe() {
      Unsafe unsafe = null;

      try {
         unsafe = (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
               Class<Unsafe> k = Unsafe.class;
               java.lang.reflect.Field[] var2 = k.getDeclaredFields();
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  java.lang.reflect.Field f = var2[var4];
                  f.setAccessible(true);
                  Object x = f.get((Object)null);
                  if (k.isInstance(x)) {
                     return (Unsafe)k.cast(x);
                  }
               }

               return null;
            }
         });
      } catch (Throwable var2) {
      }

      return unsafe;
   }

   private static MemoryAccessor getMemoryAccessor() {
      if (UNSAFE == null) {
         return null;
      } else if (Android.isOnAndroidDevice()) {
         if (IS_ANDROID_64) {
            return new Android64MemoryAccessor(UNSAFE);
         } else {
            return IS_ANDROID_32 ? new Android32MemoryAccessor(UNSAFE) : null;
         }
      } else {
         return new JvmMemoryAccessor(UNSAFE);
      }
   }

   private static boolean supportsUnsafeArrayOperations() {
      if (UNSAFE == null) {
         return false;
      } else {
         try {
            Class<?> clazz = UNSAFE.getClass();
            clazz.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
            clazz.getMethod("arrayBaseOffset", Class.class);
            clazz.getMethod("arrayIndexScale", Class.class);
            clazz.getMethod("getInt", Object.class, Long.TYPE);
            clazz.getMethod("putInt", Object.class, Long.TYPE, Integer.TYPE);
            clazz.getMethod("getLong", Object.class, Long.TYPE);
            clazz.getMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
            clazz.getMethod("getObject", Object.class, Long.TYPE);
            clazz.getMethod("putObject", Object.class, Long.TYPE, Object.class);
            if (Android.isOnAndroidDevice()) {
               return true;
            } else {
               clazz.getMethod("getByte", Object.class, Long.TYPE);
               clazz.getMethod("putByte", Object.class, Long.TYPE, Byte.TYPE);
               clazz.getMethod("getBoolean", Object.class, Long.TYPE);
               clazz.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
               clazz.getMethod("getFloat", Object.class, Long.TYPE);
               clazz.getMethod("putFloat", Object.class, Long.TYPE, Float.TYPE);
               clazz.getMethod("getDouble", Object.class, Long.TYPE);
               clazz.getMethod("putDouble", Object.class, Long.TYPE, Double.TYPE);
               return true;
            }
         } catch (Throwable var1) {
            logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + var1);
            return false;
         }
      }
   }

   private static boolean supportsUnsafeByteBufferOperations() {
      if (UNSAFE == null) {
         return false;
      } else {
         try {
            Class<?> clazz = UNSAFE.getClass();
            clazz.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
            clazz.getMethod("getLong", Object.class, Long.TYPE);
            if (bufferAddressField() == null) {
               return false;
            } else if (Android.isOnAndroidDevice()) {
               return true;
            } else {
               clazz.getMethod("getByte", Long.TYPE);
               clazz.getMethod("putByte", Long.TYPE, Byte.TYPE);
               clazz.getMethod("getInt", Long.TYPE);
               clazz.getMethod("putInt", Long.TYPE, Integer.TYPE);
               clazz.getMethod("getLong", Long.TYPE);
               clazz.getMethod("putLong", Long.TYPE, Long.TYPE);
               clazz.getMethod("copyMemory", Long.TYPE, Long.TYPE, Long.TYPE);
               clazz.getMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
               return true;
            }
         } catch (Throwable var1) {
            logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + var1);
            return false;
         }
      }
   }

   private static boolean determineAndroidSupportByAddressSize(Class<?> addressClass) {
      if (!Android.isOnAndroidDevice()) {
         return false;
      } else {
         try {
            Class<?> clazz = MEMORY_CLASS;
            clazz.getMethod("peekLong", addressClass, Boolean.TYPE);
            clazz.getMethod("pokeLong", addressClass, Long.TYPE, Boolean.TYPE);
            clazz.getMethod("pokeInt", addressClass, Integer.TYPE, Boolean.TYPE);
            clazz.getMethod("peekInt", addressClass, Boolean.TYPE);
            clazz.getMethod("pokeByte", addressClass, Byte.TYPE);
            clazz.getMethod("peekByte", addressClass);
            clazz.getMethod("pokeByteArray", addressClass, byte[].class, Integer.TYPE, Integer.TYPE);
            clazz.getMethod("peekByteArray", addressClass, byte[].class, Integer.TYPE, Integer.TYPE);
            return true;
         } catch (Throwable var2) {
            return false;
         }
      }
   }

   private static java.lang.reflect.Field bufferAddressField() {
      java.lang.reflect.Field field;
      if (Android.isOnAndroidDevice()) {
         field = field(Buffer.class, "effectiveDirectAddress");
         if (field != null) {
            return field;
         }
      }

      field = field(Buffer.class, "address");
      return field != null && field.getType() == Long.TYPE ? field : null;
   }

   private static int firstDifferingByteIndexNativeEndian(long left, long right) {
      int n = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(left ^ right) : Long.numberOfTrailingZeros(left ^ right);
      return n >> 3;
   }

   static int mismatch(byte[] left, int leftOff, byte[] right, int rightOff, int length) {
      if (leftOff >= 0 && rightOff >= 0 && length >= 0 && leftOff + length <= left.length && rightOff + length <= right.length) {
         int index = 0;
         if (HAS_UNSAFE_ARRAY_OPERATIONS) {
            for(int leftAlignment = BYTE_ARRAY_ALIGNMENT + leftOff & 7; index < length && (leftAlignment & 7) != 0; ++leftAlignment) {
               if (left[leftOff + index] != right[rightOff + index]) {
                  return index;
               }

               ++index;
            }

            for(int strideLength = (length - index & -8) + index; index < strideLength; index += 8) {
               long leftLongWord = getLong((Object)left, BYTE_ARRAY_BASE_OFFSET + (long)leftOff + (long)index);
               long rightLongWord = getLong((Object)right, BYTE_ARRAY_BASE_OFFSET + (long)rightOff + (long)index);
               if (leftLongWord != rightLongWord) {
                  return index + firstDifferingByteIndexNativeEndian(leftLongWord, rightLongWord);
               }
            }
         }

         while(index < length) {
            if (left[leftOff + index] != right[rightOff + index]) {
               return index;
            }

            ++index;
         }

         return -1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static long fieldOffset(java.lang.reflect.Field field) {
      return field != null && MEMORY_ACCESSOR != null ? MEMORY_ACCESSOR.objectFieldOffset(field) : -1L;
   }

   private static java.lang.reflect.Field field(Class<?> clazz, String fieldName) {
      java.lang.reflect.Field field;
      try {
         field = clazz.getDeclaredField(fieldName);
      } catch (Throwable var4) {
         field = null;
      }

      return field;
   }

   private static byte getByteBigEndian(Object target, long offset) {
      return (byte)(getInt(target, offset & -4L) >>> (int)((~offset & 3L) << 3) & 255);
   }

   private static byte getByteLittleEndian(Object target, long offset) {
      return (byte)(getInt(target, offset & -4L) >>> (int)((offset & 3L) << 3) & 255);
   }

   private static void putByteBigEndian(Object target, long offset, byte value) {
      int intValue = getInt(target, offset & -4L);
      int shift = (~((int)offset) & 3) << 3;
      int output = intValue & ~(255 << shift) | (255 & value) << shift;
      putInt(target, offset & -4L, output);
   }

   private static void putByteLittleEndian(Object target, long offset, byte value) {
      int intValue = getInt(target, offset & -4L);
      int shift = ((int)offset & 3) << 3;
      int output = intValue & ~(255 << shift) | (255 & value) << shift;
      putInt(target, offset & -4L, output);
   }

   private static boolean getBooleanBigEndian(Object target, long offset) {
      return getByteBigEndian(target, offset) != 0;
   }

   private static boolean getBooleanLittleEndian(Object target, long offset) {
      return getByteLittleEndian(target, offset) != 0;
   }

   private static void putBooleanBigEndian(Object target, long offset, boolean value) {
      putByteBigEndian(target, offset, (byte)(value ? 1 : 0));
   }

   private static void putBooleanLittleEndian(Object target, long offset, boolean value) {
      putByteLittleEndian(target, offset, (byte)(value ? 1 : 0));
   }

   static {
      IS_ANDROID_64 = determineAndroidSupportByAddressSize(Long.TYPE);
      IS_ANDROID_32 = determineAndroidSupportByAddressSize(Integer.TYPE);
      MEMORY_ACCESSOR = getMemoryAccessor();
      HAS_UNSAFE_BYTEBUFFER_OPERATIONS = supportsUnsafeByteBufferOperations();
      HAS_UNSAFE_ARRAY_OPERATIONS = supportsUnsafeArrayOperations();
      BYTE_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(byte[].class);
      BOOLEAN_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(boolean[].class);
      BOOLEAN_ARRAY_INDEX_SCALE = (long)arrayIndexScale(boolean[].class);
      INT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(int[].class);
      INT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(int[].class);
      LONG_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(long[].class);
      LONG_ARRAY_INDEX_SCALE = (long)arrayIndexScale(long[].class);
      FLOAT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(float[].class);
      FLOAT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(float[].class);
      DOUBLE_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(double[].class);
      DOUBLE_ARRAY_INDEX_SCALE = (long)arrayIndexScale(double[].class);
      OBJECT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(Object[].class);
      OBJECT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(Object[].class);
      BUFFER_ADDRESS_OFFSET = fieldOffset(bufferAddressField());
      BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 7L);
      IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
   }

   private static final class Android32MemoryAccessor extends MemoryAccessor {
      private static final long SMALL_ADDRESS_MASK = -1L;

      private static int smallAddress(long address) {
         return (int)(-1L & address);
      }

      Android32MemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      public byte getByte(long address) {
         throw new UnsupportedOperationException();
      }

      public void putByte(long address, byte value) {
         throw new UnsupportedOperationException();
      }

      public int getInt(long address) {
         throw new UnsupportedOperationException();
      }

      public void putInt(long address, int value) {
         throw new UnsupportedOperationException();
      }

      public long getLong(long address) {
         throw new UnsupportedOperationException();
      }

      public void putLong(long address, long value) {
         throw new UnsupportedOperationException();
      }

      public byte getByte(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(target, offset) : UnsafeUtil.getByteLittleEndian(target, offset);
      }

      public void putByte(Object target, long offset, byte value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putByteLittleEndian(target, offset, value);
         }

      }

      public boolean getBoolean(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(target, offset) : UnsafeUtil.getBooleanLittleEndian(target, offset);
      }

      public void putBoolean(Object target, long offset, boolean value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putBooleanLittleEndian(target, offset, value);
         }

      }

      public float getFloat(Object target, long offset) {
         return Float.intBitsToFloat(this.getInt(target, offset));
      }

      public void putFloat(Object target, long offset, float value) {
         this.putInt(target, offset, Float.floatToIntBits(value));
      }

      public double getDouble(Object target, long offset) {
         return Double.longBitsToDouble(this.getLong(target, offset));
      }

      public void putDouble(Object target, long offset, double value) {
         this.putLong(target, offset, Double.doubleToLongBits(value));
      }

      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         throw new UnsupportedOperationException();
      }

      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         throw new UnsupportedOperationException();
      }

      public Object getStaticObject(java.lang.reflect.Field field) {
         try {
            return field.get((Object)null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }
   }

   private static final class Android64MemoryAccessor extends MemoryAccessor {
      Android64MemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      public byte getByte(long address) {
         throw new UnsupportedOperationException();
      }

      public void putByte(long address, byte value) {
         throw new UnsupportedOperationException();
      }

      public int getInt(long address) {
         throw new UnsupportedOperationException();
      }

      public void putInt(long address, int value) {
         throw new UnsupportedOperationException();
      }

      public long getLong(long address) {
         throw new UnsupportedOperationException();
      }

      public void putLong(long address, long value) {
         throw new UnsupportedOperationException();
      }

      public byte getByte(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(target, offset) : UnsafeUtil.getByteLittleEndian(target, offset);
      }

      public void putByte(Object target, long offset, byte value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putByteLittleEndian(target, offset, value);
         }

      }

      public boolean getBoolean(Object target, long offset) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(target, offset) : UnsafeUtil.getBooleanLittleEndian(target, offset);
      }

      public void putBoolean(Object target, long offset, boolean value) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(target, offset, value);
         } else {
            UnsafeUtil.putBooleanLittleEndian(target, offset, value);
         }

      }

      public float getFloat(Object target, long offset) {
         return Float.intBitsToFloat(this.getInt(target, offset));
      }

      public void putFloat(Object target, long offset, float value) {
         this.putInt(target, offset, Float.floatToIntBits(value));
      }

      public double getDouble(Object target, long offset) {
         return Double.longBitsToDouble(this.getLong(target, offset));
      }

      public void putDouble(Object target, long offset, double value) {
         this.putLong(target, offset, Double.doubleToLongBits(value));
      }

      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         throw new UnsupportedOperationException();
      }

      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         throw new UnsupportedOperationException();
      }

      public Object getStaticObject(java.lang.reflect.Field field) {
         try {
            return field.get((Object)null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }
   }

   private static final class JvmMemoryAccessor extends MemoryAccessor {
      JvmMemoryAccessor(Unsafe unsafe) {
         super(unsafe);
      }

      public byte getByte(long address) {
         return this.unsafe.getByte(address);
      }

      public void putByte(long address, byte value) {
         this.unsafe.putByte(address, value);
      }

      public int getInt(long address) {
         return this.unsafe.getInt(address);
      }

      public void putInt(long address, int value) {
         this.unsafe.putInt(address, value);
      }

      public long getLong(long address) {
         return this.unsafe.getLong(address);
      }

      public void putLong(long address, long value) {
         this.unsafe.putLong(address, value);
      }

      public byte getByte(Object target, long offset) {
         return this.unsafe.getByte(target, offset);
      }

      public void putByte(Object target, long offset, byte value) {
         this.unsafe.putByte(target, offset, value);
      }

      public boolean getBoolean(Object target, long offset) {
         return this.unsafe.getBoolean(target, offset);
      }

      public void putBoolean(Object target, long offset, boolean value) {
         this.unsafe.putBoolean(target, offset, value);
      }

      public float getFloat(Object target, long offset) {
         return this.unsafe.getFloat(target, offset);
      }

      public void putFloat(Object target, long offset, float value) {
         this.unsafe.putFloat(target, offset, value);
      }

      public double getDouble(Object target, long offset) {
         return this.unsafe.getDouble(target, offset);
      }

      public void putDouble(Object target, long offset, double value) {
         this.unsafe.putDouble(target, offset, value);
      }

      public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
         this.unsafe.copyMemory((Object)null, srcOffset, target, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + targetIndex, length);
      }

      public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
         this.unsafe.copyMemory(src, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + srcIndex, (Object)null, targetOffset, length);
      }

      public Object getStaticObject(java.lang.reflect.Field field) {
         return this.getObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field));
      }
   }

   private abstract static class MemoryAccessor {
      Unsafe unsafe;

      MemoryAccessor(Unsafe unsafe) {
         this.unsafe = unsafe;
      }

      public final long objectFieldOffset(java.lang.reflect.Field field) {
         return this.unsafe.objectFieldOffset(field);
      }

      public abstract byte getByte(Object var1, long var2);

      public abstract void putByte(Object var1, long var2, byte var4);

      public final int getInt(Object target, long offset) {
         return this.unsafe.getInt(target, offset);
      }

      public final void putInt(Object target, long offset, int value) {
         this.unsafe.putInt(target, offset, value);
      }

      public final long getLong(Object target, long offset) {
         return this.unsafe.getLong(target, offset);
      }

      public final void putLong(Object target, long offset, long value) {
         this.unsafe.putLong(target, offset, value);
      }

      public abstract boolean getBoolean(Object var1, long var2);

      public abstract void putBoolean(Object var1, long var2, boolean var4);

      public abstract float getFloat(Object var1, long var2);

      public abstract void putFloat(Object var1, long var2, float var4);

      public abstract double getDouble(Object var1, long var2);

      public abstract void putDouble(Object var1, long var2, double var4);

      public final Object getObject(Object target, long offset) {
         return this.unsafe.getObject(target, offset);
      }

      public final void putObject(Object target, long offset, Object value) {
         this.unsafe.putObject(target, offset, value);
      }

      public final int arrayBaseOffset(Class<?> clazz) {
         return this.unsafe.arrayBaseOffset(clazz);
      }

      public final int arrayIndexScale(Class<?> clazz) {
         return this.unsafe.arrayIndexScale(clazz);
      }

      public abstract byte getByte(long var1);

      public abstract void putByte(long var1, byte var3);

      public abstract int getInt(long var1);

      public abstract void putInt(long var1, int var3);

      public abstract long getLong(long var1);

      public abstract void putLong(long var1, long var3);

      public abstract Object getStaticObject(java.lang.reflect.Field var1);

      public abstract void copyMemory(long var1, byte[] var3, long var4, long var6);

      public abstract void copyMemory(byte[] var1, long var2, long var4, long var6);
   }
}
