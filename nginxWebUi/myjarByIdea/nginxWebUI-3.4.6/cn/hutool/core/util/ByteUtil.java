package cn.hutool.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class ByteUtil {
   public static final ByteOrder DEFAULT_ORDER;
   public static final ByteOrder CPU_ENDIAN;

   public static byte intToByte(int intValue) {
      return (byte)intValue;
   }

   public static int byteToUnsignedInt(byte byteValue) {
      return byteValue & 255;
   }

   public static short bytesToShort(byte[] bytes) {
      return bytesToShort(bytes, DEFAULT_ORDER);
   }

   public static short bytesToShort(byte[] bytes, ByteOrder byteOrder) {
      return bytesToShort(bytes, 0, byteOrder);
   }

   public static short bytesToShort(byte[] bytes, int start, ByteOrder byteOrder) {
      return ByteOrder.LITTLE_ENDIAN == byteOrder ? (short)(bytes[start] & 255 | (bytes[start + 1] & 255) << 8) : (short)(bytes[start + 1] & 255 | (bytes[start] & 255) << 8);
   }

   public static byte[] shortToBytes(short shortValue) {
      return shortToBytes(shortValue, DEFAULT_ORDER);
   }

   public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
      byte[] b = new byte[2];
      if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
         b[0] = (byte)(shortValue & 255);
         b[1] = (byte)(shortValue >> 8 & 255);
      } else {
         b[1] = (byte)(shortValue & 255);
         b[0] = (byte)(shortValue >> 8 & 255);
      }

      return b;
   }

   public static int bytesToInt(byte[] bytes) {
      return bytesToInt(bytes, DEFAULT_ORDER);
   }

   public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
      return bytesToInt(bytes, 0, byteOrder);
   }

   public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
      return ByteOrder.LITTLE_ENDIAN == byteOrder ? bytes[start] & 255 | (bytes[1 + start] & 255) << 8 | (bytes[2 + start] & 255) << 16 | (bytes[3 + start] & 255) << 24 : bytes[3 + start] & 255 | (bytes[2 + start] & 255) << 8 | (bytes[1 + start] & 255) << 16 | (bytes[start] & 255) << 24;
   }

   public static byte[] intToBytes(int intValue) {
      return intToBytes(intValue, DEFAULT_ORDER);
   }

   public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {
      return ByteOrder.LITTLE_ENDIAN == byteOrder ? new byte[]{(byte)(intValue & 255), (byte)(intValue >> 8 & 255), (byte)(intValue >> 16 & 255), (byte)(intValue >> 24 & 255)} : new byte[]{(byte)(intValue >> 24 & 255), (byte)(intValue >> 16 & 255), (byte)(intValue >> 8 & 255), (byte)(intValue & 255)};
   }

   public static byte[] longToBytes(long longValue) {
      return longToBytes(longValue, DEFAULT_ORDER);
   }

   public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
      byte[] result = new byte[8];
      int i;
      if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
         for(i = 0; i < result.length; ++i) {
            result[i] = (byte)((int)(longValue & 255L));
            longValue >>= 8;
         }
      } else {
         for(i = result.length - 1; i >= 0; --i) {
            result[i] = (byte)((int)(longValue & 255L));
            longValue >>= 8;
         }
      }

      return result;
   }

   public static long bytesToLong(byte[] bytes) {
      return bytesToLong(bytes, DEFAULT_ORDER);
   }

   public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
      return bytesToLong(bytes, 0, byteOrder);
   }

   public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
      long values = 0L;
      int i;
      if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
         for(i = 7; i >= 0; --i) {
            values <<= 8;
            values |= (long)(bytes[i + start] & 255);
         }
      } else {
         for(i = 0; i < 8; ++i) {
            values <<= 8;
            values |= (long)(bytes[i + start] & 255);
         }
      }

      return values;
   }

   public static byte[] floatToBytes(float floatValue) {
      return floatToBytes(floatValue, DEFAULT_ORDER);
   }

   public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
      return intToBytes(Float.floatToIntBits(floatValue), byteOrder);
   }

   public static double bytesToFloat(byte[] bytes) {
      return (double)bytesToFloat(bytes, DEFAULT_ORDER);
   }

   public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
      return Float.intBitsToFloat(bytesToInt(bytes, byteOrder));
   }

   public static byte[] doubleToBytes(double doubleValue) {
      return doubleToBytes(doubleValue, DEFAULT_ORDER);
   }

   public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
      return longToBytes(Double.doubleToLongBits(doubleValue), byteOrder);
   }

   public static double bytesToDouble(byte[] bytes) {
      return bytesToDouble(bytes, DEFAULT_ORDER);
   }

   public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
      return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
   }

   public static byte[] numberToBytes(Number number) {
      return numberToBytes(number, DEFAULT_ORDER);
   }

   public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
      if (number instanceof Byte) {
         return new byte[]{number.byteValue()};
      } else if (number instanceof Double) {
         return doubleToBytes((Double)number, byteOrder);
      } else if (number instanceof Long) {
         return longToBytes((Long)number, byteOrder);
      } else if (number instanceof Integer) {
         return intToBytes((Integer)number, byteOrder);
      } else if (number instanceof Short) {
         return shortToBytes((Short)number, byteOrder);
      } else {
         return number instanceof Float ? floatToBytes((Float)number, byteOrder) : doubleToBytes(number.doubleValue(), byteOrder);
      }
   }

   public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder) throws IllegalArgumentException {
      Object number;
      if (Byte.class == targetClass) {
         number = bytes[0];
      } else if (Short.class == targetClass) {
         number = bytesToShort(bytes, byteOrder);
      } else if (Integer.class == targetClass) {
         number = bytesToInt(bytes, byteOrder);
      } else if (AtomicInteger.class == targetClass) {
         number = new AtomicInteger(bytesToInt(bytes, byteOrder));
      } else if (Long.class == targetClass) {
         number = bytesToLong(bytes, byteOrder);
      } else if (AtomicLong.class == targetClass) {
         number = new AtomicLong(bytesToLong(bytes, byteOrder));
      } else if (LongAdder.class == targetClass) {
         LongAdder longValue = new LongAdder();
         longValue.add(bytesToLong(bytes, byteOrder));
         number = longValue;
      } else if (Float.class == targetClass) {
         number = bytesToFloat(bytes, byteOrder);
      } else if (Double.class == targetClass) {
         number = bytesToDouble(bytes, byteOrder);
      } else if (DoubleAdder.class == targetClass) {
         DoubleAdder doubleAdder = new DoubleAdder();
         doubleAdder.add(bytesToDouble(bytes, byteOrder));
         number = doubleAdder;
      } else if (BigDecimal.class == targetClass) {
         number = NumberUtil.toBigDecimal((Number)bytesToDouble(bytes, byteOrder));
      } else if (BigInteger.class == targetClass) {
         number = BigInteger.valueOf(bytesToLong(bytes, byteOrder));
      } else {
         if (Number.class != targetClass) {
            throw new IllegalArgumentException("Unsupported Number type: " + targetClass.getName());
         }

         number = bytesToDouble(bytes, byteOrder);
      }

      return (Number)number;
   }

   static {
      DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
      CPU_ENDIAN = "little".equals(System.getProperty("sun.cpu.endian")) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
   }
}
