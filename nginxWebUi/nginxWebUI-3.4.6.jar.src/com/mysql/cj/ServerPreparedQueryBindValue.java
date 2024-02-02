/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.Duration;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
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
/*     */ public class ServerPreparedQueryBindValue
/*     */   extends ClientPreparedQueryBindValue
/*     */   implements BindValue
/*     */ {
/*  59 */   public long boundBeforeExecutionNum = 0L;
/*     */   
/*     */   public int bufferType;
/*     */   
/*     */   public Calendar calendar;
/*     */   
/*     */   PropertySet pset;
/*     */   
/*     */   private TimeZone defaultTimeZone;
/*     */   private TimeZone connectionTimeZone;
/*  69 */   private RuntimeProperty<Boolean> cacheDefaultTimeZone = null;
/*     */   
/*  71 */   protected String charEncoding = null;
/*     */   
/*     */   public ServerPreparedQueryBindValue(TimeZone defaultTimeZone, TimeZone connectionTimeZone, PropertySet pset) {
/*  74 */     this.pset = pset;
/*  75 */     this.defaultTimeZone = defaultTimeZone;
/*  76 */     this.connectionTimeZone = connectionTimeZone;
/*  77 */     this.cacheDefaultTimeZone = pset.getBooleanProperty(PropertyKey.cacheDefaultTimeZone);
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerPreparedQueryBindValue clone() {
/*  82 */     return new ServerPreparedQueryBindValue(this);
/*     */   }
/*     */   
/*     */   private ServerPreparedQueryBindValue(ServerPreparedQueryBindValue copyMe) {
/*  86 */     super(copyMe);
/*     */     
/*  88 */     this.pset = copyMe.pset;
/*  89 */     this.defaultTimeZone = copyMe.defaultTimeZone;
/*  90 */     this.connectionTimeZone = copyMe.connectionTimeZone;
/*  91 */     this.cacheDefaultTimeZone = copyMe.cacheDefaultTimeZone;
/*  92 */     this.bufferType = copyMe.bufferType;
/*  93 */     this.calendar = copyMe.calendar;
/*  94 */     this.charEncoding = copyMe.charEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  99 */     super.reset();
/* 100 */     this.calendar = null;
/* 101 */     this.charEncoding = null;
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
/*     */   public boolean resetToType(int bufType, long numberOfExecutions) {
/* 114 */     boolean sendTypesToServer = false;
/*     */ 
/*     */     
/* 117 */     reset();
/*     */     
/* 119 */     if (bufType != 6 || this.bufferType == 0)
/*     */     {
/* 121 */       if (this.bufferType != bufType) {
/* 122 */         sendTypesToServer = true;
/* 123 */         this.bufferType = bufType;
/*     */       } 
/*     */     }
/*     */     
/* 127 */     this.isSet = true;
/* 128 */     this.boundBeforeExecutionNum = numberOfExecutions;
/* 129 */     return sendTypesToServer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 134 */     return toString(false);
/*     */   }
/*     */   public String toString(boolean quoteIfNeeded) {
/*     */     String s;
/* 138 */     if (this.isStream) {
/* 139 */       return "' STREAM DATA '";
/*     */     }
/*     */     
/* 142 */     if (this.isNull) {
/* 143 */       return "NULL";
/*     */     }
/*     */ 
/*     */     
/* 147 */     DateTimeFormatter timeFmtWithOptMicros = (new DateTimeFormatterBuilder()).appendPattern("HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true).toFormatter();
/*     */     
/* 149 */     DateTimeFormatter datetimeFmtWithOptMicros = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true).toFormatter();
/*     */     
/* 151 */     switch (this.bufferType) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 8:
/* 156 */         return String.valueOf(((Long)this.value).longValue());
/*     */       case 4:
/* 158 */         return String.valueOf(((Float)this.value).floatValue());
/*     */       case 5:
/* 160 */         return String.valueOf(((Double)this.value).doubleValue());
/*     */       
/*     */       case 11:
/* 163 */         if (this.value instanceof LocalDateTime) {
/* 164 */           s = ((LocalDateTime)this.value).format(timeFmtWithOptMicros);
/* 165 */         } else if (this.value instanceof LocalTime) {
/* 166 */           s = ((LocalTime)this.value).format(timeFmtWithOptMicros);
/* 167 */         } else if (this.value instanceof Duration) {
/* 168 */           s = TimeUtil.getDurationString((Duration)this.value);
/*     */         } else {
/* 170 */           s = String.valueOf(this.value);
/*     */         } 
/* 172 */         return "'" + s + "'";
/*     */       case 10:
/* 174 */         if (this.value instanceof LocalDate) {
/* 175 */           s = ((LocalDate)this.value).format(TimeUtil.DATE_FORMATTER);
/* 176 */         } else if (this.value instanceof LocalTime) {
/* 177 */           s = ((LocalTime)this.value).atDate(LocalDate.of(1970, 1, 1)).format(TimeUtil.DATE_FORMATTER);
/* 178 */         } else if (this.value instanceof LocalDateTime) {
/* 179 */           s = ((LocalDateTime)this.value).format(TimeUtil.DATE_FORMATTER);
/*     */         } else {
/* 181 */           s = String.valueOf(this.value);
/*     */         } 
/* 183 */         return "'" + s + "'";
/*     */       case 7:
/*     */       case 12:
/* 186 */         if (this.value instanceof LocalDate) {
/* 187 */           s = ((LocalDate)this.value).format(datetimeFmtWithOptMicros);
/* 188 */         } else if (this.value instanceof LocalTime) {
/* 189 */           s = ((LocalTime)this.value).atDate(LocalDate.of(1970, 1, 1)).format(timeFmtWithOptMicros);
/* 190 */         } else if (this.value instanceof LocalDateTime) {
/* 191 */           s = ((LocalDateTime)this.value).format(datetimeFmtWithOptMicros);
/*     */         } else {
/* 193 */           s = String.valueOf(this.value);
/*     */         } 
/* 195 */         return "'" + s + "'";
/*     */       case 15:
/*     */       case 253:
/*     */       case 254:
/* 199 */         if (quoteIfNeeded) {
/* 200 */           return "'" + String.valueOf(this.value) + "'";
/*     */         }
/* 202 */         return String.valueOf(this.value);
/*     */     } 
/*     */     
/* 205 */     if (this.value instanceof byte[]) {
/* 206 */       return "byte data";
/*     */     }
/* 208 */     if (quoteIfNeeded) {
/* 209 */       return "'" + String.valueOf(this.value) + "'";
/*     */     }
/* 211 */     return String.valueOf(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBoundLength() {
/* 216 */     if (this.isNull) {
/* 217 */       return 0L;
/*     */     }
/*     */     
/* 220 */     if (this.isStream) {
/* 221 */       return this.streamLength;
/*     */     }
/*     */     
/* 224 */     switch (this.bufferType) {
/*     */       
/*     */       case 1:
/* 227 */         return 1L;
/*     */       case 2:
/* 229 */         return 2L;
/*     */       case 3:
/* 231 */         return 4L;
/*     */       case 8:
/* 233 */         return 8L;
/*     */       case 4:
/* 235 */         return 4L;
/*     */       case 5:
/* 237 */         return 8L;
/*     */       case 11:
/* 239 */         return 9L;
/*     */       case 10:
/* 241 */         return 7L;
/*     */       case 7:
/*     */       case 12:
/* 244 */         return 11L;
/*     */       case 0:
/*     */       case 15:
/*     */       case 246:
/*     */       case 253:
/*     */       case 254:
/* 250 */         if (this.value instanceof byte[]) {
/* 251 */           return ((byte[])this.value).length;
/*     */         }
/* 253 */         return ((String)this.value).length();
/*     */     } 
/*     */     
/* 256 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void storeBinding(NativePacketPayload intoPacket, boolean isLoadDataQuery, String characterEncoding, ExceptionInterceptor interceptor) {
/* 261 */     synchronized (this) {
/*     */       
/*     */       try {
/* 264 */         switch (this.bufferType) {
/*     */           
/*     */           case 1:
/* 267 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, ((Long)this.value).longValue());
/*     */             return;
/*     */           case 2:
/* 270 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT2, ((Long)this.value).longValue());
/*     */             return;
/*     */           case 3:
/* 273 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, ((Long)this.value).longValue());
/*     */             return;
/*     */           case 8:
/* 276 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, ((Long)this.value).longValue());
/*     */             return;
/*     */           case 4:
/* 279 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, Float.floatToIntBits(((Float)this.value).floatValue()));
/*     */             return;
/*     */           case 5:
/* 282 */             intoPacket.writeInteger(NativeConstants.IntegerDataType.INT8, Double.doubleToLongBits(((Double)this.value).doubleValue()));
/*     */             return;
/*     */           case 11:
/* 285 */             storeTime(intoPacket);
/*     */             return;
/*     */           case 10:
/* 288 */             storeDate(intoPacket);
/*     */             return;
/*     */           case 7:
/*     */           case 12:
/* 292 */             storeDateTime(intoPacket, this.bufferType);
/*     */             return;
/*     */           case 0:
/*     */           case 15:
/*     */           case 246:
/*     */           case 253:
/*     */           case 254:
/* 299 */             if (this.value instanceof byte[]) {
/* 300 */               intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, (byte[])this.value);
/* 301 */             } else if (!isLoadDataQuery) {
/* 302 */               intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes((String)this.value, characterEncoding));
/*     */             } else {
/* 304 */               intoPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes((String)this.value));
/*     */             } 
/*     */             return;
/*     */         } 
/*     */ 
/*     */       
/* 310 */       } catch (CJException uEE) {
/* 311 */         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.22") + characterEncoding + "'", uEE, interceptor);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void storeDate(NativePacketPayload intoPacket) {
/* 317 */     synchronized (this) {
/*     */       int year, month, day;
/*     */       
/* 320 */       if (this.value instanceof LocalDate) {
/* 321 */         year = ((LocalDate)this.value).getYear();
/* 322 */         month = ((LocalDate)this.value).getMonthValue();
/* 323 */         day = ((LocalDate)this.value).getDayOfMonth();
/*     */       }
/* 325 */       else if (this.value instanceof LocalTime) {
/* 326 */         year = AbstractQueryBindings.DEFAULT_DATE.getYear();
/* 327 */         month = AbstractQueryBindings.DEFAULT_DATE.getMonthValue();
/* 328 */         day = AbstractQueryBindings.DEFAULT_DATE.getDayOfMonth();
/*     */       }
/* 330 */       else if (this.value instanceof LocalDateTime) {
/* 331 */         year = ((LocalDateTime)this.value).getYear();
/* 332 */         month = ((LocalDateTime)this.value).getMonthValue();
/* 333 */         day = ((LocalDateTime)this.value).getDayOfMonth();
/*     */       } else {
/*     */         
/* 336 */         if (this.calendar == null) {
/* 337 */           this.calendar = Calendar.getInstance(((Boolean)this.cacheDefaultTimeZone.getValue()).booleanValue() ? this.defaultTimeZone : TimeZone.getDefault(), Locale.US);
/*     */         }
/*     */         
/* 340 */         this.calendar.setTime((Date)this.value);
/*     */         
/* 342 */         this.calendar.set(11, 0);
/* 343 */         this.calendar.set(12, 0);
/* 344 */         this.calendar.set(13, 0);
/*     */         
/* 346 */         year = this.calendar.get(1);
/* 347 */         month = this.calendar.get(2) + 1;
/* 348 */         day = this.calendar.get(5);
/*     */       } 
/*     */       
/* 351 */       intoPacket.ensureCapacity(5);
/* 352 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 4L);
/* 353 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT2, year);
/* 354 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, month);
/* 355 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, day);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void storeTime(NativePacketPayload intoPacket) {
/* 360 */     int hours, minutes, seconds, microseconds, neg = 0, days = 0;
/*     */     
/* 362 */     if (this.value instanceof LocalDateTime) {
/* 363 */       hours = ((LocalDateTime)this.value).getHour();
/* 364 */       minutes = ((LocalDateTime)this.value).getMinute();
/* 365 */       seconds = ((LocalDateTime)this.value).getSecond();
/* 366 */       microseconds = ((LocalDateTime)this.value).getNano() / 1000;
/* 367 */     } else if (this.value instanceof LocalTime) {
/* 368 */       hours = ((LocalTime)this.value).getHour();
/* 369 */       minutes = ((LocalTime)this.value).getMinute();
/* 370 */       seconds = ((LocalTime)this.value).getSecond();
/* 371 */       microseconds = ((LocalTime)this.value).getNano() / 1000;
/* 372 */     } else if (this.value instanceof Duration) {
/* 373 */       neg = ((Duration)this.value).isNegative() ? 1 : 0;
/* 374 */       long fullSeconds = ((Duration)this.value).abs().getSeconds();
/* 375 */       seconds = (int)(fullSeconds % 60L);
/* 376 */       long fullMinutes = fullSeconds / 60L;
/* 377 */       minutes = (int)(fullMinutes % 60L);
/* 378 */       long fullHours = fullMinutes / 60L;
/* 379 */       hours = (int)(fullHours % 24L);
/* 380 */       days = (int)(fullHours / 24L);
/* 381 */       microseconds = ((Duration)this.value).abs().getNano() / 1000;
/*     */     } else {
/* 383 */       if (this.calendar == null) {
/* 384 */         this.calendar = Calendar.getInstance(this.defaultTimeZone, Locale.US);
/*     */       }
/*     */       
/* 387 */       this.calendar.setTime((Date)this.value);
/*     */       
/* 389 */       hours = this.calendar.get(11);
/* 390 */       minutes = this.calendar.get(12);
/* 391 */       seconds = this.calendar.get(13);
/* 392 */       microseconds = this.calendar.get(14) * 1000;
/*     */     } 
/*     */     
/* 395 */     intoPacket.ensureCapacity((microseconds > 0) ? 13 : 9);
/* 396 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, (microseconds > 0) ? 12L : 8L);
/* 397 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, neg);
/* 398 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, days);
/* 399 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, hours);
/* 400 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, minutes);
/* 401 */     intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, seconds);
/* 402 */     if (microseconds > 0) {
/* 403 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, microseconds);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void storeDateTime(NativePacketPayload intoPacket, int mysqlType) {
/* 414 */     synchronized (this) {
/*     */       
/* 416 */       int year = 0, month = 0, day = 0, hours = 0, minutes = 0, seconds = 0, microseconds = 0;
/*     */       
/* 418 */       if (this.value instanceof LocalDate) {
/* 419 */         year = ((LocalDate)this.value).getYear();
/* 420 */         month = ((LocalDate)this.value).getMonthValue();
/* 421 */         day = ((LocalDate)this.value).getDayOfMonth();
/*     */       }
/* 423 */       else if (this.value instanceof LocalTime) {
/* 424 */         year = AbstractQueryBindings.DEFAULT_DATE.getYear();
/* 425 */         month = AbstractQueryBindings.DEFAULT_DATE.getMonthValue();
/* 426 */         day = AbstractQueryBindings.DEFAULT_DATE.getDayOfMonth();
/* 427 */         hours = ((LocalTime)this.value).getHour();
/* 428 */         minutes = ((LocalTime)this.value).getMinute();
/* 429 */         seconds = ((LocalTime)this.value).getSecond();
/* 430 */         microseconds = ((LocalTime)this.value).getNano() / 1000;
/*     */       }
/* 432 */       else if (this.value instanceof LocalDateTime) {
/* 433 */         year = ((LocalDateTime)this.value).getYear();
/* 434 */         month = ((LocalDateTime)this.value).getMonthValue();
/* 435 */         day = ((LocalDateTime)this.value).getDayOfMonth();
/* 436 */         hours = ((LocalDateTime)this.value).getHour();
/* 437 */         minutes = ((LocalDateTime)this.value).getMinute();
/* 438 */         seconds = ((LocalDateTime)this.value).getSecond();
/* 439 */         microseconds = ((LocalDateTime)this.value).getNano() / 1000;
/*     */       } else {
/*     */         
/* 442 */         if (this.calendar == null) {
/* 443 */           this
/* 444 */             .calendar = Calendar.getInstance((mysqlType == 7 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.preserveInstants).getValue()).booleanValue()) ? this.connectionTimeZone : this.defaultTimeZone, Locale.US);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 449 */         this.calendar.setTime((Date)this.value);
/*     */         
/* 451 */         if (this.value instanceof java.sql.Date) {
/* 452 */           this.calendar.set(11, 0);
/* 453 */           this.calendar.set(12, 0);
/* 454 */           this.calendar.set(13, 0);
/*     */         } 
/*     */         
/* 457 */         year = this.calendar.get(1);
/* 458 */         month = this.calendar.get(2) + 1;
/* 459 */         day = this.calendar.get(5);
/* 460 */         hours = this.calendar.get(11);
/* 461 */         minutes = this.calendar.get(12);
/* 462 */         seconds = this.calendar.get(13);
/*     */         
/* 464 */         if (this.value instanceof Timestamp) {
/* 465 */           microseconds = ((Timestamp)this.value).getNanos() / 1000;
/*     */         } else {
/* 467 */           microseconds = this.calendar.get(14) * 1000;
/*     */         } 
/*     */       } 
/*     */       
/* 471 */       intoPacket.ensureCapacity((microseconds > 0) ? 12 : 8);
/* 472 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, (microseconds > 0) ? 11L : 7L);
/* 473 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT2, year);
/* 474 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, month);
/* 475 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, day);
/* 476 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, hours);
/* 477 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, minutes);
/* 478 */       intoPacket.writeInteger(NativeConstants.IntegerDataType.INT1, seconds);
/* 479 */       if (microseconds > 0) {
/* 480 */         intoPacket.writeInteger(NativeConstants.IntegerDataType.INT4, microseconds);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteValue() {
/* 487 */     if (!this.isStream) {
/* 488 */       return (this.charEncoding != null) ? StringUtils.getBytes(toString(), this.charEncoding) : toString().getBytes();
/*     */     }
/* 490 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ServerPreparedQueryBindValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */