package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NumberUtil;
import java.math.BigDecimal;
import java.math.BigInteger;

class OverloadedNumberUtil {
   static final int BIG_MANTISSA_LOSS_PRICE = 40000;
   private static final long MAX_DOUBLE_OR_LONG = 9007199254740992L;
   private static final long MIN_DOUBLE_OR_LONG = -9007199254740992L;
   private static final int MAX_DOUBLE_OR_LONG_LOG_2 = 53;
   private static final int MAX_FLOAT_OR_INT = 16777216;
   private static final int MIN_FLOAT_OR_INT = -16777216;
   private static final int MAX_FLOAT_OR_INT_LOG_2 = 24;
   private static final double LOWEST_ABOVE_ZERO = 1.0E-6;
   private static final double HIGHEST_BELOW_ONE = 0.999999;

   private OverloadedNumberUtil() {
   }

   static Number addFallbackType(Number num, int typeFlags) {
      Class numClass = num.getClass();
      if (numClass == BigDecimal.class) {
         BigDecimal n = (BigDecimal)num;
         return (Number)((typeFlags & 316) != 0 && (typeFlags & 704) != 0 && NumberUtil.isIntegerBigDecimal(n) ? new IntegerBigDecimal(n) : n);
      } else {
         int pn;
         if (numClass == Integer.class) {
            pn = num.intValue();
            if ((typeFlags & 4) != 0 && pn <= 127 && pn >= -128) {
               return new IntegerOrByte((Integer)num, (byte)pn);
            } else {
               return (Number)((typeFlags & 8) != 0 && pn <= 32767 && pn >= -32768 ? new IntegerOrShort((Integer)num, (short)pn) : num);
            }
         } else if (numClass == Long.class) {
            long pn = num.longValue();
            if ((typeFlags & 4) != 0 && pn <= 127L && pn >= -128L) {
               return new LongOrByte((Long)num, (byte)((int)pn));
            } else if ((typeFlags & 8) != 0 && pn <= 32767L && pn >= -32768L) {
               return new LongOrShort((Long)num, (short)((int)pn));
            } else {
               return (Number)((typeFlags & 16) != 0 && pn <= 2147483647L && pn >= -2147483648L ? new LongOrInteger((Long)num, (int)pn) : num);
            }
         } else if (numClass == Double.class) {
            double doubleN = num.doubleValue();
            if ((typeFlags & 316) != 0 && !(doubleN > 9.007199254740992E15) && !(doubleN < -9.007199254740992E15)) {
               long longN = num.longValue();
               double diff = doubleN - (double)longN;
               boolean exact;
               if (diff == 0.0) {
                  exact = true;
               } else if (diff > 0.0) {
                  if (diff < 1.0E-6) {
                     exact = false;
                  } else {
                     if (!(diff > 0.999999)) {
                        return (Number)((typeFlags & 64) != 0 && doubleN >= -3.4028234663852886E38 && doubleN <= 3.4028234663852886E38 ? new DoubleOrFloat((Double)num) : num);
                     }

                     exact = false;
                     ++longN;
                  }
               } else if (diff > -1.0E-6) {
                  exact = false;
               } else {
                  if (!(diff < -0.999999)) {
                     return (Number)((typeFlags & 64) != 0 && doubleN >= -3.4028234663852886E38 && doubleN <= 3.4028234663852886E38 ? new DoubleOrFloat((Double)num) : num);
                  }

                  exact = false;
                  --longN;
               }

               if ((typeFlags & 4) != 0 && longN <= 127L && longN >= -128L) {
                  return new DoubleOrByte((Double)num, (byte)((int)longN));
               }

               if ((typeFlags & 8) != 0 && longN <= 32767L && longN >= -32768L) {
                  return new DoubleOrShort((Double)num, (short)((int)longN));
               }

               if ((typeFlags & 16) != 0 && longN <= 2147483647L && longN >= -2147483648L) {
                  int intN = (int)longN;
                  return (Number)((typeFlags & 64) != 0 && intN >= -16777216 && intN <= 16777216 ? new DoubleOrIntegerOrFloat((Double)num, intN) : new DoubleOrInteger((Double)num, intN));
               }

               if ((typeFlags & 32) != 0) {
                  if (exact) {
                     return new DoubleOrLong((Double)num, longN);
                  }

                  if (longN >= -2147483648L && longN <= 2147483647L) {
                     return new DoubleOrLong((Double)num, longN);
                  }
               }
            }

            return (Number)((typeFlags & 64) != 0 && doubleN >= -3.4028234663852886E38 && doubleN <= 3.4028234663852886E38 ? new DoubleOrFloat((Double)num) : num);
         } else {
            int bitLength;
            if (numClass == Float.class) {
               float floatN = num.floatValue();
               if ((typeFlags & 316) != 0 && !(floatN > 1.6777216E7F) && !(floatN < -1.6777216E7F)) {
                  bitLength = num.intValue();
                  double diff = (double)(floatN - (float)bitLength);
                  boolean exact;
                  if (diff == 0.0) {
                     exact = true;
                  } else {
                     if (bitLength < -128 || bitLength > 127) {
                        return num;
                     }

                     if (diff > 0.0) {
                        if (diff < 1.0E-5) {
                           exact = false;
                        } else {
                           if (!(diff > 0.99999)) {
                              return num;
                           }

                           exact = false;
                           ++bitLength;
                        }
                     } else if (diff > -1.0E-5) {
                        exact = false;
                     } else {
                        if (!(diff < -0.99999)) {
                           return num;
                        }

                        exact = false;
                        --bitLength;
                     }
                  }

                  if ((typeFlags & 4) != 0 && bitLength <= 127 && bitLength >= -128) {
                     return new FloatOrByte((Float)num, (byte)bitLength);
                  }

                  if ((typeFlags & 8) != 0 && bitLength <= 32767 && bitLength >= -32768) {
                     return new FloatOrShort((Float)num, (short)bitLength);
                  }

                  if ((typeFlags & 16) != 0) {
                     return new FloatOrInteger((Float)num, bitLength);
                  }

                  if ((typeFlags & 32) != 0) {
                     return (Number)(exact ? new FloatOrInteger((Float)num, bitLength) : new FloatOrByte((Float)num, (byte)bitLength));
                  }
               }

               return num;
            } else if (numClass == Byte.class) {
               return num;
            } else if (numClass == Short.class) {
               pn = num.shortValue();
               return (Number)((typeFlags & 4) != 0 && pn <= 127 && pn >= -128 ? new ShortOrByte((Short)num, (byte)pn) : num);
            } else if (numClass == BigInteger.class) {
               if ((typeFlags & 252) == 0) {
                  return num;
               } else {
                  BigInteger biNum = (BigInteger)num;
                  bitLength = biNum.bitLength();
                  if ((typeFlags & 4) != 0 && bitLength <= 7) {
                     return new BigIntegerOrByte(biNum);
                  } else if ((typeFlags & 8) != 0 && bitLength <= 15) {
                     return new BigIntegerOrShort(biNum);
                  } else if ((typeFlags & 16) != 0 && bitLength <= 31) {
                     return new BigIntegerOrInteger(biNum);
                  } else if ((typeFlags & 32) != 0 && bitLength <= 63) {
                     return new BigIntegerOrLong(biNum);
                  } else if ((typeFlags & 64) == 0 || bitLength > 24 && (bitLength != 25 || biNum.getLowestSetBit() < 24)) {
                     return (Number)((typeFlags & 128) == 0 || bitLength > 53 && (bitLength != 54 || biNum.getLowestSetBit() < 53) ? num : new BigIntegerOrDouble(biNum));
                  } else {
                     return new BigIntegerOrFloat(biNum);
                  }
               }
            } else {
               return num;
            }
         }
      }
   }

   static int getArgumentConversionPrice(Class fromC, Class toC) {
      if (toC == fromC) {
         return 0;
      } else if (toC == Integer.class) {
         if (fromC == IntegerBigDecimal.class) {
            return 31003;
         } else if (fromC == BigDecimal.class) {
            return 41003;
         } else if (fromC == Long.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Float.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Byte.class) {
            return 10003;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return 21003;
         } else if (fromC == DoubleOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 22003;
         } else if (fromC == DoubleOrInteger.class) {
            return 22003;
         } else if (fromC == DoubleOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == IntegerOrByte.class) {
            return 0;
         } else if (fromC == DoubleOrByte.class) {
            return 22003;
         } else if (fromC == LongOrByte.class) {
            return 21003;
         } else if (fromC == Short.class) {
            return 10003;
         } else if (fromC == LongOrShort.class) {
            return 21003;
         } else if (fromC == ShortOrByte.class) {
            return 10003;
         } else if (fromC == FloatOrInteger.class) {
            return 21003;
         } else if (fromC == FloatOrByte.class) {
            return 21003;
         } else if (fromC == FloatOrShort.class) {
            return 21003;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 16003;
         } else if (fromC == BigIntegerOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrDouble.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrByte.class) {
            return 16003;
         } else if (fromC == IntegerOrShort.class) {
            return 0;
         } else if (fromC == DoubleOrShort.class) {
            return 22003;
         } else {
            return fromC == BigIntegerOrShort.class ? 16003 : Integer.MAX_VALUE;
         }
      } else if (toC == Long.class) {
         if (fromC == Integer.class) {
            return 10004;
         } else if (fromC == IntegerBigDecimal.class) {
            return 31004;
         } else if (fromC == BigDecimal.class) {
            return 41004;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Float.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Byte.class) {
            return 10004;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return 0;
         } else if (fromC == DoubleOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 21004;
         } else if (fromC == DoubleOrInteger.class) {
            return 21004;
         } else if (fromC == DoubleOrLong.class) {
            return 21004;
         } else if (fromC == IntegerOrByte.class) {
            return 10004;
         } else if (fromC == DoubleOrByte.class) {
            return 21004;
         } else if (fromC == LongOrByte.class) {
            return 0;
         } else if (fromC == Short.class) {
            return 10004;
         } else if (fromC == LongOrShort.class) {
            return 0;
         } else if (fromC == ShortOrByte.class) {
            return 10004;
         } else if (fromC == FloatOrInteger.class) {
            return 21004;
         } else if (fromC == FloatOrByte.class) {
            return 21004;
         } else if (fromC == FloatOrShort.class) {
            return 21004;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 15004;
         } else if (fromC == BigIntegerOrLong.class) {
            return 15004;
         } else if (fromC == BigIntegerOrDouble.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrByte.class) {
            return 15004;
         } else if (fromC == IntegerOrShort.class) {
            return 10004;
         } else if (fromC == DoubleOrShort.class) {
            return 21004;
         } else {
            return fromC == BigIntegerOrShort.class ? 15004 : Integer.MAX_VALUE;
         }
      } else if (toC == Double.class) {
         if (fromC == Integer.class) {
            return 20007;
         } else if (fromC == IntegerBigDecimal.class) {
            return 32007;
         } else if (fromC == BigDecimal.class) {
            return 32007;
         } else if (fromC == Long.class) {
            return 30007;
         } else if (fromC == Float.class) {
            return 10007;
         } else if (fromC == Byte.class) {
            return 20007;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return 21007;
         } else if (fromC == DoubleOrFloat.class) {
            return 0;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 0;
         } else if (fromC == DoubleOrInteger.class) {
            return 0;
         } else if (fromC == DoubleOrLong.class) {
            return 0;
         } else if (fromC == IntegerOrByte.class) {
            return 20007;
         } else if (fromC == DoubleOrByte.class) {
            return 0;
         } else if (fromC == LongOrByte.class) {
            return 21007;
         } else if (fromC == Short.class) {
            return 20007;
         } else if (fromC == LongOrShort.class) {
            return 21007;
         } else if (fromC == ShortOrByte.class) {
            return 20007;
         } else if (fromC == FloatOrInteger.class) {
            return 10007;
         } else if (fromC == FloatOrByte.class) {
            return 10007;
         } else if (fromC == FloatOrShort.class) {
            return 10007;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 20007;
         } else if (fromC == BigIntegerOrLong.class) {
            return 30007;
         } else if (fromC == BigIntegerOrDouble.class) {
            return 20007;
         } else if (fromC == BigIntegerOrFloat.class) {
            return 20007;
         } else if (fromC == BigIntegerOrByte.class) {
            return 20007;
         } else if (fromC == IntegerOrShort.class) {
            return 20007;
         } else if (fromC == DoubleOrShort.class) {
            return 0;
         } else {
            return fromC == BigIntegerOrShort.class ? 20007 : Integer.MAX_VALUE;
         }
      } else if (toC == Float.class) {
         if (fromC == Integer.class) {
            return 30006;
         } else if (fromC == IntegerBigDecimal.class) {
            return 33006;
         } else if (fromC == BigDecimal.class) {
            return 33006;
         } else if (fromC == Long.class) {
            return 40006;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Byte.class) {
            return 20006;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return 30006;
         } else if (fromC == DoubleOrFloat.class) {
            return 30006;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 23006;
         } else if (fromC == DoubleOrInteger.class) {
            return 30006;
         } else if (fromC == DoubleOrLong.class) {
            return 40006;
         } else if (fromC == IntegerOrByte.class) {
            return 24006;
         } else if (fromC == DoubleOrByte.class) {
            return 23006;
         } else if (fromC == LongOrByte.class) {
            return 24006;
         } else if (fromC == Short.class) {
            return 20006;
         } else if (fromC == LongOrShort.class) {
            return 24006;
         } else if (fromC == ShortOrByte.class) {
            return 20006;
         } else if (fromC == FloatOrInteger.class) {
            return 0;
         } else if (fromC == FloatOrByte.class) {
            return 0;
         } else if (fromC == FloatOrShort.class) {
            return 0;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 30006;
         } else if (fromC == BigIntegerOrLong.class) {
            return 40006;
         } else if (fromC == BigIntegerOrDouble.class) {
            return 40006;
         } else if (fromC == BigIntegerOrFloat.class) {
            return 24006;
         } else if (fromC == BigIntegerOrByte.class) {
            return 24006;
         } else if (fromC == IntegerOrShort.class) {
            return 24006;
         } else if (fromC == DoubleOrShort.class) {
            return 23006;
         } else {
            return fromC == BigIntegerOrShort.class ? 24006 : Integer.MAX_VALUE;
         }
      } else if (toC == Byte.class) {
         if (fromC == Integer.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == IntegerBigDecimal.class) {
            return 35001;
         } else if (fromC == BigDecimal.class) {
            return 45001;
         } else if (fromC == Long.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Float.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == IntegerOrByte.class) {
            return 22001;
         } else if (fromC == DoubleOrByte.class) {
            return 25001;
         } else if (fromC == LongOrByte.class) {
            return 23001;
         } else if (fromC == Short.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrShort.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == ShortOrByte.class) {
            return 21001;
         } else if (fromC == FloatOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == FloatOrByte.class) {
            return 23001;
         } else if (fromC == FloatOrShort.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrDouble.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrByte.class) {
            return 18001;
         } else if (fromC == IntegerOrShort.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrShort.class) {
            return Integer.MAX_VALUE;
         } else {
            return fromC == BigIntegerOrShort.class ? Integer.MAX_VALUE : Integer.MAX_VALUE;
         }
      } else if (toC == Short.class) {
         if (fromC == Integer.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == IntegerBigDecimal.class) {
            return 34002;
         } else if (fromC == BigDecimal.class) {
            return 44002;
         } else if (fromC == Long.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Float.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Byte.class) {
            return 10002;
         } else if (fromC == BigInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == LongOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == IntegerOrByte.class) {
            return 21002;
         } else if (fromC == DoubleOrByte.class) {
            return 24002;
         } else if (fromC == LongOrByte.class) {
            return 22002;
         } else if (fromC == LongOrShort.class) {
            return 22002;
         } else if (fromC == ShortOrByte.class) {
            return 0;
         } else if (fromC == FloatOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == FloatOrByte.class) {
            return 22002;
         } else if (fromC == FloatOrShort.class) {
            return 22002;
         } else if (fromC == BigIntegerOrInteger.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrLong.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrDouble.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == BigIntegerOrByte.class) {
            return 17002;
         } else if (fromC == IntegerOrShort.class) {
            return 21002;
         } else if (fromC == DoubleOrShort.class) {
            return 24002;
         } else {
            return fromC == BigIntegerOrShort.class ? 17002 : Integer.MAX_VALUE;
         }
      } else if (toC == BigDecimal.class) {
         if (fromC == Integer.class) {
            return 20008;
         } else if (fromC == IntegerBigDecimal.class) {
            return 0;
         } else if (fromC == Long.class) {
            return 20008;
         } else if (fromC == Double.class) {
            return 20008;
         } else if (fromC == Float.class) {
            return 20008;
         } else if (fromC == Byte.class) {
            return 20008;
         } else if (fromC == BigInteger.class) {
            return 10008;
         } else if (fromC == LongOrInteger.class) {
            return 20008;
         } else if (fromC == DoubleOrFloat.class) {
            return 20008;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 20008;
         } else if (fromC == DoubleOrInteger.class) {
            return 20008;
         } else if (fromC == DoubleOrLong.class) {
            return 20008;
         } else if (fromC == IntegerOrByte.class) {
            return 20008;
         } else if (fromC == DoubleOrByte.class) {
            return 20008;
         } else if (fromC == LongOrByte.class) {
            return 20008;
         } else if (fromC == Short.class) {
            return 20008;
         } else if (fromC == LongOrShort.class) {
            return 20008;
         } else if (fromC == ShortOrByte.class) {
            return 20008;
         } else if (fromC == FloatOrInteger.class) {
            return 20008;
         } else if (fromC == FloatOrByte.class) {
            return 20008;
         } else if (fromC == FloatOrShort.class) {
            return 20008;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 10008;
         } else if (fromC == BigIntegerOrLong.class) {
            return 10008;
         } else if (fromC == BigIntegerOrDouble.class) {
            return 10008;
         } else if (fromC == BigIntegerOrFloat.class) {
            return 10008;
         } else if (fromC == BigIntegerOrByte.class) {
            return 10008;
         } else if (fromC == IntegerOrShort.class) {
            return 20008;
         } else if (fromC == DoubleOrShort.class) {
            return 20008;
         } else {
            return fromC == BigIntegerOrShort.class ? 10008 : Integer.MAX_VALUE;
         }
      } else if (toC == BigInteger.class) {
         if (fromC == Integer.class) {
            return 10005;
         } else if (fromC == IntegerBigDecimal.class) {
            return 10005;
         } else if (fromC == BigDecimal.class) {
            return 40005;
         } else if (fromC == Long.class) {
            return 10005;
         } else if (fromC == Double.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Float.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == Byte.class) {
            return 10005;
         } else if (fromC == LongOrInteger.class) {
            return 10005;
         } else if (fromC == DoubleOrFloat.class) {
            return Integer.MAX_VALUE;
         } else if (fromC == DoubleOrIntegerOrFloat.class) {
            return 21005;
         } else if (fromC == DoubleOrInteger.class) {
            return 21005;
         } else if (fromC == DoubleOrLong.class) {
            return 21005;
         } else if (fromC == IntegerOrByte.class) {
            return 10005;
         } else if (fromC == DoubleOrByte.class) {
            return 21005;
         } else if (fromC == LongOrByte.class) {
            return 10005;
         } else if (fromC == Short.class) {
            return 10005;
         } else if (fromC == LongOrShort.class) {
            return 10005;
         } else if (fromC == ShortOrByte.class) {
            return 10005;
         } else if (fromC == FloatOrInteger.class) {
            return 25005;
         } else if (fromC == FloatOrByte.class) {
            return 25005;
         } else if (fromC == FloatOrShort.class) {
            return 25005;
         } else if (fromC == BigIntegerOrInteger.class) {
            return 0;
         } else if (fromC == BigIntegerOrLong.class) {
            return 0;
         } else if (fromC == BigIntegerOrDouble.class) {
            return 0;
         } else if (fromC == BigIntegerOrFloat.class) {
            return 0;
         } else if (fromC == BigIntegerOrByte.class) {
            return 0;
         } else if (fromC == IntegerOrShort.class) {
            return 10005;
         } else if (fromC == DoubleOrShort.class) {
            return 21005;
         } else {
            return fromC == BigIntegerOrShort.class ? 0 : Integer.MAX_VALUE;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   static int compareNumberTypeSpecificity(Class c1, Class c2) {
      c1 = ClassUtil.primitiveClassToBoxingClass(c1);
      c2 = ClassUtil.primitiveClassToBoxingClass(c2);
      if (c1 == c2) {
         return 0;
      } else if (c1 == Integer.class) {
         if (c2 == Long.class) {
            return 1;
         } else if (c2 == Double.class) {
            return 4;
         } else if (c2 == Float.class) {
            return 3;
         } else if (c2 == Byte.class) {
            return -2;
         } else if (c2 == Short.class) {
            return -1;
         } else if (c2 == BigDecimal.class) {
            return 5;
         } else {
            return c2 == BigInteger.class ? 2 : 0;
         }
      } else if (c1 == Long.class) {
         if (c2 == Integer.class) {
            return -1;
         } else if (c2 == Double.class) {
            return 3;
         } else if (c2 == Float.class) {
            return 2;
         } else if (c2 == Byte.class) {
            return -3;
         } else if (c2 == Short.class) {
            return -2;
         } else if (c2 == BigDecimal.class) {
            return 4;
         } else {
            return c2 == BigInteger.class ? 1 : 0;
         }
      } else if (c1 == Double.class) {
         if (c2 == Integer.class) {
            return -4;
         } else if (c2 == Long.class) {
            return -3;
         } else if (c2 == Float.class) {
            return -1;
         } else if (c2 == Byte.class) {
            return -6;
         } else if (c2 == Short.class) {
            return -5;
         } else if (c2 == BigDecimal.class) {
            return 1;
         } else {
            return c2 == BigInteger.class ? -2 : 0;
         }
      } else if (c1 == Float.class) {
         if (c2 == Integer.class) {
            return -3;
         } else if (c2 == Long.class) {
            return -2;
         } else if (c2 == Double.class) {
            return 1;
         } else if (c2 == Byte.class) {
            return -5;
         } else if (c2 == Short.class) {
            return -4;
         } else if (c2 == BigDecimal.class) {
            return 2;
         } else {
            return c2 == BigInteger.class ? -1 : 0;
         }
      } else if (c1 == Byte.class) {
         if (c2 == Integer.class) {
            return 2;
         } else if (c2 == Long.class) {
            return 3;
         } else if (c2 == Double.class) {
            return 6;
         } else if (c2 == Float.class) {
            return 5;
         } else if (c2 == Short.class) {
            return 1;
         } else if (c2 == BigDecimal.class) {
            return 7;
         } else {
            return c2 == BigInteger.class ? 4 : 0;
         }
      } else if (c1 == Short.class) {
         if (c2 == Integer.class) {
            return 1;
         } else if (c2 == Long.class) {
            return 2;
         } else if (c2 == Double.class) {
            return 5;
         } else if (c2 == Float.class) {
            return 4;
         } else if (c2 == Byte.class) {
            return -1;
         } else if (c2 == BigDecimal.class) {
            return 6;
         } else {
            return c2 == BigInteger.class ? 3 : 0;
         }
      } else if (c1 == BigDecimal.class) {
         if (c2 == Integer.class) {
            return -5;
         } else if (c2 == Long.class) {
            return -4;
         } else if (c2 == Double.class) {
            return -1;
         } else if (c2 == Float.class) {
            return -2;
         } else if (c2 == Byte.class) {
            return -7;
         } else if (c2 == Short.class) {
            return -6;
         } else {
            return c2 == BigInteger.class ? -3 : 0;
         }
      } else if (c1 == BigInteger.class) {
         if (c2 == Integer.class) {
            return -2;
         } else if (c2 == Long.class) {
            return -1;
         } else if (c2 == Double.class) {
            return 2;
         } else if (c2 == Float.class) {
            return 1;
         } else if (c2 == Byte.class) {
            return -4;
         } else if (c2 == Short.class) {
            return -3;
         } else {
            return c2 == BigDecimal.class ? 3 : 0;
         }
      } else {
         return 0;
      }
   }

   static final class BigIntegerOrDouble extends BigIntegerOrFPPrimitive {
      BigIntegerOrDouble(BigInteger n) {
         super(n);
      }
   }

   static final class BigIntegerOrFloat extends BigIntegerOrFPPrimitive {
      BigIntegerOrFloat(BigInteger n) {
         super(n);
      }
   }

   abstract static class BigIntegerOrFPPrimitive extends BigIntegerOrPrimitive {
      BigIntegerOrFPPrimitive(BigInteger n) {
         super(n);
      }

      public float floatValue() {
         return (float)this.n.longValue();
      }

      public double doubleValue() {
         return (double)this.n.longValue();
      }
   }

   static final class BigIntegerOrLong extends BigIntegerOrPrimitive {
      BigIntegerOrLong(BigInteger n) {
         super(n);
      }
   }

   static final class BigIntegerOrInteger extends BigIntegerOrPrimitive {
      BigIntegerOrInteger(BigInteger n) {
         super(n);
      }
   }

   static final class BigIntegerOrShort extends BigIntegerOrPrimitive {
      BigIntegerOrShort(BigInteger n) {
         super(n);
      }
   }

   static final class BigIntegerOrByte extends BigIntegerOrPrimitive {
      BigIntegerOrByte(BigInteger n) {
         super(n);
      }
   }

   abstract static class BigIntegerOrPrimitive extends NumberWithFallbackType {
      protected final BigInteger n;

      BigIntegerOrPrimitive(BigInteger n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }
   }

   static final class FloatOrInteger extends FloatOrWholeNumber {
      private final int w;

      FloatOrInteger(Float n, int w) {
         super(n);
         this.w = w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   static final class FloatOrShort extends FloatOrWholeNumber {
      private final short w;

      FloatOrShort(Float n, short w) {
         super(n);
         this.w = w;
      }

      public short shortValue() {
         return this.w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   static final class FloatOrByte extends FloatOrWholeNumber {
      private final byte w;

      FloatOrByte(Float n, byte w) {
         super(n);
         this.w = w;
      }

      public byte byteValue() {
         return this.w;
      }

      public short shortValue() {
         return (short)this.w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   abstract static class FloatOrWholeNumber extends NumberWithFallbackType {
      private final Float n;

      FloatOrWholeNumber(Float n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public float floatValue() {
         return this.n;
      }
   }

   static final class DoubleOrFloat extends NumberWithFallbackType {
      private final Double n;

      DoubleOrFloat(Double n) {
         this.n = n;
      }

      public float floatValue() {
         return this.n.floatValue();
      }

      public double doubleValue() {
         return this.n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }
   }

   static final class DoubleOrLong extends DoubleOrWholeNumber {
      private final long w;

      DoubleOrLong(Double n, long w) {
         super(n);
         this.w = w;
      }

      public long longValue() {
         return this.w;
      }
   }

   static final class DoubleOrInteger extends DoubleOrWholeNumber {
      private final int w;

      DoubleOrInteger(Double n, int w) {
         super(n);
         this.w = w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   static final class DoubleOrIntegerOrFloat extends DoubleOrWholeNumber {
      private final int w;

      DoubleOrIntegerOrFloat(Double n, int w) {
         super(n);
         this.w = w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   static final class DoubleOrShort extends DoubleOrWholeNumber {
      private final short w;

      DoubleOrShort(Double n, short w) {
         super(n);
         this.w = w;
      }

      public short shortValue() {
         return this.w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   static final class DoubleOrByte extends DoubleOrWholeNumber {
      private final byte w;

      DoubleOrByte(Double n, byte w) {
         super(n);
         this.w = w;
      }

      public byte byteValue() {
         return this.w;
      }

      public short shortValue() {
         return (short)this.w;
      }

      public int intValue() {
         return this.w;
      }

      public long longValue() {
         return (long)this.w;
      }
   }

   abstract static class DoubleOrWholeNumber extends NumberWithFallbackType {
      private final Double n;

      protected DoubleOrWholeNumber(Double n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public double doubleValue() {
         return this.n;
      }
   }

   static class ShortOrByte extends NumberWithFallbackType {
      private final Short n;
      private final byte w;

      protected ShortOrByte(Short n, byte w) {
         this.n = n;
         this.w = w;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public short shortValue() {
         return this.n;
      }

      public byte byteValue() {
         return this.w;
      }
   }

   static class IntegerOrShort extends IntegerOrSmallerInteger {
      private final short w;

      IntegerOrShort(Integer n, short w) {
         super(n);
         this.w = w;
      }

      public short shortValue() {
         return this.w;
      }
   }

   static class IntegerOrByte extends IntegerOrSmallerInteger {
      private final byte w;

      IntegerOrByte(Integer n, byte w) {
         super(n);
         this.w = w;
      }

      public byte byteValue() {
         return this.w;
      }
   }

   abstract static class IntegerOrSmallerInteger extends NumberWithFallbackType {
      private final Integer n;

      protected IntegerOrSmallerInteger(Integer n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public int intValue() {
         return this.n;
      }
   }

   static class LongOrInteger extends LongOrSmallerInteger {
      private final int w;

      LongOrInteger(Long n, int w) {
         super(n);
         this.w = w;
      }

      public int intValue() {
         return this.w;
      }
   }

   static class LongOrShort extends LongOrSmallerInteger {
      private final short w;

      LongOrShort(Long n, short w) {
         super(n);
         this.w = w;
      }

      public short shortValue() {
         return this.w;
      }
   }

   static class LongOrByte extends LongOrSmallerInteger {
      private final byte w;

      LongOrByte(Long n, byte w) {
         super(n);
         this.w = w;
      }

      public byte byteValue() {
         return this.w;
      }
   }

   abstract static class LongOrSmallerInteger extends NumberWithFallbackType {
      private final Long n;

      protected LongOrSmallerInteger(Long n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public long longValue() {
         return this.n;
      }
   }

   static final class IntegerBigDecimal extends NumberWithFallbackType {
      private final BigDecimal n;

      IntegerBigDecimal(BigDecimal n) {
         this.n = n;
      }

      protected Number getSourceNumber() {
         return this.n;
      }

      public BigInteger bigIntegerValue() {
         return this.n.toBigInteger();
      }
   }

   abstract static class NumberWithFallbackType extends Number implements Comparable {
      protected abstract Number getSourceNumber();

      public int intValue() {
         return this.getSourceNumber().intValue();
      }

      public long longValue() {
         return this.getSourceNumber().longValue();
      }

      public float floatValue() {
         return this.getSourceNumber().floatValue();
      }

      public double doubleValue() {
         return this.getSourceNumber().doubleValue();
      }

      public byte byteValue() {
         return this.getSourceNumber().byteValue();
      }

      public short shortValue() {
         return this.getSourceNumber().shortValue();
      }

      public int hashCode() {
         return this.getSourceNumber().hashCode();
      }

      public boolean equals(Object obj) {
         return obj != null && this.getClass() == obj.getClass() ? this.getSourceNumber().equals(((NumberWithFallbackType)obj).getSourceNumber()) : false;
      }

      public String toString() {
         return this.getSourceNumber().toString();
      }

      public int compareTo(Object o) {
         Number n = this.getSourceNumber();
         if (n instanceof Comparable) {
            return ((Comparable)n).compareTo(o);
         } else {
            throw new ClassCastException(n.getClass().getName() + " is not Comparable.");
         }
      }
   }

   interface BigDecimalSource {
      BigDecimal bigDecimalValue();
   }

   interface BigIntegerSource {
      BigInteger bigIntegerValue();
   }

   interface DoubleSource {
      Double doubleValue();
   }

   interface FloatSource {
      Float floatValue();
   }

   interface LongSource {
      Long longValue();
   }

   interface IntegerSource {
      Integer integerValue();
   }

   interface ShortSource {
      Short shortValue();
   }

   interface ByteSource {
      Byte byteValue();
   }
}
