/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.math.RoundingMode;
/*    */ import org.h2.api.IntervalQualifier;
/*    */ import org.h2.engine.CastDataProvider;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.util.IntervalUtils;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueDecfloat;
/*    */ import org.h2.value.ValueDouble;
/*    */ import org.h2.value.ValueInterval;
/*    */ import org.h2.value.ValueNull;
/*    */ import org.h2.value.ValueNumeric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class AggregateDataAvg
/*    */   extends AggregateData
/*    */ {
/*    */   private final TypeInfo dataType;
/*    */   private long count;
/*    */   private double doubleValue;
/*    */   private BigDecimal decimalValue;
/*    */   private BigInteger integerValue;
/*    */   
/*    */   AggregateDataAvg(TypeInfo paramTypeInfo) {
/* 39 */     this.dataType = paramTypeInfo;
/*    */   }
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/*    */     BigDecimal bigDecimal;
/* 44 */     if (paramValue == ValueNull.INSTANCE) {
/*    */       return;
/*    */     }
/* 47 */     this.count++;
/* 48 */     switch (this.dataType.getValueType()) {
/*    */       case 15:
/* 50 */         this.doubleValue += paramValue.getDouble();
/*    */         return;
/*    */       case 13:
/*    */       case 16:
/* 54 */         bigDecimal = paramValue.getBigDecimal();
/* 55 */         this.decimalValue = (this.decimalValue == null) ? bigDecimal : this.decimalValue.add(bigDecimal);
/*    */         return;
/*    */     } 
/*    */     
/* 59 */     BigInteger bigInteger = IntervalUtils.intervalToAbsolute((ValueInterval)paramValue);
/* 60 */     this.integerValue = (this.integerValue == null) ? bigInteger : this.integerValue.add(bigInteger);
/*    */   }
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/*    */     ValueDouble valueDouble;
/*    */     ValueNumeric valueNumeric;
/*    */     ValueDecfloat valueDecfloat;
/* 67 */     if (this.count == 0L) {
/* 68 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/*    */     
/* 71 */     int i = this.dataType.getValueType();
/* 72 */     switch (i)
/*    */     { case 15:
/* 74 */         valueDouble = ValueDouble.get(this.doubleValue / this.count);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 87 */         return valueDouble.castTo(this.dataType, (CastDataProvider)paramSessionLocal);case 13: valueNumeric = ValueNumeric.get(this.decimalValue.divide(BigDecimal.valueOf(this.count), this.dataType.getScale(), RoundingMode.HALF_DOWN)); return valueNumeric.castTo(this.dataType, (CastDataProvider)paramSessionLocal);case 16: valueDecfloat = ValueDecfloat.divide(this.decimalValue, BigDecimal.valueOf(this.count), this.dataType); return valueDecfloat.castTo(this.dataType, (CastDataProvider)paramSessionLocal); }  ValueInterval valueInterval = IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(i - 22), this.integerValue.divide(BigInteger.valueOf(this.count))); return valueInterval.castTo(this.dataType, (CastDataProvider)paramSessionLocal);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataAvg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */