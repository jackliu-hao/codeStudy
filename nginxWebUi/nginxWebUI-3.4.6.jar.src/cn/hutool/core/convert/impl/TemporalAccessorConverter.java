/*     */ package cn.hutool.core.convert.impl;
/*     */ 
/*     */ import cn.hutool.core.convert.AbstractConverter;
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Objects;
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
/*     */ public class TemporalAccessorConverter
/*     */   extends AbstractConverter<TemporalAccessor>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<?> targetType;
/*     */   private String format;
/*     */   
/*     */   public TemporalAccessorConverter(Class<?> targetType) {
/*  55 */     this(targetType, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemporalAccessorConverter(Class<?> targetType, String format) {
/*  65 */     this.targetType = targetType;
/*  66 */     this.format = format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormat() {
/*  75 */     return this.format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFormat(String format) {
/*  84 */     this.format = format;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<TemporalAccessor> getTargetType() {
/*  90 */     return (Class)this.targetType;
/*     */   }
/*     */ 
/*     */   
/*     */   protected TemporalAccessor convertInternal(Object value) {
/*  95 */     if (value instanceof Long)
/*  96 */       return parseFromLong((Long)value); 
/*  97 */     if (value instanceof TemporalAccessor)
/*  98 */       return parseFromTemporalAccessor((TemporalAccessor)value); 
/*  99 */     if (value instanceof Date) {
/* 100 */       DateTime dateTime = DateUtil.date((Date)value);
/* 101 */       return parseFromInstant(dateTime.toInstant(), dateTime.getZoneId());
/* 102 */     }  if (value instanceof Calendar) {
/* 103 */       Calendar calendar = (Calendar)value;
/* 104 */       return parseFromInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
/*     */     } 
/* 106 */     return parseFromCharSequence(convertToStr(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromCharSequence(CharSequence value) {
/*     */     Instant instant;
/*     */     ZoneId zoneId;
/* 117 */     if (StrUtil.isBlank(value)) {
/* 118 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 123 */     if (null != this.format) {
/* 124 */       DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.format);
/* 125 */       instant = formatter.<Instant>parse(value, Instant::from);
/* 126 */       zoneId = formatter.getZone();
/*     */     } else {
/* 128 */       DateTime dateTime = DateUtil.parse(value);
/* 129 */       instant = ((DateTime)Objects.<DateTime>requireNonNull(dateTime)).toInstant();
/* 130 */       zoneId = dateTime.getZoneId();
/*     */     } 
/* 132 */     return parseFromInstant(instant, zoneId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromLong(Long time) {
/* 142 */     return parseFromInstant(Instant.ofEpochMilli(time.longValue()), (ZoneId)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromTemporalAccessor(TemporalAccessor temporalAccessor) {
/* 152 */     TemporalAccessor result = null;
/* 153 */     if (temporalAccessor instanceof LocalDateTime) {
/* 154 */       result = parseFromLocalDateTime((LocalDateTime)temporalAccessor);
/* 155 */     } else if (temporalAccessor instanceof ZonedDateTime) {
/* 156 */       result = parseFromZonedDateTime((ZonedDateTime)temporalAccessor);
/*     */     } 
/*     */     
/* 159 */     if (null == result) {
/* 160 */       result = parseFromInstant(DateUtil.toInstant(temporalAccessor), (ZoneId)null);
/*     */     }
/*     */     
/* 163 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromLocalDateTime(LocalDateTime localDateTime) {
/* 173 */     if (Instant.class.equals(this.targetType)) {
/* 174 */       return DateUtil.toInstant(localDateTime);
/*     */     }
/* 176 */     if (LocalDate.class.equals(this.targetType)) {
/* 177 */       return localDateTime.toLocalDate();
/*     */     }
/* 179 */     if (LocalTime.class.equals(this.targetType)) {
/* 180 */       return localDateTime.toLocalTime();
/*     */     }
/* 182 */     if (ZonedDateTime.class.equals(this.targetType)) {
/* 183 */       return localDateTime.atZone(ZoneId.systemDefault());
/*     */     }
/* 185 */     if (OffsetDateTime.class.equals(this.targetType)) {
/* 186 */       return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
/*     */     }
/* 188 */     if (OffsetTime.class.equals(this.targetType)) {
/* 189 */       return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
/*     */     }
/*     */     
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromZonedDateTime(ZonedDateTime zonedDateTime) {
/* 202 */     if (Instant.class.equals(this.targetType)) {
/* 203 */       return DateUtil.toInstant(zonedDateTime);
/*     */     }
/* 205 */     if (LocalDateTime.class.equals(this.targetType)) {
/* 206 */       return zonedDateTime.toLocalDateTime();
/*     */     }
/* 208 */     if (LocalDate.class.equals(this.targetType)) {
/* 209 */       return zonedDateTime.toLocalDate();
/*     */     }
/* 211 */     if (LocalTime.class.equals(this.targetType)) {
/* 212 */       return zonedDateTime.toLocalTime();
/*     */     }
/* 214 */     if (OffsetDateTime.class.equals(this.targetType)) {
/* 215 */       return zonedDateTime.toOffsetDateTime();
/*     */     }
/* 217 */     if (OffsetTime.class.equals(this.targetType)) {
/* 218 */       return zonedDateTime.toOffsetDateTime().toOffsetTime();
/*     */     }
/*     */     
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemporalAccessor parseFromInstant(Instant instant, ZoneId zoneId) {
/* 232 */     if (Instant.class.equals(this.targetType)) {
/* 233 */       return instant;
/*     */     }
/*     */     
/* 236 */     zoneId = (ZoneId)ObjectUtil.defaultIfNull(zoneId, ZoneId::systemDefault);
/*     */     
/* 238 */     TemporalAccessor result = null;
/* 239 */     if (LocalDateTime.class.equals(this.targetType)) {
/* 240 */       result = LocalDateTime.ofInstant(instant, zoneId);
/* 241 */     } else if (LocalDate.class.equals(this.targetType)) {
/* 242 */       result = instant.atZone(zoneId).toLocalDate();
/* 243 */     } else if (LocalTime.class.equals(this.targetType)) {
/* 244 */       result = instant.atZone(zoneId).toLocalTime();
/* 245 */     } else if (ZonedDateTime.class.equals(this.targetType)) {
/* 246 */       result = instant.atZone(zoneId);
/* 247 */     } else if (OffsetDateTime.class.equals(this.targetType)) {
/* 248 */       result = OffsetDateTime.ofInstant(instant, zoneId);
/* 249 */     } else if (OffsetTime.class.equals(this.targetType)) {
/* 250 */       result = OffsetTime.ofInstant(instant, zoneId);
/*     */     } 
/* 252 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\TemporalAccessorConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */