/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
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
/*     */ public class ConditionAndOr
/*     */   extends Condition
/*     */ {
/*     */   public static final int AND = 0;
/*     */   public static final int OR = 1;
/*     */   private final int andOrType;
/*     */   private Expression left;
/*     */   private Expression right;
/*     */   private Expression added;
/*     */   
/*     */   public ConditionAndOr(int paramInt, Expression paramExpression1, Expression paramExpression2) {
/*  44 */     if (paramExpression1 == null || paramExpression2 == null) {
/*  45 */       throw DbException.getInternalError(paramExpression1 + " " + paramExpression2);
/*     */     }
/*  47 */     this.andOrType = paramInt;
/*  48 */     this.left = paramExpression1;
/*  49 */     this.right = paramExpression2;
/*     */   }
/*     */   
/*     */   int getAndOrType() {
/*  53 */     return this.andOrType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  58 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  63 */     this.left.getSQL(paramStringBuilder, paramInt, 0);
/*  64 */     switch (this.andOrType) {
/*     */       case 0:
/*  66 */         paramStringBuilder.append("\n    AND ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  74 */         return this.right.getSQL(paramStringBuilder, paramInt, 0);case 1: paramStringBuilder.append("\n    OR "); return this.right.getSQL(paramStringBuilder, paramInt, 0);
/*     */     } 
/*     */     throw DbException.getInternalError("andOrType=" + this.andOrType);
/*     */   }
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/*  79 */     if (this.andOrType == 0) {
/*  80 */       this.left.createIndexConditions(paramSessionLocal, paramTableFilter);
/*  81 */       this.right.createIndexConditions(paramSessionLocal, paramTableFilter);
/*  82 */       if (this.added != null) {
/*  83 */         this.added.createIndexConditions(paramSessionLocal, paramTableFilter);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/*  92 */     Expression expression1 = this.left.getNotIfPossible(paramSessionLocal);
/*  93 */     if (expression1 == null) {
/*  94 */       expression1 = new ConditionNot(this.left);
/*     */     }
/*  96 */     Expression expression2 = this.right.getNotIfPossible(paramSessionLocal);
/*  97 */     if (expression2 == null) {
/*  98 */       expression2 = new ConditionNot(this.right);
/*     */     }
/* 100 */     boolean bool = (this.andOrType == 0) ? true : false;
/* 101 */     return new ConditionAndOr(bool, expression1, expression2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 106 */     Value value2, value1 = this.left.getValue(paramSessionLocal);
/*     */     
/* 108 */     switch (this.andOrType) {
/*     */       case 0:
/* 110 */         if (value1.isFalse() || (value2 = this.right.getValue(paramSessionLocal)).isFalse()) {
/* 111 */           return (Value)ValueBoolean.FALSE;
/*     */         }
/* 113 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 114 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 116 */         return (Value)ValueBoolean.TRUE;
/*     */       
/*     */       case 1:
/* 119 */         if (value1.isTrue() || (value2 = this.right.getValue(paramSessionLocal)).isTrue()) {
/* 120 */           return (Value)ValueBoolean.TRUE;
/*     */         }
/* 122 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 123 */           return (Value)ValueNull.INSTANCE;
/*     */         }
/* 125 */         return (Value)ValueBoolean.FALSE;
/*     */     } 
/*     */     
/* 128 */     throw DbException.getInternalError("type=" + this.andOrType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 136 */     this.left = this.left.optimize(paramSessionLocal);
/* 137 */     this.right = this.right.optimize(paramSessionLocal);
/* 138 */     int i = this.left.getCost(), j = this.right.getCost();
/* 139 */     if (j < i) {
/* 140 */       expression = this.left;
/* 141 */       this.left = this.right;
/* 142 */       this.right = expression;
/*     */     } 
/* 144 */     switch (this.andOrType) {
/*     */       case 0:
/* 146 */         if (!(paramSessionLocal.getDatabase().getSettings()).optimizeTwoEquals) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 156 */         if (this.left instanceof Comparison && this.right instanceof Comparison) {
/*     */           
/* 158 */           expression = ((Comparison)this.left).getAdditionalAnd(paramSessionLocal, (Comparison)this.right);
/* 159 */           if (expression != null) {
/* 160 */             this.added = expression.optimize(paramSessionLocal);
/*     */           }
/*     */         } 
/*     */         break;
/*     */       case 1:
/* 165 */         if (!(paramSessionLocal.getDatabase().getSettings()).optimizeOr) {
/*     */           break;
/*     */         }
/*     */         
/* 169 */         if (this.left instanceof Comparison && this.right instanceof Comparison) {
/* 170 */           expression = ((Comparison)this.left).optimizeOr(paramSessionLocal, (Comparison)this.right);
/* 171 */         } else if (this.left instanceof ConditionIn && this.right instanceof Comparison) {
/* 172 */           expression = ((ConditionIn)this.left).getAdditional((Comparison)this.right);
/* 173 */         } else if (this.right instanceof ConditionIn && this.left instanceof Comparison) {
/* 174 */           expression = ((ConditionIn)this.right).getAdditional((Comparison)this.left);
/* 175 */         } else if (this.left instanceof ConditionInConstantSet && this.right instanceof Comparison) {
/* 176 */           expression = ((ConditionInConstantSet)this.left).getAdditional(paramSessionLocal, (Comparison)this.right);
/* 177 */         } else if (this.right instanceof ConditionInConstantSet && this.left instanceof Comparison) {
/* 178 */           expression = ((ConditionInConstantSet)this.right).getAdditional(paramSessionLocal, (Comparison)this.left);
/* 179 */         } else if (this.left instanceof ConditionAndOr && this.right instanceof ConditionAndOr) {
/* 180 */           expression = optimizeConditionAndOr((ConditionAndOr)this.left, (ConditionAndOr)this.right);
/*     */         } else {
/*     */           break;
/*     */         } 
/*     */         
/* 185 */         if (expression != null)
/* 186 */           return expression.optimize(paramSessionLocal); 
/*     */         break;
/*     */     } 
/* 189 */     Expression expression = optimizeIfConstant(paramSessionLocal, this.andOrType, this.left, this.right);
/* 190 */     if (expression == null) {
/* 191 */       return optimizeN(this);
/*     */     }
/* 193 */     if (expression instanceof ConditionAndOr) {
/* 194 */       return optimizeN((ConditionAndOr)expression);
/*     */     }
/* 196 */     return expression;
/*     */   }
/*     */   
/*     */   private static Expression optimizeN(ConditionAndOr paramConditionAndOr) {
/* 200 */     if (paramConditionAndOr.right instanceof ConditionAndOr) {
/* 201 */       ConditionAndOr conditionAndOr = (ConditionAndOr)paramConditionAndOr.right;
/* 202 */       if (conditionAndOr.andOrType == paramConditionAndOr.andOrType) {
/* 203 */         return new ConditionAndOrN(paramConditionAndOr.andOrType, paramConditionAndOr.left, conditionAndOr.left, conditionAndOr.right);
/*     */       }
/*     */     } 
/*     */     
/* 207 */     if (paramConditionAndOr.right instanceof ConditionAndOrN) {
/* 208 */       ConditionAndOrN conditionAndOrN = (ConditionAndOrN)paramConditionAndOr.right;
/* 209 */       if (conditionAndOrN.getAndOrType() == paramConditionAndOr.andOrType) {
/* 210 */         conditionAndOrN.addFirst(paramConditionAndOr.left);
/* 211 */         return conditionAndOrN;
/*     */       } 
/*     */     } 
/* 214 */     return paramConditionAndOr;
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
/*     */   static Expression optimizeIfConstant(SessionLocal paramSessionLocal, int paramInt, Expression paramExpression1, Expression paramExpression2) {
/* 227 */     if (!paramExpression1.isConstant()) {
/* 228 */       if (!paramExpression2.isConstant()) {
/* 229 */         return null;
/*     */       }
/* 231 */       return optimizeConstant(paramSessionLocal, paramInt, paramExpression2.getValue(paramSessionLocal), paramExpression1);
/*     */     } 
/*     */     
/* 234 */     Value value1 = paramExpression1.getValue(paramSessionLocal);
/* 235 */     if (!paramExpression2.isConstant()) {
/* 236 */       return optimizeConstant(paramSessionLocal, paramInt, value1, paramExpression2);
/*     */     }
/* 238 */     Value value2 = paramExpression2.getValue(paramSessionLocal);
/* 239 */     switch (paramInt) {
/*     */       case 0:
/* 241 */         if (value1.isFalse() || value2.isFalse()) {
/* 242 */           return (Expression)ValueExpression.FALSE;
/*     */         }
/* 244 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 245 */           return (Expression)TypedValueExpression.UNKNOWN;
/*     */         }
/* 247 */         return (Expression)ValueExpression.TRUE;
/*     */       
/*     */       case 1:
/* 250 */         if (value1.isTrue() || value2.isTrue()) {
/* 251 */           return (Expression)ValueExpression.TRUE;
/*     */         }
/* 253 */         if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/* 254 */           return (Expression)TypedValueExpression.UNKNOWN;
/*     */         }
/* 256 */         return (Expression)ValueExpression.FALSE;
/*     */     } 
/*     */     
/* 259 */     throw DbException.getInternalError("type=" + paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Expression optimizeConstant(SessionLocal paramSessionLocal, int paramInt, Value paramValue, Expression paramExpression) {
/* 264 */     if (paramValue != ValueNull.INSTANCE) {
/* 265 */       switch (paramInt) {
/*     */         case 0:
/* 267 */           return paramValue.getBoolean() ? castToBoolean(paramSessionLocal, paramExpression) : (Expression)ValueExpression.FALSE;
/*     */         case 1:
/* 269 */           return paramValue.getBoolean() ? (Expression)ValueExpression.TRUE : castToBoolean(paramSessionLocal, paramExpression);
/*     */       } 
/* 271 */       throw DbException.getInternalError("type=" + paramInt);
/*     */     } 
/*     */     
/* 274 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addFilterConditions(TableFilter paramTableFilter) {
/* 279 */     if (this.andOrType == 0) {
/* 280 */       this.left.addFilterConditions(paramTableFilter);
/* 281 */       this.right.addFilterConditions(paramTableFilter);
/*     */     } else {
/* 283 */       super.addFilterConditions(paramTableFilter);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 289 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 290 */     this.right.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 295 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 296 */     this.right.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 301 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 302 */     this.right.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 307 */     return (this.left.isEverything(paramExpressionVisitor) && this.right.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 312 */     return this.left.getCost() + this.right.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 317 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 322 */     switch (paramInt) {
/*     */       case 0:
/* 324 */         return this.left;
/*     */       case 1:
/* 326 */         return this.right;
/*     */     } 
/* 328 */     throw new IndexOutOfBoundsException();
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
/*     */   static Expression optimizeConditionAndOr(ConditionAndOr paramConditionAndOr1, ConditionAndOr paramConditionAndOr2) {
/* 341 */     if (paramConditionAndOr1.andOrType != 0 || paramConditionAndOr2.andOrType != 0) {
/* 342 */       return null;
/*     */     }
/* 344 */     Expression expression1 = paramConditionAndOr1.getSubexpression(0), expression2 = paramConditionAndOr1.getSubexpression(1);
/* 345 */     Expression expression3 = paramConditionAndOr2.getSubexpression(0), expression4 = paramConditionAndOr2.getSubexpression(1);
/* 346 */     String str1 = expression3.getSQL(0);
/* 347 */     String str2 = expression4.getSQL(0);
/* 348 */     if (expression1.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 349 */       String str = expression1.getSQL(0);
/* 350 */       if (str.equals(str1)) {
/* 351 */         return new ConditionAndOr(0, expression1, new ConditionAndOr(1, expression2, expression4));
/*     */       }
/* 353 */       if (str.equals(str2)) {
/* 354 */         return new ConditionAndOr(0, expression1, new ConditionAndOr(1, expression2, expression3));
/*     */       }
/*     */     } 
/* 357 */     if (expression2.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 358 */       String str = expression2.getSQL(0);
/* 359 */       if (str.equals(str1))
/* 360 */         return new ConditionAndOr(0, expression2, new ConditionAndOr(1, expression1, expression4)); 
/* 361 */       if (str.equals(str2)) {
/* 362 */         return new ConditionAndOr(0, expression2, new ConditionAndOr(1, expression1, expression3));
/*     */       }
/*     */     } 
/* 365 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionAndOr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */