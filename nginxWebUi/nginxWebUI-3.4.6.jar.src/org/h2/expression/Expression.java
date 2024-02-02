/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.function.NamedExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.HasSQL;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
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
/*     */ public abstract class Expression
/*     */   implements HasSQL, Typed
/*     */ {
/*     */   public static final int MAP_INITIAL = 0;
/*     */   public static final int MAP_IN_WINDOW = 1;
/*     */   public static final int MAP_IN_AGGREGATE = 2;
/*     */   public static final int AUTO_PARENTHESES = 0;
/*     */   public static final int WITH_PARENTHESES = 1;
/*     */   public static final int WITHOUT_PARENTHESES = 2;
/*     */   private boolean addedToFilter;
/*     */   
/*     */   public static StringBuilder writeExpressions(StringBuilder paramStringBuilder, List<? extends Expression> paramList, int paramInt) {
/*     */     byte b;
/*     */     int i;
/*  74 */     for (b = 0, i = paramList.size(); b < i; b++) {
/*  75 */       if (b > 0) {
/*  76 */         paramStringBuilder.append(", ");
/*     */       }
/*  78 */       ((Expression)paramList.get(b)).getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */     } 
/*  80 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder writeExpressions(StringBuilder paramStringBuilder, Expression[] paramArrayOfExpression, int paramInt) {
/*     */     byte b;
/*     */     int i;
/*  92 */     for (b = 0, i = paramArrayOfExpression.length; b < i; b++) {
/*  93 */       if (b > 0) {
/*  94 */         paramStringBuilder.append(", ");
/*     */       }
/*  96 */       Expression expression = paramArrayOfExpression[b];
/*  97 */       if (expression == null) {
/*  98 */         paramStringBuilder.append("DEFAULT");
/*     */       } else {
/* 100 */         expression.getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */       } 
/*     */     } 
/* 103 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Value getValue(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract TypeInfo getType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Expression optimize(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Expression optimizeCondition(SessionLocal paramSessionLocal) {
/* 148 */     Expression expression = optimize(paramSessionLocal);
/* 149 */     if (expression.isConstant()) {
/* 150 */       return expression.getBooleanValue(paramSessionLocal) ? null : ValueExpression.FALSE;
/*     */     }
/* 152 */     return expression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getSQL(int paramInt) {
/* 166 */     return getSQL(new StringBuilder(), paramInt, 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public final StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 171 */     return getSQL(paramStringBuilder, paramInt, 0);
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
/*     */   
/*     */   public final String getSQL(int paramInt1, int paramInt2) {
/* 185 */     return getSQL(new StringBuilder(), paramInt1, paramInt2).toString();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public final StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt1, int paramInt2) {
/* 201 */     return (paramInt2 == 1 || (paramInt2 != 2 && needParentheses())) ? 
/* 202 */       getUnenclosedSQL(paramStringBuilder.append('('), paramInt1).append(')') : 
/* 203 */       getUnenclosedSQL(paramStringBuilder, paramInt1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 213 */     return false;
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
/*     */ 
/*     */   
/*     */   public final StringBuilder getEnclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 228 */     return getUnenclosedSQL(paramStringBuilder.append('('), paramInt).append(')');
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
/*     */   public abstract StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt);
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
/*     */   public abstract void updateAggregate(SessionLocal paramSessionLocal, int paramInt);
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
/*     */   public abstract boolean isEverything(ExpressionVisitor paramExpressionVisitor);
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
/*     */   public abstract int getCost();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 284 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/* 293 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNullConstant() {
/* 302 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isValueSet() {
/* 311 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIdentity() {
/* 320 */     return false;
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
/*     */   public boolean getBooleanValue(SessionLocal paramSessionLocal) {
/* 332 */     return getValue(paramSessionLocal).isTrue();
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
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getColumnName(SessionLocal paramSessionLocal, int paramInt) {
/* 354 */     return getAlias(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemaName() {
/* 363 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTableName() {
/* 372 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNullable() {
/* 381 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTableAlias() {
/* 391 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlias(SessionLocal paramSessionLocal, int paramInt) {
/*     */     String str;
/* 403 */     switch ((paramSessionLocal.getMode()).expressionNames)
/*     */     { default:
/* 405 */         str = getSQL(5, 2);
/* 406 */         if (str.length() <= 256) {
/* 407 */           return str;
/*     */         }
/*     */ 
/*     */       
/*     */       case AS_IS:
/* 412 */         return "C" + (paramInt + 1);
/*     */       case EXCEPTION:
/* 414 */         return "";
/*     */       case MYSQL_STYLE:
/* 416 */         return Integer.toString(paramInt + 1);
/*     */       case null:
/* 418 */         break; }  if (this instanceof NamedExpression) {
/* 419 */       return StringUtils.toLowerEnglish(((NamedExpression)this).getName());
/*     */     }
/* 421 */     return "?column?";
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
/*     */   public String getColumnNameForView(SessionLocal paramSessionLocal, int paramInt) {
/* 433 */     switch ((paramSessionLocal.getMode()).viewExpressionNames)
/*     */     
/*     */     { default:
/* 436 */         return getAlias(paramSessionLocal, paramInt);
/*     */       case EXCEPTION:
/* 438 */         throw DbException.get(90156, getTraceSQL());
/*     */       case MYSQL_STYLE:
/* 440 */         break; }  String str = getSQL(5, 2);
/* 441 */     if (str.length() > 64) {
/* 442 */       str = "Name_exp_" + (paramInt + 1);
/*     */     }
/* 444 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getNonAliasExpression() {
/* 455 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFilterConditions(TableFilter paramTableFilter) {
/* 464 */     if (!this.addedToFilter && isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
/* 465 */       paramTableFilter.addFilterCondition(this, false);
/* 466 */       this.addedToFilter = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 477 */     return getTraceSQL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 486 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 497 */     throw new IndexOutOfBoundsException();
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
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 510 */     return (paramSessionLocal.compareWithNull(paramValue, getValue(paramSessionLocal), true) == 0);
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
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 523 */     return getUnenclosedSQL(paramStringBuilder.append(' '), paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 533 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */