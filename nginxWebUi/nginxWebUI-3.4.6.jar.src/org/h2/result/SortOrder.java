/*     */ package org.h2.result;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import org.h2.command.query.QueryOrderBy;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
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
/*     */ public final class SortOrder
/*     */   implements Comparator<Value[]>
/*     */ {
/*     */   public static final int ASCENDING = 0;
/*     */   public static final int DESCENDING = 1;
/*     */   public static final int NULLS_FIRST = 2;
/*     */   public static final int NULLS_LAST = 4;
/*     */   private final SessionLocal session;
/*     */   private final int[] queryColumnIndexes;
/*     */   private final int[] sortTypes;
/*     */   private final ArrayList<QueryOrderBy> orderList;
/*     */   
/*     */   public SortOrder(SessionLocal paramSessionLocal, int[] paramArrayOfint) {
/*  76 */     this(paramSessionLocal, paramArrayOfint, new int[paramArrayOfint.length], null);
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
/*     */   public SortOrder(SessionLocal paramSessionLocal, int[] paramArrayOfint1, int[] paramArrayOfint2, ArrayList<QueryOrderBy> paramArrayList) {
/*  89 */     this.session = paramSessionLocal;
/*  90 */     this.queryColumnIndexes = paramArrayOfint1;
/*  91 */     this.sortTypes = paramArrayOfint2;
/*  92 */     this.orderList = paramArrayList;
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
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, Expression[] paramArrayOfExpression, int paramInt1, int paramInt2) {
/* 106 */     byte b = 0;
/* 107 */     for (int i : this.queryColumnIndexes) {
/* 108 */       if (b) {
/* 109 */         paramStringBuilder.append(", ");
/*     */       }
/* 111 */       if (i < paramInt1) {
/* 112 */         paramStringBuilder.append(i + 1);
/*     */       } else {
/* 114 */         paramArrayOfExpression[i].getUnenclosedSQL(paramStringBuilder, paramInt2);
/*     */       } 
/* 116 */       typeToString(paramStringBuilder, this.sortTypes[b++]);
/*     */     } 
/* 118 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void typeToString(StringBuilder paramStringBuilder, int paramInt) {
/* 127 */     if ((paramInt & 0x1) != 0) {
/* 128 */       paramStringBuilder.append(" DESC");
/*     */     }
/* 130 */     if ((paramInt & 0x2) != 0) {
/* 131 */       paramStringBuilder.append(" NULLS FIRST");
/* 132 */     } else if ((paramInt & 0x4) != 0) {
/* 133 */       paramStringBuilder.append(" NULLS LAST");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compare(Value[] paramArrayOfValue1, Value[] paramArrayOfValue2) {
/*     */     byte b;
/*     */     int i;
/* 146 */     for (b = 0, i = this.queryColumnIndexes.length; b < i; b++) {
/* 147 */       int j = this.queryColumnIndexes[b];
/* 148 */       int k = this.sortTypes[b];
/* 149 */       Value value1 = paramArrayOfValue1[j];
/* 150 */       Value value2 = paramArrayOfValue2[j];
/* 151 */       boolean bool1 = (value1 == ValueNull.INSTANCE) ? true : false, bool2 = (value2 == ValueNull.INSTANCE) ? true : false;
/* 152 */       if (bool1 || bool2) {
/* 153 */         if (bool1 != bool2)
/*     */         {
/*     */           
/* 156 */           return this.session.getDatabase().getDefaultNullOrdering().compareNull(bool1, k); } 
/*     */       } else {
/* 158 */         int m = this.session.compare(value1, value2);
/* 159 */         if (m != 0)
/* 160 */           return ((k & 0x1) == 0) ? m : -m; 
/*     */       } 
/*     */     } 
/* 163 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sort(ArrayList<Value[]> paramArrayList) {
/* 172 */     paramArrayList.sort(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sort(ArrayList<Value[]> paramArrayList, int paramInt1, int paramInt2) {
/* 183 */     if (paramInt2 == 1 && paramInt1 == 0) {
/* 184 */       paramArrayList.set(0, Collections.min(paramArrayList, this));
/*     */       return;
/*     */     } 
/* 187 */     Value[][] arrayOfValue = paramArrayList.<Value[]>toArray(new Value[0][]);
/* 188 */     Utils.sortTopN((Object[])arrayOfValue, paramInt1, paramInt2, this);
/* 189 */     for (int i = paramInt1; i < paramInt2; i++) {
/* 190 */       paramArrayList.set(i, arrayOfValue[i]);
/*     */     }
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
/*     */   public int[] getQueryColumnIndexes() {
/* 206 */     return this.queryColumnIndexes;
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
/*     */   public Column getColumn(int paramInt, TableFilter paramTableFilter) {
/* 218 */     if (this.orderList == null) {
/* 219 */       return null;
/*     */     }
/* 221 */     QueryOrderBy queryOrderBy = this.orderList.get(paramInt);
/* 222 */     Expression expression = queryOrderBy.expression;
/* 223 */     if (expression == null) {
/* 224 */       return null;
/*     */     }
/* 226 */     expression = expression.getNonAliasExpression();
/* 227 */     if (expression.isConstant()) {
/* 228 */       return null;
/*     */     }
/* 230 */     if (!(expression instanceof ExpressionColumn)) {
/* 231 */       return null;
/*     */     }
/* 233 */     ExpressionColumn expressionColumn = (ExpressionColumn)expression;
/* 234 */     if (expressionColumn.getTableFilter() != paramTableFilter) {
/* 235 */       return null;
/*     */     }
/* 237 */     return expressionColumn.getColumn();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getSortTypes() {
/* 246 */     return this.sortTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<QueryOrderBy> getOrderList() {
/* 255 */     return this.orderList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getSortTypesWithNullOrdering() {
/* 266 */     return addNullOrdering(this.session.getDatabase(), (int[])this.sortTypes.clone());
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
/*     */   public static int[] addNullOrdering(Database paramDatabase, int[] paramArrayOfint) {
/* 280 */     DefaultNullOrdering defaultNullOrdering = paramDatabase.getDefaultNullOrdering(); byte b; int i;
/* 281 */     for (b = 0, i = paramArrayOfint.length; b < i; b++) {
/* 282 */       paramArrayOfint[b] = defaultNullOrdering.addExplicitNullOrdering(paramArrayOfint[b]);
/*     */     }
/* 284 */     return paramArrayOfint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Comparator<Value> getRowValueComparator() {
/* 293 */     return (paramValue1, paramValue2) -> compare(((ValueRow)paramValue1).getList(), ((ValueRow)paramValue2).getList());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\SortOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */