/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.expression.aggregate.Aggregate;
/*     */ import org.h2.expression.aggregate.AggregateType;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.DataType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Comparison
/*     */   extends Condition
/*     */ {
/*     */   public static final int EQUAL = 0;
/*     */   public static final int NOT_EQUAL = 1;
/*     */   public static final int SMALLER = 2;
/*     */   public static final int BIGGER = 3;
/*     */   public static final int SMALLER_EQUAL = 4;
/*     */   public static final int BIGGER_EQUAL = 5;
/*     */   public static final int EQUAL_NULL_SAFE = 6;
/*     */   public static final int NOT_EQUAL_NULL_SAFE = 7;
/*     */   public static final int SPATIAL_INTERSECTS = 8;
/*  84 */   static final String[] COMPARE_TYPES = new String[] { "=", "<>", "<", ">", "<=", ">=", "IS NOT DISTINCT FROM", "IS DISTINCT FROM", "&&" };
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int FALSE = 9;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int IN_LIST = 10;
/*     */ 
/*     */   
/*     */   public static final int IN_QUERY = 11;
/*     */ 
/*     */   
/*     */   private int compareType;
/*     */ 
/*     */   
/*     */   private Expression left;
/*     */ 
/*     */   
/*     */   private Expression right;
/*     */ 
/*     */   
/*     */   private final boolean whenOperand;
/*     */ 
/*     */ 
/*     */   
/*     */   public Comparison(int paramInt, Expression paramExpression1, Expression paramExpression2, boolean paramBoolean) {
/* 112 */     this.left = paramExpression1;
/* 113 */     this.right = paramExpression2;
/* 114 */     this.compareType = paramInt;
/* 115 */     this.whenOperand = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 120 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 125 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 130 */     paramStringBuilder.append(' ').append(COMPARE_TYPES[this.compareType]).append(' ');
/* 131 */     return this.right.getSQL(paramStringBuilder, paramInt, (this.right instanceof Aggregate && ((Aggregate)this.right)
/* 132 */         .getAggregateType() == AggregateType.ANY) ? 1 : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 139 */     this.left = this.left.optimize(paramSessionLocal);
/* 140 */     this.right = this.right.optimize(paramSessionLocal);
/*     */     
/* 142 */     TypeInfo typeInfo1 = this.left.getType(), typeInfo2 = this.right.getType();
/* 143 */     if ((paramSessionLocal.getMode()).numericWithBooleanComparison) {
/* 144 */       int i; switch (this.compareType) {
/*     */         case 0:
/*     */         case 1:
/*     */         case 6:
/*     */         case 7:
/* 149 */           i = typeInfo1.getValueType();
/* 150 */           if ((i == 8) ? 
/* 151 */             DataType.isNumericType(typeInfo2.getValueType()) : (
/*     */ 
/*     */             
/* 154 */             DataType.isNumericType(i) && typeInfo2.getValueType() == 8)) {
/*     */             break;
/*     */           }
/*     */         
/*     */         default:
/* 159 */           TypeInfo.checkComparable(typeInfo1, typeInfo2); break;
/*     */       } 
/* 161 */       if (this.whenOperand) {
/* 162 */         return this;
/*     */       }
/* 164 */       if (this.right instanceof ExpressionColumn && (
/* 165 */         this.left.isConstant() || this.left instanceof Parameter)) {
/* 166 */         Expression expression = this.left;
/* 167 */         this.left = this.right;
/* 168 */         this.right = expression;
/* 169 */         this.compareType = getReversedCompareType(this.compareType);
/*     */       } 
/*     */       
/* 172 */       if (this.left instanceof ExpressionColumn) {
/* 173 */         if (this.right.isConstant()) {
/* 174 */           Value value = this.right.getValue(paramSessionLocal);
/* 175 */           if (value == ValueNull.INSTANCE && (
/* 176 */             this.compareType & 0xFFFFFFFE) != 6) {
/* 177 */             return (Expression)TypedValueExpression.UNKNOWN;
/*     */           }
/*     */           
/* 180 */           typeInfo2 = this.left.getType(); TypeInfo typeInfo = value.getType();
/* 181 */           int j = typeInfo.getValueType();
/* 182 */           if (j != typeInfo2.getValueType() || j >= 40) {
/* 183 */             TypeInfo typeInfo3 = TypeInfo.getHigherType(typeInfo2, typeInfo);
/*     */ 
/*     */ 
/*     */             
/* 187 */             if (j != typeInfo3.getValueType() || j >= 40) {
/* 188 */               Column column = ((ExpressionColumn)this.left).getColumn();
/* 189 */               this.right = (Expression)ValueExpression.get(value.convertTo(typeInfo3, (CastDataProvider)paramSessionLocal, column));
/*     */             } 
/*     */           } 
/* 192 */         } else if (this.right instanceof Parameter) {
/* 193 */           ((Parameter)this.right).setColumn(((ExpressionColumn)this.left).getColumn());
/*     */         } 
/*     */       }
/* 196 */       if (this.left.isConstant() && this.right.isConstant()) {
/* 197 */         return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal));
/*     */       }
/* 199 */       if (this.left.isNullConstant() || this.right.isNullConstant()) {
/*     */ 
/*     */         
/* 202 */         if ((this.compareType & 0xFFFFFFFE) != 6) {
/* 203 */           return (Expression)TypedValueExpression.UNKNOWN;
/*     */         }
/* 205 */         Expression expression = this.left.isNullConstant() ? this.right : this.left;
/* 206 */         int j = expression.getType().getValueType();
/* 207 */         if (j != -1 && j != 41) {
/* 208 */           return new NullPredicate(expression, (this.compareType == 7), false);
/*     */         }
/*     */       } 
/*     */       
/* 212 */       return this;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 217 */     Value value = this.left.getValue(paramSessionLocal);
/*     */     
/* 219 */     if (value == ValueNull.INSTANCE && (this.compareType & 0xFFFFFFFE) != 6) {
/* 220 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 222 */     return compare(paramSessionLocal, value, this.right.getValue(paramSessionLocal), this.compareType);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 227 */     if (!this.whenOperand) {
/* 228 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/*     */     
/* 231 */     if (paramValue == ValueNull.INSTANCE && (this.compareType & 0xFFFFFFFE) != 6) {
/* 232 */       return false;
/*     */     }
/* 234 */     return compare(paramSessionLocal, paramValue, this.right.getValue(paramSessionLocal), this.compareType).isTrue();
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
/*     */   static Value compare(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, int paramInt) {
/*     */     ValueBoolean valueBoolean;
/*     */     int i;
/* 248 */     switch (paramInt) {
/*     */       case 0:
/* 250 */         i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, true);
/* 251 */         if (i == 0) {
/* 252 */           valueBoolean = ValueBoolean.TRUE;
/* 253 */         } else if (i == Integer.MIN_VALUE) {
/* 254 */           ValueNull valueNull = ValueNull.INSTANCE;
/*     */         } else {
/* 256 */           valueBoolean = ValueBoolean.FALSE;
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
/* 328 */         return (Value)valueBoolean;case 6: valueBoolean = ValueBoolean.get(paramSessionLocal.areEqual(paramValue1, paramValue2)); return (Value)valueBoolean;case 1: i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, true); if (i == 0) { valueBoolean = ValueBoolean.FALSE; } else if (i == Integer.MIN_VALUE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.TRUE; }  return (Value)valueBoolean;case 7: valueBoolean = ValueBoolean.get(!paramSessionLocal.areEqual(paramValue1, paramValue2)); return (Value)valueBoolean;case 5: i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, false); if (i >= 0) { valueBoolean = ValueBoolean.TRUE; } else if (i == Integer.MIN_VALUE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.FALSE; }  return (Value)valueBoolean;case 3: i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, false); if (i > 0) { valueBoolean = ValueBoolean.TRUE; } else if (i == Integer.MIN_VALUE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.FALSE; }  return (Value)valueBoolean;case 4: i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, false); if (i == Integer.MIN_VALUE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.get((i <= 0)); }  return (Value)valueBoolean;case 2: i = paramSessionLocal.compareWithNull(paramValue1, paramValue2, false); if (i == Integer.MIN_VALUE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.get((i < 0)); }  return (Value)valueBoolean;case 8: if (paramValue1 == ValueNull.INSTANCE || paramValue2 == ValueNull.INSTANCE) { ValueNull valueNull = ValueNull.INSTANCE; } else { valueBoolean = ValueBoolean.get(paramValue1.convertToGeometry(null).intersectsBoundingBox(paramValue2.convertToGeometry(null))); }  return (Value)valueBoolean;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + paramInt);
/*     */   }
/*     */   public boolean isWhenConditionOperand() {
/* 333 */     return this.whenOperand;
/*     */   }
/*     */   
/*     */   private static int getReversedCompareType(int paramInt) {
/* 337 */     switch (paramInt) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/* 343 */         return paramInt;
/*     */       case 5:
/* 345 */         return 4;
/*     */       case 3:
/* 347 */         return 2;
/*     */       case 4:
/* 349 */         return 5;
/*     */       case 2:
/* 351 */         return 3;
/*     */     } 
/* 353 */     throw DbException.getInternalError("type=" + paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 359 */     if (this.compareType == 8 || this.whenOperand) {
/* 360 */       return null;
/*     */     }
/* 362 */     int i = getNotCompareType();
/* 363 */     return new Comparison(i, this.left, this.right, false);
/*     */   }
/*     */   
/*     */   private int getNotCompareType() {
/* 367 */     switch (this.compareType) {
/*     */       case 0:
/* 369 */         return 1;
/*     */       case 6:
/* 371 */         return 7;
/*     */       case 1:
/* 373 */         return 0;
/*     */       case 7:
/* 375 */         return 6;
/*     */       case 5:
/* 377 */         return 2;
/*     */       case 3:
/* 379 */         return 4;
/*     */       case 4:
/* 381 */         return 3;
/*     */       case 2:
/* 383 */         return 5;
/*     */     } 
/* 385 */     throw DbException.getInternalError("type=" + this.compareType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 391 */     if (!this.whenOperand) {
/* 392 */       createIndexConditions(paramTableFilter, this.left, this.right, this.compareType);
/*     */     }
/*     */   }
/*     */   
/*     */   static void createIndexConditions(TableFilter paramTableFilter, Expression paramExpression1, Expression paramExpression2, int paramInt) {
/* 397 */     if (!paramTableFilter.getTable().isQueryComparable()) {
/*     */       return;
/*     */     }
/* 400 */     ExpressionColumn expressionColumn1 = null;
/* 401 */     if (paramExpression1 instanceof ExpressionColumn) {
/* 402 */       expressionColumn1 = (ExpressionColumn)paramExpression1;
/* 403 */       if (paramTableFilter != expressionColumn1.getTableFilter()) {
/* 404 */         expressionColumn1 = null;
/*     */       }
/*     */     } 
/* 407 */     ExpressionColumn expressionColumn2 = null;
/* 408 */     if (paramExpression2 instanceof ExpressionColumn) {
/* 409 */       expressionColumn2 = (ExpressionColumn)paramExpression2;
/* 410 */       if (paramTableFilter != expressionColumn2.getTableFilter()) {
/* 411 */         expressionColumn2 = null;
/*     */       }
/*     */     } 
/*     */     
/* 415 */     if (((expressionColumn1 == null) ? true : false) == ((expressionColumn2 == null) ? true : false)) {
/*     */       return;
/*     */     }
/* 418 */     if (expressionColumn1 == null) {
/* 419 */       if (!paramExpression1.isEverything(ExpressionVisitor.getNotFromResolverVisitor((ColumnResolver)paramTableFilter))) {
/*     */         return;
/*     */       }
/*     */     }
/* 423 */     else if (!paramExpression2.isEverything(ExpressionVisitor.getNotFromResolverVisitor((ColumnResolver)paramTableFilter))) {
/*     */       return;
/*     */     } 
/*     */     
/* 427 */     switch (paramInt) {
/*     */       case 1:
/*     */       case 7:
/*     */         return;
/*     */       case 0:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 8:
/* 438 */         if (expressionColumn1 != null) {
/* 439 */           TypeInfo typeInfo = expressionColumn1.getType();
/* 440 */           if (TypeInfo.haveSameOrdering(typeInfo, TypeInfo.getHigherType(typeInfo, paramExpression2.getType()))) {
/* 441 */             paramTableFilter.addIndexCondition(IndexCondition.get(paramInt, expressionColumn1, paramExpression2));
/*     */           }
/*     */         } else {
/*     */           
/* 445 */           TypeInfo typeInfo = expressionColumn2.getType();
/* 446 */           if (TypeInfo.haveSameOrdering(typeInfo, TypeInfo.getHigherType(typeInfo, paramExpression1.getType()))) {
/* 447 */             paramTableFilter.addIndexCondition(IndexCondition.get(getReversedCompareType(paramInt), expressionColumn2, paramExpression1));
/*     */           }
/*     */         } 
/*     */     } 
/*     */     
/* 452 */     throw DbException.getInternalError("type=" + paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 458 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 459 */     if (this.right != null) {
/* 460 */       this.right.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 466 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 467 */     if (this.right != null) {
/* 468 */       this.right.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 474 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 475 */     this.right.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 480 */     return (this.left.isEverything(paramExpressionVisitor) && this.right.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 485 */     return this.left.getCost() + this.right.getCost() + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Expression getIfEquals(Expression paramExpression) {
/* 496 */     if (this.compareType == 0) {
/* 497 */       String str = paramExpression.getSQL(0);
/* 498 */       if (this.left.getSQL(0).equals(str))
/* 499 */         return this.right; 
/* 500 */       if (this.right.getSQL(0).equals(str)) {
/* 501 */         return this.left;
/*     */       }
/*     */     } 
/* 504 */     return null;
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
/*     */   Expression getAdditionalAnd(SessionLocal paramSessionLocal, Comparison paramComparison) {
/* 516 */     if (this.compareType == 0 && paramComparison.compareType == 0 && !this.whenOperand) {
/* 517 */       boolean bool1 = this.left.isConstant();
/* 518 */       boolean bool2 = this.right.isConstant();
/* 519 */       boolean bool3 = paramComparison.left.isConstant();
/* 520 */       boolean bool4 = paramComparison.right.isConstant();
/* 521 */       String str1 = this.left.getSQL(0);
/* 522 */       String str2 = paramComparison.left.getSQL(0);
/* 523 */       String str3 = this.right.getSQL(0);
/* 524 */       String str4 = paramComparison.right.getSQL(0);
/*     */ 
/*     */       
/* 527 */       if ((!bool2 || !bool4) && str1.equals(str2))
/* 528 */         return new Comparison(0, this.right, paramComparison.right, false); 
/* 529 */       if ((!bool2 || !bool3) && str1.equals(str4))
/* 530 */         return new Comparison(0, this.right, paramComparison.left, false); 
/* 531 */       if ((!bool1 || !bool4) && str3.equals(str2))
/* 532 */         return new Comparison(0, this.left, paramComparison.right, false); 
/* 533 */       if ((!bool1 || !bool3) && str3.equals(str4)) {
/* 534 */         return new Comparison(0, this.left, paramComparison.left, false);
/*     */       }
/*     */     } 
/* 537 */     return null;
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
/*     */   Expression optimizeOr(SessionLocal paramSessionLocal, Comparison paramComparison) {
/* 549 */     if (this.compareType == 0 && paramComparison.compareType == 0) {
/* 550 */       Expression expression1 = paramComparison.left;
/* 551 */       Expression expression2 = paramComparison.right;
/* 552 */       String str1 = expression1.getSQL(0);
/* 553 */       String str2 = expression2.getSQL(0);
/* 554 */       if (this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 555 */         String str = this.left.getSQL(0);
/* 556 */         if (str.equals(str1))
/* 557 */           return getConditionIn(this.left, this.right, expression2); 
/* 558 */         if (str.equals(str2)) {
/* 559 */           return getConditionIn(this.left, this.right, expression1);
/*     */         }
/*     */       } 
/* 562 */       if (this.right.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 563 */         String str = this.right.getSQL(0);
/* 564 */         if (str.equals(str1))
/* 565 */           return getConditionIn(this.right, this.left, expression2); 
/* 566 */         if (str.equals(str2)) {
/* 567 */           return getConditionIn(this.right, this.left, expression1);
/*     */         }
/*     */       } 
/*     */     } 
/* 571 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static ConditionIn getConditionIn(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3) {
/* 576 */     ArrayList<Expression> arrayList = new ArrayList(2);
/* 577 */     arrayList.add(paramExpression2);
/* 578 */     arrayList.add(paramExpression3);
/* 579 */     return new ConditionIn(paramExpression1, false, false, arrayList);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 584 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 589 */     switch (paramInt) {
/*     */       case 0:
/* 591 */         return this.left;
/*     */       case 1:
/* 593 */         return this.right;
/*     */     } 
/* 595 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\Comparison.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */