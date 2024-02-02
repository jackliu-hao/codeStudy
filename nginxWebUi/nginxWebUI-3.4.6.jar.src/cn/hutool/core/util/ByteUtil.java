/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.DoubleAdder;
/*     */ import java.util.concurrent.atomic.LongAdder;
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
/*     */ public class ByteUtil
/*     */ {
/*  30 */   public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final ByteOrder CPU_ENDIAN = "little".equals(System.getProperty("sun.cpu.endian")) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte intToByte(int intValue) {
/*  43 */     return (byte)intValue;
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
/*     */   public static int byteToUnsignedInt(byte byteValue) {
/*  55 */     return byteValue & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short bytesToShort(byte[] bytes) {
/*  66 */     return bytesToShort(bytes, DEFAULT_ORDER);
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
/*     */   public static short bytesToShort(byte[] bytes, ByteOrder byteOrder) {
/*  78 */     return bytesToShort(bytes, 0, byteOrder);
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
/*     */   public static short bytesToShort(byte[] bytes, int start, ByteOrder byteOrder) {
/*  91 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder)
/*     */     {
/*  93 */       return (short)(bytes[start] & 0xFF | (bytes[start + 1] & 0xFF) << 8);
/*     */     }
/*  95 */     return (short)(bytes[start + 1] & 0xFF | (bytes[start] & 0xFF) << 8);
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
/*     */   public static byte[] shortToBytes(short shortValue) {
/* 107 */     return shortToBytes(shortValue, DEFAULT_ORDER);
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
/*     */   public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
/* 119 */     byte[] b = new byte[2];
/* 120 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
/* 121 */       b[0] = (byte)(shortValue & 0xFF);
/* 122 */       b[1] = (byte)(shortValue >> 8 & 0xFF);
/*     */     } else {
/* 124 */       b[1] = (byte)(shortValue & 0xFF);
/* 125 */       b[0] = (byte)(shortValue >> 8 & 0xFF);
/*     */     } 
/* 127 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int bytesToInt(byte[] bytes) {
/* 138 */     return bytesToInt(bytes, DEFAULT_ORDER);
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
/*     */   public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
/* 150 */     return bytesToInt(bytes, 0, byteOrder);
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
/*     */   public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
/* 164 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
/* 165 */       return bytes[start] & 0xFF | (bytes[1 + start] & 0xFF) << 8 | (bytes[2 + start] & 0xFF) << 16 | (bytes[3 + start] & 0xFF) << 24;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 170 */     return bytes[3 + start] & 0xFF | (bytes[2 + start] & 0xFF) << 8 | (bytes[1 + start] & 0xFF) << 16 | (bytes[start] & 0xFF) << 24;
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
/*     */   public static byte[] intToBytes(int intValue) {
/* 186 */     return intToBytes(intValue, DEFAULT_ORDER);
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
/*     */   public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {
/* 199 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
/* 200 */       return new byte[] { (byte)(intValue & 0xFF), (byte)(intValue >> 8 & 0xFF), (byte)(intValue >> 16 & 0xFF), (byte)(intValue >> 24 & 0xFF) };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 208 */     return new byte[] { (byte)(intValue >> 24 & 0xFF), (byte)(intValue >> 16 & 0xFF), (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
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
/*     */   public static byte[] longToBytes(long longValue) {
/* 227 */     return longToBytes(longValue, DEFAULT_ORDER);
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
/*     */   public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
/* 240 */     byte[] result = new byte[8];
/* 241 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
/* 242 */       for (int i = 0; i < result.length; i++) {
/* 243 */         result[i] = (byte)(int)(longValue & 0xFFL);
/* 244 */         longValue >>= 8L;
/*     */       } 
/*     */     } else {
/* 247 */       for (int i = result.length - 1; i >= 0; i--) {
/* 248 */         result[i] = (byte)(int)(longValue & 0xFFL);
/* 249 */         longValue >>= 8L;
/*     */       } 
/*     */     } 
/* 252 */     return result;
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
/*     */   public static long bytesToLong(byte[] bytes) {
/* 264 */     return bytesToLong(bytes, DEFAULT_ORDER);
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
/*     */   public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
/* 277 */     return bytesToLong(bytes, 0, byteOrder);
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
/*     */   public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
/* 292 */     long values = 0L;
/* 293 */     if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
/* 294 */       for (int i = 7; i >= 0; i--) {
/* 295 */         values <<= 8L;
/* 296 */         values |= (bytes[i + start] & 0xFF);
/*     */       } 
/*     */     } else {
/* 299 */       for (int i = 0; i < 8; i++) {
/* 300 */         values <<= 8L;
/* 301 */         values |= (bytes[i + start] & 0xFF);
/*     */       } 
/*     */     } 
/*     */     
/* 305 */     return values;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] floatToBytes(float floatValue) {
/* 316 */     return floatToBytes(floatValue, DEFAULT_ORDER);
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
/*     */   public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
/* 328 */     return intToBytes(Float.floatToIntBits(floatValue), byteOrder);
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
/*     */   public static double bytesToFloat(byte[] bytes) {
/* 340 */     return bytesToFloat(bytes, DEFAULT_ORDER);
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
/*     */   public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
/* 353 */     return Float.intBitsToFloat(bytesToInt(bytes, byteOrder));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] doubleToBytes(double doubleValue) {
/* 364 */     return doubleToBytes(doubleValue, DEFAULT_ORDER);
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
/*     */   public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
/* 377 */     return longToBytes(Double.doubleToLongBits(doubleValue), byteOrder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double bytesToDouble(byte[] bytes) {
/* 388 */     return bytesToDouble(bytes, DEFAULT_ORDER);
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
/*     */   public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
/* 400 */     return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] numberToBytes(Number number) {
/* 410 */     return numberToBytes(number, DEFAULT_ORDER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
/* 421 */     if (number instanceof Byte)
/* 422 */       return new byte[] { number.byteValue() }; 
/* 423 */     if (number instanceof Double)
/* 424 */       return doubleToBytes(((Double)number).doubleValue(), byteOrder); 
/* 425 */     if (number instanceof Long)
/* 426 */       return longToBytes(((Long)number).longValue(), byteOrder); 
/* 427 */     if (number instanceof Integer)
/* 428 */       return intToBytes(((Integer)number).intValue(), byteOrder); 
/* 429 */     if (number instanceof Short)
/* 430 */       return shortToBytes(((Short)number).shortValue(), byteOrder); 
/* 431 */     if (number instanceof Float) {
/* 432 */       return floatToBytes(((Float)number).floatValue(), byteOrder);
/*     */     }
/* 434 */     return doubleToBytes(number.doubleValue(), byteOrder);
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
/*     */   public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder) throws IllegalArgumentException {
/*     */     Number number;
/* 451 */     if (Byte.class == targetClass) {
/* 452 */       number = Byte.valueOf(bytes[0]);
/* 453 */     } else if (Short.class == targetClass) {
/* 454 */       number = Short.valueOf(bytesToShort(bytes, byteOrder));
/* 455 */     } else if (Integer.class == targetClass) {
/* 456 */       number = Integer.valueOf(bytesToInt(bytes, byteOrder));
/* 457 */     } else if (AtomicInteger.class == targetClass) {
/* 458 */       number = new AtomicInteger(bytesToInt(bytes, byteOrder));
/* 459 */     } else if (Long.class == targetClass) {
/* 460 */       number = Long.valueOf(bytesToLong(bytes, byteOrder));
/* 461 */     } else if (AtomicLong.class == targetClass) {
/* 462 */       number = new AtomicLong(bytesToLong(bytes, byteOrder));
/* 463 */     } else if (LongAdder.class == targetClass) {
/* 464 */       LongAdder longValue = new LongAdder();
/* 465 */       longValue.add(bytesToLong(bytes, byteOrder));
/* 466 */       number = longValue;
/* 467 */     } else if (Float.class == targetClass) {
/* 468 */       number = Float.valueOf(bytesToFloat(bytes, byteOrder));
/* 469 */     } else if (Double.class == targetClass) {
/* 470 */       number = Double.valueOf(bytesToDouble(bytes, byteOrder));
/* 471 */     } else if (DoubleAdder.class == targetClass) {
/* 472 */       DoubleAdder doubleAdder = new DoubleAdder();
/* 473 */       doubleAdder.add(bytesToDouble(bytes, byteOrder));
/* 474 */       number = doubleAdder;
/* 475 */     } else if (BigDecimal.class == targetClass) {
/* 476 */       number = NumberUtil.toBigDecimal(Double.valueOf(bytesToDouble(bytes, byteOrder)));
/* 477 */     } else if (BigInteger.class == targetClass) {
/* 478 */       number = BigInteger.valueOf(bytesToLong(bytes, byteOrder));
/* 479 */     } else if (Number.class == targetClass) {
/*     */       
/* 481 */       number = Double.valueOf(bytesToDouble(bytes, byteOrder));
/*     */     } else {
/*     */       
/* 484 */       throw new IllegalArgumentException("Unsupported Number type: " + targetClass.getName());
/*     */     } 
/*     */     
/* 487 */     return (T)number;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ByteUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */