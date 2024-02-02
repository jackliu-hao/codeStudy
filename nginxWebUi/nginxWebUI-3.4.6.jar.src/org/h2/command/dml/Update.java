/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.Prepared;
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
/*     */ 
/*     */ 
/*     */ public final class Update
/*     */   extends FilteredDataChangeStatement
/*     */ {
/*     */   private SetClauseList setClauseList;
/*     */   private Insert onDuplicateKeyInsert;
/*     */   private TableFilter fromTableFilter;
/*     */   
/*     */   public Update(SessionLocal paramSessionLocal) {
/*  43 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setSetClauseList(SetClauseList paramSetClauseList) {
/*  47 */     this.setClauseList = paramSetClauseList;
/*     */   }
/*     */   
/*     */   public void setFromTableFilter(TableFilter paramTableFilter) {
/*  51 */     this.fromTableFilter = paramTableFilter;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption) {
/*  56 */     this.targetTableFilter.startQuery(this.session);
/*  57 */     this.targetTableFilter.reset();
/*  58 */     Table table = this.targetTableFilter.getTable();
/*  59 */     try (LocalResult null = LocalResult.forTable(this.session, table)) {
/*  60 */       this.session.getUser().checkTableRight(table, 8);
/*  61 */       table.fire(this.session, 2, true);
/*  62 */       table.lock(this.session, 1);
/*     */       
/*  64 */       setCurrentRowNumber(0L);
/*  65 */       long l1 = 0L;
/*  66 */       long l2 = -1L;
/*  67 */       if (this.fetchExpr != null) {
/*  68 */         Value value = this.fetchExpr.getValue(this.session);
/*  69 */         if (value == ValueNull.INSTANCE || (l2 = value.getLong()) < 0L) {
/*  70 */           throw DbException.getInvalidValueException("FETCH", value);
/*     */         }
/*     */       } 
/*  73 */       while (nextRow(l2, l1)) {
/*  74 */         Row row = this.targetTableFilter.get();
/*  75 */         if (table.isRowLockable()) {
/*  76 */           Row row1 = table.lockRow(this.session, row);
/*  77 */           if (row1 == null) {
/*     */             continue;
/*     */           }
/*  80 */           if (!row.hasSharedData(row1)) {
/*  81 */             row = row1;
/*  82 */             this.targetTableFilter.set(row);
/*  83 */             if (this.condition != null && !this.condition.getBooleanValue(this.session)) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */         } 
/*  88 */         if (this.setClauseList.prepareUpdate(table, this.session, paramResultTarget, paramResultOption, localResult, row, (this.onDuplicateKeyInsert != null)))
/*     */         {
/*  90 */           l1++;
/*     */         }
/*     */       } 
/*  93 */       doUpdate(this, this.session, table, localResult);
/*  94 */       table.fire(this.session, 2, false);
/*  95 */       return l1;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void doUpdate(Prepared paramPrepared, SessionLocal paramSessionLocal, Table paramTable, LocalResult paramLocalResult) {
/* 100 */     paramLocalResult.done();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     paramTable.updateRows(paramPrepared, paramSessionLocal, paramLocalResult);
/* 110 */     if (paramTable.fireRow()) {
/* 111 */       paramLocalResult.reset(); while (paramLocalResult.next()) {
/* 112 */         Row row1 = paramLocalResult.currentRowForTable();
/* 113 */         paramLocalResult.next();
/* 114 */         Row row2 = paramLocalResult.currentRowForTable();
/* 115 */         paramTable.fireAfterRow(paramSessionLocal, row1, row2, false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 122 */     StringBuilder stringBuilder = new StringBuilder("UPDATE ");
/* 123 */     this.targetTableFilter.getPlanSQL(stringBuilder, false, paramInt);
/* 124 */     if (this.fromTableFilter != null) {
/* 125 */       stringBuilder.append("\nFROM ");
/* 126 */       this.fromTableFilter.getPlanSQL(stringBuilder, false, paramInt);
/*     */     } 
/* 128 */     this.setClauseList.getSQL(stringBuilder, paramInt);
/* 129 */     appendFilterCondition(stringBuilder, paramInt);
/* 130 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 135 */     if (this.fromTableFilter != null) {
/* 136 */       this.targetTableFilter.addJoin(this.fromTableFilter, false, null);
/*     */     }
/* 138 */     if (this.condition != null) {
/* 139 */       this.condition.mapColumns((ColumnResolver)this.targetTableFilter, 0, 0);
/* 140 */       if (this.fromTableFilter != null) {
/* 141 */         this.condition.mapColumns((ColumnResolver)this.fromTableFilter, 0, 0);
/*     */       }
/* 143 */       this.condition = this.condition.optimizeCondition(this.session);
/* 144 */       if (this.condition != null) {
/* 145 */         this.condition.createIndexConditions(this.session, this.targetTableFilter);
/*     */       }
/*     */     } 
/* 148 */     this.setClauseList.mapAndOptimize(this.session, (ColumnResolver)this.targetTableFilter, (ColumnResolver)this.fromTableFilter);
/* 149 */     TableFilter[] arrayOfTableFilter = null;
/* 150 */     if (this.fromTableFilter == null) {
/* 151 */       arrayOfTableFilter = new TableFilter[] { this.targetTableFilter };
/*     */     } else {
/* 153 */       arrayOfTableFilter = new TableFilter[] { this.targetTableFilter, this.fromTableFilter };
/*     */     } 
/* 155 */     PlanItem planItem = this.targetTableFilter.getBestPlanItem(this.session, arrayOfTableFilter, 0, new AllColumnsForPlan(arrayOfTableFilter));
/* 156 */     this.targetTableFilter.setPlanItem(planItem);
/* 157 */     this.targetTableFilter.prepare();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 162 */     return 68;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatementName() {
/* 167 */     return "UPDATE";
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 172 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/* 173 */     if (this.condition != null) {
/* 174 */       this.condition.isEverything(expressionVisitor);
/*     */     }
/* 176 */     this.setClauseList.isEverything(expressionVisitor);
/*     */   }
/*     */   
/*     */   public Insert getOnDuplicateKeyInsert() {
/* 180 */     return this.onDuplicateKeyInsert;
/*     */   }
/*     */   
/*     */   void setOnDuplicateKeyInsert(Insert paramInsert) {
/* 184 */     this.onDuplicateKeyInsert = paramInsert;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Update.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */