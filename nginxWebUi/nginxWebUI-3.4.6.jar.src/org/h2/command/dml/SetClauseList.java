/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionList;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.DataChangeDeltaTable;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.HasSQL;
/*     */ import org.h2.value.Value;
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
/*     */ public final class SetClauseList
/*     */   implements HasSQL
/*     */ {
/*     */   private final Table table;
/*     */   private final UpdateAction[] actions;
/*     */   private boolean onUpdate;
/*     */   
/*     */   public SetClauseList(Table paramTable) {
/*  41 */     this.table = paramTable;
/*  42 */     this.actions = new UpdateAction[(paramTable.getColumns()).length];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSingle(Column paramColumn, Expression paramExpression) {
/*  52 */     int i = paramColumn.getColumnId();
/*  53 */     if (this.actions[i] != null) {
/*  54 */       throw DbException.get(42121, paramColumn.getName());
/*     */     }
/*  56 */     if (paramExpression != ValueExpression.DEFAULT) {
/*  57 */       this.actions[i] = new SetSimple(paramExpression);
/*  58 */       if (paramExpression instanceof Parameter) {
/*  59 */         ((Parameter)paramExpression).setColumn(paramColumn);
/*     */       }
/*     */     } else {
/*  62 */       this.actions[i] = UpdateAction.SET_DEFAULT;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMultiple(ArrayList<Column> paramArrayList, Expression paramExpression) {
/*  73 */     int i = paramArrayList.size();
/*  74 */     if (paramExpression instanceof ExpressionList) {
/*  75 */       ExpressionList expressionList = (ExpressionList)paramExpression;
/*  76 */       if (!expressionList.isArray()) {
/*  77 */         if (i != expressionList.getSubexpressionCount()) {
/*  78 */           throw DbException.get(21002);
/*     */         }
/*  80 */         for (byte b = 0; b < i; b++) {
/*  81 */           addSingle(paramArrayList.get(b), expressionList.getSubexpression(b));
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*  86 */     if (i == 1) {
/*     */       
/*  88 */       addSingle(paramArrayList.get(0), paramExpression);
/*     */     } else {
/*  90 */       int[] arrayOfInt = new int[i];
/*  91 */       RowExpression rowExpression = new RowExpression(paramExpression, arrayOfInt);
/*  92 */       int j = (this.table.getColumns()).length - 1, k = 0; byte b;
/*  93 */       for (b = 0; b < i; b++) {
/*  94 */         int m = ((Column)paramArrayList.get(b)).getColumnId();
/*  95 */         if (m < j) {
/*  96 */           j = m;
/*     */         }
/*  98 */         if (m > k) {
/*  99 */           k = m;
/*     */         }
/*     */       } 
/* 102 */       for (b = 0; b < i; b++) {
/* 103 */         Column column = paramArrayList.get(b);
/* 104 */         int m = column.getColumnId();
/* 105 */         arrayOfInt[b] = m;
/* 106 */         if (this.actions[m] != null) {
/* 107 */           throw DbException.get(42121, column.getName());
/*     */         }
/* 109 */         this.actions[m] = new SetMultiple(rowExpression, b, (m == j), (m == k));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean prepareUpdate(Table paramTable, SessionLocal paramSessionLocal, ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption, LocalResult paramLocalResult, Row paramRow, boolean paramBoolean) {
/* 117 */     Column[] arrayOfColumn = paramTable.getColumns();
/* 118 */     int i = arrayOfColumn.length;
/* 119 */     Row row = paramTable.getTemplateRow(); byte b;
/* 120 */     for (b = 0; b < i; b++) {
/* 121 */       Value value; UpdateAction updateAction = this.actions[b];
/* 122 */       Column column = arrayOfColumn[b];
/*     */       
/* 124 */       if (updateAction == null || updateAction == UpdateAction.ON_UPDATE) {
/* 125 */         value = column.isGenerated() ? null : paramRow.getValue(b);
/* 126 */       } else if (updateAction == UpdateAction.SET_DEFAULT) {
/* 127 */         value = !column.isIdentity() ? null : paramRow.getValue(b);
/*     */       } else {
/* 129 */         value = updateAction.update(paramSessionLocal);
/* 130 */         if (value == ValueNull.INSTANCE && column.isDefaultOnNull()) {
/* 131 */           value = !column.isIdentity() ? null : paramRow.getValue(b);
/* 132 */         } else if (column.isGeneratedAlways()) {
/* 133 */           throw DbException.get(90154, column
/* 134 */               .getSQLWithTable(new StringBuilder(), 3).toString());
/*     */         } 
/*     */       } 
/* 137 */       row.setValue(b, value);
/*     */     } 
/* 139 */     row.setKey(paramRow.getKey());
/* 140 */     paramTable.convertUpdateRow(paramSessionLocal, row, false);
/* 141 */     b = 1;
/* 142 */     if (this.onUpdate) {
/* 143 */       if (!paramRow.hasSameValues(row)) {
/* 144 */         for (byte b1 = 0; b1 < i; b1++) {
/* 145 */           if (this.actions[b1] == UpdateAction.ON_UPDATE) {
/* 146 */             row.setValue(b1, arrayOfColumn[b1].getEffectiveOnUpdateExpression().getValue(paramSessionLocal));
/* 147 */           } else if (arrayOfColumn[b1].isGenerated()) {
/* 148 */             row.setValue(b1, null);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 153 */         paramTable.convertUpdateRow(paramSessionLocal, row, false);
/* 154 */       } else if (paramBoolean) {
/* 155 */         b = 0;
/*     */       } 
/* 157 */     } else if (paramBoolean && paramRow.hasSameValues(row)) {
/* 158 */       b = 0;
/*     */     } 
/* 160 */     if (paramResultOption == DataChangeDeltaTable.ResultOption.OLD) {
/* 161 */       paramResultTarget.addRow(paramRow.getValueList());
/* 162 */     } else if (paramResultOption == DataChangeDeltaTable.ResultOption.NEW) {
/* 163 */       paramResultTarget.addRow((Value[])row.getValueList().clone());
/*     */     } 
/* 165 */     if (!paramTable.fireRow() || !paramTable.fireBeforeRow(paramSessionLocal, paramRow, row)) {
/* 166 */       paramLocalResult.addRowForTable(paramRow);
/* 167 */       paramLocalResult.addRowForTable(row);
/*     */     } 
/* 169 */     if (paramResultOption == DataChangeDeltaTable.ResultOption.FINAL) {
/* 170 */       paramResultTarget.addRow(row.getValueList());
/*     */     }
/* 172 */     return b;
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
/*     */   boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 184 */     for (UpdateAction updateAction : this.actions) {
/* 185 */       if (updateAction != null && 
/* 186 */         !updateAction.isEverything(paramExpressionVisitor)) {
/* 187 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 191 */     return true;
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
/*     */   void mapAndOptimize(SessionLocal paramSessionLocal, ColumnResolver paramColumnResolver1, ColumnResolver paramColumnResolver2) {
/* 205 */     Column[] arrayOfColumn = this.table.getColumns();
/* 206 */     boolean bool = false;
/* 207 */     for (byte b = 0; b < this.actions.length; b++) {
/* 208 */       UpdateAction updateAction = this.actions[b];
/* 209 */       if (updateAction != null) {
/* 210 */         updateAction.mapAndOptimize(paramSessionLocal, paramColumnResolver1, paramColumnResolver2);
/*     */       } else {
/* 212 */         Column column = arrayOfColumn[b];
/* 213 */         if (column.getEffectiveOnUpdateExpression() != null) {
/* 214 */           this.actions[b] = UpdateAction.ON_UPDATE;
/* 215 */           bool = true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 219 */     this.onUpdate = bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 224 */     Column[] arrayOfColumn = this.table.getColumns();
/* 225 */     paramStringBuilder.append("\nSET\n    ");
/* 226 */     boolean bool = false;
/* 227 */     for (byte b = 0; b < this.actions.length; b++) {
/* 228 */       UpdateAction updateAction = this.actions[b];
/* 229 */       if (updateAction != null && updateAction != UpdateAction.ON_UPDATE) {
/* 230 */         if (updateAction.getClass() == SetMultiple.class) {
/* 231 */           SetMultiple setMultiple = (SetMultiple)updateAction;
/* 232 */           if (setMultiple.first) {
/* 233 */             if (bool) {
/* 234 */               paramStringBuilder.append(",\n    ");
/*     */             }
/* 236 */             bool = true;
/* 237 */             RowExpression rowExpression = setMultiple.row;
/* 238 */             paramStringBuilder.append('(');
/* 239 */             int[] arrayOfInt = rowExpression.columns; byte b1; int i;
/* 240 */             for (b1 = 0, i = arrayOfInt.length; b1 < i; b1++) {
/* 241 */               if (b1 > 0) {
/* 242 */                 paramStringBuilder.append(", ");
/*     */               }
/* 244 */               arrayOfColumn[arrayOfInt[b1]].getSQL(paramStringBuilder, paramInt);
/*     */             } 
/* 246 */             rowExpression.expression.getUnenclosedSQL(paramStringBuilder.append(") = "), paramInt);
/*     */           } 
/*     */         } else {
/* 249 */           if (bool) {
/* 250 */             paramStringBuilder.append(",\n    ");
/*     */           }
/* 252 */           bool = true;
/* 253 */           Column column = arrayOfColumn[b];
/* 254 */           if (updateAction != UpdateAction.SET_DEFAULT) {
/* 255 */             updateAction.getSQL(paramStringBuilder, paramInt, column);
/*     */           } else {
/* 257 */             column.getSQL(paramStringBuilder, paramInt).append(" = DEFAULT");
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 262 */     return paramStringBuilder;
/*     */   }
/*     */   
/*     */   private static class UpdateAction
/*     */   {
/* 267 */     static UpdateAction ON_UPDATE = new UpdateAction();
/*     */     
/* 269 */     static UpdateAction SET_DEFAULT = new UpdateAction();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Value update(SessionLocal param1SessionLocal) {
/* 275 */       throw DbException.getInternalError();
/*     */     }
/*     */     
/*     */     boolean isEverything(ExpressionVisitor param1ExpressionVisitor) {
/* 279 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     void mapAndOptimize(SessionLocal param1SessionLocal, ColumnResolver param1ColumnResolver1, ColumnResolver param1ColumnResolver2) {}
/*     */ 
/*     */     
/*     */     void getSQL(StringBuilder param1StringBuilder, int param1Int, Column param1Column) {
/* 287 */       throw DbException.getInternalError();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class SetSimple
/*     */     extends UpdateAction
/*     */   {
/*     */     private Expression expression;
/*     */     
/*     */     SetSimple(Expression param1Expression) {
/* 297 */       this.expression = param1Expression;
/*     */     }
/*     */ 
/*     */     
/*     */     Value update(SessionLocal param1SessionLocal) {
/* 302 */       return this.expression.getValue(param1SessionLocal);
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isEverything(ExpressionVisitor param1ExpressionVisitor) {
/* 307 */       return this.expression.isEverything(param1ExpressionVisitor);
/*     */     }
/*     */ 
/*     */     
/*     */     void mapAndOptimize(SessionLocal param1SessionLocal, ColumnResolver param1ColumnResolver1, ColumnResolver param1ColumnResolver2) {
/* 312 */       this.expression.mapColumns(param1ColumnResolver1, 0, 0);
/* 313 */       if (param1ColumnResolver2 != null) {
/* 314 */         this.expression.mapColumns(param1ColumnResolver2, 0, 0);
/*     */       }
/* 316 */       this.expression = this.expression.optimize(param1SessionLocal);
/*     */     }
/*     */ 
/*     */     
/*     */     void getSQL(StringBuilder param1StringBuilder, int param1Int, Column param1Column) {
/* 321 */       this.expression.getUnenclosedSQL(param1Column.getSQL(param1StringBuilder, param1Int).append(" = "), param1Int);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class RowExpression
/*     */   {
/*     */     Expression expression;
/*     */     
/*     */     final int[] columns;
/*     */     
/*     */     Value[] values;
/*     */     
/*     */     RowExpression(Expression param1Expression, int[] param1ArrayOfint) {
/* 335 */       this.expression = param1Expression;
/* 336 */       this.columns = param1ArrayOfint;
/*     */     }
/*     */     
/*     */     boolean isEverything(ExpressionVisitor param1ExpressionVisitor) {
/* 340 */       return this.expression.isEverything(param1ExpressionVisitor);
/*     */     }
/*     */     
/*     */     void mapAndOptimize(SessionLocal param1SessionLocal, ColumnResolver param1ColumnResolver1, ColumnResolver param1ColumnResolver2) {
/* 344 */       this.expression.mapColumns(param1ColumnResolver1, 0, 0);
/* 345 */       if (param1ColumnResolver2 != null) {
/* 346 */         this.expression.mapColumns(param1ColumnResolver2, 0, 0);
/*     */       }
/* 348 */       this.expression = this.expression.optimize(param1SessionLocal);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class SetMultiple
/*     */     extends UpdateAction
/*     */   {
/*     */     final SetClauseList.RowExpression row;
/*     */     
/*     */     private final int position;
/*     */     boolean first;
/*     */     private boolean last;
/*     */     
/*     */     SetMultiple(SetClauseList.RowExpression param1RowExpression, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
/* 363 */       this.row = param1RowExpression;
/* 364 */       this.position = param1Int;
/* 365 */       this.first = param1Boolean1;
/* 366 */       this.last = param1Boolean2;
/*     */     }
/*     */ 
/*     */     
/*     */     Value update(SessionLocal param1SessionLocal) {
/*     */       Value[] arrayOfValue;
/* 372 */       if (this.first) {
/* 373 */         Value value = this.row.expression.getValue(param1SessionLocal);
/* 374 */         if (value == ValueNull.INSTANCE) {
/* 375 */           throw DbException.get(22018, "NULL to assigned row value");
/*     */         }
/* 377 */         this.row.values = arrayOfValue = value.convertToAnyRow().getList();
/* 378 */         if (arrayOfValue.length != this.row.columns.length) {
/* 379 */           throw DbException.get(21002);
/*     */         }
/*     */       } else {
/* 382 */         arrayOfValue = this.row.values;
/* 383 */         if (this.last) {
/* 384 */           this.row.values = null;
/*     */         }
/*     */       } 
/* 387 */       return arrayOfValue[this.position];
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isEverything(ExpressionVisitor param1ExpressionVisitor) {
/* 392 */       return (!this.first || this.row.isEverything(param1ExpressionVisitor));
/*     */     }
/*     */ 
/*     */     
/*     */     void mapAndOptimize(SessionLocal param1SessionLocal, ColumnResolver param1ColumnResolver1, ColumnResolver param1ColumnResolver2) {
/* 397 */       if (this.first)
/* 398 */         this.row.mapAndOptimize(param1SessionLocal, param1ColumnResolver1, param1ColumnResolver2); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\SetClauseList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */