package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

public class NumberConverter extends AbstractConverter<Number> {
   private static final long serialVersionUID = 1L;
   private final Class<? extends Number> targetType;

   public NumberConverter() {
      this.targetType = Number.class;
   }

   public NumberConverter(Class<? extends Number> clazz) {
      this.targetType = null == clazz ? Number.class : clazz;
   }

   public Class<Number> getTargetType() {
      return this.targetType;
   }

   protected Number convertInternal(Object value) {
      return convert(value, this.targetType, this::convertToStr);
   }

   protected String convertToStr(Object value) {
      String result = StrUtil.trim(super.convertToStr(value));
      if (StrUtil.isNotEmpty(result)) {
         char c = Character.toUpperCase(result.charAt(result.length() - 1));
         if (c == 'D' || c == 'L' || c == 'F') {
            return StrUtil.subPre(result, -1);
         }
      }

      return result;
   }

   protected static Number convert(Object value, Class<? extends Number> targetType, Function<Object, String> toStrFunc) {
      if (value instanceof Enum) {
         return convert(((Enum)value).ordinal(), targetType, toStrFunc);
      } else if (value instanceof byte[]) {
         return ByteUtil.bytesToNumber((byte[])((byte[])value), targetType, ByteUtil.DEFAULT_ORDER);
      } else {
         String valueStr;
         if (Byte.class == targetType) {
            if (value instanceof Number) {
               return ((Number)value).byteValue();
            } else if (value instanceof Boolean) {
               return BooleanUtil.toByteObj((Boolean)value);
            } else {
               valueStr = (String)toStrFunc.apply(value);

               try {
                  return StrUtil.isBlank(valueStr) ? null : Byte.valueOf(valueStr);
               } catch (NumberFormatException var5) {
                  return NumberUtil.parseNumber(valueStr).byteValue();
               }
            }
         } else if (Short.class == targetType) {
            if (value instanceof Number) {
               return ((Number)value).shortValue();
            } else if (value instanceof Boolean) {
               return BooleanUtil.toShortObj((Boolean)value);
            } else {
               valueStr = (String)toStrFunc.apply(value);

               try {
                  return StrUtil.isBlank(valueStr) ? null : Short.valueOf(valueStr);
               } catch (NumberFormatException var6) {
                  return NumberUtil.parseNumber(valueStr).shortValue();
               }
            }
         } else if (Integer.class == targetType) {
            if (value instanceof Number) {
               return ((Number)value).intValue();
            } else if (value instanceof Boolean) {
               return BooleanUtil.toInteger((Boolean)value);
            } else if (value instanceof Date) {
               return (int)((Date)value).getTime();
            } else if (value instanceof Calendar) {
               return (int)((Calendar)value).getTimeInMillis();
            } else if (value instanceof TemporalAccessor) {
               return (int)DateUtil.toInstant((TemporalAccessor)value).toEpochMilli();
            } else {
               valueStr = (String)toStrFunc.apply(value);
               return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseInt(valueStr);
            }
         } else {
            Number number;
            if (AtomicInteger.class == targetType) {
               number = convert(value, Integer.class, toStrFunc);
               if (null != number) {
                  return new AtomicInteger(number.intValue());
               }
            } else {
               if (Long.class == targetType) {
                  if (value instanceof Number) {
                     return ((Number)value).longValue();
                  }

                  if (value instanceof Boolean) {
                     return BooleanUtil.toLongObj((Boolean)value);
                  }

                  if (value instanceof Date) {
                     return ((Date)value).getTime();
                  }

                  if (value instanceof Calendar) {
                     return ((Calendar)value).getTimeInMillis();
                  }

                  if (value instanceof TemporalAccessor) {
                     return DateUtil.toInstant((TemporalAccessor)value).toEpochMilli();
                  }

                  valueStr = (String)toStrFunc.apply(value);
                  return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseLong(valueStr);
               }

               if (AtomicLong.class == targetType) {
                  number = convert(value, Long.class, toStrFunc);
                  if (null != number) {
                     return new AtomicLong(number.longValue());
                  }
               } else if (LongAdder.class == targetType) {
                  number = convert(value, Long.class, toStrFunc);
                  if (null != number) {
                     LongAdder longValue = new LongAdder();
                     longValue.add(number.longValue());
                     return longValue;
                  }
               } else {
                  if (Float.class == targetType) {
                     if (value instanceof Number) {
                        return ((Number)value).floatValue();
                     }

                     if (value instanceof Boolean) {
                        return BooleanUtil.toFloatObj((Boolean)value);
                     }

                     valueStr = (String)toStrFunc.apply(value);
                     return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseFloat(valueStr);
                  }

                  if (Double.class == targetType) {
                     if (value instanceof Number) {
                        return NumberUtil.toDouble((Number)value);
                     }

                     if (value instanceof Boolean) {
                        return BooleanUtil.toDoubleObj((Boolean)value);
                     }

                     valueStr = (String)toStrFunc.apply(value);
                     return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseDouble(valueStr);
                  }

                  if (DoubleAdder.class == targetType) {
                     number = convert(value, Double.class, toStrFunc);
                     if (null != number) {
                        DoubleAdder doubleAdder = new DoubleAdder();
                        doubleAdder.add(number.doubleValue());
                        return doubleAdder;
                     }
                  } else {
                     if (BigDecimal.class == targetType) {
                        return toBigDecimal(value, toStrFunc);
                     }

                     if (BigInteger.class == targetType) {
                        return toBigInteger(value, toStrFunc);
                     }

                     if (Number.class == targetType) {
                        if (value instanceof Number) {
                           return (Number)value;
                        }

                        if (value instanceof Boolean) {
                           return BooleanUtil.toInteger((Boolean)value);
                        }

                        valueStr = (String)toStrFunc.apply(value);
                        return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseNumber(valueStr);
                     }
                  }
               }
            }

            throw new UnsupportedOperationException(StrUtil.format("Unsupport Number type: {}", new Object[]{targetType.getName()}));
         }
      }
   }

   private static BigDecimal toBigDecimal(Object value, Function<Object, String> toStrFunc) {
      if (value instanceof Number) {
         return NumberUtil.toBigDecimal((Number)value);
      } else if (value instanceof Boolean) {
         return (Boolean)value ? BigDecimal.ONE : BigDecimal.ZERO;
      } else {
         return NumberUtil.toBigDecimal((String)toStrFunc.apply(value));
      }
   }

   private static BigInteger toBigInteger(Object value, Function<Object, String> toStrFunc) {
      if (value instanceof Long) {
         return BigInteger.valueOf((Long)value);
      } else if (value instanceof Boolean) {
         return (Boolean)value ? BigInteger.ONE : BigInteger.ZERO;
      } else {
         return NumberUtil.toBigInteger((String)toStrFunc.apply(value));
      }
   }
}
