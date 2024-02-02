/*     */ package cn.hutool.core.convert;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.convert.impl.ArrayConverter;
/*     */ import cn.hutool.core.convert.impl.AtomicBooleanConverter;
/*     */ import cn.hutool.core.convert.impl.AtomicIntegerArrayConverter;
/*     */ import cn.hutool.core.convert.impl.AtomicLongArrayConverter;
/*     */ import cn.hutool.core.convert.impl.AtomicReferenceConverter;
/*     */ import cn.hutool.core.convert.impl.BeanConverter;
/*     */ import cn.hutool.core.convert.impl.BooleanConverter;
/*     */ import cn.hutool.core.convert.impl.CalendarConverter;
/*     */ import cn.hutool.core.convert.impl.CharacterConverter;
/*     */ import cn.hutool.core.convert.impl.CharsetConverter;
/*     */ import cn.hutool.core.convert.impl.ClassConverter;
/*     */ import cn.hutool.core.convert.impl.CollectionConverter;
/*     */ import cn.hutool.core.convert.impl.CurrencyConverter;
/*     */ import cn.hutool.core.convert.impl.DateConverter;
/*     */ import cn.hutool.core.convert.impl.DurationConverter;
/*     */ import cn.hutool.core.convert.impl.EnumConverter;
/*     */ import cn.hutool.core.convert.impl.LocaleConverter;
/*     */ import cn.hutool.core.convert.impl.MapConverter;
/*     */ import cn.hutool.core.convert.impl.NumberConverter;
/*     */ import cn.hutool.core.convert.impl.OptConverter;
/*     */ import cn.hutool.core.convert.impl.OptionalConverter;
/*     */ import cn.hutool.core.convert.impl.PathConverter;
/*     */ import cn.hutool.core.convert.impl.PeriodConverter;
/*     */ import cn.hutool.core.convert.impl.PrimitiveConverter;
/*     */ import cn.hutool.core.convert.impl.ReferenceConverter;
/*     */ import cn.hutool.core.convert.impl.StackTraceElementConverter;
/*     */ import cn.hutool.core.convert.impl.StringConverter;
/*     */ import cn.hutool.core.convert.impl.TemporalAccessorConverter;
/*     */ import cn.hutool.core.convert.impl.TimeZoneConverter;
/*     */ import cn.hutool.core.convert.impl.URIConverter;
/*     */ import cn.hutool.core.convert.impl.URLConverter;
/*     */ import cn.hutool.core.convert.impl.UUIDConverter;
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.lang.Opt;
/*     */ import cn.hutool.core.lang.TypeReference;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.ServiceLoaderUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Type;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Path;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.Period;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicIntegerArray;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.AtomicLongArray;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ 
/*     */ public class ConverterRegistry
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Map<Type, Converter<?>> defaultConverterMap;
/*     */   private volatile Map<Type, Converter<?>> customConverterMap;
/*     */   
/*     */   private static class SingletonHolder
/*     */   {
/* 113 */     private static final ConverterRegistry INSTANCE = new ConverterRegistry();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConverterRegistry getInstance() {
/* 122 */     return SingletonHolder.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConverterRegistry() {
/* 129 */     defaultConverter();
/* 130 */     putCustomBySpi();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void putCustomBySpi() {
/* 137 */     ServiceLoaderUtil.load(Converter.class).forEach(converter -> {
/*     */           try {
/*     */             Type type = TypeUtil.getTypeArgument(ClassUtil.getClass(converter));
/*     */             if (null != type) {
/*     */               putCustom(type, converter);
/*     */             }
/* 143 */           } catch (Exception exception) {}
/*     */         });
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
/*     */   public ConverterRegistry putCustom(Type type, Class<? extends Converter<?>> converterClass) {
/* 157 */     return putCustom(type, (Converter)ReflectUtil.newInstance(converterClass, new Object[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConverterRegistry putCustom(Type type, Converter<?> converter) {
/* 168 */     if (null == this.customConverterMap) {
/* 169 */       synchronized (this) {
/* 170 */         if (null == this.customConverterMap) {
/* 171 */           this.customConverterMap = new ConcurrentHashMap<>();
/*     */         }
/*     */       } 
/*     */     }
/* 175 */     this.customConverterMap.put(type, converter);
/* 176 */     return this;
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
/*     */   public <T> Converter<T> getConverter(Type type, boolean isCustomFirst) {
/*     */     Converter<T> converter;
/* 189 */     if (isCustomFirst) {
/* 190 */       converter = getCustomConverter(type);
/* 191 */       if (null == converter) {
/* 192 */         converter = getDefaultConverter(type);
/*     */       }
/*     */     } else {
/* 195 */       converter = getDefaultConverter(type);
/* 196 */       if (null == converter) {
/* 197 */         converter = getCustomConverter(type);
/*     */       }
/*     */     } 
/* 200 */     return converter;
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
/*     */   public <T> Converter<T> getDefaultConverter(Type type) {
/* 212 */     return (null == this.defaultConverterMap) ? null : (Converter<T>)this.defaultConverterMap.get(type);
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
/*     */   public <T> Converter<T> getCustomConverter(Type type) {
/* 224 */     return (null == this.customConverterMap) ? null : (Converter<T>)this.customConverterMap.get(type);
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
/*     */   public <T> T convert(Type<?> type, Object value, T defaultValue, boolean isCustomFirst) throws ConvertException {
/* 240 */     if (TypeUtil.isUnknown(type) && null == defaultValue)
/*     */     {
/* 242 */       return (T)value;
/*     */     }
/* 244 */     if (ObjectUtil.isNull(value)) {
/* 245 */       return defaultValue;
/*     */     }
/* 247 */     if (TypeUtil.isUnknown(type)) {
/* 248 */       type = defaultValue.getClass();
/*     */     }
/*     */     
/* 251 */     if (type instanceof TypeReference) {
/* 252 */       type = ((TypeReference)type).getType();
/*     */     }
/*     */ 
/*     */     
/* 256 */     Converter<T> converter = getConverter(type, isCustomFirst);
/* 257 */     if (null != converter) {
/* 258 */       return converter.convert(value, defaultValue);
/*     */     }
/*     */     
/* 261 */     Class<T> rowType = TypeUtil.getClass(type);
/* 262 */     if (null == rowType) {
/* 263 */       if (null != defaultValue) {
/* 264 */         rowType = (Class)defaultValue.getClass();
/*     */       } else {
/*     */         
/* 267 */         return (T)value;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 272 */     T result = convertSpecial(type, rowType, value, defaultValue);
/* 273 */     if (null != result) {
/* 274 */       return result;
/*     */     }
/*     */ 
/*     */     
/* 278 */     if (BeanUtil.isBean(rowType)) {
/* 279 */       return (T)(new BeanConverter(type)).convert(value, defaultValue);
/*     */     }
/*     */ 
/*     */     
/* 283 */     throw new ConvertException("Can not Converter from [{}] to [{}]", new Object[] { value.getClass().getName(), type.getTypeName() });
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
/*     */   public <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
/* 298 */     return convert(type, value, defaultValue, true);
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
/*     */   public <T> T convert(Type type, Object value) throws ConvertException {
/* 311 */     return convert(type, value, null);
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
/*     */   private <T> T convertSpecial(Type type, Class<T> rowType, Object value, T defaultValue) {
/* 335 */     if (null == rowType) {
/* 336 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 340 */     if (Collection.class.isAssignableFrom(rowType)) {
/* 341 */       CollectionConverter collectionConverter = new CollectionConverter(type);
/* 342 */       return (T)collectionConverter.convert(value, (Collection)defaultValue);
/*     */     } 
/*     */ 
/*     */     
/* 346 */     if (Map.class.isAssignableFrom(rowType)) {
/* 347 */       MapConverter mapConverter = new MapConverter(type);
/* 348 */       return (T)mapConverter.convert(value, (Map)defaultValue);
/*     */     } 
/*     */ 
/*     */     
/* 352 */     if (rowType.isInstance(value)) {
/* 353 */       return (T)value;
/*     */     }
/*     */ 
/*     */     
/* 357 */     if (rowType.isEnum()) {
/* 358 */       return (T)(new EnumConverter(rowType)).convert(value, defaultValue);
/*     */     }
/*     */ 
/*     */     
/* 362 */     if (rowType.isArray()) {
/* 363 */       ArrayConverter arrayConverter = new ArrayConverter(rowType);
/* 364 */       return (T)arrayConverter.convert(value, defaultValue);
/*     */     } 
/*     */ 
/*     */     
/* 368 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ConverterRegistry defaultConverter() {
/* 377 */     this.defaultConverterMap = new ConcurrentHashMap<>();
/*     */ 
/*     */     
/* 380 */     this.defaultConverterMap.put(int.class, new PrimitiveConverter(int.class));
/* 381 */     this.defaultConverterMap.put(long.class, new PrimitiveConverter(long.class));
/* 382 */     this.defaultConverterMap.put(byte.class, new PrimitiveConverter(byte.class));
/* 383 */     this.defaultConverterMap.put(short.class, new PrimitiveConverter(short.class));
/* 384 */     this.defaultConverterMap.put(float.class, new PrimitiveConverter(float.class));
/* 385 */     this.defaultConverterMap.put(double.class, new PrimitiveConverter(double.class));
/* 386 */     this.defaultConverterMap.put(char.class, new PrimitiveConverter(char.class));
/* 387 */     this.defaultConverterMap.put(boolean.class, new PrimitiveConverter(boolean.class));
/*     */ 
/*     */     
/* 390 */     this.defaultConverterMap.put(Number.class, new NumberConverter());
/* 391 */     this.defaultConverterMap.put(Integer.class, new NumberConverter(Integer.class));
/* 392 */     this.defaultConverterMap.put(AtomicInteger.class, new NumberConverter(AtomicInteger.class));
/* 393 */     this.defaultConverterMap.put(Long.class, new NumberConverter(Long.class));
/* 394 */     this.defaultConverterMap.put(LongAdder.class, new NumberConverter(LongAdder.class));
/* 395 */     this.defaultConverterMap.put(AtomicLong.class, new NumberConverter(AtomicLong.class));
/* 396 */     this.defaultConverterMap.put(Byte.class, new NumberConverter(Byte.class));
/* 397 */     this.defaultConverterMap.put(Short.class, new NumberConverter(Short.class));
/* 398 */     this.defaultConverterMap.put(Float.class, new NumberConverter(Float.class));
/* 399 */     this.defaultConverterMap.put(Double.class, new NumberConverter(Double.class));
/* 400 */     this.defaultConverterMap.put(DoubleAdder.class, new NumberConverter(DoubleAdder.class));
/* 401 */     this.defaultConverterMap.put(Character.class, new CharacterConverter());
/* 402 */     this.defaultConverterMap.put(Boolean.class, new BooleanConverter());
/* 403 */     this.defaultConverterMap.put(AtomicBoolean.class, new AtomicBooleanConverter());
/* 404 */     this.defaultConverterMap.put(BigDecimal.class, new NumberConverter(BigDecimal.class));
/* 405 */     this.defaultConverterMap.put(BigInteger.class, new NumberConverter(BigInteger.class));
/* 406 */     this.defaultConverterMap.put(CharSequence.class, new StringConverter());
/* 407 */     this.defaultConverterMap.put(String.class, new StringConverter());
/*     */ 
/*     */     
/* 410 */     this.defaultConverterMap.put(URI.class, new URIConverter());
/* 411 */     this.defaultConverterMap.put(URL.class, new URLConverter());
/*     */ 
/*     */     
/* 414 */     this.defaultConverterMap.put(Calendar.class, new CalendarConverter());
/* 415 */     this.defaultConverterMap.put(Date.class, new DateConverter(Date.class));
/* 416 */     this.defaultConverterMap.put(DateTime.class, new DateConverter(DateTime.class));
/* 417 */     this.defaultConverterMap.put(Date.class, new DateConverter(Date.class));
/* 418 */     this.defaultConverterMap.put(Time.class, new DateConverter(Time.class));
/* 419 */     this.defaultConverterMap.put(Timestamp.class, new DateConverter(Timestamp.class));
/*     */ 
/*     */     
/* 422 */     this.defaultConverterMap.put(TemporalAccessor.class, new TemporalAccessorConverter(Instant.class));
/* 423 */     this.defaultConverterMap.put(Instant.class, new TemporalAccessorConverter(Instant.class));
/* 424 */     this.defaultConverterMap.put(LocalDateTime.class, new TemporalAccessorConverter(LocalDateTime.class));
/* 425 */     this.defaultConverterMap.put(LocalDate.class, new TemporalAccessorConverter(LocalDate.class));
/* 426 */     this.defaultConverterMap.put(LocalTime.class, new TemporalAccessorConverter(LocalTime.class));
/* 427 */     this.defaultConverterMap.put(ZonedDateTime.class, new TemporalAccessorConverter(ZonedDateTime.class));
/* 428 */     this.defaultConverterMap.put(OffsetDateTime.class, new TemporalAccessorConverter(OffsetDateTime.class));
/* 429 */     this.defaultConverterMap.put(OffsetTime.class, new TemporalAccessorConverter(OffsetTime.class));
/* 430 */     this.defaultConverterMap.put(Period.class, new PeriodConverter());
/* 431 */     this.defaultConverterMap.put(Duration.class, new DurationConverter());
/*     */ 
/*     */     
/* 434 */     this.defaultConverterMap.put(WeakReference.class, new ReferenceConverter(WeakReference.class));
/* 435 */     this.defaultConverterMap.put(SoftReference.class, new ReferenceConverter(SoftReference.class));
/* 436 */     this.defaultConverterMap.put(AtomicReference.class, new AtomicReferenceConverter());
/*     */ 
/*     */     
/* 439 */     this.defaultConverterMap.put(AtomicIntegerArray.class, new AtomicIntegerArrayConverter());
/* 440 */     this.defaultConverterMap.put(AtomicLongArray.class, new AtomicLongArrayConverter());
/*     */ 
/*     */     
/* 443 */     this.defaultConverterMap.put(Class.class, new ClassConverter());
/* 444 */     this.defaultConverterMap.put(TimeZone.class, new TimeZoneConverter());
/* 445 */     this.defaultConverterMap.put(Locale.class, new LocaleConverter());
/* 446 */     this.defaultConverterMap.put(Charset.class, new CharsetConverter());
/* 447 */     this.defaultConverterMap.put(Path.class, new PathConverter());
/* 448 */     this.defaultConverterMap.put(Currency.class, new CurrencyConverter());
/* 449 */     this.defaultConverterMap.put(UUID.class, new UUIDConverter());
/* 450 */     this.defaultConverterMap.put(StackTraceElement.class, new StackTraceElementConverter());
/* 451 */     this.defaultConverterMap.put(Optional.class, new OptionalConverter());
/* 452 */     this.defaultConverterMap.put(Opt.class, new OptConverter());
/*     */     
/* 454 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\ConverterRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */