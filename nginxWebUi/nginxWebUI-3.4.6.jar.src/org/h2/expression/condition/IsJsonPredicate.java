/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.json.JSONBytesSource;
/*     */ import org.h2.util.json.JSONItemType;
/*     */ import org.h2.util.json.JSONTarget;
/*     */ import org.h2.util.json.JSONValidationTargetWithUniqueKeys;
/*     */ import org.h2.util.json.JSONValidationTargetWithoutUniqueKeys;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueJson;
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
/*     */ public final class IsJsonPredicate
/*     */   extends Condition
/*     */ {
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private final boolean withUniqueKeys;
/*     */   private final JSONItemType itemType;
/*     */   
/*     */   public IsJsonPredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, JSONItemType paramJSONItemType) {
/*  39 */     this.left = paramExpression;
/*  40 */     this.whenOperand = paramBoolean2;
/*  41 */     this.not = paramBoolean1;
/*  42 */     this.withUniqueKeys = paramBoolean3;
/*  43 */     this.itemType = paramJSONItemType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  48 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  53 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  58 */     paramStringBuilder.append(" IS");
/*  59 */     if (this.not) {
/*  60 */       paramStringBuilder.append(" NOT");
/*     */     }
/*  62 */     paramStringBuilder.append(" JSON");
/*  63 */     switch (this.itemType) {
/*     */       case VALUE:
/*     */         break;
/*     */       case ARRAY:
/*  67 */         paramStringBuilder.append(" ARRAY");
/*     */         break;
/*     */       case OBJECT:
/*  70 */         paramStringBuilder.append(" OBJECT");
/*     */         break;
/*     */       case SCALAR:
/*  73 */         paramStringBuilder.append(" SCALAR");
/*     */         break;
/*     */       default:
/*  76 */         throw DbException.getInternalError("itemType=" + this.itemType);
/*     */     } 
/*  78 */     if (this.withUniqueKeys) {
/*  79 */       paramStringBuilder.append(" WITH UNIQUE KEYS");
/*     */     }
/*  81 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  86 */     this.left = this.left.optimize(paramSessionLocal);
/*  87 */     if (!this.whenOperand && this.left.isConstant()) {
/*  88 */       return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal));
/*     */     }
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  95 */     Value value = this.left.getValue(paramSessionLocal);
/*  96 */     if (value == ValueNull.INSTANCE) {
/*  97 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  99 */     return (Value)ValueBoolean.get(getValue(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 104 */     if (!this.whenOperand) {
/* 105 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/* 107 */     if (paramValue == ValueNull.INSTANCE) {
/* 108 */       return false;
/*     */     }
/* 110 */     return getValue(paramValue); } private boolean getValue(Value paramValue) {
/*     */     byte[] arrayOfByte;
/*     */     JSONItemType jSONItemType;
/*     */     String str;
/*     */     JSONTarget jSONTarget;
/* 115 */     switch (paramValue.getValueType())
/*     */     { case 5:
/*     */       case 6:
/*     */       case 7:
/* 119 */         arrayOfByte = paramValue.getBytesNoCopy();
/* 120 */         jSONTarget = (JSONTarget)(this.withUniqueKeys ? new JSONValidationTargetWithUniqueKeys() : new JSONValidationTargetWithoutUniqueKeys());
/*     */         
/*     */         try {
/* 123 */           int i = this.itemType.includes((JSONItemType)JSONBytesSource.parse(arrayOfByte, jSONTarget)) ^ this.not;
/* 124 */         } catch (RuntimeException runtimeException) {
/* 125 */           bool = this.not;
/*     */         } 
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
/* 157 */         return bool;case 38: jSONItemType = ((ValueJson)paramValue).getItemType(); if (!this.itemType.includes(jSONItemType)) { bool = this.not; } else if (!this.withUniqueKeys || jSONItemType == JSONItemType.SCALAR) { bool = !this.not; return bool; }  return bool;case 1: case 2: case 3: case 4: str = paramValue.getString(); }  boolean bool = this.not; return bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 162 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 167 */     if (this.whenOperand) {
/* 168 */       return null;
/*     */     }
/* 170 */     return new IsJsonPredicate(this.left, !this.not, false, this.withUniqueKeys, this.itemType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 175 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 180 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 185 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 190 */     return this.left.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 195 */     int i = this.left.getCost();
/* 196 */     if (this.left.getType().getValueType() == 38 && (!this.withUniqueKeys || this.itemType == JSONItemType.SCALAR)) {
/* 197 */       i++;
/*     */     } else {
/* 199 */       i += 10;
/*     */     } 
/* 201 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 206 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 211 */     if (paramInt == 0) {
/* 212 */       return this.left;
/*     */     }
/* 214 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\IsJsonPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */