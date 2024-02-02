/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInterval;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTimeTimeZone;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TimeZoneOperation
/*     */   extends Operation1_2
/*     */ {
/*     */   public TimeZoneOperation(Expression paramExpression1, Expression paramExpression2) {
/*  27 */     super(paramExpression1, paramExpression2);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  32 */     this.left.getSQL(paramStringBuilder, paramInt, 0).append(" AT ");
/*  33 */     if (this.right != null) {
/*  34 */       this.right.getSQL(paramStringBuilder.append("TIME ZONE "), paramInt, 0);
/*     */     } else {
/*  36 */       paramStringBuilder.append("LOCAL");
/*     */     } 
/*  38 */     return paramStringBuilder;
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     ValueNull valueNull;
/*  43 */     Value value = this.left.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*  44 */     int i = value.getValueType();
/*  45 */     if ((i == 21 || i == 19) && this.right != null) {
/*  46 */       Value value1 = this.right.getValue(paramSessionLocal);
/*  47 */       if (value1 != ValueNull.INSTANCE) {
/*  48 */         ValueTimestampTimeZone valueTimestampTimeZone; if (i == 21) {
/*  49 */           ValueTimestampTimeZone valueTimestampTimeZone1 = (ValueTimestampTimeZone)value;
/*  50 */           long l1 = valueTimestampTimeZone1.getDateValue();
/*  51 */           long l2 = valueTimestampTimeZone1.getTimeNanos();
/*  52 */           int j = valueTimestampTimeZone1.getTimeZoneOffsetSeconds();
/*  53 */           int k = parseTimeZone(value1, l1, l2, j, true);
/*  54 */           if (j != k) {
/*  55 */             valueTimestampTimeZone = DateTimeUtils.timestampTimeZoneAtOffset(l1, l2, j, k);
/*     */           }
/*     */         } else {
/*  58 */           ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)valueTimestampTimeZone;
/*  59 */           long l = valueTimeTimeZone.getNanos();
/*  60 */           int j = valueTimeTimeZone.getTimeZoneOffsetSeconds();
/*  61 */           int k = parseTimeZone(value1, 1008673L, l, j, false);
/*  62 */           if (j != k) {
/*  63 */             l += (k - j) * 1000000000L;
/*  64 */             ValueTimeTimeZone valueTimeTimeZone1 = ValueTimeTimeZone.fromNanos(DateTimeUtils.normalizeNanosOfDay(l), k);
/*     */           } 
/*     */         } 
/*     */       } else {
/*  68 */         valueNull = ValueNull.INSTANCE;
/*     */       } 
/*     */     } 
/*  71 */     return (Value)valueNull;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int parseTimeZone(Value paramValue, long paramLong1, long paramLong2, int paramInt, boolean paramBoolean) {
/*  76 */     if (DataType.isCharacterStringType(paramValue.getValueType())) {
/*     */       TimeZoneProvider timeZoneProvider;
/*     */       try {
/*  79 */         timeZoneProvider = TimeZoneProvider.ofId(paramValue.getString());
/*  80 */       } catch (RuntimeException runtimeException) {
/*  81 */         throw DbException.getInvalidValueException("time zone", paramValue.getTraceSQL());
/*     */       } 
/*  83 */       if (!paramBoolean && !timeZoneProvider.hasFixedOffset()) {
/*  84 */         throw DbException.getInvalidValueException("time zone", paramValue.getTraceSQL());
/*     */       }
/*  86 */       return timeZoneProvider.getTimeZoneOffsetUTC(DateTimeUtils.getEpochSeconds(paramLong1, paramLong2, paramInt));
/*     */     } 
/*  88 */     return parseInterval(paramValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInterval(Value paramValue) {
/*  98 */     ValueInterval valueInterval = (ValueInterval)paramValue.convertTo(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND);
/*  99 */     long l1 = valueInterval.getLeading(), l2 = valueInterval.getRemaining();
/* 100 */     if (l1 > 18L || (l1 == 18L && l2 != 0L) || l2 % 1000000000L != 0L) {
/* 101 */       throw DbException.getInvalidValueException("time zone", valueInterval.getTraceSQL());
/*     */     }
/* 103 */     int i = (int)(l1 * 3600L + l2 / 1000000000L);
/* 104 */     if (valueInterval.isNegative()) {
/* 105 */       i = -i;
/*     */     }
/* 107 */     return i;
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     StringBuilder stringBuilder;
/*     */     int j;
/* 112 */     this.left = this.left.optimize(paramSessionLocal);
/* 113 */     if (this.right != null) {
/* 114 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/* 116 */     TypeInfo typeInfo = this.left.getType();
/* 117 */     byte b = 21; int i = 9;
/* 118 */     switch (typeInfo.getValueType()) {
/*     */       case 20:
/*     */       case 21:
/* 121 */         i = typeInfo.getScale();
/*     */         break;
/*     */       case 18:
/*     */       case 19:
/* 125 */         b = 19;
/* 126 */         i = typeInfo.getScale();
/*     */         break;
/*     */       default:
/* 129 */         stringBuilder = this.left.getSQL(new StringBuilder(), 3, 0);
/* 130 */         j = stringBuilder.length();
/* 131 */         stringBuilder.append(" AT ");
/* 132 */         if (this.right != null) {
/* 133 */           this.right.getSQL(stringBuilder.append("TIME ZONE "), 3, 0);
/*     */         } else {
/* 135 */           stringBuilder.append("LOCAL");
/*     */         } 
/* 137 */         throw DbException.getSyntaxError(stringBuilder.toString(), j, "time, timestamp");
/*     */     } 
/* 139 */     this.type = TypeInfo.getTypeInfo(b, -1L, i, null);
/* 140 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 141 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 143 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\TimeZoneOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */