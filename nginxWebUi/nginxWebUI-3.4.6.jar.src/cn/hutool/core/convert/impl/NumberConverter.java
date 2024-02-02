/*     */ package cn.hutool.core.convert.impl;
/*     */ 
/*     */ import cn.hutool.core.convert.AbstractConverter;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.util.BooleanUtil;
/*     */ import cn.hutool.core.util.ByteUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.DoubleAdder;
/*     */ import java.util.concurrent.atomic.LongAdder;
/*     */ import java.util.function.Function;
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
/*     */ public class NumberConverter
/*     */   extends AbstractConverter<Number>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<? extends Number> targetType;
/*     */   
/*     */   public NumberConverter() {
/*  45 */     this.targetType = Number.class;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumberConverter(Class<? extends Number> clazz) {
/*  54 */     this.targetType = (null == clazz) ? Number.class : clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<Number> getTargetType() {
/*  60 */     return (Class)this.targetType;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Number convertInternal(Object value) {
/*  65 */     return convert(value, this.targetType, this::convertToStr);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String convertToStr(Object value) {
/*  70 */     String result = StrUtil.trim(super.convertToStr(value));
/*  71 */     if (StrUtil.isNotEmpty(result)) {
/*  72 */       char c = Character.toUpperCase(result.charAt(result.length() - 1));
/*  73 */       if (c == 'D' || c == 'L' || c == 'F')
/*     */       {
/*  75 */         return StrUtil.subPre(result, -1);
/*     */       }
/*     */     } 
/*     */     
/*  79 */     return result;
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
/*     */   protected static Number convert(Object value, Class<? extends Number> targetType, Function<Object, String> toStrFunc) {
/* 100 */     if (value instanceof Enum) {
/* 101 */       return convert(Integer.valueOf(((Enum)value).ordinal()), targetType, toStrFunc);
/*     */     }
/*     */ 
/*     */     
/* 105 */     if (value instanceof byte[]) {
/* 106 */       return ByteUtil.bytesToNumber((byte[])value, targetType, ByteUtil.DEFAULT_ORDER);
/*     */     }
/*     */     
/* 109 */     if (Byte.class == targetType) {
/* 110 */       if (value instanceof Number)
/* 111 */         return Byte.valueOf(((Number)value).byteValue()); 
/* 112 */       if (value instanceof Boolean) {
/* 113 */         return BooleanUtil.toByteObj(((Boolean)value).booleanValue());
/*     */       }
/* 115 */       String valueStr = toStrFunc.apply(value);
/*     */       try {
/* 117 */         return StrUtil.isBlank(valueStr) ? null : Byte.valueOf(valueStr);
/* 118 */       } catch (NumberFormatException e) {
/* 119 */         return Byte.valueOf(NumberUtil.parseNumber(valueStr).byteValue());
/*     */       } 
/* 121 */     }  if (Short.class == targetType) {
/* 122 */       if (value instanceof Number)
/* 123 */         return Short.valueOf(((Number)value).shortValue()); 
/* 124 */       if (value instanceof Boolean) {
/* 125 */         return BooleanUtil.toShortObj(((Boolean)value).booleanValue());
/*     */       }
/* 127 */       String valueStr = toStrFunc.apply(value);
/*     */       try {
/* 129 */         return StrUtil.isBlank(valueStr) ? null : Short.valueOf(valueStr);
/* 130 */       } catch (NumberFormatException e) {
/* 131 */         return Short.valueOf(NumberUtil.parseNumber(valueStr).shortValue());
/*     */       } 
/* 133 */     }  if (Integer.class == targetType) {
/* 134 */       if (value instanceof Number)
/* 135 */         return Integer.valueOf(((Number)value).intValue()); 
/* 136 */       if (value instanceof Boolean)
/* 137 */         return BooleanUtil.toInteger(((Boolean)value).booleanValue()); 
/* 138 */       if (value instanceof Date)
/* 139 */         return Integer.valueOf((int)((Date)value).getTime()); 
/* 140 */       if (value instanceof Calendar)
/* 141 */         return Integer.valueOf((int)((Calendar)value).getTimeInMillis()); 
/* 142 */       if (value instanceof TemporalAccessor) {
/* 143 */         return Integer.valueOf((int)DateUtil.toInstant((TemporalAccessor)value).toEpochMilli());
/*     */       }
/* 145 */       String valueStr = toStrFunc.apply(value);
/* 146 */       return StrUtil.isBlank(valueStr) ? null : Integer.valueOf(NumberUtil.parseInt(valueStr));
/* 147 */     }  if (AtomicInteger.class == targetType) {
/* 148 */       Number number = convert(value, (Class)Integer.class, toStrFunc);
/* 149 */       if (null != number)
/* 150 */         return new AtomicInteger(number.intValue()); 
/*     */     } else {
/* 152 */       if (Long.class == targetType) {
/* 153 */         if (value instanceof Number)
/* 154 */           return Long.valueOf(((Number)value).longValue()); 
/* 155 */         if (value instanceof Boolean)
/* 156 */           return BooleanUtil.toLongObj(((Boolean)value).booleanValue()); 
/* 157 */         if (value instanceof Date)
/* 158 */           return Long.valueOf(((Date)value).getTime()); 
/* 159 */         if (value instanceof Calendar)
/* 160 */           return Long.valueOf(((Calendar)value).getTimeInMillis()); 
/* 161 */         if (value instanceof TemporalAccessor) {
/* 162 */           return Long.valueOf(DateUtil.toInstant((TemporalAccessor)value).toEpochMilli());
/*     */         }
/* 164 */         String valueStr = toStrFunc.apply(value);
/* 165 */         return StrUtil.isBlank(valueStr) ? null : Long.valueOf(NumberUtil.parseLong(valueStr));
/* 166 */       }  if (AtomicLong.class == targetType)
/* 167 */       { Number number = convert(value, (Class)Long.class, toStrFunc);
/* 168 */         if (null != number) {
/* 169 */           return new AtomicLong(number.longValue());
/*     */         } }
/* 171 */       else if (LongAdder.class == targetType)
/*     */       
/* 173 */       { Number number = convert(value, (Class)Long.class, toStrFunc);
/* 174 */         if (null != number) {
/* 175 */           LongAdder longValue = new LongAdder();
/* 176 */           longValue.add(number.longValue());
/* 177 */           return longValue;
/*     */         }  }
/* 179 */       else { if (Float.class == targetType) {
/* 180 */           if (value instanceof Number)
/* 181 */             return Float.valueOf(((Number)value).floatValue()); 
/* 182 */           if (value instanceof Boolean) {
/* 183 */             return BooleanUtil.toFloatObj(((Boolean)value).booleanValue());
/*     */           }
/* 185 */           String valueStr = toStrFunc.apply(value);
/* 186 */           return StrUtil.isBlank(valueStr) ? null : Float.valueOf(NumberUtil.parseFloat(valueStr));
/* 187 */         }  if (Double.class == targetType) {
/* 188 */           if (value instanceof Number)
/* 189 */             return Double.valueOf(NumberUtil.toDouble((Number)value)); 
/* 190 */           if (value instanceof Boolean) {
/* 191 */             return BooleanUtil.toDoubleObj(((Boolean)value).booleanValue());
/*     */           }
/* 193 */           String valueStr = toStrFunc.apply(value);
/* 194 */           return StrUtil.isBlank(valueStr) ? null : Double.valueOf(NumberUtil.parseDouble(valueStr));
/* 195 */         }  if (DoubleAdder.class == targetType)
/*     */         
/* 197 */         { Number number = convert(value, (Class)Double.class, toStrFunc);
/* 198 */           if (null != number) {
/* 199 */             DoubleAdder doubleAdder = new DoubleAdder();
/* 200 */             doubleAdder.add(number.doubleValue());
/* 201 */             return doubleAdder;
/*     */           }  }
/* 203 */         else { if (BigDecimal.class == targetType)
/* 204 */             return toBigDecimal(value, toStrFunc); 
/* 205 */           if (BigInteger.class == targetType)
/* 206 */             return toBigInteger(value, toStrFunc); 
/* 207 */           if (Number.class == targetType)
/* 208 */           { if (value instanceof Number)
/* 209 */               return (Number)value; 
/* 210 */             if (value instanceof Boolean) {
/* 211 */               return BooleanUtil.toInteger(((Boolean)value).booleanValue());
/*     */             }
/* 213 */             String valueStr = toStrFunc.apply(value);
/* 214 */             return StrUtil.isBlank(valueStr) ? null : NumberUtil.parseNumber(valueStr); }  }
/*     */          }
/*     */     
/* 217 */     }  throw new UnsupportedOperationException(StrUtil.format("Unsupport Number type: {}", new Object[] { targetType.getName() }));
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
/*     */   private static BigDecimal toBigDecimal(Object value, Function<Object, String> toStrFunc) {
/* 230 */     if (value instanceof Number)
/* 231 */       return NumberUtil.toBigDecimal((Number)value); 
/* 232 */     if (value instanceof Boolean) {
/* 233 */       return ((Boolean)value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
/*     */     }
/*     */ 
/*     */     
/* 237 */     return NumberUtil.toBigDecimal(toStrFunc.apply(value));
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
/*     */   private static BigInteger toBigInteger(Object value, Function<Object, String> toStrFunc) {
/* 250 */     if (value instanceof Long)
/* 251 */       return BigInteger.valueOf(((Long)value).longValue()); 
/* 252 */     if (value instanceof Boolean) {
/* 253 */       return ((Boolean)value).booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
/*     */     }
/*     */     
/* 256 */     return NumberUtil.toBigInteger(toStrFunc.apply(value));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\NumberConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */