/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ValueEncoder
/*     */ {
/*     */   private NativePacketPayload packet;
/*     */   private String characterEncoding;
/*     */   private TimeZone timezone;
/*     */   
/*     */   public ValueEncoder(NativePacketPayload packet, String characterEncoding, TimeZone timezone) {
/*  68 */     this.packet = packet;
/*  69 */     this.characterEncoding = characterEncoding;
/*  70 */     this.timezone = timezone;
/*     */   }
/*     */   
/*     */   public void encodeValue(Object value, int fieldType) {
/*  74 */     if (value == null) {
/*     */       return;
/*     */     }
/*  77 */     switch (fieldType) {
/*     */       case 1:
/*  79 */         encodeInt1(asByte(value));
/*     */         return;
/*     */       case 2:
/*  82 */         encodeInt2(asShort(value));
/*     */         return;
/*     */       case 3:
/*     */       case 4:
/*  86 */         encodeInt4(asInteger(value));
/*     */         return;
/*     */       case 5:
/*     */       case 8:
/*  90 */         encodeInt8(asLong(value));
/*     */         return;
/*     */       case 10:
/*  93 */         encodeDate(asInternalDate(value));
/*     */         return;
/*     */       case 11:
/*  96 */         encodeTime(asInternalTime(value));
/*     */         return;
/*     */       case 12:
/*  99 */         encodeDateTime(asInternalTimestampNoTz(value));
/*     */         return;
/*     */       case 7:
/* 102 */         encodeTimeStamp(asInternalTimestampTz(value));
/*     */         return;
/*     */       case 254:
/* 105 */         encodeString(asString(value));
/*     */         return;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void encodeInt1(Byte value) {
/* 111 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, value.longValue());
/*     */   }
/*     */   
/*     */   public void encodeInt2(Short value) {
/* 115 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, value.longValue());
/*     */   }
/*     */   
/*     */   public void encodeInt4(Integer value) {
/* 119 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, value.longValue());
/*     */   }
/*     */   
/*     */   public void encodeInt8(Long value) {
/* 123 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT8, value.longValue());
/*     */   }
/*     */   
/*     */   public void encodeDate(InternalDate date) {
/* 127 */     this.packet.ensureCapacity(5);
/* 128 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, 4L);
/* 129 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, date.getYear());
/* 130 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, date.getMonth());
/* 131 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, date.getDay());
/*     */   }
/*     */   
/*     */   public void encodeTime(InternalTime time) {
/* 135 */     boolean hasFractionalSeconds = (time.getNanos() > 0);
/* 136 */     this.packet.ensureCapacity((hasFractionalSeconds ? 12 : 8) + 1);
/* 137 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, hasFractionalSeconds ? 12L : 8L);
/* 138 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, time.isNegative() ? 1L : 0L);
/* 139 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, (time.getHours() / 24));
/* 140 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, (time.getHours() % 24));
/* 141 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, time.getMinutes());
/* 142 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, time.getSeconds());
/* 143 */     if (hasFractionalSeconds) {
/* 144 */       this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros(time.getNanos()));
/*     */     }
/*     */   }
/*     */   
/*     */   public void encodeDateTime(InternalTimestamp timestamp) {
/* 149 */     boolean hasFractionalSeconds = (timestamp.getNanos() > 0);
/* 150 */     this.packet.ensureCapacity((hasFractionalSeconds ? 11 : 7) + 1);
/* 151 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, hasFractionalSeconds ? 11L : 7L);
/*     */     
/* 153 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, timestamp.getYear());
/* 154 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getMonth());
/* 155 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getDay());
/* 156 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getHours());
/* 157 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getMinutes());
/* 158 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getSeconds());
/* 159 */     if (hasFractionalSeconds) {
/* 160 */       this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros(timestamp.getNanos()));
/*     */     }
/*     */   }
/*     */   
/*     */   public void encodeTimeStamp(InternalTimestamp timestamp) {
/* 165 */     this.packet.ensureCapacity(14);
/* 166 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, 13L);
/* 167 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, timestamp.getYear());
/* 168 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getMonth());
/* 169 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getDay());
/* 170 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getHours());
/* 171 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getMinutes());
/* 172 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT1, timestamp.getSeconds());
/* 173 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT4, TimeUnit.NANOSECONDS.toMicros(timestamp.getNanos()));
/* 174 */     this.packet.writeInteger(NativeConstants.IntegerDataType.INT2, timestamp.getOffset());
/*     */   }
/*     */   
/*     */   public void encodeString(String value) {
/* 178 */     this.packet.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(value, this.characterEncoding));
/*     */   }
/*     */   
/*     */   private Byte asByte(Object value) {
/* 182 */     if (Boolean.class.isInstance(value)) {
/* 183 */       return ((Boolean)value).booleanValue() ? new Byte((byte)1) : new Byte((byte)0);
/*     */     }
/*     */     
/* 186 */     if (Byte.class.isInstance(value)) {
/* 187 */       return (Byte)value;
/*     */     }
/*     */     
/* 190 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 191 */         Messages.getString("ValueEncoder.WrongTinyIntValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private Short asShort(Object value) {
/* 195 */     if (Short.class.isInstance(value)) {
/* 196 */       return (Short)value;
/*     */     }
/*     */     
/* 199 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 200 */         Messages.getString("ValueEncoder.WrongSmallIntValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private Integer asInteger(Object value) {
/* 204 */     if (Integer.class.isInstance(value)) {
/* 205 */       return (Integer)value;
/*     */     }
/*     */     
/* 208 */     if (Float.class.isInstance(value)) {
/* 209 */       return Integer.valueOf(Float.floatToIntBits(((Float)value).floatValue()));
/*     */     }
/*     */     
/* 212 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 213 */         Messages.getString("ValueEncoder.WrongIntValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private Long asLong(Object value) {
/* 217 */     if (Long.class.isInstance(value)) {
/* 218 */       return (Long)value;
/*     */     }
/*     */     
/* 221 */     if (Double.class.isInstance(value)) {
/* 222 */       return Long.valueOf(Double.doubleToLongBits(((Double)value).doubleValue()));
/*     */     }
/*     */     
/* 225 */     if (BigInteger.class.isInstance(value)) {
/* 226 */       return Long.valueOf(((BigInteger)value).longValue());
/*     */     }
/*     */     
/* 229 */     if (BigDecimal.class.isInstance(value)) {
/* 230 */       return Long.valueOf(Double.doubleToLongBits(((BigDecimal)value).doubleValue()));
/*     */     }
/*     */     
/* 233 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 234 */         Messages.getString("ValueEncoder.WrongBigIntValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private InternalDate asInternalDate(Object value) {
/* 238 */     if (LocalDate.class.isInstance(value)) {
/* 239 */       LocalDate localDate = (LocalDate)value;
/*     */       
/* 241 */       InternalDate internalDate = new InternalDate();
/* 242 */       internalDate.setYear(localDate.getYear());
/* 243 */       internalDate.setMonth(localDate.getMonthValue());
/* 244 */       internalDate.setDay(localDate.getDayOfMonth());
/* 245 */       return internalDate;
/*     */     } 
/*     */     
/* 248 */     if (Date.class.isInstance(value)) {
/* 249 */       Calendar calendar = Calendar.getInstance(this.timezone);
/* 250 */       calendar.setTime((Date)value);
/* 251 */       calendar.set(11, 0);
/* 252 */       calendar.set(12, 0);
/* 253 */       calendar.set(13, 0);
/*     */       
/* 255 */       InternalDate internalDate = new InternalDate();
/* 256 */       internalDate.setYear(calendar.get(1));
/* 257 */       internalDate.setMonth(calendar.get(2) + 1);
/* 258 */       internalDate.setDay(calendar.get(5));
/* 259 */       return internalDate;
/*     */     } 
/*     */     
/* 262 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 263 */         Messages.getString("ValueEncoder.WrongDateValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private InternalTime asInternalTime(Object value) {
/* 267 */     if (LocalTime.class.isInstance(value)) {
/* 268 */       LocalTime localTime = (LocalTime)value;
/*     */       
/* 270 */       InternalTime internalTime = new InternalTime();
/* 271 */       internalTime.setHours(localTime.getHour());
/* 272 */       internalTime.setMinutes(localTime.getMinute());
/* 273 */       internalTime.setSeconds(localTime.getSecond());
/* 274 */       internalTime.setNanos(localTime.getNano());
/* 275 */       return internalTime;
/*     */     } 
/*     */     
/* 278 */     if (OffsetTime.class.isInstance(value)) {
/* 279 */       OffsetTime offsetTime = (OffsetTime)value;
/*     */       
/* 281 */       InternalTime internalTime = new InternalTime();
/* 282 */       internalTime.setHours(offsetTime.getHour());
/* 283 */       internalTime.setMinutes(offsetTime.getMinute());
/* 284 */       internalTime.setSeconds(offsetTime.getSecond());
/* 285 */       internalTime.setNanos(offsetTime.getNano());
/*     */       
/* 287 */       return internalTime;
/*     */     } 
/*     */     
/* 290 */     if (Duration.class.isInstance(value)) {
/* 291 */       Duration duration = (Duration)value;
/* 292 */       Duration durationAbs = duration.abs();
/*     */       
/* 294 */       long fullSeconds = durationAbs.getSeconds();
/* 295 */       int seconds = (int)(fullSeconds % 60L);
/* 296 */       long fullMinutes = fullSeconds / 60L;
/* 297 */       int minutes = (int)(fullMinutes % 60L);
/* 298 */       long fullHours = fullMinutes / 60L;
/*     */       
/* 300 */       InternalTime internalTime = new InternalTime();
/* 301 */       internalTime.setNegative(duration.isNegative());
/* 302 */       internalTime.setHours((int)fullHours);
/* 303 */       internalTime.setMinutes(minutes);
/* 304 */       internalTime.setSeconds(seconds);
/* 305 */       internalTime.setNanos(durationAbs.getNano());
/* 306 */       return internalTime;
/*     */     } 
/*     */     
/* 309 */     if (Time.class.isInstance(value)) {
/* 310 */       Time time = (Time)value;
/*     */       
/* 312 */       Calendar calendar = Calendar.getInstance(this.timezone);
/* 313 */       calendar.setTime(time);
/*     */       
/* 315 */       InternalTime internalTime = new InternalTime();
/* 316 */       internalTime.setHours(calendar.get(11));
/* 317 */       internalTime.setMinutes(calendar.get(12));
/* 318 */       internalTime.setSeconds(calendar.get(13));
/* 319 */       internalTime.setNanos((int)TimeUnit.MILLISECONDS.toNanos(calendar.get(14)));
/* 320 */       return internalTime;
/*     */     } 
/*     */     
/* 323 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 324 */         Messages.getString("ValueEncoder.WrongTimeValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private InternalTimestamp asInternalTimestampNoTz(Object value) {
/* 328 */     if (LocalDateTime.class.isInstance(value)) {
/* 329 */       LocalDateTime localDateTime = (LocalDateTime)value;
/*     */       
/* 331 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 332 */       internalTimestamp.setYear(localDateTime.getYear());
/* 333 */       internalTimestamp.setMonth(localDateTime.getMonthValue());
/* 334 */       internalTimestamp.setDay(localDateTime.getDayOfMonth());
/* 335 */       internalTimestamp.setHours(localDateTime.getHour());
/* 336 */       internalTimestamp.setMinutes(localDateTime.getMinute());
/* 337 */       internalTimestamp.setSeconds(localDateTime.getSecond());
/* 338 */       internalTimestamp.setNanos(localDateTime.getNano());
/* 339 */       return internalTimestamp;
/*     */     } 
/*     */     
/* 342 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 343 */         Messages.getString("ValueEncoder.WrongDatetimeValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private InternalTimestamp asInternalTimestampTz(Object value) {
/* 347 */     if (Instant.class.isInstance(value)) {
/* 348 */       Instant instant = (Instant)value;
/* 349 */       OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
/*     */       
/* 351 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 352 */       internalTimestamp.setYear(offsetDateTime.getYear());
/* 353 */       internalTimestamp.setMonth(offsetDateTime.getMonthValue());
/* 354 */       internalTimestamp.setDay(offsetDateTime.getDayOfMonth());
/* 355 */       internalTimestamp.setHours(offsetDateTime.getHour());
/* 356 */       internalTimestamp.setMinutes(offsetDateTime.getMinute());
/* 357 */       internalTimestamp.setSeconds(offsetDateTime.getSecond());
/* 358 */       internalTimestamp.setNanos(offsetDateTime.getNano());
/* 359 */       internalTimestamp.setOffset(0);
/* 360 */       return internalTimestamp;
/*     */     } 
/*     */     
/* 363 */     if (OffsetDateTime.class.isInstance(value)) {
/* 364 */       OffsetDateTime offsetDateTime = (OffsetDateTime)value;
/*     */       
/* 366 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 367 */       internalTimestamp.setYear(offsetDateTime.getYear());
/* 368 */       internalTimestamp.setMonth(offsetDateTime.getMonthValue());
/* 369 */       internalTimestamp.setDay(offsetDateTime.getDayOfMonth());
/* 370 */       internalTimestamp.setHours(offsetDateTime.getHour());
/* 371 */       internalTimestamp.setMinutes(offsetDateTime.getMinute());
/* 372 */       internalTimestamp.setSeconds(offsetDateTime.getSecond());
/* 373 */       internalTimestamp.setNanos(offsetDateTime.getNano());
/* 374 */       internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes(offsetDateTime.getOffset().getTotalSeconds()));
/* 375 */       return internalTimestamp;
/*     */     } 
/*     */     
/* 378 */     if (ZonedDateTime.class.isInstance(value)) {
/* 379 */       ZonedDateTime zonedDateTime = (ZonedDateTime)value;
/*     */       
/* 381 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 382 */       internalTimestamp.setYear(zonedDateTime.getYear());
/* 383 */       internalTimestamp.setMonth(zonedDateTime.getMonthValue());
/* 384 */       internalTimestamp.setDay(zonedDateTime.getDayOfMonth());
/* 385 */       internalTimestamp.setHours(zonedDateTime.getHour());
/* 386 */       internalTimestamp.setMinutes(zonedDateTime.getMinute());
/* 387 */       internalTimestamp.setSeconds(zonedDateTime.getSecond());
/* 388 */       internalTimestamp.setNanos(zonedDateTime.getNano());
/* 389 */       internalTimestamp.setOffset((int)TimeUnit.SECONDS.toMinutes(zonedDateTime.getOffset().getTotalSeconds()));
/* 390 */       return internalTimestamp;
/*     */     } 
/*     */ 
/*     */     
/* 394 */     if (Calendar.class.isInstance(value)) {
/* 395 */       Calendar calendar = (Calendar)value;
/*     */       
/* 397 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 398 */       internalTimestamp.setYear(calendar.get(1));
/* 399 */       internalTimestamp.setMonth(calendar.get(2) + 1);
/* 400 */       internalTimestamp.setDay(calendar.get(5));
/* 401 */       internalTimestamp.setHours(calendar.get(11));
/* 402 */       internalTimestamp.setMinutes(calendar.get(12));
/* 403 */       internalTimestamp.setSeconds(calendar.get(13));
/* 404 */       internalTimestamp.setNanos((int)TimeUnit.MILLISECONDS.toNanos(calendar.get(14)));
/* 405 */       internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes(calendar.getTimeZone().getOffset(calendar.getTimeInMillis())));
/* 406 */       return internalTimestamp;
/*     */     } 
/*     */ 
/*     */     
/* 410 */     if (Timestamp.class.isInstance(value)) {
/* 411 */       Timestamp timestamp = (Timestamp)value;
/*     */       
/* 413 */       Calendar calendar = Calendar.getInstance(this.timezone);
/* 414 */       calendar.setTime(timestamp);
/*     */       
/* 416 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 417 */       internalTimestamp.setYear(calendar.get(1));
/* 418 */       internalTimestamp.setMonth(calendar.get(2) + 1);
/* 419 */       internalTimestamp.setDay(calendar.get(5));
/* 420 */       internalTimestamp.setHours(calendar.get(11));
/* 421 */       internalTimestamp.setMinutes(calendar.get(12));
/* 422 */       internalTimestamp.setSeconds(calendar.get(13));
/* 423 */       internalTimestamp.setNanos(timestamp.getNanos());
/* 424 */       internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes(calendar.getTimeZone().getOffset(calendar.getTimeInMillis())));
/* 425 */       return internalTimestamp;
/*     */     } 
/*     */     
/* 428 */     if (Date.class.isInstance(value)) {
/* 429 */       Date date = (Date)value;
/*     */       
/* 431 */       Calendar calendar = Calendar.getInstance(this.timezone);
/* 432 */       calendar.setTime(date);
/*     */       
/* 434 */       InternalTimestamp internalTimestamp = new InternalTimestamp();
/* 435 */       internalTimestamp.setYear(calendar.get(1));
/* 436 */       internalTimestamp.setMonth(calendar.get(2) + 1);
/* 437 */       internalTimestamp.setDay(calendar.get(5));
/* 438 */       internalTimestamp.setHours(calendar.get(11));
/* 439 */       internalTimestamp.setMinutes(calendar.get(12));
/* 440 */       internalTimestamp.setSeconds(calendar.get(13));
/* 441 */       internalTimestamp.setNanos((int)TimeUnit.MILLISECONDS.toNanos(calendar.get(14)));
/* 442 */       internalTimestamp.setOffset((int)TimeUnit.MILLISECONDS.toMinutes(calendar.getTimeZone().getOffset(date.getTime())));
/* 443 */       return internalTimestamp;
/*     */     } 
/*     */     
/* 446 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 447 */         Messages.getString("ValueEncoder.WrongTimestampValueType", new Object[] { value.getClass() }));
/*     */   }
/*     */   
/*     */   private String asString(Object value) {
/* 451 */     if (String.class.isInstance(value)) {
/* 452 */       return (String)value;
/*     */     }
/*     */     
/* 455 */     return value.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\ValueEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */