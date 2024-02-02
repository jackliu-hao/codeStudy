/*     */ package com.mysql.cj;
/*     */ 
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
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
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
/*     */ public class NativeQueryAttributesBindValue
/*     */   implements QueryAttributesBindValue
/*     */ {
/*  49 */   private static final Map<Class<?>, Integer> JAVA_TO_MYSQL_FIELD_TYPE = new HashMap<>();
/*     */   static {
/*  51 */     JAVA_TO_MYSQL_FIELD_TYPE.put(String.class, Integer.valueOf(254));
/*  52 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Boolean.class, Integer.valueOf(1));
/*  53 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Byte.class, Integer.valueOf(1));
/*  54 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Short.class, Integer.valueOf(2));
/*  55 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Integer.class, Integer.valueOf(3));
/*  56 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Long.class, Integer.valueOf(8));
/*  57 */     JAVA_TO_MYSQL_FIELD_TYPE.put(BigInteger.class, Integer.valueOf(8));
/*  58 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Float.class, Integer.valueOf(4));
/*  59 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Double.class, Integer.valueOf(5));
/*  60 */     JAVA_TO_MYSQL_FIELD_TYPE.put(BigDecimal.class, Integer.valueOf(5));
/*  61 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Date.class, Integer.valueOf(10));
/*  62 */     JAVA_TO_MYSQL_FIELD_TYPE.put(LocalDate.class, Integer.valueOf(10));
/*  63 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Time.class, Integer.valueOf(11));
/*  64 */     JAVA_TO_MYSQL_FIELD_TYPE.put(LocalTime.class, Integer.valueOf(11));
/*  65 */     JAVA_TO_MYSQL_FIELD_TYPE.put(OffsetTime.class, Integer.valueOf(11));
/*  66 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Duration.class, Integer.valueOf(11));
/*  67 */     JAVA_TO_MYSQL_FIELD_TYPE.put(LocalDateTime.class, Integer.valueOf(12));
/*  68 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Timestamp.class, Integer.valueOf(7));
/*  69 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Instant.class, Integer.valueOf(7));
/*  70 */     JAVA_TO_MYSQL_FIELD_TYPE.put(OffsetDateTime.class, Integer.valueOf(7));
/*  71 */     JAVA_TO_MYSQL_FIELD_TYPE.put(ZonedDateTime.class, Integer.valueOf(7));
/*  72 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Date.class, Integer.valueOf(7));
/*  73 */     JAVA_TO_MYSQL_FIELD_TYPE.put(Calendar.class, Integer.valueOf(7));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */   
/*     */   public Object value;
/*     */   
/*  83 */   protected int type = 6;
/*     */   
/*     */   protected NativeQueryAttributesBindValue(String name, Object value) {
/*  86 */     this.name = name;
/*  87 */     this.value = value;
/*  88 */     this.type = getMysqlFieldType(value);
/*     */   }
/*     */   
/*     */   private int getMysqlFieldType(Object obj) {
/*  92 */     if (obj == null) {
/*  93 */       return 6;
/*     */     }
/*     */     
/*  96 */     Integer mysqlFieldType = JAVA_TO_MYSQL_FIELD_TYPE.get(obj.getClass());
/*  97 */     if (mysqlFieldType != null) {
/*  98 */       return mysqlFieldType.intValue();
/*     */     }
/*     */ 
/*     */     
/* 102 */     Optional<Integer> mysqlType = JAVA_TO_MYSQL_FIELD_TYPE.entrySet().stream().filter(m -> ((Class)m.getKey()).isAssignableFrom(obj.getClass())).map(m -> (Integer)m.getValue()).findFirst();
/* 103 */     if (mysqlType.isPresent()) {
/* 104 */       return ((Integer)mysqlType.get()).intValue();
/*     */     }
/*     */ 
/*     */     
/* 108 */     return 254;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNull() {
/* 113 */     return (this.type == 6);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 118 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 123 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValue() {
/* 128 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBoundLength() {
/* 133 */     if (isNull()) {
/* 134 */       return 0L;
/*     */     }
/*     */     
/* 137 */     switch (this.type) {
/*     */       case 1:
/* 139 */         return 1L;
/*     */       case 2:
/* 141 */         return 2L;
/*     */       case 3:
/* 143 */         return 4L;
/*     */       case 8:
/* 145 */         return 8L;
/*     */       case 4:
/* 147 */         return 4L;
/*     */       case 5:
/* 149 */         return 8L;
/*     */       case 10:
/* 151 */         return 5L;
/*     */       case 11:
/* 153 */         return 13L;
/*     */       case 12:
/* 155 */         return 12L;
/*     */       case 7:
/* 157 */         return 14L;
/*     */       case 254:
/* 159 */         return (this.value.toString().length() + 9);
/*     */     } 
/* 161 */     return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\NativeQueryAttributesBindValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */