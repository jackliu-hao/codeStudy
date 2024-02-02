/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.DataChangeDeltaTable;
/*     */ import org.h2.table.PlanItem;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
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
/*     */ public final class Delete
/*     */   extends FilteredDataChangeStatement
/*     */ {
/*     */   public Delete(SessionLocal paramSessionLocal) {
/*  36 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/*  42 */     this.targetTableFilter.startQuery(this.session);
/*  43 */     this.targetTableFilter.reset();
/*  44 */     Table table = this.targetTableFilter.getTable();
/*  45 */     this.session.getUser().checkTableRight(table, 2);
/*  46 */     table.fire(this.session, 4, true);
/*  47 */     table.lock(this.session, 1);
/*  48 */     long l = -1L;
/*  49 */     if (this.fetchExpr != null) {
/*  50 */       Value value = this.fetchExpr.getValue(this.session);
/*  51 */       if (value == ValueNull.INSTANCE || (l = value.getLong()) < 0L) {
/*  52 */         throw DbException.getInvalidValueException("FETCH", value);
/*     */       }
/*     */     } 
/*  55 */     try (LocalResult null = LocalResult.forTable(this.session, table)) {
/*  56 */       setCurrentRowNumber(0L);
/*  57 */       long l1 = 0L;
/*  58 */       while (nextRow(l, l1)) {
/*  59 */         Row row = this.targetTableFilter.get();
/*  60 */         if (table.isRowLockable()) {
/*  61 */           Row row1 = table.lockRow(this.session, row);
/*  62 */           if (row1 == null) {
/*     */             continue;
/*     */           }
/*  65 */           if (!row.hasSharedData(row1)) {
/*  66 */             row = row1;
/*  67 */             this.targetTableFilter.set(row);
/*  68 */             if (this.condition != null && !this.condition.getBooleanValue(this.session)) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */         } 
/*  73 */         if (paramResultOption == DataChangeDeltaTable.ResultOption.OLD) {
/*  74 */           paramResultTarget.addRow(row.getValueList());
/*     */         }
/*  76 */         if (!table.fireRow() || !table.fireBeforeRow(this.session, row, null)) {
/*  77 */           localResult.addRowForTable(row);
/*     */         }
/*  79 */         l1++;
/*     */       } 
/*  81 */       localResult.done();
/*  82 */       long l2 = 0L;
/*  83 */       while (localResult.next()) {
/*  84 */         if ((++l2 & 0x7FL) == 0L) {
/*  85 */           checkCanceled();
/*     */         }
/*  87 */         Row row = localResult.currentRowForTable();
/*  88 */         table.removeRow(this.session, row);
/*     */       } 
/*  90 */       if (table.fireRow()) {
/*  91 */         localResult.reset(); while (localResult.next()) {
/*  92 */           table.fireAfterRow(this.session, localResult.currentRowForTable(), null, false);
/*     */         }
/*     */       } 
/*  95 */       table.fire(this.session, 4, false);
/*  96 */       return l1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 102 */     StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
/* 103 */     this.targetTableFilter.getPlanSQL(stringBuilder, false, paramInt);
/* 104 */     appendFilterCondition(stringBuilder, paramInt);
/* 105 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 110 */     if (this.condition != null) {
/* 111 */       this.condition.mapColumns((ColumnResolver)this.targetTableFilter, 0, 0);
/* 112 */       this.condition = this.condition.optimizeCondition(this.session);
/* 113 */       if (this.condition != null) {
/* 114 */         this.condition.createIndexConditions(this.session, this.targetTableFilter);
/*     */       }
/*     */     } 
/* 117 */     TableFilter[] arrayOfTableFilter = { this.targetTableFilter };
/* 118 */     PlanItem planItem = this.targetTableFilter.getBestPlanItem(this.session, arrayOfTableFilter, 0, new AllColumnsForPlan(arrayOfTableFilter));
/* 119 */     this.targetTableFilter.setPlanItem(planItem);
/* 120 */     this.targetTableFilter.prepare();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 125 */     return 58;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementName() {
/* 130 */     return "DELETE";
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 135 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/* 136 */     if (this.condition != null)
/* 137 */       this.condition.isEverything(expressionVisitor); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Delete.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */