/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.date.format.GlobalCustomFormat;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.Month;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.time.temporal.TemporalField;
/*     */ import java.time.temporal.UnsupportedTemporalTypeException;
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
/*     */ public class TemporalAccessorUtil
/*     */   extends TemporalUtil
/*     */ {
/*     */   public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
/*  37 */     if (temporalAccessor.isSupported(field)) {
/*  38 */       return temporalAccessor.get(field);
/*     */     }
/*     */     
/*  41 */     return (int)field.range().getMinimum();
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
/*     */   public static String format(TemporalAccessor time, DateTimeFormatter formatter) {
/*  54 */     if (null == time) {
/*  55 */       return null;
/*     */     }
/*     */     
/*  58 */     if (time instanceof Month) {
/*  59 */       return time.toString();
/*     */     }
/*     */     
/*  62 */     if (null == formatter) {
/*  63 */       formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
/*     */     }
/*     */     
/*     */     try {
/*  67 */       return formatter.format(time);
/*  68 */     } catch (UnsupportedTemporalTypeException e) {
/*  69 */       if (time instanceof LocalDate && e.getMessage().contains("HourOfDay"))
/*     */       {
/*  71 */         return formatter.format(((LocalDate)time).atStartOfDay()); } 
/*  72 */       if (time instanceof LocalTime && e.getMessage().contains("YearOfEra"))
/*     */       {
/*  74 */         return formatter.format(((LocalTime)time).atDate(LocalDate.now())); } 
/*  75 */       if (time instanceof Instant)
/*     */       {
/*  77 */         return formatter.format(((Instant)time).atZone(ZoneId.systemDefault()));
/*     */       }
/*  79 */       throw e;
/*     */     } 
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
/*     */   public static String format(TemporalAccessor time, String format) {
/*  93 */     if (null == time) {
/*  94 */       return null;
/*     */     }
/*     */     
/*  97 */     if (time instanceof Month) {
/*  98 */       return time.toString();
/*     */     }
/*     */ 
/*     */     
/* 102 */     if (GlobalCustomFormat.isCustomFormat(format)) {
/* 103 */       return GlobalCustomFormat.format(time, format);
/*     */     }
/*     */ 
/*     */     
/* 107 */     DateTimeFormatter formatter = StrUtil.isBlank(format) ? null : DateTimeFormatter.ofPattern(format);
/*     */     
/* 109 */     return format(time, formatter);
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
/*     */   public static long toEpochMilli(TemporalAccessor temporalAccessor) {
/* 121 */     if (temporalAccessor instanceof Month) {
/* 122 */       return ((Month)temporalAccessor).getValue();
/*     */     }
/* 124 */     return toInstant(temporalAccessor).toEpochMilli();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Instant toInstant(TemporalAccessor temporalAccessor) {
/*     */     Instant result;
/* 135 */     if (null == temporalAccessor) {
/* 136 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 140 */     if (temporalAccessor instanceof Instant) {
/* 141 */       result = (Instant)temporalAccessor;
/* 142 */     } else if (temporalAccessor instanceof LocalDateTime) {
/* 143 */       result = ((LocalDateTime)temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
/* 144 */     } else if (temporalAccessor instanceof ZonedDateTime) {
/* 145 */       result = ((ZonedDateTime)temporalAccessor).toInstant();
/* 146 */     } else if (temporalAccessor instanceof OffsetDateTime) {
/* 147 */       result = ((OffsetDateTime)temporalAccessor).toInstant();
/* 148 */     } else if (temporalAccessor instanceof LocalDate) {
/* 149 */       result = ((LocalDate)temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
/* 150 */     } else if (temporalAccessor instanceof LocalTime) {
/*     */       
/* 152 */       result = ((LocalTime)temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
/* 153 */     } else if (temporalAccessor instanceof OffsetTime) {
/*     */       
/* 155 */       result = ((OffsetTime)temporalAccessor).atDate(LocalDate.now()).toInstant();
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 160 */       result = toInstant(LocalDateTimeUtil.of(temporalAccessor));
/*     */     } 
/*     */     
/* 163 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\TemporalAccessorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */