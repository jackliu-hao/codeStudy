/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
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
/*     */ import org.h2.table.PlanItem;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.HasSQL;
/*     */ import org.h2.util.Utils;
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
/*     */ public final class MergeUsing
/*     */   extends DataChangeStatement
/*     */ {
/*     */   TableFilter targetTableFilter;
/*     */   TableFilter sourceTableFilter;
/*     */   Expression onCondition;
/*  59 */   private ArrayList<When> when = Utils.newSmallArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   private final HashSet<Long> targetRowidsRemembered = new HashSet<>();
/*     */   
/*     */   public MergeUsing(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/*  68 */     super(paramSessionLocal);
/*  69 */     this.targetTableFilter = paramTableFilter;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/*  74 */     long l1 = 0L;
/*  75 */     this.targetRowidsRemembered.clear();
/*  76 */     checkRights();
/*  77 */     setCurrentRowNumber(0L);
/*  78 */     this.sourceTableFilter.startQuery(this.session);
/*  79 */     this.sourceTableFilter.reset();
/*  80 */     Table table = this.targetTableFilter.getTable();
/*  81 */     table.fire(this.session, evaluateTriggerMasks(), true);
/*  82 */     table.lock(this.session, 1);
/*  83 */     setCurrentRowNumber(0L);
/*  84 */     long l2 = 0L;
/*  85 */     Row row1 = null, row2 = null;
/*  86 */     boolean bool = (table.getRowIdColumn() != null) ? true : false;
/*  87 */     while (this.sourceTableFilter.next()) {
/*  88 */       Row row = this.sourceTableFilter.get();
/*  89 */       if (row2 != null) {
/*  90 */         if (row != row2) {
/*  91 */           Row row3 = this.targetTableFilter.get();
/*  92 */           this.sourceTableFilter.set(row2);
/*  93 */           this.targetTableFilter.set(table.getNullRow());
/*  94 */           l1 += merge(true, paramResultTarget, paramResultOption);
/*  95 */           this.sourceTableFilter.set(row);
/*  96 */           this.targetTableFilter.set(row3);
/*  97 */           l2++;
/*     */         } 
/*  99 */         row2 = null;
/*     */       } 
/* 101 */       setCurrentRowNumber(l2 + 1L);
/* 102 */       boolean bool1 = this.targetTableFilter.isNullRow();
/* 103 */       if (!bool1) {
/* 104 */         Row row3 = this.targetTableFilter.get();
/* 105 */         if (table.isRowLockable()) {
/* 106 */           Row row4 = table.lockRow(this.session, row3);
/* 107 */           if (row4 == null) {
/* 108 */             if (row1 != row) {
/* 109 */               row2 = row;
/*     */             }
/*     */             continue;
/*     */           } 
/* 113 */           if (!row3.hasSharedData(row4)) {
/* 114 */             row3 = row4;
/* 115 */             this.targetTableFilter.set(row3);
/* 116 */             if (!this.onCondition.getBooleanValue(this.session)) {
/* 117 */               if (row1 != row) {
/* 118 */                 row2 = row;
/*     */               }
/*     */               continue;
/*     */             } 
/*     */           } 
/*     */         } 
/* 124 */         if (bool) {
/* 125 */           long l = row3.getKey();
/* 126 */           if (!this.targetRowidsRemembered.add(Long.valueOf(l))) {
/* 127 */             throw DbException.get(23505, "Merge using ON column expression, duplicate _ROWID_ target record already processed:_ROWID_=" + l + ":in:" + this.targetTableFilter
/*     */ 
/*     */ 
/*     */                 
/* 131 */                 .getTable());
/*     */           }
/*     */         } 
/*     */       } 
/* 135 */       l1 += merge(bool1, paramResultTarget, paramResultOption);
/* 136 */       l2++;
/* 137 */       row1 = row;
/*     */     } 
/* 139 */     if (row2 != null) {
/* 140 */       this.sourceTableFilter.set(row2);
/* 141 */       this.targetTableFilter.set(table.getNullRow());
/* 142 */       l1 += merge(true, paramResultTarget, paramResultOption);
/*     */     } 
/* 144 */     this.targetRowidsRemembered.clear();
/* 145 */     table.fire(this.session, evaluateTriggerMasks(), false);
/* 146 */     return l1;
/*     */   }
/*     */   
/*     */   private int merge(boolean paramBoolean, ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/* 150 */     for (When when : this.when) {
/* 151 */       if (((when.getClass() == WhenNotMatched.class)) == paramBoolean) {
/* 152 */         Expression expression = when.andCondition;
/* 153 */         if (expression == null || expression.getBooleanValue(this.session)) {
/* 154 */           when.merge(this.session, paramResultTarget, paramResultOption);
/* 155 */           return 1;
/*     */         } 
/*     */       } 
/*     */     } 
/* 159 */     return 0;
/*     */   }
/*     */   
/*     */   private int evaluateTriggerMasks() {
/* 163 */     int i = 0;
/* 164 */     for (When when : this.when) {
/* 165 */       i |= when.evaluateTriggerMasks();
/*     */     }
/* 167 */     return i;
/*     */   }
/*     */   
/*     */   private void checkRights() {
/* 171 */     for (When when : this.when) {
/* 172 */       when.checkRights();
/*     */     }
/* 174 */     this.session.getUser().checkTableRight(this.targetTableFilter.getTable(), 1);
/* 175 */     this.session.getUser().checkTableRight(this.sourceTableFilter.getTable(), 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 180 */     StringBuilder stringBuilder = new StringBuilder("MERGE INTO ");
/* 181 */     this.targetTableFilter.getPlanSQL(stringBuilder, false, paramInt);
/* 182 */     stringBuilder.append('\n').append("USING ");
/* 183 */     this.sourceTableFilter.getPlanSQL(stringBuilder, false, paramInt);
/* 184 */     for (When when : this.when) {
/* 185 */       when.getSQL(stringBuilder.append('\n'), paramInt);
/*     */     }
/* 187 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 192 */     this.onCondition.addFilterConditions(this.sourceTableFilter);
/* 193 */     this.onCondition.addFilterConditions(this.targetTableFilter);
/*     */     
/* 195 */     this.onCondition.mapColumns((ColumnResolver)this.sourceTableFilter, 0, 0);
/* 196 */     this.onCondition.mapColumns((ColumnResolver)this.targetTableFilter, 0, 0);
/*     */     
/* 198 */     this.onCondition = this.onCondition.optimize(this.session);
/*     */     
/* 200 */     this.onCondition.createIndexConditions(this.session, this.targetTableFilter);
/*     */     
/* 202 */     TableFilter[] arrayOfTableFilter = { this.sourceTableFilter, this.targetTableFilter };
/* 203 */     this.sourceTableFilter.addJoin(this.targetTableFilter, true, this.onCondition);
/* 204 */     PlanItem planItem = this.sourceTableFilter.getBestPlanItem(this.session, arrayOfTableFilter, 0, new AllColumnsForPlan(arrayOfTableFilter));
/* 205 */     this.sourceTableFilter.setPlanItem(planItem);
/* 206 */     this.sourceTableFilter.prepare();
/*     */     
/* 208 */     boolean bool1 = false, bool2 = false;
/* 209 */     for (Iterator<When> iterator = this.when.iterator(); iterator.hasNext(); ) {
/* 210 */       When when = iterator.next();
/* 211 */       if (!when.prepare(this.session)) {
/* 212 */         iterator.remove(); continue;
/* 213 */       }  if (when.getClass() == WhenNotMatched.class) {
/* 214 */         if (bool1) {
/* 215 */           iterator.remove(); continue;
/* 216 */         }  if (when.andCondition == null)
/* 217 */           bool1 = true; 
/*     */         continue;
/*     */       } 
/* 220 */       if (bool2) {
/* 221 */         iterator.remove(); continue;
/* 222 */       }  if (when.andCondition == null) {
/* 223 */         bool2 = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSourceTableFilter(TableFilter paramTableFilter) {
/* 230 */     this.sourceTableFilter = paramTableFilter;
/*     */   }
/*     */   
/*     */   public TableFilter getSourceTableFilter() {
/* 234 */     return this.sourceTableFilter;
/*     */   }
/*     */   
/*     */   public void setOnCondition(Expression paramExpression) {
/* 238 */     this.onCondition = paramExpression;
/*     */   }
/*     */   
/*     */   public Expression getOnCondition() {
/* 242 */     return this.onCondition;
/*     */   }
/*     */   
/*     */   public ArrayList<When> getWhen() {
/* 246 */     return this.when;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addWhen(When paramWhen) {
/* 255 */     this.when.add(paramWhen);
/*     */   }
/*     */ 
/*     */   
/*     */   public Table getTable() {
/* 260 */     return this.targetTableFilter.getTable();
/*     */   }
/*     */   
/*     */   public void setTargetTableFilter(TableFilter paramTableFilter) {
/* 264 */     this.targetTableFilter = paramTableFilter;
/*     */   }
/*     */   
/*     */   public TableFilter getTargetTableFilter() {
/* 268 */     return this.targetTableFilter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 275 */     return 62;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementName() {
/* 280 */     return "MERGE";
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 285 */     paramHashSet.add(this.targetTableFilter.getTable());
/* 286 */     paramHashSet.add(this.sourceTableFilter.getTable());
/* 287 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/* 288 */     for (When when : this.when) {
/* 289 */       when.collectDependencies(expressionVisitor);
/*     */     }
/* 291 */     this.onCondition.isEverything(expressionVisitor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract class When
/*     */     implements HasSQL
/*     */   {
/*     */     Expression andCondition;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setAndCondition(Expression param1Expression) {
/* 313 */       this.andCondition = param1Expression;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract void merge(SessionLocal param1SessionLocal, ResultTarget param1ResultTarget, DataChangeDeltaTable.ResultOption param1ResultOption);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean prepare(SessionLocal param1SessionLocal) {
/* 337 */       if (this.andCondition != null) {
/* 338 */         this.andCondition.mapColumns((ColumnResolver)MergeUsing.this.targetTableFilter, 0, 0);
/* 339 */         this.andCondition.mapColumns((ColumnResolver)MergeUsing.this.sourceTableFilter, 0, 0);
/* 340 */         this.andCondition = this.andCondition.optimize(param1SessionLocal);
/* 341 */         if (this.andCondition.isConstant()) {
/* 342 */           if (this.andCondition.getBooleanValue(param1SessionLocal)) {
/* 343 */             this.andCondition = null;
/*     */           } else {
/* 345 */             return false;
/*     */           } 
/*     */         }
/*     */       } 
/* 349 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract int evaluateTriggerMasks();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract void checkRights();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void collectDependencies(ExpressionVisitor param1ExpressionVisitor) {
/* 370 */       if (this.andCondition != null) {
/* 371 */         this.andCondition.isEverything(param1ExpressionVisitor);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public StringBuilder getSQL(StringBuilder param1StringBuilder, int param1Int) {
/* 377 */       param1StringBuilder.append("WHEN ");
/* 378 */       if (getClass() == MergeUsing.WhenNotMatched.class) {
/* 379 */         param1StringBuilder.append("NOT ");
/*     */       }
/* 381 */       param1StringBuilder.append("MATCHED");
/* 382 */       if (this.andCondition != null) {
/* 383 */         this.andCondition.getUnenclosedSQL(param1StringBuilder.append(" AND "), param1Int);
/*     */       }
/* 385 */       return param1StringBuilder.append(" THEN ");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final class WhenMatchedThenDelete
/*     */     extends When
/*     */   {
/*     */     void merge(SessionLocal param1SessionLocal, ResultTarget param1ResultTarget, DataChangeDeltaTable.ResultOption param1ResultOption) {
/* 394 */       TableFilter tableFilter = MergeUsing.this.targetTableFilter;
/* 395 */       Table table = tableFilter.getTable();
/* 396 */       Row row = tableFilter.get();
/* 397 */       if (param1ResultOption == DataChangeDeltaTable.ResultOption.OLD) {
/* 398 */         param1ResultTarget.addRow(row.getValueList());
/*     */       }
/* 400 */       if (!table.fireRow() || !table.fireBeforeRow(param1SessionLocal, row, null)) {
/* 401 */         table.removeRow(param1SessionLocal, row);
/* 402 */         table.fireAfterRow(param1SessionLocal, row, null, false);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     int evaluateTriggerMasks() {
/* 408 */       return 4;
/*     */     }
/*     */ 
/*     */     
/*     */     void checkRights() {
/* 413 */       MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 2);
/*     */     }
/*     */ 
/*     */     
/*     */     public StringBuilder getSQL(StringBuilder param1StringBuilder, int param1Int) {
/* 418 */       return super.getSQL(param1StringBuilder, param1Int).append("DELETE");
/*     */     }
/*     */   }
/*     */   
/*     */   public final class WhenMatchedThenUpdate
/*     */     extends When
/*     */   {
/*     */     private SetClauseList setClauseList;
/*     */     
/*     */     public void setSetClauseList(SetClauseList param1SetClauseList) {
/* 428 */       this.setClauseList = param1SetClauseList;
/*     */     }
/*     */ 
/*     */     
/*     */     void merge(SessionLocal param1SessionLocal, ResultTarget param1ResultTarget, DataChangeDeltaTable.ResultOption param1ResultOption) {
/* 433 */       TableFilter tableFilter = MergeUsing.this.targetTableFilter;
/* 434 */       Table table = tableFilter.getTable();
/* 435 */       try (LocalResult null = LocalResult.forTable(param1SessionLocal, table)) {
/* 436 */         this.setClauseList.prepareUpdate(table, param1SessionLocal, param1ResultTarget, param1ResultOption, localResult, tableFilter
/* 437 */             .get(), false);
/* 438 */         Update.doUpdate(MergeUsing.this, param1SessionLocal, table, localResult);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     boolean prepare(SessionLocal param1SessionLocal) {
/* 444 */       boolean bool = super.prepare(param1SessionLocal);
/* 445 */       this.setClauseList.mapAndOptimize(param1SessionLocal, (ColumnResolver)MergeUsing.this.targetTableFilter, (ColumnResolver)MergeUsing.this.sourceTableFilter);
/* 446 */       return bool;
/*     */     }
/*     */ 
/*     */     
/*     */     int evaluateTriggerMasks() {
/* 451 */       return 2;
/*     */     }
/*     */ 
/*     */     
/*     */     void checkRights() {
/* 456 */       MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 8);
/*     */     }
/*     */ 
/*     */     
/*     */     void collectDependencies(ExpressionVisitor param1ExpressionVisitor) {
/* 461 */       super.collectDependencies(param1ExpressionVisitor);
/* 462 */       this.setClauseList.isEverything(param1ExpressionVisitor);
/*     */     }
/*     */ 
/*     */     
/*     */     public StringBuilder getSQL(StringBuilder param1StringBuilder, int param1Int) {
/* 467 */       return this.setClauseList.getSQL(super.getSQL(param1StringBuilder, param1Int).append("UPDATE"), param1Int);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final class WhenNotMatched
/*     */     extends When
/*     */   {
/*     */     private Column[] columns;
/*     */     
/*     */     private final Boolean overridingSystem;
/*     */     private final Expression[] values;
/*     */     
/*     */     public WhenNotMatched(Column[] param1ArrayOfColumn, Boolean param1Boolean, Expression[] param1ArrayOfExpression) {
/* 481 */       this.columns = param1ArrayOfColumn;
/* 482 */       this.overridingSystem = param1Boolean;
/* 483 */       this.values = param1ArrayOfExpression;
/*     */     }
/*     */ 
/*     */     
/*     */     void merge(SessionLocal param1SessionLocal, ResultTarget param1ResultTarget, DataChangeDeltaTable.ResultOption param1ResultOption) {
/* 488 */       Table table = MergeUsing.this.targetTableFilter.getTable();
/* 489 */       Row row = table.getTemplateRow();
/* 490 */       Expression[] arrayOfExpression = this.values; byte b; int i;
/* 491 */       for (b = 0, i = this.columns.length; b < i; b++) {
/* 492 */         Column column = this.columns[b];
/* 493 */         int j = column.getColumnId();
/* 494 */         Expression expression = arrayOfExpression[b];
/* 495 */         if (expression != ValueExpression.DEFAULT) {
/*     */           try {
/* 497 */             row.setValue(j, expression.getValue(param1SessionLocal));
/* 498 */           } catch (DbException dbException) {
/* 499 */             dbException.addSQL("INSERT -- " + Prepared.getSimpleSQL(arrayOfExpression));
/* 500 */             throw dbException;
/*     */           } 
/*     */         }
/*     */       } 
/* 504 */       table.convertInsertRow(param1SessionLocal, row, this.overridingSystem);
/* 505 */       if (param1ResultOption == DataChangeDeltaTable.ResultOption.NEW) {
/* 506 */         param1ResultTarget.addRow((Value[])row.getValueList().clone());
/*     */       }
/* 508 */       if (!table.fireBeforeRow(param1SessionLocal, null, row)) {
/* 509 */         table.addRow(param1SessionLocal, row);
/* 510 */         DataChangeDeltaTable.collectInsertedFinalRow(param1SessionLocal, table, param1ResultTarget, param1ResultOption, row);
/*     */         
/* 512 */         table.fireAfterRow(param1SessionLocal, null, row, false);
/*     */       } else {
/* 514 */         DataChangeDeltaTable.collectInsertedFinalRow(param1SessionLocal, table, param1ResultTarget, param1ResultOption, row);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     boolean prepare(SessionLocal param1SessionLocal) {
/* 521 */       boolean bool = super.prepare(param1SessionLocal);
/* 522 */       TableFilter tableFilter1 = MergeUsing.this.targetTableFilter;
/* 523 */       TableFilter tableFilter2 = MergeUsing.this.sourceTableFilter;
/* 524 */       if (this.columns == null) {
/* 525 */         this.columns = tableFilter1.getTable().getColumns();
/*     */       }
/* 527 */       if (this.values.length != this.columns.length)
/* 528 */         throw DbException.get(21002);  byte b;
/*     */       int i;
/* 530 */       for (b = 0, i = this.values.length; b < i; b++) {
/* 531 */         Expression expression = this.values[b];
/* 532 */         expression.mapColumns((ColumnResolver)tableFilter1, 0, 0);
/* 533 */         expression.mapColumns((ColumnResolver)tableFilter2, 0, 0);
/* 534 */         expression = expression.optimize(param1SessionLocal);
/* 535 */         if (expression instanceof Parameter) {
/* 536 */           ((Parameter)expression).setColumn(this.columns[b]);
/*     */         }
/* 538 */         this.values[b] = expression;
/*     */       } 
/* 540 */       return bool;
/*     */     }
/*     */ 
/*     */     
/*     */     int evaluateTriggerMasks() {
/* 545 */       return 1;
/*     */     }
/*     */ 
/*     */     
/*     */     void checkRights() {
/* 550 */       MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 4);
/*     */     }
/*     */ 
/*     */     
/*     */     void collectDependencies(ExpressionVisitor param1ExpressionVisitor) {
/* 555 */       super.collectDependencies(param1ExpressionVisitor);
/* 556 */       for (Expression expression : this.values) {
/* 557 */         expression.isEverything(param1ExpressionVisitor);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public StringBuilder getSQL(StringBuilder param1StringBuilder, int param1Int) {
/* 563 */       super.getSQL(param1StringBuilder, param1Int).append("INSERT (");
/* 564 */       Column.writeColumns(param1StringBuilder, this.columns, param1Int).append(")\nVALUES (");
/* 565 */       return Expression.writeExpressions(param1StringBuilder, this.values, param1Int).append(')');
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\MergeUsing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */