/*     */ package org.h2.expression.aggregate;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.function.BitFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
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
/*     */ final class AggregateDataDefault
/*     */   extends AggregateData
/*     */ {
/*     */   private final AggregateType aggregateType;
/*     */   private final TypeInfo dataType;
/*     */   private Value value;
/*     */   
/*     */   AggregateDataDefault(AggregateType paramAggregateType, TypeInfo paramTypeInfo) {
/*  30 */     this.aggregateType = paramAggregateType;
/*  31 */     this.dataType = paramTypeInfo;
/*     */   }
/*     */   
/*     */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/*     */     ValueBoolean valueBoolean;
/*  36 */     if (paramValue == ValueNull.INSTANCE) {
/*     */       return;
/*     */     }
/*  39 */     switch (this.aggregateType) {
/*     */       case SUM:
/*  41 */         if (this.value == null) {
/*  42 */           this.value = paramValue.convertTo(this.dataType.getValueType());
/*     */         } else {
/*  44 */           paramValue = paramValue.convertTo(this.value.getValueType());
/*  45 */           this.value = this.value.add(paramValue);
/*     */         } 
/*     */         return;
/*     */       case MIN:
/*  49 */         if (this.value == null || paramSessionLocal.compare(paramValue, this.value) < 0) {
/*  50 */           this.value = paramValue;
/*     */         }
/*     */         return;
/*     */       case MAX:
/*  54 */         if (this.value == null || paramSessionLocal.compare(paramValue, this.value) > 0) {
/*  55 */           this.value = paramValue;
/*     */         }
/*     */         return;
/*     */       case EVERY:
/*  59 */         valueBoolean = paramValue.convertToBoolean();
/*  60 */         if (this.value == null) {
/*  61 */           this.value = (Value)valueBoolean;
/*     */         } else {
/*  63 */           this.value = (Value)ValueBoolean.get((this.value.getBoolean() && valueBoolean.getBoolean()));
/*     */         } 
/*     */         return;
/*     */       case ANY:
/*  67 */         valueBoolean = valueBoolean.convertToBoolean();
/*  68 */         if (this.value == null) {
/*  69 */           this.value = (Value)valueBoolean;
/*     */         } else {
/*  71 */           this.value = (Value)ValueBoolean.get((this.value.getBoolean() || valueBoolean.getBoolean()));
/*     */         } 
/*     */         return;
/*     */       case BIT_AND_AGG:
/*     */       case BIT_NAND_AGG:
/*  76 */         if (this.value == null) {
/*  77 */           this.value = (Value)valueBoolean;
/*     */         } else {
/*  79 */           this.value = BitFunction.getBitwise(0, this.dataType, this.value, (Value)valueBoolean);
/*     */         } 
/*     */         return;
/*     */       case BIT_OR_AGG:
/*     */       case BIT_NOR_AGG:
/*  84 */         if (this.value == null) {
/*  85 */           this.value = (Value)valueBoolean;
/*     */         } else {
/*  87 */           this.value = BitFunction.getBitwise(1, this.dataType, this.value, (Value)valueBoolean);
/*     */         } 
/*     */         return;
/*     */       case BIT_XOR_AGG:
/*     */       case BIT_XNOR_AGG:
/*  92 */         if (this.value == null) {
/*  93 */           this.value = (Value)valueBoolean;
/*     */         } else {
/*  95 */           this.value = BitFunction.getBitwise(2, this.dataType, this.value, (Value)valueBoolean);
/*     */         } 
/*     */         return;
/*     */     } 
/*  99 */     throw DbException.getInternalError("type=" + this.aggregateType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Value getValue(SessionLocal paramSessionLocal) {
/* 106 */     Value value = this.value;
/* 107 */     if (value == null) {
/* 108 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 110 */     switch (this.aggregateType) {
/*     */       case BIT_NAND_AGG:
/*     */       case BIT_NOR_AGG:
/*     */       case BIT_XNOR_AGG:
/* 114 */         value = BitFunction.getBitwise(3, this.dataType, value, null); break;
/*     */     } 
/* 116 */     return value.convertTo(this.dataType);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */