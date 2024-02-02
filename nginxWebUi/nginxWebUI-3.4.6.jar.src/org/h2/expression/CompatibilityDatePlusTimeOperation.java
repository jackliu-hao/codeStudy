/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimeTimeZone;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompatibilityDatePlusTimeOperation
/*     */   extends Operation2
/*     */ {
/*     */   public CompatibilityDatePlusTimeOperation(Expression paramExpression1, Expression paramExpression2) {
/*  28 */     super(paramExpression1, paramExpression2); byte b;
/*  29 */     TypeInfo typeInfo1 = paramExpression1.getType(), typeInfo2 = paramExpression2.getType();
/*     */     
/*  31 */     switch (typeInfo1.getValueType()) {
/*     */       case 21:
/*  33 */         if (typeInfo2.getValueType() == 19) {
/*  34 */           throw DbException.getUnsupportedException("TIMESTAMP WITH TIME ZONE + TIME WITH TIME ZONE");
/*     */         }
/*     */       
/*     */       case 18:
/*  38 */         b = (typeInfo2.getValueType() == 17) ? 20 : typeInfo1.getValueType();
/*     */         break;
/*     */       case 19:
/*  41 */         if (typeInfo2.getValueType() == 19) {
/*  42 */           throw DbException.getUnsupportedException("TIME WITH TIME ZONE + TIME WITH TIME ZONE");
/*     */         }
/*  44 */         b = (typeInfo2.getValueType() == 17) ? 21 : typeInfo1.getValueType();
/*     */         break;
/*     */       case 20:
/*  47 */         b = (typeInfo2.getValueType() == 19) ? 21 : 20;
/*     */         break;
/*     */       default:
/*  50 */         throw DbException.getUnsupportedException(
/*  51 */             Value.getTypeName(typeInfo1.getValueType()) + " + " + Value.getTypeName(typeInfo2.getValueType()));
/*     */     } 
/*  53 */     this.type = TypeInfo.getTypeInfo(b, 0L, Math.max(typeInfo1.getScale(), typeInfo2.getScale()), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  58 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  63 */     this.left.getSQL(paramStringBuilder, paramInt, 0).append(" + ");
/*  64 */     return this.right.getSQL(paramStringBuilder, paramInt, 0);
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     ValueTimestampTimeZone valueTimestampTimeZone;
/*  69 */     Value value1 = this.left.getValue(paramSessionLocal);
/*  70 */     Value value2 = this.right.getValue(paramSessionLocal);
/*  71 */     if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/*  72 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  74 */     switch (value1.getValueType()) {
/*     */       case 18:
/*  76 */         if (value2.getValueType() == 17) {
/*  77 */           return (Value)ValueTimestamp.fromDateValueAndNanos(((ValueDate)value2).getDateValue(), ((ValueTime)value1)
/*  78 */               .getNanos());
/*     */         }
/*     */         break;
/*     */       case 19:
/*  82 */         if (value2.getValueType() == 17) {
/*  83 */           ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)value1;
/*  84 */           return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(((ValueDate)value2).getDateValue(), valueTimeTimeZone.getNanos(), valueTimeTimeZone
/*  85 */               .getTimeZoneOffsetSeconds());
/*     */         } 
/*     */         break;
/*     */       case 20:
/*  89 */         if (value2.getValueType() == 19) {
/*  90 */           ValueTimestamp valueTimestamp = (ValueTimestamp)value1;
/*  91 */           valueTimestampTimeZone = ValueTimestampTimeZone.fromDateValueAndNanos(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos(), ((ValueTimeTimeZone)value2)
/*  92 */               .getTimeZoneOffsetSeconds());
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/*  97 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue((Value)valueTimestampTimeZone, (CastDataProvider)paramSessionLocal);
/*  98 */     long l1 = arrayOfLong[0];
/*  99 */     long l2 = arrayOfLong[1] + ((value2 instanceof ValueTime) ? ((ValueTime)value2).getNanos() : ((ValueTimeTimeZone)value2).getNanos());
/* 100 */     if (l2 >= 86400000000000L) {
/* 101 */       l2 -= 86400000000000L;
/* 102 */       l1 = DateTimeUtils.incrementDateValue(l1);
/*     */     } 
/* 104 */     return DateTimeUtils.dateTimeToValue((Value)valueTimestampTimeZone, l1, l2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 109 */     this.left = this.left.optimize(paramSessionLocal);
/* 110 */     this.right = this.right.optimize(paramSessionLocal);
/* 111 */     if (this.left.isConstant() && this.right.isConstant()) {
/* 112 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 114 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\CompatibilityDatePlusTimeOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */