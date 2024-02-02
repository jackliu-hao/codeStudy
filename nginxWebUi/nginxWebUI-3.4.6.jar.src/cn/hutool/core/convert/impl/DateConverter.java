/*     */ package cn.hutool.core.convert.impl;
/*     */ 
/*     */ import cn.hutool.core.convert.AbstractConverter;
/*     */ import cn.hutool.core.convert.ConvertException;
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
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
/*     */ public class DateConverter
/*     */   extends AbstractConverter<Date>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<? extends Date> targetType;
/*     */   private String format;
/*     */   
/*     */   public DateConverter(Class<? extends Date> targetType) {
/*  32 */     this.targetType = targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DateConverter(Class<? extends Date> targetType, String format) {
/*  42 */     this.targetType = targetType;
/*  43 */     this.format = format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormat() {
/*  52 */     return this.format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFormat(String format) {
/*  61 */     this.format = format;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Date convertInternal(Object value) {
/*  66 */     if (value == null || (value instanceof CharSequence && StrUtil.isBlank(value.toString()))) {
/*  67 */       return null;
/*     */     }
/*  69 */     if (value instanceof TemporalAccessor)
/*  70 */       return wrap(DateUtil.date((TemporalAccessor)value)); 
/*  71 */     if (value instanceof Calendar)
/*  72 */       return wrap(DateUtil.date((Calendar)value)); 
/*  73 */     if (value instanceof Number) {
/*  74 */       return wrap(((Number)value).longValue());
/*     */     }
/*     */     
/*  77 */     String valueStr = convertToStr(value);
/*     */ 
/*     */     
/*  80 */     DateTime dateTime = StrUtil.isBlank(this.format) ? DateUtil.parse(valueStr) : DateUtil.parse(valueStr, this.format);
/*  81 */     if (null != dateTime) {
/*  82 */       return wrap(dateTime);
/*     */     }
/*     */ 
/*     */     
/*  86 */     throw new ConvertException("Can not convert {}:[{}] to {}", new Object[] { value.getClass().getName(), value, this.targetType.getName() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Date wrap(DateTime date) {
/*  97 */     if (Date.class == this.targetType) {
/*  98 */       return date.toJdkDate();
/*     */     }
/* 100 */     if (DateTime.class == this.targetType) {
/* 101 */       return (Date)date;
/*     */     }
/* 103 */     if (Date.class == this.targetType) {
/* 104 */       return date.toSqlDate();
/*     */     }
/* 106 */     if (Time.class == this.targetType) {
/* 107 */       return new Time(date.getTime());
/*     */     }
/* 109 */     if (Timestamp.class == this.targetType) {
/* 110 */       return date.toTimestamp();
/*     */     }
/*     */     
/* 113 */     throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", new Object[] { this.targetType.getName() }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Date wrap(long mills) {
/* 124 */     if (Date.class == this.targetType) {
/* 125 */       return new Date(mills);
/*     */     }
/* 127 */     if (DateTime.class == this.targetType) {
/* 128 */       return (Date)DateUtil.date(mills);
/*     */     }
/* 130 */     if (Date.class == this.targetType) {
/* 131 */       return new Date(mills);
/*     */     }
/* 133 */     if (Time.class == this.targetType) {
/* 134 */       return new Time(mills);
/*     */     }
/* 136 */     if (Timestamp.class == this.targetType) {
/* 137 */       return new Timestamp(mills);
/*     */     }
/*     */     
/* 140 */     throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", new Object[] { this.targetType.getName() }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<Date> getTargetType() {
/* 146 */     return (Class)this.targetType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\DateConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */