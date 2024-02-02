/*     */ package org.h2.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.TableType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexCondition
/*     */ {
/*     */   public static final int EQUALITY = 1;
/*     */   public static final int START = 2;
/*     */   public static final int END = 4;
/*     */   public static final int RANGE = 6;
/*     */   public static final int ALWAYS_FALSE = 8;
/*     */   public static final int SPATIAL_INTERSECTS = 16;
/*     */   private final Column column;
/*     */   private final int compareType;
/*     */   private final Expression expression;
/*     */   private List<Expression> expressionList;
/*     */   private Query expressionQuery;
/*     */   
/*     */   private IndexCondition(int paramInt, ExpressionColumn paramExpressionColumn, Expression paramExpression) {
/*  82 */     this.compareType = paramInt;
/*  83 */     this.column = (paramExpressionColumn == null) ? null : paramExpressionColumn.getColumn();
/*  84 */     this.expression = paramExpression;
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
/*     */   public static IndexCondition get(int paramInt, ExpressionColumn paramExpressionColumn, Expression paramExpression) {
/*  98 */     return new IndexCondition(paramInt, paramExpressionColumn, paramExpression);
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
/*     */   public static IndexCondition getInList(ExpressionColumn paramExpressionColumn, List<Expression> paramList) {
/* 111 */     IndexCondition indexCondition = new IndexCondition(10, paramExpressionColumn, null);
/*     */     
/* 113 */     indexCondition.expressionList = paramList;
/* 114 */     return indexCondition;
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
/*     */   public static IndexCondition getInQuery(ExpressionColumn paramExpressionColumn, Query paramQuery) {
/* 126 */     assert paramQuery.isRandomAccessResult();
/* 127 */     IndexCondition indexCondition = new IndexCondition(11, paramExpressionColumn, null);
/* 128 */     indexCondition.expressionQuery = paramQuery;
/* 129 */     return indexCondition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getCurrentValue(SessionLocal paramSessionLocal) {
/* 139 */     return this.expression.getValue(paramSessionLocal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value[] getCurrentValueList(SessionLocal paramSessionLocal) {
/* 150 */     TreeSet<Value> treeSet = new TreeSet((Comparator<?>)paramSessionLocal.getDatabase().getCompareMode());
/* 151 */     for (Expression expression : this.expressionList) {
/* 152 */       Value value = expression.getValue(paramSessionLocal);
/* 153 */       value = this.column.convert((CastDataProvider)paramSessionLocal, value);
/* 154 */       treeSet.add(value);
/*     */     } 
/* 156 */     Value[] arrayOfValue = (Value[])treeSet.toArray((Object[])new Value[treeSet.size()]);
/* 157 */     Arrays.sort(arrayOfValue, (Comparator<? super Value>)paramSessionLocal.getDatabase().getCompareMode());
/* 158 */     return arrayOfValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getCurrentResult() {
/* 168 */     return this.expressionQuery.query(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSQL(int paramInt) {
/* 178 */     if (this.compareType == 9) {
/* 179 */       return "FALSE";
/*     */     }
/* 181 */     StringBuilder stringBuilder = new StringBuilder();
/* 182 */     this.column.getSQL(stringBuilder, paramInt);
/* 183 */     switch (this.compareType) {
/*     */       case 0:
/* 185 */         stringBuilder.append(" = ");
/*     */         break;
/*     */       case 6:
/* 188 */         stringBuilder.append((this.expression.isNullConstant() || (this.column
/* 189 */             .getType().getValueType() == 8 && this.expression.isConstant())) ? " IS " : " IS NOT DISTINCT FROM ");
/*     */         break;
/*     */ 
/*     */       
/*     */       case 5:
/* 194 */         stringBuilder.append(" >= ");
/*     */         break;
/*     */       case 3:
/* 197 */         stringBuilder.append(" > ");
/*     */         break;
/*     */       case 4:
/* 200 */         stringBuilder.append(" <= ");
/*     */         break;
/*     */       case 2:
/* 203 */         stringBuilder.append(" < ");
/*     */         break;
/*     */       case 10:
/* 206 */         Expression.writeExpressions(stringBuilder.append(" IN("), this.expressionList, paramInt).append(')');
/*     */         break;
/*     */       case 11:
/* 209 */         stringBuilder.append(" IN(");
/* 210 */         stringBuilder.append(this.expressionQuery.getPlanSQL(paramInt));
/* 211 */         stringBuilder.append(')');
/*     */         break;
/*     */       case 8:
/* 214 */         stringBuilder.append(" && ");
/*     */         break;
/*     */       default:
/* 217 */         throw DbException.getInternalError("type=" + this.compareType);
/*     */     } 
/* 219 */     if (this.expression != null) {
/* 220 */       this.expression.getSQL(stringBuilder, paramInt, 0);
/*     */     }
/* 222 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMask(ArrayList<IndexCondition> paramArrayList) {
/* 232 */     switch (this.compareType) {
/*     */       case 9:
/* 234 */         return 8;
/*     */       case 0:
/*     */       case 6:
/* 237 */         return 1;
/*     */       case 10:
/*     */       case 11:
/* 240 */         if (paramArrayList.size() > 1 && 
/* 241 */           TableType.TABLE != this.column.getTable().getTableType())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 250 */           return 0;
/*     */         }
/*     */         
/* 253 */         return 1;
/*     */       case 3:
/*     */       case 5:
/* 256 */         return 2;
/*     */       case 2:
/*     */       case 4:
/* 259 */         return 4;
/*     */       case 8:
/* 261 */         return 16;
/*     */     } 
/* 263 */     throw DbException.getInternalError("type=" + this.compareType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAlwaysFalse() {
/* 273 */     return (this.compareType == 9);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStart() {
/* 283 */     switch (this.compareType) {
/*     */       case 0:
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/* 288 */         return true;
/*     */     } 
/* 290 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnd() {
/* 301 */     switch (this.compareType) {
/*     */       case 0:
/*     */       case 2:
/*     */       case 4:
/*     */       case 6:
/* 306 */         return true;
/*     */     } 
/* 308 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSpatialIntersects() {
/* 319 */     switch (this.compareType) {
/*     */       case 8:
/* 321 */         return true;
/*     */     } 
/* 323 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCompareType() {
/* 328 */     return this.compareType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column getColumn() {
/* 337 */     return this.column;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getExpression() {
/* 346 */     return this.expression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Expression> getExpressionList() {
/* 355 */     return this.expressionList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query getExpressionQuery() {
/* 364 */     return this.expressionQuery;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEvaluatable() {
/* 373 */     if (this.expression != null) {
/* 374 */       return this.expression
/* 375 */         .isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
/*     */     }
/* 377 */     if (this.expressionList != null) {
/* 378 */       for (Expression expression : this.expressionList) {
/* 379 */         if (!expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
/* 380 */           return false;
/*     */         }
/*     */       } 
/* 383 */       return true;
/*     */     } 
/* 385 */     return this.expressionQuery
/* 386 */       .isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 391 */     StringBuilder stringBuilder = (new StringBuilder("column=")).append(this.column).append(", compareType=");
/* 392 */     return compareTypeToString(stringBuilder, this.compareType)
/* 393 */       .append(", expression=").append(this.expression)
/* 394 */       .append(", expressionList=").append(this.expressionList)
/* 395 */       .append(", expressionQuery=").append(this.expressionQuery).toString();
/*     */   }
/*     */   
/*     */   private static StringBuilder compareTypeToString(StringBuilder paramStringBuilder, int paramInt) {
/* 399 */     boolean bool = false;
/* 400 */     if ((paramInt & 0x1) == 1) {
/* 401 */       bool = true;
/* 402 */       paramStringBuilder.append("EQUALITY");
/*     */     } 
/* 404 */     if ((paramInt & 0x2) == 2) {
/* 405 */       if (bool) {
/* 406 */         paramStringBuilder.append(", ");
/*     */       }
/* 408 */       bool = true;
/* 409 */       paramStringBuilder.append("START");
/*     */     } 
/* 411 */     if ((paramInt & 0x4) == 4) {
/* 412 */       if (bool) {
/* 413 */         paramStringBuilder.append(", ");
/*     */       }
/* 415 */       bool = true;
/* 416 */       paramStringBuilder.append("END");
/*     */     } 
/* 418 */     if ((paramInt & 0x8) == 8) {
/* 419 */       if (bool) {
/* 420 */         paramStringBuilder.append(", ");
/*     */       }
/* 422 */       bool = true;
/* 423 */       paramStringBuilder.append("ALWAYS_FALSE");
/*     */     } 
/* 425 */     if ((paramInt & 0x10) == 16) {
/* 426 */       if (bool) {
/* 427 */         paramStringBuilder.append(", ");
/*     */       }
/* 429 */       paramStringBuilder.append("SPATIAL_INTERSECTS");
/*     */     } 
/* 431 */     return paramStringBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\IndexCondition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */